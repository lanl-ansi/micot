package gov.lanl.micot.util.io.xml.dom4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import gov.lanl.micot.util.io.xml.XML;
import gov.lanl.micot.util.io.xml.XMLDocument;
import gov.lanl.micot.util.io.xml.XMLElement;
import junit.framework.TestCase;

/**
 * Test the XML import functionality
 * @author Russell Bent
 *
 */
public class Dom4jTest extends TestCase  {

  /**
   * Test the import of the XML files
   * @throws MalformedURLException 
   */
	public void testImport() throws MalformedURLException   {	  
	  File networkFile = new File("test_data" + File.separatorChar + "test.xml");
    if (!networkFile.exists()) {
      networkFile = new File(System.getProperty("user.dir") +File.separatorChar + networkFile);
    }    
    URI documentUri = networkFile.toURI();
    XML xml = new Dom4jXML();
    XMLDocument document = xml.parse(documentUri.toURL());  

    XMLElement documentRoot = document.getTopElement();   
    assertEquals(documentRoot.getName(), "Test");
    XMLElement element = documentRoot.getElement("Value");
    assertEquals(element.getName(), "Value");
    
    String value = documentRoot.getValue("Value");
    
    assertEquals(value, "1.0");     
	}

	
}
