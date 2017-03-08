package gov.lanl.micot.util.math.solver.quadraticprogram.ipopt;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.ipopt.IpOpt;
import gov.lanl.micot.util.math.solver.ipopt.IpOptVariable;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for visiting the objective function
 * 
 * @author Russell Bent
 * 
 */
public class ObjectiveInit extends QuadraticObjectiveVisitor {
  private IpOpt ipopt;
  private Map<Variable, IpOptVariable> _varLookup;

  public ObjectiveInit(IpOpt ipopt, Map<Variable, IpOptVariable> varLookup) {
    this.ipopt = ipopt;
    _varLookup = varLookup;
  }

  public void initObj(QuadraticObjective obj) {
    doIt(obj);
  }

  @Override
  public void applyObjectiveMaximize(QuadraticObjectiveMaximize o) {
    Map<IpOptVariable, Double> linearCoeffs = new HashMap<IpOptVariable, Double>();
    for (Variable variable : o.getVariables()) {
      linearCoeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
    }
    Map<Pair<IpOptVariable, IpOptVariable>, Double> quadCoeffs = new HashMap<Pair<IpOptVariable, IpOptVariable>, Double>();
    for (Pair<Variable, Variable> variables : o.getVariablePairs()) {
      quadCoeffs.put(new Pair<IpOptVariable, IpOptVariable>(_varLookup.get(variables.getOne()), _varLookup.get(variables.getTwo())), o.getCoefficient(variables.getOne(), variables.getTwo()));
    }
    ipopt.createQuadraticMaximizeObj(linearCoeffs, quadCoeffs);
  }

  @Override
  public void applyObjectiveMinimize(QuadraticObjectiveMinimize o) {
    Map<IpOptVariable, Double> linearCoeffs = new HashMap<IpOptVariable, Double>();
    for (Variable variable : o.getVariables()) {
      linearCoeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
    }
    Map<Pair<IpOptVariable, IpOptVariable>, Double> quadCoeffs = new HashMap<Pair<IpOptVariable, IpOptVariable>, Double>();
    for (Pair<Variable, Variable> variables : o.getVariablePairs()) {
      quadCoeffs.put(new Pair<IpOptVariable, IpOptVariable>(_varLookup.get(variables.getOne()), _varLookup.get(variables.getTwo())), o.getCoefficient(variables.getOne(), variables.getTwo()));
    }
    ipopt.createQuadraticMinimizeObj(linearCoeffs, quadCoeffs);
  }
}
