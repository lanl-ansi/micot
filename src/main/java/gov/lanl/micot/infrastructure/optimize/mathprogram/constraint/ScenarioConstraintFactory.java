package gov.lanl.micot.infrastructure.optimize.mathprogram.constraint;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.model.Scenario;

/**
 * Abstract class for assignments having to do with scenarios
 * @author Russell Bent
 *
 */
public abstract class ScenarioConstraintFactory<N extends Node, M extends Model> implements ConstraintFactory<N,M> {

  private Collection<Scenario> scenarios = null;
  
  /**
   * Constructor
   * @param scenarios
   */
  public ScenarioConstraintFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
  /**
   * Get the scenarios
   * @return
   */
  protected Collection<Scenario> getScenarios() {
    return scenarios;
  }
  
  /**
   * Sets the scenarios
   * @param scenarios
   */
  public void setScenarios(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }
  
}
