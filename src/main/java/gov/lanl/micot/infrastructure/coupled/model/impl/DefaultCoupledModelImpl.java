package gov.lanl.micot.infrastructure.coupled.model.impl;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledModelImpl;

/**
 * This is a data structure for a coupled model
 * @author Russell Bent
 */
public class DefaultCoupledModelImpl extends CoupledModelImpl implements CoupledModel {

	protected static final long serialVersionUID = 0;

	
	/**
	 * Constructor
	 */
	public DefaultCoupledModelImpl() {
	  super();
	  setCouplingFactory(new CouplingFactoryImpl());
	}
		
  @Override
  public CouplingFactoryImpl getCouplingFactory() {
    return (CouplingFactoryImpl) super.getCouplingFactory();
  }


  @Override
  protected CoupledModelImpl constructClone() {
    DefaultCoupledModelImpl model = new DefaultCoupledModelImpl();
    return model;
  }

 
	

}
