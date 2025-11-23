#!/usr/bin/env bash
# Wrapper script to run Grimoire Client with proper PATH

# Ensure stty is in PATH from nix-shell
export PATH="${PATH}"
exec java -jar "$(dirname "$0")/target/grimoire-cli-0.0.1-SNAPSHOT-client.jar" "$@"
