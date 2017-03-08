package gov.lanl.micot.util.io.dcom.jacob;

import java.io.File;

import com.jacob.activeX.ActiveXComponent;

import gov.lanl.micot.util.io.dcom.ComObject;
import gov.lanl.micot.util.io.dcom.ComObjectFactory;

import java.lang.reflect.Field;


/**
 * An implementation of the com objects using the jacob libraries
 * @author Russell Bent
 */
public class JacobComObjectFactory implements ComObjectFactory {

  
  static {
    String path = System.getProperty("java.library.path");
    path += ";" + System.getProperty("user.dir") + File.separatorChar + ".." + File.separatorChar + "micot-libraries" + File.separatorChar + "nativelib"
        + ";" + System.getenv("PATH");
  
    // this is hack to force java to load the new path...
    System.setProperty("java.library.path", path);
    try {
      Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
      fieldSysPath.setAccessible( true );
      fieldSysPath.set( null, null );
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
      
  /**
   * Singleton factory
   */
  public JacobComObjectFactory() {    
  }
  
  @Override
  public ComObject createComObject(String target) {    
    ActiveXComponent xl = new ActiveXComponent(target);
    return new JacobComObject(xl.getObject());    
  }

}
