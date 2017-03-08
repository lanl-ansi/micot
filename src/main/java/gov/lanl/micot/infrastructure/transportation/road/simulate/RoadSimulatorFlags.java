package gov.lanl.micot.infrastructure.transportation.road.simulate;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadNode;
import gov.lanl.micot.util.collection.Pair;

/**
 * Flags used for shortest path solvers
 * @author Russell Bent
 */
public class RoadSimulatorFlags extends SimulatorFlags {
	
	private static final long serialVersionUID = 1L;
 
  public static final String All_PAIR_NODES_TAG                   = "ALL_PAIR_NODES";
  public static final String INDIVIDUAL_PAIR_NODES_TAG            = "INDIVIDUAL_PAIR_NODES";
  
  /**
   * Constructor
   */
	public RoadSimulatorFlags() {
		super();
		put(All_PAIR_NODES_TAG, new ArrayList<RoadNode>());
    put(INDIVIDUAL_PAIR_NODES_TAG, new ArrayList<Pair<RoadNode,RoadNode>>());
  }
	
	/**
	 * Constructor
	 * @param flags
	 */
	public RoadSimulatorFlags(SimulatorFlags flags) {
		this();
		fill(flags);
	}
}
