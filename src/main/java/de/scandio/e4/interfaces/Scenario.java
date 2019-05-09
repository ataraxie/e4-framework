package de.scandio.e4.interfaces;

public interface Scenario {

    void execute() throws Exception;

    // scenario ABCD took 50ms
    String getKey();

    long getTimeTaken();
}
