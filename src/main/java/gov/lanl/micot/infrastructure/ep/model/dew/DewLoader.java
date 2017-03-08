package gov.lanl.micot.infrastructure.ep.model.dew;

import java.io.File;

import com.sun.jna.Native;

/**
 * This is a simple loader of the Dew library that can help with some of the debugging
 * @author Russell Bent
 *
 */
public class DewLoader {

  /**
   * No construction
   */
  private DewLoader() {    
  }
  
  /**
   * Does some error checking to see if dew loads
   * @param filename
   * @return
   */
	protected static DewEngineJNA loadDew(String filename) {
	  File file = new File(filename);
	  if (!file.exists()) {
	    System.err.println("Error: The file " + filename + " does not exist in your filespace. Please verify your path.");
	  }
	    
	  return (DewEngineJNA) Native.loadLibrary(filename, DewEngineJNA.class);
	}
	
}
