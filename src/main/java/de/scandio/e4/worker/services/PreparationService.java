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
        // TODO: create users
        // TODO: store the user credentials
        // TODO: log into every user once (to verify credentials) and click through the first time login intro
        preparationsAreFinished = true;
        throw new IllegalStateException("prepare is not yet implemented");
    }
}
