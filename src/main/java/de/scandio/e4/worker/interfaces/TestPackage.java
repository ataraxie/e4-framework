package de.scandio.e4.worker.interfaces;

import de.scandio.e4.worker.collections.ScenarioCollection;
import de.scandio.e4.worker.collections.VirtualUserCollection;

import java.util.List;

public interface TestPackage {

    ScenarioCollection getSetupScenarios();
    VirtualUserCollection getVirtualUsers();

}
