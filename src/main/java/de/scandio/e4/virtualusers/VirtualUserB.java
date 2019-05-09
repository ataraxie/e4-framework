package de.scandio.e4.virtualusers;

import de.scandio.e4.interfaces.Scenario;
import de.scandio.e4.interfaces.VirtualUser;

import java.util.List;

public class VirtualUserB implements VirtualUser {
    @Override
    public List<Class<Scenario>> getScenarios() {
        return null;
    }
}
