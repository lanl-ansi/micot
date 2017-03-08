package gov.lanl.micot.infrastructure.ep.io.matpower;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFile;
import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerAreaFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerBatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerBusFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerGeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerLineFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerLoadFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelConstants;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelImpl;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerShuntFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerTransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerZoneFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.FileParser;
import gov.lanl.micot.util.math.PiecewiseLinearFunctionFactory;
import gov.lanl.micot.util.math.PolynomialFunctionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map;
import java.awt.geom.Point2D;
import java.io.*;

/**
 * Reads a CDF file format for energy transmission simulation, allows
 * modification of parameters of the MatPower file and re-generation of a new MatPower
 * file. 
 * 
 * @author Russell Bent
 * 
 */
public class MatPowerFile extends FileParser implements ElectricPowerModelFile {

  protected static final String CASE_START_OF_LINE = "function mpc";
  protected static final String VERSION_START_OF_LINE = "mpc.version";
  protected static final String BASE_MVA_START_OF_LINE = "mpc.baseMVA";
  protected static final String BUS_START_OF_LINE = "mpc.bus";
  protected static final String BUS_END_OF_LINE = "];";
  protected static final String GENERATOR_START_OF_LINE = "mpc.gen ";
  protected static final String GENERATOR_END_OF_LINE = "];";
  protected static final String CASE_COMMENT_LINE = "%% MATPOWER Case Format : Version";
  protected static final String POWER_FLOW_DATA_COMMENT_LINE = "%%-----  Power Flow Data  -----%%";
  protected static final String BASE_MVA_COMMENT_LINE = "%% system MVA base";
  protected static final String BUS_COMMENT_LINE = "%% bus data";
  protected static final String BUS_DATA_LINE = "% bus_i type  Pd  Qd  Gs  Bs  area  Vm  Va  baseKV  zone  Vmax  Vmin";
  protected static final String GENERATOR_COMMENT_LINE = "%% generator data";
  protected static final String GENERATOR_DATA_LINE = "%  bus Pg  Qg  Qmax  Qmin  Vg  mBase status  Pmax  Pmin  Pc1 Pc2 Qc1min  Qc1max  Qc2min  Qc2max  ramp_agc  ramp_10 ramp_30 ramp_q  apf";
  protected static final String BRANCH_COMMENT_LINE = "%% branch data";
  protected static final String BRANCH_DATA_LINE = "%	fbus	tbus	r	x	b	rateA	rateB	rateC	ratio	angle	status	angmin	angmax";
  protected static final String BRANCH_START_OF_LINE = "mpc.branch";
  protected static final String BRANCH_END_OF_LINE = "];";
  protected static final String AREA_COMMENT_LINE = "%% area data";
  protected static final String AREA_DATA_LINE = "%	area	refbus";
  protected static final String AREA_START_OF_LINE = "mpc.areas";
  protected static final String AREA_END_OF_LINE = "];";
  protected static final String EXTRA_GENERATION_COMMENT_LINE = "%% generator cost data";
  protected static final String EXTRA_GENERATION_DATA_LINE1 = "%	1	startup	shutdown	n	x1	y1	...	xn	yn";
  protected static final String EXTRA_GENERATION_DATA_LINE2 = "%	2	startup	shutdown	n	c(n-1)	...	c0";
  protected static final String EXTRA_GENERATION_START_OF_LINE = "mpc.gencost";
  protected static final String EXTRA_GENERATION_END_OF_LINE = "];";
  protected static final String COORDINATE_COMMENT_LINE = "%% coordinate data";
  protected static final String COORDINATE_DATA_LINE = "%	id	lat lon";
  protected static final String COORDINATE_START_OF_LINE = "mpc.coordinates";
  protected static final String COORDINATE_END_OF_LINE = "];";
  protected static final String BATTERY_COMMENT_LINE = "%% battery data";
  protected static final String BATTERY_DATA_LINE = "%	id	capacity used cost maxmw minmw";
  protected static final String BATTERY_START_OF_LINE = "mpc.batteries";
  protected static final String BATTERY_END_OF_LINE = "];";
  protected static final String EXTRA_EXTRA_GENERATION_COMMENT_LINE = "%% extra generation data";
  protected static final String EXTRA_EXTRA_GENERATION_DATA_LINE = "%	fuel_type carbon capacity_factor";
  protected static final String EXTRA_EXTRA_GENERATION_START_OF_LINE = "mpc.extragen";
  protected static final String EXTRA_EXTRA_GENERATION_END_OF_LINE = "];";

  
 /**
	 * Constructor
	 */
	public MatPowerFile() {
	}

	@Override
	public ElectricPowerModel readModel(String filename) throws IOException {
		ElectricPowerModel state = parseFile(filename);
		return state;
	}
		
	@Override
	public void saveFile(String filename, ElectricPowerModel model)  throws IOException {
		MatPowerFileWriter writer = MatPowerFileWriterFactory.getInstance().getMatPowerFileWriter(model);		
		writer.saveFile(model, filename);
	}
	
	/**
	 * Parses the file and returns a model
	 * @throws IOException 
	 */
	private ElectricPowerModel parseFile(String filename) throws IOException {
		MatPowerModelImpl model = new MatPowerModelImpl();		
		readFile(filename);		
		ArrayList<Generator> orderedGenerators = new ArrayList<Generator>();
				
		MatPowerAreaFactory areaFactory = model.getControlAreaFactory();
		MatPowerZoneFactory zoneFactory = model.getZoneFactory();
    
		MatPowerBusFactory busFactory = model.getBusFactory();
		MatPowerGeneratorFactory generatorFactory = model.getGeneratorFactory();;
		MatPowerBatteryFactory batteryFactory = model.getBatteryFactory();
		MatPowerLineFactory lineFactory = model.getLineFactory();
		MatPowerTransformerFactory transformerFactory = model.getTransformerFactory();
		MatPowerLoadFactory loadFactory = model.getLoadFactory();
		MatPowerShuntFactory shuntFactory = model.getShuntCapacitorFactory();
		
		HashMap<Integer, Generator> generators = new HashMap<Integer,Generator>();
    Map<Integer,Point> busPoints = new HashMap<Integer,Point>();
    Map<Integer,String> buslines = new HashMap<Integer,String>();
    HashMap<Integer, Bus> buses = new HashMap<Integer, Bus>();
    HashMap<ControlArea, Integer> slackBuses = new HashMap<ControlArea, Integer>();
    HashMap<Integer, ControlArea> areas = new HashMap<Integer, ControlArea>();
    HashMap<Integer, Zone> zones = new HashMap<Integer, Zone>();
    HashMap<Asset, Integer> assetAreas = new HashMap<Asset, Integer>();
    HashMap<Asset, Integer> assetZones = new HashMap<Asset, Integer>();
    HashMap<Bus,Integer> busTypes = new HashMap<Bus,Integer>();
		
    // read the header
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      
      // get the case
      if (buffer.startsWith(CASE_START_OF_LINE)) {
        StringTokenizer tokenizer = new StringTokenizer(buffer," ");
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        String sCase = tokenizer.nextToken();
        model.setCase(sCase);
      }
      
      // get the version
      if (buffer.startsWith(VERSION_START_OF_LINE)) {
        StringTokenizer tokenizer = new StringTokenizer(buffer," ");
        tokenizer.nextToken();
        tokenizer.nextToken();
        String version = tokenizer.nextToken();
        model.setVersion(version);
      }

      // get the mva base
      if (buffer.startsWith(BASE_MVA_START_OF_LINE)) {
        StringTokenizer tokenizer = new StringTokenizer(buffer," ");
        tokenizer.nextToken();
        tokenizer.nextToken();
        String token = tokenizer.nextToken();
        if (token.endsWith(";")) {
          token = token.substring(0,token.length()-1);
        }
        double mvaBase = Double.parseDouble(token);
        model.setMVABase(mvaBase);
      }
    }
    
    // determine the coordinates
    boolean inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(COORDINATE_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(COORDINATE_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        if (tokenizer.countTokens() < 3) {
          tokenizer = new StringTokenizer(buffer, " ");
        }
        int id = Integer.parseInt(tokenizer.nextToken());
        Double lat = Double.parseDouble(tokenizer.nextToken());
        String temp = tokenizer.nextToken();
        if (temp.endsWith(";")) {
        	temp = temp.substring(0,temp.length()-1);
        }
        Double lon = Double.parseDouble(temp);
        busPoints.put(id, new PointImpl(lat,lon));        
      }
    }
    
		// create the buses
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(BUS_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      
       if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        int numTokens = tokenizer.countTokens();
        String token = tokenizer.nextToken(); // id
        int id = Integer.parseInt(token);        
        int type = Integer.parseInt(tokenizer.nextToken().trim()); // bus type
        tokenizer.nextToken(); // real load
        tokenizer.nextToken(); // reactive load
        tokenizer.nextToken(); // shunt conductance
        tokenizer.nextToken(); // shunt susceptance
        token = tokenizer.nextToken(); // area
        int area = Integer.parseInt(token.trim());
        tokenizer.nextToken(); // voltage magnitude
        tokenizer.nextToken(); // voltage angle
        if (numTokens > 13) {
          tokenizer.nextToken(); // name
        }
        tokenizer.nextToken(); // base kv
        token = tokenizer.nextToken(); //zone
        int zone = (int)Double.parseDouble(token);
        Bus bus = busFactory.createBus(buffer, busPoints.get(id));
        model.addBus(bus);
        buslines.put(id,buffer);
        buses.put(id,bus);
        assetAreas.put(bus,area);
        assetZones.put(bus,zone);
        busTypes.put(bus,type);
      }
    }
    
    // create the generators
    inBlock = false;
    Map<Integer,Integer> generatorAtBusCount = new HashMap<Integer,Integer>();
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line);
      if (buffer.startsWith(GENERATOR_END_OF_LINE)) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(GENERATOR_START_OF_LINE)) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        String token = tokenizer.nextToken();        
        int id = Integer.parseInt(token); 
        int area = assetAreas.get(buses.get(id));
        int zone = assetZones.get(buses.get(id));        
        int count = (generatorAtBusCount.get(id) == null) ? 0 : generatorAtBusCount.get(id); 

        Generator generator  = generatorFactory.createGenerator(buslines.get(id), buffer, count, busPoints.get(id), buses.get(id));
        model.addGenerator(generator, buses.get(id));      
        generators.put(id, generator);
        generatorAtBusCount.put(id, count+1);
        orderedGenerators.add(generator);
        assetAreas.put(generator,area);
        assetZones.put(generator,zone);        
      }
    }

    // create the loads
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(BUS_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        int numTokens = tokenizer.countTokens();
        String token = tokenizer.nextToken();        
        int id = Integer.parseInt(token);        
        tokenizer.nextToken();
        token = tokenizer.nextToken();
        double realLoad = Double.parseDouble(token);
        token = tokenizer.nextToken();
        double reactiveLoad = Double.parseDouble(token);
        tokenizer.nextToken(); // shunt conductance
        tokenizer.nextToken(); // shunt susceptance
        int area = Integer.parseInt(tokenizer.nextToken().trim());
        tokenizer.nextToken(); 
        tokenizer.nextToken(); 
        tokenizer.nextToken();
        if (numTokens > 13) {
          tokenizer.nextToken();
        }
        int zone = (int)Double.parseDouble(tokenizer.nextToken());

        if (realLoad == 0 && reactiveLoad == 0) {
          continue;
        }

        Load load  = loadFactory.createLoad(buffer, busPoints.get(id) ,buses.get(id));
        model.addLoad(load, buses.get(id));     
        assetAreas.put(load,area);
        assetZones.put(load,zone);
      }
    }

    // create the shunts
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(BUS_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        int numTokens = tokenizer.countTokens();
        String token = tokenizer.nextToken();
        int id = Integer.parseInt(token);        
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        token = tokenizer.nextToken();
        double realShunt = Double.parseDouble(token);
        token = tokenizer.nextToken();
        double reactiveShunt = Double.parseDouble(token);
        int area = Integer.parseInt(tokenizer.nextToken().trim());
        tokenizer.nextToken(); 
        tokenizer.nextToken(); 
        tokenizer.nextToken(); 
        if (numTokens > 13) {
          tokenizer.nextToken();
        }
        int zone = (int)Double.parseDouble(tokenizer.nextToken());
        
        if (realShunt == 0 && reactiveShunt == 0) {
          continue;
        }
        ShuntCapacitor capacitor  = shuntFactory.createShunt(buffer, busPoints.get(id));
        model.addShuntCapacitor(capacitor, buses.get(id));      
        assetAreas.put(capacitor, area);
        assetZones.put(capacitor, zone);
      }
    }
   
    // create the zones
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(BUS_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BUS_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        Zone zone  = zoneFactory.createZone(buffer);
        if (!model.getZones().contains(zone)) {
          model.addZone(zone);    
          zones.put(zone.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class), zone);
        }
      }
    }
    
    // create the lines and transformers
    inBlock = false;
    Map<Integer,Map<Integer,Integer>> lineCount = new HashMap<Integer,Map<Integer,Integer>>();
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(BRANCH_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BRANCH_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        int id1 = Integer.parseInt(tokenizer.nextToken());        
        int id2 = Integer.parseInt(tokenizer.nextToken().trim()); 
        Point point1 = busPoints.get(id1);
        Point point2 = busPoints.get(id2);
        
        ArrayList<Point> points = null;
        if (point1 != null && point2 != null) {
          points = new ArrayList<Point>();
          points.add(point1);
          points.add(point2);
        }        
        for (int i = 0; i < 6; ++i) {
        	tokenizer.nextToken();
        }
        double ratio = Double.parseDouble(tokenizer.nextToken());
        
        Integer circuit = new Integer((lineCount.get(Math.min(id1,id2)) == null || lineCount.get(Math.min(id1,id2)).get(Math.max(id1,id2)) == null) ? 0 : lineCount.get(Math.min(id1,id2)).get(Math.max(id1,id2)));
        if (ratio == 1 || ratio == 0) {
        	Line l  = lineFactory.createLine(buffer, buses.get(id1), buses.get(id2), circuit.toString(), points);
          model.addEdge(l, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));    
        }        
        else {
        	Transformer l  = transformerFactory.createTransformer(buffer, buses.get(id1), buses.get(id2), circuit.toString(), points);
          model.addEdge(l, model.getNode(buses.get(id1)), model.getNode(buses.get(id2)));
        }
        
        if (lineCount.get(Math.min(id1,id2)) == null) {
        	lineCount.put(Math.min(id1,id2),new HashMap<Integer,Integer>());
        }
        lineCount.get(Math.min(id1,id2)).put(Math.max(id1,id2),circuit+1);
        
      }
    }
        
    // create the areas
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(AREA_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(AREA_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
      	ControlArea area  = areaFactory.createArea(buffer);
        model.addArea(area);     
        areas.put(area.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class), area);
        
        StringTokenizer tokenizer = new StringTokenizer(buffer, "\t");
        tokenizer.nextToken();
        String temp = tokenizer.nextToken();
        int slackBus = Integer.parseInt(temp.substring(0,temp.length()-1));
        slackBuses.put(area,slackBus);
      }
    }
        
    // add the extra generator information
    inBlock = false;
    int generatorIdx = 0;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(EXTRA_GENERATION_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(EXTRA_GENERATION_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
      	Generator generator = orderedGenerators.get(generatorIdx);
      	StringTokenizer tokenizer = new StringTokenizer(buffer,"\t");
      	int costModel = Integer.parseInt(tokenizer.nextToken());
      	double startupCost = Double.parseDouble(tokenizer.nextToken());
      	double shutdownCost = Double.parseDouble(tokenizer.nextToken());
      	int points = Integer.parseInt(tokenizer.nextToken().trim());
      	if (costModel == 1) {
      		ArrayList<Point2D> coefficients = new ArrayList<Point2D>();      		
      		for (int i = 0; i < points; ++i) {
      			double point = Double.parseDouble(tokenizer.nextToken());
      			String temp = tokenizer.nextToken();
      			if (temp.endsWith(";")) {
      				temp = temp.substring(0,temp.length()-1);
      			}
      			double cost = Double.parseDouble(temp);
      			coefficients.add(new Point2D.Double(point,cost));
      		}
      		if (coefficients.size() > 1) {
      			generator.setAttribute(Generator.ECONOMIC_COST_KEY,PiecewiseLinearFunctionFactory.getInstance().createDefaultPiecewiseLinearFunction(coefficients));
      		}
      		else {
      			Point2D point = coefficients.get(0);
      			if (point.getX() != 0) {
      			  generator.setAttribute(Generator.ECONOMIC_COST_KEY, point.getY() / point.getX());
      			}
      			else {
      			  generator.setAttribute(Generator.ECONOMIC_COST_KEY, 0);
      			}
      		}
      	}
      	else {
      		ArrayList<Double> coefficients = new ArrayList<Double>();
      		for (int i = 0; i < points; ++i) {
      			String temp = tokenizer.nextToken();
      			if (temp.endsWith(";")) {
      				temp = temp.substring(0,temp.length()-1);
      			}
      			coefficients.add(Double.parseDouble(temp));
      		}
      		generator.setAttribute(Generator.ECONOMIC_COST_KEY, PolynomialFunctionFactory.getInstance().createDefaultPolynomialFunction(coefficients));
      	}
      	
        generator.setAttribute(Generator.STARTUP_COST_KEY, startupCost);
        generator.setAttribute(Generator.SHUTDOWN_COST_KEY, shutdownCost);     
        ++generatorIdx;
      }
    }
    
    // read in the battery information
    inBlock = false;
    for (int line = 0; line < fileLines.size(); line++) {
    	String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(BATTERY_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(BATTERY_START_OF_LINE )) {
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
    
    // add the extra extra generator information
    inBlock = false;
    generatorIdx = 0;
    for (int line = 0; line < fileLines.size(); line++) {
      String buffer = fileLines.get(line).trim();
      if (buffer.startsWith(EXTRA_EXTRA_GENERATION_END_OF_LINE )) {
        inBlock = false;
        continue;
      }
      if (buffer.startsWith(EXTRA_EXTRA_GENERATION_START_OF_LINE )) {
        inBlock = true;
        continue;
      }
      if (inBlock) {
      	Generator generator = orderedGenerators.get(generatorIdx);
      	StringTokenizer tokenizer = new StringTokenizer(buffer,"\t");
      	int ft = Integer.parseInt(tokenizer.nextToken());

      	double carbon = Double.parseDouble(tokenizer.nextToken());
      	double capacity_factor = 1;
      	if (tokenizer.hasMoreTokens()) {
      		String temp = tokenizer.nextToken();
      		if (temp.endsWith(";")) {
      			temp = temp.substring(0,temp.length()-1);
      		}
      		capacity_factor = Double.parseDouble(temp);
      	}
      	generator.setAttribute(Generator.CARBON_OUTPUT_KEY, carbon);
      	generator.setAttribute(Generator.CAPACITY_FACTOR_KEY, capacity_factor);
      	generator.setAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.getEnum(ft));
        ++generatorIdx;
      }
    }

    
    
    // set up the areas for the assets
    for (Asset asset : assetAreas.keySet()) {
      int a = assetAreas.get(asset);
      if (areas.get(a) == null) {
        ControlArea area  = areaFactory.createArea(a);
        model.addArea(area);     
        areas.put(area.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class), area);         
      }
            
      ControlArea area = areas.get(a);
      model.setControlArea(asset, area);
      
      if (asset instanceof Bus && slackBuses.get(area) == null) {
        if (busTypes.get(asset) == MatPowerIOConstants.SLACK_BUS_TYPE) {
          slackBuses.put(area, ((Bus)asset).getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class));
        }
      }
    }

    
    // set the slack buses for the areas
    for (ControlArea area : slackBuses.keySet()) {
      model.setSlackBus(area, buses.get(slackBuses.get(area)));
    }

    
     // set up the zones for the assets
    for (Asset asset : assetZones.keySet()) {
      model.setZone(asset, zones.get(assetZones.get(asset)));
    }
    
    // add some default value is there is no generator costs defined
      for (Generator generator : model.getGenerators()) {
        if (generator.getAttribute(Generator.ECONOMIC_COST_KEY) == null) {
          generator.setAttribute(Generator.ECONOMIC_COST_KEY, 1.0);
        }
        if (generator.getAttribute(Generator.STARTUP_COST_KEY) == null) {
          generator.setAttribute(Generator.STARTUP_COST_KEY, 0.0);
        }
        if (generator.getAttribute(Generator.SHUTDOWN_COST_KEY) == null) {
          generator.setAttribute(Generator.SHUTDOWN_COST_KEY, 0.0);
        }
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
    String initialFile = "data" + File.separatorChar + "ep" + File.separatorChar + "matpower" + File.separatorChar + "case96.m";
    MatPowerFile file = new MatPowerFile(); 
    ElectricPowerModel model = file.readModel(initialFile);     
    file.saveFile("temp.m", model);
  }
}