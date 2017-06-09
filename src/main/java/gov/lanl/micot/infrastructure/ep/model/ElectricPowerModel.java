package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.collection.Pair;

import java.util.Collection;
import java.util.Set;

/**
 * An interface for a model.  A model is intended to encapsulate 
 * the components and topology of a model
 * @author Russell Bent
 */
public interface ElectricPowerModel extends Model, Cloneable {
	
  public double DEFAULT_MVA_BASE = 100.0;
  
	/**
	 * Add a listener to the model
	 * @param listener
	 */
	public void addModelListener(ElectricPowerModelListener listener);
	
	/**
	 * Remove a listener from the model
	 * @param listener
	 */
	public void removeModelListener(ElectricPowerModelListener listener);

	/**
	 * Get the nodes of a edge
	 * @param edge
	 * @return
	 */
	public Collection<ElectricPowerNode> getNodes(ElectricPowerConnection edge);
	
	/**
	 * Get the nodes of a edge in a consistent order
	 * @param edge
	 * @return
	 */
	public Pair<ElectricPowerNode,ElectricPowerNode> getOrderedNodes(ElectricPowerConnection edge);
		
	/**
	 * Adds a edge to the model
	 * @param edge
	 */
	public void addEdge(ElectricPowerConnection edge, ElectricPowerNode fromNode, ElectricPowerNode toNode);
	
	/**
	 * Adds a shunt to the model
	 * @param shunt
	 * @param state
	 */
	public void addShuntCapacitor(ShuntCapacitor shunt, Bus bus);
	
	/**
	 * Adds a shunt switch to the model
	 * @param shunt
	 * @param state
	 */
	public void addShuntCapacitorSwitch(ShuntCapacitorSwitch shunt, Bus bus);
	
	/**
	 * Remove a general edge from the model
	 * @param edge
	 */
	public void removeEdge(ElectricPowerConnection edge);
	
	/**
	 * Remove a line from the model
	 * @param line
	 */
	public void removeLine(Line line);

	/**
	 * Remove a transformer from the model
	 * @param transformer
	 */
	public void removeTransformer(Transformer transformer);

	/**
   * Remove a transformer from the model
   * @param transformer
   */
  public void removeDCLine(DCLine line);
	
	/**
	 * Remove an intertie from the model
	 * @param transformer
	 */
	public void removeIntertie(Intertie intertie);
	
	 /**
   * Removes a shunt from the model
   * @param shunt
   */
  public void removeShuntCapacitor(ShuntCapacitor shunt);
	
  /**
   * Removes a shunt from the model
   * @param shunt
   */
  public void removeShuntCapacitorSwitch(ShuntCapacitorSwitch shunt);
	
	/**
	 * Get all the loads in the model
	 * @return
	 */
	public Collection<? extends Load> getLoads();
	
	/**
	 * Get all the generators in the model
	 * @return
	 */
	public Collection<? extends Generator> getGenerators();

	/**
	 * Get all the batteries in the model
	 * @return
	 */
	public Collection<? extends Battery> getBatteries();
	
	/**
	 * Get all the edges that bare flow for a node
	 * @param node
	 * @return
	 */
	public Collection<ElectricPowerFlowConnection> getFlowEdges(ElectricPowerNode node);

	/**
   * Gets all the edges that carry flow
   * @return
   */
  public Collection<ElectricPowerFlowConnection> getFlowEdges(ElectricPowerNode to, ElectricPowerNode fro);
  
	/**
   * Get all the flow bearing edges
   * @return
   */
  public Collection<? extends ElectricPowerFlowConnection> getFlowConnections();
    
  /**
	 * Get the neighbors of a node
	 * @param node
	 * @return
	 */
  public Collection<ElectricPowerNode> getNeighbors(ElectricPowerNode node);
  
  /**
   * Get all the nodes
   * @return
   */
  public Collection<ElectricPowerNode> getNodes();
    
  /**
   * Gets all the edges
   * @return
   */
  public Collection<ElectricPowerConnection> getConnections(ElectricPowerNode to, ElectricPowerNode fro);
  
  /**
   * Get the generators of a node
   * @param node
   * @return
   */
  public Collection<? extends Generator> getGenerators(ElectricPowerNode node);

  /**
   * Get the batteries of a node
   * @param node
   * @return
   */
  public Collection<? extends Battery> getBatteries(ElectricPowerNode node);
  
  /**
   * Get the loads of a node
   * @param node
   * @return
   */
  public Collection<? extends Load> getLoads(ElectricPowerNode node);
  	
	/**
	 * Get the factory used for making lines
	 * @return
	 */
	public LineFactory getLineFactory();
	
	/**
	 * Get the factory used for making transformers
	 * @return
	 */
	public TransformerFactory getTransformerFactory();
	
	/**
	 * Get the factory used for making DC lines
	 * @return
	 */
	public DCLineFactory getDCLineFactory();
	
	/**
	 * Get the factory used for making interties
	 * @return
	 */
	public IntertieFactory getIntertieFactory();
	
	/**
	 * Get the factory used for making shunt capacitors
	 * @return
	 */
	public ShuntCapacitorFactory getShuntCapacitorFactory();

	/**
	 * Get the factory used for making shunt capacitor switches
	 * @return
	 */
	public ShuntCapacitorSwitchFactory getShuntCapacitorSwitchFactory();
	
	/**
	 * Gets all the transformers associated with a node
	 * @param node
	 */
	public Collection<? extends Transformer> getTransformers(ElectricPowerNode node);
	
	/**
	 * Get all the DC lines associated with a node
	 */
	public Collection<? extends DCLine> getDCLines(ElectricPowerNode node);

	/**
   * Get all the DC Two Terminal lines
   */
  public Collection<? extends DCTwoTerminalLine> getDCTwoTerminalLines();

  /**
   * Get all the DC Multi Terminal lines
   */
  public Collection<? extends DCMultiTerminalLine> getDCMultiTerminalLines();

  /**
   * Get all the DC Voltage source lines
   */
  public Collection<? extends DCVoltageSourceLine> getDCVoltageSourceLines();
  
	/**
	 * Gets all the lines associated with a node
	 * @param node
	 */
	public Collection<? extends Line> getLines(ElectricPowerNode node);
		
	/**
	 * Get all the shunts of a node
	 * @param node
	 * @return
	 */
	public Collection<? extends ShuntCapacitor> getShuntCapacitors(ElectricPowerNode node);
	
	/**
   * Get all the shunt switches of a node
   * @param node
   * @return
   */
  public Collection<? extends ShuntCapacitorSwitch> getShuntCapacitorSwitches(ElectricPowerNode node);
  	
	/**
   * Get the node of a component
   * @param shunt
   * @return
   */
  public ElectricPowerNode getNode(Component component);
	
  /**
	 * Gets all the shunts
	 * @return
	 */
	public Collection<? extends ShuntCapacitor> getShuntCapacitors();
	
	/**
	 * Gets all the shunts
	 * @return
	 */
	public Collection<? extends ShuntCapacitorSwitch> getShuntCapacitorSwitches();
	
	/**
	 * Get all the interites
	 * @return
	 */
	public Collection<? extends Intertie> getInterties();
	
	/**
	 * Add a bus to the model
	 * @param bus
	 * @param data
	 */
	public void addBus(Bus bus);
	
	/**
	 * get all the buses
	 * @return
	 */
	public Collection<? extends Bus> getBuses();

	/**
	 * Remove the bus from the model
	 * @param bus
	 */
	void removeBus(Bus bus);

	/**
	 * Get the factory for creating buses
	 * @return
	 */
	public BusFactory getBusFactory();
	
	/**
	 * Adds a generator
	 * @param generator
	 */
	public void addGenerator(Generator generator, Bus bus);
	
	/**
	 * Removes a generator
	 * @param generator
	 */
	public void removeGenerator(Generator generator);

	/**
	 * Get the generator factory to use with this model
	 * @return
	 */
	public GeneratorFactory getGeneratorFactory();

	/**
	 * Adds a generator
	 * @param generator
	 */
	public void addBattery(Battery battery, Bus bus);
	
	/**
	 * Removes a battery
	 * @param battery
	 */
	public void removeBattery(Battery battery);

	/**
	 * Get the battery factory to use with this model
	 * @return
	 */
	public BatteryFactory getBatteryFactory();
	
	/**
	 * Add a load to the model
	 * @param load
	 * @param data
	 */
	public void addLoad(Load load, Bus bus);
	
	/**
	 * Remove a load from the model
	 * @param load
	 */
	public void removeLoad(Load load);

	/**
	 * Return the factory used for creating loads
	 * @return
	 */
	public LoadFactory getLoadFactory();
	
	/**
	 * Get all the lines
	 * @return
	 */
	public Collection<? extends Line> getLines();
	
	/**
	 * Get all the transformers
	 * @return
	 */
	public Collection<? extends Transformer> getTransformers();

 /**
   * Get all the transformers
   * @return
   */
  public Collection<? extends DCLine> getDCLines();
	
  /**
   * Gets all the buses of a certain voltage within a certain hop count
   * of node
   * @param node
   * @param islandExtent
   * @return
   */
  public abstract Collection<ElectricPowerNode> createVoltageIsland(ElectricPowerNode node, int islandExtent);

  @Override
  public Collection<ElectricPowerConnection> getConnections();

  /**
   * Get all the components in the model
   * @return
   */
  public Collection<Component> getComponents();

  /**
   * Construct a copy of this model
   * @return
   */
  public ElectricPowerModel clone();
    
  /**
   * Returns an answer as to whether or not the model
   * is a solvable state
   * @return
   */
  public boolean isSolved();
  
  /**
   * Set the solved state of the model
   * @param solved
   */
  public void setIsSolved(boolean solved);
      
  @Override
  public ElectricPowerNode getFirstNode(Connection edge);

  @Override
  public ElectricPowerNode getSecondNode(Connection edge);

  @Override
  public ElectricPowerNode getFirstNode(Edge edge);

  @Override
  public ElectricPowerNode getSecondNode(Edge edge);

  
	/**
	 * Get a set of control areas
	 * @return
	 */
	public Collection<? extends ControlArea> getControlAreas();

	 /**
   * Get a set of xones
   * @return
   */
  public Collection<? extends Zone> getZones();
	
	/**
	 * Add a control area
	 * @param area
	 */
  public void addArea(ControlArea area);

  /**
   * Add a zone
   * @param area
   */
  public void addZone(Zone zone);

  /**
   * Gets the interties
   * @param to
   * @param fro
   * @return
   */
  public Collection<Intertie> getInterties(ElectricPowerNode to, ElectricPowerNode fro);
  
  /**
   * Get the MVA Base
   * @return
   */
  public double getMVABase();
  
  /**
   * Set the MVA Base
   * @param mvaBase
   */
  public void setMVABase(double mvaBase);
  
  /**
   * Collects all the slack buses in the model
   * @return
   */
  public Set<Bus> getSlackBuses();
    
  /**
   * Get the control area associated with an asset
   * @param asset
   * @return
   */
  public ControlArea getControlArea(Asset asset);
  
  /**
   * Get the zone associated with an asset
   * @param asset
   * @return
   */
  public Zone getZone(Asset asset);
  
  /**
   * Get the slack bus associated with a control area
   * @param area
   * @return
   */
  public Bus getSlackBus(ControlArea area);
  
  /**
   * Get the metered area for an intertie
   * @param intertie
   * @return
   */
  public ControlArea getMeteredArea(Intertie intertie);
  
  /**
   * Get the nonmetered area for an intertie
   * @param intertie
   * @return
   */
  public ControlArea getNonMeteredArea(Intertie intertie);
  
  /**
   * Set the control area
   * @param asset
   * @param area
   */
  public void setControlArea(Asset asset, ControlArea area);
  
  /**
   * Set the zone of the asset
   * @param asset
   * @param zone
   */ 
  public void setZone(Asset asset, Zone zone);
  
  /**
   * Set the slack bus for an area
   * @param area
   * @param slackBus
   */
  public void setSlackBus(ControlArea area, Bus slackBus);
  
  /**
   * Set the metered area for an intertie
   * @param intertie
   * @param area
   */
  public void setMeteredArea(Intertie intertie, ControlArea area);
  
  /**
   * Set the non-mtereed are for an intertie
   * @param intertie
   * @param area
   */
  public void setNonMeteredArea(Intertie intertie, ControlArea area);
  
  /**
   * Set the control bus of an asset
   * @param asset
   * @param bus
   */
  public void setControlBus(Asset asset, Bus bus);
  
  /**
   * Get the control bus of an asset
   * @param asset
   */
  public Bus getControlBus(Asset asset);

  /**
   * Get the control area factory
   * @return
   */
  public ControlAreaFactory getControlAreaFactory();
  
  /**
   * Get the zone factory
   * @return
   */
  public ZoneFactory getZoneFactory();
}


