package de.scandio.e4.worker.collections;

import de.scandio.e4.worker.interfaces.VirtualUser;

import java.util.ArrayList;

public class VirtualUserCollection extends ArrayList<VirtualUserWithWeight> {

	public void add(VirtualUser virtualUser, double weight) {
		super.add(new VirtualUserWithWeight(virtualUser, weight));
	}

}
