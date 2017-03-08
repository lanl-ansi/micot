package gov.lanl.micot.infrastructure.ep.simulate;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerProducer;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.simulate.SimulationExecutionException;
import gov.lanl.micot.util.math.MathUtils;

/**
 * Abstract class for interacting with the simulator
 * 
 * @author Russell Bent
 */
public abstract class ElectricPowerSimulatorImpl implements ElectricPowerSimulator {

	/**
	 * Constructor
	 */
	protected ElectricPowerSimulatorImpl() {
	}
		
	@Override
	public SimulatorSolveState executeSimulation(ElectricPowerModel modelState) {
      SimulatorSolveState solution = simulateModel(modelState);
			switch (solution) {
				case NON_CONVERGED_SOLUTION:
				case ERROR_SOLUTION:
				case RESTART_SOLUTION:
          modelState.setIsSolved(false);
					break;
				case CONVERGED_SOLUTION: 
          modelState.setIsSolved(true);
					break;
				default:
					System.err.println("Exiting, cannot recover from solver");
					throw new SimulationExecutionException("Exiting, cannot recover from solver");
			}
		
			return solution;
	}

	/**
	 * Simulates the model
	 * 
	 * @param model
	 * @return
	 */
	protected abstract SimulatorSolveState simulateModel(ElectricPowerModel modelState);
	
	@Override
	public SimulatorSolveState executeSimulation(Model modelState) {
		return executeSimulation((ElectricPowerModel)modelState);
	}
	
  /**
   * Update the model status
   * @param model
   */
  protected void updateModelStatus(Model model) {
    for (Asset asset : model.getAssets()) {
      asset.setActualStatus(asset.getDesiredStatus());
    }
  }

  /**
   * Function for balancing a model in a steady state way
   * @param model
   */
  protected double balanceSteadyStateRealPower(Collection<ElectricPowerNode> nodes) {    
    double totalProduction = 0;
    double totalConsumption = 0;  
    Vector<ElectricPowerProducer> generators = new Vector<ElectricPowerProducer>();
    Vector<Load> loads = new Vector<Load>();
    for (ElectricPowerNode node : nodes) {
      for (Load load : node.getComponents(Load.class)) {
        double consume = (node.getBus().getDesiredStatus() && load.getDesiredStatus()) ? load.getDesiredRealLoad().doubleValue() : 0.0;         
        totalConsumption += consume;   
        load.setActualRealLoad(consume);
        loads.add(load);
      }
      for (Generator generator : node.getComponents(Generator.class)) {
        double produce = (node.getBus().getDesiredStatus() && generator.getDesiredStatus()) ? generator.getDesiredRealGeneration().doubleValue() : 0.0;
        totalProduction += produce;
        generator.setActualRealGeneration(produce);
        generators.add(generator);
      }
      for (Battery battery : node.getComponents(Battery.class)) {
        double produce = (node.getBus().getDesiredStatus() && battery.getDesiredStatus()) ? battery.getDesiredRealGeneration().doubleValue() : 0.0;
        totalProduction += produce;
        battery.setActualRealGeneration(produce);
        generators.add(battery);
      }
    }

    if (totalProduction < totalConsumption) {
      TreeSet<ElectricPowerProducer> sorted = new TreeSet<ElectricPowerProducer>(new UnderRealGenerationComparator());
      sorted.addAll(generators);
      for (ElectricPowerProducer generator : sorted) {
        if (totalConsumption == totalProduction) {
          break;
        }
        
        if (!generator.getActualStatus()) {
          continue;
        }
        
        double realMax =(generator instanceof Generator) ? ((Generator) generator).computeActualRealGenerationMax().doubleValue() : ((Battery)generator).getAvailableMaximumRealProduction().doubleValue();
        double adjustment = Math.max(0, realMax - generator.getActualRealGeneration().doubleValue());
        adjustment = Math.min(adjustment, totalConsumption - totalProduction);
        totalProduction += adjustment;
        generator.setActualRealGeneration(generator.getActualRealGeneration().doubleValue() + adjustment);
      }
    }
    else if (totalProduction > totalConsumption) {
      TreeSet<ElectricPowerProducer> sorted = new TreeSet<ElectricPowerProducer>(new OverRealGenerationComparator());
      sorted.addAll(generators);
      for (ElectricPowerProducer generator : sorted) {
        if (totalConsumption == totalProduction) {
          break;
        }

        if (!generator.getActualStatus()) {
          continue;
        }
        
        double realMin =(generator instanceof Generator) ? ((Generator) generator).getRealGenerationMin() : ((Battery)generator).getAvailableMinimumRealProduction().doubleValue();
        double adjustment = Math.max(0, generator.getActualRealGeneration().doubleValue() - realMin);
        adjustment = Math.min(adjustment, totalProduction - totalConsumption);
        totalProduction -= adjustment;
        generator.setActualRealGeneration(generator.getActualRealGeneration().doubleValue() - adjustment);
      }
    }

    if (totalConsumption != totalProduction) {
      if (totalConsumption == 0) {
        for (ElectricPowerProducer generator : generators) {
          generator.setActualStatus(false);
          generator.setActualRealGeneration(0);
        }
        totalProduction = 0;
      }
      else {
        double ratio = totalProduction / totalConsumption;
        for (Load load : loads) {
          if (load.getActualStatus()) {
            load.setActualRealLoad(load.getActualRealLoad().doubleValue() * ratio);
          }
        }
        totalConsumption = totalProduction;
      }
    }
    return totalProduction;
  }


  /**
   * Function for balancing a model in a steady state
   * @param model
   */
  protected double balanceSteadyStateReactivePower(Collection<ElectricPowerNode> nodes) {    
    double totalProduction = 0;
    double totalConsumption = 0;  
    Vector<ElectricPowerProducer> generators = new Vector<ElectricPowerProducer>();
    Vector<Load> loads = new Vector<Load>();
    for (ElectricPowerNode node : nodes) {
      for (Load load : node.getComponents(Load.class)) {
        double consume = (node.getBus().getDesiredStatus() && load.getDesiredStatus()) ? load.getDesiredReactiveLoad().doubleValue() : 0.0;         
        totalConsumption += consume;   
        load.setActualReactiveLoad(consume);
        loads.add(load);
      }
      for (Generator generator : node.getComponents(Generator.class)) {
        double produce = (node.getBus().getDesiredStatus() && generator.getDesiredStatus()) ? generator.getDesiredReactiveGeneration().doubleValue() : 0.0;
        totalProduction += produce;
        generator.setActualReactiveGeneration(produce);
        generators.add(generator);
      }
      for (Battery battery : node.getComponents(Battery.class)) {
        double produce = (node.getBus().getDesiredStatus() && battery.getDesiredStatus()) ? battery.getDesiredReactiveGeneration().doubleValue() : 0.0;
        totalProduction += produce;
        battery.setActualReactiveGeneration(produce);
        generators.add(battery);
      }

    }

    if (totalProduction < totalConsumption) {
      TreeSet<ElectricPowerProducer> sorted = new TreeSet<ElectricPowerProducer>(new UnderReactiveGenerationComparator());
      sorted.addAll(generators);
      for (ElectricPowerProducer generator : sorted) {
        if (totalConsumption == totalProduction) {
          break;
        }

        double reactiveMax =(generator instanceof Generator) ? ((Generator) generator).computeActualReactiveGenerationMax().doubleValue() : ((Battery)generator).getAvailableMaximumReactiveProduction().doubleValue();
        double adjustment = Math.max(0, reactiveMax - generator.getActualReactiveGeneration().doubleValue());
        adjustment = Math.min(adjustment, totalConsumption - totalProduction);
        totalProduction += adjustment;
        generator.setActualReactiveGeneration(generator.getActualReactiveGeneration().doubleValue() + adjustment);
      }
    }
    else if (totalProduction > totalConsumption) {
      TreeSet<ElectricPowerProducer> sorted = new TreeSet<ElectricPowerProducer>(new OverReactiveGenerationComparator());
      sorted.addAll(generators);
      for (ElectricPowerProducer generator : sorted) {
        if (totalConsumption == totalProduction) {
          break;
        }

        double reactiveMin =(generator instanceof Generator) ? ((Generator) generator).getReactiveMin() : ((Battery)generator).getAvailableMinimumReactiveProduction().doubleValue();
        double adjustment = Math.max(0, generator.getActualReactiveGeneration().doubleValue() - reactiveMin);
        adjustment = Math.min(adjustment, totalProduction - totalConsumption);
        totalProduction -= adjustment;
        generator.setActualReactiveGeneration(generator.getActualReactiveGeneration().doubleValue() - adjustment);
      }
    }

    if (totalConsumption != totalProduction) {
      if (totalConsumption == 0) {
        for (ElectricPowerProducer generator : generators) {
          generator.setActualStatus(false);
          generator.setActualReactiveGeneration(0);
        }
        totalProduction = 0;
      }
      else {
        double ratio = totalProduction / totalConsumption;
        for (Load load : loads) {
          load.setActualReactiveLoad(load.getActualReactiveLoad().doubleValue() * ratio);
        }
        totalConsumption = totalProduction;
      }
    }
    return totalProduction;
  }
  
  /**
   * Function for finding the slack node
   * @param nodes
   * @return
   */
  protected ElectricPowerNode getSlackNode(SortedSet<ElectricPowerNode> nodes) {
  	for (ElectricPowerNode node : nodes) {
      if (node.getGenerator() != null && node.getGenerator().getType().equals(GeneratorTypeEnum.REFERENCE_BUS_TYPE) && node.getGenerator().getActualStatus()) {
      	return node;
      }
    }

  	for (ElectricPowerNode node : nodes) {
      if (node.getGenerator() != null && node.getGenerator().getActualStatus()) {
        return node;
      }
    }
  	return null;  	
  }
  
  
  /**
   * Under real generation comparator
   * @author Russell Bent
   *
   */
  private class UnderRealGenerationComparator implements Comparator<ElectricPowerProducer> {
    @Override
    public int compare(ElectricPowerProducer arg0, ElectricPowerProducer arg1) {
      double arg0Max =(arg0 instanceof Generator) ? ((Generator) arg0).computeActualRealGenerationMax().doubleValue() : ((Battery)arg0).getAvailableMaximumRealProduction().doubleValue();
      double arg1Max =(arg1 instanceof Generator) ? ((Generator) arg1).computeActualRealGenerationMax().doubleValue() : ((Battery)arg1).getAvailableMaximumRealProduction().doubleValue();
      // most space
      return -MathUtils.COMPARE_TO(MathUtils.SUBTRACT(arg0Max,arg0.getActualRealGeneration()),MathUtils.SUBTRACT(arg1Max,arg1.getActualRealGeneration()));
    }
  }

  /**
   * Comparator of over real generation
   * @author Russell Bent
   */
  private class OverRealGenerationComparator implements Comparator<ElectricPowerProducer> {
    @Override
    public int compare(ElectricPowerProducer arg0, ElectricPowerProducer arg1) {
      double arg0Min =(arg0 instanceof Generator) ? ((Generator) arg0).getRealGenerationMin() : ((Battery)arg0).getAvailableMinimumRealProduction().doubleValue();
      double arg1Min =(arg1 instanceof Generator) ? ((Generator) arg1).getRealGenerationMin() : ((Battery)arg1).getAvailableMinimumRealProduction().doubleValue();

      // most space
      return MathUtils.COMPARE_TO((MathUtils.SUBTRACT(arg0Min,arg0.getActualRealGeneration())),(MathUtils.SUBTRACT(arg1Min,arg1.getActualRealGeneration())));
    }
  }

  /**
   * Under reactive generation comparator
   * @author Russell Bent
   *
   */
  private class UnderReactiveGenerationComparator implements Comparator<ElectricPowerProducer> {
    @Override
    public int compare(ElectricPowerProducer arg0, ElectricPowerProducer arg1) {
      double arg0Max =(arg0 instanceof Generator) ? ((Generator) arg0).computeActualReactiveGenerationMax().doubleValue() : ((Battery)arg0).getAvailableMaximumReactiveProduction().doubleValue();
      double arg1Max =(arg1 instanceof Generator) ? ((Generator) arg1).computeActualReactiveGenerationMax().doubleValue() : ((Battery)arg1).getAvailableMaximumReactiveProduction().doubleValue();
    	
      // most space
      return -MathUtils.COMPARE_TO(MathUtils.SUBTRACT(arg0Max,arg0.getActualReactiveGeneration()),(MathUtils.SUBTRACT(arg1Max,arg1.getActualReactiveGeneration())));
    }
  }

  /**
   * Comparator of over real generation
   * @author Russell Bent
   */
  private class OverReactiveGenerationComparator implements Comparator<ElectricPowerProducer> {
    @Override
    public int compare(ElectricPowerProducer arg0, ElectricPowerProducer arg1) {
    	 double arg0Min =(arg0 instanceof Generator) ? ((Generator) arg0).getReactiveMin() : ((Battery)arg0).getAvailableMinimumReactiveProduction().doubleValue();
       double arg1Min =(arg1 instanceof Generator) ? ((Generator) arg1).getReactiveMin() : ((Battery)arg1).getAvailableMinimumReactiveProduction().doubleValue();
     	   	
      // most space
      return MathUtils.COMPARE_TO(MathUtils.SUBTRACT(arg0Min,arg0.getActualReactiveGeneration()),(MathUtils.SUBTRACT(arg1Min,arg1.getActualReactiveGeneration())));
    }
  }

  @Override
  public void close() {    
  }


}
