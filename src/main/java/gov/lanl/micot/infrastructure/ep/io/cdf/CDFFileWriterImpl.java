package gov.lanl.micot.infrastructure.ep.io.cdf;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.TransformerControlModeEnum;
import gov.lanl.micot.infrastructure.ep.model.TransformerTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFHeader;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelConstants;
import gov.lanl.micot.util.math.PolynomialFunction;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Formatter;
import java.util.Locale;

/**
 * General format for writing cdf files
 * 
 * @author Russell Bent
 */
public abstract class CDFFileWriterImpl implements CDFFileWriter {

	/**
	 * Constructor
	 */
	public CDFFileWriterImpl() {
		super();
	}

	/**
	 * Routine for writing buses
	 * 
	 * @param buses
	 */
	protected abstract void writeBuses(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Routine for writing areas
	 * 
	 * @param buses
	 */
	protected abstract void writeAreas(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Routine for writing zones
	 * 
	 * @param buses
	 */
	protected abstract void writeZones(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Routine for writing interties
	 * 
	 * @param buses
	 */
	protected abstract void writeInterties(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Routine for writing lines with flows
	 * 
	 * @param buses
	 */
	protected abstract void writeFlowLines(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Get the file header information
	 * 
	 * @param model
	 * @return
	 */
	protected abstract CDFHeader getHeader(ElectricPowerModel model);

	/**
	 * Routine for writing coordinates
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeCoordinates(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Routine for writing extra generator information
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeExtraGeneratorInfo(ElectricPowerModel model,
			StringBuffer buffer);

	/**
	 * Routine for writing battery information
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeBatteryInfo(ElectricPowerModel model,
			StringBuffer buffer);

	@Override
	public void saveFile(ElectricPowerModel model, String filename)
			throws IOException {
		FileOutputStream fileStream = new FileOutputStream(filename);
		BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(fileStream));
		StringBuffer buffer = new StringBuffer();

		// output the header
		CDFHeader header = getHeader(model);
		buffer.append(header.toString());
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the buses
		buffer.append(CDFFile.BUS_HEADER + "                            "
				+ model.getNodes().size() + " ITEMS");
		buffer.append(System.getProperty("line.separator"));
		writeBuses(model, buffer);
		buffer.append(CDFFile.BUS_FOOTER);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the lines
		buffer.append(CDFFile.LINE_HEADER + "                         "
				+ model.getFlowConnections().size() + " ITEMS");
		buffer.append(System.getProperty("line.separator"));
		writeFlowLines(model, buffer);
		buffer.append(CDFFile.LINE_FOOTER);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the zones
		buffer.append(CDFFile.ZONE_HEADER + "                     "
				+ model.getZones().size() + " ITEMS");
		buffer.append(System.getProperty("line.separator"));
		writeZones(model, buffer);
		buffer.append(CDFFile.ZONE_FOOTER);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the areas
		buffer.append(CDFFile.AREA_HEADER + "                 "
				+ model.getControlAreas().size() + " ITEMS");
		buffer.append(System.getProperty("line.separator"));
		writeAreas(model, buffer);
		buffer.append(CDFFile.AREA_FOOTER);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the interties
		buffer.append(CDFFile.TIE_HEADER + "                     "
				+ model.getInterties().size() + " ITEMS");
		buffer.append(System.getProperty("line.separator"));
		writeInterties(model, buffer);
		buffer.append(CDFFile.TIE_FOOTER);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the end of the "official" file
		buffer.append(CDFFile.LAST_LINE);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());

		// output extra data that we want to keep around
		// writes the coordinates
		buffer = new StringBuffer();
		buffer.append(CDFFile.COORDINATE_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeCoordinates(model, buffer);
		buffer.append(CDFFile.COORDINATE_FOOTER);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());

		// writes the extra generator
		buffer = new StringBuffer();
		buffer.append(CDFFile.EXTRA_GENERATOR_HEADER_START
				+ CDFFile.EXTRA_GENERATOR_HEADER_END);
		buffer.append(System.getProperty("line.separator"));
		writeExtraGeneratorInfo(model, buffer);
		buffer.append(CDFFile.EXTRA_GENERATOR_FOOTER_START
				+ CDFFile.EXTRA_GENERATOR_FOOTER_END);
		buffer.append(System.getProperty("line.separator"));
		bufferedWriter.write(buffer.toString());

		// writes the battery information
		buffer = new StringBuffer();
		buffer.append(CDFFile.BATTERY_HEADER);
		buffer.append(System.getProperty("line.separator"));
		writeBatteryInfo(model, buffer);
		buffer.append(CDFFile.BATTERY_FOOTER);
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
	protected String getCDFBusLine(int id, String name, int area, int zone, Bus bus, Generator generator, Load load, ShuntCapacitor capacitor, Bus rb) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		// write the id
		String temp = new Integer(id).toString();
		while (temp.length() < 4) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the name
		temp = " " + name;
		while (temp.length() < 12) {
			temp += " ";
		}
		buffer.append(temp);

		// write the area
		temp = new Integer(area).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the zone
		temp = new Integer(zone).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the type
		int type = (generator == null) ? 0 : generator.getType()
				.getGeneratorType();
		temp = new Integer(type).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the voltage
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%4.3f", bus.getVoltagePU());
		buffer.append(" " + formatter.toString());
		formatter.close();

		// write the angle
		if (bus.getPhaseAngle().doubleValue() == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.2f", bus.getPhaseAngle());
			temp = formatter.toString();
			formatter.close();
		}
		while (temp.length() < 7) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the real load
		double mwLoad = (load == null) ? 0 : load.getDesiredRealLoad()
				.doubleValue();
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", mwLoad);
		temp = formatter.toString();
		while (temp.length() < 9) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write the reactive load
		double mVarLoad = (load == null) ? 0 : load.getDesiredReactiveLoad()
				.doubleValue();
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", mVarLoad);
		temp = formatter.toString();
		while (temp.length() < 9) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write the real generation
		double mwGen = (generator == null) ? 0 : generator.getRealGeneration().doubleValue();
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", mwGen);
		temp = formatter.toString();
		while (temp.length() < 9) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write the reactive generation
		double mVarGen = (generator == null) ? 0 : generator.getReactiveGeneration().doubleValue();
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", mVarGen);
		temp = formatter.toString();
		while (temp.length() < 8) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write the base KV
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", bus.getSystemVoltageKV());
		temp = formatter.toString();
		while (temp.length() < 8) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write the base KV
		if (bus.getRemoteVoltagePU() == null || bus.getRemoteVoltagePU() == 0) {
			temp = "0.0  ";
		} 
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", bus.getRemoteVoltagePU());
			temp = formatter.toString();
			formatter.close();
		}
		while (temp.length() < 7) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write maximum MVAR or voltage limit
		double limit = 0;
		if (type == 1) {
			limit = bus.getMaximumVoltagePU();//generator.getMaximumVoltage();
		} 
		else if (type == 2) {
			limit = generator.computeReactiveGenerationMax().doubleValue(); // steady state model
		}
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", limit);
		temp = formatter.toString();
		while (temp.length() < 8) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write minimum MVAR or voltage limit
		limit = 0;
		if (type == 1) {
			limit = bus.getMinimumVoltagePU(); //generator.getMinimumVoltage();
		} 
		else if (type == 2) {
			limit = generator.getReactiveGenerationMin();
		}
		sb = new StringBuilder();
		formatter = new Formatter(sb, Locale.US);
		formatter.format("%8.1f", limit);
		temp = formatter.toString();
		while (temp.length() < 8) {
			temp = " " + temp;
		}
		buffer.append(temp);
		formatter.close();

		// write the real compensation
		double shunt = (capacitor == null) ? 0 : capacitor
				.getRealCompensation();
		if (shunt == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.2f", shunt);
			temp = formatter.toString();
			formatter.close();
		}
		while (temp.length() < 6) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the reactive compensation
		shunt = (capacitor == null) ? 0 : capacitor.getReactiveCompensation();
		if (shunt == 0) {
			temp = "0.0";
			while (temp.length() < 7) {
				temp = " " + temp;
			}
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.3f", shunt);
			temp = formatter.toString();
			while (temp.length() < 8) {
				temp = " " + temp;
			}
			formatter.close();
		}
		buffer.append(temp);

		double length = 9;
		if (shunt != 0) {
			length = 8;
		}

		//int remoteBus = generator == null ? 0 : rb.getAttribute(Bus.INTEGER_ID_KEY, Integer.class);
    int remoteBus = generator == null ? 0 : rb.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		temp = new Integer(remoteBus).toString();
		while (temp.length() < length) {
			temp = " " + temp;
		}
		buffer.append(temp);

		return buffer.toString();
	}

	/**
	 * Creates a PFW for an area
	 */
	protected String getCDFAreaLine(ControlArea area, Bus slack) {
    int id = area.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    int slackBus = slack.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		String name = area.getAttribute(ControlArea.AREA_BUS_NAME_KEY,String.class);
		double export = area.getAttribute(ControlArea.AREA_SCHEDULED_EXPORT_KEY, Double.class);
		double tolerance = area.getAttribute(ControlArea.AREA_TOLERANCE_KEY,Double.class);
		String areaCode = area.getAttribute(ControlArea.AREA_CODE_KEY,String.class);
		String areaName = area.getAttribute(ControlArea.AREA_NAME_KEY,String.class);

		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		// write the area
		String temp = new Integer(id).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the slack bus
		temp = new Integer(slackBus).toString();
		while (temp.length() < 4) {
			temp = " " + temp;
		}
		buffer.append(temp);

		temp = name;
		buffer.append(" " + temp);

		// write the export
		if (export == 0) {
			temp = "0.0";
		} 
		else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.5f", export);
			temp = formatter.toString();
		    formatter.close();
		}
		while (temp.length() < 7) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the tolerance
		if (tolerance == 0) {
			temp = "10000.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.2f", tolerance);
			temp = formatter.toString();
		}
		while (temp.length() < 8) {
			temp = " " + temp;
		}
		buffer.append(temp);

		temp = areaCode;
		buffer.append("  " + temp);

		temp = areaName;
		buffer.append("  " + temp);

		return buffer.toString();
	}

	/**
	 * Create pfw line for zones
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	protected String getCDFZoneLine(Zone zone) {
		int id = zone.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class); 
		String name = zone.getName();

		StringBuffer buffer = new StringBuffer();

		// write the id
		String temp = new Integer(id).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the name
		temp = " " + name;
		buffer.append(temp);

		return buffer.toString();
	}

	/**
	 * Create pfw output of the intertie
	 * 
	 * @return
	 */
	protected String getCDFIntertieLine(Intertie intertie, Bus meterBus, Bus nonMeterBus, ControlArea mA, ControlArea nA) {
	  //int meteredBus = intertie.getAttribute(Intertie.FROM_NODE_INTEGER_ID_KEY, Integer.class);
	  //int nonMeteredBus = intertie.getAttribute(Intertie.TO_NODE_INTEGER_ID_KEY, Integer.class);
		int meteredBus = meterBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		int nonMeteredBus = nonMeterBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		//int meteredArea = mA.getAttribute(ControlArea.INTEGER_ID_KEY, Integer.class);
	  //int nonMeteredArea = nA.getAttribute(ControlArea.INTEGER_ID_KEY, Integer.class);
    int meteredArea = mA.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    int nonMeteredArea = nA.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
	  
		String circuit = intertie.getAttribute(Intertie.CIRCUIT_KEY).toString();
		StringBuffer buffer = new StringBuffer();

		String temp = new Integer(meteredBus).toString();
		buffer.append(temp + " ");

		temp = new Integer(meteredArea).toString();
		buffer.append(temp + " ");

		temp = new Integer(nonMeteredBus).toString();
		buffer.append(temp + " ");

		temp = new Integer(nonMeteredArea).toString();
		buffer.append(temp + " ");

		temp = circuit;
		buffer.append(temp);

		return buffer.toString();
	}

	/**
	 * Create a pfw line format
	 * 
	 * @param data
	 * @return
	 */
	protected String getCDFLineLine(Line data, Bus fromBus, Bus toBus, ControlArea a, Zone z) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		//int from = data.getAttribute(Line.FROM_NODE_INTEGER_ID_KEY,Integer.class);
		//int to = data.getAttribute(Line.TO_NODE_INTEGER_ID_KEY, Integer.class);
    int from = fromBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,Integer.class);
    int to = toBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		
		String circuit = data.getCircuit().toString();
  	int area = a.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    int zone = z.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		int type = 0;
		double resistance = data.getResistance();
		double reactance = data.getReactance();
		double lineCharging = data.getLineCharging();
		int rating1 = data.getCapacityRating().intValue();
		int rating2 = data.getShortTermEmergencyCapacityRating().intValue();
		int rating3 = data.getLongTermEmergencyCapacityRating().intValue();
		int controlBus = 0;
		int controlSide = 0;
		double tapRatio = 0;
		double tapAngle = 0;
		double minTapAngle = 0;
		double maxTapAngle = 0;
		double stepSize = 0;
		double controlMin = 0;
		double controlMax = 0;

		// write the from
		String temp = new Integer(from).toString();
		while (temp.length() < 4) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the to
		temp = new Integer(to).toString();
		while (temp.length() < 5) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the area
		temp = new Integer(area).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the zone
		temp = new Integer(zone).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the circuit
		temp = circuit;
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the type
		temp = new Integer(type).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp + "  ");

		// write the resistance
		if (resistance == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.5f", resistance);
			temp = formatter.toString();
	    formatter.close();
		}
		while (temp.length() < 10) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the reactance
		if (reactance == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.5f", reactance);
			temp = formatter.toString();
		}
		while (temp.length() < 12) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the line charging
		if (lineCharging == 0) {
			temp = "0.0";
			while (temp.length() < 6) {
				temp = temp + " ";
			}
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%5.4f", lineCharging);
			temp = formatter.toString();
			while (temp.length() < 5) {
				temp = temp + " ";
			}
		}
		buffer.append(temp);

		// write the first rating - left justify
		temp = new Integer(rating1).toString();
		while (temp.length() < 6) {
			temp = temp + " ";
		}
		buffer.append("     " + temp);

		// write the second rating - left justify
		temp = new Integer(rating2).toString();
		while (temp.length() < 6) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the third rating - left justify
		temp = new Integer(rating3).toString();
		while (temp.length() < 4) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the control bus
		temp = new Integer(controlBus).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the control side
		temp = new Integer(controlSide).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the tap ratio
		if (tapRatio == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", tapRatio);
			temp = formatter.toString();
		}
		while (temp.length() < 10) {
			temp = temp + " ";
		}
		buffer.append("  " + temp);

		// write the tap angle
		if (tapAngle == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", tapAngle);
			temp = formatter.toString();
		}
		while (temp.length() < 4) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the min tap angle
		if (minTapAngle == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", minTapAngle);
			temp = formatter.toString();
		}
		while (temp.length() < 7) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the max tap angle
		if (maxTapAngle == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", maxTapAngle);
			temp = formatter.toString();
		}
		while (temp.length() < 8) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the step size
		if (stepSize == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", stepSize);
			temp = formatter.toString();
		}
		while (temp.length() < 7) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the control min
		if (controlMin == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", controlMin);
			temp = formatter.toString();
		}
		while (temp.length() < 6) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the control max
		if (controlMax == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", controlMax);
			temp = formatter.toString();
		}
		buffer.append(temp);

		return buffer.toString();
	}

	/**
	 * Output a cdf transformer
	 * 
	 * @return
	 */
	protected String getCDFTransformerLine(Transformer data, Bus fromBus, Bus toBus, ControlArea a, Zone z, Bus cb) {
		StringBuffer buffer = new StringBuffer();
		StringBuilder sb = null;
		Formatter formatter = null;

		//int from = data.getAttribute(Transformer.FROM_NODE_INTEGER_ID_KEY,Integer.class);
		//int to = data.getAttribute(Transformer.TO_NODE_INTEGER_ID_KEY,Integer.class);
    int from = fromBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,Integer.class);
    int to = toBus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY,Integer.class);
		String circuit = data.getCircuit().toString();
		double resistance = data.getResistance();
		double reactance = data.getReactance();
		double lineCharging = data.getLineCharging();
		int rating1 = data.getCapacityRating().intValue();
		int rating2 = data.getShortTermEmergencyCapacityRating().intValue();
		int rating3 = data.getLongTermEmergencyCapacityRating().intValue();
	  int area = a.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    int zone = z.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);    
		TransformerTypeEnum type = data.getAttribute(Transformer.TYPE_KEY,TransformerTypeEnum.class);
		double tapRatio = data.getAttribute(Transformer.TAP_RATIO_KEY,Double.class);
		double tapAngle = data.getAttribute(Transformer.TAP_ANGLE_KEY,Double.class);
		double minTapAngle = data.getAttribute(Transformer.MIN_TAP_ANGLE_KEY,Double.class);
		double maxTapAngle = data.getAttribute(Transformer.MAX_TAP_ANGLE_KEY,Double.class);
		double stepSize = data.getAttribute(Transformer.STEP_SIZE_KEY,Double.class);
		double controlMin = data.getAttribute(Transformer.CONTROL_MIN_KEY,Double.class);
		double controlMax = data.getAttribute(Transformer.CONTROL_MAX_KEY,Double.class);
	  int controlBus = cb.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		TransformerControlModeEnum controlSide = data.getAttribute(Transformer.CONTROL_SIDE_KEY, TransformerControlModeEnum.class);

		// write the from
		String temp = new Integer(from).toString();
		while (temp.length() < 4) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the to
		temp = new Integer(to).toString();
		while (temp.length() < 5) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the area
		temp = new Integer(area).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the zone
		temp = new Integer(zone).toString();
		while (temp.length() < 3) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the circuit
		temp = circuit;
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the type
		temp = new Integer(type.getTransformerType()).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp + "  ");

		// write the resistance
		if (resistance == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.5f", resistance);
			temp = formatter.toString();
		}
		while (temp.length() < 10) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the reactance
		if (reactance == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%6.5f", reactance);
			temp = formatter.toString();
		}
		while (temp.length() < 12) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the line charging
		if (lineCharging == 0) {
			temp = "0.0";
			while (temp.length() < 6) {
				temp = temp + " ";
			}
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%5.4f", lineCharging);
			temp = formatter.toString();
			while (temp.length() < 5) {
				temp = temp + " ";
			}
		}
		buffer.append(temp);

		// write the first rating - left justify
		temp = new Integer(rating1).toString();
		while (temp.length() < 6) {
			temp = temp + " ";
		}
		buffer.append("     " + temp);

		// write the second rating - left justify
		temp = new Integer(rating2).toString();
		while (temp.length() < 6) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the third rating - left justify
		temp = new Integer(rating3).toString();
		while (temp.length() < 4) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the control bus
		temp = new Integer(controlBus).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the control side
		temp = new Integer(controlSide.getControlMode()).toString();
		while (temp.length() < 2) {
			temp = " " + temp;
		}
		buffer.append(temp);

		// write the tap ratio
		if (tapRatio == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", tapRatio);
			temp = formatter.toString();
		}
		while (temp.length() < 10) {
			temp = temp + " ";
		}
		buffer.append("  " + temp);

		// write the tap angle
		if (tapAngle == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", tapAngle);
			temp = formatter.toString();
		}
		while (temp.length() < 4) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the min tap angle
		if (minTapAngle == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", minTapAngle);
			temp = formatter.toString();
		}
		while (temp.length() < 7) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the max tap angle
		if (maxTapAngle == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", maxTapAngle);
			temp = formatter.toString();
		}
		while (temp.length() < 8) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the step size
		if (stepSize == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", stepSize);
			temp = formatter.toString();
		}
		while (temp.length() < 7) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the control min
		if (controlMin == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", controlMin);
			temp = formatter.toString();
		}
		while (temp.length() < 6) {
			temp = temp + " ";
		}
		buffer.append(temp);

		// write the control max
		if (controlMax == 0) {
			temp = "0.0";
		} else {
			sb = new StringBuilder();
			formatter = new Formatter(sb, Locale.US);
			formatter.format("%4.3f", controlMax);
			temp = formatter.toString();
		}
		buffer.append(temp);

		formatter.close();
		return buffer.toString();
	}

	/**
	 * Write a PFW line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getCoordinateLine(int id, Bus bus) {
		return id + "," + bus.getCoordinate().getX() + ","
				+ bus.getCoordinate().getY();
	}

	/**
	 * Write a PFW line for extra generator information
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getExtraGeneratorInfoLine(int id, Generator generator) {
		GeneratorDefaults defaults = GeneratorDefaults.getInstance();

		double cost = 0;
		if (generator.getAttribute(Generator.ECONOMIC_COST_KEY) != null) {
		  Object obj = generator.getAttribute(Generator.ECONOMIC_COST_KEY);
		  if (obj instanceof Number) {
		    cost = ((Number)obj).doubleValue();
		  }
		  else {
		    PolynomialFunction fcn = ((PolynomialFunction)obj);
		    Collection<Double> coeffs = fcn.getPolynomialCoefficients();
		    Double[] array = coeffs.toArray(new Double[0]);
		    cost = array[array.length-2];
		  }		      
		}
		else {
		  cost = defaults.calculateCost(generator);
		}
		
		
		FuelTypeEnum fuelType = generator.getAttribute(Generator.FUEL_TYPE_KEY) != null ? generator
				.getAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.class)
				: FuelTypeEnum.UNKNOWN;
		double carbon = generator.getAttribute(Generator.CARBON_OUTPUT_KEY) != null ? generator
				.getAttribute(Generator.CARBON_OUTPUT_KEY, Double.class)
				: defaults.calculateCarbon(generator);
		double capacityFactor = generator.getCapacityFactor().doubleValue(); 
		return id + "," + cost + "," + fuelType.getFuelType() + "," + carbon
				+ "," + capacityFactor;
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
		double cost = battery.getAttribute(Battery.ECONOMIC_COST_KEY) == null ? 0
				: battery.getAttribute(Battery.ECONOMIC_COST_KEY, Double.class);
		String name = battery.getAttribute(Generator.NAME_KEY, String.class);
		double max = battery.getRealGenerationMax();
		double min = battery.getRealGenerationMin();

		return id + "," + name + "," + capacity + "," + used + "," + cost + ","
				+ max + "," + min;
	}

	/**
	 * Write a PFW line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getCDFBusLine(Bus bus, Generator generator, Load load, ShuntCapacitor capacitor, ControlArea a, Zone z, Bus controlBus) {
		int id = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		String name = bus.getAttribute(Bus.NAME_KEY, String.class);
	  int area = a.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
    int zone = z.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
		return getCDFBusLine(id, name, area, zone, bus, generator, load, capacitor, controlBus);
	}

}
