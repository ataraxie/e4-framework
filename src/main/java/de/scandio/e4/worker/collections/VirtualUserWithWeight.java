package de.scandio.e4.worker.collections;

import de.scandio.e4.worker.interfaces.VirtualUser;

public class VirtualUserWithWeight {

	private VirtualUser virtualUser;
	private double weight;

	public VirtualUserWithWeight(VirtualUser virtualUser, double weight) {
		this.virtualUser = virtualUser;
		this.weight = weight;
	}

	public VirtualUser getVirtualUser() {
		return virtualUser;
	}

	public double getWeight() {
		return weight;
	}
}
