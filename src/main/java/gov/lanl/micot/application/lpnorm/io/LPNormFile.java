package gov.lanl.micot.application.lpnorm.io;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.application.lpnorm.model.LPNormBusFactory;
import gov.lanl.micot.application.lpnorm.model.LPNormGeneratorFactory;
import gov.lanl.micot.application.lpnorm.model.LPNormLineFactory;
import gov.lanl.micot.application.lpnorm.model.LPNormLoadFactory;
import gov.lanl.micot.application.lpnorm.model.LPNormModelImpl;
import gov.lanl.micot.application.lpnorm.model.LPNormTransformerFactory;
import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Reads in model data using the LPNORM input schema
 * 
 * @author Russell Bent
 * 
 */
public class LPNormFile implements ElectricPowerModelFile {

//  private static final double DEFAULT_MVA_BASE = 100.0;
  
 /**
	 * Constructor
	 */
	public LPNormFile() {
	}

	@Override
	public ElectricPowerModel readModel(String filename) throws IOException {
		ElectricPowerModel state = parseFile(filename);
		return state;
	}
		
	@Override
	public void saveFile(String filename, ElectricPowerModel model)  throws IOException {
	  throw new RuntimeException("Error: LPNormFile::saveFile is not implemented yet");
	}
	
	/**
	 * Parses the file and returns a model
	 * @throws IOException 
	 */
	private ElectricPowerModel parseFile(String filename) throws IOException {
		LPNormModelImpl model = new LPNormModelImpl();
		
		FileInputStream fstream = new FileInputStream(filename);
    JSONReader reader = JSON.getDefaultJSON().createReader(fstream);
    JSONObject object = reader.readObject();
    				
		LPNormBusFactory busFactory = model.getBusFactory();
		LPNormGeneratorFactory generatorFactory = model.getGeneratorFactory();;
		LPNormLineFactory lineFactory = model.getLineFactory();
    LPNormTransformerFactory transformerFactory = model.getTransformerFactory();
		LPNormLoadFactory loadFactory = model.getLoadFactory();

    HashMap<String, Bus> buses = new HashMap<String, Bus>();
    HashMap<String, Generator> generators = new HashMap<String, Generator>();
		
		JSONArray busArray = object.getArray(LPNormIOConstants.BUSES_TAG);
    JSONArray loadArray = object.getArray(LPNormIOConstants.LOADS_TAG);
    JSONArray generatorArray = object.getArray(LPNormIOConstants.GENERATORS_TAG);
    JSONArray edgeArray = object.getArray(LPNormIOConstants.LINES_TAG);
    JSONArray lineCodeArray = object.getArray(LPNormIOConstants.LINE_CODES_TAG);
    
    double mvaBase = 1.0; //object.containsKey(LPNormIOConstants.MVA_BASE_TAG) ? object.getDouble(LPNormIOConstants.MVA_BASE_TAG) : DEFAULT_MVA_BASE;
    model.setMVABase(mvaBase);
    
		// create the buses
    for (int i = 0; i < busArray.size(); ++i) {
      JSONObject jbus = busArray.getObject(i);
      Bus bus = busFactory.createBus(jbus);
      String id = jbus.getString(LPNormIOConstants.BUS_ID_TAG);      
      model.addBus(bus);
      buses.put(id,bus);
    }
    
    // create the generators
    for (int i = 0; i < generatorArray.size(); ++i) {
      JSONObject gen = generatorArray.getObject(i);
      String busid = gen.getString(LPNormIOConstants.GEN_BUS_ID_TAG);
      Generator generator = generatorFactory.createGenerator(gen, buses.get(busid));
      String id = generator.getAttribute(LPNormIOConstants.GEN_ID_TAG, String.class);      
      model.addGenerator(generator, buses.get(busid));      
      generators.put(id, generator);
    }
    
    // create the loads
    double defaultLineCapacity = 0.0;
    for (int i = 0; i < loadArray.size(); ++i) {
      JSONObject load = loadArray.getObject(i);
      String busid = load.getString(LPNormIOConstants.LOAD_BUS_ID_TAG);
      Load l = loadFactory.createLoad(load, buses.get(busid));
      model.addLoad(l, buses.get(busid));      
      defaultLineCapacity += l.getAttribute(Load.REACTIVE_LOAD_A_MAX_KEY, Number.class).doubleValue()
                          + l.getAttribute(Load.REACTIVE_LOAD_B_MAX_KEY, Number.class).doubleValue()
                          + l.getAttribute(Load.REACTIVE_LOAD_C_MAX_KEY, Number.class).doubleValue()
                          + l.getAttribute(Load.REAL_LOAD_A_MAX_KEY, Number.class).doubleValue()
                          + l.getAttribute(Load.REAL_LOAD_B_MAX_KEY, Number.class).doubleValue()
                          + l.getAttribute(Load.REAL_LOAD_C_MAX_KEY, Number.class).doubleValue();
    }
      
    HashMap<String, JSONObject> lineCodes = new HashMap<String,JSONObject>();
    for (int i = 0; i < lineCodeArray.size(); ++i) {
      JSONObject lineCode = lineCodeArray.getObject(i);
      lineCodes.put(lineCode.getString(LPNormIOConstants.LINE_CODE_ID_TAG), lineCode);
    }
        
    // create the lines and transformers
    for (int i = 0; i < edgeArray.size(); ++i) {
      JSONObject line = edgeArray.getObject(i);
      String id1 = line.getString(LPNormIOConstants.LINE_BUS1_ID_TAG);
      String id2 = line.getString(LPNormIOConstants.LINE_BUS2_ID_TAG);
      boolean isTransformer = line.containsKey(LPNormIOConstants.LINE_IS_TRANSFORMER_TAG) ? line.getBoolean(LPNormIOConstants.LINE_IS_TRANSFORMER_TAG) : false;
      String codeId = line.getString(LPNormIOConstants.LINE_LINE_CODE_TAG);
      JSONObject lineCode = lineCodes.get(codeId);
      
      if (!isTransformer) {
        Line l  = lineFactory.createLine(line, lineCode, buses.get(id1), buses.get(id2), defaultLineCapacity);
        model.addEdge(l, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));    
      }        
      else {
        Transformer l  = transformerFactory.createTransformer(line, lineCode, buses.get(id1), buses.get(id2), defaultLineCapacity);
        model.addEdge(l, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));
      }
    }        
		return model;
	}

  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename,(ElectricPowerModel)model);
  }

  /**
   * Read in a simple test file
   * @param args
   * @throws IOException
   */  
  public static void main(String[] args) throws IOException {
    String initialFile = "lpnorm" + File.separatorChar + "Ice_Harden_Rural_1.json";
    LPNormFile file = new LPNormFile(); 
    ElectricPowerModel model = file.readModel(initialFile);     
  }
}