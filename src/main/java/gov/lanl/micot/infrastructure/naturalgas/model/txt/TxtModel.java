package gov.lanl.micot.infrastructure.naturalgas.model.txt;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModelImpl;

/**
 * Definition of a txt model model
 * 
 * @author Russell Bent
 */
public class TxtModel extends NaturalGasModelImpl {

  /**
   * Constructor
   */
  public TxtModel() {
    super();
    setJunctionFactory(new TxtJunctionFactory());
    setValveFactory(new TxtValveFactory());
    setControlValveFactory(new TxtControlValveFactory());
    setWellFactory(new TxtWellFactory());
    setPipeFactory(new TxtPipeFactory());
    setShortPipeFactory(new TxtShortPipeFactory());
    setCityGateFactory(new TxtCityGateFactory());
    setCompressorFactory(new TxtCompressorFactory());
    setReservoirFactory(new TxtReservoirFactory());
    setResistorFactory(new TxtResistorFactory());
  }

  @Override
  public TxtJunctionFactory getJunctionFactory() {
    return (TxtJunctionFactory) super.getJunctionFactory();
  }

  @Override
  public TxtWellFactory getWellFactory() {
    return (TxtWellFactory) super.getWellFactory();
  }

  @Override
  public TxtValveFactory getValveFactory() {
    return (TxtValveFactory) super.getValveFactory();
  }

  @Override
  public TxtControlValveFactory getControlValveFactory() {
    return (TxtControlValveFactory) super.getControlValveFactory();
  }

  @Override
  public TxtPipeFactory getPipeFactory() {
    return (TxtPipeFactory) super.getPipeFactory();
  }

  @Override
  public TxtShortPipeFactory getShortPipeFactory() {
    return (TxtShortPipeFactory) super.getShortPipeFactory();
  }

  @Override
  public TxtCityGateFactory getCityGateFactory() {
    return (TxtCityGateFactory) super.getCityGateFactory();
  }

  @Override
  public TxtCompressorFactory getCompressorFactory() {
    return (TxtCompressorFactory) super.getCompressorFactory();
  }

  @Override
  public TxtReservoirFactory getReservoirFactory() {
    return (TxtReservoirFactory) super.getReservoirFactory();
  }
  
  @Override
  public TxtResistorFactory getResistorFactory() {
    return (TxtResistorFactory) super.getResistorFactory();
  }


  @Override
  protected NaturalGasModelImpl constructClone() {
    return new TxtModel();
  }

}
