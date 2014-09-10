Jeopardy
========

A group coding project for the Columbus Scala Enthusiasts group.

Running the project
=======
Due to some issues with the version of Play we are using you have to run a nonstandard command to get the game project to work in sbt.  To run tasks on the play play application from sbt start from the root directory and run:
```
sbt "project game" <task>
```

The functional project will follow the normal sbt multiproject build convention:
```
sbt functional/<task>
```

Karma/Jasmine
=======

To run Jasmine tests automagically you'll need a node installation.
http://nodejs.org/

If you are running these tests for the first time you will need to install the karma and some related node packages.  In a console, navigate to this directory and run the following command to do so:
```
npm install
```

This should install all the required packages.  Once this is successful you can start running tests automatically by:
Starting Karma by running from the same directory: ```karma start```
Navigate to the url printed out by karma in any browsers you wish to test.
Tests will run automatically when you first connect a browser and any time you make changes to the watched files.
