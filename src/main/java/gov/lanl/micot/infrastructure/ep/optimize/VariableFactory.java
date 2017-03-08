package gov.lanl.micot.infrastructure.ep.optimize;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * Interface for factories that create variables
 * @author Russell Bent
 */
public interface VariableFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory<ElectricPowerNode, ElectricPowerModel> {


}
