package gov.lanl.micot.infrastructure.ep.exec;

import gov.lanl.micot.infrastructure.ep.io.ElectricPowerModelFileFactory;
import gov.lanl.micot.infrastructure.io.ModelFile;
import gov.lanl.micot.infrastructure.io.csv.CSVModelExporter;
import gov.lanl.micot.infrastructure.model.Model;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * A quick and dirty main class for exporting models as csv files
 * @author Russell Bent
 *
 */
public class ExportCSV {
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
    System.out.println("Beginning CSV export..."); 

    ElectricPowerModelFileFactory.registerExtension("dewc", Class.forName("gov.lanl.micot.infrastructure.ep.io.dew.DewFile"));

    // default configuration
    String initialFile = "C:/Users/305232/Documents/Distribution Resilience/repo/ORU/Burns 19-9-13 and Nanuet 58-8-13.dewc";
    String outputDirectory = "C:/Users/305232/outage/data/dew_csv_export";
    
    if (args.length >= 1)
    	initialFile = args[0];

    if (args.length >= 2)
    	outputDirectory = args[1];

    ModelFile file = new ElectricPowerModelFileFactory().createModelFile(initialFile);    
    Model pfwModel = file.readModel(initialFile);

    CSVModelExporter export = new CSVModelExporter();
    export.exportModel(pfwModel, outputDirectory);
    
    BufferedWriter writer = null;
    try {
        //create a temporary file
        File metaFile = new File(outputDirectory + "/export_metadata.yaml");

        // This will output the full path where the file will be written to...
        System.out.println(metaFile.getCanonicalPath());

        writer = new BufferedWriter(new FileWriter(metaFile));
        writer.write("initialFile: " + initialFile + "\n");
        writer.write("outputDirectory: " + outputDirectory + "\n");        
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            // Close the writer regardless of what happens...
            writer.close();
        } catch (Exception e) {
        }
    }    
    System.out.println("CSV export completed without abnormal termination"); 
    }
}
