package de.scandio.e4.testpackages.virtualusers;

import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.VirtualUser;

import java.util.List;

public class VirtualUserB implements VirtualUser {
    @Override
    public List<Class<Scenario>> getScenarios() {
        return null;
    }
}
