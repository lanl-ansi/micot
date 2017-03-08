package gov.lanl.micot.infrastructure.naturalgas.application.ogf;

import java.io.IOException;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizer;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;

/**
 * Factory method for gas optimization applications
 * @author Russell Bent
 */
public class OGFApplicationFactory implements ApplicationFactory {

  @Override
  public Application createApplication(ProjectConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    
    // set up the models for the application
    if (configuration.getModels().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one electric power model. Using the first one");
    }    
    ModelConfiguration modelConfiguration = configuration.getFirstModel();
    NaturalGasModel model = (NaturalGasModel) ProjectConfigurationUtility.createModel(modelConfiguration);
    ProjectConfigurationUtility.modifyModel(model, configuration, modelConfiguration);

    // set up the scs algorithm
    if (configuration.getAlgorithmConfigurations().size() > 1) {
      System.err.println("Warning: Configuration file defines more than one optimization model. Using the first one");      
    }
    AlgorithmConfiguration algorithmConfiguration = configuration.getFirstAlgorithm(); // assumes a single algorithm
    NaturalGasMathProgramOptimizer algorithm = (NaturalGasMathProgramOptimizer) ProjectConfigurationUtility.createOptimizer(configuration, algorithmConfiguration, model);
        
    OGFApplication application = new OGFApplication();
    application.setModel(model);
    application.setAlgorithm(algorithm);
  
    return application;
  }
      
}
