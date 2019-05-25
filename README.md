# E4 - Enjoyable Elastic Experiment Executor

Our own testing framework.

**Note:** this is work in progress! Expect frequent changes!

## Test Packages

### What is a test package?

A test package is a bunch of Kotlin source files that define how an app can be properly tested. The intention is that the test package is independent from any structural components and focuses on the tasks that are required to test the app.

### Components of a test package

Each test package has a set of components that have certain purposes. All sources for a test package live in de.scandio.e4.testpackages in src/main/kotlin/ and src/test/kotlin.

#### TestPackage declarator

At the top level is a class that extends `TestPackage`. It defines the following:
* Virtual Users: a set of virtual users that perform actions against the a running instance
* Weights: in what relationship should the virtual users be simulated in comparison to vanilla virtual users (i.e. virtual users that have nothing to do with the app)
* Setup Actions: a set of actions that must be executed before the virtual users can be simulated
* Weight ratio: defines the ratio between defined app-specific virtual users and vanilla virtual users

#### Virtual User

Classes in the sub-package `virtualusers` that define virtual users. They must define the following:
* Assumptions: what is assumed in the environment before the virtual user can be simulated
* Preparation: what actions must be performed before the virtual user can be simulated
* Actions: what actions are performed by the virtual user. These can involve both Selenium tasks (majority) and REST calls (minority).

#### Action

Classes in the sub-package `actions` that define actions invoked by virtual users. They must define the following:
* Assumptions: what is assumed in the environment before the action can be executed
* Procedure: what are the actual procedures performed in the action. These can involve both Selenium tasks (majority) and REST calls (minority).
* Result: what is the result if the action was executed successfully
* Time taken: each action defines how long the part (!) of the action took that should be measured (i.e. not everything in an action must be part of a measurement; e.g. sometimes an action will need to login the user first and that shouldn't count to the time being measured)

### How do I test/run/develop a test package?

For development of a test package, create a unit test class in your kotlin test package that extends `TestPackageTestRun`. This class will define the following:
* BASE_URL: the base url of a running and accessible instance to be used for running the tests
* OUT_DIR: absolute path to a directory where output can be saved (e.g. Selenium screenshots)
* USERNAME: username of the application user to be used for testing (e.g. "admin")
* PASSWORD: password of the application user to be used for testing (e.g. "admin")
* TEST_PACKAGE: an instance of your TestPackage declarator class
* PREPARATION_RUN: boolean indicating if this should be a preparation run. If set to true, only the setup actions of your test package will be executed. If set to false, only the virtual users of your test package will be executed (we found some issues running both in sequence with one execution).

#### Examples

There is a (pretty much) completed test package for the app "Page Branching for Confluence" in the package `de.scandio.e4.testpackages.pagebranching`. It has a `PageBranchingTestPackage` class that is the entry point. The constants need to be adjusted to your environment first (yes, these could be outside the source code but that's more annoying during for development). After the properties are set, the class can be run as a simple unit test from within the IDE.
