package gov.lanl.micot.util.math.solver.quadraticprogram.scip;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintGreater;
import gov.lanl.micot.util.math.solver.LinearConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.LinearConstraintLess;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.QuadraticConstraintEquals;
import gov.lanl.micot.util.math.solver.QuadraticConstraintGreater;
import gov.lanl.micot.util.math.solver.QuadraticConstraintGreaterEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLess;
import gov.lanl.micot.util.math.solver.QuadraticConstraintLessEq;
import gov.lanl.micot.util.math.solver.QuadraticConstraintVisitor;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipConstraint;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for initiating the constraints
 * 
 * @author Russell Bent
 */
public class ConstraintInit extends QuadraticConstraintVisitor {
  private Scip scip;
  private ScipConstraint constraint;
  private Map<Variable, ScipVariable> _varLookup;

  /**
   * Constructor
   * 
   * @param cplex
   * @param varLookup
   */
  public ConstraintInit(Scip scip, Map<Variable, ScipVariable> varLookup) {
    this.scip = scip;
    _varLookup = varLookup;
  }

  public ScipConstraint initConst(LinearConstraint c) {
    constraint = null;
    doIt(c);
    return constraint;
  }

  public ScipConstraint initConst(QuadraticConstraint c) {
    constraint = null;
    doIt(c);
    return constraint;
  }

  @Override
  public void applyConstraintEquals(QuadraticConstraintEquals c) {
    Map<ScipVariable, Double> linearcoeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<ScipVariable, ScipVariable>, Double> quadcoeffs = new HashMap<Pair<ScipVariable, ScipVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<ScipVariable, ScipVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    scip.createQuadraticConstraintEq(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintGreater(QuadraticConstraintGreater c) {
    assert (false) : "strict greater not supported by cplex...";
  }

  @Override
  public void applyConstraintGreaterEq(QuadraticConstraintGreaterEq c) {
    Map<ScipVariable, Double> linearcoeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<ScipVariable, ScipVariable>, Double> quadcoeffs = new HashMap<Pair<ScipVariable, ScipVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<ScipVariable, ScipVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    scip.createQuadraticConstraintGE(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintLess(QuadraticConstraintLess c) {
    assert (false) : "strict less not supported by cplex...";
  }

  @Override
  public void applyConstraintLessEq(QuadraticConstraintLessEq c) {
    Map<ScipVariable, Double> linearcoeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<ScipVariable, ScipVariable>, Double> quadcoeffs = new HashMap<Pair<ScipVariable, ScipVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<ScipVariable, ScipVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    scip.createQuadraticConstraintLE(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintEquals(LinearConstraintEquals c) {
    Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    scip.createLinearConstraintEq(c.getName(), c.getRightHandSide(), coeffs);
  }

  @Override
  public void applyConstraintGreater(LinearConstraintGreater c) {
    assert (false) : "strict greater not supported by scip...";
  }

  @Override
  public void applyConstraintGreaterEq(LinearConstraintGreaterEq c) {
    Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    scip.createLinearConstraintGE(c.getName(), c.getRightHandSide(), coeffs);
  }

  @Override
  public void applyConstraintLess(LinearConstraintLess c) {
    assert (false) : "strict less not supported by scip...";
  }

  @Override
  public void applyConstraintLessEq(LinearConstraintLessEq c) {
    Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    scip.createLinearConstraintLE(c.getName(), c.getRightHandSide(), coeffs);
  }

}
