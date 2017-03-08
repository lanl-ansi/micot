package gov.lanl.micot.infrastructure.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * This is a scenario dependent attribute, mainly used for storing results
 * 
 * @author Russell Bent
 */
public class ScenarioAttribute extends Number {

	private static final long serialVersionUID = 1L;
	private TreeMap<Scenario, Number> lookupTable = null;

	/**
	 * Constructor
	 */
	public ScenarioAttribute() {
		lookupTable = new TreeMap<Scenario, Number>();
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 */
	public ScenarioAttribute(Map<Scenario, Number> map) {
		lookupTable = new TreeMap<Scenario, Number>();
		lookupTable.putAll(map);
	}

	/**
	 * Get a value
	 * @param scenario
	 * @return
	 */
	public Number getValue(Scenario scenario) {
	  return lookupTable.get(scenario);
	}

	/**
	 * Add an entry to the table
	 * 
	 * @param time
	 * @param value
	 */
	public void addEntry(Scenario scenario, Number value) {
		lookupTable.put(scenario, value);
	}

	@Override
	public double doubleValue() {
	  return lookupTable.firstEntry().getValue().doubleValue();
	}

	@Override
	public float floatValue() {
    return lookupTable.firstEntry().getValue().floatValue();
	}

	@Override
	public int intValue() {
    return lookupTable.firstEntry().getValue().intValue();
	}

	@Override
	public long longValue() {
    return lookupTable.firstEntry().getValue().longValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Scenario key : lookupTable.keySet()) {
			result = prime * result + key.hashCode();
			result = prime * result + lookupTable.get(key).hashCode();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ScenarioAttribute)) {
			return false;
		}

		ScenarioAttribute fcn = (ScenarioAttribute) obj;

		for (Scenario key : lookupTable.keySet()) {
			if (fcn.lookupTable.get(key) == null) {
				return false;
			}
			if (!lookupTable.get(key).equals(fcn.lookupTable.get(key))) {
				return false;
			}
		}

		// just check to make sure there are no other keys floating around
		for (Scenario key : fcn.lookupTable.keySet()) {
			if (lookupTable.get(key) == null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return lookupTable.toString();
	}
}
