package gov.lanl.micot.infrastructure.naturalgas.io.gaslib;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibModel;
import gov.lanl.micot.infrastructure.naturalgas.model.gaslib.GaslibModelConstants;

/**
 * This class is intended to write Txt files for general non Txt models
 * 
 * @author Russell Bent
 */
public class GeneralGaslibFileWriter extends GaslibFileWriterImpl {

	private Map<Junction, Integer>										ids				= null;
	private GaslibModel                               tempModel = null;
	private int edgeLegacyId                                    = -1;
	private Set<String> existingIds                                     = null;
	
	
	/**
	 * Constructor
	 */
	protected GeneralGaslibFileWriter() {
	  tempModel = new GaslibModel();
	  existingIds = new HashSet<String>();
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
			ids.put(junction, junction.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, Integer.class));
		}
	}

	/**
	 * Create the txt data that we might need
	 * @param id
	 * @param bus
	 * @return
	 */
	private int fillWithTxtData(int id, Junction junction) {
//	 TxtJunctionFactory.getInstance().updateJunction(junction);
    tempModel.getJunctionFactory().updateJunction(junction);
	  return Math.max(id, junction.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, Integer.class) + 1);
	}
  
  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, Pipe pipe) {
//    TxtPipeFactory.getInstance().updatePipe(pipe, junction1, junction2);
    tempModel.getPipeFactory().updatePipe(pipe, junction1, junction2, edgeLegacyId+"");
    existingIds.add(pipe.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY).toString());
    updateNextEdgeId();
  }

  /**
   * Create the pfw data that we might need
   * @param id
   * @param bus
   * @return
   */
  private void fillWithTxtData(Junction junction1, Junction junction2, Compressor compressor) {
//    TxtCompressorFactory.getInstance().updateCompressor(compressor, junction1, junction2);
    tempModel.getCompressorFactory().updateCompressor(compressor, junction1, junction2,edgeLegacyId+"");
    existingIds.add(compressor.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY).toString());
    updateNextEdgeId();
  }

	
	@Override
	protected void writePipes(NaturalGasModel model, StringBuffer buffer) {
		for (FlowConnection line : model.getFlowConnections()) {
		  String string = getTxtPipeLine(line, model.getFirstNode(line).getJunction(), model.getSecondNode(line).getJunction()); 
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
      ids.put(junction, junction.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, Integer.class));
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
    for (Compressor compressor : model.getCompressors()) {
      Junction id1 = model.getFirstNode(compressor).getJunction();
      Junction id2 = model.getSecondNode(compressor).getJunction();
      fillWithTxtData(id1, id2, compressor);      
    }

    for (NaturalGasConnection connection : model.getFlowConnections()) {
      Object obj = connection.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      if (obj != null) {
        existingIds.add(obj.toString());
      }
    }
    
    updateNextEdgeId();
  }

  /**
   * Increment the id counter
   */
  private void updateNextEdgeId() {
    while (existingIds.contains(edgeLegacyId+"")) {
      ++edgeLegacyId;
    }
  }
  
}
