package gov.lanl.micot.infrastructure.optimize.jump;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.OptimizerImpl;
import gov.lanl.micot.util.math.solver.exception.SolverException;

/**
 * This class contains basic information about an optimization routine that talks to jump to 
 * run optimization code
 * 
 * @author Russell Bent
 */

public abstract class JumpOptimizer<N extends Node, M extends Model> extends OptimizerImpl<N,M> {

  protected double cpuTime = 0;  
  protected double objectiveValue = 0;
  private JumpOptimizerFlags flags = null;
  
  /**
   * Constructor
   * 
   */
  protected JumpOptimizer() {
    super();
    setupMathProgramFlags();
  }

  /**
   * Set up the math program flags
   */
  private void setupMathProgramFlags() {
    flags = new JumpOptimizerFlags();
  }
    
  /**
   * Add a math program flag
   * @param key
   * @param value
   */
  public void addMathProgramFlag(String key, Object value) {
    flags.put(key, value);
  }
    
  @Override
  public boolean solve(M model) {
            
//    model.clearViolations();
    updateModelStatus(model);
          
    boolean state = true;
    try {
      cpuTime = 0;
      objectiveValue = 0;
      optimize(model);
    }
    catch (Exception e) {
      e.printStackTrace();
      state = false;
    }
    
    setIsFeasible(state);
    return state;
  }


  /**
   * Creates the input for Julia
   * @param model
   * @return
   */
  protected abstract String[] createJuliaInput(M model);
  
  /**
   * Creates the output for Julia
   * @param model
   */
  protected abstract void readJuliaOutput(M model);
  
  /**
   * Perform the optimization
   * @param model
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   * @throws SolverException
   */
  protected synchronized void optimize(M model) {
    String input_commands[] = createJuliaInput(model);
    String command_line[] = getJuliaCommand();
 
    String commands[] = new String[input_commands.length + command_line.length];
    int i = 0;
    for (int j = 0; j < command_line.length; ++j, ++i) {
      commands[i] = command_line[j];
    }
    for (int j = 0; j < input_commands.length; ++j, ++i) {
      commands[i] = input_commands[j];
    }

    try {
      
      ProcessBuilder pb=new ProcessBuilder(commands);
      pb.redirectErrorStream(true);
      Process process=pb.start();
      BufferedReader inStreamReader = new BufferedReader(
          new InputStreamReader(process.getInputStream())); 

      String line = null;
      while((line = inStreamReader.readLine()) != null) { 
         System.out.println(line);
      }
      
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    readJuliaOutput(model);
 }
  
  /**
   * Get last CPU time
   * @return
   */
  public double getLastCPUTime() {
    return cpuTime;
  }
  
  /**
   * Get the last objective value
   * @return
   */
  public double getLastObjectiveValue() {
    return objectiveValue;
  }
  
  /**
   * A function for getting summary statistics about the performance of the algorithm
   * @return
   */
  public int getLastAlgorithmIterations() {   
    return 1;
  }
  
  /**
   * Sets all the asset deired status
   * @param model
   */
  protected void updateModelStatus(M model) {
    for (Asset asset : model.getAssets()) {
      boolean status = asset.getDesiredStatus();      
      Object failed = asset.getAttribute(Asset.IS_FAILED_KEY);
      if (failed != null) {
          status = status &= !(Boolean)failed;
        }
      asset.setActualStatus(status);
	  }
	}
  
  /**
   * Get the the flags
   * @return
   */
  protected JumpOptimizerFlags getFlags() {
    return flags;
  }
  
  private String[] getJuliaCommand() {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("mac")) {
      return new String[] {"julia "}; // I think
    } 
    else if (osName.contains("win")) {
      return new String[] {"cmd", "/c", "julia"}; 
    } 
    else if (osName.contains("nux")) {
      return new String[] {"julia "}; // I think
    } 
    else {
      return new String[] {"julia "}; // I think
    }
  }

}
