package gov.lanl.micot.infrastructure.ep.io.pfw;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWHeader;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModelConstants;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWSwitchedShuntControlModeEnum;
import gov.lanl.micot.util.math.NumberFormat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Formatter;
import java.util.Locale;

/**
 * General format for writing pfw files
 * 
 * @author Russell Bent
 */
public abstract class PFWFileWriterImpl implements PFWFileWriter {

  /**
   * Constructor
   */
  public PFWFileWriterImpl() {
    super();
  }
  
	/**
	 * Routine for writing buses
	 * 
	 * @param buses
	 */
	protected abstract void writeBuses(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing areas
	 * 
	 * @param buses
	 */
	protected abstract void writeAreas(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing zones
	 * 
	 * @param buses
	 */
	protected abstract void writeZones(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing generators
	 * 
	 * @param buses
	 */
	protected abstract void writeGenerators(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing interties
	 * 
	 * @param buses
	 */
	protected abstract void writeInterties(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing lines
	 * 
	 * @param buses
	 */
	protected abstract void writeLines(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing transformers
	 * 
	 * @param buses
	 */
	protected abstract void writeTransformers(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing loads
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeLoads(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing shunts
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeShunts(ElectricPowerModel model, StringBuffer buffer);
	
	/**
	 * Routine for writing switched shunts
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeSwitchedShunts(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Get the file header information
	 * @param model
	 * @return
	 */
	protected abstract PFWHeader getHeader(ElectricPowerModel model);
	
	/**
	 * Routine for writing coordinates
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeCoordinates(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing extra generator information
	 * @param model
	 * @param buffer
	 */
  protected abstract void writeExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer);
	
  /**
	 * Routine for writing battery information
	 * @param model
	 * @param buffer
	 */
  protected abstract void writeBatteryInfo(ElectricPowerModel model, StringBuffer buffer);
  
	@Override
	public void saveFile(ElectricPowerModel model, String filename) throws IOException {		
		FileOutputStream fileStream = new FileOutputStream(filename);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileStream));
		StringBuffer buffer = new StringBuffer();

		double minVoltage = 0;
		double maxVoltage = 2;
		for (Bus bus : model.getBuses()) {
		  minVoltage = Math.max(minVoltage, bus.getMinimumVoltagePU());
      maxVoltage = Math.min(maxVoltage, bus.getMaximumVoltagePU());
		}
		
		// output the header
		PFWHeader header = getHeader(model);
		header.setMVABase(model.getMVABase());
		header.setMinVoltage(minVoltage);
    header.setMaxVoltage(maxVoltage);
		buffer.append(header.toString());
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the buses
		buffer.append(PFWFile.BUS_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeBuses(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the generators
		buffer.append(PFWFile.GENERATOR_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeGenerators(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the loads
		buffer.append(PFWFile.LOAD_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeLoads(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the shunts
		buffer.append(PFWFile.SHUNT_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeShunts(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the switched shunts
		buffer.append(PFWFile.SWITCHED_SHUNT_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeSwitchedShunts(model,buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the lines
		buffer.append(PFWFile.LINE_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeLines(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the transformers
		buffer.append(PFWFile.TRANSFORMER_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeTransformers(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the zones
		buffer.append(PFWFile.ZONE_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeZones(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the areas
		buffer.append(PFWFile.AREA_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeAreas(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the interties
		buffer.append(PFWFile.TIE_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeInterties(model, buffer);
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the end of the "official" file
		buffer.append(PFWFile.LAST_LINE);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		
		// output extra data that we want to keep around
		// writes the coordinates
		buffer = new StringBuffer();
		buffer.append(PFWFile.COORDINATE_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeCoordinates(model, buffer);
		buffer.append(PFWFile.COORDINATE_FOOTER);
    buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());

    // writes the extra generator
    buffer = new StringBuffer();
    buffer.append(PFWFile.EXTRA_GENERATOR_HEADER_START + PFWFile.EXTRA_GENERATOR_HEADER_END);
    buffer.append(System.getProperty("line.separator"));
    writeExtraGeneratorInfo(model, buffer);
    buffer.append(PFWFile.EXTRA_GENERATOR_FOOTER_START + PFWFile.EXTRA_GENERATOR_FOOTER_END);
    buffer.append(System.getProperty("line.separator"));
    bufferedWriter.write(buffer.toString());

    // writes the battery information
    buffer = new StringBuffer();
    buffer.append(PFWFile.BATTERY_HEADER);
    buffer.append(System.getProperty("line.separator"));
    writeBatteryInfo(model, buffer);
    buffer.append(PFWFile.BATTERY_FOOTER);
    buffer.append(System.getProperty("line.separator"));
    bufferedWriter.write(buffer.toString());
    
		
		bufferedWriter.flush();
		bufferedWriter.close();
		fileStream.flush();
		fileStream.close();
	}

	/**
	 * Write a PFW line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getPFWBusLine(int id, String name, int area, int zone, Bus data) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%-8d", id);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%14s", name);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4d", area);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4d", zone);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getVoltagePU().doubleValue(), 8, 5), data.getVoltagePU().doubleValue());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.3f", data.getPhaseAngle());
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		if (data.getSystemVoltageKV() >= 1) {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%8.2f", data.getSystemVoltageKV());
			buffer.append(formatter.toString());
		   formatter.close();
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%8.3f", data.getSystemVoltageKV());
			buffer.append(formatter.toString());
		  formatter.close();
		}
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.4f", data.getVoltagePU().doubleValue());
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%1d", data.getStatus() ? 1 : 0);
		buffer.append(formatter.toString());
    formatter.close();
				
		return buffer.toString();
	}

	/**
	 * Creates a PFW for an area
	 */
	protected String getPFWAreaLine(ControlArea area, Bus slack) { 
	  int id = area.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  int slackBus = slack.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  String name = area.getAttribute(ControlArea.AREA_BUS_NAME_KEY, String.class);
	  double export = area.getAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, Double.class);
	  double tolerance = area.getAttribute(ControlArea.AREA_TOLERANCE_KEY, Double.class);
	  String areaCode = area.getAttribute(ControlArea.AREA_CODE_KEY, String.class);
    String areaName = area.getAttribute(ControlArea.AREA_NAME_KEY, String.class);
	  
	  StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%-4d", id);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%-8d", slackBus);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%14s", name);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.2f", export);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.5f", tolerance);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8s", areaCode);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%30s", areaName);
		buffer.append(formatter.toString());
    formatter.close();

		return buffer.toString();
	}

	/**
	 * Create pfw line for zones
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	protected String getPFWZoneLine(Zone zone) {
	  int id = zone.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class); 
	  String name = zone.getName();
	  
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4d", id);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%14s", name);
		buffer.append(formatter.toString());
    formatter.close();

		return buffer.toString();
	}

	
	private String getPFWGeneratorLine(int id, String name, int area, int zone, int remoteBus, double vt, double vb, double realGeneration,
			double reactiveGeneration, double desiredVoltage, double reactiveMax, double reactiveMin, boolean desiredStatus, double realMax, double realMin,
			int generatorType) {
		
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		buffer.append(NumberFormat.resizeAfter(new Integer(id).toString(), 8));
		buffer.append(",");

		buffer.append(NumberFormat.resizeAfter(name, 14));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(area).toString(), 4));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(zone).toString(), 4));
		buffer.append(",");

		buffer.append(generatorType);
		buffer.append(",");

		if (new Double(Double.NaN).equals(realGeneration)) {
			realGeneration = 0;
		}
		
		if (new Double(Double.NaN).equals(reactiveGeneration)) {
			reactiveGeneration = 0;
		}
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(realGeneration, 10, 5), realGeneration);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(reactiveGeneration, 10, 5), reactiveGeneration);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(desiredVoltage, 8, 4), desiredVoltage);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(reactiveMax, 8, 5), reactiveMax);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(reactiveMin, 8, 5), reactiveMin);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		buffer.append(NumberFormat.resizeBefore(new Integer(remoteBus).toString(), 8));
		buffer.append(",");

		buffer.append(desiredStatus ? 1 : 0);
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(realMax, 8, 5), realMax);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(realMin, 8, 5), realMin);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(vt, 8, 5), vt);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(vb, 8, 5), vb);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
    formatter.close();

		return buffer.toString();
	}
	
	/**
	 * Create pfw line output for batteries (assumes that some generation
	 * information has been filled in)
	 * @param data
	 * @return
	 */
	protected String getPFWGeneratorLine(Battery battery, Bus bus, ControlArea a, Zone z, Bus rb) {
	  int id = battery.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  String name = battery.getAttribute(Generator.NAME_KEY,String.class);
	  int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    int remoteBus = rb.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);    
    double vt = bus.getMaximumVoltagePU(); 
    double vb = bus.getMinimumVoltagePU(); 
		double realGeneration = battery.getRealGeneration().doubleValue();
		double reactiveGeneration = battery.getReactiveGeneration().doubleValue();
		double desiredVoltage = bus.getRemoteVoltagePU(); //battery.getAttribute(Generator.DESIRED_VOLTAGE_KEY,Double.class);
		double reactiveMax = battery.getReactiveGenerationMax();
		double reactiveMin = battery.getReactiveGenerationMin();
		boolean desiredStatus = battery.getStatus();
		double realMax = battery.getAvailableMaximumRealProduction().doubleValue();
		double realMin = battery.getAvailableMinimumRealProduction().doubleValue();
		int generatorType = battery.getAttribute(Generator.TYPE_KEY,GeneratorTypeEnum.class).getGeneratorType();

		return getPFWGeneratorLine(id, name, area, zone, remoteBus, vt, vb, realGeneration,
				reactiveGeneration, desiredVoltage, reactiveMax, reactiveMin, desiredStatus, realMax, realMin,
				generatorType);
	}
	
	/**
	 * Create pfw line output for generators
	 * 
	 * @param data
	 * @return
	 */
	protected String getPFWGeneratorLine(Generator generator, Bus bus, ControlArea a, Zone z, Bus rb) {
	  int id = generator.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  String name = generator.getAttribute(Generator.NAME_KEY,String.class);
    int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    int remoteBus = rb.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    double vt = bus.getMaximumVoltagePU(); 
    double vb = bus.getMinimumVoltagePU(); 
		double realGeneration = generator.getRealGeneration().doubleValue();
		double reactiveGeneration = generator.getReactiveGeneration().doubleValue();
		double desiredVoltage = bus.getRemoteVoltagePU(); 
    double reactiveMax = generator.computeReactiveGenerationMax().doubleValue(); // steady state model
		double reactiveMin = generator.getReactiveGenerationMin();
		boolean desiredStatus = generator.getStatus();
    double realMax = generator.computeRealGenerationMax().doubleValue(); // steady state model
		double realMin = generator.getRealGenerationMin();
		int generatorType = generator.getType().getGeneratorType();

		return getPFWGeneratorLine(id, name, area, zone, remoteBus, vt, vb, realGeneration,
				reactiveGeneration, desiredVoltage, reactiveMax, reactiveMin, desiredStatus, realMax, realMin,
				generatorType);		
	}

	/**
	 * Create pfw output of the intertie
	 * 
	 * @return
	 */
	protected String getPFWIntertieLine(Intertie intertie, Bus meterBus, Bus nonMeterBus, ControlArea mA, ControlArea nA) {
	  //int meteredBus = intertie.getAttribute(Intertie.FROM_NODE_INTEGER_ID_KEY, Integer.class);
	  //int nonMeteredBus = intertie.getAttribute(Intertie.TO_NODE_INTEGER_ID_KEY, Integer.class);
	  int meteredBus = meterBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  int nonMeteredBus = nonMeterBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int meteredArea = mA.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int nonMeteredArea = nA.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  String circuit = intertie.getAttribute(Intertie.CIRCUIT_KEY).toString();
    
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8d", meteredBus);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4d", meteredArea);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8d", nonMeteredBus);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4d", nonMeteredArea);
		buffer.append(formatter.toString());
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4s", circuit);
		buffer.append(formatter.toString());
    formatter.close();
		
		return buffer.toString();
	}

	/**
	 * Create a pfw line format
	 * 
	 * @param data
	 * @return
	 */
	protected String getPFWLineLine(Line data, Bus fromBus, Bus toBus, ControlArea a, Zone z) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

//		int from = data.getAttribute(Line.FROM_NODE_INTEGER_ID_KEY, Integer.class);
  //  int to = data.getAttribute(Line.TO_NODE_INTEGER_ID_KEY, Integer.class);
    int from = fromBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int to = toBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    String circuit  = data.getCircuit().toString();  
    int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    String branchRating = data.getAttribute(PFWModelConstants.PFW_LINE_BRANCH_RATING_DESGINATOR_KEY, String.class);
    int unknown = data.getAttribute(PFWModelConstants.PFW_LINE_UNKNOWN_KEY, Integer.class);
		
		buffer.append(NumberFormat.resizeBefore(new Integer(from).toString(), 8));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(to).toString(), 8));
		buffer.append(",");

		buffer.append("\"" + NumberFormat.resizeBefore(circuit, 2) +"\"");
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(area).toString(), 4));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(zone).toString(), 4));
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getResistance(), 10, 5), data.getResistance());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getReactance(), 10, 5), data.getReactance());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getLineCharging(), 10, 5), data.getLineCharging());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getCapacityRating().intValue()).toString(), 5));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getShortTermEmergencyCapacityRating().intValue()).toString(), 5));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getLongTermEmergencyCapacityRating().intValue()).toString(), 5));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getStatus() ? 1 : 0).toString(), 1));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(branchRating, 1));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(unknown).toString(), 2));

		return buffer.toString();
	}

	/**
	 * Output a pfw transformer
	 * 
	 * @return
	 */
	protected String getPFWTransformerLine(Transformer data, Bus fromBus, Bus toBus, ControlArea a, Zone z, Bus cb) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;
				
//	  int from = data.getAttribute(Transformer.FROM_NODE_INTEGER_ID_KEY, Integer.class);
  //  int to = data.getAttribute(Transformer.TO_NODE_INTEGER_ID_KEY, Integer.class);
    int from = fromBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int to = toBus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);

    String circuit = data.getCircuit().toString();
        
    int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);    
    String branchRating = data.getAttribute(PFWModelConstants.PFW_TRANSFORMER_BRANCH_RATING_DESGINATOR_KEY, String.class);
    TransformerTypeEnum type = data.getAttribute(Transformer.TYPE_KEY, TransformerTypeEnum.class);
    double tapRatio = data.getAttribute(Transformer.TAP_RATIO_KEY, Double.class);
    double tapAngle = data.getAttribute(Transformer.TAP_ANGLE_KEY, Double.class);
    double minTapAngle = data.getAttribute(Transformer.MIN_TAP_ANGLE_KEY, Double.class);
    double maxTapAngle = data.getAttribute(Transformer.MAX_TAP_ANGLE_KEY, Double.class);
    double stepSize = data.getAttribute(Transformer.STEP_SIZE_KEY, Double.class);
    double controlMin = data.getAttribute(Transformer.CONTROL_MIN_KEY, Double.class);
    double controlMax = data.getAttribute(Transformer.CONTROL_MAX_KEY, Double.class);
    int controlBus = cb == null ? 0 : cb.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    TransformerControlModeEnum controlSide = data.getAttribute(Transformer.CONTROL_SIDE_KEY, TransformerControlModeEnum.class);
    
		buffer.append(NumberFormat.resizeBefore(new Integer(from).toString(), 8));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(to).toString(), 8));
		buffer.append(",");

		buffer.append("\"" + NumberFormat.resizeAfter(circuit, 2) +"\"");
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(area).toString(), 4));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(zone).toString(), 4));
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getResistance(), 10, 5), data.getResistance());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getReactance(), 10, 5), data.getReactance());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getLineCharging(), 10, 5), data.getLineCharging());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getCapacityRating().intValue()).toString(), 5));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getShortTermEmergencyCapacityRating().intValue()).toString(), 5));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getLongTermEmergencyCapacityRating().intValue()).toString(), 5));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(data.getStatus() ? 1 : 0).toString(), 1));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(branchRating, 1));
		buffer.append(",");

		//buffer.append(PFWUtils.resizeBefore(new Integer(PFWFile.getTransformerTypeInt(type)).toString(), 2));
		buffer.append(NumberFormat.resizeBefore(new Integer(type.getTransformerType()).toString(), 2));
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(tapRatio, 8, 4), tapRatio);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		// tapAngle has some special case formatting
		if (tapAngle == 0) {
			buffer.append("     0.0,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%8.2f", tapAngle);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		  formatter.close();
		}

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(minTapAngle, 8, 5), minTapAngle);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(maxTapAngle, 8, 5), maxTapAngle);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(stepSize, 8, 5), stepSize);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(controlMin, 8, 5), controlMin);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(controlMax, 8, 5), controlMax);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		buffer.append(NumberFormat.resizeBefore(new Integer(controlBus).toString(), 8));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(controlSide.getControlMode()).toString(), 2));
		
		return buffer.toString();
	}

	/**
	 * Create pfw output
	 * 
	 * @param data
	 * @return
	 */
	protected String getPFWLoadLine(Load data, ControlArea a, Zone z) {
	  //int id = data.getAttribute(Load.INTEGER_ID_KEY, Integer.class);
	  int id = data.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
	  String name = data.getAttribute(Load.LOAD_NAME_KEY,String.class);
    int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);

		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		buffer.append(NumberFormat.resizeAfter(new Integer(id).toString(), 8));
		buffer.append(",");

		buffer.append(NumberFormat.resizeAfter(name, 14));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(area).toString(), 4));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(zone).toString(), 4));
		buffer.append(",");

		// T2000 "helps" by eliminating loads that are 0 from the model.  This can cause
		// problems if you later look for that load and its not in the model anymore.  This
		// forces 0 loads to be very very small and thus stay in the model.  A hack, but oh well. 
		// what can you do?
		double realLoad = data.getRealLoad().doubleValue();
		if (realLoad == 0 && data.getReactiveLoad().doubleValue() == 0) {
			realLoad = .001;
		}
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(realLoad, 10, 5), realLoad);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
		buffer.append(",");
    formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getReactiveLoad().doubleValue(), 10, 5), data.getReactiveLoad().doubleValue());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 10));
    formatter.close();
		
		return buffer.toString();
	}

	/**
	 * Create a pfw text line
	 * 
	 * @param state
	 * @return
	 */
	protected String getPFWShuntLine(ShuntCapacitor data, ControlArea a, Zone z) {
	  int id = data.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    String name = data.getAttribute(ShuntCapacitor.SHUNT_NAME_KEY, String.class);
    int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);

		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		buffer.append(NumberFormat.resizeAfter(new Integer(id).toString(), 8));
		buffer.append(",");
		buffer.append(NumberFormat.resizeAfter(name, 14));
		buffer.append(",");
		buffer.append(NumberFormat.resizeBefore(new Integer(area).toString(), 4));
		buffer.append(",");
		buffer.append(NumberFormat.resizeBefore(new Integer(zone).toString(), 4));
		buffer.append(",");

		// T2000 "helps" by eliminating shunts that are 0 from the model.  This can cause
		// problems if you later look for that load and its not in the model anymore.  This
		// forces 0 loads to be very very small and thus stay in the model.  A hack, but oh well. 
		// what can you do?
		double reactiveCompensation = data.getReactiveCompensation();
		if (reactiveCompensation == 0 && data.getRealCompensation() == 0) {
			reactiveCompensation = .00001;
		}
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getRealCompensation(), 8, 5), data.getRealCompensation());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(reactiveCompensation, 8, 5), reactiveCompensation);
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
    formatter.close();

		
		return buffer.toString();
	}

	/**
	 * Create the pfw text line representation of this switched shunt
	 * 
	 * @param data
	 * @return
	 */
	protected String getPFWSwitchedLine(ShuntCapacitorSwitch data, int id, PFWSwitchedShuntControlModeEnum controlMode, int remoteBus, int area, int zone, int normalStatus,
			Integer numSubsections1, Double subsection1Size, Integer numSubsections2, Double subsection2Size, Integer numSubsections3, Double subsection3Size,
			Integer numSubsections4, Double subsection4Size, Integer numSubsections5, Double subsection5Size, Integer numSubsections6, Double subsection6Size,
			Integer numSubsections7, Double subsection7Size, Integer numSubsections8, Double subsection8Size, Integer numSubsections9, Double subsection9Size,
			Integer numSubsections10, Double subsection10Size) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		buffer.append(NumberFormat.resizeBefore(new Integer(id).toString(), 8));
		buffer.append(",");

    buffer.append(controlMode.getControlMode());
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getScheduleHighVoltage(), 8, 5), data.getScheduleHighVoltage());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getScheduledLowVoltage(), 8, 5), data.getScheduledLowVoltage());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
		formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getDesiredVoltage(), 8, 5), data.getDesiredVoltage());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
		formatter.close();

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getRemoteDesiredVoltage(), 8, 5), data.getRemoteDesiredVoltage());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
		formatter.close();

		buffer.append(NumberFormat.resizeBefore(new Integer(remoteBus).toString(), 8));
		buffer.append(",");

		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getInitialSusceptance(), 8, 5), data.getInitialSusceptance());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();
		
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format(NumberFormat.createSignificantDigitFormat(data.getSusceptance(), 8, 5), data.getSusceptance());
		buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
		buffer.append(",");
    formatter.close();

		buffer.append(NumberFormat.resizeBefore(new Integer(area).toString(), 4));
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore(new Integer(zone).toString(), 4));
		buffer.append(",");

		buffer.append(data.getStatus() ? 1 : 0);
		buffer.append(",");

		buffer.append(normalStatus);
		buffer.append(",");

		buffer.append(NumberFormat.resizeBefore((numSubsections1 == null) ? " " : numSubsections1.toString(), 4));
		buffer.append(",");

		if (subsection1Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection1Size, 8, 5), subsection1Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections2 == null) ? " " : numSubsections2.toString(), 4));
		buffer.append(",");

		if (subsection2Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection2Size, 8, 5), subsection2Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections3 == null) ? " " : numSubsections3.toString(), 4));
		buffer.append(",");

		if (subsection3Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection3Size, 8, 5), subsection3Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections4 == null) ? " " : numSubsections4.toString(), 4));
		buffer.append(",");

		if (subsection4Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection4Size, 8, 5), subsection4Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections5 == null) ? " " : numSubsections5.toString(), 4));
		buffer.append(",");

		if (subsection5Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection5Size, 8, 5), subsection5Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections6 == null) ? " " : numSubsections6.toString(), 4));
		buffer.append(",");

		if (subsection6Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection6Size, 8, 5), subsection6Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections7 == null) ? " " : numSubsections7.toString(), 4));
		buffer.append(",");

		if (subsection7Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection7Size, 8, 5), subsection7Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections8 == null) ? " " : numSubsections8.toString(), 4));
		buffer.append(",");

		if (subsection8Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection8Size, 8, 5), subsection8Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections9 == null) ? " " : numSubsections9.toString(), 4));
		buffer.append(",");

		if (subsection9Size == null) {
			buffer.append("        ,");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection9Size, 8, 5), subsection9Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			buffer.append(",");
			formatter.close();
		}

		buffer.append(NumberFormat.resizeBefore((numSubsections10 == null) ? " " : numSubsections10.toString(), 4));
		buffer.append(",");

		if (subsection10Size == null) {
			buffer.append("        ");
		}
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format(NumberFormat.createSignificantDigitFormat(subsection10Size, 8, 5), subsection10Size);
			buffer.append(NumberFormat.resizeBefore(formatter.toString(), 8));
			formatter.close();
		}

		return buffer.toString();
	}

	/**
	 * Write a PFW line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getCoordinateLine(int id, Bus bus) {
		return id + "," + bus.getCoordinate().getY() + "," + bus.getCoordinate().getX(); 
	}
	
	/**
   * Write a PFW line for extra generator information
   * 
   * @param bus
   * @param data
   */
  protected String getExtraGeneratorInfoLine(int id, Generator generator) {
    GeneratorDefaults defaults = GeneratorDefaults.getInstance();
        
    double cost = generator.getAttribute(Generator.ECONOMIC_COST_KEY) != null && generator.getAttribute(Generator.ECONOMIC_COST_KEY) instanceof Number ? 
        generator.getAttribute(Generator.ECONOMIC_COST_KEY, Number.class).doubleValue() : defaults.convertToLinearCost(generator).doubleValue();
    FuelTypeEnum fuelType = generator.getAttribute(Generator.FUEL_TYPE_KEY) != null ?  
      generator.getAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.class) : FuelTypeEnum.UNKNOWN;
    double carbon = generator.getAttribute(Generator.CARBON_OUTPUT_KEY) != null ? 
        generator.getAttribute(Generator.CARBON_OUTPUT_KEY, Double.class) : defaults.calculateCarbon(generator);
    double capacityFactor = generator.getCapacityFactor().doubleValue(); // steady state model
     
    return id + "," + cost + "," + fuelType.getFuelType() + "," + carbon + "," + capacityFactor; 
  }
  
  /**
   * Write a PFW line for batteries
   * 
   * @param bus
   * @param data
   */
  protected String getBatteryLine(int id, Battery battery) {
    
    double capacity = battery.getEnergyCapacity().doubleValue();
    double used = battery.getUsedEnergyCapacity().doubleValue();
    double cost = battery.getAttribute(Battery.ECONOMIC_COST_KEY) == null ? 0 : battery.getAttribute(Battery.ECONOMIC_COST_KEY, Double.class);
    String name = battery.getAttribute(Generator.NAME_KEY, String.class);
    double max = battery.getRealGenerationMax(); 
    double min = battery.getRealGenerationMin();
      
    return id + "," + name + "," + capacity + "," + used + "," + cost + "," + max + "," + min; 
  }
  
	
	 /**
   * Write a PFW line for buses
   * @param bus
   * @param data
   */
  protected String getPFWBusLine(Bus bus, ControlArea a, Zone z) {
    int id = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    String name = bus.getAttribute(Bus.NAME_KEY, String.class);
    int area = a.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int zone = z.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);    
    return getPFWBusLine(id,name,area,zone,bus);
  }

  /**
   * Write a PFW line for shunts
   * @param bus
   * @param data
   */
  protected String getPFWSwitchedShuntLine(ShuntCapacitorSwitch shunt, ControlArea a, Zone z, Bus rb) {
    int legacyid = shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    PFWSwitchedShuntControlModeEnum controlMode = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_CONTROL_MODE_KEY, PFWSwitchedShuntControlModeEnum.class);
    int remoteBus = shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int area = shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    int zone = shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);    
    int normalStatus = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NORMAL_STATUS_KEY, Integer.class);
    int numSubsections1 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS1_KEY, Integer.class);
    int numSubsections2 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS2_KEY, Integer.class);
    int numSubsections3 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS3_KEY, Integer.class);
    int numSubsections4 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS4_KEY, Integer.class);
    int numSubsections5 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS5_KEY, Integer.class);
    int numSubsections6 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS6_KEY, Integer.class);
    int numSubsections7 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS7_KEY, Integer.class);
    int numSubsections8 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS8_KEY, Integer.class);
    int numSubsections9 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS9_KEY, Integer.class);
    int numSubsections10 = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS10_KEY, Integer.class);
    double subsection1Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION1_SIZE_KEY, Double.class);
    double subsection2Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION2_SIZE_KEY, Double.class);
    double subsection3Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION3_SIZE_KEY, Double.class);
    double subsection4Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION4_SIZE_KEY, Double.class);
    double subsection5Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION5_SIZE_KEY, Double.class);
    double subsection6Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION6_SIZE_KEY, Double.class);
    double subsection7Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION7_SIZE_KEY, Double.class);
    double subsection8Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION8_SIZE_KEY, Double.class);
    double subsection9Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION9_SIZE_KEY, Double.class);
    double subsection10Size = shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION10_SIZE_KEY, Double.class);
    
    return getPFWSwitchedLine(shunt, legacyid, controlMode, remoteBus, area, zone, 
        normalStatus, numSubsections1, subsection1Size, numSubsections2, subsection2Size, 
        numSubsections3, subsection3Size, numSubsections4, subsection4Size, numSubsections5, 
        subsection5Size, numSubsections6, subsection6Size, numSubsections7, subsection7Size, 
        numSubsections8, subsection8Size, numSubsections9, subsection9Size, numSubsections10, 
        subsection10Size);
  }

  
}
