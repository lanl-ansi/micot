package gov.lanl.micot.util.math.solver.quadraticprogram.ipopt;

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
import gov.lanl.micot.util.math.solver.ipopt.IpOpt;
import gov.lanl.micot.util.math.solver.ipopt.IpOptConstraint;
import gov.lanl.micot.util.math.solver.ipopt.IpOptVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for initiating the constraints
 * 
 * @author Russell Bent
 */
public class ConstraintInit extends QuadraticConstraintVisitor {
  private IpOpt ipopt;
  private IpOptConstraint constraint;
  private Map<Variable, IpOptVariable> _varLookup;

  /**
   * Constructor
   * 
   * @param cplex
   * @param varLookup
   */
  public ConstraintInit(IpOpt ipopt, Map<Variable, IpOptVariable> varLookup) {
    this.ipopt = ipopt;
    _varLookup = varLookup;
  }

  public IpOptConstraint initConst(LinearConstraint c) {
    constraint = null;
    doIt(c);
    return constraint;
  }

  public IpOptConstraint initConst(QuadraticConstraint c) {
    constraint = null;
    doIt(c);
    return constraint;
  }

  @Override
  public void applyConstraintEquals(QuadraticConstraintEquals c) {
    Map<IpOptVariable, Double> linearcoeffs = new HashMap<IpOptVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<IpOptVariable, IpOptVariable>, Double> quadcoeffs = new HashMap<Pair<IpOptVariable, IpOptVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<IpOptVariable, IpOptVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    ipopt.createQuadraticConstraintEq(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintGreater(QuadraticConstraintGreater c) {
    assert (false) : "strict greater not supported by ipopt...";
  }

  @Override
  public void applyConstraintGreaterEq(QuadraticConstraintGreaterEq c) {
    Map<IpOptVariable, Double> linearcoeffs = new HashMap<IpOptVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<IpOptVariable, IpOptVariable>, Double> quadcoeffs = new HashMap<Pair<IpOptVariable, IpOptVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<IpOptVariable, IpOptVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    ipopt.createQuadraticConstraintGE(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintLess(QuadraticConstraintLess c) {
    assert (false) : "strict less not supported by cplex...";
  }

  @Override
  public void applyConstraintLessEq(QuadraticConstraintLessEq c) {
    Map<IpOptVariable, Double> linearcoeffs = new HashMap<IpOptVariable, Double>();
    for (Variable v : c.getVariables()) {
      linearcoeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }

    Map<Pair<IpOptVariable, IpOptVariable>, Double> quadcoeffs = new HashMap<Pair<IpOptVariable, IpOptVariable>, Double>();
    for (Pair<Variable, Variable> v : c.getVariablePairs()) {
      quadcoeffs.put(new Pair<IpOptVariable, IpOptVariable>(_varLookup.get(v.getOne()), _varLookup.get(v.getTwo())), c.getCoefficient(v.getOne(), v.getTwo()));
    }

    ipopt.createQuadraticConstraintLE(c.getName(), c.getRightHandSide(), linearcoeffs, quadcoeffs);
  }

  @Override
  public void applyConstraintEquals(LinearConstraintEquals c) {
    Map<IpOptVariable, Double> coeffs = new HashMap<IpOptVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    ipopt.createLinearConstraintEq(c.getName(), c.getRightHandSide(), coeffs);
  }

  @Override
  public void applyConstraintGreater(LinearConstraintGreater c) {
    assert (false) : "strict greater not supported by ipopt...";
  }

  @Override
  public void applyConstraintGreaterEq(LinearConstraintGreaterEq c) {
    Map<IpOptVariable, Double> coeffs = new HashMap<IpOptVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    ipopt.createLinearConstraintGE(c.getName(), c.getRightHandSide(), coeffs);
  }

  @Override
  public void applyConstraintLess(LinearConstraintLess c) {
    assert (false) : "strict less not supported by ipopt...";
  }

  @Override
  public void applyConstraintLessEq(LinearConstraintLessEq c) {
    Map<IpOptVariable, Double> coeffs = new HashMap<IpOptVariable, Double>();
    for (Variable v : c.getVariables()) {
      coeffs.put(_varLookup.get(v), c.getCoefficient(v));
    }
    ipopt.createLinearConstraintLE(c.getName(), c.getRightHandSide(), coeffs);
  }

}
