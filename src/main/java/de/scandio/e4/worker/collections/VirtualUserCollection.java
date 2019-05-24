package de.scandio.e4.worker.collections;

import de.scandio.e4.worker.interfaces.VirtualUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VirtualUserCollection extends ArrayList<VirtualUser> {

	private double totalWeight = 0;

	private Map<VirtualUser, Double> weights = new HashMap<>();

	public void add(VirtualUser virtualUser, double weight) throws Exception {
		super.add(virtualUser);
		this.totalWeight += weight;
		if (this.totalWeight > 1) {
			throw new Exception("Total weight is now above 1 in this collection!");
		}
		this.weights.put(virtualUser, weight);
	}

	public Double getWeight(VirtualUser virtualUser) {
		return this.weights.get(virtualUser);
	}
}
