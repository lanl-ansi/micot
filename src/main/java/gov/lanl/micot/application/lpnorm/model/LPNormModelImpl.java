package gov.lanl.micot.application.lpnorm.model;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;

/**
 * This is a data structure for an LPNorm model
 * @author Russell Bent
 */
public class LPNormModelImpl extends ElectricPowerModelImpl implements LPNormModel {

	protected static final long serialVersionUID = 0;

	/**
	 * Constructor
	 */
	public LPNormModelImpl() {	  
	  super();
	  setLineFactory(new LPNormLineFactory());
	  setTransformerFactory(new LPNormTransformerFactory());
	  setIntertieFactory(new LPNormIntertieFactory());
	  setBusFactory(new LPNormBusFactory());
	  setLoadFactory(new LPNormLoadFactory());
	  setGeneratorFactory(new LPNormGeneratorFactory());
	  setCapacitorFactory(new LPNormShuntFactory());
	  setShuntCapacitorSwitchFactory(new LPNormSwitchedShuntFactory());
	  setBatteryFactory(new LPNormBatteryFactory());
	  setControlAreaFactory(new LPNormAreaFactory());
	  setZoneFactory(new LPNormZoneFactory());
	}
		
	@Override
	public LPNormLineFactory getLineFactory() {
		return (LPNormLineFactory) super.getLineFactory();
	}

	@Override
	public LPNormTransformerFactory getTransformerFactory() {
    return (LPNormTransformerFactory) super.getTransformerFactory();
	}
	
	@Override
	public LPNormIntertieFactory getIntertieFactory() {
    return (LPNormIntertieFactory) super.getIntertieFactory();
	}

	@Override
	public LPNormShuntFactory getShuntCapacitorFactory() {
    return (LPNormShuntFactory) super.getShuntCapacitorFactory();
	}

	@Override
	public LPNormSwitchedShuntFactory getShuntCapacitorSwitchFactory() {
    return (LPNormSwitchedShuntFactory) super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public LPNormBusFactory getBusFactory() {
    return (LPNormBusFactory) super.getBusFactory();
	}

	@Override
	public LPNormGeneratorFactory getGeneratorFactory() {
    return (LPNormGeneratorFactory) super.getGeneratorFactory();
	}

	@Override
	public LPNormBatteryFactory getBatteryFactory() {
    return (LPNormBatteryFactory) super.getBatteryFactory();
	}
	
	@Override
	public LPNormLoadFactory getLoadFactory() {
    return (LPNormLoadFactory) super.getLoadFactory();
	}
	
	@Override
	public LPNormAreaFactory getControlAreaFactory() {
	  return (LPNormAreaFactory) super.getControlAreaFactory();
	}

	@Override
	public LPNormZoneFactory getZoneFactory() {
	  return (LPNormZoneFactory) super.getZoneFactory();
	}

  @Override 
  public LPNormModelImpl constructClone() {
    return new LPNormModelImpl();
  }  
}
