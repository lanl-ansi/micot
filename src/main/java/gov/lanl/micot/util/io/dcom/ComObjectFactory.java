package gov.lanl.micot.util.io.dcom;

/**
 * Factory for creating com objectives
 * @author Russell Bent
 */
public interface ComObjectFactory {

  /**
   * creates the com object using a specified target name
   * @param target
   * @return
   */
  public ComObject createComObject(String target);
  
}
