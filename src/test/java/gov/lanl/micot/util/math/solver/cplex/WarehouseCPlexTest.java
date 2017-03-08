package gov.lanl.micot.util.math.solver.cplex;

import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.LinearConstraintEquals;
import gov.lanl.micot.util.math.solver.LinearConstraintLessEq;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.mathprogram.DefaultMathematicalProgramFactory;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjective;
import gov.lanl.micot.util.math.solver.mathprogram.LinearObjectiveMinimize;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgramFlags;
import junit.framework.TestCase;


/**
 * This is a duplicate of the CPLEX example implemented using the abstract model framework
 * @author 236322
 *
 */
public class WarehouseCPlexTest extends TestCase  {

	public void testIntegerProgram() throws SolverException  {
    if (System.getenv("TEST_CPLEX") != null) {
      return;
    }
	  
		MathematicalProgramFlags flags = new MathematicalProgramFlags();
  	flags.put(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG, "gov.lanl.micot.util.math.solver.integerprogram.cplex.CPLEXIntegerProgramFactory");
  	MathematicalProgram model = DefaultMathematicalProgramFactory.getInstance().constructMathematicalProgram(flags);

		int nbWhouses = 4;
		int nbLoads = 31;
     
	 DiscreteVariable[] capVars = model.makeDiscreteVariable(nbWhouses);
	 for(int i = 0; i < capVars.length; i++){
		 model.addBounds(capVars[i], 0, 10);
	 }
	 
	 double[]    costs   = {1.0, 2.0, 4.0, 6.0}; // Cost per warehouse
	 
	 // These variables represent the assignment of a
	 // load to a warehouse.
	 DiscreteVariable[][] assignVars = new DiscreteVariable[nbWhouses][];
	 for (int w = 0; w < nbWhouses; w++) {
		 assignVars[w] = model.makeDiscreteVariable(nbLoads);
		 for(int j = 0; j < assignVars[w].length; j++){
			 model.addBounds(assignVars[w][j], 0, 1);
		 }
		
		 // Links the number of loads assigned to a warehouse with 
		 // the capacity variable of the warehouse.
		 LinearConstraint loadConst = new LinearConstraintEquals("cap"+w);
		 for(int j = 0; j < assignVars[w].length; j++){
			 loadConst.addVariable(assignVars[w][j]);
		 }
		 loadConst.setRightHandSide(capVars[w]);
		 model.addLinearConstraint(loadConst);
	  }
	 
	   // Each load must be assigned to just one warehouse.
	 for (int l = 0; l < nbLoads; l++) {
	    LinearConstraint loadConst = new LinearConstraintEquals("assign"+l);
	    for (int w = 0; w < nbWhouses; w++){
	    	loadConst.addVariable(assignVars[w][l]);
	    }
	    loadConst.setRightHandSide(1.0);
	    model.addLinearConstraint(loadConst);
	 }
	 
	 LinearObjective obj = new LinearObjectiveMinimize(capVars, costs);
	 
	 
	 model.setLinearObjective(obj);
	 
	 /*Solution solution = model.solve();
	 solution.removeSmallValues(1e-7);
	 
	 System.out.println("--------------------------------------------");
	 System.out.println();
	 System.out.println("Solution found:");
	 System.out.println(" Objective value = " + solution.getObjectiveValue());
	 System.out.println();
	 	for (int w = 0; w < nbWhouses; w++) {
	 		System.out.println("Warehouse " + w + ": stored " + solution.getValue(capVars[w]) + " loads");
	    for (int l = 0; l < nbLoads; l++) {
	    	if (solution.getValueDouble(assignVars[w][l]) > 0){
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
*/
	  
	}
}
