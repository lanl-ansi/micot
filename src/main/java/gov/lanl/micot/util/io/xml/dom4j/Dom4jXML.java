package gov.lanl.micot.util.io.xml.dom4j;

import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import gov.lanl.micot.util.io.xml.XML;
import gov.lanl.micot.util.io.xml.XMLDocument;

/**
 * Implementation of the XML functions using the dom4j libraries
 * @author Russell Bent
 */
public class Dom4jXML extends XML {

  @Override
  public XMLDocument parse(URL url) {
    try {
      SAXReader reader = new SAXReader();
		  Document document = reader.read(url);
		  return new Dom4jXMLDocument(document);
		}
		catch (DocumentException e) {
		  throw new RuntimeException(e.getMessage());
		}
	}

	
}
