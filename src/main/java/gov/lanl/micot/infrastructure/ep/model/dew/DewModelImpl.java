package gov.lanl.micot.infrastructure.ep.model.dew;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.ep.io.dew.DewFile;
import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.model.Asset;

/**
 * This is a data structure for a MatPower model
 * 
 * @author Russell Bent
 */
public class DewModelImpl extends ElectricPowerModelImpl implements DewModel {

	protected static final long serialVersionUID = 0;

	private Dew engine = null;

	 static {
	    ElectricPowerModelFileFactory.registerClass(DewModel.class, DewFile.class);
	    ElectricPowerModelFileFactory.registerExtension("dewc",DewFile.class);
	  }

	
	/**
	 * Constructor
	 */
	public DewModelImpl(Dew engine) {
	  super();
	  this.engine = engine;	 
	  setLineFactory(new DewLineFactory());
	  setTransformerFactory(new DewTransformerFactory());
	  setIntertieFactory(new DewIntertieFactory());
	  setBusFactory(new DewBusFactory());
	  setLoadFactory(new DewLoadFactory());
	  setGeneratorFactory(new DewGeneratorFactory());
	  setCapacitorFactory(new DewShuntFactory());
	  setShuntCapacitorSwitchFactory(new DewSwitchedShuntFactory());
	  setBatteryFactory(new DewBatteryFactory());
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
	public DewLineFactory getLineFactory() {
		return (DewLineFactory) super.getLineFactory();
	}

	@Override
	public DewTransformerFactory getTransformerFactory() {
    return (DewTransformerFactory) super.getTransformerFactory();
	}
	
	@Override
	public DewIntertieFactory getIntertieFactory() {
    return (DewIntertieFactory) super.getIntertieFactory();
	}

	@Override
	public DewShuntFactory getShuntCapacitorFactory() {
    return (DewShuntFactory) super.getShuntCapacitorFactory();
	}

	@Override
	public DewSwitchedShuntFactory getShuntCapacitorSwitchFactory() {
    return (DewSwitchedShuntFactory) super.getShuntCapacitorSwitchFactory();
	}

	@Override
	public DewBusFactory getBusFactory() {
    return (DewBusFactory) super.getBusFactory();
	}

	@Override
	public DewGeneratorFactory getGeneratorFactory() {
    return (DewGeneratorFactory) super.getGeneratorFactory();
	}

	@Override
	public DewBatteryFactory getBatteryFactory() {
    return (DewBatteryFactory) super.getBatteryFactory();
	}
	
	@Override
	public DewLoadFactory getLoadFactory() {
    return (DewLoadFactory) super.getLoadFactory();
	}
  
  @Override
  protected ElectricPowerModelImpl constructClone() {
    try {
      syncModel();
    } 
    catch (Exception e) {
      e.printStackTrace();
    } 
    DewModelImpl model = new DewModelImpl(engine);
    return model;
  }
  
	/**
   * Sync the underling IEISS model with the attributes stored in the date and the top
	 * @throws DewException 
	 * @throws NumberFormatException 
   */
  public void syncModel() throws NumberFormatException, DewException {
    engine.clearCache();
    syncTopology();    
    syncStatus();
    
    // sync the models
    for (Generator generator : getGenerators()) {
      engine.syncGenerator(generator);
    }
    for (Battery battery : getBatteries()) {
      engine.syncBattery(battery);
    }
    for (Load load : getLoads()) {
      engine.syncLoad(load);
    }
    for (ShuntCapacitor capacitor : getShuntCapacitors()) {
      engine.syncCapacitor(capacitor);
    }
    for (Line line : getLines()) {
      engine.syncLine(line);
    }
    for (Transformer transformer : getTransformers()) {
      engine.syncTransformer(transformer);
    }
    for (Bus bus : getBuses()) {
      engine.syncBus(bus);
    }
  }

  /**
   * Since we have split many DEW components into multiple components in this model
   * we need to do some aggregation
   */
  public void syncStatus() {
    HashMap<DewLegacyId, Boolean> isFailed = new HashMap<DewLegacyId, Boolean>();
    HashMap<DewLegacyId, Boolean> status = new HashMap<DewLegacyId, Boolean>();

    for (Asset asset : getAssets()) {
      DewLegacyId legacyId = asset.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
      if (status.get(legacyId) == null) {
        status.put(legacyId, true);
        isFailed.put(legacyId, false);
      }      
      status.put(legacyId, status.get(legacyId) && asset.getStatus());
      isFailed.put(legacyId, isFailed.get(legacyId) || asset.getAttribute(Asset.IS_FAILED_KEY, Boolean.class));      
    }
    
    for (Asset asset : getAssets()) {
      DewLegacyId legacyId = asset.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
      asset.setAttribute(DewVariables.DEW_AGGREGATE_STATUS_KEY, status.get(legacyId));
      asset.setAttribute(DewVariables.DEW_AGGREGATE_FAILED_KEY, isFailed.get(legacyId));      
    }
  }
  
  /**
   * Quick and dirty function to grab the legacy id
   * @param asset
   * @return
   */
  private DewLegacyId getLegacyId(Asset asset) {
    return asset.getAttribute(DewVariables.DEW_LEGACY_ID_KEY, DewLegacyId.class);
  }

  /**
   * Get some cluster ids
   * @param bus
   * @return
   */
  @SuppressWarnings("unchecked")
  private Set<DewLegacyId> getClusterIds(Bus bus) {
    return bus.getAttribute(DewVariables.DEW_LEGACY_IDS_KEY) == null ? new HashSet<DewLegacyId>() : bus.getAttribute(DewVariables.DEW_LEGACY_IDS_KEY, Set.class);
  }

  
  /**
   * Need to remove/add any components to remain in sync
   */
  private void syncTopology() {
    Set<DewLegacyId> containedEntities = new HashSet<DewLegacyId>();

    // make sure all entities are added...
    for (Bus bus : getBuses()) {
      containedEntities.add(getLegacyId(bus));
      containedEntities.addAll(getClusterIds(bus));
      
      if (!engine.hasId(getLegacyId(bus)) && bus.getAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, Integer.class) >= 0) {
        engine.addBus(bus);
      }     
    }

    for (Load load : getLoads()) {
      containedEntities.add(getLegacyId(load));
      if (!engine.hasId(getLegacyId(load)) && load.getAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, Integer.class) >= 0) {
        engine.addLoad(load);
      }     
    }

    for (Generator generator : getGenerators()) {
      containedEntities.add(getLegacyId(generator));
      if (!engine.hasId(getLegacyId(generator)) && generator.getAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, Integer.class) >= 0) {
        engine.addGenerator(generator);
      }     
    }

    
    for (Line line : getLines()) {
      containedEntities.add(getLegacyId(line));
      if (!engine.hasId(getLegacyId(line)) && line.getAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, Integer.class) >= 0) {
        engine.addLine(line);
      }     
    }

    for (Transformer transformer : getTransformers()) {
      containedEntities.add(getLegacyId(transformer));
      if (!engine.hasId(getLegacyId(transformer)) && transformer.getAttribute(DewVariables.DEW_COMPONENT_TYPE_KEY, Integer.class) >= 0) {
        engine.addTransformer(transformer);
      }     
    }

    Collection<DewLegacyId> ids = engine.getComponentIds();
    Collection<DewLegacyId> toRemove = new ArrayList<DewLegacyId>();
    for (DewLegacyId id : ids) {
      if (!containedEntities.contains(id)) {
        toRemove.add(id);
      }
    }
    
    for (DewLegacyId id : toRemove) {
      engine.remove(id);
    }
  }

  /**
   * Get DEWEngine
   * @return
   */
  public Dew getDew() {
    return engine;
  }
  
  
  
  
}
