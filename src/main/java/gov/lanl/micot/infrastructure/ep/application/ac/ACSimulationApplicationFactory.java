package gov.lanl.micot.infrastructure.ep.application.ac;

import java.io.IOException;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.simulate.ElectricPowerSimulator;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;

/**
 * Factory method for creating AC Simulation applications
 * @author Russell Bent
 */
public class ACSimulationApplicationFactory implements ApplicationFactory {

  @Override
  public Application createApplication(ProjectConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    
    // set up the models for the application
    if (configuration.getModels().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one electric power model. Using the first one");
    }    
    ModelConfiguration modelConfiguration = configuration.getFirstModel();
    ElectricPowerModel model = (ElectricPowerModel) ProjectConfigurationUtility.createModel(modelConfiguration);
    ProjectConfigurationUtility.modifyModel(model, configuration, modelConfiguration);

    if (configuration.getSimulatorConfigurations().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one simulator model. Using the first one");      
    }
    SimulatorConfiguration simulatorConfiguration = configuration.getFirstSimulator(); // assumes a single simulator
    ElectricPowerSimulator simulator = (ElectricPowerSimulator) ProjectConfigurationUtility.createSimulator(configuration, simulatorConfiguration, model);
        
    ACSimulationApplication application = new ACSimulationApplication();
    application.setModel(model);
    application.setSimulator(simulator);

    return application;
  }
      
}
