# BOLD Server
The BOLD server can serve an RDF dataset for reading and writing, evolve this dataset using a simulation defined in SPARQL queries, and make measurements for agent benchmarking.

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

## Loading a Configuration

You can configure BOLD by providing configuration files that are read upon startup. `<taskname>` refers to a configuration file `taskname.properties`:
```shell script
bin/bold-server <taskname>
```
Alternatively, with Docker (obviously the configuration files need to be in the Docker image):
```shell script
docker run -p 8080:8080 -e TASKNAME=<taskname> -it bold-server
```
Default server configuration (when no argument is given) is stored in `sim.properties`.

## Serving an RDF Dataset

The BOLD server can serve RDF datasets. The dataset can be configured in a `.properties` file using (taken from [tc3.properties](https://github.com/bold-benchmark/bold-server/blob/jakarta-rest/tc3.properties)):
```
bold.init.dataset = data/*.trig
```
Thereby, the relative URIs in the dataset get resolved against the base URI of the BOLD server. Obviously, the BOLD server only serves URIs that start with the server's base URI. Thus, we again highlight the importance of relative URIs in graph name and the graphs, see [a room classification](https://github.com/bold-benchmark/bold-server/blob/jakarta-rest/data/IBM_B3-classification.trig) as an example.

## Evolving an RDF Dataset (Simulation)

The BOLD server can evolve RDF datasets during simulation runs according to SPARQL UPDATE queries. To this end, update queries need to get registered in the `.properties` file using (taken from [tc3.properties](https://github.com/bold-benchmark/bold-server/blob/jakarta-rest/tc3.properties)):

```
bold.runtime.update = query/tc3-update.rq
```
To start a simulation, make a `POST` request to `/sim` -- this assumes that there is a simulation configuration deployed, see [sim.ttl](https://github.com/bold-benchmark/bold-server/blob/master/data/sim.ttl) for an example. If this is not deployed, e.g. as part of the initial dataset, defaults are used. Alternatively, you can PUT a file that follows the structure of `sim.ttl` to `/gsp/sim` before sending the `POST`.

While running, simulated time is added to `/gsp/sim` as follows:
```
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix sim: <http://ti.rw.fau.de/sim#> .

<sim> sim:currentTime "2020-05-21T09:12:00Z"^xsd:dateTime ;
      sim:currentIteration 72 .
```
To initialise the simulation, it may be necessary to run a query once at the beginning. Such a query can get registered in the `.properties` file using (taken from [tc3.properties](https://github.com/bold-benchmark/bold-server/blob/jakarta-rest/tc3.properties))::
```
bold.init.update = query/tc3-init.rq
```

## Benchmarking Run

To benchmark, we may be interested in faults that are counted. To this end, queries can get registered that determine when there is a fault. Such queries can get registered in the `.properties` file using (taken from [tc3.properties](https://github.com/bold-benchmark/bold-server/blob/jakarta-rest/tc3.properties)):
```
bold.runtime.query = query/tc3.rq
```

At the end of a simulation run, results are stored in the following two files:
 - `faults.tsv`: first column gives the iteration number, following columns give a number of faults for each registered query
 - `interactions.tsv`: first column also gives the iteration number, second column gives the total execution time for registered updates and the number of GET, PUT, DELETE, POST interactions with agents (included average processing time for each)

Results for any two successive runs are separated by `\n\n` (Gnuplot convention for multi-dataset files). Each dataset, i.e. data for a single run, includes a header line starting with `#` (Gnuplot comment symbol).

## Documentation
The BOLD server serves documentation on the root resource. The content can be configured using (taken from [tc3.properties](https://github.com/bold-benchmark/bold-server/blob/jakarta-rest/tc3.properties)):
```
bold.welcome.directory.filepath = doc/
```
## Acknowledgments

This work was partially funded by the German Federal Ministry of Education and Research through the MOSAIK project (grant no. 01IS18070A).
