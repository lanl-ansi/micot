package gov.lanl.micot.infrastructure.ep.model.cdf;

import java.io.Serializable;
import java.util.Formatter;
import java.util.Locale;

/**
 * A class for encapsulating the header information for cdf formatting electric power models
 * @author Russell Bent
 */
public class CDFHeader implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private String header = null;

	/**
	 * Constructor
	 * @param headerData
	 */
	public CDFHeader(String header) {
	  this.header = header;
	}
		
	@Override
	public String toString() {
	  return header;
	}
	
	/**
	 * Set the MVA Base
	 * @param mvaBase
	 */
	public void setMVABase(double mvaBase) {	  	  
	  StringBuilder sb =  new StringBuilder();
    Formatter formatter = new Formatter(sb, Locale.US);
    formatter.format("%10.1f", mvaBase);
    String str = formatter.toString().trim();
    while (str.length() < 6) {
      str += " ";
    }
    header = header.substring(0,31) + str + header.substring(37, header.length());    
	}
	
}
