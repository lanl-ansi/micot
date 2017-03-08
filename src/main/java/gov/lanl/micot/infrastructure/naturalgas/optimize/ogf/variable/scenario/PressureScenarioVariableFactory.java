package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.variable.scenario;

import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.infrastructure.naturalgas.model.CityGate;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;
import gov.lanl.micot.infrastructure.naturalgas.model.Well;
import gov.lanl.micot.infrastructure.naturalgas.optimize.VariableFactory;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.NoVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;
import gov.lanl.micot.util.math.solver.mathprogram.MathematicalProgram;

import java.util.ArrayList;
import java.util.Collection;

/**
 * General class for creating pressure variables associated with scenarios
 * 
 * @author Conrado Borraz (extending Russell's factory)
 */
public class PressureScenarioVariableFactory implements VariableFactory {
  private Collection<Scenario> scenarios = new ArrayList<Scenario>();

  // Constructor: @param models
  public PressureScenarioVariableFactory(Collection<Scenario> scenarios) {
    this.scenarios = scenarios;
  }

  // Get the variable name: @param edge, @return
  public String getVariableName(Scenario k, NaturalGasNode n) {
    return "P." + k.getIndex() + "." + n.toString();
  }

  @Override
  public Collection<Variable> createVariables(MathematicalProgram program, NaturalGasModel model) throws VariableExistsException {

    ArrayList<Variable> variables = new ArrayList<Variable>();
    for (Scenario scenario : scenarios) {
//      SystemLogger.getSystemLogger().systemLogger.println("\n-- Create PressureScenario variables: " + // p,S,D = pressure, Supply, Demand
  //        "\n\t       \t       \t p,S,D \t       \t       \tCurrent\tActual \tDesired\t   VARIABLE  " + "\n\t Node  \t Name  \t Value \t  Min  \t  Max  \t  P    \tStatus \tStatus \t    STATUS   " + "\n\t-------\t-------\t-------\t-------\t-------\t-------\t-------\t-------\t--------------");
      for (NaturalGasNode n : model.getNodes()) {
        String varName = getVariableName(scenario, n);
        variables.add(program.makeContinuousVariable(varName));
    //    SystemLogger.getSystemLogger().systemLogger.print("\t" + n);
        //if (n.getJunction().getName().length() > 7)
      //    SystemLogger.getSystemLogger().systemLogger.print("\t" + n.getJunction().getName().substring(0, 7));
        //else
          //SystemLogger.getSystemLogger().systemLogger.print("\t" + n.getJunction().getName());
        //SystemLogger.getSystemLogger().systemLogger.print("\t" + n.getJunction().getInitialPressure() + "\t" + n.getJunction().getMinimumPressure() + "\t" + n.getJunction().getMaximumPressure() + "\t" + n.getJunction().getPressure() + "\t" + n.getJunction().getActualStatus() + "\t" + n.getJunction().getDesiredStatus()
          //  + "\t'" + varName + "' ADDED!");
        //for (Well n1 : n.getComponents(Well.class)) {
          //SystemLogger.getSystemLogger().systemLogger.print("\n\t\tWELL" + "\t" + n1.getActualProduction() + "\t" + n1.getMinimumProduction() + "\t" + n1.getMaximumProduction()); // + "\t Coordinates: " + n1.getCoordinate());
       // }
        //for (CityGate n1 : n.getComponents(CityGate.class)) {
          //SystemLogger.getSystemLogger().systemLogger.print("\n\t\tGATE" + "\t" + n1.getActualConsumption() + "\t" + n1.getMinimumConsumption() + "\t" + n1.getMaximumConsumption()); // + "\t Coordinates: " + n1.getCoordinate());
        //}
      }

      double TotalGasSupply = 0.0, TotalGasDemand = 0.0;

//      SystemLogger.getSystemLogger().systemLogger.println("\n-- Verification of wells: ");
  //    SystemLogger.getSystemLogger().systemLogger.println("\t       \t       \t P R O D U C T I O N");
    //  SystemLogger.getSystemLogger().systemLogger.println("\t Node  \t Name  \tActual \t  Min  \t  Max  \tCoordinates");
      //SystemLogger.getSystemLogger().systemLogger.print("\t-------\t-------\t-------\t-------\t-------\t-----------");
      for (Well n : model.getWells()) {
        //SystemLogger.getSystemLogger().systemLogger.print("\n\t" + n + "\t" + "\t" + n.getActualProduction() + "\t" + n.getMinimumProduction() + "\t" + n.getMaximumProduction() + "\t " + n.getCoordinate());
        TotalGasSupply += n.getMaximumProduction().doubleValue();
      }
//      SystemLogger.getSystemLogger().systemLogger.println("\n\tTOTAL GAS SUPPLY = [" + TotalGasSupply + "]\n");

  //    SystemLogger.getSystemLogger().systemLogger.println("\n-- Verification of city gates: ");
    //  SystemLogger.getSystemLogger().systemLogger.println("\t       \t       \t C O N S U M P T I O N");
      //SystemLogger.getSystemLogger().systemLogger.println("\t Node  \t Name  \tActual \t  Min  \t  Max  \tCoordinates");
      //SystemLogger.getSystemLogger().systemLogger.print("\t-------\t-------\t-------\t-------\t-------\t-----------");
      for (CityGate n1 : model.getCityGates()) {
        //SystemLogger.getSystemLogger().systemLogger.print("\n\t" + n1 + "\t  ---" + "\t" + n1.getActualConsumption().intValue() + "\t" + n1.getMinimumConsumption().doubleValue() + "\t" + n1.getMaximumConsumption().intValue() + "\t " + n1.getCoordinate());
        TotalGasDemand += n1.getMinimumConsumption().doubleValue();
      }
      //SystemLogger.getSystemLogger().systemLogger.println("\n\tTOTAL GAS DEMAND = [" + TotalGasDemand + "]\n");

    }
    return variables;
  }

  @Override
  public Variable getVariable(MathematicalProgram program, Object asset) throws NoVariableException {
    throw new NoVariableException(asset.toString());
  }

  /**
   * Get a variable
   *
   * @param program
   * @param asset
   * @param scenario
   * @return
   * @throws NoVariableException
   */
  public Variable getVariable(MathematicalProgram program, Object asset, Scenario scenario) throws NoVariableException {
    if (asset instanceof NaturalGasNode) {
      Variable v = program.getVariable(getVariableName(scenario, (NaturalGasNode) asset));
      if (v != null)
        return v;
    }
    return null;
  }
}
