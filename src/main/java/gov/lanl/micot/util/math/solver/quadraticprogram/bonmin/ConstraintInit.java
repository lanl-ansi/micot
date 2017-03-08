package gov.lanl.micot.util.math.solver.quadraticprogram.bonmin;

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
import gov.lanl.micot.util.math.solver.bonmin.Bonmin;
import gov.lanl.micot.util.math.solver.bonmin.BonminConstraint;
import gov.lanl.micot.util.math.solver.bonmin.BonminVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for initiating the constraints
 * 
 * @author Russell Bent
 */
public class ConstraintInit extends QuadraticConstraintVisitor {
  private Bonmin bonmin;
  private BonminConstraint constraint;
  private Map<Variable, BonminVariable> _varLookup;

  /**
   * Constructor
   * 
   * @param cplex
   * @param varLookup
   */
  public ConstraintInit(Bonmin bonmin, Map<Variable, BonminVariable> varLookup) {
    this.bonmin = bonmin;
    _varLookup = varLookup;
  }

  public BonminConstraint initConst(LinearConstraint c) {
    constraint = null;
    doIt(c);
    return constraint;
  }

  public BonminConstraint initConst(QuadraticConstraint c) {
    constraint = null;
    doIt(c);
    return constraint;
  }

  @Override
  public void applyConstraintEquals(QuadraticConstraintEquals c) {
    Map<BonminVariable, Double> linearcoeffs = new HashMap<BonminVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<BonminVariable, BonminVariable>, Double> quadcoeffs = new HashMap<Pair<BonminVariable, BonminVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<BonminVariable, BonminVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    bonmin.createQuadraticConstraintEq(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintGreater(QuadraticConstraintGreater c) {
    assert (false) : "strict greater not supported by Bonmin...";
  }

  @Override
  public void applyConstraintGreaterEq(QuadraticConstraintGreaterEq c) {
    Map<BonminVariable, Double> linearcoeffs = new HashMap<BonminVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<BonminVariable, BonminVariable>, Double> quadcoeffs = new HashMap<Pair<BonminVariable, BonminVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<BonminVariable, BonminVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    bonmin.createQuadraticConstraintGE(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintLess(QuadraticConstraintLess c) {
    assert (false) : "strict less not supported by cplex...";
  }

  @Override
  public void applyConstraintLessEq(QuadraticConstraintLessEq c) {
    Map<BonminVariable, Double> linearcoeffs = new HashMap<BonminVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<BonminVariable, BonminVariable>, Double> quadcoeffs = new HashMap<Pair<BonminVariable, BonminVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<BonminVariable, BonminVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    bonmin.createQuadraticConstraintLE(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintEquals(LinearConstraintEquals c) {
    Map<BonminVariable, Double> coeffs = new HashMap<BonminVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    bonmin.createLinearConstraintEq(c.getName(), c.getRightHandSide(), coeffs);
  }

  @Override
  public void applyConstraintGreater(LinearConstraintGreater c) {
    assert (false) : "strict greater not supported by Bonmin...";
  }

  @Override
  public void applyConstraintGreaterEq(LinearConstraintGreaterEq c) {
    Map<BonminVariable, Double> coeffs = new HashMap<BonminVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    bonmin.createLinearConstraintGE(c.getName(), c.getRightHandSide(), coeffs);
  }

  @Override
  public void applyConstraintLess(LinearConstraintLess c) {
    assert (false) : "strict less not supported by Bonmin...";
  }

  @Override
  public void applyConstraintLessEq(LinearConstraintLessEq c) {
    Map<BonminVariable, Double> coeffs = new HashMap<BonminVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    bonmin.createLinearConstraintLE(c.getName(), c.getRightHandSide(), coeffs);
  }

}
