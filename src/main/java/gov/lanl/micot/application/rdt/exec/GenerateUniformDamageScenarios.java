package gov.lanl.micot.application.rdt.exec;

import gov.lanl.micot.application.rdt.RDDTApplicationFactory;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.application.rdt.algorithm.ep.mip.ResilienceExpansionFactory;
import gov.lanl.micot.application.scenariobuilder.ScenarioConfigurationBuilder;
import gov.lanl.micot.application.scenariobuilder.UniformEdgeDamageScenarioConfigurationBuilder;
import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.opendss.OpenDSSModelConstants;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ApplicationConfiguration;
import gov.lanl.micot.infrastructure.project.JsonProjectConfigurationWriter;
import gov.lanl.micot.infrastructure.project.ModelConfiguration;
import gov.lanl.micot.infrastructure.project.OutputConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Generates a damage model proportional to a fixed factor
 * 
 * @author Russell Bent
 * 
 */
public class GenerateUniformDamageScenarios {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    double damagePercentsPerMile[] = new double[] { 0.0, .01, .02, .03, .04, .05,
        .1, .15, .2, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7, .75, .8, .85,
        .9, .95, 1.0, 1000, 1000000000 };
        
    double damagePercentPerMileHarden = 0.0;

    for (int ii = 0; ii < damagePercentsPerMile.length; ++ii) {
      double damagePercentPerMile = damagePercentsPerMile[ii];

      String inputFile = "application_data" + File.separatorChar + "rdt" + File.separatorChar
          + "data" + File.separatorChar + "123_Network" + File.separatorChar
          + "RDTMaster.DSS";

      String directory = System.getProperty("user.dir") + File.separatorChar
          + "application_data" + File.separatorChar + "rdt"
          + File.separatorChar + "config" + File.separatorChar + "123_Network"
          + File.separatorChar
          + damagePercentPerMile + " Damage Rate" + File.separatorChar;

      String extension = inputFile.substring(inputFile.lastIndexOf(".") + 1);
      ElectricPowerModelFileFactory modelFileFactory = new ElectricPowerModelFileFactory();
      
      ElectricPowerModelFile modelFile = modelFileFactory.createModelFileFromExtension(extension);
      ElectricPowerModel model = modelFile.readModel(inputFile);

      File file = new File(directory);
      if (!file.exists()) {
        boolean b = file.mkdir();
        if (!b) {
          System.err.println("Failure to make directory: " + directory);
        }
      }

      int scenarios = 100;
      String prefix = "config";

      // damage for normal lines
      String[] damageableLines = { 
          "l115", "l1", "l2", "l3", "l4", "l5", "l6",
          "l7", "l8", "l9", "l10", "l11", "l12", "l13",
          "l14", "l15", "l16", "l17", "l18", "l19", "l20",
          "l21", "l22", "l23", "l24", "l25", "l26", "l27",
          "l28", "l29", "l30", "l31", "l32", "l33", "l34",
          "l35", "l36", "l37", "l38", "l39", "l40", "l41",
          "l42", "l44", "l45", "l46", "l47", "l48", "l49",
          "l50", "l51", "l52", "l53", "l54", "l55", "l56",
          "l57", "l58", "l59", "l60", 
          "l66", "l67", "l68", "l69", "l70",
          "l71", "l72", "l73", "l74", "l75", "l76", "l77",
          "l78", "l79", "l80", "l81", "l82", "l83", "l84",
          "l85", "l86", "l87", "l88", "l89", "l90", "l91",
          "l92", "l93", "l94", "l95", "l96", "l97", "l98",
          "l99", "l118", "l100", "l101", "l102", "l103", "l104",
          "l105", "l106", "l107", "l108", "l109", "l110", "l111",
          "l112", "l113", "l114", "l116", "l117"
          };


      
      
      // damage hardened lines
      String[] damageableLinesHarden = { "l115", "l1", "l2", "l3", "l4", "l5", "l6",
          "l7", "l8", "l9", "l10", "l11", "l12", "l13",
          "l14", "l15", "l16", "l17", "l18", "l19", "l20",
          "l21", "l22", "l23", "l24", "l25", "l26", "l27",
          "l28", "l29", "l30", "l31", "l32", "l33", "l34",
          "l35", "l36", "l37", "l38", "l39", "l40", "l41",
          "l42", "l44", "l45", "l46", "l47", "l48", "l49",
          "l50", "l51", "l52", "l53", "l54", "l55", "l56",
          "l57", "l58", "l59", "l60", 
          "l66", "l67", "l68", "l69", "l70",
          "l71", "l72", "l73", "l74", "l75", "l76", "l77",
          "l78", "l79", "l80", "l81", "l82", "l83", "l84",
          "l85", "l86", "l87", "l88", "l89", "l90", "l91",
          "l92", "l93", "l94", "l95", "l96", "l97", "l98",
          "l99", "l118", "l100", "l101", "l102", "l103", "l104",
          "l105", "l106", "l107", "l108", "l109", "l110", "l111",
          "l112", "l113", "l114", "l116", "l117" };

      // new line costs
      String[] newLines = { "n1", "n2", "n3_3", "n4_3", "n5_1", "n6", "n7", "n8", "n9"}; 
      
      double[] newLineCosts = new double[newLines.length];

      for (int i = 0; i < newLines.length; ++i) {
        for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
          if (edge.toString().equals(newLines[i])) {
            double length = edge.getAttribute(ElectricPowerFlowConnection.LENGTH_KEY, Double.class);
            double miles = (length * 1000.0) / 5280.0;
            if (newLines[i].endsWith("_1") || newLines[i].endsWith("_2") || newLines[i].endsWith("_3")) {
              newLineCosts[i] = miles * 100;
            } 
            else {
              newLineCosts[i] = miles * 500;
            }
          }
        }
      }

      // hardening costs
      double[] hardenLineCosts = new double[damageableLinesHarden.length];
      for (int i = 0; i < damageableLinesHarden.length; ++i) {
        for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
          if (edge.toString().equals(damageableLinesHarden[i])) {
            double length = edge.getAttribute(ElectricPowerFlowConnection.LENGTH_KEY, Double.class);
            double miles = (length * 1000.0) / 5280.0;
            if (edge.getAttribute(Line.NUMBER_OF_PHASES_KEY, Integer.class) == 1) {
              hardenLineCosts[i] = (miles * 100.0) * .2;
            } 
            else {
              hardenLineCosts[i] = (miles * 500.0) * .2;
            }
          }
        }
      }

      String[] existingSwitches = { "sw1", "sw2", "sw3", "sw4", "sw5", "sw6", "sw7", "sw8" };
      
      String[] newSwitches = { 
          "l115", "l1", "l2", "l3", "l4", "l5", "l6",
          "l7", "l8", "l9", "l10", "l11", "l12", "l13",
          "l14", "l15", "l16", "l17", "l18", "l19", "l20",
          "l21", "l22", "l23", "l24", "l25", "l26", "l27",
          "l28", "l29", "l30", "l31", "l32", "l33", "l34",
          "l35", "l36", "l37", "l38", "l39", "l40", "l41",
          "l42", "l44", "l45", "l46", "l47", "l48", "l49",
          "l50", "l51", "l52", "l53", "l54", "l55", "l56",
          "l57", "l58", "l59", "l60", "l61", "l62", "l63",
          "l64", "l65", "l66", "l67", "l68", "l69", "l70",
          "l71", "l72", "l73", "l74", "l75", "l76", "l77",
          "l78", "l79", "l80", "l81", "l82", "l83", "l84",
          "l85", "l86", "l87", "l88", "l89", "l90", "l91",
          "l92", "l93", "l94", "l95", "l96", "l97", "l98",
          "l99", "l118", "l100", "l101", "l102", "l103", "l104",
          "l105", "l106", "l107", "l108", "l109", "l110", "l111",
          "l112", "l113", "l114", "l116", "l117" 
          };
      
      double[] newSwitchCosts = new double[newSwitches.length];
      for (int i = 0; i < newSwitchCosts.length; ++i) {
        newSwitchCosts[i] = 15.0;
      }
       
       
      String[] newMicrogrids = { "g250", "g450", "g95", "g610", "g300", "g48", "g76", "g64" }; 
      double[] newMicrogridCosts = { 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5 };
      double[] newMicrogridFixedCosts = { 500, 500, 500, 500, 500, 500, 500, 500, 500 };
      double[] newMicrogridCapacities = { 5, 5, 5, 5, 5, 5, 5, 5, 5 };

      String[] criticalLoads = { "s48", "s49a", "s49b","s49c", "s76a", "s76b", "s76c", "s64b"}; 

      // print out the main data information
      ProjectConfiguration master = new ProjectConfiguration();
      ModelConfiguration modelConfiguration = new ModelConfiguration();
      AlgorithmConfiguration algorithmConfiguration = new AlgorithmConfiguration();
      OutputConfiguration output = new OutputConfiguration();
      ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration();
      applicationConfiguration.setApplicationFactoryClass(RDDTApplicationFactory.class.getCanonicalName());
      
      master.addModelConfiguration(modelConfiguration);
      master.addAlgorithmConfiguration(algorithmConfiguration);
      master.setOutputConfiguration(output);
      master.setApplicationConfiguration(applicationConfiguration);
      
      modelConfiguration.setModelFile(inputFile);
      modelConfiguration.setModelFileFactoryClass(ElectricPowerModelFileFactory.class.getCanonicalName());
      algorithmConfiguration.setAlgorithmFactoryClass(ResilienceExpansionFactory.class.getCanonicalName());
      algorithmConfiguration.addAlgorithmFlag(AlgorithmConstants.LOAD_MET_KEY, 0.5);
      algorithmConfiguration.addAlgorithmFlag(AlgorithmConstants.CRITICAL_LOAD_MET_KEY, 0.98);
      algorithmConfiguration.addAlgorithmFlag(AlgorithmConstants.PHASE_VARIATION_KEY, 0.15);
      
      // Modifications associated with hardening costs (under ground)
      for (int j = 0; j < damageableLinesHarden.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Line.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, damageableLinesHarden[j]);        
        modification.addAttribute(AlgorithmConstants.LINE_HARDEN_COST_KEY,  hardenLineCosts[j]);
        modelConfiguration.addComponentModification(modification);
      }
            
      // Modifications associated with line construction costs (under ground)
      for (int j = 0; j < newLines.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Line.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newLines[j]);        
        modification.addAttribute(AlgorithmConstants.LINE_CONSTRUCTION_COST_KEY,  newLineCosts[j] );
        modelConfiguration.addComponentModification(modification);

        AssetModification modification2 = new AssetModification();
        modification2.setComponentClass(Line.class.getCanonicalName());
        modification2.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newLines[j]);        
        modification2.addAttribute(AlgorithmConstants.IS_NEW_LINE_KEY,  true );
        modelConfiguration.addComponentModification(modification2);
      }
            
      // new switches
      for (int j = 0; j < newSwitches.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Line.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newSwitches[j]);        
        modification.addAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY, newSwitchCosts[j]);
        modelConfiguration.addComponentModification(modification);
      }
      
      // existing switches
      for (int j = 0; j < existingSwitches.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Line.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, existingSwitches[j]);        
        modification.addAttribute(AlgorithmConstants.HAS_SWITCH_KEY, true);
        modelConfiguration.addComponentModification(modification);
      }
      
      // micro grid costs
      for (int j = 0; j < newMicrogrids.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Generator.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newMicrogrids[j]);        
        modification.addAttribute(AlgorithmConstants.MICROGRID_COST_KEY, newMicrogridCosts[j]);
        modelConfiguration.addComponentModification(modification);
        
        AssetModification modification2 = new AssetModification();
        modification2.setComponentClass(Line.class.getCanonicalName());
        modification2.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newLines[j]);        
        modification2.addAttribute(AlgorithmConstants.IS_NEW_MICROGRID_KEY,  true);
        modelConfiguration.addComponentModification(modification2);

      }
      
      // micro grid fixed costs
      for (int j = 0; j < newMicrogrids.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Generator.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newMicrogrids[j]);        
        modification.addAttribute(AlgorithmConstants.MICROGRID_FIXED_COST_KEY, newMicrogridFixedCosts[j]);
        modelConfiguration.addComponentModification(modification);
      }
      
      // critical load
      for (int j = 0; j < criticalLoads.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Load.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, criticalLoads[j]);        
        modification.addAttribute(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, true);
        modelConfiguration.addComponentModification(modification);
      }
      
      // maximum microgrid expansion
      for (int j = 0; j < newMicrogrids.length; ++j) {
        AssetModification modification = new AssetModification();
        modification.setComponentClass(Generator.class.getCanonicalName());
        modification.addKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, newMicrogrids[j]);        
        modification.addAttribute(AlgorithmConstants.MAX_MICROGRID_KEY, newMicrogridCapacities[j]);
        modelConfiguration.addComponentModification(modification);
      }
      
      // create the scenarios
      ArrayList<ScenarioConfiguration> configurations = new ArrayList<ScenarioConfiguration>();
      for (int i = 1; i <= scenarios; ++i) {
        ScenarioConfiguration config = new ScenarioConfiguration(i,1.0 / (double)scenarios);  
        config.setScenarioBuilderFactoryClass(UniformEdgeDamageScenarioConfigurationBuilder.class.getCanonicalName());
        master.addScenarioConfiguration(config);
        configurations.add(config);
      }
      
      // create the normal line damage for the scenarios
      ArrayList<Asset> damageAssets = new ArrayList<Asset>();
      for (int j = 0; j < damageableLines.length; ++j) {
        for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
          if (edge.toString().equals(damageableLines[j])) {
            damageAssets.add(edge);
          }
        }
      }        
      ScenarioConfigurationBuilder builder = new UniformEdgeDamageScenarioConfigurationBuilder(damagePercentPerMile, Asset.IS_FAILED_KEY);
      builder.updateScenarios(configurations, damageAssets, OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);
      
      // create the harden line damage for the scenarios
      ArrayList<Asset> damageHardenAssets = new ArrayList<Asset>();
      for (int j = 0; j < damageableLinesHarden.length; ++j) {
        for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
          if (edge.toString().equals(damageableLinesHarden[j])) {
            damageHardenAssets.add(edge);
          }
        }
      }        
      builder = new UniformEdgeDamageScenarioConfigurationBuilder(damagePercentPerMileHarden, AlgorithmConstants.HARDENED_DISABLED_KEY);
      builder.updateScenarios(configurations, damageHardenAssets, OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);
            
      JsonProjectConfigurationWriter writer = new JsonProjectConfigurationWriter();
      writer.writeMultiScenarioConfiguration(master, directory + prefix + ".json");
    }
    
  }

}
