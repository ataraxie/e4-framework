package de.scandio.e4.services;

import de.scandio.e4.interfaces.Scenario;
import de.scandio.e4.interfaces.TestPackage;
import de.scandio.e4.interfaces.VirtualUser;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TestRunnerService {

    public void stopTests() throws Exception {
        throw new Exception("stopping tests not yet implemented");
    }

    public void runTestPackage(String testPackageKey) throws Exception {

        // TODO: Test if no other package is running first

        final Class<TestPackage> testPackage = (Class<TestPackage>) Class.forName(testPackageKey);
        final TestPackage testPackageInstance = testPackage.newInstance();
        final List<Class<? extends VirtualUser>> virtualUsers = testPackageInstance.getVirtualUsers();



        for (Class<? extends VirtualUser> virtualUserClass : virtualUsers) {

            // 1 virtual == 1 thread TODO: make a new thread for this user
            // The threads need to be saved somewhere so we can stop them again
            // Whether there are saved threads can also be used to determine if there is a test running

            final VirtualUser virtualUserInstance = virtualUserClass.newInstance();
            final List<Class<Scenario>> scenarios = virtualUserInstance.getScenarios();

            for (Class<Scenario> scenarioClass : scenarios) {
                Scenario scenario = scenarioClass.newInstance();

                try {
                    scenario.execute();
                    scenario.getTimeTaken();
                } catch (Exception e) {
                    // record scenario as failed
                    e.printStackTrace();
                }
            }
        }
    }
}
