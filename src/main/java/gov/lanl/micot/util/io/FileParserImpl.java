package gov.lanl.micot.util.io;

import java.util.Vector;

/**
 * A default file parser that allows access to the lines of a file
 * @author Russell Bent
 */
public class FileParserImpl extends FileParser {

	/**
	 * Constructor
	 * @param filename
	 */
	public FileParserImpl(String filename) {
		readFile(filename);
	}
	
	/**
	 * Get the file lines
	 * @return
	 */
	public Vector<String> getFileLines() {
		return fileLines;
	}
	
}
