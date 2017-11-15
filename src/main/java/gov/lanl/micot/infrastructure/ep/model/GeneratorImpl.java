package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.util.math.MathUtils;

/**
 * Implementation of generators
 * 
 * @author Russell Bent
 */
public class GeneratorImpl extends ElectricPowerProducerImpl implements Generator {

  private Set<GeneratorChangeListener> listeners        = null;
  
  /**
   * Constructor
   */
  protected GeneratorImpl(long assetId) {
    super();
    listeners = new HashSet<GeneratorChangeListener>();
    setAttribute(Generator.ASSET_ID_KEY, assetId);
  }

   @Override
  public void addGeneratorDataListener(GeneratorChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeGeneratorDataListener(GeneratorChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (GeneratorChangeListener listener : listeners) {
      listener.generatorDataChanged(this, attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key, object);
    fireDataChangeEvent(key);
  }

  @Override
  public GeneratorTypeEnum getType() {
    return getAttribute(TYPE_KEY, GeneratorTypeEnum.class);
  }

  @Override
  public void setType(GeneratorTypeEnum type) {
    setAttribute(TYPE_KEY, type);
  }
  
  @Override
  public Number getCapacityFactor() {
    Number capacityFactor = getAttribute(CAPACITY_FACTOR_KEY, Number.class);
    if (capacityFactor == null) {
      return 1;
    }    
    return capacityFactor;
  }

  @Override
  public void setCapacityFactor(Number capacityFactor) {
    setAttribute(CAPACITY_FACTOR_KEY,capacityFactor); 
  }

  @Override
  public void setGeneration(Set<Generator> generators) {
    Number realMax = 0;
    Number reactiveMax = 0;
    Number actualReal = 0;
    Number actualReactive = 0;

    for (Generator state : generators) {
      if (state.getStatus() == true) {
        realMax = MathUtils.ADD(state.computeActualRealGenerationMax(),realMax);
        reactiveMax = MathUtils.ADD(state.computeActualReactiveGenerationMax(), reactiveMax);
        actualReal = MathUtils.ADD(actualReal, state.getRealGeneration());
        actualReactive = MathUtils.ADD(actualReactive, state.getReactiveGeneration());
      }
    }

    Number realPercentage = MathUtils.DIVIDE(actualReal,realMax);
    Number reactivePercentage = MathUtils.DIVIDE(actualReactive,reactiveMax);

    setReactiveGeneration(MathUtils.MULTIPLY(reactivePercentage,computeActualReactiveGenerationMax()));
    setRealGeneration(MathUtils.MULTIPLY(realPercentage,computeActualRealGenerationMax()));
  }

  @Override
  public Number computeActualRealGenerationMax() {
    return MathUtils.MULTIPLY(getCapacityFactor(),getDesiredRealGenerationMax());
  }

  @Override
  public Number computeActualReactiveGenerationMax() {
    return MathUtils.MULTIPLY(getCapacityFactor(),getDesiredReactiveMax());
  }

  @Override
  public void makeGenerationBoundsFeasible() {
    setReactiveMin(Math.min(getReactiveMin(), MathUtils.MIN(computeActualReactiveGenerationMax()).doubleValue()));
    setRealGenerationMin(Math.min(getRealGenerationMin(), MathUtils.MIN(computeActualRealGenerationMax()).doubleValue()));    
  }

  @Override
  public GeneratorImpl clone() {
    GeneratorImpl newGenerator = new GeneratorImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newGenerator);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newGenerator;
  }

}
