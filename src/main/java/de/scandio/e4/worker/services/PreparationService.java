package de.scandio.e4.worker.services;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PreparationService {
    private boolean preparationsAreFinished = false;

    public void reset() {
        preparationsAreFinished = false;
    }

    public boolean arePreparationsFinished() {
        return preparationsAreFinished;
    }

    public void prepare(Map<String, Object> options) {
        System.out.println("[E4W] Starting to prepare...");

        // TODO: create users
        // TODO: store the user credentials
        // TODO: log into every user once (to verify credentials) and click through the first time login intro
        // We need Selenium / REST calls for this
        // This should somehow be defined in a scenario as well

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[E4W] Preparations are finished...");
        preparationsAreFinished = true;
    }
}
