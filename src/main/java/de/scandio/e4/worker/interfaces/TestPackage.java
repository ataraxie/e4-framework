package de.scandio.e4.worker.interfaces;

import java.util.List;

public interface TestPackage {

    List<Class<? extends Scenario>> getSetupScenarios();

    List<Class<? extends VirtualUser>> getVirtualUsers();

}
