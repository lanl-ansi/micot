package gov.lanl.micot.infrastructure.ep.io.powerworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModel;
import gov.lanl.micot.infrastructure.ep.model.powerworld.PowerworldModelFactory;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.io.dcom.ComObjectFactory;
import gov.lanl.micot.util.io.dcom.ComObjectUtilities;

/**
 * File for creating power world model files
 * 
 * @author Russell Bent
 */
public class PowerworldModelFile implements ElectricPowerModelFile {

  /**
   * Constructor
   */
  public PowerworldModelFile() {
  }

  @Override
  public  void saveFile(String filename, ElectricPowerModel model) throws IOException {
    PowerworldModel tempModel = PowerworldModelFactory.getInstance().constructModel(model);
    ComObject powerWorldModel = tempModel.getPowerworld();
    try {
      ComDataObject text = powerWorldModel.callData(PowerworldIOConstants.SAVE_CASE, filename, PowerworldIOConstants.PWB, true); 
      ArrayList<ComDataObject> data = text.getArrayValue();
      if (!data.get(0).getStringValue().equals("")) {
        System.err.println("Error saving file: " + data.get(0).getStringValue());
      }      
    }
    catch (Throwable e) {
      e.printStackTrace();
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Customized export of data in matpower format
   * @param filename
   * @param model
   * @throws IOException
   */
  public  void saveMatPowerFile(String filename, ElectricPowerModel model) throws IOException {
    if (!filename.contains(":")) {
      System.err.println("Warning: The matpower export file name " + filename + " does not appear to be a full path.  The save may fail");
    }
    
    PowerworldModel tempModel = PowerworldModelFactory.getInstance().constructModel(model);
    ComObject powerWorldModel = tempModel.getPowerworld();    
    try {
  //    String scriptCommand = "SaveCase(\"" + filename + "\",\"MatPower\")";
      //ComDataObject text = powerWorldModel.callData("RunScriptCommand", scriptCommand); 
      ComDataObject text = powerWorldModel.callData(PowerworldIOConstants.SAVE_CASE, filename, PowerworldIOConstants.MATPOWER, true); 
      ArrayList<ComDataObject> data = text.getArrayValue();
      if (!data.get(0).getStringValue().equals("")) {
        System.err.println("Error saving file: " + data.get(0).getStringValue());
      }      
    }
    catch (Throwable e) {
      e.printStackTrace();
      throw new IOException(e.getMessage());
    }
  }
  
  /**
   * Customized export of data in PTI23 format
   * @param filename
   * @param model
   * @throws IOException
   */
  public void savePTI23File(String filename, ElectricPowerModel model) throws IOException {
    PowerworldModel tempModel = PowerworldModelFactory.getInstance().constructModel(model);
    ComObject powerWorldModel = tempModel.getPowerworld();
    try {
      ComDataObject text = powerWorldModel.callData(PowerworldIOConstants.SAVE_CASE, filename, PowerworldIOConstants.PTI23, true); 
      ArrayList<ComDataObject> data = text.getArrayValue();
      if (!data.get(0).getStringValue().equals("")) {
        System.err.println("Error saving file: " + data.get(0).getStringValue());
      }      
    }
    catch (Throwable e) {
      e.printStackTrace();
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Customized export of data in PTI33 format
   * @param filename
   * @param model
   * @throws IOException
   */
  public void savePTI33File(String filename, ElectricPowerModel model) throws IOException {
    PowerworldModel tempModel = PowerworldModelFactory.getInstance().constructModel(model);
    ComObject powerWorldModel = tempModel.getPowerworld();
    try {
      ComDataObject text = powerWorldModel.callData(PowerworldIOConstants.SAVE_CASE, filename, PowerworldIOConstants.PTI33, true); 
      ArrayList<ComDataObject> data = text.getArrayValue();
      if (!data.get(0).getStringValue().equals("")) {
        System.err.println("Error saving file: " + data.get(0).getStringValue());
      }      
    }
    catch (Throwable e) {
      e.printStackTrace();
      throw new IOException(e.getMessage());
    }
  }
  

  @Override
  public ElectricPowerModel readModel(String filename) throws IOException {
    // safety check on relative paths....
    if (!filename.contains(":")) {
      filename = System.getProperty("user.dir") + File.separatorChar + filename;
    }
        
    ComObjectFactory factory = ComObjectUtilities.getDefaultFactory();
    ComObject model = factory.createComObject(PowerworldIOConstants.POWERWORLD);
    ComDataObject text = model.callData(PowerworldIOConstants.OPEN_CASE, filename);    
    
    ArrayList<ComDataObject> data = text.getArrayValue();
    if (!data.get(0).getStringValue().equals("")) {
      System.err.println("Error reading file: " + data.get(0).getStringValue());
    }      
      
    PowerworldModel powerWorldModel = new PowerworldModel(model);
    return powerWorldModel;
  }

  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename, (ElectricPowerModel) model);
  }

 
}
