package gov.lanl.micot.infrastructure.naturalgas.io.txt;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModelConstants;

/**
 * General format for writing txt files
 * 
 * @author Russell Bent
 */
public abstract class TxtFileWriterImpl implements TxtFileWriter {

  /**
   * Constructor
   */
  public TxtFileWriterImpl() {
    super();
  }
  
	/**
	 * Routine for writing junctions
	 * 
	 * @param buses
	 */
	protected abstract void writeJunctions(NaturalGasModel model, StringBuffer buffer);

	/**
	 * Routine for writing lines
	 * 
	 * @param buses
	 */
	protected abstract void writeConnections(NaturalGasModel model, StringBuffer buffer);

  
	@Override
	public void saveFile(NaturalGasModel model, String filename) throws IOException {		
		FileOutputStream fileStream = new FileOutputStream(filename);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileStream));
		StringBuffer buffer = new StringBuffer();

		// output the header
		buffer.append(model.getNodes().size() + " " + model.getConnections().size() + " " + TxtModelFile.CURRENT_VERSION);
    buffer.append(System.getProperty("line.separator"));
    bufferedWriter.write(buffer.toString());
    buffer = new StringBuffer();
    
		// output the junctions
		writeJunctions(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the connections
		writeConnections(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		bufferedWriter.flush();
		bufferedWriter.close();
		fileStream.flush();
		fileStream.close();
	}

	/**
	 * Write a PFW line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getTxtJunctionLine(Junction junction, Well well, CityGate gate) {
	  int legacyid = junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
	  String name = junction.getAttribute(Junction.NAME_KEY, String.class);
	  double lat = junction.getCoordinate().getY();
    double lon = junction.getCoordinate().getX();
	  double minPressure = junction.getMinimumPressure();
    double maxPressure = junction.getMaximumPressure();
    double minInjection = 0;
    double maxInjection = 0;
    double cost = 0;

    if (well != null) {
      minInjection = well.getMinimumProduction().doubleValue();
      maxInjection = well.getMaximumProduction().doubleValue();
      cost = well.getAttribute(Well.ECONOMIC_COST_KEY) == null ? 0.0 : well.getAttribute(Well.ECONOMIC_COST_KEY, Number.class).doubleValue();
    }
    
    if (gate != null) {
      minInjection = -gate.getMaximumConsumption().doubleValue();
      maxInjection = -gate.getMinimumConsumption().doubleValue();
    }
    
	  return legacyid + " " + name + " " + lat + " " + lon + " " + minPressure + " " + maxPressure + " " + minInjection + " " + maxInjection + " " + cost;
	}


	/**
	 * Create a txt pipe format
	 * 
	 * @param data
	 * @return
	 */
	protected String getTxtPipeLine(Pipe pipe, Junction fromJunction, Junction toJunction) {	  
	  int legacyid = pipe.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
	  int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);    
    double diameter = pipe.getDiameter();
    double length = pipe.getLength();
    double resistance = pipe.getResistance();
    String str = legacyid + " " + TxtModelFile.TXT_PIPE_TYPE + " " + from + " " + to + " " + diameter + " " + length + " " + resistance;    
    return str;
	}

  /**
   * Create a txt pipe format
   * 
   * @param data
   * @return
   */
  protected String getTxtShortPipeLine(ShortPipe pipe, Junction fromJunction, Junction toJunction) {    
    int legacyid = pipe.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);    
    String str = legacyid + " " + TxtModelFile.TXT_SHORT_PIPE_TYPE + " " + from + " " + to;    
    return str;
  }

  /**
   * Create a txt pipe format
   * 
   * @param data
   * @return
   */
  protected String getTxtCompressorLine(Compressor compressor, Junction fromJunction, Junction toJunction) {    
    int legacyid = compressor.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);    
    double diameter = compressor.getDiameter();
    double length = compressor.getLength();
    double resistance = compressor.getResistance();
    double minRatio = compressor.getMinimumCompressionRatio();
    double maxRatio = compressor.getMaximumCompressionRatio();
   
    String str = legacyid + " " + TxtModelFile.TXT_COMPRESSOR_TYPE + " " + from + " " + to + " " + diameter + " " + length + " " + resistance + " " + minRatio + " " + maxRatio;    
    return str;
  }

  /**
   * Create a txt pipe format
   * 
   * @param data
   * @return
   */
  protected String getTxtValveLine(Valve valve, Junction fromJunction, Junction toJunction) {    
    int legacyid = valve.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);    
    String str = legacyid + " " + TxtModelFile.TXT_VALVE_TYPE + " " + from + " " + to;    
    return str;
  }
  

  /**
   * Create a txt pipe format
   * 
   * @param data
   * @return
   */
  protected String getTxtControlValveLine(ControlValve valve, Junction fromJunction, Junction toJunction) {    
    int legacyid = valve.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);    
    double diameter = valve.getDiameter();
    double length = valve.getLength();
    double resistance = valve.getResistance();
    double minRatio = valve.getAttribute(ControlValve.MINIMUM_COMPRESSION_RATIO_KEY, Number.class).doubleValue();
    double maxRatio = valve.getAttribute(ControlValve.MAXIMUM_COMPRESSION_RATIO_KEY, Number.class).doubleValue();
   
    String str = legacyid + " " + TxtModelFile.TXT_CONTROL_VALVE_TYPE + " " + from + " " + to + " " + diameter + " " + length + " " + resistance + " " + minRatio + " " + maxRatio;    
    return str;
  }

  /**
   * Create a txt pipe format
   * 
   * @param data
   * @return
   */
  protected String getTxtResistorLine(Resistor resistor, Junction fromJunction, Junction toJunction) {    
    int legacyid = resistor.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);    
    double diameter = resistor.getDiameter();
    double drag = resistor.getAttribute(Resistor.DRAG_FACTOR_KEY, Number.class).doubleValue();
    double resistance = resistor.getResistance();
    String str = legacyid + " " + TxtModelFile.TXT_RESISTOR_TYPE + " " + from + " " + to + " " + diameter + " " + drag + " " + resistance;    
    return str;
  }

  
}
