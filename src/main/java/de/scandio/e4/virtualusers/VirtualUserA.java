package de.scandio.e4.virtualusers;

import de.scandio.e4.interfaces.Scenario;
import de.scandio.e4.interfaces.VirtualUser;

import java.util.List;

public class VirtualUserA implements VirtualUser {

    @Override
    public List<Class<Scenario>> getScenarios() {

        return null;
    }
}
