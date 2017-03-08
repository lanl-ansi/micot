package gov.lanl.micot.util.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Abstract file parser
 * @author Matt Fair
 *
 */
public abstract class FileParser {

	protected Vector<String>	fileLines;
	protected Integer maxLines = null;
	
	/**
	 * Constructor
	 */
	public FileParser() {
		super();
		fileLines = new Vector<String>();
	}

	/**
	 * Read the file
	 * @param stream
	 */
	protected void readFile(InputStream stream) {
		fileLines.clear();
		try {
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(stream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (maxLines != null && fileLines.size() >= maxLines) {
					break;
				}
				fileLines.add(strLine);
			}
			// Close the input stream
			br.close();
			in.close();
			stream.close();
		}
		catch (Exception e) { // Catch exception if any
			System.err.println("Read File Error: " + e.getMessage());
		}
	}
	
	
	/**
	 * Read the file.
	 */
	protected void readFile(String filename) {
		try {
			FileInputStream fstream = new FileInputStream(filename);
			readFile(fstream);
		}
		catch (Exception e) {// Catch exception if any
			System.err.println("Read File Error: " + e.getMessage());
		}		
	}
}