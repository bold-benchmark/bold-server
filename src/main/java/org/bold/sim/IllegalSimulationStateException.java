package org.bold.sim;

public class IllegalSimulationStateException extends RuntimeException {

    public IllegalSimulationStateException() {
        super();
    }

    public IllegalSimulationStateException(String msg) {
        super(msg);
    }

    public IllegalSimulationStateException(Throwable reason) {
        super(reason);
    }

    public IllegalSimulationStateException(String msg, Throwable reason) {
        super(msg, reason);
    }

}
