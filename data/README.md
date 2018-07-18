# Data

This folder contains raw and analyzed data of a few runs, supporting our paper describing the BPjs-based solution to the MDETools18 leader follower challenge.

## What's Here

* **simple-follow**: Trace of the locations of a leader and follower during a simulated run.
* **too-far**: Verification trace of a run, where the follower ends up being too far from the leader.
* **stale-goslow**: Verification trace of a run, where the follower control logic emits a
   `GoSlowGradient` event, based on old data.
* **makeGv.sh**: A Scala shell script that takes the output of a verification trace as a parameter, and creates an event log file and a graphviz file. This graphivz file was used to generate the PDF visualizations, using `neato -Tpdf [[filename.gv]]`. Requires Scala and a unix shell.
