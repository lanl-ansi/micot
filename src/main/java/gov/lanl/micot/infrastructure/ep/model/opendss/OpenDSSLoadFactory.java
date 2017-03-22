package gov.lanl.micot.infrastructure.ep.model.opendss;

import gov.lanl.micot.infrastructure.ep.io.opendss.OpenDSSIOConstants;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.LoadFactory;
import gov.lanl.micot.util.io.dcom.ComObject;

/**
 * Factory for creating loads
 * 
 * @author Russell Bent
 */
public class OpenDSSLoadFactory extends LoadFactory {

  private static final String LEGACY_TAG = "OPENDSS";

  /**
   * Singleton constructor
   */
  protected OpenDSSLoadFactory() {
  }

  /**
   * Creates a bus and data from a bus
   * 
   * @param line
   * @return
   */
  public Load createLoad(ComObject iLoad, Bus bus, ComObject activeLoad) {
    String legacyid = iLoad.getString(OpenDSSIOConstants.LOAD_NAME);
    double reactiveLoad = iLoad.getDouble(OpenDSSIOConstants.LOAD_KVAR) / 1000.0;
    double realLoad = iLoad.getDouble(OpenDSSIOConstants.LOAD_KW) / 1000.0;
    Load load = registerLoad(legacyid);
    initializeLoad(load, bus, realLoad, reactiveLoad);
    fill(load, iLoad, bus, activeLoad);
    return load;
  }

  /**
   * Creates an opendss load from another load object
   * 
   * @param load
   * @param loadData
   * @param busId
   * @return
   */
  protected Load createLoad(Load load, Bus bus) {
    throw new RuntimeException("OpenDSSLoadFactory::createLoad");
    // TODO
  }

  @Override
  protected Load createEmptyLoad(Bus bus) {
    String legacyId = bus.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
    Load load = registerLoad(legacyId);
    return load;
  }

  /**
   * Register the load
   * 
   * @param legacyId
   * @param bus
   * @return
   */
  private Load registerLoad(String legacyId) {
    Load load = getLegacy(LEGACY_TAG, legacyId);
    if (load == null) {
      load = createNewLoad();
      load.setAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, legacyId);
      load.addOutputKey(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, load);
    }
    return load;
  }

  /**
   * Fill the load with the ieiss load
   * 
   * @param load
   * @param iLoad
   */
  private void fill(Load load, ComObject iLoad, Bus bus, ComObject activeLoad) {
    String legacyid = load.getAttribute(OpenDSSModelConstants.OPENDSS_LEGACY_ID_KEY, String.class);
    double reactiveLoad = iLoad.getDouble(OpenDSSIOConstants.LOAD_KVAR) / 1000.0;
    double realLoad = iLoad.getDouble(OpenDSSIOConstants.LOAD_KW) / 1000.0;
    double actualReactiveLoad = iLoad.getDouble(OpenDSSIOConstants.LOAD_KVAR) / 1000.0;
    double actualRealLoad = iLoad.getDouble(OpenDSSIOConstants.LOAD_KW) / 1000.0;
    boolean status = activeLoad.getBoolean(OpenDSSIOConstants.LOAD_STATUS);

    ComObject property = activeLoad.call(OpenDSSIOConstants.PROPERTIES, OpenDSSIOConstants.LOAD_PHASES);
    int phases = Integer.parseInt(property.getString(OpenDSSIOConstants.PROPERTY_VALUE));
    double reactivePhaseA = 0;
    double realPhaseA = 0;
    double reactivePhaseB = 0;
    double realPhaseB = 0;
    double reactivePhaseC = 0;
    double realPhaseC = 0;
    boolean hasPhaseA = false;
    boolean hasPhaseB = false;
    boolean hasPhaseC = false;

    double minPU = iLoad.getDouble(OpenDSSIOConstants.LOAD_MIN_PU);
    double maxPU = iLoad.getDouble(OpenDSSIOConstants.LOAD_MAX_PU);

    if (phases == 3) {
      reactivePhaseA = reactiveLoad / 3.0;
      realPhaseA = realLoad / 3.0;
      reactivePhaseB = reactiveLoad / 3.0;
      realPhaseB = realLoad / 3.0;
      reactivePhaseC = reactiveLoad / 3.0;
      realPhaseC = realLoad / 3.0;
      hasPhaseA = hasPhaseB = hasPhaseC = true;
    }
    else if (phases == 1) {
      char phase = legacyid.charAt(legacyid.length() - 1);
      if (phase == 'a') {
        reactivePhaseA = reactiveLoad;
        realPhaseA = realLoad;
        hasPhaseA = true;
      }
      else if (phase == 'b') {
        reactivePhaseB = reactiveLoad;
        realPhaseB = realLoad;
        hasPhaseB = true;
      }
      else if (phase == 'c') {
        reactivePhaseC = reactiveLoad;
        realPhaseC = realLoad;
        hasPhaseC = true;
      }
      else {
        throw new RuntimeException("Don't have code for phase " + phase);
      }

    }
    else {
      throw new RuntimeException("Don't have code for phases " + phases);
    }

    load.setAttribute(Load.NUM_PHASE_KEY, phases);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, reactivePhaseA);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, realPhaseA);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_A_KEY, reactivePhaseA);
    load.setAttribute(Load.DESIRED_REAL_LOAD_A_KEY, realPhaseA);
    load.setAttribute(Load.HAS_PHASE_A_KEY, hasPhaseA);

    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, reactivePhaseB);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, realPhaseB);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_B_KEY, reactivePhaseB);
    load.setAttribute(Load.DESIRED_REAL_LOAD_B_KEY, realPhaseB);
    load.setAttribute(Load.HAS_PHASE_B_KEY, hasPhaseB);

    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, reactivePhaseC);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, realPhaseC);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_C_KEY, reactivePhaseC);
    load.setAttribute(Load.DESIRED_REAL_LOAD_C_KEY, realPhaseC);
    load.setAttribute(Load.HAS_PHASE_C_KEY, hasPhaseC);

    load.setDesiredStatus(status);
    load.setActualStatus(status);
    load.setDesiredReactiveLoad(reactiveLoad);
    load.setDesiredRealLoad(realLoad);
    load.setActualReactiveLoad(load.getActualStatus() ? actualReactiveLoad : 0.0);
    load.setActualRealLoad(load.getActualStatus() ? actualRealLoad : 0.0);
    load.setCoordinate(bus.getCoordinate());

    bus.setMinimumVoltagePU(minPU);
    bus.setMaximumVoltagePU(maxPU);

  }

  /**
   * Create load from text data
   * 
   * @param loadData
   * @param bus
   * @return
   */
  public Load createLoad(String loadData, Bus bus) {
    loadData = loadData.replace("\"", "");
    String legacyid = OpenDSSIOConstants.getData(loadData, "Load", ".");
    int phases = Integer.parseInt(OpenDSSIOConstants.getData(loadData, "phases", "="));

    double realLoad = Double.parseDouble(OpenDSSIOConstants.getData(loadData, "kW", "=")) / 1000.0;
    double reactiveLoad = Double.parseDouble(OpenDSSIOConstants.getData(loadData, "kvar", "=")) / 1000.0;
    Load load = registerLoad(legacyid);
    initializeLoad(load, bus, realLoad, reactiveLoad);
    fill(load, legacyid, bus, reactiveLoad, realLoad, phases);
    return load;
  }

  /**
   * Fill from a few sources
   * 
   * @param load
   * @param legacyid
   * @param bus
   * @param reactiveLoad
   * @param realLoad
   * @param phases
   */
  private void fill(Load load, String legacyid, Bus bus, double reactiveLoad, double realLoad, int phases) {
    double actualReactiveLoad = reactiveLoad;
    double actualRealLoad = realLoad;
    boolean status = true;

    double reactivePhaseA = 0;
    double realPhaseA = 0;
    double reactivePhaseB = 0;
    double realPhaseB = 0;
    double reactivePhaseC = 0;
    double realPhaseC = 0;
    boolean hasPhaseA = false;
    boolean hasPhaseB = false;
    boolean hasPhaseC = false;

    if (phases == 3) {
      reactivePhaseA = reactiveLoad / 3.0;
      realPhaseA = realLoad / 3.0;
      reactivePhaseB = reactiveLoad / 3.0;
      realPhaseB = realLoad / 3.0;
      reactivePhaseC = reactiveLoad / 3.0;
      realPhaseC = realLoad / 3.0;
      hasPhaseA = hasPhaseB = hasPhaseC = true;
    }
    else if (phases == 1) {
      char phase = legacyid.charAt(legacyid.length() - 1);
      if (phase == 'a') {
        reactivePhaseA = reactiveLoad;
        realPhaseA = realLoad;
        hasPhaseA = true;
      }
      else if (phase == 'b') {
        reactivePhaseB = reactiveLoad;
        realPhaseB = realLoad;
        hasPhaseB = true;
      }
      else if (phase == 'c') {
        reactivePhaseC = reactiveLoad;
        realPhaseC = realLoad;
        hasPhaseC = true;
      }
      else {
        throw new RuntimeException("Don't have code for phase " + phase);
      }

    }
    else {
      throw new RuntimeException("Don't have code for phases " + phases);
    }

    load.setAttribute(Load.NUM_PHASE_KEY, phases);
    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_A_KEY, reactivePhaseA);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_A_KEY, realPhaseA);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_A_KEY, reactivePhaseA);
    load.setAttribute(Load.DESIRED_REAL_LOAD_A_KEY, realPhaseA);
    load.setAttribute(Load.HAS_PHASE_A_KEY, hasPhaseA);

    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_B_KEY, reactivePhaseB);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_B_KEY, realPhaseB);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_B_KEY, reactivePhaseB);
    load.setAttribute(Load.DESIRED_REAL_LOAD_B_KEY, realPhaseB);
    load.setAttribute(Load.HAS_PHASE_B_KEY, hasPhaseB);

    load.setAttribute(Load.ACTUAL_REACTIVE_LOAD_C_KEY, reactivePhaseC);
    load.setAttribute(Load.ACTUAL_REAL_LOAD_C_KEY, realPhaseC);
    load.setAttribute(Load.DESIRED_REACTIVE_LOAD_C_KEY, reactivePhaseC);
    load.setAttribute(Load.DESIRED_REAL_LOAD_C_KEY, realPhaseC);
    load.setAttribute(Load.HAS_PHASE_C_KEY, hasPhaseC);

    load.setDesiredStatus(status);
    load.setActualStatus(status);
    load.setDesiredReactiveLoad(reactiveLoad);
    load.setDesiredRealLoad(realLoad);
    load.setActualReactiveLoad(load.getActualStatus() ? actualReactiveLoad : 0.0);
    load.setActualRealLoad(load.getActualStatus() ? actualRealLoad : 0.0);
    load.setCoordinate(bus.getCoordinate());
  }

}
