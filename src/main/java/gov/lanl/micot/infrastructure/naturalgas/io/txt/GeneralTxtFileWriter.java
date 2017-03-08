package gov.lanl.micot.infrastructure.naturalgas.io.txt;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.ControlValve;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;
import gov.lanl.micot.infrastructure.naturalgas.model.ShortPipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Valve;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModel;
import gov.lanl.micot.infrastructure.naturalgas.model.txt.TxtModelConstants;

/**
 * This class is intended to write Txt files for general non Txt models
 * 
 * @author Russell Bent
 */
public class GeneralTxtFileWriter extends TxtFileWriterImpl {

	private Map<Junction, Integer>										ids				= null;
	private TxtModel tempModel                                  = null;
	int edgeLegacyId                                            = -1;
	
	/**
	 * Constructor
	 */
	protected GeneralTxtFileWriter() {
	  tempModel = new TxtModel();
	}

	@Override
	protected void writeJunctions(NaturalGasModel model, StringBuffer buffer) {
		ids = new HashMap<Junction, Integer>();
    setupJunctions(model);
    setupPipes(model);
    			
		for (Junction junction : model.getJunctions()) {
			String line = getTxtJunctionLine(junction, model.getNode(junction).getWell(), model.getNode(junction).getCityGate());
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
			ids.put(junction, junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class));
		}
	}

	/**
	 * Create the txt data that we might need
	 * @param id
	 * @param bus
	 * @return
	 */
	private int fillWithTxtData(int id, Junction junction) {
//	   TxtJunctionFactory.getInstance().updateJunction(junction);
     tempModel.getJunctionFactory().updateJunction(junction);
	  return Math.max(id, junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class) + 1);
	}
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, Pipe pipe) {
//    TxtPipeFactory.getInstance().updatePipe(pipe, junction1, junction2);
    tempModel.getPipeFactory().updatePipe(pipe, junction1, junction2,edgeLegacyId);
    edgeLegacyId = Math.max(edgeLegacyId,pipe.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue());
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, Compressor compressor) {
//    TxtCompressorFactory.getInstance().updateCompressor(compressor, junction1, junction2);
    tempModel.getCompressorFactory().updateCompressor(compressor, junction1, junction2,edgeLegacyId);
    edgeLegacyId = Math.max(edgeLegacyId,compressor.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue());
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, Valve valve) {
//    TxtValveFactory.getInstance().updateValve(valve, junction1, junction2);
    tempModel.getValveFactory().updateValve(valve, junction1, junction2,edgeLegacyId);
    edgeLegacyId = Math.max(edgeLegacyId,valve.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue());
  }

  /**
   * Create the txt data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, ControlValve valve) {
//    TxtControlValveFactory.getInstance().updateValve(valve, junction1, junction2);
    tempModel.getControlValveFactory().updateValve(valve, junction1, junction2,edgeLegacyId);
    edgeLegacyId = Math.max(edgeLegacyId,valve.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue());
  }

  /**
   * Create the txt data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, ShortPipe pipe) {
//    TxtShortPipeFactory.getInstance().updateShortPipe(pipe, junction1, junction2);
    tempModel.getShortPipeFactory().updateShortPipe(pipe, junction1, junction2, edgeLegacyId);
    edgeLegacyId = Math.max(edgeLegacyId,pipe.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue());
  }

  
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, Resistor resistor) {
//    TxtResistorFactory.getInstance().updateResistor(resistor, junction1, junction2);
    tempModel.getResistorFactory().updateResistor(resistor, junction1, junction2, edgeLegacyId);
    edgeLegacyId = Math.max(edgeLegacyId,resistor.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue());
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
 
  /**
   * Set up the buses with pfw data
   * @param model
   */
  private void setupJunctions(NaturalGasModel model) {
    int id = 1; 
    for (Junction junction : model.getJunctions()) {
      id = fillWithTxtData(id,junction);
      ids.put(junction, junction.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY, Integer.class));
    }
  }

  /**
   * Set up the lines with pfw data
   * @param model
   */
  private void setupPipes(NaturalGasModel model) {
    for (Pipe pipe : model.getPipes()) {
      Junction id1 = model.getFirstNode(pipe).getJunction();
      Junction id2 = model.getSecondNode(pipe).getJunction();
      fillWithTxtData(id1, id2, pipe);      
    }
    for (ShortPipe pipe : model.getShortPipes()) {
      Junction id1 = model.getFirstNode(pipe).getJunction();
      Junction id2 = model.getSecondNode(pipe).getJunction();
      fillWithTxtData(id1, id2, pipe);      
    }
    for (Compressor compressor : model.getCompressors()) {
      Junction id1 = model.getFirstNode(compressor).getJunction();
      Junction id2 = model.getSecondNode(compressor).getJunction();
      fillWithTxtData(id1, id2, compressor);      
    }
    for (Valve valve : model.getValves()) {
      Junction id1 = model.getFirstNode(valve).getJunction();
      Junction id2 = model.getSecondNode(valve).getJunction();
      fillWithTxtData(id1, id2, valve);      
    }
    for (ControlValve valve : model.getControlValves()) {
      Junction id1 = model.getFirstNode(valve).getJunction();
      Junction id2 = model.getSecondNode(valve).getJunction();
      fillWithTxtData(id1, id2, valve);      
    }    
    for (Resistor resistor : model.getResistors()) {
      Junction id1 = model.getFirstNode(resistor).getJunction();
      Junction id2 = model.getSecondNode(resistor).getJunction();
      fillWithTxtData(id1, id2, resistor);      
    }

    edgeLegacyId = -1;
    for (NaturalGasConnection c : model.getFlowConnections()) {
      int id = c.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY) == null ? 0 : c.getAttribute(TxtModelConstants.TXT_LEGACY_ID_KEY,Number.class).intValue();
      edgeLegacyId = Math.max(edgeLegacyId,id);
    }
    ++edgeLegacyId;

  }
    
}
