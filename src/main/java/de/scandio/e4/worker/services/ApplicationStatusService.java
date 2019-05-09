package de.scandio.e4.worker.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApplicationStatusService {

    private final TestRunnerService testRunnerService;
    private final PreparationService preparationService;
    private final UserCredentialsService userCredentialsService;

    public ApplicationStatusService(TestRunnerService testRunnerService,
                                    PreparationService preparationService,
                                    UserCredentialsService userCredentialsService) {
        this.testRunnerService = testRunnerService;
        this.preparationService = preparationService;
        this.userCredentialsService = userCredentialsService;
    }

    public Map<String, Object> getApplicationStatus() {
        final Map<String, Object> applicationStatus = new HashMap<>();

        applicationStatus.put("areTestsRunning", testRunnerService.areTestsRunning());
        applicationStatus.put("arePreparationsFinished", preparationService.arePreparationsFinished());
        applicationStatus.put("storedUsers", userCredentialsService.getAllUsers());

        return applicationStatus;
    }
}
