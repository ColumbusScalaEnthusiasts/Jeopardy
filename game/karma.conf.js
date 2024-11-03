// Karma configuration
// Generated on Thu Jul 17 2014 19:50:55 GMT-0400 (EDT)

module.exports = (config) => {
  config.set({
    // Base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // Frameworks to use
    // Available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // List of files / patterns to load in the browser
    files: [
      'public/javascripts/jquery*.js',
      { pattern: 'public/javascripts/**/*.?(m)js', type: 'module' },
      { pattern: 'test/javascripts/**/*Spec.?(m)js', type: 'module' },
    ],

    // List of files to exclude
    exclude: [
      'public/javascripts/app/**/*.js',
      'public/javascripts/ember*'
    ],

    plugins: [
      'karma-jasmine',
      'karma-firefox-launcher',
      'karma-junit-reporter'
    ],

    // Preprocess matching files before serving them to the browser
    // Available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {},


    // Test results reporter to use
    // Possible values: 'dots', 'progress'
    // Available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'junit'],


    // Web server port
    port: 9876,


    // Enable / disable colors in the output (reporters and logs)
    colors: true,


    // Level of logging
    // Possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // Enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // Start these browsers
    // Available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: [
      // 'Chromium',
      // 'ChromiumHeadless',
      'FirefoxHeadless',
    ],

    // Continuous Integration mode
    // If true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
