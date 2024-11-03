Jeopardy
========

A group coding project for the Columbus Scala Enthusiasts group.

Running the project
=======
To run tasks on the game project
```
sbt game/<task>
```

The functional project will follow the normal sbt multiproject build convention:
```
sbt functional/<task>
```
Requiremnts
=======
Software needed by default, assuming you don't change configs to a different browser.
A jdk, sbt, yarn/npm, firefox, and geckodriver.

A flake is present that will provide all required softwarea via `nix develop` or direnv.
```sh
nix develop
```

Karma/Jasmine
=======

To run Jasmine tests automagically you'll need a node installation.
http://nodejs.org/

If you are running these tests for the first time you will need to install the karma and some related node packages.  In a console, navigate to this directory and run the following command to do so:
```sh
npm install
```
or as appropriate
```sh
yarn install
```

This should install all the required packages.  Once this is successful you can start running tests automatically by:
Starting Karma by running from the same directory: ```karma start```
Navigate to the url printed out by karma in any browsers you wish to test.
Tests will run automatically when you first connect a browser and any time you make changes to the watched files.
For debugging Js calls to `console.info` will appaar alongside karma's output

Alternatively jasmine tests can be run in browser via
```sh
yarn jasmine-browser-runner server
```
