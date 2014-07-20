// Karma configuration
// Generated on Thu Jul 17 2014 19:50:55 GMT-0400 (EDT)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      //'public/javascripts/jquery*.js',
      //'public/javascripts/handlebars-*.js',
      //'public/javascripts/ember.js',
      //'public/javascripts/ember-data.js',
      //'public/javascripts/*.js',
      'public/javascripts/app/temporary.js',
      'test/javascripts/app/*Spec.js',
      //'app/assets/templates/*.handlebars',
    ],


    // list of files to exclude
    exclude: [
    ],

    plugins: [
        'karma-jasmine',
        'karma-ember-preprocessor'
    ],

    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
        "**/*.handlebars" : "ember"
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: [],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
