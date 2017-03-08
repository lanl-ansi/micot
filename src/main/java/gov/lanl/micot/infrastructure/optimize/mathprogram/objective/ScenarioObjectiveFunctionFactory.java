package gov.lanl.micot.infrastructure.optimize.mathprogram.objective;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.model.Scenario;

/**
 * Abstract class for objective function factories having to do with scenarios
 * @author Russell Bent
 *
 */
public abstract class ScenarioObjectiveFunctionFactory<N extends Node, M extends Model> implements ObjectiveFunctionFactory<N,M> {

  private Collection<Scenario> scenarios = null;
  
  /**
   * Constructor
   * @param scenarios
   */
  public ScenarioObjectiveFunctionFactory(Collection<Scenario> scenarios) {
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
