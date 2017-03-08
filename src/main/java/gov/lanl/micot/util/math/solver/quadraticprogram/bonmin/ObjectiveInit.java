package gov.lanl.micot.util.math.solver.quadraticprogram.bonmin;

import gov.lanl.micot.util.collection.Pair;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.bonmin.Bonmin;
import gov.lanl.micot.util.math.solver.bonmin.BonminVariable;
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
  private Bonmin bonmin;
  private Map<Variable, BonminVariable> _varLookup;

  public ObjectiveInit(Bonmin bonmin, Map<Variable, BonminVariable> varLookup) {
    this.bonmin = bonmin;
    _varLookup = varLookup;
  }

  public void initObj(QuadraticObjective obj) {
    doIt(obj);
  }

  @Override
  public void applyObjectiveMaximize(QuadraticObjectiveMaximize o) {
    Map<BonminVariable, Double> linearCoeffs = new HashMap<BonminVariable, Double>();
    for (Variable variable : o.getVariables()) {
      linearCoeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
    }
    Map<Pair<BonminVariable, BonminVariable>, Double> quadCoeffs = new HashMap<Pair<BonminVariable, BonminVariable>, Double>();
    for (Pair<Variable, Variable> variables : o.getVariablePairs()) {
      quadCoeffs.put(new Pair<BonminVariable, BonminVariable>(_varLookup.get(variables.getOne()), _varLookup.get(variables.getTwo())), o.getCoefficient(variables.getOne(), variables.getTwo()));
    }
    bonmin.createQuadraticMaximizeObj(linearCoeffs, quadCoeffs);
  }

  @Override
  public void applyObjectiveMinimize(QuadraticObjectiveMinimize o) {
    Map<BonminVariable, Double> linearCoeffs = new HashMap<BonminVariable, Double>();
    for (Variable variable : o.getVariables()) {
      linearCoeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
    }
    Map<Pair<BonminVariable, BonminVariable>, Double> quadCoeffs = new HashMap<Pair<BonminVariable, BonminVariable>, Double>();
    for (Pair<Variable, Variable> variables : o.getVariablePairs()) {
      quadCoeffs.put(new Pair<BonminVariable, BonminVariable>(_varLookup.get(variables.getOne()), _varLookup.get(variables.getTwo())), o.getCoefficient(variables.getOne(), variables.getTwo()));
    }
    bonmin.createQuadraticMinimizeObj(linearCoeffs, quadCoeffs);
  }
}
