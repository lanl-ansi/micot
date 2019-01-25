package gov.lanl.micot.application.lpnorm.exec;

import gov.lanl.micot.infrastructure.application.Application;
import gov.lanl.micot.infrastructure.application.ApplicationOutput;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.io.geojson.GeoJSONModelExporter;
import gov.lanl.micot.infrastructure.io.shapefile.ShapefileModelExporter;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;
import gov.lanl.micot.application.lpnorm.LPNormApplicationFactory;
import gov.lanl.micot.application.lpnorm.io.LPNormJsonProjectConfigurationReader;
import gov.lanl.micot.application.lpnorm.model.LPNormModelConstants;
import gov.lanl.micot.application.rdt.JsonResultExporter;
import gov.lanl.micot.application.rdt.RDTApplication;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;
import gov.lanl.micot.util.io.geometry.geojson.GeoJSONWriter;
import gov.lanl.micot.util.io.geometry.geojson.GeoJSONWriterFactory;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileWriter;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileWriterFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Code for running the RDT for an LPNorm application
 * 
 * @author Russell Bent
 */
public class RunLPNorm  {
   
  /**
   * @param args
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   */
  public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    LPNormCommandLineParser parser = new LPNormCommandLineParser(args);
    boolean valid = parser.checkOptions();
    if (!valid) {
      System.exit(-1);
    }
        
    String filename = parser.getRDTInputFile();
    String exportFile = parser.getOutputFile();
    
    long start = System.currentTimeMillis();

    System.out.println("Using configuration file: " + filename);
    LPNormJsonProjectConfigurationReader reader = new LPNormJsonProjectConfigurationReader();
    ProjectConfiguration configuration = reader.readConfiguration(filename);
    
    LPNormApplicationFactory factory = new LPNormApplicationFactory();
    Application application = factory.createApplication(configuration);
    ApplicationOutput output = application.execute();
    
    // export the results
    JsonResultExporter exporter = new JsonResultExporter();
    exporter.exportResults(output, configuration, exportFile);
    
    // add scenario damage fields
    ElectricPowerModel model = output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class);
    Collection<ScenarioConfiguration> scenarios = configuration.getScenarioConfigurations();
    for (ScenarioConfiguration config : scenarios) {
      Scenario scenario = config.getScenario();
      int idx = scenario.getIndex();
      for (ElectricPowerFlowConnection c : model.getFlowConnections()) {
        Boolean isDamaged = scenario.getModification(c, Asset.IS_FAILED_KEY, Boolean.class);
        if (isDamaged == null) {
          isDamaged = false;
        }
        String key = "sc " + idx + " damaged";        
        c.setAttribute(key, isDamaged);        
      }
    }

    long end = System.currentTimeMillis();
    System.out.println("Total execution time: " + ((end - start) / 1000.0) + " seconds");
    
    // export optional results
    if (parser.getBusShpFile() != null) {
      exportBusShp(parser.getBusShpFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getGenShpFile() != null) {
      exportGenShp(parser.getGenShpFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getLoadShpFile() != null) {
      exportLoadShp(parser.getLoadShpFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getBranchShpFile() != null) {
      exportBranchShp(parser.getBranchShpFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getBusJSONFile() != null) {
      exportBusJSON(parser.getBusJSONFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getGenJSONFile() != null) {
      exportGenJSON(parser.getGenJSONFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getLoadJSONFile() != null) {
      exportLoadJSON(parser.getLoadJSONFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
    
    if (parser.getBranchJSONFile() != null) {
      exportBranchJSON(parser.getBranchJSONFile(), output.get(RDTApplication.MODEL_FLAG, ElectricPowerModel.class));
    }
        
  }
  
  /**
   * Export the buses as a shapefile
   * @param filename
   */
  private static void exportBusShp(String filename, ElectricPowerModel model) {
    ShapefileModelExporter export = new ShapefileModelExporter();
    ShapefileWriter writer = ShapefileWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(Bus.MAXIMUM_VOLTAGE_PU_KEY, "maxV");
    export.changeFieldName(Bus.MINIMUM_VOLTAGE_PU_KEY, "minV");
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(Bus.VOLTAGE_PU_A_KEY, "Va");
    export.changeFieldName(Bus.VOLTAGE_PU_B_KEY, "Vb");
    export.changeFieldName(Bus.VOLTAGE_PU_C_KEY, "Vc");
    export.changeFieldName(Bus.STATUS_KEY, "status");
    export.changeFieldName(Bus.ASSET_ID_KEY, "int id");
    
    export.exportAssets(model, writer, Bus.class, filename);
  }
  
  
  /**
   * Export the buses as a geo json file
   * @param filename
   */
  private static void exportBusJSON(String filename, ElectricPowerModel model) {
    GeoJSONModelExporter export = new GeoJSONModelExporter();
    GeoJSONWriter writer = GeoJSONWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(Bus.MAXIMUM_VOLTAGE_PU_KEY, "max voltage");
    export.changeFieldName(Bus.MINIMUM_VOLTAGE_PU_KEY, "min voltage");
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(Bus.VOLTAGE_PU_A_KEY, "voltage phase a");
    export.changeFieldName(Bus.VOLTAGE_PU_B_KEY, "voltage phase b");
    export.changeFieldName(Bus.VOLTAGE_PU_C_KEY, "voltage phase c");
    export.changeFieldName(Bus.STATUS_KEY, "status");
    export.changeFieldName(Bus.ASSET_ID_KEY, "int id");
    
    export.exportAssets(model, writer, Bus.class, filename);
  }
    
  /**
   * Export the generators as a shapefile
   * @param filename
   */
  private static void exportGenShp(String filename, ElectricPowerModel model) {
    ShapefileModelExporter export = new ShapefileModelExporter();
    ShapefileWriter writer = ShapefileWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(Generator.STATUS_KEY, "status");
    export.changeFieldName(Generator.ASSET_ID_KEY, "int id");
    export.changeFieldName(AlgorithmConstants.GENERATOR_CONSTRUCTION_COST_KEY, "cost");
    export.changeFieldName(Generator.MVA_BASE_KEY, "mva base");
    export.changeFieldName(Generator.REACTIVE_GENERATION_A_KEY, "mvar a");
    export.changeFieldName(Generator.REACTIVE_GENERATION_B_KEY, "mvar b");
    export.changeFieldName(Generator.REACTIVE_GENERATION_C_KEY, "mvar c");
    export.changeFieldName(Generator.REAL_GENERATION_A_KEY, "mw a");
    export.changeFieldName(Generator.REAL_GENERATION_B_KEY, "mw b");
    export.changeFieldName(Generator.REAL_GENERATION_C_KEY, "mw c");
    
    export.changeFieldName(Generator.REAL_GENERATION_KEY, "mw");
    export.changeFieldName(Generator.REACTIVE_GENERATION_KEY, "mvar");
    
    export.changeFieldName(Generator.HAS_PHASE_A_KEY, "has a");
    export.changeFieldName(Generator.HAS_PHASE_B_KEY, "has b");
    export.changeFieldName(Generator.HAS_PHASE_C_KEY, "has c");
    export.changeFieldName(Generator.TYPE_KEY, "type");
    export.changeFieldName(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, "built a");
    export.changeFieldName(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, "built b");
    export.changeFieldName(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, "built c");
    export.changeFieldName(AlgorithmConstants.IS_NEW_MICROGRID_KEY, "is new");
    export.changeFieldName(AlgorithmConstants.IS_CONSTRUCTED_KEY, "built");
  //  export.changeFieldName(AlgorithmConstants.MAX_MICROGRID_KEY, "max cap");
    export.changeFieldName(AlgorithmConstants.MICROGRID_COST_KEY, "cost");
//    export.changeFieldName(AlgorithmConstants.MICROGRID_COST_KEY, "mw cost");
    
    export.changeFieldName(Generator.REACTIVE_GENERATION_MAX_KEY, "mvar max");
    export.changeFieldName(Generator.REACTIVE_GENERATION_MIN_KEY, "mvar min");
    export.changeFieldName(Generator.REAL_GENERATION_MAX_KEY, "mw max");
    export.changeFieldName(Generator.REAL_GENERATION_MIN_KEY, "mw min");

    export.exportAssets(model, writer, Generator.class, filename);
  }
  
  /**
   * Export the generators as a geojson file
   * @param filename
   */
  private static void exportGenJSON(String filename, ElectricPowerModel model) {
    GeoJSONModelExporter export = new GeoJSONModelExporter();
    GeoJSONWriter writer = GeoJSONWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(Generator.STATUS_KEY, "status");
    export.changeFieldName(Generator.ASSET_ID_KEY, "int id");
    export.changeFieldName(AlgorithmConstants.GENERATOR_CONSTRUCTION_COST_KEY, "cost");
    export.changeFieldName(Generator.MVA_BASE_KEY, "mva base");
    export.changeFieldName(Generator.REACTIVE_GENERATION_A_KEY, "mvar phase a");
    export.changeFieldName(Generator.REACTIVE_GENERATION_B_KEY, "mvar phase b");
    export.changeFieldName(Generator.REACTIVE_GENERATION_C_KEY, "mvar phase c");
    export.changeFieldName(Generator.REAL_GENERATION_A_KEY, "mw phase a");
    export.changeFieldName(Generator.REAL_GENERATION_B_KEY, "mw phase b");
    export.changeFieldName(Generator.REAL_GENERATION_C_KEY, "mw phase c");
    
    export.changeFieldName(Generator.REAL_GENERATION_KEY, "mw");
    export.changeFieldName(Generator.REACTIVE_GENERATION_KEY, "mvar");
    
    export.changeFieldName(Generator.HAS_PHASE_A_KEY, "has phase a");
    export.changeFieldName(Generator.HAS_PHASE_B_KEY, "has phase b");
    export.changeFieldName(Generator.HAS_PHASE_C_KEY, "has phase c");
    export.changeFieldName(Generator.TYPE_KEY, "type");
    export.changeFieldName(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_A_KEY, "built capacity phase a");
    export.changeFieldName(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_B_KEY, "built capacity phase b");
    export.changeFieldName(AlgorithmConstants.MICROGRID_CAPACITY_PHASE_C_KEY, "built capacity phase c");
    export.changeFieldName(AlgorithmConstants.IS_NEW_MICROGRID_KEY, "is new");
    export.changeFieldName(AlgorithmConstants.IS_CONSTRUCTED_KEY, "built");
    //export.changeFieldName(AlgorithmConstants.MAX_MICROGRID_KEY, "max capacity");
    //export.changeFieldName(AlgorithmConstants.MICROGRID_FIXED_COST_KEY, "cost");
    export.changeFieldName(AlgorithmConstants.MICROGRID_COST_KEY, "cost");
    
    export.changeFieldName(Generator.REACTIVE_GENERATION_MAX_KEY, "mvar max");
    export.changeFieldName(Generator.REACTIVE_GENERATION_MIN_KEY, "mvar min");
    export.changeFieldName(Generator.REAL_GENERATION_MAX_KEY, "mw max");
    export.changeFieldName(Generator.REAL_GENERATION_MIN_KEY, "mw min");

    export.exportAssets(model, writer, Generator.class, filename);
  }
  
  /**
   * Export the loads as a shapefile
   * @param filename
   */
  private static void exportLoadShp(String filename, ElectricPowerModel model) {
    ShapefileModelExporter export = new ShapefileModelExporter();
    ShapefileWriter writer = ShapefileWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(Load.STATUS_KEY, "status");
    export.changeFieldName(Load.ASSET_ID_KEY, "int id");
    export.changeFieldName(Load.REACTIVE_LOAD_A_KEY, "mvar a");
    export.changeFieldName(Load.REACTIVE_LOAD_B_KEY, "mvar b");
    export.changeFieldName(Load.REACTIVE_LOAD_C_KEY, "mvar c");
    export.changeFieldName(Load.REAL_LOAD_A_KEY, "mw a");
    export.changeFieldName(Load.REAL_LOAD_B_KEY, "mw b");
    export.changeFieldName(Load.REAL_LOAD_C_KEY, "mw c");
    export.changeFieldName(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, "is critical");
    export.changeFieldName(Load.REAL_LOAD_KEY, "mw");
    export.changeFieldName(Load.REACTIVE_LOAD_KEY, "mvar");
    export.changeFieldName(Load.REACTIVE_LOAD_A_MAX_KEY, "mvarmaxa");
    export.changeFieldName(Load.REACTIVE_LOAD_B_MAX_KEY, "mvarmaxb");
    export.changeFieldName(Load.REACTIVE_LOAD_C_MAX_KEY, "mvarmaxc");    
    export.changeFieldName(Load.REAL_LOAD_A_MAX_KEY, "mwmaxa");
    export.changeFieldName(Load.REAL_LOAD_B_MAX_KEY, "mwmaxb");
    export.changeFieldName(Load.REAL_LOAD_C_MAX_KEY, "mwmaxc");    

    export.changeFieldName(Load.REACTIVE_LOAD_A_MIN_KEY, "mvarmina");
    export.changeFieldName(Load.REACTIVE_LOAD_B_MIN_KEY, "mvarminb");
    export.changeFieldName(Load.REACTIVE_LOAD_C_MIN_KEY, "mvarminc");    
    export.changeFieldName(Load.REAL_LOAD_A_MIN_KEY, "mwmina");
    export.changeFieldName(Load.REAL_LOAD_B_MIN_KEY, "mwminb");
    export.changeFieldName(Load.REAL_LOAD_C_MIN_KEY, "mwminc");    
    
    export.changeFieldName(Load.REACTIVE_LOAD_KEY, "mvar");
    export.changeFieldName(Load.REAL_LOAD_KEY, "mw");

    export.changeFieldName(Load.REACTIVE_LOAD_MIN_KEY, "mvar min");
    export.changeFieldName(Load.REAL_LOAD_MIN_KEY, "mw min");

    export.changeFieldName(Load.REACTIVE_LOAD_MAX_KEY, "mvar max");
    export.changeFieldName(Load.REAL_LOAD_MAX_KEY, "mw max");
    
    export.changeFieldName(Load.HAS_PHASE_A_KEY, "has a");
    export.changeFieldName(Load.HAS_PHASE_B_KEY, "has b");
    export.changeFieldName(Load.HAS_PHASE_C_KEY, "has c");

    export.changeFieldName(Load.NUM_PHASE_KEY, "phases");

    
    export.exportAssets(model, writer, Load.class, filename);
  }
  
  /**
   * Export the loads as a geo json file
   * @param filename
   */
  private static void exportLoadJSON(String filename, ElectricPowerModel model) {
    GeoJSONModelExporter export = new GeoJSONModelExporter();
    GeoJSONWriter writer = GeoJSONWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(Load.STATUS_KEY, "status");
    export.changeFieldName(Load.ASSET_ID_KEY, "int id");
    export.changeFieldName(Load.REACTIVE_LOAD_A_KEY, "mvar phase a");
    export.changeFieldName(Load.REACTIVE_LOAD_B_KEY, "mvar phase b");
    export.changeFieldName(Load.REACTIVE_LOAD_C_KEY, "mvar phase c");
    export.changeFieldName(Load.REAL_LOAD_A_KEY, "mw phase a");
    export.changeFieldName(Load.REAL_LOAD_B_KEY, "mw phase b");
    export.changeFieldName(Load.REAL_LOAD_C_KEY, "mw phase c");
    export.changeFieldName(AlgorithmConstants.IS_CRITICAL_LOAD_KEY, "is critical");
    export.changeFieldName(Load.REAL_LOAD_KEY, "mw");
    export.changeFieldName(Load.REACTIVE_LOAD_KEY, "mvar");
    export.changeFieldName(Load.REACTIVE_LOAD_A_MAX_KEY, "max mvar phase a");
    export.changeFieldName(Load.REACTIVE_LOAD_B_MAX_KEY, "max mvar phase b");
    export.changeFieldName(Load.REACTIVE_LOAD_C_MAX_KEY, "max mvar phase c");    
    export.changeFieldName(Load.REAL_LOAD_A_MAX_KEY, "max mw phase a");
    export.changeFieldName(Load.REAL_LOAD_B_MAX_KEY, "max mw phase b");
    export.changeFieldName(Load.REAL_LOAD_C_MAX_KEY, "max mw phase c");    

    export.changeFieldName(Load.REACTIVE_LOAD_A_MIN_KEY, "min mvar phase a");
    export.changeFieldName(Load.REACTIVE_LOAD_B_MIN_KEY, "min mvar phase b");
    export.changeFieldName(Load.REACTIVE_LOAD_C_MIN_KEY, "min mvar phase c");    
    export.changeFieldName(Load.REAL_LOAD_A_MIN_KEY, "min mw phase a");
    export.changeFieldName(Load.REAL_LOAD_B_MIN_KEY, "min mw phase b");
    export.changeFieldName(Load.REAL_LOAD_C_MIN_KEY, "min mw phase c");    
    
    export.changeFieldName(Load.REACTIVE_LOAD_KEY, "mvar");
    export.changeFieldName(Load.REAL_LOAD_KEY, "mw");

    export.changeFieldName(Load.REACTIVE_LOAD_MIN_KEY, "mvar min");
    export.changeFieldName(Load.REAL_LOAD_MIN_KEY, "mw min");

    export.changeFieldName(Load.REACTIVE_LOAD_MAX_KEY, "mvar max");
    export.changeFieldName(Load.REAL_LOAD_MAX_KEY, "mw max");
    
    export.changeFieldName(Load.HAS_PHASE_A_KEY, "has phase a");
    export.changeFieldName(Load.HAS_PHASE_B_KEY, "has phase b");
    export.changeFieldName(Load.HAS_PHASE_C_KEY, "has phase c");

    export.changeFieldName(Load.NUM_PHASE_KEY, "phases");
    
    export.exportAssets(model, writer, Load.class, filename);
  }

  
  
  /**
   * Export the branches as a shapefile
   * @param filename
   */
  private static void exportBranchShp(String filename, ElectricPowerModel model) {
    ShapefileModelExporter export = new ShapefileModelExporter();
    ShapefileWriter writer = ShapefileWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(ElectricPowerFlowConnection.STATUS_KEY, "status");
    export.changeFieldName(ElectricPowerFlowConnection.ASSET_ID_KEY, "int id");

    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, "x a");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, "x b");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, "x c");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, "x ab");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, "x ac");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, "x ba");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, "x bc");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, "x ca");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, "x cb");

    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, "r a");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, "r b");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, "r c");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, "r ab");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, "r ac");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, "r ba");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, "r bc");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, "r ca");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, "r cb");

    export.changeFieldName(AlgorithmConstants.CAN_ADD_SWITCH_KEY, "can add sw");
    export.changeFieldName(AlgorithmConstants.CAN_HARDEN_KEY, "can harden");    
    export.changeFieldName(AlgorithmConstants.IS_NEW_LINE_KEY, "is new");
    export.changeFieldName(AlgorithmConstants.IS_CONSTRUCTED_KEY, "built");
    export.changeFieldName(AlgorithmConstants.IS_HARDENED_KEY, "hardened");
    export.changeFieldName(AlgorithmConstants.HAS_SWITCH_KEY, "has switch");
    export.changeFieldName(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, "built switch");
    export.changeFieldName(AlgorithmConstants.LINE_CONSTRUCTION_COST_KEY, "cost");
    export.changeFieldName(AlgorithmConstants.LINE_HARDEN_COST_KEY, "harden cost");
    export.changeFieldName(AlgorithmConstants.LINE_SWITCH_COST_KEY, "switch cost");
    export.changeFieldName(AlgorithmConstants.IS_SWITCH_OPEN_KEY, "open switch");

    export.changeFieldName(AlgorithmConstants.IS_USED_KEY, "is_used");
    
    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, "rate a");
    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, "rate b");
    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, "rate c");
    export.changeFieldName(ElectricPowerFlowConnection.LENGTH_KEY, "length");
    export.changeFieldName(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, "has a");
    export.changeFieldName(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, "has b");
    export.changeFieldName(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, "has c");

    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_KEY, "rate");
    export.changeFieldName(ElectricPowerFlowConnection.LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY, "rate long");
    export.changeFieldName(ElectricPowerFlowConnection.SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY, "rate short");

    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_KEY, "mvar");
    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_KEY, "mw");

    export.changeFieldName(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, "phases");

    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, "mvar a");
    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, "mvar b");
    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, "mvar c");

    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, "mw a");
    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, "mw b");
    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, "mw c");

    
    export.exportAssets(model, writer, ElectricPowerFlowConnection.class, filename);
  }
  
  
  /**
   * Export the branches as a geojson file
   * @param filename
   */
  private static void exportBranchJSON(String filename, ElectricPowerModel model) {
    GeoJSONModelExporter export = new GeoJSONModelExporter();
    GeoJSONWriter writer = GeoJSONWriterFactory.getInstance().getDefaultWriter();
    
    export.changeFieldName(LPNormModelConstants.LPNORM_LEGACY_ID_KEY, "lpnorm id");
    export.changeFieldName(ElectricPowerFlowConnection.STATUS_KEY, "status");
    export.changeFieldName(ElectricPowerFlowConnection.ASSET_ID_KEY, "int id");

    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_A_KEY, "x phase a");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_B_KEY, "x phase b");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_C_KEY, "x phase c");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_AB_KEY, "x phase ab");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_AC_KEY, "x phase ac");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_BA_KEY, "x phase ba");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_BC_KEY, "x phase bc");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_CA_KEY, "x phase ca");
    export.changeFieldName(ElectricPowerFlowConnection.REACTANCE_PHASE_CB_KEY, "x phase cb");

    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_A_KEY, "r phase a");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_B_KEY, "r phase b");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_C_KEY, "r phase c");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_AB_KEY, "r phase ab");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_AC_KEY, "r phase ac");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_BA_KEY, "r phase ba");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_BC_KEY, "r phase bc");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_CA_KEY, "r phase ca");
    export.changeFieldName(ElectricPowerFlowConnection.RESISTANCE_PHASE_CB_KEY, "r phase cb");

    export.changeFieldName(AlgorithmConstants.CAN_ADD_SWITCH_KEY, "can add switch");
    export.changeFieldName(AlgorithmConstants.CAN_HARDEN_KEY, "can harden");    
    export.changeFieldName(AlgorithmConstants.IS_NEW_LINE_KEY, "is new");
    export.changeFieldName(AlgorithmConstants.IS_CONSTRUCTED_KEY, "built");
    export.changeFieldName(AlgorithmConstants.IS_HARDENED_KEY, "hardened");
    export.changeFieldName(AlgorithmConstants.HAS_SWITCH_KEY, "has switch");
    export.changeFieldName(AlgorithmConstants.IS_SWITCH_CONSTRUCTED_KEY, "built switch");
    export.changeFieldName(AlgorithmConstants.LINE_CONSTRUCTION_COST_KEY, "cost");
    export.changeFieldName(AlgorithmConstants.LINE_HARDEN_COST_KEY, "harden cost");
    export.changeFieldName(AlgorithmConstants.LINE_SWITCH_COST_KEY, "switch cost");
    export.changeFieldName(AlgorithmConstants.IS_SWITCH_OPEN_KEY, "open switch");

    export.changeFieldName(AlgorithmConstants.IS_USED_KEY, "is_used");
    
    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_A_KEY, "rate a");
    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_B_KEY, "rate b");
    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_C_KEY, "rate c");
    export.changeFieldName(ElectricPowerFlowConnection.LENGTH_KEY, "length");
    export.changeFieldName(ElectricPowerFlowConnection.HAS_PHASE_A_KEY, "has phase a");
    export.changeFieldName(ElectricPowerFlowConnection.HAS_PHASE_B_KEY, "has phase b");
    export.changeFieldName(ElectricPowerFlowConnection.HAS_PHASE_C_KEY, "has phase c");

    export.changeFieldName(ElectricPowerFlowConnection.CAPACITY_RATING_KEY, "rate");
    export.changeFieldName(ElectricPowerFlowConnection.LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY, "rate long");
    export.changeFieldName(ElectricPowerFlowConnection.SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY, "rate short");

    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_KEY, "mvar");
    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_KEY, "mw");

    export.changeFieldName(ElectricPowerFlowConnection.NUMBER_OF_PHASES_KEY, "phases");

    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_A_KEY, "mvar phase a");
    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_B_KEY, "mvar phase b");
    export.changeFieldName(ElectricPowerFlowConnection.MVAR_FLOW_PHASE_C_KEY, "mvar phase c");

    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_PHASE_A_KEY, "mw phase a");
    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_PHASE_B_KEY, "mw phase b");
    export.changeFieldName(ElectricPowerFlowConnection.MW_FLOW_PHASE_C_KEY, "mw phase c");
    
    export.exportAssets(model, writer, ElectricPowerFlowConnection.class, filename);
  }
  
  
}
