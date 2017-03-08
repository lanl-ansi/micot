package gov.lanl.micot.util.math.solver.linearprogram.scip;

import java.util.HashMap;
import java.util.Map;

import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMaximize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveVisitor;
import gov.lanl.micot.util.math.solver.scip.Scip;
import gov.lanl.micot.util.math.solver.scip.ScipVariable;

/**
 * Class for visiting the objective function
 * @author Russell Bent
 *
 */
public class ObjectiveInit extends LinearObjectiveVisitor {
		private Scip									scip;
		private Map<Variable, ScipVariable>	_varLookup;

		public ObjectiveInit(Scip scip, Map<Variable, ScipVariable> varLookup) {
			this.scip = scip;
			_varLookup = varLookup;
		}

		public void initObj(LinearObjective obj) {
			doIt(obj);
		}

		@Override
		public void applyObjectiveMaximize(LinearObjectiveMaximize o) {
		  Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
		  for (Variable variable : o.getVariables()) {
		    coeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
		  }		  
		  scip.createLinearMaximizeObj(coeffs);
		}

		@Override
		public void applyObjectiveMinimize(LinearObjectiveMinimize o) {
      Map<ScipVariable, Double> coeffs = new HashMap<ScipVariable, Double>();
      for (Variable variable : o.getVariables()) {
        coeffs.put(_varLookup.get(variable), o.getCoefficient(variable));
      }     
      scip.createLinearMinimizeObj(coeffs);
		}
	}
	