package gov.lanl.micot.infrastructure.ep.io.cdf;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFAreaFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFBatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFBusFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFGeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFHeader;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFIntertieFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFLineFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFLoadFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelConstants;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelImpl;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFShuntFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFTransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFZoneFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.FileParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map;
import java.io.*;

/**
 * Reads a CDF file format for energy transmission simulation, allows
 * modification of parameters of the PFW file and re-generation of a new PFW
 * file. CDF is an IEEE format
 * 
 * @author Russell Bent
 * 
 */
public class CDFFile extends FileParser implements ElectricPowerModelFile {

	public static final String	  LAST_LINE	    = "END OF DATA";
	protected static final String BUS_HEADER  = "BUS DATA FOLLOWS";
	protected static final String BUS_FOOTER  = "-999";
  protected static final String LINE_HEADER = "BRANCH DATA FOLLOWS";
  protected static final String LINE_FOOTER = "-999";
  protected static final String ZONE_HEADER = "LOSS ZONES FOLLOWS";
  protected static final String ZONE_FOOTER = "-99";
  protected static final String AREA_HEADER = "INTERCHANGE DATA FOLLOWS";
	protected static final String AREA_FOOTER = "-9";
	protected static final String TIE_HEADER  = "TIE LINES FOLLOWS";
	protected static final String TIE_FOOTER  = "-999";
  
	/**
	 * Constructor
	 */
	public CDFFile() {
	}

	@Override
	public ElectricPowerModel readModel(String filename) throws IOException {
		ElectricPowerModel state = parseFile(filename);
		return state;
	}
		
	@Override
	public void saveFile(String filename, ElectricPowerModel model)  throws IOException {
		CDFFileWriter writer = CDFFileWriterFactory.getInstance().getCDFFileWriter(model);		
		writer.saveFile(model, filename);
	}
	
	/**
	 * Parses the file and returns a model
	 * @throws IOException 
	 */
	private ElectricPowerModel parseFile(String filename) throws IOException {
		CDFModelImpl model = new CDFModelImpl();
		
		readFile(filename);		
		
		CDFAreaFactory areaFactory = model.getControlAreaFactory();
		CDFZoneFactory zoneFactory = model.getZoneFactory();
		CDFBusFactory busFactory = model.getBusFactory();
		CDFGeneratorFactory generatorFactory = model.getGeneratorFactory();
		CDFBatteryFactory batteryFactory = model.getBatteryFactory();
		CDFIntertieFactory intertieFactory = model.getIntertieFactory();
		CDFLineFactory lineFactory = model.getLineFactory();
		CDFTransformerFactory transformerFactory = model.getTransformerFactory();
		CDFLoadFactory loadFactory = model.getLoadFactory();
		CDFShuntFactory shuntFactory = model.getShuntCapacitorFactory();
		
		HashMap<Integer, Generator> generators = new HashMap<Integer,Generator>();
    Map<Integer,Point> busPoints = new HashMap<Integer,Point>();
    HashMap<Integer, Bus> buses = new HashMap<Integer, Bus>();
    HashMap<ControlArea, Integer> slackBuses = new HashMap<ControlArea, Integer>();
    HashMap<Asset, Integer> assetAreas = new HashMap<Asset, Integer>();
    HashMap<Asset, Integer> assetZones = new HashMap<Asset, Integer>();
    HashMap<Integer, ControlArea> areas = new HashMap<Integer, ControlArea>();
    HashMap<Integer, Zone> zones = new HashMap<Integer, Zone>();
    HashMap<Asset, Integer> controlBuses = new HashMap<Asset, Integer>();
    HashMap<Intertie, Integer> meteredAreas = new HashMap<Intertie, Integer>();
    HashMap<Intertie, Integer> nonMeteredAreas = new HashMap<Intertie, Integer>();
		
    String header = fileLines.get(0);
    model.setHeader(new CDFHeader(header));
    double mvaBase = Double.parseDouble(header.substring(31,37).trim());    
    model.setMVABase(mvaBase);

    // determine the coordinates
    boolean inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(COORDINATE_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(COORDINATE_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, ",");
        int id = Integer.parseInt(tokenizer.nextToken().trim());
        Double x = 0.0;
        Double y = 0.0;
        
        if (tokenizer.hasMoreTokens()) {
          x = Double.parseDouble(tokenizer.nextToken().trim());
          y = Double.parseDouble(tokenizer.nextToken().trim());
        }
        busPoints.put(id, new PointImpl(x,y));
      }
    }

		// create the buses
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(BUS_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id = Integer.parseInt(tokenizer.nextToken());        
        Bus bus = busFactory.createBus(buffer, busPoints.get(id));
        model.addBus(bus);      
     
        buses.put(id, bus);
        int area = Integer.parseInt(buffer.substring(19,20).trim());
        int zone = Integer.parseInt(buffer.substring(21,23).trim());
        assetAreas.put(bus, area);
        assetZones.put(bus, zone);
      }
    }
    
    // create the generators
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(BUS_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        double realGeneration = Double.parseDouble(buffer.substring(60,67).trim());
        double reactiveGeneration = Double.parseDouble(buffer.substring(68,75).trim());

        if (realGeneration == 0 && reactiveGeneration == 0) {
          continue;
        }
        
        StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id = Integer.parseInt(tokenizer.nextToken());        
        Generator generator  = generatorFactory.createGenerator(buffer, busPoints.get(id));
        model.addGenerator(generator, buses.get(id));      
        generators.put(id, generator);
        
        int area = Integer.parseInt(buffer.substring(19,20).trim());
        int zone = Integer.parseInt(buffer.substring(21,23).trim());
        int remoteBus = Integer.parseInt(buffer.substring(124,127).trim());
        assetAreas.put(generator, area);
        assetZones.put(generator, zone);
        controlBuses.put(generator, remoteBus);        
      }
    }

    // create the loads
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(BUS_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        double realLoad = Double.parseDouble(buffer.substring(41,49).trim());
        double reactiveLoad = Double.parseDouble(buffer.substring(50,59).trim());

        if (realLoad == 0 && reactiveLoad == 0) {
          continue;
        }
                
        StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id = Integer.parseInt(tokenizer.nextToken());        
        Load load  = loadFactory.createLoad(buffer, busPoints.get(id));
        model.addLoad(load, buses.get(id));      
        
        int area = Integer.parseInt(buffer.substring(19,20).trim());
        int zone = Integer.parseInt(buffer.substring(21,23).trim());    
        assetAreas.put(load, area);
        assetZones.put(load, zone);
      }
    }


    // create the shunts
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(BUS_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        double realShunt = Double.parseDouble(buffer.substring(107,114).trim());
        double reactiveShunt = Double.parseDouble(buffer.substring(115,122).trim());

        if (realShunt == 0 && reactiveShunt == 0) {
          continue;
        }
        
        StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id = Integer.parseInt(tokenizer.nextToken());        
        ShuntCapacitor capacitor  = shuntFactory.createShunt(buffer, busPoints.get(id));
        model.addShuntCapacitor(capacitor, buses.get(id));      
        
        int area = Integer.parseInt(buffer.substring(19,20).trim());
        int zone = Integer.parseInt(buffer.substring(21,23).trim());        
        assetAreas.put(capacitor, area);
        assetZones.put(capacitor, zone);
      }
    }

    // create the lines
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(LINE_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(LINE_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        int type = Integer.parseInt(buffer.substring(18,19).trim());

        
        if (type != 0) {
          continue;
        }
                
        StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id1 = Integer.parseInt(tokenizer.nextToken());        
        int id2 = Integer.parseInt(tokenizer.nextToken());        
        Point point1 = busPoints.get(id1);
        Point point2 = busPoints.get(id2);
        
        ArrayList<Point> points = null;
        if (point1 != null && point2 != null) {
          points = new ArrayList<Point>();
          points.add(point1);
          points.add(point2);
        }
        
        Line l  = lineFactory.createLine(buffer, buses.get(id1), buses.get(id2), points);
        model.addEdge(l, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));      
        
        int area = Integer.parseInt(buffer.substring(11,12).trim());
        int zone = Integer.parseInt(buffer.substring(14,15).trim());
        assetAreas.put(l,area);
        assetZones.put(l, zone);
      }
    }

    // create the transformers
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(LINE_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(LINE_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        int type = Integer.parseInt(buffer.substring(18,19).trim());

        
        if (type == 0) {
          continue;
        }
        
        StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id1 = Integer.parseInt(tokenizer.nextToken());        
        int id2 = Integer.parseInt(tokenizer.nextToken());        
        Point point1 = busPoints.get(id1);
        Point point2 = busPoints.get(id2);
        
        ArrayList<Point> points = null;
        if (point1 != null && point2 != null) {
          points = new ArrayList<Point>();
          points.add(point1);
          points.add(point2);
        }
                
        Transformer l  = transformerFactory.createTransformer(buffer, buses.get(id1), buses.get(id2), points);
        model.addEdge(l, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));      
        
        int area = Integer.parseInt(buffer.substring(11,12).trim());
        int zone = Integer.parseInt(buffer.substring(14,15).trim());
        int controlBus = Integer.parseInt(buffer.substring(69,72).trim());
        assetAreas.put(l, area);
        assetZones.put(l, zone);
        controlBuses.put(l, controlBus);
      }
    }
    
    // create the zones
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(ZONE_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(ZONE_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {                
        Zone zone  = zoneFactory.createZone(buffer);
        model.addZone(zone);   
        zones.put(zone.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class), zone);
      }
    }
    
    // create the areas
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(AREA_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(AREA_HEADER)) {
        inBlock = true;
        continue;
      }
      if (inBlock) { 
        int slackBus = Integer.parseInt(buffer.substring(3,7).trim());        
        ControlArea area  = areaFactory.createArea(buffer);
        model.addArea(area); 
        slackBuses.put(area,slackBus);
        areas.put(area.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class), area);        
      }
    }

    // create the interties
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(TIE_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(TIE_HEADER)) {
        inBlock = true;
        continue;
      }
            
      if (inBlock) {              
        //StringTokenizer tokenizer = new StringTokenizer(buffer, " ");
        int id1 = Integer.parseInt(buffer.substring(0,4).trim());        
        int id2 = Integer.parseInt(buffer.substring(10,14).trim());      
        Point point1 = busPoints.get(id1);
        Point point2 = busPoints.get(id2);
        
        ArrayList<Point> points = null;
        if (point1 != null && point2 != null) {
          points = new ArrayList<Point>();
          points.add(point1);
          points.add(point2);
        }
        
        Intertie intertie  = intertieFactory.createIntertie(buffer, buses.get(id1), buses.get(id2), points);
        model.addEdge(intertie, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));      
        
        int meteredArea = Integer.parseInt(buffer.substring(6,8).trim());   
        int nonMeteredArea = Integer.parseInt(buffer.substring(16,18).trim());
        meteredAreas.put(intertie, meteredArea);
        nonMeteredAreas.put(intertie, nonMeteredArea);
      }
    }
    
    // read in the extra generator information
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(EXTRA_GENERATOR_FOOTER_START)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(EXTRA_GENERATOR_HEADER_START)) {
        inBlock = true;
        continue;
      }
            
      if (inBlock) {              
        StringTokenizer tokenizer = new StringTokenizer(buffer,",");
        int id = Integer.parseInt(tokenizer.nextToken());
        double cost = Double.parseDouble(tokenizer.nextToken());
        int fuelType = Integer.parseInt(tokenizer.nextToken());
        double carbon = Double.parseDouble(tokenizer.nextToken());
        double capacityFactor = tokenizer.hasMoreTokens() ? Double.parseDouble(tokenizer.nextToken()) : 1.0;
        
        Generator generator = generators.get(id);
        generator.setAttribute(Generator.ECONOMIC_COST_KEY,cost);
        generator.setAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.getEnum(fuelType));
        generator.setAttribute(Generator.CARBON_OUTPUT_KEY, carbon);
        generator.setCapacityFactor(capacityFactor);
        
        // update the max generation
        generator.setReactiveGenerationMax(generator.getReactiveGenerationMax() / capacityFactor);
        generator.setRealGenerationMax(generator.getRealGenerationMax() / capacityFactor);
      }
    }
    
    // read in the battery information
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(BATTERY_FOOTER)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BATTERY_HEADER)) {
        inBlock = true;
        continue;
      }
            
      if (inBlock) {              
        StringTokenizer tokenizer = new StringTokenizer(buffer,",");
        int id = Integer.parseInt(tokenizer.nextToken());
        
        Generator generator = generators.get(id);
        Battery battery = batteryFactory.createBattery(fileLines.get(line), generator, buses.get(id));
        model.addBattery(battery, buses.get(id));
        if (generator != null) {
          model.removeGenerator(generator);
        }         
      }
    }

    // set the slack buses for the areas
    for (ControlArea area : slackBuses.keySet()) {
      model.setSlackBus(area, buses.get(slackBuses.get(area)));
    }
    
    // set up the areas for the assets
    for (Asset asset : assetAreas.keySet()) {
      model.setControlArea(asset, areas.get(assetAreas.get(asset)));
    }

     // set up the zones for the assets
    for (Asset asset : assetZones.keySet()) {
      model.setZone(asset, zones.get(assetZones.get(asset)));
    }
    
    // set up the control buses for the assets
    for (Asset asset : controlBuses.keySet()) {
      model.setControlBus(asset, buses.get(controlBuses.get(asset)));
    }
    
    // set up the metered areas for the interties
    for (Intertie intertie : meteredAreas.keySet()) {
      model.setMeteredArea(intertie, areas.get(meteredAreas.get(intertie)));
    }
    
    // set up the nonmetered areas for the interties
    for (Intertie intertie : nonMeteredAreas.keySet()) {
      model.setMeteredArea(intertie, areas.get(nonMeteredAreas.get(intertie)));
    }
    
		return model;
	}


  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename,(ElectricPowerModel)model);
  }

  /**
   * Read in a simple test file
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    //String initialFile = "ieee300.cdf";        
    String initialFile = "data" + File.separatorChar + "ep" + File.separatorChar + "ieee300.cdf";        
    ElectricPowerModel model = new CDFFile().readModel(initialFile);
    double netload = 0;
    for (Generator generator : model.getGenerators()) {    	
    	netload += generator.getRealGenerationMax();
    }
    
    System.out.println(netload);
    
    for (Load load : model.getLoads()) {
    	netload -= load.getRealLoad().doubleValue();
    }
    
    System.out.println(netload);
  }

}
