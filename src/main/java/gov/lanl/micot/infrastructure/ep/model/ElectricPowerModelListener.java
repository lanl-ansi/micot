package gov.lanl.micot.infrastructure.ep.model;

/**
 * This is an interface for listening to changes in a model's
 * structure
 * @author Russell Bent
 */
public interface ElectricPowerModelListener {

	/**
	 * Notification that a link has been added to the model
	 * @param link
	 */
	public void linkAdded(ElectricPowerConnection link);
	
	/**
	 * Notification that a link has been removed from the model
	 * @param link
	 */
	public void linkRemoved(ElectricPowerConnection link);
		
	/**
	 * Notification that a shunt was added
	 * @param node
	 * @param shunt
	 * @param state
	 */
	public void shuntCapacitorAdded(ShuntCapacitor shunt);
	
	/**
	 * Notification that a shunt was removed
	 * @param node
	 * @param shunt
	 * @param state
	 */
	public void shuntCapacitorRemoved(ShuntCapacitor shunt);
	
	/**
	 * Added a switched shunt
	 * @param shunt
	 */
	public void shuntCapacitorSwitchAdded(ShuntCapacitorSwitch shunt);
	
	/**
	 * Removed a switched shunt
	 * @param shunt
	 */
	public void shuntCapacitorSwitchRemoved(ShuntCapacitorSwitch shunt);

	/**
	 * Notification that a bus has been added
	 * @param bus
	 */
	public void busAdded(Bus bus);

	/**
	 * Notification that a bus has been removed
	 * @param bus
	 */
	public void busRemoved(Bus bus);

  /**
   * Notification that bus data has changed
   * @param bus
   * @param newBusData
   */
  public void busDataChange(Bus bus);
	
	/**
	 * Notification that a generator was added
	 * @param generator
	 * @param data
	 */
	public void generatorAdded(Generator generator);

	/**
	 * Notification that a generator was added
	 * @param generator
	 * @param data
	 */
	public void batteryAdded(Battery battery);
	
	/**
	 * Notification that a generation has been removed
	 * @param generator
	 */
	public void generatorRemove(Generator generator);

	/**
	 * Notification that a generation has been removed
	 * @param generator
	 */
	public void batteryRemove(Battery battery);
	
	/**
	 * Notification that a load has been added
	 * @param load
	 * @param data
	 */
	public void loadAdded(Load load);

	/**
	 * Notification that a load has been removed
	 * @param load
	 */
	public void loadRemoved(Load load);
	
  /**
   * Notification that shunt capacitor data has changed
   * @param shunt
   * @param newShuntCapacitorData
   */
  public void shuntCapacitorDataChange(ShuntCapacitor shunt);

  /**
   * Notification that generator data has changed
   * @param generator
   * @param newGeneratorData
   */
  public void generatorDataChange(Generator generator);

  /**
   * Notification that battery data has changed
   * @param generator
   * @param newGeneratorData
   */
  public void batteryDataChange(Battery battery);

  
  /**
   * Notification that load data has changed
   * @param load
   * @param newLoadData
   */
  public void loadDataChange(Load load);
  
  /**
   * Notification that shunt capacitor switch data has changed
   * @param shunt
   * @param newShuntCapacitorSwitchData
   */
  public void shuntCapacitorSwitchDataChange(ShuntCapacitorSwitch shunt);

  /**
   * Notification that line data has changed
   * @param line
   * @param newLineData
   */
  public void lineDataChange(Line line);
  
  /**
   * Notification that transformer data has changed
   * @param transformer
   * @param newTransformerData
   */
  public void transformerDataChange(Transformer transformer);
  
  /**
   * Notification that intertie data has changed
   * @param intertie
   * @param newIntertieData
   */
  public void intertieDataChange(Intertie intertie);

}
