package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * Implementation of a switch capacitor
 * @author Russell Bent
 */
public class ShuntCapacitorSwitchImpl extends ComponentImpl implements ShuntCapacitorSwitch {

  protected static final long serialVersionUID = 1L;
  
  private Set<ShuntCapacitorSwitchChangeListener> listeners = null;

  /**
   * Constructor
   */
  protected ShuntCapacitorSwitchImpl(long assetId) {
    super();
    listeners = new HashSet<ShuntCapacitorSwitchChangeListener>();
    setAttribute(ShuntCapacitorSwitch.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param bus
   */
//  public ShuntCapacitorSwitchImpl(ShuntCapacitorSwitch capacitor) {
  //  super(capacitor);
   // listeners = new HashSet<ShuntCapacitorSwitchChangeListener>();    
  //}
  
  @Override 
  public double getScheduledLowVoltage() {
    return getAttribute(SCHEDULED_LOW_VOLTAGE_KEY,Double.class);
  }

  @Override
  public void setScheduledLowVoltage(double scheduledLowVoltage) {
    setAttribute(SCHEDULED_LOW_VOLTAGE_KEY,scheduledLowVoltage);
  }

  @Override 
  public double getScheduleHighVoltage() {
    return getAttribute(SCHEDULED_HIGH_VOLTAGE_KEY,Double.class);
  }

  @Override
  public void setScheduleHighVoltage(double scheduledHighVoltage) {
    setAttribute(SCHEDULED_HIGH_VOLTAGE_KEY,scheduledHighVoltage);
  }
  
  @Override
  public double getDesiredVoltage() {
    return getAttribute(DESIRED_VOLTAGE_KEY,Double.class);
  }

  @Override
  public void setDesiredVoltage(double desiredVoltage) {
    setAttribute(DESIRED_VOLTAGE_KEY,desiredVoltage);
  }

  @Override
  public double getRemoteDesiredVoltage() {
    return getAttribute(REMOTED_DESIRED_VOLTAGE_KEY,Double.class);
  }

  @Override
  public void setRemoteDesiredVoltage(double remotedDesiredVoltage) {
    setAttribute(REMOTED_DESIRED_VOLTAGE_KEY,remotedDesiredVoltage);
  }
  
  @Override
  public double getInitialSusceptance() {
    return getAttribute(INITIAL_SUSCEPTANCE_KEY,Double.class);
  }

  @Override
  public void setInitialSusceptance(double initialSusceptance) {
    setAttribute(INITIAL_SUSCEPTANCE_KEY,initialSusceptance);
  }

  @Override
  public double getSusceptance() {
    return getAttribute(SUSCEPTANCE_KEY,Double.class);
  }

  @Override
  public void setSusceptance(double susceptance) {
    setAttribute(SUSCEPTANCE_KEY,susceptance);
  }
    
  @Override
  public void addShuntCapacitorSwitchChangeListener(ShuntCapacitorSwitchChangeListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeShuntCapacitorSwitchChangeListener(ShuntCapacitorSwitchChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire a shunt capacitor switch data change event
   */
  protected void fireShuntCapacitorSwitchDataChangeEvent(Object attribute) {
    for (ShuntCapacitorSwitchChangeListener listener : listeners) {
      listener.shuntCapacitorSwitchDataChanged(this, attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireShuntCapacitorSwitchDataChangeEvent(key);
  }
  
  @Override
  public ShuntCapacitorSwitchImpl clone() {
//    ShuntCapacitorSwitchImpl newSwitch = new ShuntCapacitorSwitchImpl((ShuntCapacitorSwitch)getBaseData());
    ShuntCapacitorSwitchImpl newSwitch = new ShuntCapacitorSwitchImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newSwitch);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newSwitch;
  }

  @Override
  public void setMaxMW(double maxMW) {
    setAttribute(MAX_MW_KEY,maxMW);    
  }

  @Override
  public double getMaxMW() {
    return getAttribute(MAX_MW_KEY) == null ? 0 : getAttribute(MAX_MW_KEY,Double.class);
  }

  @Override
  public void setMinMW(double minMW) {
    setAttribute(MIN_MW_KEY,minMW);    
  }

  @Override
  public double getMinMW() {
    return getAttribute(MIN_MW_KEY) == null ? 0 : getAttribute(MIN_MW_KEY,Double.class);
  }

  @Override
  public void setMaxMVar(double maxMVar) {
    setAttribute(MAX_MVAR_KEY,maxMVar);    
  }

  @Override
  public double getMaxMVar() {
    return getAttribute(MAX_MVAR_KEY) == null ? 0 : getAttribute(MAX_MVAR_KEY,Double.class);
  }

  @Override
  public void setMinMVar(double minMVar) {
    setAttribute(MIN_MVAR_KEY,minMVar);    
  }

  @Override
  public double getMinMVar() {
    return getAttribute(MIN_MVAR_KEY) == null ? 0 : getAttribute(MIN_MVAR_KEY,Double.class);
  }
  
  @Override
  public Number getReactiveCompensation() {
    return getAttribute(REACTIVE_COMPENSATION_KEY,Number.class) == null ? 0.0 : getAttribute(REACTIVE_COMPENSATION_KEY,Number.class);
  }

  @Override
  public Number getRealCompensation() {
    return getAttribute(REAL_COMPENSATION_KEY,Number.class) == null ? 0.0 : getAttribute(REAL_COMPENSATION_KEY,Number.class);
  }

  @Override
  public void setReactiveCompensation(Number s) {
    setAttribute(REACTIVE_COMPENSATION_KEY,s);
  }

  @Override
  public void setRealCompensation(Number s) {
    setAttribute(REAL_COMPENSATION_KEY,s);
  }

}
