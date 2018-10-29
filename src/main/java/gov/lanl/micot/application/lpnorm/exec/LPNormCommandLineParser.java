package gov.lanl.micot.application.lpnorm.exec;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line parser for LPNorm
 * @author Russell Bent
 */
public class LPNormCommandLineParser {

  private static Options options = null;

  private String rdtInputFile = null;
  private String outputFile = null;
  
  private String busShpFile = null;
  private String genShpFile = null;
  private String loadShpFile = null;
  private String branchShpFile = null;
  
  private String busJSONFile = null;
  private String genJSONFile = null;
  private String loadJSONFile = null;
  private String branchJSONFile = null;

  private static final String INPUT_FLAG = "c";
  private static final String OUTPUT_FLAG = "e";
  private static final String HELP_FLAG = "h";
  
  private static final String BUS_SHP_FLAG = "bs";
  private static final String GEN_SHP_FLAG = "gs";
  private static final String LOAD_SHP_FLAG = "ls";
  private static final String BRANCH_SHP_FLAG = "brs";

  private static final String BUS_JSON_FLAG = "bj";
  private static final String GEN_JSON_FLAG = "gj";
  private static final String LOAD_JSON_FLAG = "lj";
  private static final String BRANCH_JSON_FLAG = "brj";
  
  private static final String INPUT_LONG_FLAG = "config";
  private static final String OUTPUT_LONG_FLAG = "export";
  private static final String HELP_LONG_FLAG = "help";

  private static final String BUS_SHP_LONG_FLAG = "bus_shapefile";
  private static final String GEN_SHP_LONG_FLAG = "generator_shapefile";
  private static final String LOAD_SHP_LONG_FLAG = "load_shapefile";
  private static final String BRANCH_SHP_LONG_FLAG = "branch_shapefile";
  
  private static final String BUS_JSON_LONG_FLAG = "bus_geojsonfile";
  private static final String GEN_JSON_LONG_FLAG = "generator_geojsonfile";
  private static final String LOAD_JSON_LONG_FLAG = "load_geojsonfile";
  private static final String BRANCH_JSON_LONG_FLAG = "branch_geojsonfile";

  /**
   * Constructor
   * 
   * @param args
   */
  public LPNormCommandLineParser(String[] args) {
    options = defineOptions();
    parse(args);
  }

  /**
   * Define all the availability of options
   * 
   * @return
   */
  private Options defineOptions() {
    options = new Options();
    options.addOption(new Option(HELP_FLAG, HELP_LONG_FLAG, false, "print lpnorm rdt help"));
    options.addOption(new Option(INPUT_FLAG, INPUT_LONG_FLAG, true, "RDTJson input file"));
    options.addOption(new Option(OUTPUT_FLAG, OUTPUT_LONG_FLAG, true, "export results file"));
    options.addOption(new Option(BUS_SHP_FLAG, BUS_SHP_LONG_FLAG, true, "[optional] export bus results as shapefile"));
    options.addOption(new Option(GEN_SHP_FLAG, GEN_SHP_LONG_FLAG, true, "[optional] export generator results as shapefile"));
    options.addOption(new Option(LOAD_SHP_FLAG, LOAD_SHP_LONG_FLAG, true, "[optional] export load results as shapefile"));
    options.addOption(new Option(BRANCH_SHP_FLAG, BRANCH_SHP_LONG_FLAG, true, "[optional] export branch results as shapefile"));
    options.addOption(new Option(BUS_JSON_FLAG, BUS_JSON_LONG_FLAG, true, "[optional] export bus results as geojson"));
    options.addOption(new Option(GEN_JSON_FLAG, GEN_JSON_LONG_FLAG, true, "[optional] export generator results as geojson"));
    options.addOption(new Option(LOAD_JSON_FLAG, LOAD_JSON_LONG_FLAG, true, "[optional] export load results as geojson"));
    options.addOption(new Option(BRANCH_JSON_FLAG, BRANCH_JSON_LONG_FLAG, true, "[optional] export branch results as geojson"));

    return options;
  }

  /**
   * Print the usuage of the RDT LPNORM
   */
  public void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    String header = "micot-rdt  [OPTIONS]\n options:\n";
    String footer = "\nExamples:\n\njava -jar micot-rdt.jar -" + INPUT_FLAG + " <RDT_input_file.json> -" + OUTPUT_FLAG + " <RDT_results_export_file.json>\n\n";
    formatter.printHelp("micot-rdt", header, options, footer, true);
  }

  /**
   * Parse the command line
   * 
   * @param args
   */
  private void parse(String[] args) {
    CommandLine commandLine = null;
    // Parse the command line.
    CommandLineParser parser = new DefaultParser();
    try {
      // Parse the options and return the command line object.
      commandLine = parser.parse(options, args);

      // Check to see if only help is requested.
      if (commandLine.hasOption("help")) {
        System.out.println("RDT help:");
        printUsage();
        System.exit(0);
      }

      rdtInputFile = commandLine.getOptionValue(INPUT_LONG_FLAG);
      outputFile = commandLine.getOptionValue(OUTPUT_LONG_FLAG);
      busShpFile = commandLine.getOptionValue(BUS_SHP_LONG_FLAG);
      genShpFile = commandLine.getOptionValue(GEN_SHP_LONG_FLAG);
      loadShpFile = commandLine.getOptionValue(LOAD_SHP_LONG_FLAG);
      branchShpFile = commandLine.getOptionValue(BRANCH_SHP_LONG_FLAG);
      
      busJSONFile = commandLine.getOptionValue(BUS_JSON_LONG_FLAG);
      genJSONFile = commandLine.getOptionValue(GEN_JSON_LONG_FLAG);
      loadJSONFile = commandLine.getOptionValue(LOAD_JSON_LONG_FLAG);
      branchJSONFile = commandLine.getOptionValue(BRANCH_JSON_LONG_FLAG);
    }
    catch (ParseException exp) {
      printUsage();
      System.exit(1);
    }
  }

  /**
   * Check the options of the arguments
   * 
   * @param args
   * @return
   */
  public boolean checkOptions() {
    if (getRDTInputFile() == null) {
      System.out.println("Missing the input option -" + INPUT_FLAG + " ...");
      return false;
    }

    if (getOutputFile() == null) {
      System.out.println("Missing the output option -" + OUTPUT_FLAG + " ...");
      return false;
    }

    return true;
  }

  /**
   * Get all the options
   * 
   * @return
   */
  public Options getOptions() {
    return options;
  }

  /**
   * Get the rdt input file
   * 
   * @return
   */
  public String getRDTInputFile() {
    return rdtInputFile;
  }

  /**
   * Get the output file
   * 
   * @return
   */
  public String getOutputFile() {
    return outputFile;
  }

  /**
   * Get the bus output shape file
   * 
   * @return
   */
  public String getBusShpFile() {
    return busShpFile;
  }
  
  /**
   * Get the load output shape file
   * 
   * @return
   */
  public String getLoadShpFile() {
    return loadShpFile;
  }
  
  /**
   * Get the generator output shape file
   * 
   * @return
   */
  public String getGenShpFile() {
    return genShpFile;
  }
  
  /**
   * Get the branch output shape file
   * 
   * @return
   */
  public String getBranchShpFile() {
    return branchShpFile;
  }
  
  /**
   * Get the bus output geojson file
   * 
   * @return
   */
  public String getBusJSONFile() {
    return busJSONFile;
  }
  
  /**
   * Get the load output geojson file
   * 
   * @return
   */
  public String getLoadJSONFile() {
    return loadJSONFile;
  }
  
  /**
   * Get the generator output geojson file
   * 
   * @return
   */
  public String getGenJSONFile() {
    return genJSONFile;
  }
  
  /**
   * Get the branch output geojson file
   * 
   * @return
   */
  public String getBranchJSONFile() {
    return branchJSONFile;
  }
  
  
}
