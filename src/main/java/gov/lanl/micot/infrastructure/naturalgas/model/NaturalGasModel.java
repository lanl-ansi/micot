package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.Edge;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.util.collection.Pair;

import java.util.Collection;

/**
 * An interface for a model.  A model is intended to encapsulate 
 * the components and topology of a model
 * @author Russell Bent
 */
public interface NaturalGasModel extends Model, Cloneable {
	
	/**
	 * Add a listener to the model
	 * @param listener
	 */
	public void addModelListener(NaturalGasModelListener listener);
	
	/**
	 * Remove a listener from the model
	 * @param listener
	 */
	public void removeModelListener(NaturalGasModelListener listener);

	/**
	 * Get the nodes of a link
	 * @param link
	 * @return
	 */
	public Collection<NaturalGasNode> getNodes(NaturalGasConnection connection);
	
	/**
	 * Get the nodes of a link in a consistent order
	 * @param link
	 * @return
	 */
	public Pair<NaturalGasNode,NaturalGasNode> getOrderedNodes(NaturalGasConnection connection);
		
	/**
	 * Adds a link to the model
	 * @param link
	 */
	public void addPipe(Pipe pipe, NaturalGasNode fromNode, NaturalGasNode toNode);

	 /**
   * Adds a link to the model
   * @param link
   */
  public void addShortPipe(ShortPipe pipe, NaturalGasNode fromNode, NaturalGasNode toNode);
	
	/**
	 * Adds a compressor to the model
	 * @param shunt
	 * @param state
	 */
	public void addCompressor(Compressor compressor, NaturalGasNode fromNode, NaturalGasNode toNode);
	
	 /**
   * Adds a valve to the model
   * @param shunt
   * @param state
   */
  public void addValve(Valve valve, NaturalGasNode fromNode, NaturalGasNode toNode);

  /**
  * Adds a control valve to the model
  * @param shunt
  * @param state
  */
 public void addControlValve(ControlValve valve, NaturalGasNode fromNode, NaturalGasNode toNode);
  
  /**
  * Adds a resistor to the model
  * @param shunt
  * @param state
  */
 public void addResistor(Resistor resistor, NaturalGasNode fromNode, NaturalGasNode toNode);
  
	/**
	 * Adds a reservoir to the model
	 * @param shunt
	 * @param state
	 */
	public void addReservoir(Reservoir reservoir, Junction junction);
	
	/**
	 * Remove a pipe from the model
	 * @param link
	 */
	public void removePipe(Pipe pipe);

	 /**
   * Remove a pipe from the model
   * @param link
   */
  public void removeShortPipe(ShortPipe pipe);
	
	 /**
   * Removes a compressor from the model
   * @param shunt
   */
  public void removeCompressor(Compressor compressor);

  /**
  * Removes a valve from the model
  * @param shunt
  */
  public void removeValve(Valve valve);

  /**
  * Removes a valve from the model
  * @param shunt
  */
  public void removeControlValve(ControlValve valve);
  
  /**
  * Removes a resistor from the model
  * @param shunt
  */
  public void removeResistor(Resistor resistor);
  
  /**
   * Removes a reservoir from the model
   * @param shunt
   */
  public void removeReservoir(Reservoir reservoir);
	
	/**
	 * Get all the city gates in the model
	 * @return
	 */
	public Collection<? extends CityGate> getCityGates();
	
	/**
	 * Get all the wells in the model
	 * @return
	 */
	public Collection<? extends Well> getWells();
	
	/**
	 * Get all the links that bare flow for a node
	 * @param node
	 * @return
	 */
	public Collection<Pipe> getPipes(NaturalGasNode node);

	/**
   * Gets all the pipes that carry flow
   * @return
   */
  public Collection<Pipe> getPipes(NaturalGasNode to, NaturalGasNode fro);
  
	/**
   * Get all the flow bearing links
   * @return
   */
  public Collection<Pipe> getPipes();

  /**
   * Get all the links that bare flow for a node
   * @param node
   * @return
   */
  public Collection<ShortPipe> getShortPipes(NaturalGasNode node);

  /**
   * Gets all the pipes that carry flow
   * @return
   */
  public Collection<ShortPipe> getShortPipes(NaturalGasNode to, NaturalGasNode fro);
  
  /**
   * Get all the flow bearing links
   * @return
   */
  public Collection<ShortPipe> getShortPipes();
  
  /**
   * Gets all the pipes that carry flow
   * @return
   */
  public Collection<Compressor> getCompressors(NaturalGasNode to, NaturalGasNode fro);

  /**
   * Gets all the valves that carry flow
   * @return
   */
  public Collection<Valve> getValves(NaturalGasNode to, NaturalGasNode fro);

  /**
   * Gets all the valves that carry flow
   * @return
   */
  public Collection<ControlValve> getControlValves(NaturalGasNode to, NaturalGasNode fro);

  
  /**
   * Gets all the resistors that carry flow
   * @return
   */
  public Collection<Resistor> getResistors(NaturalGasNode to, NaturalGasNode fro);
  
  /**
	 * Get the neighbors of a node
	 * @param node
	 * @return
	 */
  public Collection<NaturalGasNode> getNeighbors(NaturalGasNode node);
  
  /**
   * Get all the nodes
   * @return
   */
  public Collection<NaturalGasNode> getNodes();
      
  /**
   * Get the wells of a node
   * @param node
   * @return
   */
  public Collection<? extends Well> getWells(NaturalGasNode node);

  /**
   * Get the reservoirs of a node
   * @param node
   * @return
   */
  public Collection<Reservoir> getReservoirs(NaturalGasNode node);

  
  /**
   * Get the city gates of a node
   * @param node
   * @return
   */
  public Collection<? extends CityGate> getCityGates(NaturalGasNode node);
  		
	/**
	 * Get the factory used for making pipes
	 * @return
	 */
	public PipeFactory getPipeFactory();

	 /**
   * Get the factory used for making pipes
   * @return
   */
  public ShortPipeFactory getShortPipeFactory();
	
	/**
	 * Get the factory used for making compressors
	 * @return
	 */
	public CompressorFactory getCompressorFactory();

	 /**
   * Get the factory used for making valves
   * @return
   */
  public ValveFactory getValveFactory();

  /**
  * Get the factory used for making valves
  * @return
  */
 public ControlValveFactory getControlValveFactory();
  
  /**
  * Get the factory used for making valves
  * @return
  */
 public ResistorFactory getResistorFactory();
  
	/**
	 * Get the factory used for making reservoiors
	 * @return
	 */
	public ReservoirFactory getReservoirFactory();
		
	/**
	 * Get all the compressor of a node
	 * @param node
	 * @return
	 */
	public Collection<? extends Compressor> getCompressors(NaturalGasNode node);

	 /**
   * Get all the valve of a node
   * @param node
   * @return
   */
  public Collection<? extends Valve> getValves(NaturalGasNode node);

  /**
  * Get all the valve of a node
  * @param node
  * @return
  */
 public Collection<? extends ControlValve> getControlValves(NaturalGasNode node);
  
  /**
  * Get all the valve of a node
  * @param node
  * @return
  */
 public Collection<? extends Resistor> getResistors(NaturalGasNode node);

	
	/**
   * Get the node of a component
   * @param shunt
   * @return
   */
  public NaturalGasNode getNode(Component component);
	
  /**
	 * Gets all the compressors
	 * @return
	 */
	public Collection<? extends Compressor> getCompressors();
	
  /**
   * Gets all the compressors
   * @return
   */
  public Collection<? extends Valve> getValves();

  /**
   * Gets all the compressors
   * @return
   */
  public Collection<? extends ControlValve> getControlValves();
  
  /**
   * Gets all the compressors
   * @return
   */
  public Collection<? extends Resistor> getResistors();

  
	/**
	 * Gets all the reservoirs
	 * @return
	 */
	public Collection<? extends Reservoir> getReservoirs();
	
	/**
	 * Add a junction to the model
	 * @param bus
	 * @param data
	 */
	public void addJunction(Junction junction);
	
	/**
	 * get all the junctions
	 * @return
	 */
	public Collection<? extends Junction> getJunctions();

	/**
	 * Remove the bus from the model
	 * @param bus
	 */
	void removeJunction(Junction junction);

	/**
	 * Get the factory for creating junctions
	 * @return
	 */
	public JunctionFactory getJunctionFactory();
	
	/**
	 * Adds a well
	 * @param generator
	 */
	public void addWell(Well well, Junction junction);
	
	/**
	 * Removes a well
	 * @param generator
	 */
	public void removeWell(Well well);

	/**
	 * Get the well factory to use with this model
	 * @return
	 */
	public WellFactory getWellFactory();
	
	/**
	 * Add a load to the model
	 * @param load
	 * @param data
	 */
	public void addCityGate(CityGate gate, Junction junction);
	
	/**
	 * Remove a load from the model
	 * @param load
	 */
	public void removeCityGate(CityGate gate);

	/**
	 * Return the factory used for creating loads
	 * @return
	 */
	public CityGateFactory getCityGateFactory();
	
	/**
	 * Get the edges between nodes
	 * @param node1
	 * @param node2
	 * @return
	 */
	public Collection<NaturalGasConnection> getConnections(NaturalGasNode node1, NaturalGasNode node2);

	 /**
   * Get the edges between nodes
   * @param node1
   * @param node2
   * @return
   */
  public Collection<Edge> getEdges(NaturalGasNode node1, NaturalGasNode node2);

	
	/**
	 * Get the natural edges
	 * @param node
	 * @return
	 */
//	public Collection<NaturalGasConnection> getNaturalGasEdges(Node node);

	/**
	 * Get natural gas edges
	 * @return
	 */
	//public Collection<NaturalGasConnection> getNaturalGasEdges();
	
  /**
   * Get all the components in the model
   * @return
   */
  public Collection<Component> getComponents();

  /**
   * Construct a copy of this model
   * @return
   */
  public NaturalGasModel clone();
  
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
  public NaturalGasNode getFirstNode(Connection edge);

  @Override
  public NaturalGasNode getSecondNode(Connection edge);

  @Override
  public NaturalGasNode getFirstNode(Edge edge);

  @Override
  public NaturalGasNode getSecondNode(Edge edge);
  
  /**
   * Set a ranking of simulation quality used to generator results for this model
   * @param rank
   */
  public void setSimulationQualityRank(int rank);
  
	/**
	 * Get the simulation quality rank
	 * @return
	 */
	public int getSimulationQualityRank();
	
	@Override
	public Collection<NaturalGasConnection> getFlowConnections();

	@Override
  public Collection<NaturalGasConnection> getFlowConnections(Node node);
	
  @Override
  public Collection<NaturalGasConnection> getFlowConnections(Node to, Node fro);
  
  @Override
  public Collection<NaturalGasConnection> getConnections();



}
