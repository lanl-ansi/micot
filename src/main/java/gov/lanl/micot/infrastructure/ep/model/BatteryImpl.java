package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a simple implementation of a battery that
 * is used as a basis for most battery implementations
 * @author Russell Bent
 *
 */
public class BatteryImpl extends ElectricPowerProducerImpl implements Battery {

	private Set<BatteryChangeListener>   listeners        = null;
	
  /**
   * Constructor
   */
  protected BatteryImpl(long assetId) {
    super();
    listeners = new HashSet<BatteryChangeListener>();
    setAttribute(Battery.ASSET_ID_KEY, assetId);
  }

  @Override
	public void setEnergyCapacity(Number energy) {
		setAttribute(ENERGY_CAPACITY_KEY,energy);
	}

	@Override
	public Number getEnergyCapacity() {
    return getAttribute(ENERGY_CAPACITY_KEY, Number.class);
	}

	@Override
	public void setUsedEnergyCapacity(Number energy) {
		setAttribute(USED_ENERGY_CAPACITY_KEY,energy);
	}

	@Override
	public Number getUsedEnergyCapacity() {
    return getAttribute(USED_ENERGY_CAPACITY_KEY, Number.class);
	}

	@Override
	public void addBatteryDataListener(BatteryChangeListener listener) {
		listeners.add(listener);		
	}

	@Override
	public void removeBatteryDataListener(BatteryChangeListener listener) {
		listeners.remove(listener);
	}

	@Override
	public Number getAvailableMaximumRealProduction() {
		return Math.min(getDesiredRealGenerationMax(), getUsedEnergyCapacity().doubleValue());
	}

	@Override
	public Number getAvailableMinimumRealProduction() {
		return Math.max(getRealGenerationMin(), -(getEnergyCapacity().doubleValue() - getUsedEnergyCapacity().doubleValue()));
	}

	@Override
	public Number getAvailableMaximumReactiveProduction() {
		return Math.min(getDesiredReactiveMax(), 0.0);
	}

	@Override
	public Number getAvailableMinimumReactiveProduction() {
		return Math.max(getReactiveMin(), 0.0);
	}

	@Override
  public BatteryImpl clone() {
    BatteryImpl newBattery = new BatteryImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newBattery);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newBattery;
  }
}
