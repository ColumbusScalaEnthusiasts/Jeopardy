export default {
  srcDir: "public/javascripts",
  // SrcFiles should usually be left empty when using ES modules, because you'll
  // Explicitly import sources from your specs.
  srcFiles: [
    "public/javascripts/jquery-1.8.2.min.js",
    "jquery-1.8.2.min.js"
  ],
  files: [],
  specDir: ".",
  specFiles: [
    "test/javascripts/**/*[sS]pec.?(m)js"
  ],
  helpers: [
    "spec/helpers/**/*.?(m)js"
  ],
  esmFilenameExtension: ".js",
  // Allows the use of top-level await in src/spec/helper files. This is off by
  // Default because it makes files load more slowly.
  enableTopLevelAwait: false,
  env: {
    stopSpecOnExpectationFailure: false,
    stopOnSpecFailure: false,
    random: true
  },

  // For security, listen only to localhost. You can also specify a different
  // Hostname or IP address, or remove the property or set it to "*" to listen
  // To all network interfaces.
  listenAddress: "localhost",

  // The hostname that the browser will use to connect to the server.
  hostname: "localhost",

  browser: {
    name: "firefox"
  }
};
