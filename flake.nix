{
  description = "Achievement sersr";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};
        jdk = pkgs.temurin-bin;
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = [
            (pkgs.sbt.override {jre = jdk;})
            jdk
            pkgs.firefox
            pkgs.geckodriver
            pkgs.yarn
            pkgs.fontconfig
          ];

          shellHook = ''
            # export CHROMIUM_BIN=${pkgs.chromium}/bin/chromium
            # export JAVA_OPTS="$JAVA_OPTS -Dwebdriver.chrome.driver=${pkgs.chromedriver}/bin/chromedriver"
            export JAVA_OPTS="$JAVA_OPTS -Dwebdriver.gecko.driver=${pkgs.geckodriver}/bin/geckodriver"
            export JAVA_OPTS="$JAVA_OPTS -DwebDriverType=FH"
          '';
        };
      }
    );
}
