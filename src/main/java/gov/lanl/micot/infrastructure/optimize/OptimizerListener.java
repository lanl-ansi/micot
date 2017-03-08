package gov.lanl.micot.infrastructure.optimize;

import gov.lanl.micot.infrastructure.model.Model;

/** 
 * Interface for listening to algorithms
 * @author Russell Bent
 */
public interface OptimizerListener {

	/**
	 * Notification that the model has changed because of some variable
	 * @param state
	 * @param variable
	 */
	public void modelChanged(Model state);
	
	/**
	 * Notification that has changed due to a variable being "undone" 
	 * @param State
	 * @param variable
	 */
	public void modelBacktrack(Model state);
	
	/**
	 * Notification that a new best model has been discovered
	 * @param state
	 */
	public void modelImproved(Model state);
	
	/**
	 * Notification that the algorithm is restarting from the best model it has seen so far
	 */
	public void restartFromBestModel();
	
	/**
	 * Notification that the algorithm has started
	 * @param state
	 */
	public void algorithmStart(Model state);
}
