package gov.lanl.micot.infrastructure.ep.model;

import java.util.Vector;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;
import gov.lanl.micot.util.geometry.Point;

/**
 * Interface for creating line factories
 * @author Russell Bent
 */
public abstract class LineFactory {

	private AssetRegistry<Line> registry = null;

	protected LineFactory() {
		registry = new AssetRegistry<Line>();
	}

	/**
	 * Creates a line from a transformer
	 * @param transformer
	 * @param state
	 * @return
	 */
	public Line createLine(Transformer transformer, Bus fromBus, Bus toBus) {
		Line line = createEmptyLine(fromBus, toBus, transformer);
		line.setAttribute(Line.CIRCUIT_KEY, transformer.getCircuit());
		line.setResistance(transformer.getResistance());
		line.setReactance(transformer.getReactance());
		line.setLineCharging(transformer.getLineCharging());
		line.setCapacityRating(transformer.getCapacityRating());
		line.setShortTermEmergencyCapacityRating(transformer.getShortTermEmergencyCapacityRating());
		line.setLongTermEmergencyCapacityRating(transformer.getLongTermEmergencyCapacityRating());
		line.setDesiredStatus(transformer.getDesiredStatus());
		line.setActualStatus(line.getDesiredStatus());
		line.setCoordinates(transformer.getCoordinates());
		return line;	  
	}

	/**
	 * Create an empty line to populate with data
	 * @param fromBus
	 * @param toBus
	 * @return
	 */
	protected abstract Line createEmptyLine(Bus fromBus, Bus toBus, Transformer transformer);

	/**
	 * Create an empty line to populate with data
	 * @param fromBus
	 * @param toBus
	 * @return
	 */
	protected abstract Line createEmptyLine(Bus fromBus, Bus toBus, Object circuit);

	/**
	 * Creates a line from a transmission corridor and some parameters
	 * @param corridor
	 * @param parameters
	 * @return
	 */
	public Line createLine(ElectricPowerNode node1, ElectricPowerNode node2, double reactance, double resistance, double charging, double capacityRating, Object circuit) {
		Line line = createEmptyLine(node1.getBus(), node2.getBus(), circuit);
		line.setAttribute(Line.CIRCUIT_KEY, circuit);
		line.setResistance(resistance);
		line.setReactance(reactance);
		line.setLineCharging(charging);
		line.setCapacityRating(capacityRating);
		line.setShortTermEmergencyCapacityRating(capacityRating);
		line.setLongTermEmergencyCapacityRating(capacityRating);
		line.setDesiredStatus(true);
		line.setActualStatus(true);

		Vector<Point> points = new Vector<Point>();
		points.add(node1.getBus().getCoordinate());
		points.add(node2.getBus().getCoordinate());
		line.setCoordinates(new gov.lanl.micot.util.geometry.LineImpl(points.toArray(new Point[0])));
		return line;
	}
	
	/**
	 * Creates a line from some data...
	 * @param node1
	 * @param node2
	 * @param reactance
	 * @param resistance
	 * @param charging
	 * @param capacityRating
	 * @param As
	 * @param deltaTheta
	 * @param forwardTrl
	 * @param forwardTrb
	 * @param backwardTrl
	 * @param backwardTrb
	 * @param circuit
	 * @return
	 */
	public Line createLine(ElectricPowerNode node1, ElectricPowerNode node2, double reactance, double resistance, double charging, double capacityRating, double deltaTheta, Object circuit) {
		Line line = createEmptyLine(node1.getBus(), node2.getBus(), circuit);

		line.setAttribute(Line.CIRCUIT_KEY, circuit);
		line.setAttribute(Line.PHASE_ANGLE_DIFFERENCE_LIMIT_KEY, deltaTheta);
		line.setResistance(resistance);
		line.setReactance(reactance);
		line.setLineCharging(charging);
		line.setCapacityRating(capacityRating);
		line.setShortTermEmergencyCapacityRating(capacityRating);
		line.setLongTermEmergencyCapacityRating(capacityRating);
		line.setDesiredStatus(true);
		line.setActualStatus(true);

		Vector<Point> points = new Vector<Point>();
		points.add(node1.getBus().getCoordinate());
		points.add(node2.getBus().getCoordinate());
		line.setCoordinates(new gov.lanl.micot.util.geometry.LineImpl(points.toArray(new Point[0])));
		
		return line;
	}

	/**
	 * Creates a new line between buses
	 * @param bus
	 * @return
	 */
	public Line createNewLine() {
		AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
		long assetId = modifier.getNextId();
		Line line = new LineImpl(assetId);
		registry.register(assetId, line);
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
	protected void registerLegacy(String legacyTag, Object key, Line asset) {
		registry.registerLegacy(legacyTag, key, asset);
	} 

	/**
	 * Get the legacy
	 * @param legacyTag
	 * @param key
	 * @return
	 */
	protected Line getLegacy(String legacyTag, Object key) {
		return registry.getLegacyCopy(legacyTag, key);
	}
}