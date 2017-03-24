package gov.lanl.micot.application.scenariobuilder;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * Interface for creating scenarios.  Most common use case is scenarios of damage, but in principal
 * could be used for any type of scenario that modifies and underlying set of assets.
 * 
 * This should serve as the entry point to the fragility codes
 * 
 * I really, really, really don't like having to pass in the identifier tag, but I don't see a way around it.
 * 
 * @author Russell Bent
 *
 */
public interface ScenarioConfigurationBuilder {

  public void updateScenarios(Collection<ScenarioConfiguration> scenarios, Collection<Asset> assets, String idKey);
  
}
