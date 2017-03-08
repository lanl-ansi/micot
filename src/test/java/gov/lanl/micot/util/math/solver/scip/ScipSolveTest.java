package gov.lanl.micot.util.math.solver.scip;

import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.integerprogram.scip.ScipIntegerProgramFactory;
import gov.lanl.micot.util.math.solver.linearprogram.scip.ScipLinearProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;
import gov.lanl.micot.util.math.solver.quadraticprogram.scip.ScipQuadraticProgramFactory;
import junit.framework.TestCase;

/**
 * Test the scip solver classes
 * 
 * @author Russell Bent
 */
public class ScipSolveTest extends TestCase {

  /**
   * test the linear program
   * 
   * @throws LpSolveException
   * @throws SolverException
   */
  public void testLinearProgram() throws SolverException {

    if (System.getenv("TEST_SCIP") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, ScipLinearProgramFactory.class.getCanonicalName());
    MathematicalProgram model = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    Variable var1 = model.makeContinuousVariable("1");
    Variable var2 = model.makeContinuousVariable("2");
    Variable var3 = model.makeContinuousVariable("3");
    Variable var4 = model.makeContinuousVariable("4");

    LinearObjective obj = new LinearObjectiveMinimize();
    obj.addVariable(var1, 2);
    obj.addVariable(var2, 3);
    obj.addVariable(var3, -2);
    obj.addVariable(var4, 3);
    model.setLinearObjective(obj);

    LinearConstraint con1 = new LinearConstraintLessEq("le");
    con1.addVariable(var1, 3);
    con1.addVariable(var2, 2);
    con1.addVariable(var3, 2);
    con1.addVariable(var4, 1);
    con1.setRightHandSide(4);
    model.addLinearConstraint(con1);

    LinearConstraint con2 = new LinearConstraintGreaterEq("ge");
    con2.addVariable(var2, 4);
    con2.addVariable(var3, 3);
    con2.addVariable(var4, 1);
    con2.setRightHandSide(3);
    model.addLinearConstraint(con2);

    LinearConstraint con3 = new LinearConstraintGreaterEq("constraint 3");
    con3.addVariable(var1, 1);
    con3.setRightHandSide(0);
    model.addLinearConstraint(con3);

    LinearConstraint con4 = new LinearConstraintGreaterEq("constraint 4");
    con4.addVariable(var2, 1);
    con4.setRightHandSide(0);
    model.addLinearConstraint(con4);

    LinearConstraint con5 = new LinearConstraintGreaterEq("constraint 5");
    con5.addVariable(var3, 1);
    con5.setRightHandSide(0);
    model.addLinearConstraint(con5);

    LinearConstraint con6 = new LinearConstraintGreaterEq("constraint 6");
    con6.addVariable(var4, 1);
    con6.setRightHandSide(0);
    model.addLinearConstraint(con6);

    Solution solution = model.solve();

    String message = "Solution should be -4, but it was reported as " + solution.getObjectiveValue();
    assertEquals(message, solution.getObjectiveValue(), -4.0, 1e-4);

    message = "Variable 1 should be 0, but it was reported as " + solution.getValueDouble(var1);
    assertEquals(message, solution.getValueDouble(var1), 0.0, 1e-4);

    message = "Variable 2 should be 0, but it was reported as " + solution.getValueDouble(var2);
    assertEquals(message, solution.getValueDouble(var2), 0.0, 1e-4);

    message = "Variable 3 should be 2, but it was reported as " + solution.getValueDouble(var3);
    assertEquals(message, solution.getValueDouble(var3), 2.0, 1e-4);

    message = "Variable 4 should be 0, but it was reported as " + solution.getValueDouble(var4);
    assertEquals(message, solution.getValueDouble(var4), 0.0, 1e-4);
  }

  /**
   * test the linear program
   * 
   * @throws LpSolveException
   * @throws SolverException
   */
  public void testNegativeLinearProgram() throws SolverException {

    if (System.getenv("TEST_SCIP") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, ScipLinearProgramFactory.class.getCanonicalName());
    MathematicalProgram model = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    Variable var1 = model.makeContinuousVariable("1");

    LinearObjective obj = new LinearObjectiveMaximize();
    obj.addVariable(var1, 1);
    model.setLinearObjective(obj);

    LinearConstraint con1 = new LinearConstraintLessEq("le");
    con1.addVariable(var1, 1);
    con1.setRightHandSide(-1);
    model.addLinearConstraint(con1);

    Solution solution = model.solve();

    String message = "Variable 1 should be -1, but it was reported as " + solution.getValueDouble(var1);
    assertEquals(message, solution.getValueDouble(var1), -1.0);

    message = "Solution should be -1, but it was reported as " + solution.getObjectiveValue();
    assertEquals(message, solution.getObjectiveValue(), -1.0);
  }

  /**
   * Tests the integer SCIP Solver
   * 
   * @throws SolverException
   */
  public void testIntegerProgram() throws SolverException {
    if (System.getenv("TEST_SCIP") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, ScipIntegerProgramFactory.class.getCanonicalName());
    MathematicalProgram model = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    int nbWhouses = 4;
    int nbLoads = 31;

    DiscreteVariable[] capVars = model.makeDiscreteVariable(nbWhouses);
    for (int i = 0; i < capVars.length; i++) {
      model.addBounds(capVars[i], 0, 10);
    }

    double[] costs = { 1.0, 2.0, 4.0, 6.0 }; // Cost per warehouse

    // These variables represent the assignment of a
    // load to a warehouse.
    DiscreteVariable[][] assignVars = new DiscreteVariable[nbWhouses][];
    for (int w = 0; w < nbWhouses; w++) {
      assignVars[w] = model.makeDiscreteVariable(nbLoads);
      for (int j = 0; j < assignVars[w].length; j++) {
        model.addBounds(assignVars[w][j], 0, 1);
      }

      // Links the number of loads assigned to a warehouse with
      // the capacity variable of the warehouse.
      LinearConstraint loadConst = new LinearConstraintEquals("cap" + w);
      for (int j = 0; j < assignVars[w].length; j++) {
        loadConst.addVariable(assignVars[w][j]);
      }
      loadConst.setRightHandSide(capVars[w]);
      model.addLinearConstraint(loadConst);
    }

    // Each load must be assigned to just one warehouse.
    for (int l = 0; l < nbLoads; l++) {
      LinearConstraint loadConst = new LinearConstraintEquals("assign" + l);
      for (int w = 0; w < nbWhouses; w++) {
        loadConst.addVariable(assignVars[w][l]);
      }
      loadConst.setRightHandSide(1.0);
      model.addLinearConstraint(loadConst);
    }

    LinearObjective obj = new LinearObjectiveMinimize(capVars, costs);

    model.setLinearObjective(obj);

    Solution solution = model.solve();
    solution.removeSmallValues(1e-7);

    System.out.println("--------------------------------------------");
    System.out.println();
    System.out.println("Solution found:");
    System.out.println(" Objective value = " + solution.getObjectiveValue());
    System.out.println();
    for (int w = 0; w < nbWhouses; w++) {
      System.out.println("Warehouse " + w + ": stored " + solution.getValue(capVars[w]) + " loads");
      for (int l = 0; l < nbLoads; l++) {
        if (solution.getValueDouble(assignVars[w][l]) > 0) {
          System.out.print("Load " + l + " | ");
        }
      }
      System.out.println();
      System.out.println();
    }
    System.out.println("--------------------------------------------");

    assertEquals(solution.getObjectiveValue(), 76.0, 1e-6);

    assertEquals(solution.getValueDouble(capVars[0]), 10.0, 1e-6);
    assertEquals(solution.getValueDouble(capVars[1]), 10.0, 1e-6);
    assertEquals(solution.getValueDouble(capVars[2]), 10.0, 1e-6);
    assertEquals(solution.getValueDouble(capVars[3]), 1.0, 1e-6);
  }

  /**
   * Tests a problem with a quadratic objective function
   * 
   * @throws SolverException
   */
  public void testQP() throws SolverException {
    if (System.getenv("TEST_SCIP") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, ScipQuadraticProgramFactory.class.getCanonicalName());
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
    if (System.getenv("TEST_SCIP") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, ScipQuadraticProgramFactory.class.getCanonicalName());
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
