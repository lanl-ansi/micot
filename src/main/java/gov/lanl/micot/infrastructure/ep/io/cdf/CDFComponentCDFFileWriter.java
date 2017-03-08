package gov.lanl.micot.infrastructure.ep.io.cdf;

import java.util.TreeSet;

import gov.lanl.micot.infrastructure.ep.model.Battery;
import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ControlArea;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;
import gov.lanl.micot.infrastructure.ep.model.Generator;
import gov.lanl.micot.infrastructure.ep.model.Intertie;
import gov.lanl.micot.infrastructure.ep.model.Line;
import gov.lanl.micot.infrastructure.ep.model.Load;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitor;
import gov.lanl.micot.infrastructure.ep.model.Transformer;
import gov.lanl.micot.infrastructure.ep.model.Zone;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFHeader;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModel;
import gov.lanl.micot.infrastructure.ep.model.cdf.CDFModelConstants;

/**
 * This is a PFWFile writer that assumes that the model it gets
 * is made up of PFW Components
 * @author Russell Bent
 */
public class CDFComponentCDFFileWriter  extends CDFFileWriterImpl {

	/**
	 * Constructor
	 */
	protected CDFComponentCDFFileWriter() {		
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
		  ControlArea area = model.getControlArea(bus);
		  Zone zone = model.getZone(bus);
		  Bus controlBus =  model.getControlBus(generator);
			String line = getCDFBusLine(bus, generator,load, capacitor, area, zone, controlBus);
			buffer.append(line);
			buffer.append(System.getProperty("line.separator"));
		}		
	}

	@Override
	protected void writeAreas(ElectricPowerModel model, StringBuffer buffer) {
		for (ControlArea area : model.getControlAreas()) {
			buffer.append(getCDFAreaLine(area, model.getSlackBus(area)));
			buffer.append(System.getProperty("line.separator"));	
		}		
	}
	
	@Override
	protected void writeZones(ElectricPowerModel model, StringBuffer buffer) {
		for (Zone zone : model.getZones()) {
			buffer.append(this.getCDFZoneLine(zone));
			buffer.append(System.getProperty("line.separator"));	
		}		
	}
	
	@Override
	protected void writeInterties(ElectricPowerModel model, StringBuffer buffer) {
		for (Intertie intertie : model.getInterties()) {
			String line = getCDFIntertieLine(intertie, model.getFirstNode(intertie).getBus(), model.getSecondNode(intertie).getBus(), model.getMeteredArea(intertie), model.getNonMeteredArea(intertie));
      buffer.append(line);
      buffer.append(System.getProperty("line.separator"));
    }		
	}
	
	@Override
	protected void writeFlowLines(ElectricPowerModel model, StringBuffer buffer) {  
	  TreeSet<ElectricPowerFlowConnection> lines = new TreeSet<ElectricPowerFlowConnection>();
    lines.addAll(model.getFlowConnections());
	    
		for (ElectricPowerFlowConnection line : lines) {
		  String string = null;
		  if (line instanceof Line) {
		    string = getCDFLineLine((Line)line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus(), model.getControlArea(line), model.getZone(line));
		  }
		  else {
        string = getCDFTransformerLine((Transformer)line, model.getFirstNode(line).getBus(), model.getSecondNode(line).getBus(), model.getControlArea(line), model.getZone(line),model.getControlBus(line));	    
		  }
			buffer.append(string);
			buffer.append(System.getProperty("line.separator"));
		}
	}
	
	@Override
	protected CDFHeader getHeader(ElectricPowerModel model) {
		return ((CDFModel)model).getHeader();
	}
	
	@Override
	protected void writeCoordinates(ElectricPowerModel model, StringBuffer buffer) {
		for (Bus bus : model.getBuses()) {
	    int id = bus.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
			buffer.append(getCoordinateLine(id,bus));
			buffer.append(System.getProperty("line.separator"));
		}		
	}

  @Override
  protected void writeExtraGeneratorInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Generator generator : model.getGenerators()) {
      int id = generator.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
      buffer.append(getExtraGeneratorInfoLine(id,generator));
      buffer.append(System.getProperty("line.separator"));
    }   
  }
	
  @Override
  protected void writeBatteryInfo(ElectricPowerModel model, StringBuffer buffer) {
    for (Battery battery : model.getBatteries()) {
      int legacyid = battery.getAttribute(CDFModelConstants.CDF_LEGACY_ID_KEY, Integer.class);
      buffer.append(getBatteryLine(legacyid,battery));
      buffer.append(System.getProperty("line.separator"));
    }   
  }
}
