package gov.lanl.micot.infrastructure.ep.exec;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.io.ModelFile;
import gov.lanl.micot.infrastructure.io.shapefile.ShapefileModelExporter;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileWriter;
import gov.lanl.micot.util.io.geometry.shapefile.ShapefileWriterFactory;

import java.io.IOException;

/**
 * A quick and dirty main class for exporting models as shapefiles
 * 
 * @author Russell Bent
 *
 */
public class ExportShapefile {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    System.out.println("Beginning Shapefile export");
    String initialFile = "C://Users//210117//Documents//GitHub//lpnormBeta//SVEC PeachGrove GFM Input.json";

    String outputDirectory = "Shapefile Exports";

    ModelFile file = new ElectricPowerModelFileFactory().createModelFile(initialFile);
    Model model = file.readModel(initialFile);

    ShapefileModelExporter export = new ShapefileModelExporter();
    ShapefileWriter writer = ShapefileWriterFactory.getInstance().getDefaultWriter();

    // General Fields
    export.changeFieldName("DEW_COMPONENT_TYPE", "dewType");
    export.changeFieldName("DEW_LEGACY_ID", "DEW_LEGACY"); // leave this be to
                                                           // not break code but
                                                           // don't want
                                                           // warnings
    export.changeFieldName("DEW_LEGACY_IDS", "DEW_LEGACY"); // leave this be to
                                                            // not break code
                                                            // but don't want
                                                            // warnings

    export.changeFieldName(Asset.STATUS_KEY, "status");

    // Bus Fields
    export.changeFieldName("DESIRED_VOLTAGE_PU", "Vdes");
    export.changeFieldName("INITIAL_VOLTAGE_PU", "Vinit");
    export.changeFieldName("SYSTEM_VOLTAGE_KV", "kVbase");

    export.changeFieldName("MAXIMUM_VOLTAGE_PU", "Vmax");
    export.changeFieldName("MINIMUM_VOLTAGE_PU", "Vmin");

    export.changeFieldName("PHASE_ANGLE", "angle");

    // Line Fields
    export.changeFieldName("RESISTANCE_PHASE_A", "Raa");
    export.changeFieldName("RESISTANCE_PHASE_B", "Rbb");
    export.changeFieldName("RESISTANCE_PHASE_C", "Rcc");

    export.changeFieldName("RESISTANCE_AB", "Rab");
    export.changeFieldName("RESISTANCE_BC", "Rbc");
    export.changeFieldName("RESISTANCE_CA", "Rca");

    export.changeFieldName("REACTANCE_PHASE_A", "Xaa");
    export.changeFieldName("REACTANCE_PHASE_B", "Xbb");
    export.changeFieldName("REACTANCE_PHASE_C", "Xcc");

    export.changeFieldName("REACTANCE_AB", "Xab");
    export.changeFieldName("REACTANCE_BC", "Xbc");
    export.changeFieldName("REACTANCE_CA", "Xca");

    export.changeFieldName("LINE_CHARGING", "Xshunt");

    export.changeFieldName("HAS_PHASE_A", "IS_PHASE_A");
    export.changeFieldName("HAS_PHASE_B", "IS_PHASE_B");
    export.changeFieldName("HAS_PHASE_C", "IS_PHASE_C");

    export.changeFieldName("NUMBER_OF_PHASES", "nPhases");
    export.changeFieldName("INSTALLATION_TYPE", "lineType");
    export.changeFieldName("LINE_DESCRIPTION_TYPE", "linedDesc");

    export.changeFieldName("CAPACITY_RATING", "Imax");

    export.changeFieldName("CAPACITY_RATING_A", "ImaxA");
    export.changeFieldName("CAPACITY_RATING_B", "ImaxB");
    export.changeFieldName("CAPACITY_RATING_C", "ImaxC");

    export.changeFieldName("LONG_TERM_EMERGENCY_CAPACITY_RATING", "ImaxLT");
    export.changeFieldName("SHORT_TERM_EMERGENCY_CAPACITY_RATING", "ImaxST");

    // Load Fields
    export.changeFieldName("DESIRED_REAL_LOAD", "P");
    export.changeFieldName("DESIRED_REACTIVE_LOAD", "Q");

    export.changeFieldName("DESIRED_REAL_LOAD_A", "Pa");
    export.changeFieldName("DESIRED_REAL_LOAD_B", "Pb");
    export.changeFieldName("DESIRED_REAL_LOAD_C", "Pc");

    export.changeFieldName("DESIRED_REACTIVE_LOAD_A", "Qa");
    export.changeFieldName("DESIRED_REACTIVE_LOAD_B", "Qb");
    export.changeFieldName("DESIRED_REACTIVE_LOAD_C", "Qc");

    export.changeFieldName("ACTUAL_REAL_LOAD", "Pact");
    export.changeFieldName("ACTUAL_REACTIVE_LOAD", "Qact");

    export.changeFieldName("ACTUAL_REAL_LOAD_A", "PaAct");
    export.changeFieldName("ACTUAL_REAL_LOAD_B", "PbAct");
    export.changeFieldName("ACTUAL_REAL_LOAD_C", "PcAct");

    export.changeFieldName("ACTUAL_REACTIVE_LOAD_A", "QaAct");
    export.changeFieldName("ACTUAL_REACTIVE_LOAD_B", "QbAct");
    export.changeFieldName("ACTUAL_REACTIVE_LOAD_C", "QcAct");

    // Generator Fields
    export.changeFieldName("REAL_GENERATION_MAX", "Pmax");
    export.changeFieldName("REAL_GENERATION_MIN", "Pmin");
    export.changeFieldName("REACTIVE_GENERATION_MAX", "Qmax");
    export.changeFieldName("REACTIVE_GENERATION_MIN", "Qmin");

    export.changeFieldName("DESIRED_REAL_GENERATION", "Pdes");
    export.changeFieldName("DESIRED_REACTIVE_GENERATION", "Qdes");
    export.changeFieldName("ACTUAL_REAL_GENERATION", "P");
    export.changeFieldName("ACTUAL_REACTIVE_GENERATION", "Q");

    export.changeFieldName("DESIRED_REAL_GENERATION_A", "PaDes");
    export.changeFieldName("DESIRED_REAL_GENERATION_B", "PbDes");
    export.changeFieldName("DESIRED_REAL_GENERATION_C", "PcDes");

    export.changeFieldName("DESIRED_REACTIVE_GENERATION_A", "QaDes");
    export.changeFieldName("DESIRED_REACTIVE_GENERATION_B", "QbDes");
    export.changeFieldName("DESIRED_REACTIVE_GENERATION_C", "QcDes");

    export.changeFieldName("ACTUAL_REAL_GENERATION_A", "Pa");
    export.changeFieldName("ACTUAL_REAL_GENERATION_B", "Pb");
    export.changeFieldName("ACTUAL_REAL_GENERATION_C", "Pc");

    export.changeFieldName("ACTUAL_REACTIVE_GENERATION_A", "Qa");
    export.changeFieldName("ACTUAL_REACTIVE_GENERATION_B", "Qb");
    export.changeFieldName("ACTUAL_REACTIVE_GENERATION_C", "Qc");

    export.changeFieldName("MAX_VOLTAGE", "Vmax");
    export.changeFieldName("MIN_VOLTAGE", "Vmin");
    export.changeFieldName("DESIRED_VOLTAGE", "Vdes");

    // Transformer Fields
    export.changeFieldName("CONTROL_SIDE", "ctrlSide");
    export.changeFieldName("CONTROL_MAX", "ctrlMax");
    export.changeFieldName("CONTROL_MIN", "ctrlMin");

    export.changeFieldName("MAX_TAP_ANGLE", "tapMax");
    export.changeFieldName("MIN_TAP_ANGLE", "tapMIN");

    export.changeFieldName("REACTIVE_MAX", "Qmax");
    export.changeFieldName("REACTIVE_MIN", "Qmin");

    // Shunt Capacitor Fields
    export.changeFieldName("REACTIVE_COMPENSATION", "Q");
    export.changeFieldName("REAL_COMPENSATION", "P");

    export.exportModel(model, writer, outputDirectory);

    System.out.println("Shapefile export completed without abnormal termination");

  }

}
