package de.scandio.e4.worker.interfaces;

import de.scandio.e4.worker.collections.ActionCollection;
import de.scandio.e4.worker.collections.VirtualUserCollection;

public interface TestPackage {

    VirtualUserCollection getVirtualUsers();
    ActionCollection getSetupActions();

}
