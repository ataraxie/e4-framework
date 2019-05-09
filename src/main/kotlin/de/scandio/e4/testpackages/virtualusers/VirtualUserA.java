package de.scandio.e4.testpackages.virtualusers;

import de.scandio.e4.worker.interfaces.Scenario;
import de.scandio.e4.worker.interfaces.VirtualUser;

import java.util.Arrays;
import java.util.List;

public class VirtualUserA implements VirtualUser {
	@Override
	public List<Class<Scenario>> getScenarios() {

		return Arrays.asList(
				null,
				null
		);
	}
}
