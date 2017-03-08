package gov.lanl.micot.infrastructure.ep.model.pfw;

import gov.lanl.micot.infrastructure.ep.model.Bus;
import gov.lanl.micot.util.math.NumberFormat;

import java.io.Serializable;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A class for encapsulating the header information for pfw formatting electric power models
 * @author Russell Bent
 * TODO: Actually understand what the header file is doing so that we can set any parameters we 
 * want to that exist in the header file
 */
public class PFWHeader implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private Vector<String> headerData = null;
	
	/**
	 * Constructor
	 * @param headerData
	 */
	public PFWHeader(Vector<String> headerData) {
		this.headerData = headerData;		
	}
		
	/**
	 * Adds a line
	 * @param line
	 */
	//public void addHeaderLine(String line) {
		//headerData.add(line);
	//}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		// Header Data
		Iterator<String> iter = headerData.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}
	
	/**
	 * Get the MVA base out of the header file
	 * @return
	 */
	public double getMVABase() {
	  for (String str : headerData) {
	    if (str.contains("BASEMVA")) {
	      StringTokenizer tokenizer = new StringTokenizer(str, ",");
	      tokenizer.nextToken();
	      return Double.parseDouble(tokenizer.nextToken());
	    }	    
	  }
	  return 0;
	}
	
	/**
	 * Set the MVA Base
	 * @param mvaBase
	 */
	public void setMVABase(double mvaBase) {	  
	  for (int i = 0; i < headerData.size(); ++i) {
	    if (headerData.get(i).contains("BASEMVA")) {
	      String str = "\"BASEMVA             \", ";    
	      StringBuilder sb = new StringBuilder();
	      Formatter formatter = new Formatter(sb, Locale.US);
	      formatter.format(NumberFormat.createSignificantDigitFormat(mvaBase,6, 6), mvaBase);
	      str += formatter.toString();	      
        headerData.set(i, str);
	      break;
	    }
	  }
	}
	
 /**
   * Get the MVA base out of the header file
   * @return
   */
  public double getMinVoltage() {
    for (String str : headerData) {
      if (str.contains("VMINPU")) {
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        tokenizer.nextToken();
        return Double.parseDouble(tokenizer.nextToken());
      }     
    }
    return Bus.DEFAULT_MINIMUM_VOLTAGE;
  }

  /**
   * Get the MVA base out of the header file
   * @return
   */
  public double getMaxVoltage() {
    for (String str : headerData) {
      if (str.contains("VMAXPU")) {
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        tokenizer.nextToken();
        return Double.parseDouble(tokenizer.nextToken());
      }     
    }
    return Bus.DEFAULT_MAXIMUM_VOLTAGE;
  }

  /**
   * Set the MVA Base
   * @param mvaBase
   */
  public void setMinVoltage(double minVoltage) {    
    for (int i = 0; i < headerData.size(); ++i) {
      if (headerData.get(i).contains("VMINPU")) {
        String str = "\"VMINPU              \", ";
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(NumberFormat.createSignificantDigitFormat(minVoltage,6, 6), minVoltage);
        str += formatter.toString();                
        headerData.set(i, str);
        break;
      }
    }
  }

  /**
   * Set the MVA Base
   * @param mvaBase
   */
  public void setMaxVoltage(double maxVoltage) {    
    for (int i = 0; i < headerData.size(); ++i) {
      if (headerData.get(i).contains("VMAXPU")) {
        String str = "\"VMAXPU              \", ";
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.US);
        formatter.format(NumberFormat.createSignificantDigitFormat(maxVoltage,6, 6), maxVoltage);
        str += formatter.toString();                
        headerData.set(i, str);
        break;
      }
    }
  }

  
}
