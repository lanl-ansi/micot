package gov.lanl.micot.infrastructure.ep.model;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import gov.lanl.micot.util.math.PiecewiseLinearFunction;
import gov.lanl.micot.util.math.PolynomialFunction;

/**
 * Class for computing generator defaults
 * @author Russell Bent
 */
public class GeneratorDefaults {
  
  private static GeneratorDefaults INSTANCE = null;
  
  public static GeneratorDefaults getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new GeneratorDefaults();
    }
    return INSTANCE;
  }

  /**
   * Constructor
   */
  private GeneratorDefaults() {    
  }

  /**
   * Calculate default cost per MWH
   * Source EIA Annual Energy Outlook 2001
   * @param generator
   * @return
   */
  public double calculateCost(Generator generator) {
    Number cost = generator.getAttribute(Generator.ECONOMIC_COST_KEY, Number.class);
    if (cost != null) {
      return cost.doubleValue();
    }
    FuelTypeEnum fuelType = generator.getAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.class);
    if (fuelType != null) {
      if (fuelType.equals(FuelTypeEnum.BIOMASS)) {
        return 112.5;
      }
      else if (fuelType.equals(FuelTypeEnum.COAL)) {
        return 94.8;
      }
      else if (fuelType.equals(FuelTypeEnum.GEOTHERMOL)) {
        return 101.7;
      }
      else if (fuelType.equals(FuelTypeEnum.HYDRO)) {
        return 86.4;
      }
      else if (fuelType.equals(FuelTypeEnum.NATURAL_GAS)) {
        return 66.1;
      }
      else if (fuelType.equals(FuelTypeEnum.NUCLEAR)) {
        return 113.9;
      }
      else if (fuelType.equals(FuelTypeEnum.OIL)) {
        return 300.0;
      }
      else if (fuelType.equals(FuelTypeEnum.SOLAR)) {
        return 210.7;
      }
      else if (fuelType.equals(FuelTypeEnum.WIND)) {
        return 97.0;
      }
      else if (fuelType.equals(FuelTypeEnum.UNKNOWN)) {
        return 0.0;
      }
    }
    return 0.0;
  }

  /**
   * Calculate the default cost of a battery per MWH (default is lithium ion)
   * @param battery
   * @return
   */
  public double calculateCost(Battery battery) {
    Number cost = battery.getAttribute(Generator.ECONOMIC_COST_KEY, Number.class);
    if (cost != null) {
      return cost.doubleValue();
    }    
    return 0.0;
  }
  
  /**
   * Return the default cost of producer
   * @param generator
   * @return
   */
  public double calculateCost(ElectricPowerProducer generator) {
  	if (generator instanceof Generator) {
  		return calculateCost((Generator)generator);
  	}
  	else if (generator instanceof Battery) {
  		return calculateCost((Battery)generator);  		
  	}
  	return 0.0;
  }
  
  /**
   * Calculate default carbons
   * @param generator
   * @return
   * SOURCE: EIA report "Carbon Dioxide Emissions from Generation of Electric Power in 
   * the United States, 2000"
   */
  public Double calculateCarbon(ElectricPowerProducer generator) {
    Number carbon = generator.getAttribute(Generator.CARBON_OUTPUT_KEY, Number.class);
    if (carbon != null) {
      return carbon.doubleValue();
    }
    FuelTypeEnum fuelType = generator.getAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.class);
    if (fuelType != null) {
      if (fuelType.equals(FuelTypeEnum.BIOMASS)) {
        return .01378;
      }
      else if (fuelType.equals(FuelTypeEnum.COAL)) {
        return .002117;
      }
      else if (fuelType.equals(FuelTypeEnum.GEOTHERMOL)) {
        return 0.0;
      }
      else if (fuelType.equals(FuelTypeEnum.HYDRO)) {
        return 0.0;
      }
      else if (fuelType.equals(FuelTypeEnum.NATURAL_GAS)) {
        return .001314;
      }
      else if (fuelType.equals(FuelTypeEnum.NUCLEAR)) {
        return 0.0;
      }
      else if (fuelType.equals(FuelTypeEnum.OIL)) {
        return .001915;
      }
      else if (fuelType.equals(FuelTypeEnum.SOLAR)) {
        return 0.0;
      }
      else if (fuelType.equals(FuelTypeEnum.WIND)) {
        return 0.0;
      }
      else if (fuelType.equals(FuelTypeEnum.UNKNOWN)) {
        return 0.0;
      }
    }
    return 0.0;
  }

  
  /**
   * covert to a linear model
   * @param cost
   * @return
   */
  public Number convertToLinearCost(ElectricPowerProducer generator) {
    if (generator.getRealGenerationMin() == generator.getRealGenerationMax()) {
      return 0;
    }
    Object cost = generator.getAttribute(ElectricPowerProducer.ECONOMIC_COST_KEY);   
    
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
      double xmax = generator.getRealGenerationMax();
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
    
    return calculateCost(generator);
  }

  public Number getConstantPartOfCost(ElectricPowerProducer generator) {
	    if (generator.getRealGenerationMin() == generator.getRealGenerationMax()) {
	      return 0;
	    }
	    Object cost = generator.getAttribute(ElectricPowerProducer.ECONOMIC_COST_KEY);   
	    
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
	    // return just the quadratic component
	    if (cost instanceof PolynomialFunction) {
	      PolynomialFunction fcn = (PolynomialFunction)cost;
	      Collection<Double> coefficients = fcn.getPolynomialCoefficients();      
	      Double[] c = coefficients.toArray(new Double[0]);
	      int i = -(0-c.length+1);
	      if (i >= c.length || i < 0) {
	        return 0;
	      }
	      return c[i];
	    }
	    return 0;
	  }
  
  /**
   * Get the linear part of a cost model
   * @param cost
   * @return
   */
  public Number getLinearPartOfCost(ElectricPowerProducer generator) {
    if (generator.getRealGenerationMin() == generator.getRealGenerationMax()) {
      return 0;
    }
    Object cost = generator.getAttribute(ElectricPowerProducer.ECONOMIC_COST_KEY);   
    
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
    // return just the quadratic component
    if (cost instanceof PolynomialFunction) {
      PolynomialFunction fcn = (PolynomialFunction)cost;
      Collection<Double> coefficients = fcn.getPolynomialCoefficients();      
      Double[] c = coefficients.toArray(new Double[0]);
      int i = -(1-c.length+1);
      if (i >= c.length || i < 0) {
        return 0;
      }
      return c[i];
    }
    return 0;
  }

  /**
   * Get the quadratic part of a cost model
   * @param cost
   * @return
   */
  public Number getQuadraticPartOfCost(ElectricPowerProducer generator) {
    if (generator.getRealGenerationMin() == generator.getRealGenerationMax()) {
      return 0;
    }
    Object cost = generator.getAttribute(ElectricPowerProducer.ECONOMIC_COST_KEY);   
    
    if (cost instanceof Number) {
      return 0; // it is the linear term
    }
    // extrapolate first and last point
    if (cost instanceof PiecewiseLinearFunction) {
      return 0; // same for here
    }
    // return just the quadratic component
    if (cost instanceof PolynomialFunction) {
      PolynomialFunction fcn = (PolynomialFunction)cost;
      Collection<Double> coefficients = fcn.getPolynomialCoefficients();      
      Double[] c = coefficients.toArray(new Double[0]);
      int i = -(2-c.length+1);
      if (i >= c.length || i < 0) {
        return 0;
      }
      return c[i];
    }
    return 0;
  }

  
  
  /**
   * Calculate the load shed cost
   * @param load
   * @param nodes
   * @return
   */
  public Number calculateLoadShedCost(Load load, Collection<ElectricPowerNode> nodes) {      
    double maxCost = 0;
    for (ElectricPowerNode node : nodes) {
      for (ElectricPowerProducer producer : node.getComponents(ElectricPowerProducer.class)) {      

        if (producer.getRealGenerationMin() == producer.getRealGenerationMax()) {
          continue;
        }
      
        double cost = convertToLinearCost(producer).doubleValue();
        maxCost = Math.max(cost, maxCost);
      }     
    }
    maxCost += 1;
    return maxCost * 2;
  }
  
}
