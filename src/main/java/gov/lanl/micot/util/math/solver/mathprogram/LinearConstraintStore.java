package gov.lanl.micot.util.math.solver.mathprogram;

import java.util.Map;
import java.util.TreeMap;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;

/**
 * This is a general class for storing and maintaining a set of linear constraints
 * @author Russell Bent
 *
 */
public class LinearConstraintStore {
  
  private Map<String, LinearConstraint>  linearConstraintByName         = null;
  private Map<LinearConstraint, Integer> _linearConstraints           = null;

  /**
   * Constructor
   */
  public LinearConstraintStore() {
    _linearConstraints = new TreeMap<LinearConstraint,Integer>();
    linearConstraintByName = new TreeMap<String,LinearConstraint>();
  }

  /**
   * Add a linear constraint to the store
   * @param constraint
   * @throws InvalidConstraintException 
   */
  public void addLinearConstraint(LinearConstraint constraint) throws InvalidConstraintException {
    if (linearConstraintByName.containsKey(constraint.getName())) {      
      throw new InvalidConstraintException("Error: already contains constraint " + constraint.getName()); 
    }
    linearConstraintByName.put(constraint.getName(), constraint);
    _linearConstraints.put(constraint, _linearConstraints.size());
  }

  /**
   * Remove a linear constraint from the store
   * @param constraint
   */
  public void removeLinearConstraint(LinearConstraint constraint) {
    linearConstraintByName.remove(constraint.getName());
    int idx = _linearConstraints.get(constraint);   
    _linearConstraints.remove(constraint);

    // efficiency step... don't have to update any indices --> corrected: indices this way
    if (idx == _linearConstraints.size()) {
      return;
    }

    for (LinearConstraint c : _linearConstraints.keySet()) {
      if (_linearConstraints.get(c) > idx) {
        _linearConstraints.put(c, _linearConstraints.get(c) - 1);
      }
    }
  }

  /**
   * Get the linear constraints
   * @return
   */
  public Iterable<LinearConstraint> getLinearConstraints() {
    return _linearConstraints.keySet();
  }

  /**
   * Get constraint by name
   * @param constraint
   * @return
   */
  public LinearConstraint getLinearConstraint(String constraint) {
    return linearConstraintByName.get(constraint);
  }

  /**
   * Gets the number of constraints
   */
  public int getNumberOfLinearConstraints() {
    return _linearConstraints.size();
  }

  /**
   * Get the index of a constraint
   * @param c
   * @return
   */
  public int getIndex(LinearConstraint c) {
    return _linearConstraints.get(c);
  }

  
}
