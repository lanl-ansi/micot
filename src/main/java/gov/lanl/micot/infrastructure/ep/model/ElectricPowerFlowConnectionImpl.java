package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.util.math.MathUtils;


/**
 * Implementation of the electric power flow edges
 * @author 210117
 *
 */
public abstract class ElectricPowerFlowConnectionImpl extends ElectricPowerConnectionImpl implements ElectricPowerFlowConnection {

  /**
   * Constructor
   * @param identifierKeys
   * @param oredKeys
   * @param additiveKeys
   * @param subtractiveKeys
   */
  public ElectricPowerFlowConnectionImpl() {
    super();
  }
  
  @Override
  public Double getResistance() {
    return getAttribute(RESISTANCE_KEY, Double.class);
  }

  @Override
  public void setResistance(Number resistance) {
    setAttribute(RESISTANCE_KEY,resistance);
  }

  @Override
  public Number getMWFlow() {
    return getAttribute(MW_FLOW_KEY, Number.class);
  }

  @Override
  public void setMWFlow(Number flow) {
    setAttribute(MW_FLOW_KEY,flow);
  }

  @Override
  public Number getMVarFlow() {
    return getAttribute(MVAR_FLOW_KEY, Number.class);
  }

  @Override
  public void setMVarFlow(Number flow) {
    setAttribute(MVAR_FLOW_KEY,flow);
  }
  
  @Override
  public Double getReactance() {
    return getAttribute(REACTANCE_KEY, Double.class);
  }

  @Override
  public void setReactance(Number reactance) {
    setAttribute(REACTANCE_KEY,reactance);
  }

  @Override
  public Double getCapacityRating() {
    return getAttribute(CAPACITY_RATING_KEY, Number.class).doubleValue();
  }

  @Override
  public Double getLineCharging() {
    return getAttribute(LINE_CHARGING_KEY, Double.class);
  }

  @Override
  public Double getLongTermEmergencyCapacityRating() {
    return getAttribute(LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY, Double.class);
  }

  @Override
  public Double getShortTermEmergencyCapacityRating() {
    return getAttribute(SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY, Double.class);
  }

  @Override
  public void setCapacityRating(Number capacityRating) {
    setAttribute(CAPACITY_RATING_KEY,capacityRating);
  }

  @Override
  public void setLineCharging(Number lineCharging) {
    setAttribute(LINE_CHARGING_KEY,lineCharging);
  }

  @Override
  public void setLongTermEmergencyCapacityRating(Number capacityRating) {
    setAttribute(LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY,capacityRating);
  }

  @Override
  public void setShortTermEmergencyCapacityRating(Number capacityRating) {
    setAttribute(SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY,capacityRating);
  }

  @Override
  public Number getCapacity() {
    return getCapacityRating();
  }

  @Override
  public void setCapacity(Number capacity) {
    setCapacityRating(capacity.doubleValue());
  }

  @Override
  public Number getFlow() {
    if (getMWFlow() == null || getMVarFlow() == null) {
      return 0;
    }
  	return MathUtils.SIGNED_NORM(getMWFlow(), getMVarFlow());
  }

  @Override
  public void setFlow(Number flow) {
    setMWFlow(flow);
    setMVarFlow(0.0);
  }

  @Override
  public Double getSusceptance() {
    return -getReactance() / ((getReactance() * getReactance()) + (getResistance() * getResistance()));
  }
  
  @Override
  public Double getConductance() {
    return getResistance() / ((getReactance() * getReactance()) + (getResistance() * getResistance()));
  }
  
  @Override
  public Double getRealLoss() {
    return getAttribute(REAL_LOSS_KEY, Double.class);
  }
  
  @Override
  public void setRealLoss(double loss) {
    setAttribute(REAL_LOSS_KEY,loss);
  }
  
  @Override
  public Double getReactiveLoss() {
    return getAttribute(REACTIVE_LOSS_KEY, Double.class);
  }
  
  @Override
  public void setReactiveLoss(double loss) {
    setAttribute(REACTIVE_LOSS_KEY,loss);
  }

}
