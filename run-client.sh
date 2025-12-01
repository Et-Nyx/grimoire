#!/usr/bin/env bash
# Wrapper script to run Grimoire TUI Client

# Ensure stty is in PATH from nix-shell
export PATH="${PATH}"
exec java -jar "$(dirname "$0")/tui/target/grimoire-tui-0.0.1-SNAPSHOT-jar-with-dependencies.jar" "$@"
