#!/bin/bash

SCRIPT_DIR="$(
  cd "$(dirname "$0")"
  pwd -P
)"

_check() {
  "${SCRIPT_DIR}/gradlew" clean check --console=verbose
}

_format() {
  "${SCRIPT_DIR}/gradlew" spotlessCheck spotlessJavaApply --console=verbose
}

usage() {
cat <<EOF
format                                               Format all files
check                                                Check for security vulnerabilities
EOF
  exit 1
}

CMD=${1:-}
case ${CMD} in
format) _format ;;
check) _check ;;
*) usage ;;
esac
