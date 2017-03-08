package gov.lanl.micot.infrastructure.ep.io.pfw;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWHeader;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModel;
import gov.lanl.micot.infrastructure.ep.model.pfw.PFWModelConstants;

/**
 * This is a PFWFile writer that assumes that the model it gets
 * is made up of PFW Components
 * @author Russell Bent
 */
public class PFWComponentPFWFileWriter  extends PFWFileWriterImpl {

	/**
	 * Constructor
	 */
	protected PFWComponentPFWFileWriter() {		
	}

	@Override
	protected void writeBuses(ElectricPowerModel model, StringBuffer buffer) {
		for (Bus bus : model.getBuses()) {
			String line = getPFWBusLine(bus, model.getControlArea(bus), model.getZone(bus));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}

	@Override
	protected void writeAreas(ElectricPowerModel model, StringBuffer buffer) {
		for (ControlArea area : model.getControlAreas()) {
			buffer.append(getPFWAreaLine(area, model.getSlackBus(area)));
			buffer.append(System.getProperty("line.separator"));	
		}		
	}
	
	@Override
	protected void writeZones(ElectricPowerModel model, StringBuffer buffer) {
		for (Zone zone : model.getZones()) {
			buffer.append(this.getPFWZoneLine(zone));
			buffer.append(System.getProperty("line.separator"));	
		}		
	}

	@Override
	protected void writeGenerators(ElectricPowerModel model, StringBuffer buffer) {
		for (Generator generator : model.getGenerators()) {		
			String line = getPFWGeneratorLine(generator, model.getNode(generator).getBus(), model.getControlArea(generator), model.getZone(generator), model.getControlBus(generator));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
		
		// write the batteries as generators
		for (Battery battery : model.getBatteries()) {		
			String line = getPFWGeneratorLine(battery, model.getNode(battery).getBus(), model.getControlArea(battery), model.getZone(battery), model.getControlBus(battery));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}
	
	@Override
	protected void writeInterties(ElectricPowerModel model, StringBuffer buffer) {
		for (Intertie intertie : model.getInterties()) {
			String line = getPFWIntertieLine(intertie, model.getFirstNode(intertie).getBus(), model.getSecondNode(intertie).getBus(), model.getMeteredArea(intertie), model.getNonMeteredArea(intertie));
      buffer.append(line);
      buffer.append(System.getProperty("line.separator"));
    }		
	}
	
	@Override
	protected void writeLines(ElectricPowerModel model, StringBuffer buffer) {
		for (Line line : model.getLines()) {
			String string = getPFWLineLine(line,model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus(), model.getControlArea(line), model.getZone(line)); 
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}
	
	@Override
	protected void writeTransformers(ElectricPowerModel model, StringBuffer buffer) {
		for (Transformer line : model.getTransformers()) {		
			String string = getPFWTransformerLine(line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus(), model.getControlArea(line), model.getZone(line), model.getControlBus(line));
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));			
		}		
	}

	@Override
	protected void writeLoads(ElectricPowerModel model, StringBuffer buffer) {
		for (Load load : model.getLoads()) {
			String line = getPFWLoadLine(load,model.getControlArea(load), model.getZone(load));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}
	
	@Override
	protected void writeShunts(ElectricPowerModel model, StringBuffer buffer) {
		for (ShuntCapacitor shunt : model.getShuntCapacitors()) {
			String line = getPFWShuntLine(shunt,model.getControlArea(shunt), model.getZone(shunt));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}
	}
	
	@Override
	protected void writeSwitchedShunts(ElectricPowerModel model, StringBuffer buffer) {
		for (ShuntCapacitorSwitch shunt : model.getShuntCapacitorSwitches()) {
			String line = getPFWSwitchedShuntLine(shunt, model.getControlArea(shunt), model.getZone(shunt), model.getControlBus(shunt));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}

	@Override
	protected PFWHeader getHeader(ElectricPowerModel model) {
		return ((PFWModel)model).getHeader();
	}
	
	@Override
	protected void writeCoordinates(ElectricPowerModel model, StringBuffer buffer) {
		for (Bus bus : model.getBuses()) {
	    int id = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
			buffer.append(getCoordinateLine(id,bus));
			buffer.append(System.getProperty("line.separator"));
		}		
	}

  @Override
  protected void writeExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Generator generator : model.getGenerators()) {
      int id = generator.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
      buffer.append(getExtraGeneratorInfoLine(id,generator));
      buffer.append(System.getProperty("line.separator"));
    }   
  }
	
  @Override
  protected void writeBatteryInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Battery battery : model.getBatteries()) {
      int legacyid = battery.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
      buffer.append(getBatteryLine(legacyid,battery));
      buffer.append(System.getProperty("line.separator"));
    }   
  }
}
