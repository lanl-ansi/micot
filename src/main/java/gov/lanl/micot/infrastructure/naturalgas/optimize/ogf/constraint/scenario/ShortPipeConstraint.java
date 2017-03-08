package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ConstraintFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.FlowScenarioVariableFactory;
import gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario.PressureScenarioVariableFactory;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Constrains the pressure changes along a 
 * pipe with no resistance (pipe)
 * 
 * IMPORTANT, this does not account for construction variables, if those are included
 * 
 * @author Russell Bent 
 */

public class ShortPipeConstraint implements ConstraintFactory {

  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param scenario collection
  public ShortPipeConstraint(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  /**
   * Get constraint 1
   * @param compressor
   * @return
   */
  private String getConstraintName(NaturalGasConnection connection) {
    return "ShortPipe-" + connection;
  }
  
  // Constraint
  @Override
  public void constructConstraint(MathematicalProgram problem, NaturalGasModel model) throws VariableExistsException, NoVariableException, InvalidConstraintException {
    // Step 1: Instantiate all the variables being used in this constraint
    PressureScenarioVariableFactory pressureFactory = new PressureScenarioVariableFactory(scenarios);
    FlowScenarioVariableFactory factory = new FlowScenarioVariableFactory(scenarios);
    
    LinkedHashSet<NaturalGasConnection> connections = new LinkedHashSet<NaturalGasConnection>();
    connections.addAll(model.getShortPipes());

    //int maxC = 11;
    //int i = 0;
    
    //HashSet<Integer> include = new HashSet<Integer>();
    //include.add(10); // edge 288
    //include.add(9); // edge 287
    //include.add(8); // edge 286
    //include.add(7); // edge 285
    //include.add(6); // edge 284
    //include.add(5); // edge 283
    //include.add(4); // edge 282
    //include.add(3); // edge 281
    //include.add(2); // edge 280
    //include.add(1); // edge 279
    //include.add(0); // edge 278
    
    
    // Step 2: Add the scenario-based bounds by looping over the set of specific scenarios
    for (Scenario scenario : scenarios)
      for (NaturalGasConnection c : connections) {
//        if (i == maxC) {
  //        break;
   //     }
                
     //   if (!include.contains(i)) {
       //   ++i;
        //  continue;
        //}
          
        // Get pressure bounds on the compressor arc
        NaturalGasNode ni = model.getFirstNode(c);
        NaturalGasNode nj = model.getSecondNode(c);
                
        Variable pi = pressureFactory.getVariable(problem, ni, scenario);
        Variable pj = pressureFactory.getVariable(problem, nj, scenario);
        
        Variable flow = factory.getVariable(problem, c, scenario);               
        if (flow == null) {
          continue; // in case the edge is disabled
        }
        
        LinearConstraint constraint = new LinearConstraintEquals(getConstraintName(c));
        constraint.addVariable(pj, 1.0);
        constraint.addVariable(pi, -1.0);
        constraint.setRightHandSide(0.0);
      //  System.err.println(c);
        
        problem.addLinearConstraint(constraint);
//        ++i;        
        
        
      }
  }
}
