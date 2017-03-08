package gov.lanl.micot.util.math.solver.ipopt;

import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintEquals;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMinimize;
import gov.lanl.micot.util.math.solver.quadraticprogram.QuadraticProgram;
import gov.lanl.micot.util.math.solver.quadraticprogram.ipopt.IpOptQuadraticProgramFactory;
import junit.framework.TestCase;

/**
 * Test the ipopt solver classes
 * 
 * @author Russell Bent
 */
public class IpOptSolveTest extends TestCase {

  /**
   * test the linear program
   * 
   * @throws LpSolveException
   * @throws SolverException
   */
  public void testLinearProgram() throws SolverException {
    if (System.getenv("TEST_IPOPT") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, IpOptQuadraticProgramFactory.class.getCanonicalName());
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
    if (System.getenv("TEST_IPOPT") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, IpOptQuadraticProgramFactory.class.getCanonicalName());
    MathematicalProgram model = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);
    model.setQuadraticObjective(new QuadraticObjectiveMaximize());

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
    assertEquals(message, solution.getValueDouble(var1), -1.0, 1e-6);

    message = "Solution should be -1, but it was reported as " + solution.getObjectiveValue();
    assertEquals(message, solution.getObjectiveValue(), -1.0, 1e-6);
  }

  /**
   * Tests a problem with a quadratic objective function
   * 
   * @throws SolverException
   */
  public void testQP() throws SolverException {
    if (System.getenv("TEST_IPOPT") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, IpOptQuadraticProgramFactory.class.getCanonicalName());
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
    if (System.getenv("TEST_IPOPT") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, IpOptQuadraticProgramFactory.class.getCanonicalName());
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

    // loose tolerances...
    assertEquals(solution.getObjectiveValue(), 2.0023465568873084, 1e-2);
    assertEquals(solution.getValueDouble(x0), 0.12907533157470916, 1e-2);
    assertEquals(solution.getValueDouble(x1), 0.5499468111182552, 1e-1);
    assertEquals(solution.getValueDouble(x2), 0.8251652306108547, 1e-2);

  }

  /**
   * Tests a quadratically constrained quadratic program as an expression
   * program
   * 
   * this contains a mix of constraints expressed as linear constraints,
   * quadratic constraints, and expression constraints, to test all features
   * 
   * @throws SolverException
   */
  /*
   * public void testExpressionProgram() throws SolverException {
   * MathematicalProgramFlags flags = new MathematicalProgramFlags();
   * flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG,
   * "gov.lanl.util.math.solver.expressionprogram.ipopt.IpOptExpressionProgramFactory"
   * ); ExpressionProgram model = (ExpressionProgram)
   * DefaultMathematicalProgramFactory.getInstance().
   * constructMathematicalProgram(flags);
   * 
   * // create the variables Variable x0 = model.makeContinuousVariable("x0");
   * Variable x1 = model.makeContinuousVariable("x1"); Variable x2 =
   * model.makeContinuousVariable("x2");
   * 
   * // add the bounds model.addBounds(x0, 0.0, 40.0); model.addBounds(x1, 0.0,
   * Double.MAX_VALUE); model.addBounds(x2, 0.0, Double.MAX_VALUE);
   * 
   * 
   * // add the constraints
   * 
   * // add this linear constraint as a linear constraint LinearConstraint c1 =
   * new LinearConstraintLessEq("c1"); c1.addVariable(x0, -1);
   * c1.addVariable(x1, 1); c1.addVariable(x2, 1); c1.setRightHandSide(20);
   * model.addLinearConstraint(c1);
   * 
   * // add this linear constraint as an expression constraint
   * ExpressionConstraint c2 = new ExpressionConstraintLessEq("c2");
   * c2.addTerm(new LinearTerm(x0, 1)); c2.addTerm(new LinearTerm(x1, -3));
   * c2.addTerm(new LinearTerm(x2, 1)); c2.setRightHandSide(30);
   * model.addExpressionConstraint(c2);
   * 
   * // add this quadraric constrain as an expression constraint
   * ExpressionConstraint c3 = new ExpressionConstraintLessEq("c3");
   * c3.addTerm(new QuadraticTerm(x0, x0,1.0)); c3.addTerm(new QuadraticTerm(x1,
   * x1,1.0)); c3.addTerm(new QuadraticTerm(x2, x2,1.0));
   * c3.setRightHandSide(1.0); model.addExpressionConstraint(c3);
   * 
   * // add the objective.. as a quadratic objective... should work
   * QuadraticObjective objective = new QuadraticObjectiveMaximize();
   * objective.addVariables(x0, x0, 33.0 * -0.5); objective.addVariables(x1, x1,
   * 22.0 * -0.5); objective.addVariables(x2, x2, 11.0 * -0.5);
   * objective.addVariables(x0, x1, -12.0 * -0.5); objective.addVariables(x1,
   * x2, -23.0 * -0.5); objective.addVariable(x0, 1.0);
   * objective.addVariable(x1, 2.0); objective.addVariable(x2, 3.0);
   * model.setQuadraticObjective(objective);
   * 
   * Solution solution = model.solve();
   * 
   * // loose tolerances... assertEquals(solution.getObjectiveValue(),
   * 2.0023465568873084, 1e-2); assertEquals(solution.getValueDouble(x0),
   * 0.12907533157470916, 1e-2); assertEquals(solution.getValueDouble(x1),
   * 0.5499468111182552, 1e-1); assertEquals(solution.getValueDouble(x2),
   * 0.8251652306108547, 1e-2);
   * 
   * }
   * 
   */

  /**
   * This is the example included with IP Opt
   * 
   * @throws SolverException
   */
  public void testExample() throws SolverException {
    if (System.getenv("TEST_IPOPT") != null) {
      return;
    }

    MathematicalProgramFlags flags = new MathematicalProgramFlags();
    flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, IpOptQuadraticProgramFactory.class.getCanonicalName());
    QuadraticProgram model = (QuadraticProgram) DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

    // create the variables
    Variable x1 = model.makeContinuousVariable("x1");
    Variable x2 = model.makeContinuousVariable("x2");

    // add the bounds
    model.addBounds(x1, -1.0, 1.0);
    model.addBounds(x2, 0.0, 1.0);

    // add the constraint
    QuadraticConstraint c3 = new QuadraticConstraintEquals("c3");
    c3.addVariables(x1, x1, -1.0);
    c3.addVariable(x2, -1.0);
    c3.setRightHandSide(-1.0);
    model.addQuadraticConstraint(c3);

    // add the objective
    QuadraticObjective objective = new QuadraticObjectiveMinimize();
    objective.addVariables(x2, x2, -1);
    objective.addVariable(x2, 4);
    model.setQuadraticObjective(objective);

    Solution initialSolution = new Solution(0, false);
    initialSolution.addValue(x1, 1.0);
    initialSolution.addValue(x2, 0);
    model.setInitialSolution(initialSolution);

    Solution solution = model.solve();

    // This is not a convex function. The objective function is concave. The
    // equality constraint with a quadratic is also
    // not convex However, it is symmetric
    // with an optimal solution at x_1 = 1,-1, x_2 = 0

    // to make this more robust, I ended up setting the initial solution

    assertEquals(solution.getObjectiveValue(), 0.0, 1e-4);
    assertEquals(Math.abs(solution.getValueDouble(x1)), 1.0, 1e-4);
    assertEquals(solution.getValueDouble(x2), 0.0, 1e-4);
  }

}
