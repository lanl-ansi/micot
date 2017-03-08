package gov.lanl.micot.infrastructure.ep.io.matpower;

import java.util.TreeSet;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModel;
import gov.lanl.micot.infrastructure.ep.model.matpower.MatPowerModelConstants;

/**
 * This is a MatPowerFile writer that assumes that the model it gets
 * is made up of Mat Power Components
 * @author Russell Bent
 */
public class MatPowerComponentMatPowerFileWriter  extends MatPowerFileWriterImpl {

	/**
	 * Constructor
	 */
	protected MatPowerComponentMatPowerFileWriter() {		
	}

	@Override
	protected void writeBuses(ElectricPowerModel model, StringBuffer buffer) {
	  TreeSet<ElectricPowerNode> nodes = new TreeSet<ElectricPowerNode>();
	  nodes.addAll(model.getNodes());
	  
		for (ElectricPowerNode node : nodes) {
		  Bus bus = node.getBus();
		  Generator generator = node.getGenerator();
		  Load load = node.getLoad();
		  ShuntCapacitor capacitor = node.getShunt();
			String line = getMatPowerBusLine(bus, generator,load, capacitor, model.getControlArea(bus), model.getZone(bus));
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}

	@Override
	protected void writeAreas(ElectricPowerModel model, StringBuffer buffer) {
		for (ControlArea area : model.getControlAreas()) {
			buffer.append(getMatPowerAreaLine(area, model.getSlackBus(area)));
			buffer.append(System.getProperty("line.separator"));	
		}		
	}
		
	@Override
	protected void writeFlowLines(ElectricPowerModel model, StringBuffer buffer) {  
	  TreeSet<ElectricPowerFlowConnection> lines = new TreeSet<ElectricPowerFlowConnection>();
    lines.addAll(model.getFlowConnections());	    
		for (ElectricPowerFlowConnection line : lines) {
		  String string = getMatPowerLineLine(line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus());
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}
	
	@Override
	protected String getVersion(ElectricPowerModel model) {
		return ((MatPowerModel)model).getVersion();
	}

	@Override
	protected String getCase(ElectricPowerModel model) {
	  return ((MatPowerModel)model).getCase();
	}
	
	@Override
	protected void writeCoordinates(ElectricPowerModel model, StringBuffer buffer) {
		for (Bus bus : model.getBuses()) {
	    int id = bus.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
			buffer.append(getCoordinateLine(id,bus));
			buffer.append(System.getProperty("line.separator"));
		}		
	}

  @Override
  protected void writeExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Generator generator : model.getGenerators()) {
      int id = generator.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
      buffer.append(getExtraGeneratorInfoLine(id,generator));
      buffer.append(System.getProperty("line.separator"));
    }   
  }
	
  @Override
  protected void writeBatteryInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Battery battery : model.getBatteries()) {
      int legacyid = battery.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class);
      buffer.append(getBatteryLine(legacyid,battery));
      buffer.append(System.getProperty("line.separator"));
    }   
  }

	@Override
	protected void writeGenerators(ElectricPowerModel model, StringBuffer buffer) {
		for (Generator generator : model.getGenerators()) {
			buffer.append(getMatPowerGeneratorLine(generator.getAttribute(MatPowerModelConstants.MATPOWER_LEGACY_ID_KEY, Integer.class),generator, model.getNode(generator).getBus()));
			buffer.append(System.getProperty("line.separator"));	
		}	
		
	}
}
