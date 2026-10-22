#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -n "${JAVA_HOME:-}" ]]; then
    JAVA="$JAVA_HOME/bin/java"
    JAVAC="$JAVA_HOME/bin/javac"
else
    JAVA=java
    JAVAC=javac
fi
mkdir -p build
find src tests -name '*.java' -print > build/sources.txt
"$JAVAC" -encoding UTF-8 -d build @build/sources.txt
case "${1:-demo}" in
    demo) "$JAVA" -cp build app.CityPlanner ;;
    test) "$JAVA" -cp build tests.FoundationTests ;;
    *) echo 'Commands: demo | test' >&2; exit 2 ;;
esac
