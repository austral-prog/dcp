#!/usr/bin/env node
// Corrector automático del TP 2 – TDD (Classroom 50). Ver evaluation/README.md.
import { execFile } from 'node:child_process';
import { promisify, parseArgs } from 'node:util';
import { existsSync, mkdirSync, copyFileSync, readFileSync, writeFileSync, readdirSync, rmSync } from 'node:fs';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const run = promisify(execFile);
const here = dirname(fileURLToPath(import.meta.url));
const VERSION = 'tp2-v3'; // cambiar si cambian los criterios, para invalidar el caché

const { values: opts } = parseArgs({
  options: {
    org: { type: 'string', default: 'austral-prog' },
    classroom: { type: 'string', default: 'dcp' },
    assignment: { type: 'string', default: 'tp-2-tdd' },
    deadline: { type: 'string', default: '2026-10-15T09:00' },
    'gh-user': { type: 'string' },
    only: { type: 'string' },
    concurrency: { type: 'string', default: '2' },
    timeout: { type: 'string', default: '300' },
    out: { type: 'string', default: join(here, 'output') },
    fresh: { type: 'boolean', default: false },
    help: { type: 'boolean', short: 'h', default: false },
  },
});

if (opts.help) {
  console.log(`Uso: node evaluation/grade-tp2.mjs [opciones]

  --deadline <fecha>     ISO 8601; sin zona se asume -03:00. Default "2026-10-15T09:00"
  --gh-user <usuario>    Cuenta de gh con acceso a la organización (ej. el docente)
  --org / --classroom / --assignment   Por defecto: austral-prog / dcp / tp-2-tdd
  --only a,b             Evaluar solo estos usuarios de GitHub
  --concurrency <n>      Alumnos en paralelo (default 2)
  --timeout <seg>        Límite por ejecución de Gradle (default 300)
  --out <dir>            Carpeta de salida (default evaluation/output)
  --fresh                Ignorar el caché de resultados por commit`);
  process.exit(0);
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
const cachePath = join(work, `cache-${opts.assignment}.json`);
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
const norm = (s) => s.replace(/\r\n/g, '\n');

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

// Archivos de la cátedra que el alumno no puede tocar, más el Gradle Wrapper de la plantilla.
const CONTRACT = ['src/test/kotlin/com/university/dcp/ContratoSalidaTest.kt', 'salida-esperada.txt', 'reporte-esperado.txt'];
const WRAPPER = ['gradlew', 'gradlew.bat', 'gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.properties'];
const DATA = 'datos/lecturas.csv';

// Escenarios de la Parte 2 (caja negra): reemplazan datos/lecturas.csv y comparan las dos salidas.
const escDir = join(here, 'tp2', 'escenarios');
const SCENARIOS = [
  { id: 'kpa', label: 'kPa', points: 1.5 },
  { id: 'unidad-invalida', label: 'unidad inválida', points: 2 },
  { id: 'mbar', label: 'mbar', points: 0.5 },
];

const gradle = (dir, args) => sh('sh', ['gradlew', ...args, '--console=plain'], {
  cwd: dir,
  timeout: Number(opts.timeout) * 1000,
  killSignal: 'SIGKILL',
});

function testResults(dir) {
  const xmlDir = join(dir, 'build', 'test-results', 'test');
  const r = { total: 0, failed: 0, propios: 0 };
  if (!existsSync(xmlDir)) return r;
  for (const f of readdirSync(xmlDir).filter((x) => x.endsWith('.xml'))) {
    const xml = readFileSync(join(xmlDir, f), 'utf8');
    for (const m of xml.matchAll(/<testcase\b[^>]*classname="([^"]*)"[^>]*?(\/>|>([\s\S]*?)<\/testcase>)/g)) {
      r.total++;
      if (/<(failure|error)\b/.test(m[3] ?? '')) r.failed++;
      if (!m[1].endsWith('ContratoSalidaTest')) r.propios++;
    }
  }
  return r;
}

const strip = (s) => s.normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[_*`]/g, '').trim().toLowerCase();

function parseCasos(text) {
  const c = { feliz: 0, borde: 0, invalido: 0 };
  for (const line of text.split('\n')) {
    if (!line.trim().startsWith('|')) continue;
    const cells = line.split('|').slice(1, -1).map((x) => x.trim());
    if (cells.length < 2) continue;
    const tipo = strip(cells[0]);
    const caso = strip(cells[1]);
    if (!caso || /^-+$/.test(caso) || caso.startsWith('ej.')) continue;
    if (tipo.startsWith('feliz')) c.feliz++;
    else if (tipo.startsWith('borde')) c.borde++;
    else if (tipo.startsWith('invalid')) c.invalido++;
  }
  return c;
}

async function blob(dir, sha, path) {
  return git(dir, ['rev-parse', `${sha}:${path}`]).then((r) => r.stdout.trim(), () => null);
}

function normalizeTestNewlines(dir) {
  const testDir = join(dir, 'src', 'test', 'kotlin');
  if (!existsSync(testDir)) return;
  const walk = (d) => {
    for (const entry of readdirSync(d, { withFileTypes: true })) {
      const p = join(d, entry.name);
      if (entry.isDirectory()) walk(p);
      else if (entry.name.endsWith('.kt') && entry.name !== 'ContratoSalidaTest.kt') {
        const src = readFileSync(p, 'utf8');
        const fixed = src.replace(/\\r\\n/g, '\\n');
        if (fixed !== src) writeFileSync(p, fixed);
      }
    }
  };
  walk(testDir);
}

function countAgentePendientes(text) {
  const lines = text.split('\n');
  let pending = 0;
  for (let i = 0; i < lines.length; i++) {
    if (/^\s*[-*]\s*_?\(completar\)/.test(lines[i])) {
      // Si el alumno dejó la línea guía pero agregó su respuesta debajo antes del próximo (completar) o título, cuenta como completado.
      let answeredBelow = false;
      for (let j = i + 1; j < lines.length; j++) {
        const l = lines[j].trim();
        if (!l) continue;
        if (l.startsWith('#') || /^[-*]\s*_?\(completar\)/.test(l)) break;
        answeredBelow = true;
        break;
      }
      if (!answeredBelow) pending++;
    }
  }
  return pending;
}

// Evalúa un commit. Usa los archivos de contrato, wrapper y datos de la plantilla, no los del alumno.
async function evaluate(dir, sha, ctx) {
  await git(dir, ['checkout', '--quiet', '--force', '--detach', sha]);
  await git(dir, ['clean', '-fdxq']);
  const r = { sha };

  const changed = [];
  for (const f of CONTRACT) if ((await blob(dir, sha, f)) !== ctx.templateBlobs[f]) changed.push(f.split('/').pop());
  r.contrato = changed;
  r.codigoModificado = (await blob(dir, sha, 'src/main/kotlin')) !== ctx.templateBlobs['src/main/kotlin'];

  for (const f of [...CONTRACT, ...WRAPPER, DATA]) {
    mkdirSync(dirname(join(dir, f)), { recursive: true });
    copyFileSync(join(ctx.ref, f), join(dir, f));
  }
  normalizeTestNewlines(dir);

  try {
    await gradle(dir, ['test']);
    r.testsOk = true;
  } catch (e) {
    r.testsOk = false;
    r.testsError = e.killed ? 'timeout' : null;
  }
  Object.assign(r, testResults(dir));
  if (!r.testsOk && r.total === 0 && !r.testsError) r.testsError = 'no compila';

  r.escenarios = {};
  for (const s of SCENARIOS) {
    copyFileSync(join(escDir, `${s.id}.csv`), join(dir, DATA));
    rmSync(join(dir, 'reporte.txt'), { force: true });
    try {
      const { stdout } = await gradle(dir, ['run', '-q']);
      const consola = norm(stdout) === norm(readFileSync(join(escDir, `${s.id}.salida.txt`), 'utf8'));
      const reporte = existsSync(join(dir, 'reporte.txt'))
        && norm(readFileSync(join(dir, 'reporte.txt'), 'utf8')) === norm(readFileSync(join(escDir, `${s.id}.reporte.txt`), 'utf8'));
      r.escenarios[s.id] = consola && reporte;
    } catch {
      r.escenarios[s.id] = false;
    }
  }

  const casosPath = join(dir, 'CASOS.md');
  r.casos = existsSync(casosPath) ? parseCasos(readFileSync(casosPath, 'utf8')) : { feliz: 0, borde: 0, invalido: 0 };

  const agentePath = join(dir, 'agente-tests.md');
  r.agenteModificado = (await blob(dir, sha, 'agente-tests.md')) !== ctx.templateBlobs['agente-tests.md'];
  r.agentePendientes = existsSync(agentePath) ? countAgentePendientes(readFileSync(agentePath, 'utf8')) : -1;

  await git(dir, ['checkout', '--quiet', '--force', '--detach', sha]);
  return r;
}

// Nota sugerida sobre 10.
function score(r) {
  const contratoOk = r.contrato.length === 0;
  const p = {};
  p.tests = r.codigoModificado && r.testsOk && contratoOk ? (r.propios > 0 ? 4 : 1) : 0;
  p.escenarios = !r.codigoModificado ? 0 : SCENARIOS.reduce((acc, s) => acc + (r.escenarios[s.id] ? s.points : 0), 0);
  p.casos = (Math.min(r.casos.feliz, 3) + Math.min(r.casos.borde, 3) + Math.min(r.casos.invalido, 3)) / 9;
  p.agente = !r.agenteModificado || r.agentePendientes < 0 ? 0 : r.agentePendientes === 0 ? 1 : 0.5;
  const total = p.tests + p.escenarios + p.casos + p.agente;
  return Math.round(total * 2) / 2;
}

const passes = (r) => r.codigoModificado && r.testsOk && r.contrato.length === 0;

function describe(r) {
  const parts = [];
  if (!r.codigoModificado) parts.push('src/main/kotlin igual a la plantilla');
  if (r.testsOk) parts.push(`tests OK (${r.propios} propios)`);
  else if (r.testsError) parts.push(`tests: ${r.testsError}`);
  else parts.push(`tests FALLAN (${r.failed}/${r.total})`);
  if (r.contrato.length) parts.push(`MODIFICÓ ${r.contrato.join(', ')}`);
  parts.push(SCENARIOS.map((s) => `${s.label} ${r.escenarios[s.id] ? 'OK' : 'FALLA'}`).join(', '));
  parts.push(`casos ${r.casos.feliz}/${r.casos.borde}/${r.casos.invalido}`);
  parts.push(!r.agenteModificado ? 'agente sin cambios' : r.agentePendientes > 0 ? `agente con ${r.agentePendientes} (completar)` : 'agente completo');
  return parts.join(' · ');
}

const cache = !opts.fresh && existsSync(cachePath) ? JSON.parse(readFileSync(cachePath, 'utf8')) : {};

async function cachedEvaluate(dir, sha, ctx) {
  const key = `${VERSION}:${ctx.templateSha}:${sha}`;
  if (!cache[key]) {
    cache[key] = await evaluate(dir, sha, ctx);
    writeFileSync(cachePath, JSON.stringify(cache));
  }
  return cache[key];
}

async function grade(student, ctx) {
  const repo = `${ctx.org}/${ctx.classroom}-${ctx.assignment}-${student.username}`;
  const row = {
    nombre: student.nombre, usuario: student.username, repo: `https://github.com/${repo}`,
    entregado: 'no', a_tiempo: 'no', nota: '', nota_deadline: '', ultimo_commit: '', detalle: '',
  };

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

  const { stdout: log } = await git(dir, ['log', '--first-parent', '--format=%H\t%cI', `origin/${branch}`]);
  const commits = log.trim().split('\n').filter(Boolean).map((l) => {
    const [sha, date] = l.split('\t');
    return { sha, date: new Date(date) };
  });
  const head = commits[0];
  const atDeadline = commits.find((c) => c.date <= deadline);
  row.ultimo_commit = localIso(head.date);

  if (commits.length === 1) {
    row.nota = 0;
    row.detalle = 'sin commits propios (solo el de la plantilla)';
    return row;
  }

  const h = await cachedEvaluate(dir, head.sha, ctx);
  row.nota = score(h);
  row.entregado = passes(h) ? 'si' : 'no';

  if (atDeadline && atDeadline.sha !== head.sha) {
    const d = await cachedEvaluate(dir, atDeadline.sha, ctx);
    row.a_tiempo = passes(d) ? 'si' : 'no';
    row.nota_deadline = score(d);
    row.detalle = `HEAD ${head.sha.slice(0, 7)}: ${describe(h)} | al límite ${atDeadline.sha.slice(0, 7)}: ${describe(d)}`;
  } else {
    row.a_tiempo = atDeadline ? row.entregado : 'no';
    row.nota_deadline = atDeadline ? row.nota : 0;
    row.detalle = `HEAD ${head.sha.slice(0, 7)}: ${describe(h)}${atDeadline ? '' : ' | sin commits antes del límite'}`;
  }
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

// Se lee el manifiesto directo: `gh teacher assignment list` falla si alguna entrada es inválida.
let manifest;
try {
  const raw = (await gh(['api', `repos/${org}/classroom50/contents/${classroom}/assignments.json`, '--jq', '.content'])).stdout;
  manifest = JSON.parse(Buffer.from(raw, 'base64').toString('utf8')).assignments;
} catch (e) {
  fail(`no pude leer el manifiesto de assignments (¿cuenta correcta? usá --gh-user): ${firstLine(e.stderr || e.message)}`);
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
const templateSha = (await git(templateDir, ['rev-parse', 'HEAD'])).stdout.trim();
const templateBlobs = {};
for (const f of [...CONTRACT, 'src/main/kotlin', 'agente-tests.md']) templateBlobs[f] = await blob(templateDir, templateSha, f);
const ctx = { org, classroom, assignment, templateSha, templateBlobs, ref: resolve(templateDir) };

console.error(`Assignment ${assignment}: ${students.length} alumnos · plantilla ${templateRepo}@${templateSha.slice(0, 7)} · límite ${localIso(deadline)}`);

let done = 0;
const rows = await pool(students, Number(opts.concurrency), async (s) => {
  let row;
  try {
    row = await grade(s, ctx);
  } catch (e) {
    row = { nombre: s.nombre, usuario: s.username, repo: `https://github.com/${org}/${classroom}-${assignment}-${s.username}`, entregado: 'no', a_tiempo: 'no', nota: '', nota_deadline: '', ultimo_commit: '', detalle: `error inesperado: ${firstLine(e.stderr || e.message)}` };
  }
  console.error(`[${++done}/${students.length}] ${s.username}: nota=${row.nota} entregado=${row.entregado} a_tiempo=${row.a_tiempo} (${row.detalle})`);
  return row;
});

rows.sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'));
const cols = ['nombre', 'usuario', 'repo', 'entregado', 'a_tiempo', 'nota', 'nota_deadline', 'ultimo_commit', 'detalle'];
const header = ['nombre', 'usuario', 'repo', 'entregado', 'entrego_a_tiempo', 'nota_sugerida', 'nota_al_limite', 'ultimo_commit', 'detalle'];
const csv = [header, ...rows.map((r) => cols.map((c) => r[c]))].map((r) => r.map(csvCell).join(',')).join('\n');
const outFile = join(opts.out, `reporte-${assignment}.csv`);
writeFileSync(outFile, `\uFEFF${csv}\n`);

console.table(rows.map((r) => ({ nombre: r.nombre, entregado: r.entregado, 'a tiempo': r.a_tiempo, nota: r.nota, 'nota al límite': r.nota_deadline })));
const delivered = rows.filter((r) => r.entregado === 'si').length;
const onTime = rows.filter((r) => r.a_tiempo === 'si').length;
console.error(`Entregaron: ${delivered}/${rows.length} · a tiempo: ${onTime}/${rows.length}\nReporte: ${outFile}`);
