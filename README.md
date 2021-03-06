# E4 - The enjoyable performance testing framework

E4 is intended for performance and scale testing web applications with arbitrarily many browser agents running on 
arbitrarily many "worker nodes" around the globe.

## Disclaimer

This project and its documentation are work in progress.
While E4 is applicable for any web application, it is currently being used and designed as performance testing 
framework for Atlassian's data center products (particularly Confluence at the moment). 
This may change in the future and the concept and core implementation are independent from Atlassian. 
There is also a small intro [E4 presentation](https://slides.com/fgrund/e4/) 
and a related blog post [Our Atlassian Data Center Story](https://www.scandio.de/blog/en/2019/08/our-data-center-story).

## Why E4?

Our work on E4 was motivated by us not finding the means to test our own Atlassian apps properly with the tools available. In particular, we were bugged by a lack of platform independence (only for a specific product), transparency (what is happening? why am I paying so many $$$ to AWS?) and versatility in testing (e.g. only REST or only Selenium user interaction). 

Therefore, we designed E4 with the following goals in mind:

* E4 works for _any application_ that runs in a browser (at least theoretically; we're not there yet).
* E4 does not fully automate _everything_. We want the developer to remain aware of _what's happening_. For example, we intentionally do not start scripted AWS instances. We accept that using E4 is _not a single command_. We try to make it as easy as necessary - but not easier.
* E4 provides means to test an application with a mix of _HTTP requests and Selenium_. We think that both types of interaction (simulated users using a browser _and_ manually sending HTTP requests / REST calls) are necessary for proper testing. The frameworks we have seen support only one of the two.

## Table of Contents

* [How does it work?](#how-does-it-work)
* [What do I need to do to test my application?](#what-do-i-need-to-do-to-test-my-application)
* [How do I implement a test package?](#how-do-i-implement-a-test-package)
  + [What is a test package?](#what-is-a-test-package)
  + [Components of a test package](#components-of-a-test-package)
    - [TestPackage declarator](#testpackage-declarator)
    - [Virtual User](#virtual-user)
    - [Action](#action)
    - [Developing a test package](#developing-a-test-package)
    - [Run the vanilla Confluence example yourself](#run-the-vanilla-confluence-example-yourself)
    - [Explaining test packages with the `VanillaTestPackage` example](#explaining-test-packages-with-the--vanillatestpackage--example)
* [How do I start a test instance?](#how-do-i-start-a-test-instance)
  + [How to start a Confluence Data Center test system](#how-to-start-a-confluence-data-center-test-system)
    - [Start data center application](#start-data-center-application)
    - [Stop data center application](#stop-data-center-application)
    - [Scale application nodes dynamically](#scale-application-nodes-dynamically)
* [How do I start workers and run a test package?](#how-do-i-start-workers-and-run-a-test-package)
* [How do I start an E4 client and tell workers what to do?](#how-do-i-start-an-e4-client-and-tell-workers-what-to-do)
* [How do I collect and process data?](#how-do-i-collect-and-process-data)

## How does it work?

<img src="doc/e4-map.png" width="600" style="border: 1px solid #ccc; padding: 5px;">

E4 is intended for developers who want to test their web application under load.
The developer implements a _test package_ for her web application that defines how the application is tested with REST and Selenium interactions.
The test package is then executed with a number of concurrent _virtual users_ that are distributed among a configured number of _worker nodes_.
The number of virtual users that are created is defined by the number of configured concurrent users for a test run and the _weight_ specified for each virtual user in the test package.
For example, if there are three virtual users "Creator", "Reader", "Searcher" with weights 0.2, 0.5, 0.3 respectively in the test package, and 100 concurrent users are configured, there will be 20 Creators, 50 Readers and 30 Searchers. Each worker node is a Docker container that executes a fragment of these virtual users (or all if only one worker node is configured).

E4 Workers have REST endpoints and are orchestrated by an E4 client application that is controlled by the developer.
This application is configured with a JSON file specifying the number of concurrent users to simulate, how long to simulate them, and onto which worker nodes to distribute them. 
Each virtual user is assigned a number of _actions_.
These are executed in a loop that only terminates when the test duration (specified by the developer) ends. 
The result of each worker when the test duration ends is an SQLite database file containing measurements for all actions performed.

We think it is most intuitive to see an example worker log output to get a better picture how this works. [Here](doc/sample-worker-log.zip) you can find the (compressed) logs for a test run simulating a "Vanilla" test package for a Atlassian Confluence (Data Center) application.

## What do I need to do to test my application?

You will need to:

1. Implement a test package with Kotlin
1. Have your application running and accessible by HTTP
1. Start worker nodes for running the test package
1. Start a client that tells the workers what to do
1. Collect and process the data from the results

For each of the points above there is a section in this documentation below:

1. [How do I implement a test package?](#how-do-i-implement-a-test-package)
2. [How do I start a test instance?](#how-do-i-start-a-test-instance)
3. [How do I start workers and run a test package?](#how-do-i-start-workers-and-run-a-test-package)
4. [How do I start an E4 client and tell workers what to do?](#how-do-i-start-an-e4-client-and-tell-workers-what-to-do)
5. [How do I collect and process data?](#how-do-i-collect-and-process-data)

## How do I implement a test package?

### What is a test package?

A test package is a bunch of Kotlin source files that define how an app can be properly tested. The intention is that the test package is independent from any structural components of the framework and focuses on the tasks that are required to test the application.

### Components of a test package

Each test package has a set of components that have certain purposes. All sources for a test package live in `de.scandio.e4.testpackages` in `src/main/kotlin/testpackages/<testpackage_name>`.

#### TestPackage declarator

At the top level is a class that extends `TestPackage`. It defines the following:

* **Virtual Users**: a set of simulated users that perform actions against a running application. Each virtual users are assigned one or more _actions_.
* **Weights**: in what quantities/relations/ratios should virtual users be distributed onto worker threads?
* **Setup Actions**: a set of _actions_ that must be executed before the virtual users can be simulated.

#### Virtual User

Classes in the sub-package `virtualusers` that define virtual users. A virtual user repeatedly executes one or more actions, each of which can be measured in terms of `time_taken`. 

#### Action

Classes in the sub-package `actions` that define actions invoked by virtual users. Actions define what steps should be measured as `time_taken`. This allows to run procedures that are not measured (e.g. login procedures).

#### Developing a test package

During development of a test package, it makes most sense to forget about the rather complex test architecture of E4. 
Just assume two things:

* You have a simple test instance of your application running (this doesn't need to have a particularly large dataset)
* You have one admin user in the application that is used for running all virtual users

Once you make sure this works as expected, you can scale up (see later sections).

Create a unit test class in your Kotlin test package that extends [`TestPackageTestRun`](src/test/kotlin/de/scandio/e4/testpackages/TestPackageTestRun.kt).
Create a test method like this:

```
@Test
fun runTest() {
    val testPackage = VanillaTestPackage()
    if (E4TestEnv.PREPARATION_RUN) {
        executeTestPackagePrepare(testPackage)
    } else {
        executeTestPackage(testPackage)
    }
}
```

See [`VanillaTestRun`](src/test/kotlin/de/scandio/e4/testpackages/vanilla/VanillaTestRun.kt) for an example that shows the example for our Confluence `VanillaTestPackage`.

A few environment variables should be set to make this know where things a are:

* `E4_APPLICATION_BASE_URL`: specifies the base URL where your application is running and accessible by HTTP. (default: `http://confluence-cluster-6153-lb:26153`. This is based on the E4 docker toolkit for Confluence data center.)
* `E4_APPLICATION_NAME`: name of application/platform on which you are testing. (Currently `confluence` and `jira` are supported. Default: `confluence`)
* `E4_APPLICATION_VERSION`: version of the application/platform (default: `6.15.3`)
* `E4_USER_NAME`: username of an admin user in the application (default: `admin`)
* `E4_USER_PASSWORD`: password of an admin user in the application (default: `admin`)
* `E4_IN_DIR`: path to directory where files needed for a test package are located (default: `./target/in`)
* `E4_OUT_DIR`: path to directory where output files from E4 are produced (default: `./target/out`)
* `E4_PREPARATION_RUN`: set this to `true` if you want to simulate a preparation run (default: `false`)
* `E4_ENABLE_DUMPING`: dump screenshots and HTML snapshots for Selenium driver to `E4_OUT_DIR` (default: `true`)

We used IntelliJ to set the environment variables and then run the unit tests.
It looks like this for the `VanillaTestPackage`:

<img src="doc/linked/envvars2.png" width="600" style="border: 1px solid #ccc; padding: 5px;">
<img src="doc/linked/envvars.png" width="600" style="border: 1px solid #ccc; padding: 5px;">
<img src="doc/linked/junit.png" width="600" style="border: 1px solid #ccc; padding: 5px;">

The output then looks like this:

<img src="doc/linked/ide-test-output.png" width="600" style="border: 1px solid #ccc; padding: 5px;">

#### Run the vanilla Confluence example yourself

When you clone this repo and set the above JUnit configuration, you should be able to run the `VanillaTestPackage` against your running Confluence instance (this can be a simple empty server instance and doesn't need to be data center). 
If you get this to work, you're not too far away from executing a proper Confluence data center test. 
Feel free to create an issue in this repo if it doesn't work for you and we'll try to help. 

#### Explaining test packages with the `VanillaTestPackage` example

The `VanillaTestPackage` is a test package specific for Atlassian Confluence that is as basic as possible. 
There are 6 virtual users:

* Commentor (weight 0.08) - creates comments
* Reader (weight 0.36) - reads pages and blog posts
* Creator (weight 0.08) - creates pages and blogposts
* Searcher (weight 0.16) - uses the confluence search
* Editor (weight 0.16) - edits pages and blogposts
* Dashboarder (weight 0.16) - visits the dashboard

The weights add up to 1.0, which is necessary for the E4 logic when virtual user instances are created.
If this is not the case, E4 will show a validation error and will not start.
Also, all weights will need to be a multiple of 0.02 (or 0.02) which is for reasons of the internals of the virtual user creation algorithm in E4.

Assume the developer specifies that she wants to run 150 concurrent users.
This will mean that `weight * 150` instances of each virtual user will be created:

* Commentor (weight 0.08) - `0.08 * 150 = 12 virtual users`
* Reader (weight 0.36) - `0.36 * 150 = 54 virtual users`
* ... (you get the idea) ...

While a virtual user in E4 can execute arbitrarily many actions, each virtual user in this test package executes only one action.
These actions are called in a loop, which means that in this example a virtual user will always execute the same actions.

A `virtual users vs. actions` matrix will therefore have 6 lines (virtual users) and only one column (actions).

<table style="text-align: left">
	<tr><th>Commentor</th><td>AddRandomCommentAction</td></tr>
	<tr><th>Reader</th><td>ViewRandomContent</td></tr>
	<tr><th>Creator</th><td>CreateRandomPageAction</td></tr>
	<tr><th>Searcher</th><td>SearchLoremIpsumAction</td></tr>
	<tr><th>Editor</th><td>EditRandomContent</td></tr>
	<tr><th>Dashboarder</th><td>ViewDashboardAction</td></tr>
</table>

While we think this is both simple and does the job of performing different actions against a running application (in this case Confluence), E4 is certainly open for other matrices.
For example, you could think of 3 virtual users `ConfluenceSimpleUser`, `ConfluencePowerUser`, `ConfluenceAdministrator`, that all execute an overlapping set of actions.
In that case there would be only three rows but many more columns in the matrix above.

## How do I start a test instance?

First off: __it's up to you and it's intended!__
We did not want to take all the control away from you!
The idea is: you can start whatever application you want in any environment you want!

However, E4 will help you creating a Confluence/Jira data center instance for testing.
Based on [codeclou](https://github.com/codeclou)'s **AMAZING** [Docker Confluence Data Center](https://github.com/codeclou/docker-atlassian-confluence-data-center) and [Docker Jira Data Center](https://github.com/codeclou/docker-atlassian-jira-data-center) scripts we have created a script for orchestrating a dockerized data center environment for this purpose. 

In this section we describe how we created a Confluence data center instance for testing.
We think it's fairly easy to adapt this for one's particular needs.

**Note**: the developers behind E4 are mainly Atlassian app developers, so we are no DevOps/AWS/Docker pros.
Some of the steps in this manual can most likely more automated or generally more convenient.
But it worked for us this way.

### How to start a Confluence Data Center test system

You need some server with enough resources to run all parts of the cluster (i.e. all application nodes, database, 
load balancer). For a `small dataset`, 50/150/250 concurrent users, and 1/2/4 nodes, we used an `t2.2xlarge` 
(8 CPUs, 32 GB RAM, 20 GB storage) AWS EC2 instance. 
For a `large dataset`, and the same configurations, we used a `c5n.9xlarge` (36 CPUs, 96 GB RAM, 50 GB storage) instance.
In general, we have observed that demand of resources increases strongly with the size of the dataset.

All the instances were Ubuntu systems. 
Docker needs to be installed which we scripted with `./docker/atlassian-cluster/docker-install-ubuntu.sh`.
This script will also create a directory for provisioning and set an environment variable accordingly.
You can also just look at the script and execute the steps manually.
In general the requirements are (Ubuntu example):

1. Installed: `docker-ce`, `docker-ce-cli`, `containerd.io`
1. Docker user setup: `sudo usermod -a -G docker ubuntu`
1. Environment variable `E4_PROV_DIR` set to some directory that E4 will use for provisioning resources

#### Start data center application

On the server, we started our test environment with our script `./docker/atlassian-cluster/e4-atlassian-cluster.sh` (again, this is based on [codeclou](https://github.com/codeclou)'s amazing scripts).
A cluster with 1 node, a heap space of 4096MB, and a ready-made small dataset is started with this command:

```
./e4-atlassian-cluster.sh --action create \
    --scale 1 \
    --appname confluence \
    --version 6.15.3 \
    --provkey conf6153_small \
    --nodeheap 4096
```

The script will print a message when it's done and tell you the URL where Confluence is accessible.
It will all be ready for you after you map the host name in the URL to the IP address of your server in `/etc/hosts`.

The parameters of the script are as follows:

* `action`: what to do? values:
  * `create`: create the cluster (requires `scale`,  `appname`, `version`, `provkey`, `nodeheap` parameters)
  * `destroy`: destroy the cluster (requires `appname`, `version` parameters)
  * `update`: add nodes to the cluster (requires `appname`, `version`, `scale` parameters)
* `scale` (any number): how many nodes to start?
* `appname`: what application to start? currently supported: `confluence|jira`
* `version`: version of the application (e.g. `6.15.3` for Confluence 6.15.3)
* `provkey`: key of the dataset to provision the instance with on startup. If this is not provided the started application will start with the setup wizard. Currently available:
  * `conf6153` - empty Confluence 6.15.3 with setup completed
  * `conf6153_small` - Confluence 6.15.3 with a small dataset
  * `conf6153_large` - Confluence 6.15.3 with a large dataset
  * `jira830` - empty Jira 8.3.0 with setup completed
* `nodeheap`: heap space to use for each node in MB. This will be set as both `-Xmx` and `-Xms` startup parameters.

**Note**: the provided datasets ship with a 10 user timebomb license. To make the datasets work you will have to enter a proper (evaluation) license in the Confluence Admin UI.

#### Stop data center application

For the start command used above, the stop command would be:

```
./e4-atlassian-cluster.sh --action destroy \
    --appname confluence \
    --version 6.15.3
```

#### Scale application nodes dynamically

You can add application nodes with the `update` action using the `scale` parameter.
For example, the following command will add 3 nodes to the cluster:

```
./e4-atlassian-cluster.sh --action destroy \
    --appname confluence \
    --version 6.15.3 \
    --scale 4
```

## How do I start workers and run a test package?

Similar as with the data center machine, you'll need one or more machines to start workers. 
We will show an example with only one machine.
The requirements are (Ubuntu example):

1. Installed: `docker-ce`, `docker-ce-cli`, `containerd.io`
1. Docker user setup: `sudo usermod -a -G docker ubuntu`

There is an installation script for Ubuntu `./docker/worker/docker-install-ubuntu.sh`. 
Afterwards you'll need to re-login to your terminal.

You can now start a worker with `./docker/worker/startworker.sh WORKER_PORT TARGET_SYSTEM_IP APP_NAME APP_VERSION_NODOTS`, where:
* `WORKER_PORT`: the port where the worker will listen for requests from the client
* `TARGET_SYSTEM_IP`: the IP address of the server where Confluence is running (must be accessible)
* `APP_NAME`: name of the Atlassian product (e.g. "confluence")
* `APP_VERSION_NODOTS`: version of the Atlassian products without dots (e.g. "6153" for Confluence version "6.15.3")

This will start a worker Docker container which will wait for requests from the E4 client. 

For example, this will start a worker container on port 3000 of the local machine, with a Confluence 6.15.3 host that is
accessible at IP 18.194.234.155.

```
./docker/worker/startworker.sh 3000 18.194.234.155 confluence 6153
```

The logs of the container will produce this:

```
15:58:10.621 [main] INFO de.scandio.e4.E4Application - E4 in worker-only mode... Enjoy!
15:58:10.624 [main] INFO de.scandio.e4.E4Application - Set custom output dir: /tmp/e4/out/3000
15:58:10.624 [main] INFO de.scandio.e4.E4Application - Set custom input dir: /tmp/e4/in
2019-09-03 | 15:58:11.126 | main |  INFO | d.s.e4.E4Application | Starting E4Application v0.0.1-SNAPSHOT on 6448d9dabef2 with PID 9 (/e4.jar started by root in /)
2019-09-03 | 15:58:11.127 | main |  INFO | d.s.e4.E4Application | No active profile set, falling back to default profiles: default
2019-09-03 | 15:58:12.214 | main |  INFO | d.s.e.w.s.StorageService | New DB created with driver SQLite JDBC
2019-09-03 | 15:58:12.435 | main |  INFO | d.s.e4.E4Application | Started E4Application in 1.715 seconds (JVM running for 2.073)
2019-09-03 | 15:58:12.435 | main |  INFO | d.s.e4.E4Application | E4 Worker is running on: http://localhost:3000/ and waiting for commands.
```

A note on *machine resources*: a worker will spin up a Headless Chrome instance for each virtual user of a test package.
This requires some resources. In our experience, a simplistic formula that works quite well is `100MB RAM for one 
virtual user and 1 CPU for 10 virtual users` (measurements taken from AWS). 
So, for example, if you want to run 50 virtual users on one worker, the worker process will require roughly 
`5GB RAM and 5 CPUs`.

## How do I start an E4 client and tell workers what to do?

An E4 client that will connect to one or more workers is started with the e4-XXX.jar artifact (built with `./mvnw package`).
It will consume a JSON config file. One example is provided in `./example-config.json`. Example command for this config:

```
java -jar target/e4-LATEST.jar -config example-config.json
```

The example config is as follows:

```
{
  "target": { // specifies the target instance
    "url": "http://confluence-cluster-6153-lb:26153/", // base url of the target instance
    "adminUser": "admin", // admin username
    "adminPassword": "admin" // admin password
  },
  "testPackage": "de.scandio.e4.testpackages.vanilla.VanillaTestPackage", // full class path to the test package class
  "numConcurrentUsers": 50, // how many concurrent users?
  "durationInSeconds": 600, // how long should the users run?
  // An array of worker instances. In our case we have only one worker that runs on a machine with the hostname 'e4w'
  // on port 3000 (as described previously with the startworker.sh script). You can specify multiple workers here
  // and the virtual users will be divided onto the workers accordingly.
  "workers": [
    "http://e4w:3000"
  ]
}
```

The logs of the worker will then continue as follows:

```
2019-09-04 | 06:26:42.732 | Thread-5 |  INFO | d.s.e.w.s.PreparationService | [E4W] Preparing worker with index {0} ...
2019-09-04 | 06:26:42.732 | Thread-5 |  INFO | d.s.e.w.s.PreparationService | Running prepare actions of package {de.scandio.e4.testpackages.vanilla.VanillaTestPackage} against URL {http://confluence-cluster-6153-lb:26153/}
2019-09-04 | 06:26:43.608 | Thread-5 |  INFO | d.s.e.w.s.PreparationService | No setup scenarios given.
2019-09-04 | 06:26:43.608 | Thread-5 |  INFO | d.s.e.w.s.PreparationService | [E4W] Preparations are finished!
2019-09-04 | 06:26:44.805 | Thread-6 |  INFO | d.s.e.w.s.TestRunnerService | >>> MAIN E4 THREAD: Running test package {de.scandio.e4.testpackages.vanilla.VanillaTestPackage} against URL {http://confluence-cluster-6153-lb:26153/}
2019-09-04 | 06:26:44.807 | Thread-6 |  INFO | d.s.e.w.s.TestRunnerService | >>> MAIN E4 THREAD: This worker with index {0} needs to start {250} users.
...
```

The performance measurements for each action is also logged:

```
2019-09-04 | 06:38:03.607 | pool-2-thread-218 |  INFO | d.s.e.c.web.WebConfluence | [SELENIUM] Navigating to {pages/viewpage.action?pageId=1245504} with current URL {http://confluence-cluster-6153-lb:26153/#all-updates}
2019-09-04 | 06:38:03.698 | pool-2-thread-202 |  INFO | d.s.e.w.s.StorageService | [REC_MEASURE]4872|pool-2-thread-202-486|Reader|ViewRandomContent|node1:d62e1ae6
```

Then, at the very end, you will be pointed to a SQLite database that contains all the results:

```
2019-09-04 | 06:39:33.510 | Thread-6 |  INFO | d.s.e.w.s.TestRunnerService | >>> MAIN E4 THREAD: All {250} threads are finished. {1634} actions were executed with {45} errors. Your database is at {jdbc:sqlite:/tmp/e4/out/3000/e4-1567578305764.sqlite}
```

This database will contain all the results for your test run. It looks as follows:

<img src="doc/linked/dbscreen.png" width="600" style="border: 1px solid #ccc; padding: 5px;">

You can now download this file (e.g. with `scp`) and continue with the processing. We used 
[DB Browser for SQLite](https://sqlitebrowser.org/) on MacOS as well as the 
[SQLite Command Line Shell](https://sqlite.org/cli.html) for scripted processing.

## How do I collect and process data?

We used Google Spreadsheets for processing of the data. We have a public 
[example spreadsheet](https://docs.google.com/spreadsheets/d/1Oxm7it2gV0oibya9xuTYMv3IEBy_Y2XuvZrYjNGeEeg/edit#gid=0) 
that is a good example of processed data that also contains a fair number of charts.

<img src="doc/linked/spreadsheet.png" width="600" style="border: 1px solid #ccc; padding: 5px;">

We used some scripts (in `./docker/worker/experimental`) to get the data out of the SQLite DB (using the 
_SQLite Command Line Shell_) in the correct format 
for the spreadsheets, but we have found that this is not a tremendous 
improvement over copying the data by hand out of 
the _DB Browser for SQLite_ UI tool.

## Contributing / Contact / Support

E4 has consumed quite some resources at [Scandio](https://github.com/scandio) and we'd love to see others contribute.
We are not sure yet how this should happen, but at the very first, feel free to fork the project and create pull requests.
There is no real format yet as to how we would like to proceed here in the future.

As described in the disclaimer, we do not provide active support for this project. However, feel free to create
issues here in the Github repository if you have questions or would like to get in touch.

## License

[MIT](https://github.com/scandio/e4-framework/blob/docs/LICENSE) ?? [Lively Apps](https://www.livelyapps.com)
