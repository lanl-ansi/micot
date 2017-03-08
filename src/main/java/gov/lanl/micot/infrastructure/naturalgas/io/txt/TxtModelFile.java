package gov.lanl.micot.infrastructure.naturalgas.io.txt;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.naturalgas.io.NaturalGasModelFile;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtCityGateFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtCompressorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtControlValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtJunctionFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModel;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtPipeFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtResistorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtShortPipeFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtWellFactory;
import gov.lanl.micot.util.io.FileParser;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.io.*;

/**
 * Reads a txt file based format of natural gas systems
 * 
 * @author Russell bent Slightly modified by Conrado - Oct 2014 Bug fixed:
 *         Compressor data were never parsed when calling 'NaturalGasModel
 *         parseFile(String filename)' -- A compressor-data text line has 8
 *         entries (tokens), yet the 'tokenizer.countTokens' method counted 5
 *         because 'tokenizer' was already called 3 times. -- Basically,
 *         'tokenizer.countTokens()' calculates the # of times that the
 *         'nextToken' method can be called before it generates an exception.
 * 
 */
public class TxtModelFile extends FileParser implements NaturalGasModelFile {

  protected static final int CURRENT_VERSION = 2;
  protected static final int TXT_PIPE_TYPE = 0;
  protected static final int TXT_SHORT_PIPE_TYPE = 1;
  protected static final int TXT_COMPRESSOR_TYPE = 2;
  protected static final int TXT_VALVE_TYPE = 3;
  protected static final int TXT_CONTROL_VALVE_TYPE = 4;
  protected static final int TXT_RESISTOR_TYPE = 5;

  private String deliminiter = " ";
  private int nodes = -1;
  private int edges =1;
    
  /**
   * Constructor
   */
  public TxtModelFile() {
  }

  @Override
  public NaturalGasModel readModel(String filename) throws IOException {
    NaturalGasModel state = parseFile(filename);
    return state;
  }

  @Override
  public void saveFile(String filename, NaturalGasModel model) throws IOException {
    TxtFileWriter writer = TxtFileWriterFactory.getInstance().getTxtFileWriter(model);
    writer.saveFile(model, filename);
  }

  /**
   * Parses the file and returns a model
   * 
   * @throws IOException
   */
  private NaturalGasModel parseFile(String fn) throws IOException {
    String filename = fn;
    filename = filename.replace('/', File.separatorChar); // some reobustness on input
    filename = filename.replace('\\', File.separatorChar); // some reobustness on input
    
    
    readFile(filename);
    
    String firstLine = fileLines.get(0);
    deliminiter = " ";
    if (firstLine.contains("\t")) {
      deliminiter = "\t";
    }

    StringTokenizer tokenizer = new StringTokenizer(firstLine, deliminiter);

    nodes = Integer.parseInt(tokenizer.nextToken());
    edges = Integer.parseInt(tokenizer.nextToken());
    int version = 1;
    if (tokenizer.hasMoreTokens()) {
      version = Integer.parseInt(tokenizer.nextToken());
    }
    
    if (version == 1) {
      return parseVersion1();
    }
    else {
      return parseVersion2();
    }
  }

  /**
   * parse the old version 1 of the txt files
   * @return
   */
  private NaturalGasModel parseVersion1() {
    TxtModel model = new TxtModel();

    TxtJunctionFactory junctionFactory = model.getJunctionFactory();
    TxtWellFactory wellFactory = model.getWellFactory();
    TxtPipeFactory pipeFactory = model.getPipeFactory();
    TxtCompressorFactory compressorFactory = model.getCompressorFactory();
    TxtCityGateFactory gateFactory = model.getCityGateFactory();

    HashMap<Integer, Junction> junctions = new HashMap<Integer, Junction>();


    for (int line = 1; line <= nodes; ++line) {
      
      String buffer = fileLines.get(line);
      StringTokenizer tokenizer = new StringTokenizer(buffer, deliminiter);

      int id = Integer.parseInt(tokenizer.nextToken());
      tokenizer.nextToken(); // name
      tokenizer.nextToken(); // lat
      tokenizer.nextToken(); // lon
      tokenizer.nextToken(); // p_min
      tokenizer.nextToken(); // p_max
      double minFlow = Double.parseDouble(tokenizer.nextToken());
      double maxFlow = Double.parseDouble(tokenizer.nextToken());

      Junction junction = junctionFactory.createJunction(buffer, deliminiter);
      model.addJunction(junction);
      junctions.put(id, junction);

      if (minFlow < 0) {
        CityGate gate = gateFactory.createCityGate(buffer, junction, deliminiter);
        model.addCityGate(gate, junction);
      }

      if (maxFlow > 0) {
        Well well = wellFactory.createWell(buffer, junction, deliminiter);
        model.addWell(well, junction);
      }
    }

    for (int line = nodes + 1; line <= nodes + edges; ++line) {
      String buffer = fileLines.get(line);
      StringTokenizer tokenizer = new StringTokenizer(buffer, deliminiter);
      int numOfTokens = tokenizer.countTokens(); 
      tokenizer.nextToken();
      int from = Integer.parseInt(tokenizer.nextToken());
      int to = Integer.parseInt(tokenizer.nextToken());

      if (numOfTokens <= 6) {
        Pipe pipe = pipeFactory.createPipeVersion1(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addPipe(pipe, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      } else {
        Compressor compressor = compressorFactory.createCompressorVersion1(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addCompressor(compressor, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      }
    }
    return model;
  }

  
  /**
   * Parse version 2 of the txt file
   * @return
   */
  private NaturalGasModel parseVersion2() {
    TxtModel model = new TxtModel();

    TxtJunctionFactory junctionFactory = model.getJunctionFactory();
    TxtWellFactory wellFactory = model.getWellFactory();
    TxtPipeFactory pipeFactory = model.getPipeFactory();
    TxtCompressorFactory compressorFactory = model.getCompressorFactory();
    TxtCityGateFactory gateFactory = model.getCityGateFactory();
    TxtShortPipeFactory shortPipeFactory = model.getShortPipeFactory();
    TxtValveFactory valveFactory = model.getValveFactory();
    TxtControlValveFactory controlValveFactory = model.getControlValveFactory();
    TxtResistorFactory resistorFactory = model.getResistorFactory();

    HashMap<Integer, Junction> junctions = new HashMap<Integer, Junction>();


    for (int line = 1; line <= nodes; ++line) {
      String buffer = fileLines.get(line);
      StringTokenizer tokenizer = new StringTokenizer(buffer, deliminiter);

      int id = Integer.parseInt(tokenizer.nextToken());
      tokenizer.nextToken(); // name
      tokenizer.nextToken(); // lat
      tokenizer.nextToken(); // lon
      tokenizer.nextToken(); // p_min
      tokenizer.nextToken(); // p_max
      double minFlow = Double.parseDouble(tokenizer.nextToken());
      double maxFlow = Double.parseDouble(tokenizer.nextToken());

      Junction junction = junctionFactory.createJunction(buffer, deliminiter);
      model.addJunction(junction);
      junctions.put(id, junction);

      if (minFlow < 0) {
        CityGate gate = gateFactory.createCityGate(buffer, junction, deliminiter);
        model.addCityGate(gate, junction);
      }

      if (maxFlow > 0) {
        Well well = wellFactory.createWell(buffer, junction, deliminiter);
        model.addWell(well, junction);
      }
    }

    for (int line = nodes + 1; line <= nodes + edges; ++line) {
      String buffer = fileLines.get(line);
      StringTokenizer tokenizer = new StringTokenizer(buffer, deliminiter);
      tokenizer.nextToken(); // id
      int type = Integer.parseInt(tokenizer.nextToken());;
      int from = Integer.parseInt(tokenizer.nextToken());
      int to = Integer.parseInt(tokenizer.nextToken());

      if (type == TxtModelFile.TXT_PIPE_TYPE) {
        Pipe pipe = pipeFactory.createPipeVersion2(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addPipe(pipe, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      } 
      else if (type == TxtModelFile.TXT_COMPRESSOR_TYPE) {
        Compressor compressor = compressorFactory.createCompressorVersion2(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addCompressor(compressor, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      }
      else if (type == TxtModelFile.TXT_VALVE_TYPE) {
        Valve valve = valveFactory.createValveVersion2(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addValve(valve, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      }
      else if (type == TxtModelFile.TXT_CONTROL_VALVE_TYPE) {
        ControlValve valve = controlValveFactory.createControlValveVersion2(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addControlValve(valve, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      }
      else if (type == TxtModelFile.TXT_RESISTOR_TYPE) {
        Resistor resistor = resistorFactory.createResistorVersion2(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addResistor(resistor, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      }
      else if (type == TxtModelFile.TXT_SHORT_PIPE_TYPE) {
        ShortPipe pipe = shortPipeFactory.createPipeVersion2(buffer, junctions.get(from), junctions.get(to), deliminiter);
        model.addShortPipe(pipe, model.getNode(junctions.get(from)), model.getNode(junctions.get(to)));
      }
      else {
        throw new RuntimeException("Error: no edge of type " + type);
      }


    }
    return model;
  }

  
  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename, (NaturalGasModel) model);
  }

}
