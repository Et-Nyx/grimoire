{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk17
    maven
    curl
    git
    httpie
    poppler_utils
    util-linux
    coreutils
  ];

  shellHook = ''
    echo "Grimoire Development Environment"

    # Ensure stty is in PATH for Lanterna
    export PATH="${pkgs.coreutils}/bin:$PATH"
    echo "stty location: $(which stty)"

    # Alternative: Set TERM to ensure terminal compatibility
    export TERM=xterm-256color

    echo "Java Version:"
    java -version
    echo "Maven Version:"
    mvn -version
  '';
}
