# BPjsLeaderFollower

This project is a solution to the [leader-follower challenge](https://mdetools.github.io/mdetools18/challengeproblem.html)
from MODELS18. It contains logic that controls a rover, making sure it to follows another rover at a safe distance. The controller logic is written using Behavioral Programming (BP), using [BPjs](https://github.com/bThink-BGU/BPjs).

The code in this repository is arranged using [Maven](http://maven.apache.org), which means most IDEs support it out-of-the-box, or via plugins.

## What's here

* **README.md**: This file
* **data**: Interesting runs and their analyses
* **pom.xml**: Maven project file
* **src**: Source code (both Java and Javascript)

## What to run

This project contains a few executable classes:

* `il.ac.bgu.cs.bp.leaderfollower.BPJsRoverControl`: Our implementation of the leader/follower challenge. Running this class opens an application that connects to the `UnityObserver.jar` application, and then displays a window allowing users to start the simulation.
* `il.ac.bgu.cs.bp.leaderfollower.ComplexEnvironmentVerification`: Runs verification on the rover control logic. This verification checks that the distance between the follower and the leader is no larger than 15 units.
* `il.ac.bgu.cs.bp.leaderfollower.SimpleModelChecking`: Verifies simple properties of the model, by pushing specific `Telemetry` events and expecting some response. This approach is close to unit testing, but it is resilient to indeterminism in the tested code.
* `il.ac.bgu.cs.bp.leaderfollower.StaleGoSlowVerification`: Verifies an old version of the rover control model, and exposes a bug where a `GoSlowGradient` event with stale data is selected. The results of this verification can be seen [here](/data/).

## How to Run
* From an IDE: select a main class to run (see above about which class).
* From the commandline:
  1. Make sure [Maven](http://maven.apache.org) and Java are installed and are known to the commandline (e.g. in UNIX, part of the `$PATH`).
  1. Generate a `.jar` file: Run `mvn -P uber-jar package` at the repository's top-level directory.
  1. Run: `java -cp target/BPjsLeaderFollower-0.5-DEV.jar [[main class]]`

### How to Control a Rover
* Download the simulators from https://mdetools.github.io/mdetools18/challengeproblem.html
* Run the simulator for your platform, and `UnityObserver.jar`. Make sure that the observer connected to the simulator successfully.
* Run class `il.ac.bgu.cs.bp.leaderfollower.BPJsRoverControl`.


## Notes
* The settings file `config.txt` is stored in the `resources` folder.


## Infrastructure
* This project uses [BPjs](https://github.com/bThink-BGU/BPjs).
* BPjs uses the Mozilla Rhino Javascript engine. See [here](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino) for project page and source code.
