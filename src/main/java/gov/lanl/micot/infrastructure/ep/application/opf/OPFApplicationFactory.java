package gov.lanl.micot.infrastructure.ep.application.opf;

import java.io.IOException;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;

/**
 * Factory method for creating SCS applications
 * @author Russell Bent
 */
public class OPFApplicationFactory implements ApplicationFactory {

  @SuppressWarnings("unchecked")
  @Override
  public Application createApplication(ProjectConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    
    // set up the models for the application
    if (configuration.getModels().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one electric power model. Using the first one");
    }    
    ModelConfiguration modelConfiguration = configuration.getFirstModel();
    ElectricPowerModel model = (ElectricPowerModel) ProjectConfigurationUtility.createModel(modelConfiguration);
    ProjectConfigurationUtility.modifyModel(model, configuration, modelConfiguration);

    // set up the scs algorithm
    if (configuration.getAlgorithmConfigurations().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one optimization model. Using the first one");      
    }
    AlgorithmConfiguration algorithmConfiguration = configuration.getFirstAlgorithm(); // assumes a single algorithm
    Optimizer<ElectricPowerNode, ElectricPowerModel> algorithm = ( Optimizer<ElectricPowerNode, ElectricPowerModel>) ProjectConfigurationUtility.createOptimizer(configuration, algorithmConfiguration, model);
        
    OPFApplication application = new OPFApplication();
    application.setModel(model);
    application.setAlgorithm(algorithm);
  
    return application;
  }
      
}
