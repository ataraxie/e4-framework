package de.scandio.e4.testpackages;

import de.scandio.e4.worker.interfaces.TestPackage;
import de.scandio.e4.worker.interfaces.VirtualUser;
import de.scandio.e4.testpackages.virtualusers.VirtualUserA;
import de.scandio.e4.testpackages.virtualusers.VirtualUserB;

import java.util.Arrays;
import java.util.List;

public class PageBranchingTestPackage implements TestPackage {
	@Override
	public List<Class<? extends VirtualUser>> getVirtualUsers() {
		return Arrays.asList(
				VirtualUserA.class,
				VirtualUserB.class
		);
	}
}
