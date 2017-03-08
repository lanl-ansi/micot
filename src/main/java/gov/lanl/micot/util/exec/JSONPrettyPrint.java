package gov.lanl.micot.util.exec;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import gov.lanl.micot.util.io.ParameterReader;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONReader;
import gov.lanl.micot.util.io.json.JSONWriter;

/**
 * Export JSON files in a pretty print format 
 * @author Russell Bent
 *
 */
public class JSONPrettyPrint {

  private static final String INPUT_FLAG = "-i";
  private static final String OUTPUT_FLAG = "-o";
  
  /**
   * JSON pretty print
   * @param args
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws FileNotFoundException {
    String input = ParameterReader.getRequiredStringParameter(args, INPUT_FLAG, "json input file");    
    String output = ParameterReader.getRequiredStringParameter(args, OUTPUT_FLAG, "json output file");

    FileInputStream fstream = new FileInputStream(input);
    JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
    JSONObject object = reader.readObject();
    
    PrintStream out = new PrintStream(output);
    JSONWriter writer = JSON.getDefaultJSON().createWriter(out);
    writer.write(object);

  }

}
