package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.CalorificUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.MolarMassUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.NormDensityUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.PressureUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.TemperatureUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.model.WellFactory;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.xml.XMLElement;

/**
 * Factory for creating wells
 * 
 * @author Russell Bent
 */
public class GaslibWellFactory extends WellFactory {

  private static final String LEGACY_TAG = "GASLIB";
  
  /**
   * Singleton constructor
   */
  protected GaslibWellFactory() {
  }

  /**
   * Creates a generator and data from a generator
   * 
   * @param line
   * @return
   */
  public Well createWell(XMLElement element, Junction junction) {
    String legacyid = element.getValue(GaslibModelConstants.NODE_ID_TAG);

    String longitude = element.getValue(GaslibModelConstants.NODE_LONGITUDE_TAG);
    String latitude = element.getValue(GaslibModelConstants.NODE_LATITUDE_TAG);
    double y = Double.parseDouble(latitude);
    double x = Double.parseDouble(longitude);

    XMLElement flowMinElement = element.getElement(GaslibModelConstants.NODE_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);

    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.NODE_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    
    double minProduction = Double.parseDouble(flowMin);
    double maxProduction = Double.parseDouble(flowMax);
    double production = maxProduction;
        
    Well well = registerWell(legacyid);
    initializeWell(well, junction, production);
    well.setAttribute(Compressor.NAME_KEY, legacyid);
        
    well.setActualProduction(production);
    well.setStatus(true);
    well.setCoordinate(new PointImpl(x,y));
    well.setDesiredProduction(production);
    well.setMaximumProduction(maxProduction);
    well.setMinimumProduction(minProduction);
    
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      well.setAttribute(Well.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: unknown flow unit constant " + flowUnit);
    }

    XMLElement gasTemperatureElement = element.getElement(GaslibModelConstants.NODE_GAS_TEMPERATURE_TAG);
    String temperature = gasTemperatureElement.getValue(GaslibModelConstants.VALUE_TAG);
    String tempUnit = gasTemperatureElement.getValue(GaslibModelConstants.UNIT_TAG);
    well.setAttribute(Well.TEMPERATURE_KEY, Double.parseDouble(temperature));
    if (tempUnit.equals(GaslibModelConstants.CELSIUS_CONSTANT)) {
      well.setAttribute(Well.TEMPERATURE_UNIT_KEY, TemperatureUnitEnum.CELSIUS_TYPE);
    }
    else {
      System.err.println("Error: temperature constant " + tempUnit);
    }

    XMLElement calorificElement = element.getElement(GaslibModelConstants.NODE_CALORIFIC_TAG);
    String calorific = calorificElement.getValue(GaslibModelConstants.VALUE_TAG);
    String calUnit = calorificElement.getValue(GaslibModelConstants.UNIT_TAG);
    well.setAttribute(Well.CALORIFIC_KEY, Double.parseDouble(calorific));
    if (calUnit.equals(GaslibModelConstants.M3_PER_M_CUBE_CONSTANT)) {
      well.setAttribute(Well.CALORIFIC_UNIT_KEY, CalorificUnitEnum.M3_PER_M_CUBE_TYPE);
    }
    else {
      System.err.println("Error: calorific constant " + tempUnit);
    }

    XMLElement densityElement = element.getElement(GaslibModelConstants.NODE_NORM_DENSITY_TAG);
    String normDensity = densityElement.getValue(GaslibModelConstants.VALUE_TAG);
    String normUnit = densityElement.getValue(GaslibModelConstants.UNIT_TAG);
    well.setAttribute(Well.NORM_DENSITY_KEY, Double.parseDouble(normDensity));
    if (normUnit.equals(GaslibModelConstants.KG_PER_M_CUBE_CONSTANT)) {
      well.setAttribute(Well.NORM_DENSITY_UNIT_KEY, NormDensityUnitEnum.KG_PER_M_BUBE_TYPE);
    }
    else {
      System.err.println("Error: norm density constant " + tempUnit);
    }

    XMLElement molarMassElement = element.getElement(GaslibModelConstants.NODE_MOLAR_MASS_TAG);
    String molarMass = molarMassElement.getValue(GaslibModelConstants.VALUE_TAG);
    String massUnit = molarMassElement.getValue(GaslibModelConstants.UNIT_TAG);
    well.setAttribute(Well.MOLAR_MASS_KEY, Double.parseDouble(molarMass));
    if (massUnit.equals(GaslibModelConstants.KG_PER_KMOL_CONSTANT)) {
      well.setAttribute(Well.MOLAR_MASS_UNIT_KEY, MolarMassUnitEnum.KG_PER_KMOL_TYPE);
    }
    else {
      System.err.println("Error: cmolar mass constant " + tempUnit);
    }

    XMLElement pseudoCriticalPressureElement = element.getElement(GaslibModelConstants.NODE_PSEUDO_CRITICAL_PRESSURE_TAG);
    String pseudoCriticalPressure = pseudoCriticalPressureElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pseudoCriticalPressureUnit = pseudoCriticalPressureElement.getValue(GaslibModelConstants.UNIT_TAG);
    well.setAttribute(Well.PSEUDO_CRITICAL_PRESSURE_KEY, Double.parseDouble(pseudoCriticalPressure));
    if (pseudoCriticalPressureUnit.equals(GaslibModelConstants.BAR_PRESSURE_CONSTANT)) {
      well.setAttribute(Well.PSEUDO_CRITICAL_PRESSURE_UNIT_KEY, PressureUnitEnum.BAR_TYPE);
    }
    else {
      System.err.println("Error: pseudo pressure constant " + tempUnit);
    }

    XMLElement pseudoCriticalTemperatureElement = element.getElement(GaslibModelConstants.NODE_PSEUDO_CRITICAL_TEMPERATURE_TAG);
    String pseudoCriticalTemperature = pseudoCriticalTemperatureElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pseudoCriticalTemperatureUnit = pseudoCriticalTemperatureElement.getValue(GaslibModelConstants.UNIT_TAG);
    well.setAttribute(Well.PSEUDO_CRITICAL_TEMPERATURE_KEY, Double.parseDouble(pseudoCriticalTemperature));
    if (pseudoCriticalTemperatureUnit.equals(GaslibModelConstants.KELVIN_CONSTANT)) {
      well.setAttribute(Well.PSEUDO_CRITICAL_TEMPERATURE_UNIT_KEY, TemperatureUnitEnum.KELVIN_TYPE);
    }
    else {
      System.err.println("Error: pseudo temperature constant " + tempUnit);
    }

    XMLElement heatCapacityAElement = element.getElement(GaslibModelConstants.NODE_HEAT_CAPACITY_A_TAG);
    String heatCapacityA = heatCapacityAElement.getValue(GaslibModelConstants.VALUE_TAG);
    well.setAttribute(Well.HEAT_CAPACITY_A_KEY, Double.parseDouble(heatCapacityA));

    XMLElement heatCapacityBElement = element.getElement(GaslibModelConstants.NODE_HEAT_CAPACITY_B_TAG);
    String heatCapacityB = heatCapacityBElement.getValue(GaslibModelConstants.VALUE_TAG);
    well.setAttribute(Well.HEAT_CAPACITY_B_KEY, Double.parseDouble(heatCapacityB));

    XMLElement heatCapacityCElement = element.getElement(GaslibModelConstants.NODE_HEAT_CAPACITY_C_TAG);
    String heatCapacityC = heatCapacityCElement.getValue(GaslibModelConstants.VALUE_TAG);
    well.setAttribute(Well.HEAT_CAPACITY_C_KEY, Double.parseDouble(heatCapacityC));

    return well;
  }

  /**
   * Register the well
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Well registerWell(String legacyId) {
    Well well = getLegacy(LEGACY_TAG, legacyId);
    if (well == null) {
      well = createNewWell();
      well.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      well.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, well);
    }
    return well;
  }

  
}
