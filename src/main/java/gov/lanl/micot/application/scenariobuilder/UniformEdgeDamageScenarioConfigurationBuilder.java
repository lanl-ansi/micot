package gov.lanl.micot.application.scenariobuilder;

import gov.lanl.micot.infrastructure.config.AssetModification;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.project.ScenarioConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Damage is uniform based on a specified rate
 * @author Russell Bent
 *
 */
public class UniformEdgeDamageScenarioConfigurationBuilder implements ScenarioConfigurationBuilder {

  private double damageRate;  
  private String damageKey;
  
  /**
   * Constructor
   */
  public UniformEdgeDamageScenarioConfigurationBuilder(double damageRate, String damageKey) {    
    this.damageRate = damageRate;
    this.damageKey = damageKey;
  }
    
  @Override
  public void updateScenarios(Collection<ScenarioConfiguration> scenarios, Collection<Asset> assets, String idKey) {
    
    Random random = new Random(System.currentTimeMillis());
    
    // first compute all the asset length
    Map<Asset, Double> lengths = new HashMap<Asset,Double>();
    for (Asset asset : assets) {
      if (!(asset instanceof Connection)) {
        continue;
      }
      
      Connection e = (Connection)asset;      
      
      double length = e.getAttribute(ElectricPowerFlowConnection.LENGTH_KEY, Double.class);
      double miles = (length * 1000.0) / 5280.0;

//      double miles = 0;
//      Line line = e.getCoordinates();
  //    Point points[] = line.getCoordinates();
    //  for (int i = 1; i < points.length; ++i) {
      //  miles += points[i-1].distance(points[i]) * MILES_PER_DEGREES;      
      //}
      
      lengths.put(asset, miles);
    }
    
      
    for (ScenarioConfiguration config : scenarios) {  
      for (Asset asset : assets) {
        if (!(asset instanceof Connection)) {
          continue;
        }

        Connection e = (Connection)asset;
        
        double miles = lengths.get(e);
        boolean damaged = false;
        int numMiles = (int) miles;
        double leftover = miles - numMiles;
        for (int k = 0; k < numMiles; ++k) {
          if (random.nextDouble() <= damageRate) {
            damaged = true;
          }
        }

        if (random.nextDouble() <= leftover * damageRate) {
          damaged = true;
        }

     
        AssetModification modification = new AssetModification();
        modification.setComponentClass(e.getClass().getCanonicalName());
        modification.addKey(idKey, e.getAttribute(idKey));        
        modification.addAttribute(damageKey, damaged);
        config.addComponentModification(modification);
      }
      
    }   

    
    
  }

}
