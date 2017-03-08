package gov.lanl.micot.util.math.solver.mathprogram;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;

/**
 * This is a general class for storing and maintaining a set of quadratic constraints
 * @author Russell Bent
 *
 */
public class QuadraticConstraintStore {
  
  private Map<String, QuadraticConstraint>  quadraticConstraintByName   = null;
  private Map<QuadraticConstraint, Integer> _quadraticConstraints     = null;

  /**
   * Constructor
   */
  public QuadraticConstraintStore() {
    _quadraticConstraints = new LinkedHashMap<QuadraticConstraint,Integer>();
    quadraticConstraintByName = new LinkedHashMap<String,QuadraticConstraint>();
  }
  
  /**
   * Gets the number of constraints
   */
  public int getNumberOfQuadraticConstraints() {
    return _quadraticConstraints.size();
  }

  /**
   * Get the constraints
   * @return
   */
  public Iterable<QuadraticConstraint> getQuadraticConstraints() {
    return _quadraticConstraints.keySet();
  }

  /**
   * Add a constraint to the store
   * @param constraint
   * @throws InvalidConstraintException
   */
  public void addQuadraticConstraint(QuadraticConstraint constraint) throws InvalidConstraintException {
    if (quadraticConstraintByName.containsKey(constraint.getName())) {
      throw new InvalidConstraintException("Error: already contains constraint " + constraint.getName()); 
    }
    quadraticConstraintByName.put(constraint.getName(), constraint);
    _quadraticConstraints.put(constraint, _quadraticConstraints.size());
  }

  /**
   * Remove a quadratic constraint
   * @param constraint
   */
  public  void removeQuadraticConstraint(QuadraticConstraint constraint) {
    quadraticConstraintByName.remove(constraint.getName());
    int idx = _quadraticConstraints.get(constraint);   
    _quadraticConstraints.remove(constraint);

    // efficiency step... don't have to update any indicies this way
    if (idx == _quadraticConstraints.size()) {
      return;
    }

    for (QuadraticConstraint c : _quadraticConstraints.keySet()) {
      if (_quadraticConstraints.get(c) > idx) {
        _quadraticConstraints.put(c, _quadraticConstraints.get(c) - 1);
      }
    }
  }

  /**
   * Get a quadrartic constraint
   * @param constraint
   * @return
   */
  public QuadraticConstraint getQuadraticConstraint(String constraint) {
    return quadraticConstraintByName.get(constraint);
  }

  
}
