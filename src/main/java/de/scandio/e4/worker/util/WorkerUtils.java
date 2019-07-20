package de.scandio.e4.worker.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WorkerUtils {

	public static String getRuntimeName() {
		Thread currentThread = Thread.currentThread();
		return currentThread.getName() + "-" + currentThread.getId();
	}

	public static <T> List<T> getRandomItems(List<T> items, int howMany) {
		List<T> ret = new ArrayList<>();
		int size = items.size();
		for (int i = 0; i < howMany; i++) {
			int rnd = new Random().nextInt(size);
			ret.add(items.get(rnd));
		}
		return ret;
	}

	public static <T> T getRandomItem(List<T> items) {
		return getRandomItems(items, 1).get(0);
	}
}
