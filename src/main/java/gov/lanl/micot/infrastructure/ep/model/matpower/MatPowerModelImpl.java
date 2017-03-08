package gov.lanl.micot.infrastructure.ep.model.matpower;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * This is a data structure for a MatPower model
 * @author Russell Bent
 */
public class MatPowerModelImpl extends ElectricPowerModelImpl implements MatPowerModel {

	protected static final long serialVersionUID = 0;

	private String sCase   = null;
	private String version = null;
	
	/**
	 * Constructor
	 */
	public MatPowerModelImpl() {	  
	  super();
	  setLineFactory(new MatPowerLineFactory());
	  setTransformerFactory(new MatPowerTransformerFactory());
	  setIntertieFactory(new MatPowerIntertieFactory());
	  setBusFactory(new MatPowerBusFactory());
	  setLoadFactory(new MatPowerLoadFactory());
	  setGeneratorFactory(new MatPowerGeneratorFactory());
	  setCapacitorFactory(new MatPowerShuntFactory());
	  setShuntCapacitorSwitchFactory(new MatPowerSwitchedShuntFactory());
	  setBatteryFactory(new MatPowerBatteryFactory());
	  setControlAreaFactory(new MatPowerAreaFactory());
	  setZoneFactory(new MatPowerZoneFactory());
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
	public MatPowerLineFactory getLineFactory() {
		return (MatPowerLineFactory) super.getLineFactory();
	}

	@Override
	public MatPowerTransformerFactory getTransformerFactory() {
    return (MatPowerTransformerFactory) super.getTransformerFactory();
	}
	
	@Override
	public MatPowerIntertieFactory getIntertieFactory() {
    return (MatPowerIntertieFactory) super.getIntertieFactory();
	}

	@Override
	public MatPowerShuntFactory getShuntCapacitorFactory() {
    return (MatPowerShuntFactory) super.getShuntCapacitorFactory();
	}

	@Override
	public MatPowerSwitchedShuntFactory getShuntCapacitorSwitchFactory() {
    return (MatPowerSwitchedShuntFactory) super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public MatPowerBusFactory getBusFactory() {
    return (MatPowerBusFactory) super.getBusFactory();
	}

	@Override
	public MatPowerGeneratorFactory getGeneratorFactory() {
    return (MatPowerGeneratorFactory) super.getGeneratorFactory();
	}

	@Override
	public MatPowerBatteryFactory getBatteryFactory() {
    return (MatPowerBatteryFactory) super.getBatteryFactory();
	}
	
	@Override
	public MatPowerLoadFactory getLoadFactory() {
    return (MatPowerLoadFactory) super.getLoadFactory();
	}
	
	@Override
	public MatPowerAreaFactory getControlAreaFactory() {
	  return (MatPowerAreaFactory) super.getControlAreaFactory();
	}

	@Override
	public MatPowerZoneFactory getZoneFactory() {
	  return (MatPowerZoneFactory) super.getZoneFactory();
	}

  @Override 
  public MatPowerModelImpl constructClone() {
    return new MatPowerModelImpl();
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String getCase() {
    return sCase;
  }

  @Override
  public void setCase(String sCase) {
    this.sCase = sCase;
  }
  
}
