package gov.lanl.micot.infrastructure.ep.model.opendss;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
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
