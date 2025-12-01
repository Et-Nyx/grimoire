#!/usr/bin/env bash
# Wrapper script to run Grimoire Server

exec java -jar "$(dirname "$0")/server/target/grimoire-server-0.0.1-SNAPSHOT.jar" "$@"
