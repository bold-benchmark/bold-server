package org.bold;

import org.bold.io.FileUtils;
import org.bold.sim.SimulationEngine;
import org.bold.sim.SimulationHandler;
import org.bold.ts.FMUTransitionSystem;

import java.io.FileInputStream;
import java.util.Properties;

public class Configurator {

    private final static String SERVER_HTTP_PORT_KEY = "bold.server.httpPort";

    private final static String SERVER_HTTP_PORT_DEFAULT = "8080";

    private final static String INIT_DATASET_KEY = "bold.init.dataset";

    private final static String RUNTIME_QUERY_KEY = "bold.runtime.query";

    private final static String REPLAY_DUMP_KEY = "bold.replay.dump";

    private final static String SPARQL_INIT = "bold.sparql.init";

    private final static String SPARQL_UPDATE = "bold.sparql.update";

    private final static String FMU_FILENAME = "bold.fmu.filename";

    public static void main(String[] args) throws Exception {
        // TODO more advanced CLI
        String task = args.length > 0 ? args[0] : "sim";

        Properties config = new Properties();
        config.load(new FileInputStream((task + ".properties")));

        int port = Integer.parseInt(config.getProperty(SERVER_HTTP_PORT_KEY, SERVER_HTTP_PORT_DEFAULT));
        SimulationHandler handler = new SimulationHandler(port);

        SimulationEngine engine = handler.getSimulationEngine();

        for (String f : FileUtils.listFiles(config.getProperty(INIT_DATASET_KEY))) {
            engine.registerDataset(f);
        }

        for (String f : FileUtils.listFiles(config.getProperty(SPARQL_INIT))) {
            engine.registerSingleUpdate(f);
        }

        for (String f : FileUtils.listFiles(config.getProperty(SPARQL_UPDATE))) {
            engine.registerContinuousUpdate(f);
        }

        for (String f : FileUtils.listFiles(config.getProperty(RUNTIME_QUERY_KEY))) {
            engine.registerQuery(f);
        }

        for (String f : FileUtils.listFiles(config.getProperty(FMU_FILENAME))) {
            engine.registerTransitionSystem(new FMUTransitionSystem(engine, f));
        }

        String filenamePattern = config.getProperty(REPLAY_DUMP_KEY);
        engine.setDumpPattern(filenamePattern);

        engine.registrationDone();
    }

}
