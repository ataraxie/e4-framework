package de.scandio.e4.worker.collections;

import de.scandio.e4.worker.interfaces.Action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ActionCollection extends ArrayList<Action> {

	private Set<Action> actionsExcludedFromMeasurement = new HashSet<>();

	public void addExcludeFromMeasurement(Action action) {
		super.add(action);
		this.actionsExcludedFromMeasurement.add(action);
	}

	public void addAllExcludeFromMeasurement(ActionCollection actions) {
		for (Action action : actions) {
			this.addExcludeFromMeasurement(action);
		}
	}

	public boolean isExcludedFromMeasurement(Action action) {
		return this.actionsExcludedFromMeasurement.contains(action);
	}

}
