package gov.lanl.micot.infrastructure.ep.optimize.dcopf.assignment;

import java.util.Collection;
import java.util.HashMap;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.optimize.AssignmentFactory;
import gov.lanl.micot.infrastructure.ep.optimize.dcopf.variable.PhaseAngleTimeVariableFactory;
import gov.lanl.micot.util.math.LookupTableTimeDependentFunction;
import gov.lanl.micot.util.math.MathUtils;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

/**
 * Assignments for generator flows
 * @author Russell Bent
 */
public class FlowTimeAssignmentFactory implements AssignmentFactory {

  private int numberOfIncrements = -1;
  private double incrementSize = -1;
    
  /**
   * Constructor
   * @param numberOfIncrements
   * @param incrementSize
   */
  public FlowTimeAssignmentFactory(int numberOfIncrements, double incrementSize) {
    super();
    this.numberOfIncrements = numberOfIncrements;
    this.incrementSize = incrementSize;
  }
  
  @Override
  public void performAssignment(ElectricPowerModel model, MathematicalProgram problem, Solution solution/*, Collection<ElectricPowerNode> nodes*/) throws VariableExistsException, NoVariableException {
    PhaseAngleTimeVariableFactory phaseAngleVariableFactory = new PhaseAngleTimeVariableFactory(numberOfIncrements, incrementSize);
    HashMap<ElectricPowerNode,LookupTableTimeDependentFunction> phaseAngles = new HashMap<ElectricPowerNode,LookupTableTimeDependentFunction>();
    
    for (ElectricPowerNode node : model.getNodes()) {
      phaseAngles.put(node, new LookupTableTimeDependentFunction());
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize; 
        Variable variable = phaseAngleVariableFactory.getVariable(problem, node, time);
        phaseAngles.get(node).addEntry(time,solution.getValueDouble(variable));        
      }
    }
    
    
    for (ElectricPowerFlowConnection edge : model.getFlowConnections()) {
      if (!phaseAngles.containsKey(model.getFirstNode(edge)) || !phaseAngles.containsKey(model.getSecondNode(edge))) {
        continue;
      }
      
      LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();      
      for (int i = 0; i < numberOfIncrements; ++i) {
        double time = i * incrementSize;
      
        if (edge.getActualStatus() == false) {
          function.addEntry(time, 0.0);
          continue;
        }

        double impedance = edge.getSusceptance();
        ElectricPowerNode firstNode = model.getFirstNode(edge);
        ElectricPowerNode secondNode = model.getSecondNode(edge);

        double realFlow = impedance * (MathUtils.TO_FUNCTION(phaseAngles.get(firstNode)).getValue(time).doubleValue()
            - MathUtils.TO_FUNCTION(phaseAngles.get(secondNode)).getValue(time).doubleValue());
        function.addEntry(time, realFlow * model.getMVABase());
      }
      edge.setMWFlow(function);      
    }
    

    
  }

 
}
