package gov.lanl.micot.infrastructure.ep.io.opendss;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

import gov.lanl.micot.util.geometry.Point;
import gov.lanl.micot.util.geometry.PointImpl;
import gov.lanl.micot.util.io.FileParserImpl;

/**
 * An encapulation of the data contained in opendss files
 * @author Russell Bent
 *
 */
public class OpenDSSData {
  private HashMap<String, String> vsources = null;;
  private HashMap<String, String> lineCodes = null;
  private HashMap<String, String> loadShapes = null;
  private HashMap<String, String> growthShapes = null;
  private HashMap<String, String> tccCurves = null;
  private HashMap<String, String> spectrums = null;
  private HashMap<String, String> lines = null;
  private HashMap<String, String> loads = null;
  private HashMap<String, String> transformers = null;
  private HashMap<String, String> regControls = null;
  private HashMap<String, String> capacitors = null;
  private HashMap<String, Point> buscoords = null;
  private HashMap<String, String> generators = null;

  /**
   * Contructor
   * @param filename
   */
  public OpenDSSData(String filename) {
    vsources = new HashMap<String,String>();
    lineCodes = new HashMap<String,String>();
    loadShapes = new HashMap<String,String>();
    growthShapes = new HashMap<String,String>();
    tccCurves = new HashMap<String,String>();
    spectrums = new HashMap<String,String>();
    lines = new HashMap<String,String>();
    loads = new HashMap<String,String>();
    transformers = new HashMap<String,String>();
    regControls = new HashMap<String,String>();
    capacitors = new HashMap<String,String>();
    buscoords = new HashMap<String,Point>();
    generators = new HashMap<String,String>();
    
    filename = filename.replace('\\', File.separatorChar);
    String directory = new File(filename).getParent();
    
    Stack<String> filenames = new Stack<String>();
    filenames.push(filename);
    
    while (filenames.size() >= 1) {
      String fn = filenames.pop();
      fn = fn.replace('\\', File.separatorChar);
      
      
      FileParserImpl parser = new FileParserImpl(fn);
      Collection<String> flines = parser.getFileLines();
      for (String line : flines) {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");
        if (!tokenizer.hasMoreTokens()) {
          continue;
        }
        
        String token1 = tokenizer.nextToken();
        String token2 = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";
        
        if (token1.equalsIgnoreCase(OpenDSSIOConstants.REDIRECT)) {
          filenames.push(directory + File.separatorChar + token2);
        }
        else if (token1.equalsIgnoreCase(OpenDSSIOConstants.BUSCOORDS)) {
          String fn2 = directory + File.separatorChar + token2;
          fn2 = fn2.replace('\\', File.separatorChar);
          FileParserImpl parser2 = new FileParserImpl(fn2);
          Collection<String> coords = parser2.getFileLines();
          for (String coord : coords) {
            StringTokenizer tokenizer2 = new StringTokenizer(coord, ",");
            String id = tokenizer2.nextToken();
            String x = tokenizer2.nextToken();
            String y = tokenizer2.nextToken();
            buscoords.put(id, new PointImpl(Double.parseDouble(x),Double.parseDouble(y)));
          }
        }
        else if (token1.equalsIgnoreCase(OpenDSSIOConstants.NEW)) {
          if (token2.toLowerCase().contains(OpenDSSIOConstants.CAPACITOR.toLowerCase())) {
            capacitors.put(getId(token2),line);            
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.LINE.toLowerCase()) && !token2.toLowerCase().contains(OpenDSSIOConstants.LINE_CODE.toLowerCase())) {
            lines.put(getId(token2),line);
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.LINE_CODE.toLowerCase())) {
            lineCodes.put(getId(token2),line);
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.TRANSFORMER.toLowerCase())) {
            transformers.put(getId(token2),line);
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.GROWTH_SHAPE.toLowerCase())) {
            growthShapes.put(getId(token2),line);
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.LOAD.toLowerCase())) {
            loads.put(getId(token2),line);
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.LOAD_SHAPE.toLowerCase())) {
            loadShapes.put(getId(token2),line);            
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.REG_CONTROL.toLowerCase())) {
            regControls.put(getId(token2),line);            
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.SPECTRUM.toLowerCase())) {
            spectrums.put(getId(token2),line);            
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.TCC_CURVE.toLowerCase())) {
            tccCurves.put(getId(token2),line);            
          }
          else if (token2.toLowerCase().contains(OpenDSSIOConstants.GENERATOR.toLowerCase())) {
            generators.put(getId(token2),line);            
          }

        }
        else if (token1.equalsIgnoreCase(OpenDSSIOConstants.EDIT)) {
          if (token2.toLowerCase().contains(OpenDSSIOConstants.VSOURCE.toLowerCase())) {
            vsources.put(getId(token2),line);            
          }

        }
      }      
    }    
  }

  /**
   * general function for extracting an id
   * @param id
   * @return
   */
  private String getId(String id) {
    id = id.replace('\"', ' ');
    int idx = id.indexOf(".");
    id = id.substring(idx+1, id.length());
    return id.trim();
  }
  
  /**
   * Get the ids of all of v source ids
   * @return
   */
  public Set<String> getVsourceIds() {
    return vsources.keySet();
  }

  /**
   * Get the data associated with the v source data
   * @param id
   * @return
   */
  public String getVSourceData(String id) {
    return vsources.get(id);
  }
  
  /**
   * Get the line code ids
   * @return
   */
  public Set<String> getLineCodeIds() {
    return lineCodes.keySet();
  }
  
  /**
   * Get the line code
   * @param id
   * @return
   */
  public String getLineCodeData(String id) {
    return lineCodes.get(id);
  }

  /**
   * Get the load shape ids
   * @return
   */
  public Set<String> getLoadShapeIds() {
    return loadShapes.keySet();
  }
  
  /**
   * Get the load shape data
   * @param id
   * @return
   */
  public String getLoadShapeData(String id) {
    return loadShapes.get(id);
  }
  
  /**
   * Get the growth shape ids
   * @return
   */
  public Set<String> getGrowthShapeIds() {
    return growthShapes.keySet();
  }
  
  /**
   * Get the growth shape data
   * @param id
   * @return
   */
  public String getGrowthShapeData(String id) {
    return growthShapes.get(id);
  }
  
  /**
   * Get the tcc curve ids
   * @return
   */
  public Set<String> getTccCurveIds() {
    return tccCurves.keySet();
  }
  
  /**
   * Get the tcc curve data
   * @param id
   * @return
   */
  public String getTccCurveData(String id) {
    return tccCurves.get(id);
  }
  
  /**
   * Get the spectrum ids
   * @return
   */
  public Set<String> getSpectrumIds() {
    return spectrums.keySet();
  }
  
  /**
   * Get the spectrue data
   * @param id
   * @return
   */
  public String getSpectrumData(String id) {
    return spectrums.get(id);
  }

  /**
   * Get the line ids
   * @return
   */
  public Set<String> getLineIds() {
    return lines.keySet();
  }

  /**
   * Get the line data
   * @param id
   * @return
   */
  public String getLineData(String id) {
    return lines.get(id);
  }

  /**
   * Return loads ids
   * @return
   */
  public Set<String> getLoadIds() {
    return loads.keySet();
  }

  /**
   * Get load data
   * @param id
   * @return
   */
  public String getLoadData(String id) {
    return loads.get(id);
  }

  /**
   * Get transformer ids
   * @return
   */
  public Set<String> getTransformerIds() {
    return transformers.keySet();
  }

  /**
   * Get transformer data
   * @param id
   * @return
   */
  public String getTransformerData(String id) {
    return transformers.get(id);
  }

  /**
   * Get transformer ids
   * @return
   */
  public Set<String> getGeneratorIds() {
    return generators == null ? null : generators.keySet();
  }

  /**
   * Get transformer data
   * @param id
   * @return
   */
  public String getGeneratorData(String id) {
    return generators.get(id);
  }

  
  
  /**
   * Get the regulator control ids
   * @return
   */
  public Set<String> getRegControlIds() {
    return regControls.keySet();
  }

  /**
   * Get the control id
   * @param id
   * @return
   */
  public String getRegControlData(String id) {
    return regControls.get(id);
  }
  
  /**
   * Get the capacitor ids
   * @return
   */
  public Set<String> getCapacitorIds() {
    return capacitors.keySet();
  }
  
  /**
   * Get the capacitor data
   * @param id
   * @return
   */
  public String getCapacitorData(String id) {
    return capacitors.get(id);
  }

  /**
   * Get the bus coordinate data
   * @param id
   * @return
   */
  public Point getBusCoordData(String id) {
    return buscoords.get(id);
  }
  
  /**
   * Get tje bus coordinate data
   * @return
   */
  public Set<String> getBusCoordIds() {
    return buscoords.keySet();
  } 
  
}
