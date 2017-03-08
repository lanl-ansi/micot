package gov.lanl.micot.util.time;

/**
 * A timer class for keeping track of how much clock
 * time has passed
 * @author Russell Bent
 */
public class Timer {
  private long iStartValue = -1;
  
  /**
   * Constructor
   */
  public Timer() {  
  }

  /**
   * Begins the timer
   */
  public void startTimer() { 
    iStartValue = System.currentTimeMillis(); 
  }

  /**
   * Given a "times up" value, has the CPU clock run that long?
   * @finish finish time in milliseconds
   */  
  public boolean isTimeUp(long finish) {
    long value = System.currentTimeMillis();
    long diff = value - iStartValue;
    if (diff < finish) {
      return false;
    }
    else {
      return true;
    }
  }
    
  /**
   * Return the number of CPU minutes since the timer started
   * @return the number of CPU minutes
   */
  public int getCPUMinutes() {
    long value = System.currentTimeMillis();
    double diff = value - iStartValue;
    double seconds = diff / 1000.0; 
    double minutes = seconds / 60.0;
    return (int)minutes;    
  }

  /**
   * Get the number of CPU minutes in terms of decimals
   * @return the number of CPU minutes
   */
  public double getCPUMinutesDec() {
    long value = System.currentTimeMillis();
    double diff = value - iStartValue;
    double seconds = diff / 1000.0; 
    double minutes = seconds / 60.0;
    return minutes;
  } 
}
