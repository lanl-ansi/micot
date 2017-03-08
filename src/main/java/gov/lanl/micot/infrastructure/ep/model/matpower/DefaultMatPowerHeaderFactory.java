package gov.lanl.micot.infrastructure.ep.model.matpower;

/**
 * This is a factory for creating default mat power file headers
 * 
 * @author Russell Bent
 */
public class DefaultMatPowerHeaderFactory {

	private static DefaultMatPowerHeaderFactory	instance	= null;

	/**
	 * Singleton
	 * @return
	 */
	public static DefaultMatPowerHeaderFactory getInstance() {
		if (instance == null) {
			instance = new DefaultMatPowerHeaderFactory();
		}
		return instance;
	}

	/**
	 * Constructor
	 */
	private DefaultMatPowerHeaderFactory() {
	}

	/**
	 * Get the version
	 * @return
	 */
	public String getVersion() {
	  return "'2'";
	}
	
  /**
   * Get the case
   * @return
   */
  public String getCase() {
    return "caseA";
  }

	

}
