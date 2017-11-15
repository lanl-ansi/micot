package gov.lanl.micot.infrastructure.naturalgas.io.gaslib;

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
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibCityGateFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibCompressorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibControlValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibJunctionFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibModel;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibModelConstants;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibPipeFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibResistorFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibShortPipeFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibValveFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibWellFactory;
import gov.lanl.micot.util.io.FileParser;
import gov.lanl.micot.util.io.PropertiesFactory;
import gov.lanl.micot.util.io.xml.XML;
import gov.lanl.micot.util.io.xml.XMLDocument;
import gov.lanl.micot.util.io.xml.XMLElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.*;
import java.net.URI;

/**
 * Reads a gaslib xml based format of natural gas systems
 * 
 * 
 */
public class GaslibModelFile extends FileParser implements NaturalGasModelFile {
   
	/**
	 * Constructor
	 */
	public GaslibModelFile() {
	}

	@Override
	public NaturalGasModel readModel(String filename) throws IOException {
		NaturalGasModel state = parseFile(filename);
		return state;
	}
	
	@Override
	public void saveFile(String filename, NaturalGasModel model)  throws IOException {
		GaslibFileWriter writer = GaslibFileWriterFactory.getInstance().getTxtFileWriter(model);		
		writer.saveFile(model, filename);
	}
	
	/**
	 * Parses the file and returns a model
	 * @throws IOException 
	 */
  private NaturalGasModel parseFile(String filename) throws IOException {
		GaslibModel model = new GaslibModel();
		
		GaslibJunctionFactory junctionFactory = model.getJunctionFactory();
		GaslibWellFactory wellFactory = model.getWellFactory();
		GaslibPipeFactory pipeFactory = model.getPipeFactory();
    GaslibShortPipeFactory shortPipeFactory = model.getShortPipeFactory();
		GaslibCompressorFactory compressorFactory = model.getCompressorFactory();
		GaslibCityGateFactory gateFactory = model.getCityGateFactory();		
    GaslibValveFactory valveFactory = model.getValveFactory();   
    GaslibControlValveFactory controlValveFactory = model.getControlValveFactory();    
    GaslibResistorFactory resistorFactory = model.getResistorFactory();    

		Map<String,Junction> junctions = new HashMap<String,Junction>();
		
		Properties properties = PropertiesFactory.getInstance().createProperties(filename);
		String compressorFilename = properties.getProperty(GaslibModelConstants.COMPRESSOR_FILE_TAG);
	  String networkFilename = properties.getProperty(GaslibModelConstants.NETWORK_FILE_TAG);
	  String injectionFilename = properties.getProperty(GaslibModelConstants.INJECTION_FILE_TAG);
	  
	  // network file
    File networkFile = new File(networkFilename);
    if (!networkFile.exists()) {
      networkFile = new File(System.getProperty("user.dir") +File.separatorChar + networkFile);
    }    
    URI networkUri = networkFile.toURI();
    XML xml = XML.getDefaultXML();
    XMLDocument networkDocument = xml.parse(networkUri.toURL());  

    XMLElement networkRoot = networkDocument.getTopElement();    
    XMLElement nodesElement = networkRoot.getElement(GaslibModelConstants.NODES_TAG);
    XMLElement connectionsElement = networkRoot.getElement(GaslibModelConstants.CONNECTIONS_TAG);
    
    // get all the node data
    Collection<XMLElement> elements = nodesElement.elements();

    for (XMLElement element : elements) {
      Junction junction = junctionFactory.createJunction(element);
      model.addJunction(junction);
      junctions.put(junction.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, String.class), junction); 

      if (element.getName().equals(GaslibModelConstants.SOURCE_TAG)) {
        Well well = wellFactory.createWell(element, junction);
        model.addWell(well, junction);
      }
      else if (element.getName().equals(GaslibModelConstants.SINK_TAG)) {
        CityGate gate = gateFactory.createCityGate(element, junction);
        model.addCityGate(gate, junction);
      }
      else if (!element.getName().equals(GaslibModelConstants.INNODE_TAG)) {
        System.out.println(element.getName());
      }
    }
        
    elements = connectionsElement.elements();
    for (XMLElement element : elements) {
      String fromId = element.getValue(GaslibModelConstants.CONNECTION_FROM_TAG);
      String toId = element.getValue(GaslibModelConstants.CONNECTION_TO_TAG);
      Junction fromJcn = junctions.get(fromId);  
      Junction toJcn = junctions.get(toId);  
      
      if (element.getName().equals(GaslibModelConstants.PIPE_TAG)) {
        Pipe pipe = pipeFactory.createPipe(element, fromJcn, toJcn);
        model.addPipe(pipe, model.getNode(fromJcn), model.getNode(toJcn));
      }
      else if (element.getName().equals(GaslibModelConstants.SHORT_PIPE_TAG)) {
        // a short pipe is a compressor with a compression ratio of 1.0
        ShortPipe pipe = shortPipeFactory.createShortPipe(element, fromJcn, toJcn);
        model.addShortPipe(pipe, model.getNode(fromJcn), model.getNode(toJcn));
      }
      else if (element.getName().equals(GaslibModelConstants.COMPRESSOR_TAG)) {
        Compressor compressor = compressorFactory.createCompressor(element, fromJcn, toJcn);
        model.addCompressor(compressor, model.getNode(fromJcn), model.getNode(toJcn));
      }
      else if (element.getName().equals(GaslibModelConstants.VALVE_TAG)) {
        Valve valve = valveFactory.createValve(element, fromJcn, toJcn);
        model.addValve(valve, model.getNode(fromJcn), model.getNode(toJcn));
      }
      else if (element.getName().equals(GaslibModelConstants.CONTROL_VALVE_TAG)) {
        ControlValve valve = controlValveFactory.createControlValve(element, fromJcn, toJcn);
        model.addControlValve(valve, model.getNode(fromJcn), model.getNode(toJcn));
      }
      else if (element.getName().equals(GaslibModelConstants.RESISTOR_TAG)) {
        Resistor resistor = resistorFactory.createResistor(element, fromJcn, toJcn);
        model.addResistor(resistor, model.getNode(fromJcn), model.getNode(toJcn));
      }
      else {
        System.out.println(element.getName());
      }
    }

    // get an actual flow scenario
    if (injectionFilename != null) {
      String injectionScenario = properties.getProperty(GaslibModelConstants.INJECTION_SCENARIO_TAG);

      File injectionFile = new File(injectionFilename);
      if (!injectionFile.exists()) {
        injectionFile = new File(System.getProperty("user.dir") +File.separatorChar + injectionFile);
      }    
      URI injectionUri = injectionFile.toURI();
      XMLDocument injectionDocument = xml.parse(injectionUri.toURL());
      XMLElement injectionRoot = injectionDocument.getTopElement();

      Collection<XMLElement> scenarioElements = injectionRoot.getElements(GaslibModelConstants.SCENARIO_TAG);
      
      boolean found = false;
      for (XMLElement element : scenarioElements) {
        String id = element.getValue(GaslibModelConstants.SCENARIO_ID_TAG);        
        if (id.equals(injectionScenario)) {
          found = true;
          
          Collection<XMLElement> nelements = element.getElements(GaslibModelConstants.NODE_TAG);
          for (XMLElement node : nelements) {
            String legacyid = node.getValue(GaslibModelConstants.NODE_ID_TAG);
            
            XMLElement flowElement = node.getElement(GaslibModelConstants.NODE_FLOW_TAG);
            String flowStr = flowElement.getValue(GaslibModelConstants.VALUE_TAG);
            double flow = Double.parseDouble(flowStr);
            
            Junction junction = junctions.get(legacyid);
            Well well = model.getNode(junction).getWell();
            CityGate gate = model.getNode(junction).getCityGate();
            if (well != null) {
              well.setMaximumProduction(flow);
              well.setMinimumProduction(flow);
              well.setProduction(flow);
            }
            else {
              gate.setMaximumConsumption(flow);
              gate.setMinimumConsumption(flow);
              gate.setConsumption(flow);
            }
          }
          
        }
      }
      
      if (!found) {
        System.err.println("Error: did not find scenario " + injectionScenario);
      }
    }
    return model;
	}


  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename,(NaturalGasModel)model);
  }

}
