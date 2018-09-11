package edu.ucsf.rbvi.mtxReader.internal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MTXManager {
	Map<String, MatrixMarket> mtxMap;

	public MTXManager() {
		mtxMap = new HashMap<>();
	}

	public void addMatrix(String name, MatrixMarket mtx) {
		mtxMap.put(name, mtx);
	}

	public MatrixMarket getMatrix(String name) {
		if (mtxMap.containsKey(name)) return mtxMap.get(name);
		return null;
	}

	public Set<String> getMatrixNames() {
		return mtxMap.keySet();
	}
}
