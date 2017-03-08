package gov.lanl.micot.infrastructure.transportation.road.application.simulation;

import java.io.IOException;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationFactory;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;
import gov.lanl.micot.infrastructure.transportation.road.simulate.RoadSimulator;

/**
 * Factory method for creating Road Simulation applications
 * @author Russell Bent
 */
public class RoadSimulationApplicationFactory implements ApplicationFactory {

  @Override
  public Application createApplication(ProjectConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    
    // set up the models for the application
    if (configuration.getModels().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one electric power model. Using the first one");
    }    
    ModelConfiguration modelConfiguration = configuration.getFirstModel();
    RoadModel model = (RoadModel) ProjectConfigurationUtility.createModel(modelConfiguration);
    ProjectConfigurationUtility.modifyModel(model, configuration, modelConfiguration);

    if (configuration.getSimulatorConfigurations().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one simulator model. Using the first one");      
    }
    SimulatorConfiguration simulatorConfiguration = configuration.getFirstSimulator(); // assumes a single simulator
    RoadSimulator simulator = (RoadSimulator) ProjectConfigurationUtility.createSimulator(configuration, simulatorConfiguration, model);
        
    RoadSimulationApplication application = new RoadSimulationApplication();
    application.setModel(model);
    application.setSimulator(simulator);

    return application;
  }
      
}
