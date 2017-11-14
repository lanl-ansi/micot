package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitch;
import gov.lanl.micot.infrastructure.ep.model.ShuntCapacitorSwitchFactory;
import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;

import java.util.StringTokenizer;

/**
 * Factory class for creating PFWSwitchedShunts and ensuring their uniqueness
 * @author Russell Bent
 */
public class PFWSwitchedShuntFactory extends ShuntCapacitorSwitchFactory {

	private static final String LEGACY_TAG = "PFW";
	
	/**
	 * Constructor
	 */
	protected PFWSwitchedShuntFactory() {
	}
	
	/**
	 * Creates a switched shunt and its state from a PFW file line
	 * @param line
	 * @return
	 * @throws PFWModelException 
	 */
	public ShuntCapacitorSwitch createSwitchedShunt(String line, Point point) throws PFWModelException {		
	  ShuntCapacitorSwitch shunt = constructSwitchedShunt(line, point);
	  int legacyid = shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,Integer.class);
		if (getLegacy(LEGACY_TAG,legacyid) != null) {
			if (point == null) {
  			shunt.setCoordinate(getLegacy(LEGACY_TAG,legacyid).getCoordinate());
  		}
  	}
		return shunt;
	}
	
	/**
	 * Construction of a shunt
	 * @param line
	 * @return
	 */
	private ShuntCapacitorSwitch constructSwitchedShunt(String line, Point point) {
	  line = line.trim();
		StringTokenizer tokenizer = new StringTokenizer(line, ",");
		int legacyid = Integer.parseInt(tokenizer.nextToken().trim());
		int controlMode = Integer.parseInt(tokenizer.nextToken().trim());
		double scheduleHighVoltage = Double.parseDouble(tokenizer.nextToken().trim());
		double scheduledLowVoltage = Double.parseDouble(tokenizer.nextToken().trim());
		double desiredVoltage = Double.parseDouble(tokenizer.nextToken().trim());
		double remotedDesiredVoltage = Double.parseDouble(tokenizer.nextToken().trim());
		/*int remoteBus = */Integer.parseInt(tokenizer.nextToken().trim());
		double initialSusceptance = Double.parseDouble(tokenizer.nextToken().trim());
		double susceptance = Double.parseDouble(tokenizer.nextToken().trim());
		/*int area =*/ Integer.parseInt(tokenizer.nextToken().trim());
		/*int zone =*/ Integer.parseInt(tokenizer.nextToken().trim());
		int status = Integer.parseInt(tokenizer.nextToken().trim());
		int normalStatus = Integer.parseInt(tokenizer.nextToken().trim());

		String temp = tokenizer.nextToken().trim();
		Integer numSubsections1 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection1Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections2 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection2Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections3 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection3Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections4 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection4Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections5 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection5Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections6 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection6Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections7 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection7Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections8 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection8Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections9 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		temp = tokenizer.nextToken().trim();
		Double subsection9Size = (temp.length() == 0) ? null : Double.parseDouble(temp);

		temp = tokenizer.nextToken().trim();
		Integer numSubsections10 = (temp.length() == 0) ? null : Integer.parseInt(temp);
		if (tokenizer.hasMoreTokens()) {
		  temp = tokenizer.nextToken().trim();
		}
		else {
		  temp = "";
		}
		Double subsection10Size = (temp.length() == 0) ? null : Double.parseDouble(temp);
 	 
		
  	// check to see if the area already exists
		PFWSwitchedShuntControlModeEnum type = PFWSwitchedShuntControlModeEnum.getEnum(controlMode);
		ShuntCapacitorSwitch shunt = registerCapacitor(legacyid);
		shunt.setStatus((status == 1 ? true : false));
   shunt.setDesiredVoltage(desiredVoltage);
    shunt.setScheduledLowVoltage(scheduledLowVoltage);
    shunt.setScheduleHighVoltage(scheduleHighVoltage);
    shunt.setRemoteDesiredVoltage(remotedDesiredVoltage);
   	shunt.setInitialSusceptance(initialSusceptance);
  	shunt.setSusceptance(susceptance);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_CONTROL_MODE_KEY, type);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NORMAL_STATUS_KEY, normalStatus);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS1_KEY, numSubsections1);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION1_SIZE_KEY, subsection1Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS2_KEY, numSubsections2);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION2_SIZE_KEY, subsection2Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS3_KEY, numSubsections3);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION3_SIZE_KEY, subsection3Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS4_KEY, numSubsections4);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION4_SIZE_KEY, subsection4Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS5_KEY, numSubsections5);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION5_SIZE_KEY, subsection5Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS6_KEY, numSubsections6);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION6_SIZE_KEY, subsection6Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS7_KEY, numSubsections7);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION7_SIZE_KEY, subsection7Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS8_KEY, numSubsections8);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION8_SIZE_KEY, subsection8Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS9_KEY, numSubsections9);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION9_SIZE_KEY, subsection9Size);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NUM_SUBSECTIONS10_KEY, numSubsections10);
    shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_SUBSECTION10_SIZE_KEY, subsection10Size);
  	shunt.setCoordinate(point == null ? new PointImpl(0,0) : point);
  	return shunt;
	}

  /**
   * Creates a bus from another bus
   * @param bus
   * @return
   */
  public void updateSwitchedShunt(ShuntCapacitorSwitch shunt, Bus bus) {
    int legacyid = bus.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, Integer.class);
    if (shunt.getAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY) == null) {
      shunt.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY, legacyid);
      registerLegacy(LEGACY_TAG, legacyid, shunt);
    }
        
    if (shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_CONTROL_MODE_KEY) == null) {
      shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_CONTROL_MODE_KEY, PFWSwitchedShuntControlModeEnum.LOCKED_MODE);
    }
    if (shunt.getAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NORMAL_STATUS_KEY) == null) {
      shunt.setAttribute(PFWModelConstants.PFW_SWITCHED_SHUNT_NORMAL_STATUS_KEY, shunt.getStatus() ? 1 : 0);
    }
  }	
  
  /**
   * Register the shunt capacitor
   * @param legacyId
   * @param bus
   * @return
   */
  private ShuntCapacitorSwitch registerCapacitor(int legacyId) {
    ShuntCapacitorSwitch capacitor = getLegacy(LEGACY_TAG, legacyId);
    
    if (capacitor == null) {
      capacitor = createNewSwitch();
      capacitor.setAttribute(PFWModelConstants.PFW_LEGACY_ID_KEY,legacyId);
      capacitor.addOutputKey(PFWModelConstants.PFW_LEGACY_ID_KEY);
      registerLegacy(LEGACY_TAG, legacyId, capacitor);
    }
    return capacitor;
  }

  
}
