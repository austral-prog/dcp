#!/usr/bin/env bash
set -euo pipefail

# Script de verificacion docente para TP 4
# Valida que la solucion de referencia pasa todos los tests (16/16)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
TP4_DIR="$ROOT_DIR/tp/tp-4-funcional"
SOLUCION_DIR="$SCRIPT_DIR/solucion/src/main/kotlin/com/university/dcp"
STARTER_DIR="$TP4_DIR/src/main/kotlin/com/university/dcp"

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
"$TP4_DIR/gradlew" -p "$TP4_DIR" test --rerun

echo "==> Solucion de referencia verificada con exito (todos los tests pasaron)."
