package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * Some common bus implementation information
 * 
 * @author Russell Bent
 */
public class BusImpl extends ComponentImpl implements Bus {

  private Set<BusChangeListener> listeners           = null;
  
  /**
   * Constructor
   */
  protected BusImpl(long assetId) {
    super();
    listeners = new HashSet<BusChangeListener>();
    setAttribute(Bus.ASSET_ID_KEY, assetId);
  }
  
  @Override
  public Number getVoltagePU() {
    return getAttribute(VOLTAGE_PU_KEY,Double.class);
  }

  @Override
  public void setVoltagePU(Number pu) {
    setAttribute(VOLTAGE_PU_KEY,pu);
  }

  @Override
  public Double getRemoteVoltagePU() {
    return getAttribute(REMOTE_VOLTAGE_PU_KEY,Double.class);
  }

  @Override
  public void setRemoteVoltagePU(double pu) {
    setAttribute(REMOTE_VOLTAGE_PU_KEY,pu);
  }

  @Override
  public Number getPhaseAngle() {
    return getAttribute(PHASE_ANGLE_KEY,Number.class);
  }

  @Override
  public void setPhaseAngle(Number theta) {
    setAttribute(PHASE_ANGLE_KEY,theta);
  }

  @Override
  public double getSystemVoltageKV() {
    return getAttribute(SYSTEM_VOLTAGE_KV_KEY,Double.class);
  }

  @Override
  public void setSystemVoltageKV(double kv) {
    setAttribute(SYSTEM_VOLTAGE_KV_KEY,kv);
  }

  @Override
  public void addBusChangeListener(BusChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeBusChangeListener(BusChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireBusDataChangeEvent(Object attribute) {
    for (BusChangeListener listener : listeners) {
      listener.busDataChanged(this,attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireBusDataChangeEvent(key);
  }

  @Override
  public double getMaximumVoltagePU() {
    Double pu = getAttribute(MAXIMUM_VOLTAGE_PU_KEY, Double.class);
    if (pu == null) {
      return DEFAULT_MAXIMUM_VOLTAGE;
    }
    return pu;
  }

  @Override
  public void setMaximumVoltagePU(double pu) {
    setAttribute(MAXIMUM_VOLTAGE_PU_KEY, pu);
  }

  @Override
  public double getMinimumVoltagePU() {
    Double pu = getAttribute(MINIMUM_VOLTAGE_PU_KEY, Double.class);
    if (pu == null) {
      return DEFAULT_MINIMUM_VOLTAGE;
    }
    return pu;
  }

  @Override
  public void setMinimumVoltagePU(double pu) {
    setAttribute(MINIMUM_VOLTAGE_PU_KEY, pu);
  }

  @Override
  public void setOwnerName(String ownerName) {
    setAttribute(OWNER_KEY, ownerName);
  }

  @Override
  public String getOwnerName() {
    String owner = getAttribute(OWNER_KEY, String.class);
    if (owner == null) {
      return "";
    }
    return owner;
  }
  
  @Override
  public BusImpl clone() {
    BusImpl newBus = new BusImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newBus);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newBus;
  }
}
