package gov.lanl.micot.infrastructure.ep.exec;

import java.io.IOException;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.io.matpower.MatPowerFile;
import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.util.io.ParameterReader;

/**
 * This executible exports PTI 23 files
 * @author Russell Bent
 *
 */
public class Powerworld2PTI23 {

  private static final String INPUT_FILE_FLAG = "-i";
  private static final String OUTPUT_FILE_FLAG = "-o";

  
  /**
   * Main Executible
   * @param args
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws IOException
   */
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    
    String initialFile = ParameterReader.getRequiredStringParameter(args, INPUT_FILE_FLAG, "input file");
    String outputFile = ParameterReader.getRequiredStringParameter(args, OUTPUT_FILE_FLAG, "output file");
    
    // associates raw files with Powerworld
    ElectricPowerModelFileFactory.registerExtension("raw",Class.forName("gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile"));
    
    ElectricPowerModelFileFactory factory = new ElectricPowerModelFileFactory();
    PowerworldModelFile file = (PowerworldModelFile)factory.createModelFile(initialFile);
        
    ElectricPowerModel model = file.readModel(initialFile);
    file.savePTI23File(outputFile, model);
     
//    MatPowerFile iFile = new MatPowerFile();
 //   iFile.saveFile(outputFile, model);        
  }

}
