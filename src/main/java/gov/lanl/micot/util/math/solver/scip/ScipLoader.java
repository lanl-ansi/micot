package gov.lanl.micot.util.math.solver.scip;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.sun.jna.Native;

/**
 * This is a simple loader of the Scip library that can help with some of the
 * debugging
 * 
 * @author Russell Bent
 *
 */
public class ScipLoader {

  /**
   * No construction
   */
  private ScipLoader() {
  }

  /**
   * Does some error checking to see if scip loads
   * 
   * @param dependentlibs
   * @param filename
   * @return
   */
  protected static ScipEngineJNA loadScip(String libraryName, String fullFilename, String[] dependentlibs) {
    // check for library dependicies
    ArrayList<String> missingLibraries = new ArrayList<String>();
    for (int i = 0; i < dependentlibs.length; ++i) {
      try {
        Native.loadLibrary(dependentlibs[i], ScipEngineJNA.class);
      }
      catch (UnsatisfiedLinkError e) {
        missingLibraries.add("\t" + dependentlibs[i]);
      }
    }

    if (missingLibraries.size() > 0) {
      System.err.println("Some Libraries appear to be missing from your system.  SCIP may fail.");
      for (String str : missingLibraries) {
        System.err.println(str);
      }
    }
    
    
    // check of the file
    File file = new File(fullFilename);
    if (file.exists()) {
      return (ScipEngineJNA) Native.loadLibrary(fullFilename, ScipEngineJNA.class);
    }

    // see if you are running this in the local directory
    try {
      return (ScipEngineJNA) Native.loadLibrary(libraryName, ScipEngineJNA.class);
    }
    catch (java.lang.UnsatisfiedLinkError e) {
    }

    // check the PATH environment variable
    String path = System.getenv("PATH");
    StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
    while (tokenizer.hasMoreTokens()) {
      String newPath = tokenizer.nextToken() + File.separatorChar + libraryName;
      file = new File(newPath);
      if (file.exists()) {
        return (ScipEngineJNA) Native.loadLibrary(newPath, ScipEngineJNA.class);
      }       
    }
    
    System.err.println("Error: The file " + libraryName + " does not exist in your filespace. Please verify your PATH environment variable contains " + libraryName + ".");
    return null;
  }

}
