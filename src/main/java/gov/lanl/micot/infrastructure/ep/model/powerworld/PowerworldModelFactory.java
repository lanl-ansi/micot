package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.io.File;
import java.io.IOException;

import gov.lanl.micot.infrastructure.ep.io.cdf.CDFFile;
import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldModelFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelConstants;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.ModelFactory;

/**
 * Factory class for creating power world models
 * @author Russell Bent
 */
public class PowerworldModelFactory implements ModelFactory<ElectricPowerModel> {

	private static PowerworldModelFactory INSTANCE = null;
	
	public static synchronized PowerworldModelFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PowerworldModelFactory();
		}
		return INSTANCE;
	}
		
	/**
	 * Constructor
	 */
	private PowerworldModelFactory() {		
	}
	
	@Override
	public PowerworldModel constructModel(Model model) {
    PowerworldModel newModel =  (model instanceof PowerworldModel) ? constructPowerworldModel((PowerworldModel)model) : constructDefaultPowerworldModel((ElectricPowerModel)model);
    return newModel;
  }
	
	/**
	 * Method for constructing a model state
	 * @param state
	 * @return
	 */
	 public PowerworldModel constructPowerworldModel(ElectricPowerModel state) {
	    PowerworldModel model = constructModel(state);
	    return model;
	  }

	/**
	 * Constructor a default power world model
	 * @param model
	 * @return
	 */
	 private synchronized PowerworldModel constructDefaultPowerworldModel(ElectricPowerModel model) {	   
	   // ok, so the way this works... 
	   // 1. we will output the model (if not already a powerworld model) in ieee commmon format
	   // 2. we will then read in the model using the powerworld com interfaces... trying to build a model from scratch using the power world commands seems like a recipe for disaster
	   
	   // save the cf file....
	   CDFFile modelFile = new CDFFile();
	   String filename = getUniqueName();

	   PowerworldModel pModel = null;
	   try {
      modelFile.saveFile(filename, model);
      // create a powerworld model
      PowerworldModelFile pModelFile = new PowerworldModelFile();
      pModel = (PowerworldModel) pModelFile.readModel(filename);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
	  	   
	  // sync up the legacy ids... unnecessary when the global registry system is in place, but that will go away.. the cdf and power world ids should end up matching....
	  for (Asset asset : model.getAssets()) {
	    if (asset.getAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY) == null) {
	      asset.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY, asset.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY));
	    }
	  }
	   	   
	  return pModel;	   
	}

	/**
	 * Basically creates an openDSS model state when the old model was an openDSS model
	 * @param state
	 * @return
	 */
	private PowerworldModel constructPowerworldModel(PowerworldModel model) {		
		return model;
	}
	
	/**
	 * Get a random number associated with this instance
	 * @return
	 */
	private synchronized String getUniqueName() {
	  String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
	  int uniqueName = 0;
	  String filename = uniqueName + "-" + processName + ".cf";
	  while (new File(filename).exists()) {
	    ++uniqueName;
	    filename = uniqueName + "-" + processName + ".cf";
	  }
	  return filename;
	}

	
}
