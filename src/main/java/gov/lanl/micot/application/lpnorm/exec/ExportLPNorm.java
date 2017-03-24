package gov.lanl.micot.application.lpnorm.exec;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationReader;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfigurationUtility;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.application.rdt.io.RDTLPNormExporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Code for a problem specification into the format required by LPNORM
 * 
 * @author Russell Bent
 */
public class ExportLPNorm {

  /**
   * @param args
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    
    // create the example for running the lpnorm code
    int scenarios = 2;
    int scenario = 1;
    
    String dir = "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar;
    String out = "example2";
    String directories[] = { "0.1% Per Mile Damage" };
    String output_postfix[] = { "" };
    String master_fn = "config-scip.json";    
 
    
    
    
   
    
    
//    int scenarios = 100;
 //   int scenario = 1;
  //  String master_fn = "config.json";


    
    
    
    // Ice No Harden
    //String dir = System.getProperty("user.dir") + File.separatorChar + "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + 
      //  "Ice No Harden" + File.separatorChar + "Urban" + File.separatorChar;
    //String out = "Ice_No_Harden_Urban_";
    
    //String directories[] = { "1% Per Mile Damage", "10% Per Mile Damage", "100% Per Mile Damage", "100000% Per Mile Damage", "100000000000% Per Mile Damage", "15% Per Mile Damage", "2% Per Mile Damage", "20% Per Mile Damage", "25% Per Mile Damage", "3% Per Mile Damage", "30% Per Mile Damage", "35% Per Mile Damage", "4% Per Mile Damage", "40% Per Mile Damage", "45% Per Mile Damage" };
    //String output_postfix[] = { "1", "10", "100", "100000", "1E11", "15", "2", "20", "25", "3", "30", "35", "4", "40", "45" };

    // Ice Harden
/*    String dir = System.getProperty("user.dir") + File.separatorChar + "config_files" + File.separatorChar + "Resilience" + File.separatorChar + "34Bus" + File.separatorChar + 
        "Ice Harden" + File.separatorChar + "Rural" + File.separatorChar;
    String out = "Ice_Harden_Rural_";    
    String directories[] = {
        "0.01% Per Mile Damage", 
        "0.02% Per Mile Damage",
        "0.03% Per Mile Damage",
        "0.04% Per Mile Damage",
        "0.05% Per Mile Damage",
        "0.1% Per Mile Damage",
        "0.15% Per Mile Damage",
        "0.2% Per Mile Damage",
        "0.25% Per Mile Damage",
        "0.3% Per Mile Damage",
        "0.35% Per Mile Damage",
        "0.4% Per Mile Damage",
        "0.45% Per Mile Damage",
        "0.5% Per Mile Damage",
        "0.55% Per Mile Damage",
        "0.6% Per Mile Damage",
        "0.65% Per Mile Damage",
        "0.7% Per Mile Damage",
        "0.75% Per Mile Damage",
        "0.8% Per Mile Damage",
        "0.85% Per Mile Damage",
        "0.9% Per Mile Damage",
        "0.95% Per Mile Damage",
        "1.0% Per Mile Damage",
        "1.0E9% Per Mile Damage",
        "1000.0% Per Mile Damage"
    };
    
    String output_postfix[] = { 
        "1", 
        "2",
        "3",
        "4",
        "5",
        "10",
        "15",
        "20",
        "25",
        "30",
        "35",
        "40",
        "45",
        "50",
        "55",
        "60",
        "65",
        "70",
        "75",
        "80",
        "85",
        "90",
        "95",
        "100",
        "100000",
        "1E11"
    };*/

    
    for (int k = 0; k < output_postfix.length; ++k) {
      String directory = dir + directories[k] + File.separatorChar;
      
      System.out.println(directory);
      

      String masterFile = directory + master_fn;
      String scenarioStart = directory + "config-";

      String output = out + output_postfix[k] + ".json";

      ArrayList<String> scenarioConfigurationFiles = new ArrayList<String>();
      for (scenario = 1; scenario <= scenarios; ++scenario) {
        String scenarioFile = scenarioStart + scenario + ".json";
        scenarioConfigurationFiles.add(scenarioFile);
      }

      JsonProjectConfigurationReader reader = new JsonProjectConfigurationReader();
      ProjectConfiguration configuration = reader.readConfiguration(masterFile, scenarioConfigurationFiles);

      ElectricPowerModel model = (ElectricPowerModel) ProjectConfigurationUtility.createModel(configuration.getFirstModel());
      ProjectConfigurationUtility.modifyModel(model, configuration, configuration.getFirstModel());

      Collection<Scenario> s = new ArrayList<Scenario>();
      for (ScenarioConfiguration c : configuration.getScenarioConfigurations()) {
        s.add(c.getScenario());
      }

      RDTLPNormExporter exporter = new RDTLPNormExporter();
      exporter.exportJsonPowerModel(model, configuration.getFirstAlgorithm(), s, output);
    }
  }
}
