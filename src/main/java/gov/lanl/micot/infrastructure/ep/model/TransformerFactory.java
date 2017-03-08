package gov.lanl.micot.infrastructure.ep.model;

import java.util.Vector;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;
import gov.lanl.micot.util.geometry.LineImpl;
import gov.lanl.micot.util.geometry.Point;

/**
 * Interface for creating line factories
 * @author Russell Bent
 */
public abstract class TransformerFactory {

  private AssetRegistry<Transformer> registry = null;
  
  protected static final double DEFAULT_CONTROL_MAX = 1.02;
  protected static final double DEFAULT_CONTROL_MIN = 0.97;
  protected static final TransformerControlModeEnum  DEFAULT_CONTROL_SIDE = TransformerControlModeEnum.CONTROL_BUS_IS_TERMINAL ;
  protected static final double DEFAULT_MAX_TAP_ANGLE = 1.1;
  protected static final double DEFAULT_MIN_TAP_ANGLE = 0.9;
  protected static final double DEFAULT_TAP_ANGLE = 0;
  protected static final double DEFAULT_TAP_RATIO = 1.01;
  protected static final TransformerTypeEnum DEFAULT_TYPE = TransformerTypeEnum.TRANSMISSION_FIXED_ANGLE_FIXED_TAP_TYPE;
  protected static final double DEFAULT_STEP_SIZE = 0.00625;
  protected static final String DEFAULT_BRANCH_RATING = "M";

  
  protected TransformerFactory() {
    registry = new AssetRegistry<Transformer>();
  }
  
	/**
	 * Creates a transformer from a transmission corridor and some parameters
	 * @param corridor
	 * @param parameters
	 * @return
	 */
	public Transformer createTransformer(ElectricPowerNode node1, ElectricPowerNode node2, double reactance, double resistance, double charging, double capacityRating, Object circuit) {
	  Transformer transformer = createEmptyTransformer(node1.getBus(), node2.getBus(), circuit);
	  
    double controlMax = DEFAULT_CONTROL_MAX;
    double controlMin = DEFAULT_CONTROL_MIN;
    TransformerControlModeEnum controlSide = DEFAULT_CONTROL_SIDE;
    double maxTapAngle = DEFAULT_MAX_TAP_ANGLE;
    double minTapAngle = DEFAULT_MIN_TAP_ANGLE;
    double tapAngle = DEFAULT_TAP_ANGLE;
    double tapRatio = DEFAULT_TAP_RATIO;
    TransformerTypeEnum type = DEFAULT_TYPE;
    double stepSize = DEFAULT_STEP_SIZE;
    
    transformer.setAttribute(Transformer.CIRCUIT_KEY, circuit);
    transformer.setResistance(resistance);
    transformer.setReactance(reactance);
    transformer.setLineCharging(charging);
    transformer.setCapacityRating(capacityRating);
    transformer.setShortTermEmergencyCapacityRating(capacityRating);
    transformer.setLongTermEmergencyCapacityRating(capacityRating);
    transformer.setDesiredStatus(true);
    transformer.setAttribute(Transformer.TYPE_KEY, type);
    transformer.setAttribute(Transformer.TAP_RATIO_KEY, tapRatio);
    transformer.setAttribute(Transformer.TAP_ANGLE_KEY, tapAngle);
    transformer.setAttribute(Transformer.MIN_TAP_ANGLE_KEY, minTapAngle);
    transformer.setAttribute(Transformer.MAX_TAP_ANGLE_KEY, maxTapAngle);
    transformer.setAttribute(Transformer.STEP_SIZE_KEY, stepSize);
    transformer.setAttribute(Transformer.CONTROL_MIN_KEY, controlMin);
    transformer.setAttribute(Transformer.CONTROL_MAX_KEY, controlMax);
    transformer.setAttribute(Transformer.CONTROL_SIDE_KEY, controlSide);   
    transformer.setActualStatus(transformer.getDesiredStatus());

    Vector<Point> points = new Vector<Point>();
    points.add(node1.getBus().getCoordinate());
    points.add(node2.getBus().getCoordinate());
    transformer.setCoordinates(new LineImpl(points.toArray(new Point[0])));
    return transformer;    
	}

	/**
	 * create an empty transformer
	 * @param bus1
	 * @param bus2
	 * @param circuit
	 * @return
	 */
	protected abstract Transformer createEmptyTransformer(Bus bus1, Bus bus2, Object circuit);
	
  /**
   * Creates a new line between buses
   * @param bus
   * @return
   */
  public Transformer createNewTransformer() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Transformer transformer = new TransformerImpl(assetId);
    registry.register(assetId, transformer);
    return registry.getCopy(assetId);
  }
  
  /**
   * Does the legacy id exist
   * @param legacyTag
   * @param key
   * @return
   */
  protected boolean doesLegacyExist(String legacyTag, Object key) {
    return registry.doesLegacyExist(legacyTag, key);    
  }

  /**
   * Register the legacy id
   * @param legacyTag
   * @param key
   * @param asset
   */
  protected void registerLegacy(String legacyTag, Object key, Transformer asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Transformer getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }


}
