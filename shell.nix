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
  ];

  shellHook = ''
    echo "Grimoire Development Environment"
    echo "Java Version:"
    java -version
    echo "Maven Version:"
    mvn -version
  '';
}
