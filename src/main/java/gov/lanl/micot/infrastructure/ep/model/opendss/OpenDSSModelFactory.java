package gov.lanl.micot.infrastructure.ep.model.opendss;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.ModelFactory;

/**
 * Factory class for creating open dss model states from other model states
 * @author Russell Bent
 */
public class OpenDSSModelFactory implements ModelFactory<ElectricPowerModel> {

	private static OpenDSSModelFactory INSTANCE = null;
	
	public static OpenDSSModelFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OpenDSSModelFactory();
		}
		return INSTANCE;
	}
	
	public static final boolean HACK_8500_NODE_SYSTEM = false; // the 8500 node system has some special features that need to be addressed during the import.  This should be refactored somehow
	
	/**
	 * Constructor
	 */
	private OpenDSSModelFactory() {		
	}
	
	@Override
	public OpenDSSModel constructModel(Model model) {
    OpenDSSModel newModel =  (model instanceof OpenDSSModel) ? constructOpenDSSModel((OpenDSSModel)model) : constructDefaulOpenDSSModel((ElectricPowerModel)model);
    return newModel;
  }
	
	/**
	 * Method for constructing a model state
	 * @param state
	 * @return
	 */
	 public ElectricPowerModel constructOpenDSSModel(ElectricPowerModel state) {
	    OpenDSSModel model = constructModel(state);
	    return model;
	  }

	/**
	 * Constructor a default ieiss model
	 * @param model
	 * @return
	 */
	 private OpenDSSModel constructDefaulOpenDSSModel(ElectricPowerModel model) {
	  throw new RuntimeException("OpenDSSModelFactory::constructDefaultOpenDSSModel");
	  // TODO	   
	}

	/**
	 * Basically creates an openDSS model state when the old model was an openDSS model
	 * @param state
	 * @return
	 */
	private OpenDSSModel constructOpenDSSModel(OpenDSSModel model) {		
		return model;
	}
	

	
}
