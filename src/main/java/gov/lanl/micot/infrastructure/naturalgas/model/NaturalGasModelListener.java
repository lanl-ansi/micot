package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * This is an interface for listening to changes in a model's
 * structure
 * @author Russell Bent
 */
public interface NaturalGasModelListener {

	/**
	 * Notification that a link has been added to the model
	 * @param link
	 */
	public void pipeAdded(Pipe link);
	
	/**
	 * Notification that a link has been removed from the model
	 * @param link
	 */
	public void pipeRemoved(Pipe link);

	 /**
   * Notification that a link has been added to the model
   * @param link
   */
  public void shortPipeAdded(ShortPipe link);
  
  /**
   * Notification that a link has been removed from the model
   * @param link
   */
  public void shortPipeRemoved(ShortPipe link);

	
	/**
	 * Notification that a compressor was added
	 * @param node
	 * @param shunt
	 * @param state
	 */
	public void compressorAdded(Compressor compressor);
	
	/**
	 * Notification that a compressor was removed
	 * @param node
	 * @param shunt
	 * @param state
	 */
	public void compressorRemoved(Compressor compressor);
	
	 /**
   * Notification that a compressor was added
   * @param node
   * @param shunt
   * @param state
   */
  public void valveAdded(Valve valve);
  
  /**
   * Notification that a compressor was removed
   * @param node
   * @param shunt
   * @param state
   */
  public void valveRemoved(Valve valve);

  /**
  * Notification that a compressor was added
  * @param node
  * @param shunt
  * @param state
  */
 public void resistorAdded(Resistor resistor);
 
 /**
  * Notification that a compressor was removed
  * @param node
  * @param shunt
  * @param state
  */
 public void resistorRemoved(Resistor resistor);  
	
	/**
	 * Added a reservoir
	 * @param shunt
	 */
	public void reservoirAdded(Reservoir reservoir);
	
	/**
	 * Removed a reservoir
	 * @param shunt
	 */
	public void reservoirRemoved(Reservoir reservoir);

	/**
	 * Notification that a junction has been added
	 * @param bus
	 */
	public void junctionAdded(Junction junction);

	/**
	 * Notification that a junction has been removed
	 * @param bus
	 */
	public void junctionRemoved(Junction junction);

  /**
   * Notification that junction data has changed
   * @param bus
   * @param newBusData
   */
  public void junctionDataChange(Junction junction);
	
	/**
	 * Notification that a generator was added
	 * @param generator
	 * @param data
	 */
	public void wellAdded(Well well);

	/**
	 * Notification that a well has been removed
	 * @param generator
	 */
	public void wellRemoved(Well well);
	
	/**
	 * Notification that a city gate has been added
	 * @param load
	 * @param data
	 */
	public void cityGateAdded(CityGate gate);

	/**
	 * Notification that a city gate has been removed
	 * @param load
	 */
	public void cityGateRemoved(CityGate gate);
	
  /**
   * Notification that compressor data has changed
   * @param shunt
   * @param newShuntCapacitorData
   */
  public void compressorDataChange(Compressor compressor);

  /**
   * Notification that compressor data has changed
   * @param shunt
   * @param newShuntCapacitorData
   */
  public void valveDataChange(Valve valve);
  
  /**
   * Notification that compressor data has changed
   * @param shunt
   * @param newShuntCapacitorData
   */
  public void resistorDataChange(Resistor resistor);
  
  
  /**
   * Notification that well data has changed
   * @param generator
   * @param newGeneratorData
   */
  public void wellDataChange(Well well);

  /**
   * Notification that city gate data has changed
   * @param load
   * @param newLoadData
   */
  public void cityGateDataChange(CityGate gate);
  
  /**
   * Notification that reservoir data has changed
   * @param shunt
   * @param newShuntCapacitorSwitchData
   */
  public void reservoirDataChange(Reservoir reservoir);

  /**
   * Notification that pipe data has changed
   * @param line
   * @param newLineData
   */
  public void pipeDataChange(Pipe pipe);

  /**
   * Notification that short pipe data has changed
   * @param line
   * @param newLineData
   */
  public void shortPipeDataChange(ShortPipe pipe);
  
  /**
  * Notification that a control valve was added
  * @param node
  * @param shunt
  * @param state
  */
 public void controlValveAdded(ControlValve valve);
 
 /**
  * Notification that a control valve was removed
  * @param node
  * @param shunt
  * @param state
  */
 public void controlValveRemoved(ControlValve valve);

 /**
  * Notification that control valve data has changed
  * @param shunt
  * @param newShuntCapacitorData
  */
 public void controlValveDataChange(ControlValve valve);
  
}
