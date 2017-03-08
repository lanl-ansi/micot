package gov.lanl.micot.infrastructure.ep.model.cdf;

/**
 * Interface for CDF models
 * @author Russell Bent
 */
public interface CDFModel {
		  
  /**
   * Get the CDF header
   * @return
   */
  public CDFHeader getHeader();
}
