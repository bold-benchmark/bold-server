# Benchmark Server

To run the server:

```shell script
gradle install
cd build/install/bbench-server
bin/bbench-server
```

Default server configuration is stored in `sim.properties`.
The different tasks of the benchmark also have their own configuration file.

To start/stop a simulation run, send the following HTTP requests to the server:

 - `PUT /sim` with payload `data/sim.ttl`
 - `DELETE /sim`

See also `run.sh`.