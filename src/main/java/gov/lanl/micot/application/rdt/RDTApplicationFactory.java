package gov.lanl.micot.application.rdt;

import java.io.IOException;
import java.util.Collection;

import gov.lanl.micot.infrastructure.application.ApplicationFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

/**
 * Factory method for creating RDT applications
 * @author Russell Bent
 */
public class RDTApplicationFactory implements ApplicationFactory {

  @SuppressWarnings("unchecked")
  @Override
  public RDTApplication createApplication(ProjectConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    
    // set up the models for the application
    if (configuration.getModels().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one electric power model. Using the first one");
    }    
    
    ModelConfiguration modelConfiguration = configuration.getFirstModel();
    ElectricPowerModel model = (ElectricPowerModel) ProjectConfigurationUtility.createModel(modelConfiguration);
        
    
    ProjectConfigurationUtility.modifyModel(model, configuration, modelConfiguration);
    loadMissingData(model, configuration.getScenarioConfigurations());
        
    // check for bad data in the Electric Power Model
    RDTDataValidator validator = new RDTDataValidator();
    validator.checkData(model, configuration.getScenarioConfigurations());
    
    // set up the resilient design algorithm
    if (configuration.getAlgorithmConfigurations().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one optimization model. Using the first one");      
    }
    AlgorithmConfiguration algorithmConfiguration = configuration.getFirstAlgorithm(); // assumes a single algorithm
    Optimizer<ElectricPowerNode, ElectricPowerModel> algorithm = ( Optimizer<ElectricPowerNode, ElectricPowerModel>) ProjectConfigurationUtility.createOptimizer(configuration, algorithmConfiguration, model);
    
    RDTApplication application = new RDTApplication();
    application.setModel(model);
    application.setAlgorithm(algorithm);

    return application;
  }
    
  /**
   * This is required data for the application, and this fills in default values
   * @param model
   */
  protected void loadMissingData(ElectricPowerModel model, Collection<ScenarioConfiguration> scenarios) {
    for (Generator generator : model.getComponents(Generator.class)) {
      if (generator.getAttribute(Generator.HAS_PHASE_A_KEY) == null && generator.getAttribute(Generator.HAS_PHASE_B_KEY) == null && generator.getAttribute(Generator.HAS_PHASE_C_KEY) == null) {
        generator.setAttribute(Generator.HAS_PHASE_A_KEY, true);
        generator.setAttribute(Generator.HAS_PHASE_B_KEY, false);
        generator.setAttribute(Generator.HAS_PHASE_C_KEY, false);
        generator.setAttribute(Generator.NUM_PHASE_KEY, 1);
      }
    }
    for (Load load : model.getComponents(Load.class)) {
      if (load.getAttribute(Load.HAS_PHASE_A_KEY) == null && load.getAttribute(Load.HAS_PHASE_B_KEY) == null && load.getAttribute(Load.HAS_PHASE_C_KEY) == null) {
        load.setAttribute(Load.HAS_PHASE_A_KEY, true);
        load.setAttribute(Load.HAS_PHASE_B_KEY, false);
        load.setAttribute(Load.HAS_PHASE_C_KEY, false);
        load.setAttribute(Load.NUM_PHASE_KEY, 1);
        load.setAttribute(Load.DESIRED_REAL_LOAD_A_KEY, load.getDesiredRealLoad());
        load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, load.getDesiredRealLoad());
      }
    }
    for (ElectricPowerFlowConnection edge : model.getConnections(ElectricPowerFlowConnection.class)) {
      if (edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY) == null && edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY) == null && edge.getAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY) == null) {
        edge.setAttribute(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, true);
        edge.setAttribute(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, false);
        edge.setAttribute(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, false);
        edge.setAttribute(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, 1);
      }
    }
  }
  
}
