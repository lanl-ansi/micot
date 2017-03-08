package gov.lanl.micot.util.io.json;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Used to create default JSON objects
 * @author Russell Bent
 */
public abstract class JSON {

  // set this to change the default packages
  public static String defaultJSONName = "gov.lanl.micot.util.io.json.javax.JavaxJSON";
 
  /**
   * The default JSON reader, which can be changed by adjusted the class name above.  Removes strict library dependecies.
   * @return
   */
  public static JSON getDefaultJSON() {
    try {
      return (JSON) Class.forName(defaultJSONName).newInstance();
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Create a reader
   * @param stream
   * @return
   */
  public abstract JSONReader createReader(InputStream stream);

  /**
   * Create an object builder
   * @return
   */
  public abstract JSONObjectBuilder createObjectBuilder();

  /**
   * Create an array builder
   * @return
   */
  public abstract JSONArrayBuilder createArrayBuilder();
  
  /**
   * Writes the JSON to a stream
   * @param out
   * @return
   */
  public abstract JSONWriter createWriter(OutputStream out);

}
