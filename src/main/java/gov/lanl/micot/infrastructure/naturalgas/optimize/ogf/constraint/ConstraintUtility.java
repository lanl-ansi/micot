package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf.constraint;

import gov.lanl.micot.infrastructure.naturalgas.model.Junction;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasConnection;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.Pipe;
import gov.lanl.micot.infrastructure.naturalgas.model.Resistor;

/**
 * A simple class for centralizing common calculations that are used by a lot of constraints
 * @author Russell Bent
 */
public class ConstraintUtility {
  
  /**
   * No instantations
   */
  private ConstraintUtility() {    
  }

  public static double computeflowUpperBound(NaturalGasModel model, NaturalGasConnection edge, double totalConsumption) {
    Junction i = model.getFirstNode(edge).getJunction();
    Junction j = model.getSecondNode(edge).getJunction();
  
    double maxPi = i.getMaximumPressure() * i.getMaximumPressure();
    double minPj = j.getMinimumPressure() * j.getMinimumPressure();
    
    double w = edge instanceof Pipe ? ((Pipe)edge).getResistance() : ((Resistor)edge).getResistance();
    double ub = (maxPi - minPj) * w;
    
    // no way to send flow from i to j
    if (ub < 0) {
      ub = -Math.sqrt(Math.abs(ub));
      ub = Math.max(ub, -totalConsumption);
    }
    else {
      ub = Math.sqrt(ub);              
      ub = Math.min(ub, totalConsumption);              
    }    
    return ub;
  }
  
  public static double computeflowLowerBound(NaturalGasModel model, NaturalGasConnection edge, double totalConsumption) {
    Junction i = model.getFirstNode(edge).getJunction();
    Junction j = model.getSecondNode(edge).getJunction();
  
    double minPi = i.getMinimumPressure() * i.getMinimumPressure();
    double maxPj = j.getMaximumPressure() * j.getMaximumPressure();
    
    double w = edge instanceof Pipe ? ((Pipe)edge).getResistance() : ((Resistor)edge).getResistance();
    double lb = (maxPj - minPi) * w;
    
    // no way to send flow from j to i
    if (lb < 0) {
      lb = Math.sqrt(Math.abs(lb));
      lb = Math.min(lb, totalConsumption);
    }
    else {
      lb =-Math.sqrt(lb);
      lb = Math.max(lb, -totalConsumption);
    }    
    return lb;
  }  
}
