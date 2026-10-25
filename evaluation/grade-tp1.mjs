#!/usr/bin/env node
// Corrector automático del TP 1 (Classroom 50). Ver evaluation/README.md.
import { execFile } from 'node:child_process';
import { promisify, parseArgs } from 'node:util';
import { existsSync, mkdirSync, copyFileSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const run = promisify(execFile);
const here = dirname(fileURLToPath(import.meta.url));

const { values: opts } = parseArgs({
  options: {
    org: { type: 'string', default: 'austral-prog' },
    classroom: { type: 'string', default: 'dcp' },
    assignment: { type: 'string', default: 'tp-1-refactor' },
    deadline: { type: 'string' },
    'gh-user': { type: 'string' },
    only: { type: 'string' },
    concurrency: { type: 'string', default: '2' },
    timeout: { type: 'string', default: '300' },
    out: { type: 'string', default: join(here, 'output') },
    fresh: { type: 'boolean', default: false },
    help: { type: 'boolean', short: 'h', default: false },
  },
});

if (opts.help || !opts.deadline) {
  console.log(`Uso: node evaluation/grade-tp1.mjs --deadline <fecha> [opciones]

  --deadline <fecha>     Obligatorio. ISO 8601; sin zona se asume -03:00 (Argentina).
                         Ej: "2026-10-05T23:59"
  --gh-user <usuario>    Cuenta de gh con acceso a la organización (ej. el docente)
  --org / --classroom / --assignment   Por defecto: austral-prog / dcp / tp-1-refactor
  --only a,b             Evaluar solo estos usuarios de GitHub
  --concurrency <n>      Alumnos en paralelo (default 2)
  --timeout <seg>        Límite por ejecución de Gradle (default 300)
  --out <dir>            Carpeta de salida (default evaluation/output)
  --fresh                Ignorar el caché de resultados por commit`);
  process.exit(opts.help ? 0 : 1);
}

function fail(msg) {
  console.error(`Error: ${msg}`);
  process.exit(1);
}

const TZ_OFFSET_MS = -3 * 3600 * 1000;
const hasZone = /(Z|[+-]\d{2}:?\d{2})$/i;
const deadline = new Date(hasZone.test(opts.deadline) ? opts.deadline : `${opts.deadline}-03:00`);
if (Number.isNaN(deadline.getTime())) fail(`--deadline inválido: ${opts.deadline}`);

const work = join(opts.out, '.work');
const reposDir = join(work, 'repos');
const cachePath = join(work, 'cache.json');
mkdirSync(reposDir, { recursive: true });

const env = { ...process.env, GIT_TERMINAL_PROMPT: '0' };

const sh = (cmd, args, o = {}) => run(cmd, args, { env, maxBuffer: 64 * 1024 * 1024, ...o });
const gh = (args) => sh('gh', args);
// Clona por HTTPS con el token de gh, ignorando config global (p. ej. reglas https -> ssh).
const gitConfig = ['-c', 'credential.helper=!gh auth git-credential'];
const git = (cwd, args, o = {}) => sh('git', [...gitConfig, ...args], {
  cwd,
  ...o,
  env: { ...env, GIT_CONFIG_GLOBAL: '/dev/null', GIT_CONFIG_SYSTEM: '/dev/null' },
});

if (opts['gh-user']) {
  env.GH_TOKEN = (await sh('gh', ['auth', 'token', '--hostname', 'github.com', '--user', opts['gh-user']])).stdout.trim();
}

const localIso = (date) => new Date(date.getTime() + TZ_OFFSET_MS).toISOString().replace(/\.\d+Z$/, '-03:00');
const firstLine = (s) => String(s).split('\n').find((l) => l.trim() && !l.includes('JDK_JAVA_OPTIONS')) ?? '';

async function cloneOrUpdate(repo, branch) {
  const dir = join(reposDir, repo.replace(/\//g, '__'));
  if (existsSync(join(dir, '.git'))) {
    await git(dir, ['fetch', '--quiet', '--prune', 'origin']);
  } else {
    await git(reposDir, ['clone', '--quiet', `https://github.com/${repo}.git`, dir]);
  }
  await git(dir, ['checkout', '--quiet', '--force', '--detach', `origin/${branch}`]);
  return dir;
}

// Una ejecución de la consigna: dos diff contra las salidas de referencia de la cátedra.
async function check(dir, sha, ref) {
  await git(dir, ['checkout', '--quiet', '--force', '--detach', sha]);
  await git(dir, ['clean', '-fdxq']);
  for (const f of ['salida-esperada.txt', 'reporte-esperado.txt', 'datos/lecturas.csv']) {
    mkdirSync(dirname(join(dir, f)), { recursive: true });
    copyFileSync(join(ref, f), join(dir, f));
  }
  let out;
  try {
    ({ stdout: out } = await sh('sh', ['gradlew', 'run', '-q', '--console=plain'], {
      cwd: dir,
      timeout: Number(opts.timeout) * 1000,
      killSignal: 'SIGKILL',
    }));
  } catch (e) {
    const why = e.killed ? 'timeout' : firstLine(e.stderr || e.message);
    return { ok: false, reason: `no compila/ejecuta: ${why}`.slice(0, 160) };
  }
  const norm = (s) => s.replace(/\r\n/g, '\n');
  const reportePath = join(dir, 'reporte.txt');
  const consola = norm(out) === norm(readFileSync(join(ref, 'salida-esperada.txt'), 'utf8'));
  const archivo = existsSync(reportePath)
    && norm(readFileSync(reportePath, 'utf8')) === norm(readFileSync(join(ref, 'reporte-esperado.txt'), 'utf8'));
  if (consola && archivo) return { ok: true };
  const bad = [!consola && 'consola', !archivo && 'reporte.txt'].filter(Boolean).join(' y ');
  return { ok: false, reason: `diff distinto: ${bad}` };
}

const cache = !opts.fresh && existsSync(cachePath) ? JSON.parse(readFileSync(cachePath, 'utf8')) : {};

async function cachedCheck(dir, sha, ctx) {
  const key = `${ctx.templateSha}:${sha}`;
  if (!cache[key]) {
    cache[key] = await check(dir, sha, ctx.ref);
    writeFileSync(cachePath, JSON.stringify(cache));
  }
  return cache[key];
}

async function grade(student, ctx) {
  const repo = `${ctx.org}/${ctx.classroom}-${ctx.assignment}-${student.username}`;
  const row = { nombre: student.nombre, repo: `https://github.com/${repo}`, timestamp: '', a_tiempo: 'no', entregado: 'no', usuario: student.username, detalle: '' };

  let branch;
  try {
    branch = (await gh(['api', `repos/${repo}`, '--jq', '.default_branch'])).stdout.trim();
  } catch (e) {
    row.detalle = /404|Not Found/.test(`${e.stderr}${e.message}`) ? 'no aceptó el assignment (repo inexistente)' : `error de API: ${firstLine(e.stderr || e.message)}`;
    return row;
  }

  let dir;
  try {
    dir = await cloneOrUpdate(repo, branch);
  } catch (e) {
    row.detalle = `no se pudo clonar: ${firstLine(e.stderr || e.message)}`;
    return row;
  }

  const { stdout: log } = await git(dir, ['log', '--first-parent', '--reverse', '--format=%H\t%cI', `origin/${branch}`]);
  const commits = log.trim().split('\n').filter(Boolean).map((l) => {
    const [sha, date] = l.split('\t');
    return { sha, date: new Date(date) };
  });
  for (const c of commits) {
    const tree = await git(dir, ['rev-parse', `${c.sha}:${ctx.codePath}`]).then((r) => r.stdout.trim(), () => null);
    c.modified = tree !== ctx.templateTree;
  }

  const head = commits.at(-1);
  if (!head.modified) {
    const { stdout: files } = await git(dir, ['ls-tree', '-r', '--name-only', head.sha]);
    const stray = files.split('\n').filter((f) => f.endsWith('.kt') && !f.startsWith(`${ctx.codePath}/`));
    row.detalle = stray.length
      ? `src/main/kotlin sin cambios; ${stray.length} archivo(s) .kt fuera de la ruta del build (ej. ${stray[0]})`
      : 'no modificó el código (src/main/kotlin igual a la plantilla)';
    return row;
  }
  const headResult = await cachedCheck(dir, head.sha, ctx);
  if (!headResult.ok) {
    row.detalle = `HEAD no pasa: ${headResult.reason}`;
    return row;
  }

  let first = head;
  for (const c of commits.filter((x) => x.modified)) {
    if (c === head || (await cachedCheck(dir, c.sha, ctx)).ok) {
      first = c;
      break;
    }
  }
  row.entregado = 'si';
  row.timestamp = localIso(first.date);
  row.a_tiempo = first.date <= deadline ? 'si' : 'no';
  row.detalle = `primer commit andando ${first.sha.slice(0, 7)} (${commits.indexOf(first) + 1} de ${commits.length})`;
  return row;
}

async function pool(items, n, fn) {
  const results = new Array(items.length);
  let next = 0;
  await Promise.all(Array.from({ length: n }, async () => {
    while (next < items.length) {
      const i = next++;
      results[i] = await fn(items[i]);
    }
  }));
  return results;
}

const csvCell = (v) => (/[",\n]/.test(String(v ?? '')) ? `"${String(v).replace(/"/g, '""')}"` : String(v ?? ''));

// --- main ---
const { org, classroom, assignment } = opts;

let manifest;
try {
  manifest = JSON.parse((await gh(['teacher', 'assignment', 'list', org, classroom, '--json', '--quiet'])).stdout);
} catch (e) {
  fail(`no pude leer los assignments (¿gh teacher instalado y cuenta correcta? usá --gh-user): ${firstLine(e.stderr || e.message)}`);
}
const entry = manifest.find((a) => a.slug === assignment);
if (!entry) fail(`assignment "${assignment}" no existe. Disponibles: ${manifest.map((a) => a.slug).join(', ')}`);

const roster = JSON.parse((await gh(['teacher', 'roster', 'list', org, classroom, '--json'])).stdout);
const only = opts.only ? new Set(opts.only.split(',').map((s) => s.trim().toLowerCase())) : null;
const students = await Promise.all(roster
  .filter((r) => (r.role === 'student' || !r.role) && (!only || only.has(r.username.toLowerCase())))
  .map(async (r) => {
    let nombre = [r.first_name, r.last_name].filter(Boolean).join(' ').trim();
    if (!nombre) {
      nombre = await gh(['api', `users/${r.username}`, '--jq', '.name // ""']).then((x) => x.stdout.trim(), () => '');
    }
    return { username: r.username, nombre: nombre || r.username };
  }));
if (!students.length) fail('no hay alumnos para evaluar');

const tpl = entry.template;
const templateRepo = `${tpl.owner}/${tpl.repo}`;
const templateDir = await cloneOrUpdate(templateRepo, tpl.branch);
const codePath = 'src/main/kotlin';
const templateTree = (await git(templateDir, ['rev-parse', `HEAD:${codePath}`])).stdout.trim();
const templateSha = (await git(templateDir, ['rev-parse', 'HEAD'])).stdout.trim();
const ctx = { org, classroom, assignment, codePath, templateTree, templateSha, ref: resolve(templateDir) };

console.error(`Assignment ${assignment}: ${students.length} alumnos · plantilla ${templateRepo}@${templateSha.slice(0, 7)} · límite ${localIso(deadline)}`);

let done = 0;
const rows = await pool(students, Number(opts.concurrency), async (s) => {
  let row;
  try {
    row = await grade(s, ctx);
  } catch (e) {
    row = { nombre: s.nombre, repo: `https://github.com/${org}/${classroom}-${assignment}-${s.username}`, timestamp: '', a_tiempo: 'no', entregado: 'no', usuario: s.username, detalle: `error inesperado: ${firstLine(e.stderr || e.message)}` };
  }
  console.error(`[${++done}/${students.length}] ${s.username}: entregado=${row.entregado} a_tiempo=${row.a_tiempo} (${row.detalle})`);
  return row;
});

rows.sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'));
const cols = ['nombre', 'repo', 'timestamp', 'a_tiempo', 'entregado', 'usuario', 'detalle'];
const header = ['nombre', 'repo', 'timestamp', 'entrego_a_tiempo', 'entregado', 'usuario', 'detalle'];
const csv = [header, ...rows.map((r) => cols.map((c) => r[c]))].map((r) => r.map(csvCell).join(',')).join('\n');
const outFile = join(opts.out, `reporte-${assignment}.csv`);
writeFileSync(outFile, `\uFEFF${csv}\n`);

console.table(rows.map((r) => ({ nombre: r.nombre, timestamp: r.timestamp, 'a tiempo': r.a_tiempo, entregado: r.entregado })));
const delivered = rows.filter((r) => r.entregado === 'si').length;
const onTime = rows.filter((r) => r.a_tiempo === 'si').length;
console.error(`Entregaron: ${delivered}/${rows.length} · a tiempo: ${onTime}/${rows.length}\nReporte: ${outFile}`);
