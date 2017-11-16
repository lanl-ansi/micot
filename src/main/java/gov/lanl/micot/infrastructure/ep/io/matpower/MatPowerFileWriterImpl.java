package gov.lanl.micot.infrastructure.ep.io.matpower;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.FuelTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.GeneratorDefaults;
import gov.lanl.micot.infrastructure.ep.model.GeneratorTypeEnum;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelConstants;
import gov.lanl.micot.util.math.Function;
import gov.lanl.micot.util.math.PiecewiseLinearFunction;
import gov.lanl.micot.util.math.PolynomialFunction;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

/**
 * General format for writing mat power files
 * 
 * @author Russell Bent
 */
public abstract class MatPowerFileWriterImpl implements MatPowerFileWriter {

	/**
	 * Constructor
	 */
	public MatPowerFileWriterImpl() {
		super();
	}

	/**
	 * Routine for writing buses
	 * 
	 * @param buses
	 */
	protected abstract void writeBuses(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing buses
	 * 
	 * @param buses
	 */
	protected abstract void writeGenerators(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing areas
	 * 
	 * @param buses
	 */
	protected abstract void writeAreas(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing lines with flows
	 * 
	 * @param buses
	 */
	protected abstract void writeFlowLines(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Get the file header information
	 * 
	 * @param model
	 * @return
	 */
	protected abstract String getVersion(ElectricPowerModel model);

	/**
	 * Get the file header information
	 * 
	 * @param model
	 * @return
	 */
	protected abstract String getCase(ElectricPowerModel model);

	/**
	 * Routine for writing coordinates
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeCoordinates(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing extra generator information
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer);

	/**
	 * Routine for writing extra generator information
	 * 
	 * @param model
	 * @param buffer
	 */
	protected void writeExtraExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Generator generator : model.getGenerators()) {
      buffer.append(getExtraExtraGeneratorInfoLine(generator));
      buffer.append(System.getProperty("line.separator"));
    }   
	}

	
	/**
	 * Routine for writing battery information
	 * 
	 * @param model
	 * @param buffer
	 */
	protected abstract void writeBatteryInfo(ElectricPowerModel model, StringBuffer buffer);

	@Override
	public void saveFile(ElectricPowerModel model, String filename) throws IOException {
		FileOutputStream fileStream = new FileOutputStream(filename);
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileStream));
		StringBuffer buffer = new StringBuffer();

		// output the header
		double mvaBase = model.getMVABase();
		String version = getVersion(model);
		String sCase = getCase(model);

		buffer.append(MatPowerFile.CASE_START_OF_LINE + " = " + sCase);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));

		buffer.append(MatPowerFile.CASE_COMMENT_LINE + " " + version);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.VERSION_START_OF_LINE + " = " + version + ";");
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));

		buffer.append(MatPowerFile.POWER_FLOW_DATA_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BASE_MVA_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BASE_MVA_START_OF_LINE + " = " + mvaBase + ";");
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));

		bufferedWriter.write(buffer.toString());
		buffer = new StringBuffer();

		// output the buses
		buffer.append(MatPowerFile.BUS_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BUS_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BUS_START_OF_LINE+ " = [");
		buffer.append(System.getProperty("line.separator"));
		writeBuses(model, buffer);
		buffer.append(MatPowerFile.BUS_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		// output the generators
		buffer.append(MatPowerFile.GENERATOR_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.GENERATOR_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.GENERATOR_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeGenerators(model, buffer);
		buffer.append(MatPowerFile.GENERATOR_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		// output the lines
		buffer.append(MatPowerFile.BRANCH_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BRANCH_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BRANCH_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeFlowLines(model, buffer);
		buffer.append(MatPowerFile.BRANCH_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		// output the areas
		buffer.append(MatPowerFile.AREA_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.AREA_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.AREA_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeAreas(model, buffer);
		buffer.append(MatPowerFile.AREA_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		// output the extra generation information
		buffer.append(MatPowerFile.EXTRA_GENERATION_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.EXTRA_GENERATION_DATA_LINE1);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.EXTRA_GENERATION_DATA_LINE2);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.EXTRA_GENERATION_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeExtraGeneratorInfo(model, buffer);
		buffer.append(MatPowerFile.EXTRA_GENERATION_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		// output the coordinate information
		buffer.append(MatPowerFile.COORDINATE_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.COORDINATE_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.COORDINATE_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeCoordinates(model, buffer);
		buffer.append(MatPowerFile.COORDINATE_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		// write the battery information
		buffer.append(MatPowerFile.BATTERY_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BATTERY_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.BATTERY_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeBatteryInfo(model, buffer);
		buffer.append(MatPowerFile.BATTERY_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		
		// output the extra extra generation information
		buffer.append(MatPowerFile.EXTRA_EXTRA_GENERATION_COMMENT_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.EXTRA_EXTRA_GENERATION_DATA_LINE);
		buffer.append(System.getProperty("line.separator"));
		buffer.append(MatPowerFile.EXTRA_EXTRA_GENERATION_START_OF_LINE + " = [");
		buffer.append(System.getProperty("line.separator"));
		writeExtraExtraGeneratorInfo(model, buffer);
		buffer.append(MatPowerFile.EXTRA_EXTRA_GENERATION_END_OF_LINE);
		buffer.append(System.getProperty("line.separator"));

		
		bufferedWriter.write(buffer.toString());
		bufferedWriter.flush();
		bufferedWriter.close();
		fileStream.flush();
		fileStream.close();
	}

	/**
	 * Write a MatPower line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getMatPowerBusLine(Bus bus, Generator generator, Load load, ShuntCapacitor capacitor, ControlArea area, Zone zone) {
	  int zoneid = zone.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
	  int areaid = area.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
	  int id = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);

	  int type = 1;
	  if (generator != null && generator.getType().equals(GeneratorTypeEnum.REFERENCE_BUS_TYPE)) {
	    type = 3;
	  }
    if (generator != null && generator.getType().equals(GeneratorTypeEnum.HOLD_VOLTAGE_TYPE)) {
      type = 2;
    }
	  
	  
	  
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t");

		// write the id
		buffer.append(id + "\t");

		// write the type
		buffer.append(type + "\t");

		// write the real load
		double realLoad = load == null ? 0 : load.getDesiredRealLoad().doubleValue();
		buffer.append(realLoad + "\t\t");

		// write the reactive load
		double reactiveLoad = load == null ? 0 : load.getDesiredReactiveLoad().doubleValue();
		buffer.append(reactiveLoad + "\t\t");

		// write the real compensation
		double realShunt = capacitor == null ? 0 : capacitor.getRealCompensation();
		buffer.append(realShunt + "\t");

		// write the reactive compensation
		double reactiveShunt = capacitor == null ? 0 : capacitor.getReactiveCompensation();
		buffer.append(reactiveShunt + "\t");

		// write the area
    buffer.append(areaid + "\t");
		
		// write the voltage magnitude
    double voltage = bus.getVoltagePU().doubleValue();
		buffer.append(voltage + "\t");

		// write the phase angle
		buffer.append(bus.getPhaseAngle() + "\t");

		// write the system voltage
		buffer.append(bus.getSystemVoltageKV() + "\t");

		// write the zone
    buffer.append(zoneid + "\t");
		
		// write the maximum voltage
		double voltageMax = bus.getMaximumVoltagePU();
		buffer.append(voltageMax + "\t");

		// write the minimum voltage
		double voltageMin = bus.getMinimumVoltagePU();
		buffer.append(voltageMin + ";");

		return buffer.toString();
	}

	/**
	 * Write a MatPower line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getMatPowerGeneratorLine(int id, Generator generator, Bus bus) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\t");

		// write the id
		buffer.append(id + "\t");

		// write the real generation
		buffer.append(generator.getRealGeneration() + "\t\t");

		// write the reactive generation
		buffer.append(generator.getReactiveGeneration() + "\t\t");

		// write the max reactive generation
		buffer.append(generator.getReactiveGenerationMax() + "\t\t");

		// write the min reactive generation
		buffer.append(generator.getReactiveGenerationMin() + "\t\t");

		// write desired voltage
//		buffer.append(generator.getDesiredVoltage() + "\t");
    buffer.append(bus.getVoltagePU() + "\t");

		// write mbase
		buffer.append(generator.getAttribute(Generator.MVA_BASE_KEY) + "\t");

		// write status
		buffer.append((generator.getStatus() ? 1 : 0) + "\t");

		// write the max real generation
		buffer.append(generator.getRealGenerationMax() + "\t");

		// write the min real generation
		buffer.append(generator.getRealGenerationMin() + "\t");

		// write the lower real power output of PQ capability curve (MW)
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REAL_CURVE_KEY) + "\t");

		// write the upper real power output of PQ capability curve (MW)
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REAL_CURVE_KEY) + "\t");

		// write the minimum reactive power output at Pc1
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MIN_REAL_KEY) + "\t");

		// write the maximum reactive power output at Pc1
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MIN_REAL_KEY) + "\t");

		// write the minimum reactive power output at Pc2
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MIN_REACTIVE_CURVE_FOR_MAX_REAL_KEY) + "\t");

		// write the maximum reactive power output at Pc2
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_GENERATOR_MAX_REACTIVE_CURVE_FOR_MAX_REAL_KEY) + "\t");

		// ramp rate for load following/AGC (MW/min)
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_LOAD_RAMP_RATE_KEY) + "\t");

		// ramp rate for 10 minute reserves (MW)
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_TEN_MINUTE_RESERVE_RAMP_RATE_KEY) + "\t");

		// ramp rate for 30 minute reserves (MW)
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_THIRTY_MINUTE_RESERVE_RAMP_RATE_KEY) + "\t");

		// ramp rate for reactive power (2 sec timescale) (MVAr/min)
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_REACTIVE_RAMP_RATE_KEY) + "\t");

		// APF, area participation factor
		buffer.append(generator.getAttribute(MatPowerModelConstants.MATPOWER_AREA_PARTICIPATION_FACTOR_KEY) + ";");

		return buffer.toString();
	}

	/**
	 * Creates a MatPower for an area
	 */
	protected String getMatPowerAreaLine(ControlArea area, Bus sb) {
		int id = area.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
    int slackBus = sb.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);


		StringBuffer buffer = new StringBuffer();
		buffer.append("\t");

		// write the id
		buffer.append(id + "\t");

		// write the slack bus
		buffer.append(slackBus + ";");

		return buffer.toString();
	}

	/**
	 * Create a pfw line format
	 * 
	 * @param data
	 * @return
	 */
	protected String getMatPowerLineLine(ElectricPowerFlowConnection data, Bus fromBus, Bus toBus) {
		StringBuffer buffer = new StringBuffer();
    int from = fromBus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
    int to = toBus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
		double resistance = data.getResistance();
		double reactance = data.getReactance();
		double lineCharging = data.getLineCharging();
		double longTermRating = data.getCapacityRating();
		double shortTermRating = data.getLongTermEmergencyCapacityRating();
		double emergencyRating = data.getShortTermEmergencyCapacityRating();
		double ratio = (data instanceof Line) ? 1.0 : ((Transformer) data).getAttribute(Transformer.TAP_RATIO_KEY, Double.class);
		double angle = (data instanceof Line) ? 0 : ((Transformer) data).getAttribute(Transformer.TAP_ANGLE_KEY, Double.class);
		int status = data.getStatus() ? 1 : 0;
		double minAngle = data.getAttribute(MatPowerModelConstants.MATPOWER_MIN_ANGLE_DIFFERENCE_KEY, Double.class);
		double maxAngle = data.getAttribute(MatPowerModelConstants.MATPOWER_MAX_ANGLE_DIFFERENCE_KEY, Double.class);

		buffer.append(from + "\t");
		buffer.append(to + "\t");
		buffer.append(resistance + "\t");
		buffer.append(reactance + "\t");
		buffer.append(lineCharging + "\t");
		buffer.append(longTermRating + "\t");
		buffer.append(shortTermRating + "\t");
		buffer.append(emergencyRating + "\t");
		buffer.append(ratio + "\t");
		buffer.append(angle + "\t");
		buffer.append(status + "\t");
		buffer.append(minAngle + "\t");
		buffer.append(maxAngle + ";");

		return buffer.toString();
	}

	/**
	 * Write a PFW line for buses
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getCoordinateLine(int id, Bus bus) {
		return "\t" + id + "\t" + bus.getCoordinate().getX() + "\t" + bus.getCoordinate().getY() + ";";
	}

	/**
	 * Write a PFW line for extra generator information
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getExtraGeneratorInfoLine(int id, Generator generator) {
	  Object cost = generator.getAttribute(Generator.ECONOMIC_COST_KEY, Object.class);
	  if (cost == null) {
	    cost = 0.0; 
	  }
	  
		int costModel = cost instanceof PolynomialFunction ? 2 : 1;
		double startupCost = generator.getAttribute(Generator.STARTUP_COST_KEY, Double.class);
		double shutdownCost = generator.getAttribute(Generator.SHUTDOWN_COST_KEY, Double.class);
		int points = cost instanceof Number ? 1 : ((Function)cost).getNumOfDataPoints();

		Double coefficients[] = null;
		if (cost instanceof Number) {
			coefficients = new Double[2];
			coefficients[0] = generator.getRealGenerationMax();
			coefficients[1] = generator.getRealGenerationMax() * ((Number)cost).doubleValue();
		}
		else if (cost instanceof PolynomialFunction) {
			Collection<Double> dataPoints = ((PolynomialFunction)cost).getPolynomialCoefficients();
			coefficients = dataPoints.toArray(new Double[0]);
		}
		else {
			Collection<Point2D> dataPoints = ((PiecewiseLinearFunction)cost).getDataPoints();
			coefficients = new Double[dataPoints.size()*2];
			int idx = 0;
			for (Point2D pair : dataPoints) {
				coefficients[idx] = pair.getX();
				++idx;
				coefficients[idx] = pair.getY();
				++idx;
			}			
		}
		

		String temp = "\t" + costModel + "\t" + startupCost + "\t" + shutdownCost + "\t" + points + "\t";
		for (int i = 0; i < coefficients.length; ++i) {
			temp += coefficients[i] + "\t";
		}
		
		temp += ";";		
		return temp;
	}

	
	/**
	 * Write a PFW line for extra generator information
	 * 
	 * @param bus
	 * @param data
	 */
	protected String getExtraExtraGeneratorInfoLine(Generator generator) {
		GeneratorDefaults defaults = GeneratorDefaults.getInstance();
		
		FuelTypeEnum fuelType = generator.getAttribute(Generator.FUEL_TYPE_KEY) != null ?  
	      generator.getAttribute(Generator.FUEL_TYPE_KEY, FuelTypeEnum.class) : FuelTypeEnum.UNKNOWN;
	  double carbon = generator.getAttribute(Generator.CARBON_OUTPUT_KEY) != null ? 
	      generator.getAttribute(Generator.CARBON_OUTPUT_KEY, Double.class) : defaults.calculateCarbon(generator);
	  double capacityFactor = generator.getCapacityFactor().doubleValue(); // steady-state output model
		
		String temp = "\t" + fuelType.getFuelType() + "\t" +carbon + "\t" + capacityFactor + ";";
		return temp;
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
		double max = battery.getRealGenerationMax();
		double min = battery.getRealGenerationMin();
		return "\t" + id + "\t" + capacity + "\t" + used + "\t" + cost + "\t" + max + "\t" + min + ";";
	}

}
