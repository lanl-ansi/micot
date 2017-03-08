package gov.lanl.micot.infrastructure.naturalgas.io.gaslib;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;

/**
 * This is a TxtFile writer that assumes that the model it gets
 * is made up of Txt Components
 * @author Russell Bent
 */
public class GaslibComponentFileWriter  extends GaslibFileWriterImpl {

	/**
	 * Constructor
	 */
	protected GaslibComponentFileWriter() {		
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
	protected void writePipes(NaturalGasModel model, StringBuffer buffer) {
		for (FlowConnection edge : model.getFlowConnections()) {
			String string = getTxtPipeLine(edge, model.getFirstNode(edge).getJunction(), model.getSecondNode(edge).getJunction()); 
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}
		
}
