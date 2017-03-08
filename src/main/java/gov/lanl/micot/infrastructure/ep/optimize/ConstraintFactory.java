package gov.lanl.micot.infrastructure.ep.optimize;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * This is an interface for augmenting OPFDC simulators with additional constraints
 * @author Russell Bent
 *
 */
public interface ConstraintFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory<ElectricPowerNode, ElectricPowerModel>{

 
}
