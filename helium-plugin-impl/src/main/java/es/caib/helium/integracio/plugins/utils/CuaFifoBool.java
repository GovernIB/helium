package es.caib.helium.integracio.plugins.utils;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class CuaFifoBool {

	private final LinkedList<Boolean> linkedList;
	private final int maxSize;

	public CuaFifoBool(int maxSize) {

		this.linkedList = new LinkedList<>();
		this.maxSize = maxSize;
	}

	public void add(boolean item) {

		if (linkedList.size() >= maxSize) {
			linkedList.removeFirst();
		}
		linkedList.add(item);
	}

	public boolean remove() {
		return linkedList.poll();
	}

	public boolean get(int index) {
		return linkedList.get(index);
	}

	public int getOk() {
		return linkedList.stream().filter(x -> x).collect(Collectors.toList()).size();
	}

	public int getError() {
		return linkedList.stream().filter(x -> !x).collect(Collectors.toList()).size();
	}

	public boolean isEmpty() {
		return linkedList.isEmpty();
	}

	public int size() {
		return linkedList.size();
	}

	public int getMaxSize() {
		return maxSize; // Returns the maximum size of the linked list
	}
}
