package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.Compressor;
import gov.lanl.micot.infrastructure.naturalgas.model.FlowUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.HeatTransferCoefficientUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.LengthUnitEnum;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.PipeFactory;
import gov.lanl.micot.infrastructure.naturalgas.model.PressureUnitEnum;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.io.xml.XMLElement;

import java.util.Vector;

/**
 * A factory for generating pipes
 * 
 * @author Russell Bent
 * 
 */
public class GaslibPipeFactory extends PipeFactory {

  private static final String LEGACY_TAG = "GASLIB";

  /**
   * Singleton constructor
   */
  protected GaslibPipeFactory() {
  }

  /**
   * Creates a line and data from a line
   * 
   * @param line
   * @return
   */
  public Pipe createPipe(XMLElement element, Junction fromJcn, Junction toJcn) {
    String legacyid = element.getValue(GaslibModelConstants.CONNECTION_ID_TAG);
    Pipe pipe = registerPipe(legacyid);
    pipe.setAttribute(Compressor.NAME_KEY, legacyid);
        
    // flow min and maxes
    XMLElement flowMinElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MIN_TAG);
    String flowMin = flowMinElement.getValue(GaslibModelConstants.VALUE_TAG);
    String flowUnit = flowMinElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setAttribute(Pipe.FLOW_MIN_KEY, Double.parseDouble(flowMin));    
    XMLElement flowMaxElement = element.getElement(GaslibModelConstants.CONNECTION_FLOW_MAX_TAG);
    String flowMax = flowMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    pipe.setAttribute(Pipe.FLOW_MAX_KEY, Double.parseDouble(flowMax));   
    if (flowUnit.equals(GaslibModelConstants.FLOW_M_CUBED_PER_HOUR_CONSTANT)) {
      pipe.setAttribute(Pipe.FLOW_UNIT_KEY, FlowUnitEnum.M_CUBED_PER_HOUR_TYPE);
    }
    else {
      System.err.println("Error: flow constant " + flowUnit);
    }

    // length attribute
    XMLElement lengthElement = element.getElement(GaslibModelConstants.CONNECTION_LENGTH_TAG);
    String length = lengthElement.getValue(GaslibModelConstants.VALUE_TAG);
    String lengthUnit = lengthElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setLength(Double.parseDouble(length));
    if (lengthUnit.equals(GaslibModelConstants.KM_CONSTANT)) {
      pipe.setAttribute(Pipe.LENGTH_UNIT_KEY, LengthUnitEnum.KM_TYPE);
    }
    else {
      System.err.println("Error: length constant " + lengthUnit);
    }

    // diameter attribute
    XMLElement diameterElement = element.getElement(GaslibModelConstants.CONNECTION_DIAMETER_TAG);
    String diameter = diameterElement.getValue(GaslibModelConstants.VALUE_TAG);
    String diameterUnit = diameterElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setDiameter(Double.parseDouble(diameter));
    if (diameterUnit.equals(GaslibModelConstants.MM_CONSTANT)) {
      pipe.setAttribute(Pipe.DIAMETER_UNIT_KEY, LengthUnitEnum.MM_TYPE);
    }
    else {
      System.err.println("Error: diameter constant " + diameterUnit);
    }
    
    // roughness attribute
    XMLElement roughnessElement = element.getElement(GaslibModelConstants.CONNECTION_ROUGHNESS_TAG);
    String roughness = roughnessElement.getValue(GaslibModelConstants.VALUE_TAG);
    String roughnessUnit = roughnessElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setAttribute(Pipe.ROUGHNESS_KEY,Double.parseDouble(roughness));
    if (roughnessUnit.equals(GaslibModelConstants.MM_CONSTANT)) {
      pipe.setAttribute(Pipe.ROUGHNESS_UNIT_KEY, LengthUnitEnum.MM_TYPE);
    }
    else {
      System.err.println("Error: roughness constant " + diameterUnit);
    }

    // pressure max attribute
    XMLElement pressureMaxElement = element.getElement(GaslibModelConstants.CONNECTION_PRESSURE_MAX_TAG);
    String pressureMax = pressureMaxElement.getValue(GaslibModelConstants.VALUE_TAG);
    String pressureMaxUnit = pressureMaxElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setAttribute(Pipe.PRESSURE_MAX_KEY,Double.parseDouble(pressureMax));
    if (pressureMaxUnit.equals(GaslibModelConstants.BAR_PRESSURE_CONSTANT)) {
      pipe.setAttribute(Pipe.PRESSURE_MAX_UNIT_KEY, PressureUnitEnum.BAR_TYPE);
    }
    else {
      System.err.println("Error: pressure max constant " + diameterUnit);
    }

    // heat transfer coefficient attribute
    XMLElement heatCoeffElement = element.getElement(GaslibModelConstants.CONNECTION_HEAT_TRANSFER_COEFFICIENT_TAG);
    String heatCoeff = heatCoeffElement.getValue(GaslibModelConstants.VALUE_TAG);
    String heatCoeffUnit = heatCoeffElement.getValue(GaslibModelConstants.UNIT_TAG);
    pipe.setAttribute(Pipe.HEAT_TRANSFER_COEFFICIENT_KEY,Double.parseDouble(heatCoeff));
    if (heatCoeffUnit.equals(GaslibModelConstants.W_PER_M_SQUARE_PER_K_CONSTANT)) {
      pipe.setAttribute(Pipe.HEAT_TRANSFER_COEFFICIENT_UNIT_KEY, HeatTransferCoefficientUnitEnum.W_PER_M_SQUARE_PER_K_TYPE);
    }
    else {
      System.err.println("Error: heat transfer constant " + heatCoeffUnit);
    }
        
    pipe.setResistance(-Double.MAX_VALUE);
    pipe.setFlow(0.0);
    pipe.setCapacity(Double.MAX_VALUE);
    pipe.setActualStatus(true);
    pipe.setDesiredStatus(true);

    Vector<Point> points = new Vector<Point>();
    points.add(fromJcn.getCoordinate());
    points.add(toJcn.getCoordinate());
    pipe.setCoordinates(new LineImpl(points));

    return pipe;
  }
  
  
  
  
  
  /**
   * Creates an ieiss line from another line
   * 
   * @param line
   * @param id
   * @param id2
   * @return
   */
  public void updatePipe(Pipe pipe, Junction id1, Junction id2, String legacyId) {
    if (pipe.getAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY) == null) {
//      String legacyId = findUnusedId();
      pipe.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, pipe);
    }
  }

  /**
   * Create a line with a particular id
   * 
   * @param link
   * @param data
   * @param makeCircuitId
   * @return
   */
 // protected Pipe createParallelPipe(Pipe link) {  
   // String legacyId =  findUnusedId();
   // Pipe pipe = registerPipe(legacyId);    
   // pipe.setDiameter(link.getDiameter());
   // pipe.setLength(link.getLength());
   // pipe.setResistance(link.getResistance());
   // pipe.setCoordinates(link.getCoordinates());
   // return pipe;
  //}

  /**
   * Register the pipe
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Pipe registerPipe(String legacyId) {
    Pipe pipe = getLegacy(LEGACY_TAG, legacyId);
    if (pipe == null) {
      pipe = createNewPipe();
      pipe.addOutputKey(GaslibModelConstants.GASLIB_LEGACY_ID_KEY);
      pipe.setAttribute(GaslibModelConstants.GASLIB_LEGACY_ID_KEY, legacyId);
      registerLegacy(LEGACY_TAG, legacyId, pipe);
    }
    return pipe;
  }
  
  /**
   * Find an unused id number
   * 
   * @return
   */
 // private String findUnusedId() {
  //  GaslibCompressorFactory factory = GaslibCompressorFactory.getInstance();
    //for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      //if (getLegacy(LEGACY_TAG, i+"") == null && factory.getLegacy(LEGACY_TAG, i+"") == null) {
        //return i+"";
      //}
   // }
    //throw new RuntimeException("Error: Cannot find an unused id");
  //}

  @Override
  protected Pipe getLegacy(String legacyTag, Object key) {
    return super.getLegacy(legacyTag, key);
  }
  
}
