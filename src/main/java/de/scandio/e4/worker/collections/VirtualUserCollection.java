package de.scandio.e4.worker.collections;

import de.scandio.e4.worker.interfaces.VirtualUser;

import java.util.ArrayList;

public class VirtualUserCollection extends ArrayList<VirtualUserWithWeight> {

	private double totalWeight = 0;

	public void add(VirtualUser virtualUser, double weight) throws Exception {
		totalWeight += weight;
		if (totalWeight > 1) {
			throw new Exception("Total weight is now above 1 in this collection!");
		}
		super.add(new VirtualUserWithWeight(virtualUser, weight));
	}

}
