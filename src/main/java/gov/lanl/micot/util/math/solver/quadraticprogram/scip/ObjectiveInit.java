package gov.lanl.micot.util.math.solver.quadraticprogram.scip;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjective;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.QuadraticObjectiveVisitor;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for visiting the objective function
 * 
 * @author Russell Bent
 * 
 */
public class ObjectiveInit extends QuadraticObjectiveVisitor {
  private Scip scip;
  private Map<Variable, ScipVariable> _varLookup;

  public ObjectiveInit(Scip scip, Map<Variable, ScipVariable> varLookup) {
    this.scip = scip;
    _varLookup = varLookup;
  }

  public void initObj(QuadraticObjective obj) {
    doIt(obj);
  }

  @Override
  public void applyObjectiveMaximize(QuadraticObjectiveMaximize o) {
    Map<ScipVariable, Double> linearCoeffs = new HashMap<ScipVariable, Double>();
    for (Variable variable : o.getVariables()) {
      linearCoeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
    }
    Map<Pair<ScipVariable, ScipVariable>, Double> quadCoeffs = new HashMap<Pair<ScipVariable, ScipVariable>, Double>();
    for (Pair<Variable, Variable> variables : o.getVariablePairs()) {
      quadCoeffs.put(new Pair<ScipVariable, ScipVariable>(_varLookup.get(variables.getOne()), _varLookup.get(variables.getTwo())), o.getCoefficient(variables.getOne(), variables.getTwo()));
    }
    scip.createQuadraticMaximizeObj(linearCoeffs, quadCoeffs);
  }

  @Override
  public void applyObjectiveMinimize(QuadraticObjectiveMinimize o) {
    Map<ScipVariable, Double> linearCoeffs = new HashMap<ScipVariable, Double>();
    for (Variable variable : o.getVariables()) {
      linearCoeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
    }
    Map<Pair<ScipVariable, ScipVariable>, Double> quadCoeffs = new HashMap<Pair<ScipVariable, ScipVariable>, Double>();
    for (Pair<Variable, Variable> variables : o.getVariablePairs()) {
      quadCoeffs.put(new Pair<ScipVariable, ScipVariable>(_varLookup.get(variables.getOne()), _varLookup.get(variables.getTwo())), o.getCoefficient(variables.getOne(), variables.getTwo()));
    }
    scip.createQuadraticMinimizeObj(linearCoeffs, quadCoeffs);
  }
}
