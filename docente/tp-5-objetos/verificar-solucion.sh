#!/usr/bin/env bash
set -euo pipefail

# Script de verificacion docente para TP 5
# Valida que la solucion de referencia pasa todos los tests (23/23)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
TP5_DIR="$ROOT_DIR/tp/tp-5-objetos"
SOLUCION_DIR="$SCRIPT_DIR/solucion/src/main/kotlin/com/university/dcp/smarthome"
STARTER_DIR="$TP5_DIR/src/main/kotlin/com/university/dcp/smarthome"

TMP_BACKUP="$(mktemp -d)"

cleanup() {
    if [ -d "$TMP_BACKUP" ]; then
        cp -r "$TMP_BACKUP"/* "$STARTER_DIR/"
        rm -rf "$TMP_BACKUP"
    fi
}
trap cleanup EXIT

echo "==> Guardando copia temporal del codigo de partida..."
cp -r "$STARTER_DIR"/* "$TMP_BACKUP/"

echo "==> Copiando archivos de la solucion de referencia..."
cp -r "$SOLUCION_DIR"/* "$STARTER_DIR/"

echo "==> Ejecutando suite de tests automatizados..."
"$TP5_DIR/gradlew" -p "$TP5_DIR" test --rerun

echo "==> Solucion de referencia verificada con exito (todos los tests pasaron)."
