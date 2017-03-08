package gov.lanl.micot.infrastructure.naturalgas.io.txt;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;

/**
 * This is a TxtFile writer that assumes that the model it gets
 * is made up of Txt Components
 * @author Russell Bent
 */
public class TxtComponentFileWriter  extends TxtFileWriterImpl {

	/**
	 * Constructor
	 */
	protected TxtComponentFileWriter() {		
	}

	@Override
	protected void writeJunctions(NaturalGasModel model, StringBuffer buffer) {
		for (Junction junction : model.getJunctions()) {
			String line = getTxtJunctionLine(junction, model.getNode(junction).getWell(), model.getNode(junction).getCityGate());
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}

	@Override
	protected void writeConnections(NaturalGasModel model, StringBuffer buffer) {
    for (Pipe pipe : model.getPipes()) {
      String string = getTxtPipeLine(pipe, model.getFirstNode(pipe).getJunction(), model.getSecondNode(pipe).getJunction()); 
      buffer.append(string);
      buffer.append(System.getProperty("line.separator"));
    }
    for (ShortPipe pipe : model.getShortPipes()) {
      String string = getTxtShortPipeLine(pipe, model.getFirstNode(pipe).getJunction(), model.getSecondNode(pipe).getJunction()); 
      buffer.append(string);
      buffer.append(System.getProperty("line.separator"));
    }
    for (Compressor compressor : model.getCompressors()) {
      String string = getTxtCompressorLine(compressor, model.getFirstNode(compressor).getJunction(), model.getSecondNode(compressor).getJunction()); 
      buffer.append(string);
      buffer.append(System.getProperty("line.separator"));
    }
    for (Valve valve : model.getValves()) {
      String string = getTxtValveLine(valve, model.getFirstNode(valve).getJunction(), model.getSecondNode(valve).getJunction()); 
      buffer.append(string);
      buffer.append(System.getProperty("line.separator"));
    }
    for (ControlValve valve : model.getControlValves()) {
      String string = getTxtControlValveLine(valve, model.getFirstNode(valve).getJunction(), model.getSecondNode(valve).getJunction()); 
      buffer.append(string);
      buffer.append(System.getProperty("line.separator"));
    }
    for (Resistor resistor : model.getResistors()) {
      String string = getTxtResistorLine(resistor, model.getFirstNode(resistor).getJunction(), model.getSecondNode(resistor).getJunction()); 
      buffer.append(string);
      buffer.append(System.getProperty("line.separator"));
    }
	}
		
}
