package gov.lanl.micot.util.math.solver.cplex;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;
import junit.framework.TestCase;

/**
 * This is a duplicate of the CPLEX example implemented using the abstract model
 * framework
 * 
 * @author Russell Bent
 *
 */
public class QPTest extends TestCase {

  public void testQP() throws SolverException {
    if (System.getenv("TEST_CPLEX") != null) {
      return;
    }
    
    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, "gov.lanl.micot.util.math.solver.quadraticprogram.cplex.CPLEXQuadraticProgramFactory");
    QuadraticProgram model = (QuadraticProgram) DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    // create the variables
    Variable x0 = model.makeContinuousVariable("x0");
    Variable x1 = model.makeContinuousVariable("x1");
    Variable x2 = model.makeContinuousVariable("x2");

    // add the bounds
    model.addBounds(x0, 0.0, 40.0);
    model.addBounds(x1, 0.0, Double.MAX_VALUE);
    model.addBounds(x2, 0.0, Double.MAX_VALUE);

    // add the constraints
    LinearConstraint c1 = new LinearConstraintLessEq("c1");
    c1.addVariable(x0, -1);
    c1.addVariable(x1, 1);
    c1.addVariable(x2, 1);
    c1.setRightHandSide(20);
    model.addLinearConstraint(c1);

    LinearConstraint c2 = new LinearConstraintLessEq("c2");
    c2.addVariable(x0, 1);
    c2.addVariable(x1, -3);
    c2.addVariable(x2, 1);
    c2.setRightHandSide(30);
    model.addLinearConstraint(c2);

    // add the objective
    QuadraticObjective objective = new QuadraticObjectiveMaximize();
    objective.addVariables(x0, x0, 33.0 * -0.5);
    objective.addVariables(x1, x1, 22.0 * -0.5);
    objective.addVariables(x2, x2, 11.0 * -0.5);
    objective.addVariables(x0, x1, -12.0 * -0.5);
    objective.addVariables(x1, x2, -23.0 * -0.5);
    objective.addVariable(x0, 1.0);
    objective.addVariable(x1, 2.0);
    objective.addVariable(x2, 3.0);
    model.setQuadraticObjective(objective);

    Solution solution = model.solve();
    assertEquals(solution.getObjectiveValue(), 2.015616523289157, 1e-4);
    assertEquals(solution.getValueDouble(x0), 0.13911493520482582, 1e-4);
    assertEquals(solution.getValueDouble(x1), 0.5984654742056661, 1e-4);
    assertEquals(solution.getValueDouble(x2), 0.8983957232479367, 1e-4);

  }

  /**
   * Tests a quadratically constrained quadratic program
   * 
   * @throws SolverException
   */
  public void testQCQP() throws SolverException {
    if (System.getenv("TEST_CPLEX") != null) {
      return;
    }
    
    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, "gov.lanl.micot.util.math.solver.quadraticprogram.cplex.CPLEXQuadraticProgramFactory");
    QuadraticProgram model = (QuadraticProgram) DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    // create the variables
    Variable x0 = model.makeContinuousVariable("x0");
    Variable x1 = model.makeContinuousVariable("x1");
    Variable x2 = model.makeContinuousVariable("x2");

    // add the bounds
    model.addBounds(x0, 0.0, 40.0);
    model.addBounds(x1, 0.0, Double.MAX_VALUE);
    model.addBounds(x2, 0.0, Double.MAX_VALUE);

    // add the constraints
    LinearConstraint c1 = new LinearConstraintLessEq("c1");
    c1.addVariable(x0, -1);
    c1.addVariable(x1, 1);
    c1.addVariable(x2, 1);
    c1.setRightHandSide(20);
    model.addLinearConstraint(c1);

    LinearConstraint c2 = new LinearConstraintLessEq("c2");
    c2.addVariable(x0, 1);
    c2.addVariable(x1, -3);
    c2.addVariable(x2, 1);
    c2.setRightHandSide(30);
    model.addLinearConstraint(c2);

    QuadraticConstraint c3 = new QuadraticConstraintLessEq("c3");
    c3.addVariables(x0, x0, 1.0);
    c3.addVariables(x1, x1, 1.0);
    c3.addVariables(x2, x2, 1.0);
    c3.setRightHandSide(1.0);
    model.addQuadraticConstraint(c3);

    // add the objective
    QuadraticObjective objective = new QuadraticObjectiveMaximize();
    objective.addVariables(x0, x0, 33.0 * -0.5);
    objective.addVariables(x1, x1, 22.0 * -0.5);
    objective.addVariables(x2, x2, 11.0 * -0.5);
    objective.addVariables(x0, x1, -12.0 * -0.5);
    objective.addVariables(x1, x2, -23.0 * -0.5);
    objective.addVariable(x0, 1.0);
    objective.addVariable(x1, 2.0);
    objective.addVariable(x2, 3.0);
    model.setQuadraticObjective(objective);

    Solution solution = model.solve();

    assertEquals(solution.getObjectiveValue(), 2.0023465568873084, 1e-4);
    assertEquals(solution.getValueDouble(x0), 0.12907533157470916, 1e-4);
    assertEquals(solution.getValueDouble(x1), 0.5499468111182552, 1e-4);
    assertEquals(solution.getValueDouble(x2), 0.8251652306108547, 1e-4);
  }

}
