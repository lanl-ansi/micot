package gov.lanl.micot.infrastructure.optimize.vns;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.OptimizerFactoryImpl;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;
import gov.lanl.micot.util.io.Flags;

/**
 * A factory for creating variable neighborhood search algorithms
 * @author Russell Bent
 */
public abstract class VariableNeighborhoodSearchFactoryImpl<N extends Node, M extends Model> extends OptimizerFactoryImpl<N, M> implements VariableNeighborhoodSearchFactory<N,M> {
	
	/**
	 *  Constructor
	 */
	public VariableNeighborhoodSearchFactoryImpl() {
	  super();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void addVariableFactories(OptimizerFlags flags,  VariableNeighborhoodSearchOptimizer<N,M> optimizer) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  Collection<Flags> variableFactories = flags.getCollection(OptimizerFlags.VARIABLE_FACTORIES_KEY, Flags.class);
    for (Flags f : variableFactories) {
      Object rf = f.get(VariableNeighborhoodSearchOptimizerFlags.RELAXED_VARIABLE_FACTORY_KEY);
      Object ff = f.get(VariableNeighborhoodSearchOptimizerFlags.VARIABLE_FACTORY_KEY);
      Boolean isDiscrete = f.getBoolean(VariableNeighborhoodSearchOptimizerFlags.IS_DISCRETE_KEY);
            
      VariableFactory relaxFactory = getVariableFactory(rf);
      VariableFactory fullFactory = getVariableFactory(ff);      
      optimizer.addVariableFactory(relaxFactory, fullFactory, isDiscrete);
    }
  }
	
	/**
	 * Add the variable factories
	 * @param flags
	 * @param simulator
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
  protected void addVariableFactories(OptimizerFlags flags,  Optimizer<N,M> optimizer) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
	  System.err.println("Warning: Variable neighborhood search needs to know if a variable is an lp relaxation variable or not.");
	  super.addVariableFactories(flags, optimizer);
	}	
}
