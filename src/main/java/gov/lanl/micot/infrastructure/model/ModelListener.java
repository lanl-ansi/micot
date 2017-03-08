package gov.lanl.micot.infrastructure.model;

/**
 * This is an interface for listening to changes in a model's
 * structure
 * @author Russell Bent
 */
public interface ModelListener {

  /**
   * Notification that a violation has occurred
   * @param violation
   */
//  public void addViolation(Violation violation);
    
  /**
   * Notification that a violation has been removed
   * @param v
   */
 // public void removeViolation(Violation v);

  /**
   * Notification that violations have been cleared
   */
  //public void clearViolations();

  /**
   * Set the simulation quality rank
   * @param rank
   */
  public void setSimulationQualityRank(int rank, boolean validSimulation);
  
  /**
   * Updates the objective after the addition of a link
   * @param link
   */
  public void addEdge(Connection link, /*Corridor corridor,*/ Node node1, Node node2);
  
  /**
   * Updates the objective after the removal of a link
   * @param link
   */
  public void removeEdge(Connection link, /*Corridor corridor,*/ Node bus1, Node bus2);

    /**
   * Notification that an attribute has been updated
   * @param asset
   * @param attribute
   */
  public void attributeUpdated(Asset asset, Node node, Object attribute, Object value);
  
  /**
   * Notification that an attribute has been updated
   * @param asset
   * @param attribute
   */
  public void attributeUpdated(Connection edge, /*Corridor corridor,*/ Node node1, Node node2, Object attribute, Object value);
  
  /**
   * Add a component
   * @param component
   * @param node1
   */
  public void addComponent(Component component, Node node);

  /**
   * remove a component
   * @param component
   * @param node2
   */
  public void removeComponent(Component component, Node node);

  /**
   * Add a control to the model
   * @param control
   */
  public void addControl(Control control);
  
  /**
   * remove a control to the model
   * @param control
   */
  public void removeControl(Control control);
}
