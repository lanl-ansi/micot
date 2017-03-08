package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Producer;
import gov.lanl.micot.util.math.PiecewiseLinearFunction;
import gov.lanl.micot.util.math.PolynomialFunction;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

/**
 * Class for computing well field defaults
 * @author Russell Bent
 */
public class WellDefaults {
  
  private static WellDefaults INSTANCE = null;
  
  public static WellDefaults getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new WellDefaults();
    }
    return INSTANCE;
  }

  /**
   * Constructor
   */
  private WellDefaults() {    
  }

  /**
   * Calculate the default cost of a well per unit of gas
   * @param battery
   * @return
   */
  public double calculateCost(Producer well) {
    Number cost = well.getAttribute(Well.ECONOMIC_COST_KEY, Number.class);
    if (cost != null) {
      return cost.doubleValue();
    }    
    return 1.0;
  }
  
  /**
   * Convert a cost object into a linear cost model
   * @param cost
   * @return
   */
  public Number calculateLinearCost(Producer producer) {
    if (producer.getMaximumProduction().equals(producer.getMinimumProduction())) {
      return 0;
    }
    Object cost = producer.getAttribute(Producer.ECONOMIC_COST_KEY);   
    
    if (cost instanceof Number) {
      return (Number)cost;
    }
    // extrapolate first and last point
    if (cost instanceof PiecewiseLinearFunction) {
      PiecewiseLinearFunction fcn = (PiecewiseLinearFunction)cost;
      Collection<Point2D> points = fcn.getDataPoints();
      Iterator<Point2D> it = points.iterator();
      Point2D point = null;
      while (it.hasNext()) {
        point = it.next();
      }
      return point.getY() / point.getX();
    }
    // return just the linear component
    if (cost instanceof PolynomialFunction) {
      double xmin = 0;
      double xmax = producer.getMaximumProduction().doubleValue();
      double ymin = 0;
      double ymax = 0;
      
      PolynomialFunction fcn = (PolynomialFunction)cost;
      Collection<Double> coefficients = fcn.getPolynomialCoefficients();      
      Double[] c = coefficients.toArray(new Double[0]);
      for (int i = 0; i < c.length; ++i) {
        int coeff = c.length - i - 1;
        ymin += c[i] * Math.pow(xmin,coeff);
        ymax += c[i] * Math.pow(xmax,coeff);
      }
      
      return (ymax - ymin) / (xmax - xmin);
    }
    
    return calculateCost(producer);
  }
  
  /**
   * Calculate the load shed cost
   * @param load
   * @param nodes
   * @return
   */
  public Number calculateLoadShedCost(CityGate load, Collection<NaturalGasNode> nodes) {      
    double maxCost = 0;
    for (NaturalGasNode node : nodes) {
      for (Producer producer : node.getComponents(Producer.class)) {      

        if (producer.getMaximumProduction().equals(producer.getMinimumProduction())) {
          continue;
        }
      
        double cost = calculateLinearCost(producer).doubleValue();
        maxCost = Math.max(cost, maxCost);
      }     
    }
    maxCost += 1;
    return maxCost * 2;
  }
  
}
