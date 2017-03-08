package gov.lanl.micot.infrastructure.ep.optimize.dcopf.constraint;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.GeneratorTimeVariableFactory;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.Collection;

/**
 * This constraint adds constraints to the generation
 * @author Russell Bent
 */
public class GenerationTimeConstraint implements ConstraintFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public GenerationTimeConstraint(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getGenerationUpperBoundConstraintName(Generator gen, double time) {
    return "GU" + gen.toString()+"-"+time;
  }

  /**
   * Function for create the variable name associated with a load
   * @param node
   * @return
   */
  private String getGenerationLowerBoundConstraintName(Generator gen, double time) {
    return "GL" + gen.toString()+"-"+time;
  }
  
  @Override
  public void constructConstraint(MathematicalProgram problem, ElectricPowerModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    double mva = model.getMVABase();      
    GeneratorTimeVariableFactory generatorVariableFactory = new GeneratorTimeVariableFactory(numberOfIncrements, incrementSize);
            
    for (ElectricPowerNode node : model.getNodes()) {      
      Collection<Generator> generators = node.getComponents(Generator.class); 

      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
        for (Generator gen : generators) {
          double totalMaxGeneration = gen.getActualStatus() == true ? MathUtils.TO_FUNCTION(gen.computeActualRealGenerationMax()).getValue(time).doubleValue() / mva : 0;
          double totalMinGeneration = gen.getActualStatus() == true ? MathUtils.TO_FUNCTION(gen.getRealGenerationMin()).getValue(time).doubleValue() / mva : 0;
          
           if (totalMaxGeneration == totalMinGeneration) {
            LinearConstraint constraint = new LinearConstraintEquals(getGenerationUpperBoundConstraintName(gen,time)); 
            constraint.setRightHandSide(totalMaxGeneration);  
            constraint.addVariable(generatorVariableFactory.getVariable(problem, gen, time), 1.0);
            problem.addLinearConstraint(constraint);
           }
           else {      
               
            LinearConstraint constraint = new LinearConstraintLessEq(getGenerationUpperBoundConstraintName(gen,time)); 
            constraint.setRightHandSide(totalMaxGeneration);  
            constraint.addVariable(generatorVariableFactory.getVariable(problem, gen, time), 1.0);
            problem.addLinearConstraint(constraint);

            constraint = new LinearConstraintGreaterEq(getGenerationLowerBoundConstraintName(gen,time)); 
            constraint.setRightHandSide(totalMinGeneration);  
            constraint.addVariable(generatorVariableFactory.getVariable(problem, gen, time), 1.0);
            problem.addLinearConstraint(constraint);        
           }          
        }
      }
    }
    
  }

  
}
