package gov.lanl.micot.application.lpnorm;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.RDDTApplicationFactory;

/**
 * Factory method for creating LPNorm versions of RDT applications 
 * @author Russell Bent
 */
public class LPNormApplicationFactory extends RDDTApplicationFactory {

  private static final double LARGE_HARDEN_COST = 1e10;
    
  @Override
  protected void loadMissingData(ElectricPowerModel model, Collection<ScenarioConfiguration> scenarios) {
    super.loadMissingData(model, scenarios);
    
    // add a hardening cost for a line that gets disabled in an scenario
    for (Line line : model.getLines()) {
      Object hardenCost = line.getAttribute(AlgorithmConstants.LINE_HARDEN_COST_KEY);
      if (hardenCost != null) {
        continue;
      }
      
      for (ScenarioConfiguration scenario : scenarios) {
        boolean damage = !scenario.getScenario().computeActualStatus(line, true);        
        if (damage) {
          line.setAttribute(AlgorithmConstants.LINE_HARDEN_COST_KEY, LARGE_HARDEN_COST);
          break;
        }
      }
    }
    
    
  }
  
}
