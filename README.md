# BOLD Server

## Quickstart

To run the server:

```shell script
gradle install
cd build/install/bold-server
bin/bold-server
```
Alternatively, with Docker:
``` shell script
docker build . -t bold-server
docker run -p 8080:8080 -it bold-server
```

Then, go to http://127.0.1.1:8080 for a tutorial.

## Benchmark Run

To evaluate agents against one of the tasks of the benchmark, run the server with an argument:

```shell script
bin/bold-server <taskname>
```
Alternatively, with Docker:
```shell script
docker run -p 8080:8080 -e TASKNAME=<taskname> -it bold-server
```

The first argument of the server command is a task name, e.g. `ts1` will load `ts1.properties`.
Default server configuration (when no argument is given) is stored in `sim.properties`.

To start/stop a simulation run, send the following HTTP requests to the server:

 - `POST /sim` -- this assumes that there is a simulation configuration deployed, see [sim.ttl](https://github.com/bold-benchmark/bold-server/blob/master/data/sim.ttl) for an example. If this is not deployed, defaults are used, or can PUT `sim.ttl` to `/gsp/sim`.

See also `run.sh` (to execute after the server has started on port 8080).

While running, simulated time is added to `/gsp/sim` as follows:
```
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix sim: <http://ti.rw.fau.de/sim#> .

<sim> sim:currentTime "2020-05-21T09:12:00Z"^xsd:dateTime ;
      sim:currentIteration 72 .
```

At the end of a simulation run, results are stored in the following two files:
 - `faults.tsv`: first column gives the iteration number, following columns give a number of faults for each registered query
 - `interactions.tsv`: first column also gives the iteration number, second column gives the total execution time for registered updates and the number of GET, PUT, DELETE, POST interactions with agents (included average processing time for each)

Results for any two successive runs are separated by `\n\n` (Gnuplot convention for multi-dataset files). Each dataset, i.e. data for a single run, includes a header line starting with `#` (Gnuplot comment symbol).

## Acknowledgments

This work was partially funded by the German Federal Ministry of Education and Research through the MOSAIK project (grant no. 01IS18070A).
