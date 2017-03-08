package gov.lanl.micot.infrastructure.ep.model.cdf;

import java.util.Collection;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * This is a data structure for a PFW model
 * @author Russell Bent
 */
public class CDFModelImpl extends ElectricPowerModelImpl implements CDFModel {

	protected static final long serialVersionUID = 0;

	public CDFHeader header = null;
	
	/**
	 * Constructor
	 */
	public CDFModelImpl() {
	  super();
	  setLineFactory(new CDFLineFactory());
	  setTransformerFactory(new CDFTransformerFactory());
    setIntertieFactory(new CDFIntertieFactory());
    setBusFactory(new CDFBusFactory());
    setLoadFactory(new CDFLoadFactory());
    setGeneratorFactory(new CDFGeneratorFactory());
    setCapacitorFactory(new CDFShuntFactory());
    setShuntCapacitorSwitchFactory(new CDFSwitchedShuntFactory());
    setBatteryFactory(new CDFBatteryFactory());
    setControlAreaFactory(new CDFAreaFactory());
    setZoneFactory(new CDFZoneFactory());
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
	public CDFLineFactory getLineFactory() {
	  return (CDFLineFactory)super.getLineFactory();
	}

	@Override
	public CDFTransformerFactory getTransformerFactory() {
    return (CDFTransformerFactory)super.getTransformerFactory();
	}
	
	@Override
	public CDFIntertieFactory getIntertieFactory() {
    return (CDFIntertieFactory)super.getIntertieFactory();
	}

	@Override
	public CDFShuntFactory getShuntCapacitorFactory() {
    return (CDFShuntFactory)super.getShuntCapacitorFactory();
	}

	@Override
	public CDFSwitchedShuntFactory getShuntCapacitorSwitchFactory() {
    return (CDFSwitchedShuntFactory)super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public CDFBusFactory getBusFactory() {
    return (CDFBusFactory)super.getBusFactory();
	}

	@Override
	public CDFGeneratorFactory getGeneratorFactory() {
    return (CDFGeneratorFactory)super.getGeneratorFactory();
	}

	@Override
	public CDFBatteryFactory getBatteryFactory() {
    return (CDFBatteryFactory)super.getBatteryFactory();
	}
	
	@Override
	public CDFLoadFactory getLoadFactory() {
    return (CDFLoadFactory)super.getLoadFactory();
	}

	@Override
	public CDFAreaFactory getControlAreaFactory() {
	  return (CDFAreaFactory)super.getControlAreaFactory();
	}

	@Override
	public CDFZoneFactory getZoneFactory() {
	  return (CDFZoneFactory)super.getZoneFactory();
	}

  @Override
  protected CDFModelImpl constructClone() {
    CDFModelImpl newModel = new CDFModelImpl();
    newModel.setHeader(getHeader());
    return newModel;
  }
  
  @Override
  public CDFHeader getHeader() {
    return header;
  }
  
  /**
   * @param header the header to set
   */
  public void setHeader(CDFHeader header) {
    this.header = header;
  }
  
  @Override
  public void setMVABase(double mvaBase) {
    super.setMVABase(mvaBase);
    header.setMVABase(mvaBase);
  }


}
