package gov.lanl.micot.infrastructure.ep.model.pfw;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * This is a data structure for a PFW model
 * @author Russell Bent
 */
public class PFWModelImpl extends ElectricPowerModelImpl implements PFWModel {

	protected static final long serialVersionUID = 0;

	private PFWHeader header = null;
	
	private double minVoltageHeader = Bus.DEFAULT_MINIMUM_VOLTAGE;
  private double maxVoltageHeader = Bus.DEFAULT_MAXIMUM_VOLTAGE;
  	
	/**
	 * Constructor
	 */
	public PFWModelImpl() {
	  super();
	  setLineFactory(new PFWLineFactory());
	  setTransformerFactory(new PFWTransformerFactory());
	  setIntertieFactory(new PFWIntertieFactory());
	  setBusFactory(new PFWBusFactory());
	  setLoadFactory(new PFWLoadFactory());
	  setGeneratorFactory(new PFWGeneratorFactory());
	  setCapacitorFactory(new PFWShuntFactory());
	  setShuntCapacitorSwitchFactory(new PFWSwitchedShuntFactory());
	  setBatteryFactory(new PFWBatteryFactory());
	  setControlAreaFactory(new PFWAreaFactory());
	  setZoneFactory(new PFWZoneFactory());
	}
		
	/**
	 * @return the header
	 */
	public PFWHeader getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(PFWHeader header) {
		this.header = header;
		setMVABase(header.getMVABase());
		setMinVoltageHeader(header.getMinVoltage());
    setMaxVoltageHeader(header.getMaxVoltage());
    for (Bus bus : getBuses()) {
      if (getHeader() != null) {
        if (bus.getAttribute(Bus.MINIMUM_VOLTAGE_PU_KEY) == null) {
          bus.setMinimumVoltagePU(getMinVoltageHeader());
        }
        if (bus.getAttribute(Bus.MAXIMUM_VOLTAGE_PU_KEY) == null) {
          bus.setMaximumVoltagePU(getMaxVoltageHeader());
        }
      }
    }
	}

	/**
	 * Finds a particular link
	 * @param node1
	 * @param node2
	 * @param circuit
	 * @return
	 */
	protected ElectricPowerFlowConnection getFlowLink(ElectricPowerNode node1, ElectricPowerNode node2, String circuit) {
	  Collection<ElectricPowerFlowConnection> links = getFlowEdges(node1, node2);
	  for (ElectricPowerFlowConnection line : links) {
      if (line.getCircuit().equals(circuit)) {
        return line;
      }	    
	  }
	  return null;
	}
	
	
	@Override
	public PFWLineFactory getLineFactory() {
		return (PFWLineFactory) super.getLineFactory();
	}

	@Override
	public PFWTransformerFactory getTransformerFactory() {
    return (PFWTransformerFactory) super.getTransformerFactory();
	}
	
	@Override
	public PFWIntertieFactory getIntertieFactory() {
    return (PFWIntertieFactory) super.getIntertieFactory();
	}

	@Override
	public PFWShuntFactory getShuntCapacitorFactory() {
    return (PFWShuntFactory) super.getShuntCapacitorFactory();
	}

	@Override
	public PFWSwitchedShuntFactory getShuntCapacitorSwitchFactory() {
    return (PFWSwitchedShuntFactory) super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public PFWBusFactory getBusFactory() {
    return (PFWBusFactory) super.getBusFactory();
	}

	@Override
	public PFWGeneratorFactory getGeneratorFactory() {
    return (PFWGeneratorFactory) super.getGeneratorFactory();
	}

	@Override
	public PFWBatteryFactory getBatteryFactory() {
    return (PFWBatteryFactory) super.getBatteryFactory();
	}
	
	@Override
	public PFWLoadFactory getLoadFactory() {
    return (PFWLoadFactory) super.getLoadFactory();
	}
	
	@Override
	public PFWAreaFactory getControlAreaFactory() {
	  return (PFWAreaFactory) super.getControlAreaFactory();
	}

	@Override
	public PFWZoneFactory getZoneFactory() {
	  return (PFWZoneFactory) super.getZoneFactory();
	}
	
  @Override
  protected PFWModelImpl constructClone() {
    PFWModelImpl newModel = new PFWModelImpl();
    newModel.setHeader(getHeader());
    return newModel;
  }
  
  /**
   * Set the min voltage header
   * @param minVoltageHeader
   */
  private void setMinVoltageHeader(double minVoltageHeader) {
    this.minVoltageHeader = minVoltageHeader;
  }

  /**
   * Set the min voltage header
   * @param minVoltageHeader
   */
  private void setMaxVoltageHeader(double maxVoltageHeader) {
    this.maxVoltageHeader = maxVoltageHeader;
  }
  
  /**
   * Set the min voltage header
   * @param minVoltageHeader
   */
  private double getMinVoltageHeader() {
    return minVoltageHeader;
  }

  /**
   * Set the min voltage header
   * @param minVoltageHeader
   */
  private double getMaxVoltageHeader() {
    return maxVoltageHeader;
  }

  @Override
  public void addBus(Bus bus) {
    super.addBus(bus);
    if (getHeader() != null) {
      if (bus.getAttribute(Bus.MINIMUM_VOLTAGE_PU_KEY) == null) {
        bus.setMinimumVoltagePU(getMinVoltageHeader());
      }
      if (bus.getAttribute(Bus.MAXIMUM_VOLTAGE_PU_KEY) == null) {
        bus.setMaximumVoltagePU(getMaxVoltageHeader());
      }
    }
  }
}
