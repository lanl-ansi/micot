package gov.lanl.micot.infrastructure.naturalgas.io.gaslib;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModelConstants;

/**
 * General format for writing txt files
 * 
 * @author Russell Bent
 */
public abstract class GaslibFileWriterImpl implements GaslibFileWriter {

  /**
   * Constructor
   */
  public GaslibFileWriterImpl() {
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
	protected abstract void writePipes(NaturalGasModel model, StringBuffer buffer);

  
	@Override
	public void saveFile(NaturalGasModel model, String filename) throws IOException {		
		FileOutputStream fileStream = new FileOutputStream(filename);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileStream));
		StringBuffer buffer = new StringBuffer();

		// output the header
		buffer.append(model.getNodes().size() + " " + model.getConnections().size());
    buffer.append(System.getProperty("line.separator"));
    bufferedWriter.write(buffer.toString());
    buffer = new StringBuffer();
    
		// output the junctions
		writeJunctions(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the pipes
		writePipes(model, buffer);
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
	 * Create a pfw line format
	 * 
	 * @param data
	 * @return
	 */
	protected String getTxtPipeLine(FlowConnection edge, Junction fromJunction, Junction toJunction) {	  
	  int legacyid = edge.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
	  int from = fromJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    int to = toJunction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class);
    double diameter = edge instanceof Pipe ? ((Pipe)edge).getDiameter() : ((Compressor)edge).getDiameter();
    double length = edge instanceof Pipe ? ((Pipe)edge).getLength() : ((Compressor)edge).getLength();
    double resistance = edge instanceof Pipe ? ((Pipe)edge).getResistance() : ((Compressor)edge).getResistance();
	  
    String str = legacyid + " " + from + " " + to + " " + diameter + " " + length + " " + resistance;
    
    if (edge instanceof Compressor) {
      str += " " + ((Compressor)edge).getMinimumCompressionRatio() + " " +  ((Compressor)edge).getMaximumCompressionRatio();
    }
    return str;
	}


	
  
  
}
