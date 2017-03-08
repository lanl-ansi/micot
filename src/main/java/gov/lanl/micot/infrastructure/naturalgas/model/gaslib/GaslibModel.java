package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModelImpl;

/**
 * Definition of a gaslib model
 * @author Russell Bent
 */
public class GaslibModel extends NaturalGasModelImpl {
		
	/**
	 * Constructor
	 */
	public GaslibModel() {
		super();
		setJunctionFactory(new GaslibJunctionFactory());
    setValveFactory(new GaslibValveFactory());
    setControlValveFactory(new GaslibControlValveFactory());
    setWellFactory(new GaslibWellFactory());
    setPipeFactory(new GaslibPipeFactory());
    setShortPipeFactory(new GaslibShortPipeFactory());
    setCityGateFactory(new GaslibCityGateFactory());
    setCompressorFactory(new GaslibCompressorFactory());
    setReservoirFactory(new GaslibReservoirFactory());
    setResistorFactory(new GaslibResistorFactory());

	}
		
	@Override
	public GaslibJunctionFactory getJunctionFactory() {
    return (GaslibJunctionFactory) super.getJunctionFactory();
	}

	@Override
	public GaslibValveFactory getValveFactory() {
    return (GaslibValveFactory) super.getValveFactory();
	}
	
	@Override
	public GaslibControlValveFactory getControlValveFactory() {
    return (GaslibControlValveFactory) super.getControlValveFactory();
	}
	
	@Override
	public GaslibWellFactory getWellFactory() {		
    return (GaslibWellFactory) super.getWellFactory();
	}

	@Override
	public GaslibPipeFactory getPipeFactory() {
    return (GaslibPipeFactory) super.getPipeFactory();
	}

	@Override
	public GaslibShortPipeFactory getShortPipeFactory() {
    return (GaslibShortPipeFactory) super.getShortPipeFactory();
	}
	
	@Override
	public GaslibCityGateFactory getCityGateFactory() {
    return (GaslibCityGateFactory) super.getCityGateFactory();
	}

	@Override
	public GaslibCompressorFactory getCompressorFactory() {
    return (GaslibCompressorFactory) super.getCompressorFactory();
	}

	@Override
	public GaslibReservoirFactory getReservoirFactory() {
    return (GaslibReservoirFactory) super.getReservoirFactory();
	}
	
	@Override
	public GaslibResistorFactory getResistorFactory() {
	  return (GaslibResistorFactory) super.getResistorFactory();
	}

  @Override
  protected NaturalGasModelImpl constructClone() {
    return new GaslibModel();
  }
}
