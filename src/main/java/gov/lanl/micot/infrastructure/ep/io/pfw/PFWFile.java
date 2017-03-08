package gov.lanl.micot.infrastructure.ep.io.pfw;

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
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWAreaFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWBatteryFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWBusFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWGeneratorFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWHeader;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWIntertieFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWLineFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWLoadFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModelConstants;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModelImpl;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWShuntFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWSwitchedShuntFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWTransformerFactory;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWZoneFactory;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.FileParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

/**
 * Reads a PFW file format for energy transmission simulation, allows
 * modification of parameters of the PFW file and re-generation of a new PFW
 * file. PFW is a file format for T2000?
 * 
 * @author Matt Fair
 * 
 */
public class PFWFile extends FileParser implements ElectricPowerModelFile {

	private int										currentLine;
	private int                   currentAttempts;
	private int                   maxAttempts;
		
	// The current section of the PFW file is currently parsing
	private ArrayList<String>			sections;
	private Map<String, Boolean>	currentSection;

	public static final String	LAST_LINE	= "END OF DATA";
	protected static final String BUS_HEADER = "HEADER BUS, I:8:I, NAME:12:T, AREA:4:I, ZONE:4:I, VM:8:R, VA:8:R, BASKV:8:R, VS:8:R, STATUS:1:I";
	protected static final String GENERATOR_HEADER = "HEADER GENERATOR, I:8:I, NAME:12:T, AREA:4:I, ZONE:4:I, TYPE:1:T, PG:10:R, QG:10:R, VS:8:R, QT:8:R, QB:8:R, IREG:8:I, STAT:1:I, PMAX:8:R, PMIN:8:R, VT:8:R, VB:8:R";
	protected static final String LOAD_HEADER = "HEADER LOAD, I:8:I, NAME:12:T, AREA:4:I, ZONE:4:I, PL:10:R, QL:10:R";
	protected static final String SHUNT_HEADER = "HEADER SHUNT, I:8:I, NAME:12:T, AREA:4:I, ZONE:4:I, GS:8:R, BS:8:R";
	protected static final String SWITCHED_SHUNT_HEADER = "HEADER SWITCHED_SHUNT, I:8:I, MODSW:1:I, VSWHI:8:R, VSWLO:8:R, DV:8:R, REMOTEDV:8:R, SWREM:8:I, BINIT:8:R, SUSECPT:8:R, AREA:4:I, ZONE:4:I, ST:1:I, NST:1:I, N1:4:I, B1:8:R, N2:4:I, B2:8:R, N3:4:I, B3:8:R, N4:4:I, B4:8:R, N5:4:I, B5:8:R, N6:4:I, B6:8:R, N7:4:I, B7:8:R, N8:4:I, B8:8:R, N9:4:I, B9:8:R, N10:4:I, B10:8:R";
	protected static final String LINE_HEADER = "HEADER LINE, I:8:I, J:8:I, CKT:2:T, AREA:4:I, ZONE:4:I, R:10:R, X:10:R, B:10:R, RATEA:5:R, RATEB:5:R, RATEC:5:R, ST:1:T, RATE_UNIT:1:T";
	protected static final String TRANSFORMER_HEADER = "HEADER TRANSFORMER, I:8:I, J:8:I, CKT:2:T, AREA:4:I, ZONE:4:I, R:10:R, X:10:R, B:10:R, RATEA:5:R, RATEB:5:R, RATEC:5:R, ST:1:T, RATECHAR:1:T, TYPE:2:T, RATIO:8:R, ANGLE:8:R, MINTAPANG:8:R, MAXTAPANG:8:R, STEP:8:R, VMIN:8:R, VMAX:8:R, ICONT:8:I, SIDE:2:T";
	protected static final String ZONE_HEADER = "HEADER ZONE, I:4:I, ZONAM:12:T";
	protected static final String AREA_HEADER = "HEADER AREA, I:4:I, ISW:8:I, NAME:12:T, PDES:8:R, PTOL:8:R, ARCODE:6:T, ARNAM:30:T";
	protected static final String TIE_HEADER = "HEADER TIE_LINE, ENDBUS1:8:I, METEREDAREA:4:I, ENDBUS2:8:I, NONMETEREDAREA:4:I, CKT:2:T";
 
	/**
	 * Constructor
	 */
	public PFWFile() {
		currentAttempts = 0;
		maxAttempts = 10;
	}

	@Override
	public ElectricPowerModel readModel(String filename) throws IOException {
		// sections of the PFW file
		setupHeaders();

		// Parse the file
		currentAttempts = 0;
		ElectricPowerModel state = parseFile(filename);
		return state;
	}
	
	/**
	 * Sets up the headers
	 */
	private void setupHeaders() {
		sections = new ArrayList<String>();
		sections.add("HEADER");
		sections.add("BUS");
		sections.add("GENERATOR");
		sections.add("LOAD");
		sections.add("SHUNT");
		sections.add("SWITCHED_SHUNT");
		sections.add("LINE");
		sections.add("TRANSFORMER");
		sections.add("ZONE");
		sections.add("AREA");
		sections.add("TIE_LINE");
    sections.add("EXTRA_GENERATOR");

		currentSection = new HashMap<String, Boolean>();
		for (int index = 0; index < sections.size(); index++) {
			currentSection.put(sections.get(index), new Boolean(false));
		}

		// start with the header
		currentSection.put("HEADER", new Boolean(true));
	}
	
	@Override
	public void saveFile(String filename, ElectricPowerModel model)  throws IOException {
		PFWFileWriter writer = PFWFileWriterFactory.getInstance().getPFWFileWriter(model);		
		writer.saveFile(model, filename);
	}
	
	/**
	 * Parses the file and returns a model
	 * @throws IOException 
	 */
	private ElectricPowerModel parseFile(String filename) throws IOException {
		PFWModelImpl model = new PFWModelImpl();
		
		Vector<String> headerList = new Vector<String>();
		readFile(filename);		
		
		PFWAreaFactory areaFactory = model.getControlAreaFactory();
		PFWZoneFactory zoneFactory = model.getZoneFactory();
		PFWBusFactory busFactory = model.getBusFactory();
		PFWGeneratorFactory generatorFactory = model.getGeneratorFactory();
		PFWBatteryFactory batteryFactory = model.getBatteryFactory();
		PFWIntertieFactory intertieFactory = model.getIntertieFactory();
		PFWLineFactory lineFactory = model.getLineFactory();
		PFWTransformerFactory transformerFactory = model.getTransformerFactory();
		PFWLoadFactory loadFactory = model.getLoadFactory();
		PFWShuntFactory shuntFactory = model.getShuntCapacitorFactory();
		PFWSwitchedShuntFactory switchedShuntFactory = model.getShuntCapacitorSwitchFactory();

		HashMap<Integer, Generator> generators = new HashMap<Integer,Generator>();
		HashMap<Integer, HashMap<String,Generator>> generatorsByName = new HashMap<Integer, HashMap<String,Generator>>();
    HashMap<Integer, Bus> buses = new HashMap<Integer, Bus>();
    HashMap<ControlArea, Integer> slackBuses = new HashMap<ControlArea, Integer>();
    HashMap<Asset, Integer> assetAreas = new HashMap<Asset, Integer>();
    HashMap<Asset, Integer> assetZones = new HashMap<Asset, Integer>();
    HashMap<Asset, Integer> controlBuses = new HashMap<Asset, Integer>();
    HashMap<Integer, ControlArea> areas = new HashMap<Integer, ControlArea>();
    HashMap<Integer, Zone> zones = new HashMap<Integer, Zone>();
    HashMap<Intertie, Integer> meteredAreas = new HashMap<Intertie, Integer>();
    HashMap<Intertie, Integer> nonMeteredAreas = new HashMap<Intertie, Integer>();
		
		try {
			
			Map<Integer,Point> busPoints = new HashMap<Integer,Point>();
			
			// read in coordinates (if they exist) first
			boolean inCoordinate = false;
			for (int line = 0; line < fileLines.size(); line++) {
				if (fileLines.get(line).trim().equals(COORDINATE_HEADER)) {
					inCoordinate = true;
				}
				else if (fileLines.get(line).trim().equals(COORDINATE_FOOTER)) {
					inCoordinate = false;
				}
				else if (inCoordinate) {
					String buffer = fileLines.get(line);
					StringTokenizer tokenizer = new StringTokenizer(buffer,",");
					int id = Integer.parseInt(tokenizer.nextToken());
					double lat = Double.parseDouble(tokenizer.nextToken());
					double lon = Double.parseDouble(tokenizer.nextToken());
					busPoints.put(id,new PointImpl(lon,lat));		
				}
			}
									
			// now read in the other information
			for (int line = 0; line < fileLines.size(); line++) {
				currentLine = line;
				int currentSelectionIndex = getCurrentSection();

				if (fileLines.get(line).trim().equals(LAST_LINE)) {
					break;
				}

				if (sections.get(currentSelectionIndex).equals("HEADER")) {
				  headerList.add(fileLines.get(line));
				}
				else if (sections.get(currentSelectionIndex).equals("BUS")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line),",");
				  	int id = Integer.parseInt(tokenizer.nextToken().trim());
	          tokenizer.nextToken();
	          int area = Integer.parseInt(tokenizer.nextToken().trim());
	          int zone = Integer.parseInt(tokenizer.nextToken().trim());
				  	
						Bus bus = busFactory.createBus(fileLines.get(line), busPoints.get(id));
						model.addBus(bus);
            buses.put(id, bus);
            assetAreas.put(bus, area);
            assetZones.put(bus, zone);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("GENERATOR")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line),",");
				  	int id = Integer.parseInt(tokenizer.nextToken().trim());
	          tokenizer.nextToken();
	          int area = Integer.parseInt(tokenizer.nextToken().trim());
	          int zone = Integer.parseInt(tokenizer.nextToken().trim());
	          tokenizer.nextToken();
	          tokenizer.nextToken();
	          tokenizer.nextToken();
	          tokenizer.nextToken();
	          tokenizer.nextToken();
	          tokenizer.nextToken();
	          int remoteBus = Integer.parseInt(tokenizer.nextToken().trim());
				  	
						Generator gen = generatorFactory.createGenerator(fileLines.get(line), busPoints.get(id), buses.get(id));
						model.addGenerator(gen, buses.get(id));
						generators.put(gen.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class), gen);
						if (generatorsByName.get(gen.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class)) == null) {
							generatorsByName.put(gen.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class), new HashMap<String,Generator>());
						}
						generatorsByName.get(gen.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class)).put(gen.getAttribute(Generator.NAME_KEY,String.class),gen);
            assetAreas.put(gen, area);
            assetZones.put(gen, zone);
            controlBuses.put(gen, remoteBus);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("LOAD")) {					
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line),",");
				  	int id = Integer.parseInt(tokenizer.nextToken().trim());
	          tokenizer.nextToken();
	          int area = Integer.parseInt(tokenizer.nextToken().trim());
	          int zone = Integer.parseInt(tokenizer.nextToken().trim());

						Load load = loadFactory.createLoad(fileLines.get(line), busPoints.get(id), buses.get(id));
						model.addLoad(load, buses.get(id));
						assetAreas.put(load, area);
            assetZones.put(load, zone);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("SHUNT")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line),",");
				  	int id = Integer.parseInt(tokenizer.nextToken().trim());
	          tokenizer.nextToken();
	          int area = Integer.parseInt(tokenizer.nextToken().trim());
	          int zone = Integer.parseInt(tokenizer.nextToken().trim());

						ShuntCapacitor shunt = shuntFactory.createShunt(fileLines.get(line), busPoints.get(id));
						model.addShuntCapacitor(shunt, buses.get(id));
            assetAreas.put(shunt, area);
            assetZones.put(shunt, zone);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("SWITCHED_SHUNT")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line),",");
				  	int id = Integer.parseInt(tokenizer.nextToken().trim());
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            int remoteBus = Integer.parseInt(tokenizer.nextToken().trim());
            tokenizer.nextToken();
            tokenizer.nextToken();
            int area = Integer.parseInt(tokenizer.nextToken().trim());
            int zone = Integer.parseInt(tokenizer.nextToken().trim());
				  	
						ShuntCapacitorSwitch shunt = switchedShuntFactory.createSwitchedShunt(fileLines.get(line), busPoints.get(id));
						model.addShuntCapacitorSwitch(shunt, buses.get(id));
            assetAreas.put(shunt, area);
            assetZones.put(shunt, zone);
            controlBuses.put(shunt,remoteBus);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("LINE")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line), ",");
						int from = Integer.parseInt(tokenizer.nextToken().trim());
						int to = Integer.parseInt(tokenizer.nextToken().trim());
            tokenizer.nextToken();
            int area = Integer.parseInt(tokenizer.nextToken().trim());
            int zone = Integer.parseInt(tokenizer.nextToken().trim());
												
						Vector<Point> points = new Vector<Point>();
	          points.add(buses.get(from).getCoordinate());
	          points.add(buses.get(to).getCoordinate());
						Line l = lineFactory.createLine(fileLines.get(line), buses.get(from), buses.get(to), points);						
            model.addEdge(l, model.getNode(buses.get(from)), model.getNode(buses.get(to)));   
            assetAreas.put(l,area);
            assetZones.put(l, zone);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("TRANSFORMER")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
						StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line), ",");
						int from = Integer.parseInt(tokenizer.nextToken().trim());
						int to = Integer.parseInt(tokenizer.nextToken().trim());
            tokenizer.nextToken();
            int area = Integer.parseInt(tokenizer.nextToken().trim());
            int zone = Integer.parseInt(tokenizer.nextToken().trim());
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            int controlBus = Integer.parseInt(tokenizer.nextToken().trim());
						
						Vector<Point> points = new Vector<Point>();
            points.add(buses.get(from).getCoordinate());
            points.add(buses.get(to).getCoordinate());           

						Transformer transformer = transformerFactory.createTransformer(fileLines.get(line), buses.get(from), buses.get(to), points);
            model.addEdge(transformer,model.getNode(buses.get(from)), model.getNode(buses.get(to)));
            assetAreas.put(transformer, area);
            assetZones.put(transformer, zone);            
            controlBuses.put(transformer, controlBus);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("ZONE")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
	          Zone zone = zoneFactory.createZone(fileLines.get(line));
	          model.addZone(zone);
	          zones.put(zone.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class),zone);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("AREA")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
	          StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line),",");
	          tokenizer.nextToken().trim();
	          int slackBus = Integer.parseInt(tokenizer.nextToken().trim());

						ControlArea area = areaFactory.createArea(fileLines.get(line));
						model.addArea(area);
            slackBuses.put(area,slackBus);
            areas.put(area.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class),area);
					}
				}
				else if (sections.get(currentSelectionIndex).equals("TIE_LINE")) {
					if (!fileLines.get(line).startsWith("HEADER")) {
					  StringTokenizer tokenizer = new StringTokenizer(fileLines.get(line), ",");
            int from = Integer.parseInt(tokenizer.nextToken().trim());
            int meteredArea = Integer.parseInt(tokenizer.nextToken().trim());
            int to = Integer.parseInt(tokenizer.nextToken().trim());
            int nonMeteredArea = Integer.parseInt(tokenizer.nextToken().trim());
            
						Vector<Point> points = new Vector<Point>();
            points.add(buses.get(from).getCoordinate());
            points.add(buses.get(to).getCoordinate());           

            Intertie intertie = intertieFactory.createIntertie(fileLines.get(line), buses.get(from), buses.get(to), points);
						model.addEdge(intertie,model.getNode(buses.get(from)), model.getNode(buses.get(to)));
            meteredAreas.put(intertie, meteredArea);
            nonMeteredAreas.put(intertie, nonMeteredArea);
					}
				}
				else {
					System.err.println("Section error: unknown section " + currentSelectionIndex);
				}
			}
						
	    // read in the extra generator information
	    boolean inExtraGeneration = false;
	    for (int line = 0; line < fileLines.size(); line++) {
	      if (fileLines.get(line).trim().startsWith(EXTRA_GENERATOR_HEADER_START)) {
	        inExtraGeneration = true;
	      }
	      else if (fileLines.get(line).trim().startsWith(EXTRA_GENERATOR_FOOTER_START)) {
	        inExtraGeneration = false;
	      }
	      else if (inExtraGeneration) {
	        String buffer = fileLines.get(line);
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
	      }
	    }


	    // read in the battery information
	    boolean inBattery = false;
	    for (int line = 0; line < fileLines.size(); line++) {
	      if (fileLines.get(line).trim().equals(BATTERY_HEADER)) {
	        inBattery = true;
	      }
	      else if (fileLines.get(line).trim().equals(BATTERY_FOOTER)) {
	        inBattery = false;
	      }
	      else if (inBattery) {
	        String buffer = fileLines.get(line);
	        StringTokenizer tokenizer = new StringTokenizer(buffer,",");
	        int id = Integer.parseInt(tokenizer.nextToken());
	        String name = tokenizer.nextToken();
	        
	        Generator generator = generatorsByName.get(id).get(name);
	        Battery battery = batteryFactory.createBattery(fileLines.get(line), generator, buses.get(id));
					model.addBattery(battery, buses.get(id));
					assetAreas.put(battery, assetAreas.get(generator));
					assetZones.put(battery, assetZones.get(generator));
					controlBuses.put(battery, controlBuses.get(generator));
					
					if (generator != null) {
						model.removeGenerator(generator);						
						assetAreas.remove(generator);
						assetZones.remove(generator);
						controlBuses.remove(generator);
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
        if (buses.get(controlBuses.get(asset)) == null) {
          System.err.println("Error: Trying to associated a null control bus " + asset + " " + controlBuses.get(asset));
        }
        
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

	    
			
		}
		// sometimes we try to read the file as PFlow is writing it...
		catch (NoSuchElementException e) {
			e.printStackTrace();
			++currentAttempts;
			if (currentAttempts < maxAttempts) {
				System.err.println("Error reading file " + filename + ". Likely still be written by T2000. Trying again");
				setupHeaders();
				return parseFile(filename/*, objs*/);
			}
			else {
				throw new IOException("Error: Failed to read file too many times");
			}
		}
		catch (NumberFormatException e) {
			++currentAttempts;
			if (currentAttempts < maxAttempts) {
			  System.err.println("Error reading file " + filename + ". Likely still be written by T2000. Trying again");
				setupHeaders();
				return parseFile(filename);
			}
			else {
				e.printStackTrace();
				throw new IOException("Error: Failed to read file too many times");
			}
		}
		
		if (fileLines.size() == 0 || !fileLines.get(currentLine).trim().equals(LAST_LINE)) {
			++currentAttempts;
			if (currentAttempts < maxAttempts) {
			  System.err.println("Error reading file " + filename + ". Likely still be written by T2000. Trying again");
				setupHeaders();
				return parseFile(filename);
			}
			else {
				throw new IOException("Error reading pflow file");
			}
		}
		
    model.setHeader(new PFWHeader(headerList));
		return model;
	}

	/**
	 * Gets the section index value.
	 * 
	 * @see sections
	 * @return section index value
	 */
	private int getCurrentSection() {
		int currentIndex = 0;
		for (int index = 0; index < sections.size(); index++) {
			if (isSection(sections.get(index)))
				currentIndex = index;
		}
		return currentIndex;
	}

	/**
	 * Checks to see if the the PTI file is entering into or leaving a section of
	 * the PTI file and returning if it is in that specific section.
	 * 
	 * @param name
	 * @return true if it is in the name section, false otherwise.
	 * @see currentSection
	 */
	private Boolean isSection(String name) {
		if (fileLines.get(currentLine).trim().startsWith("HEADER " + name + ",")) {
			// turn off old section
			for (int i = 0; i < currentSection.size(); i++)
				currentSection.put(sections.get(i), new Boolean(false));

			// turn on new section
			currentSection.put(name, new Boolean(true));
		}

		return currentSection.get(name);
	}

  @Override
  public void saveFile(String filename, Model model) throws IOException {
    saveFile(filename,(ElectricPowerModel)model);
  }

  //@Override
  //public ElectricPowerModel readModel(String filename) throws IOException {
    //return readModel(filename,new Vector<ObjectiveFunction>(), new Vector<ObjectiveFunctionFactory<ElectricPowerModel>>());
  //}

}
