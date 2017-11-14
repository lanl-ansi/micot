package gov.lanl.micot.infrastructure.ep.model.powerworld;

import java.util.ArrayList;

import gov.lanl.micot.infrastructure.ep.io.powerworld.PowerworldIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.dcom.ComDataObject;
import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.math.PolynomialFunctionFactory;

/**
 * Factory for creating generators
 * 
 * @author Russell Bent
 */
public class PowerworldGeneratorFactory extends GeneratorFactory {

  private static final String LEGACY_TAG = "Powerworld";
    
  /**
   * Singleton constructor
   */
  protected PowerworldGeneratorFactory() {
  }

  /**
   * Creates a generator and data from a generator
   * 
   * @param line
   * @return
   */
  public Generator createGenerator(ComObject powerworld, Bus bus, Pair<Integer, String> id) {
    String fields[] = new String[]{PowerworldIOConstants.BUS_NUM, PowerworldIOConstants.GEN_NUM, PowerworldIOConstants.GEN_FUEL_TYPE, 
        PowerworldIOConstants.GEN_COST_CONSTANT, PowerworldIOConstants.GEN_COST_LINEAR, PowerworldIOConstants.GEN_COST_SQUARE, 
        PowerworldIOConstants.GEN_COST_CUBE, PowerworldIOConstants.GEN_MVA_BASE, PowerworldIOConstants.GEN_MVAR, PowerworldIOConstants.GEN_MW, 
        PowerworldIOConstants.GEN_MVAR_MAX, PowerworldIOConstants.GEN_MW_MAX, PowerworldIOConstants.GEN_MVAR_MIN, PowerworldIOConstants.GEN_MW_MIN, 
        PowerworldIOConstants.GEN_STATUS, PowerworldIOConstants.GEN_VOLTAGE, PowerworldIOConstants.GEN_FUEL_COST}; 
    String values[] = new String[] {id.getLeft()+"", id.getRight()+"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
        
    ComDataObject dataObject = powerworld.callData(PowerworldIOConstants.GET_PARAMETERS_SINGLE_ELEMENT, PowerworldIOConstants.GENERATOR, fields, values);
    ArrayList<ComDataObject> genData = dataObject.getArrayValue();
    String errorString = genData.get(0).getStringValue();
    if (!errorString.equals("")) {
      System.err.println("Error getting powerworld generator data: " + errorString);                
    }

    ArrayList<ComDataObject> gData = genData.get(1).getArrayValue();                       
    String fuelTypeString = gData.get(2).getStringValue();
    String constantCost = gData.get(3).getStringValue();
    String linearCost = gData.get(4).getStringValue();
    String squareCost = gData.get(5).getStringValue();
    String cubeCost = gData.get(6).getStringValue();
    String mvaBaseString = gData.get(7).getStringValue();
    String mvarString = gData.get(8).getStringValue();
    String mwString = gData.get(9).getStringValue();
    String mvarMaxString = gData.get(10).getStringValue();
    String mwMaxString = gData.get(11).getStringValue();
    String mvarMinString = gData.get(12).getStringValue();
    String mwMinString = gData.get(13).getStringValue();
    String statusString = gData.get(14).getStringValue();
    String voltageString = gData.get(15).getStringValue();
    String fuelCostString = gData.get(15).getStringValue();

    FuelTypeEnum fuelType = getFuelType(fuelTypeString);
    
    Point point = bus.getCoordinate();
    double reactiveMax = Double.parseDouble(mvarMaxString.trim());
    double reactiveMin = Double.parseDouble(mvarMinString.trim());
    double realMax = Double.parseDouble(mwMaxString.trim());
    double realMin = Double.parseDouble(mwMinString.trim());
    double remoteVoltage = Double.parseDouble(voltageString.trim());
    double mvaBase = Double.parseDouble(mvaBaseString.trim());
    double mvar = Double.parseDouble(mvarString.trim());
    double mw = Double.parseDouble(mwString.trim());
    double fuelCost = Double.parseDouble(fuelCostString.trim());
        
    ArrayList<Double> coefficients = new ArrayList<Double>();
    coefficients.add(Double.parseDouble((constantCost)) * fuelCost);
    coefficients.add(Double.parseDouble((linearCost)) * fuelCost);
    coefficients.add(Double.parseDouble((squareCost)) * fuelCost);
    coefficients.add(Double.parseDouble((cubeCost)) * fuelCost);
    
    Generator generator = registerGenerator(id, bus);    
    initializeGenerator(generator, bus, realMax, reactiveMax, realMin, reactiveMin, point);
  
    // backwards way of doing this, but this is how the data is stored...
    bus.setRemoteVoltagePU(remoteVoltage);
    
    generator.setType(GeneratorTypeEnum.HOLD_VOLTAGE_TYPE);    
    generator.setStatus(statusString.equalsIgnoreCase(PowerworldIOConstants.GEN_CLOSED));
    generator.setAttribute(Generator.FUEL_TYPE_KEY, fuelType);
    generator.setAttribute(Generator.MVA_BASE_KEY, mvaBase);
    generator.setActualRealGeneration(mw);
    generator.setDesiredRealGeneration(mw);
    generator.setActualReactiveGeneration(mvar);
    generator.setDesiredReactiveGeneration(mvar);
        
    generator.setAttribute(Generator.ECONOMIC_COST_KEY, PolynomialFunctionFactory.getInstance().createDefaultPolynomialFunction(coefficients));

    String powerworldBuscat = bus.getAttribute(PowerworldModelConstants.POWERWORLD_BUS_CATEGORY_KEY, String.class);
    if (powerworldBuscat.toLowerCase().startsWith(PowerworldIOConstants.BUS_PV))     {
      generator.setType(GeneratorTypeEnum.HOLD_GENERATION_TYPE);    
    }
    else if (powerworldBuscat.toLowerCase().startsWith(PowerworldIOConstants.BUS_PQ))     {
      generator.setType(GeneratorTypeEnum.HOLD_VOLTAGE_TYPE);    
    }
    else if (powerworldBuscat.toLowerCase().startsWith(PowerworldIOConstants.BUS_SLACK_STRING))     {
      generator.setType(GeneratorTypeEnum.REFERENCE_BUS_TYPE);    
    }
    return generator;    
  }

  /**
  * Register the load
  * @param legacyId
  * @param bus
  * @return
  */
  private Generator registerGenerator(Pair<Integer, String> legacyId, Bus bus) {
    Generator generator = getLegacy(LEGACY_TAG, legacyId);
    if (generator == null) {
      generator = createNewGenerator();
      generator.setAttribute(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY,legacyId);
      generator.addOutputKey(PowerworldModelConstants.POWERWORLD_LEGACY_ID_KEY);      
      registerLegacy(LEGACY_TAG, legacyId, generator);
    }
   return generator;
 }
  
  /**
   * Maps powerworld fuel types to our fuel types
   * @param string
   * @return
   */
  private FuelTypeEnum getFuelType(String string) {
    if (string.equalsIgnoreCase(PowerworldIOConstants.COAL)) {
      return FuelTypeEnum.COAL;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.NATURAL_GAS)) {
      return FuelTypeEnum.NATURAL_GAS;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.HYDRO)) {
      return FuelTypeEnum.HYDRO;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.NUCLEAR)) {
      return FuelTypeEnum.NUCLEAR;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.SOLAR)) {
      return FuelTypeEnum.SOLAR;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.WIND)) {
      return FuelTypeEnum.WIND;
    }
    if (string.toLowerCase().contains(PowerworldIOConstants.OIL)) {
      return FuelTypeEnum.OIL;
    }
    if (string.toLowerCase().contains(PowerworldIOConstants.DIESEL)) {
      return FuelTypeEnum.DIESEL;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.GEOTHERMAL)) {
      return FuelTypeEnum.GEOTHERMOL;
    }
    if (string.equalsIgnoreCase(PowerworldIOConstants.BIOMASS)) {
      return FuelTypeEnum.BIOMASS;
    }
    return FuelTypeEnum.UNKNOWN;
  }
  
}
