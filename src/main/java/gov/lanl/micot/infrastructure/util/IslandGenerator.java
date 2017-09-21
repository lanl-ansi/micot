package gov.lanl.micot.infrastructure.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import gov.lanl.micot.infrastructure.model.FlowConnection;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;

/**
 * Class for finding all islands in a network
 * @author Russell Bent
 *
 */
public class IslandGenerator {

  /**
   * Constructor
   */
  public IslandGenerator() {    
  }
  
  /**
   * Determine the islands of the nodes
   * @param model
   * @return
   */
  public Collection<Collection<Node>> getIslands(Model model) {
    Vector<Collection<Node>> islands = new Vector<Collection<Node>>();

    HashSet<Node> availableNodes = new HashSet<Node>();
    availableNodes.addAll(model.getNodes());

    while (availableNodes.size() > 0) {
      SortedSet<Node> set = new TreeSet<Node>();
      HashSet<Node> toExplore = new HashSet<Node>();
      toExplore.add(availableNodes.iterator().next());
      while (toExplore.size() > 0) {
        Node node = toExplore.iterator().next();
        toExplore.remove(node);
        availableNodes.remove(node);
        set.add(node);

        Collection<? extends FlowConnection> edges = model.getFlowConnections(node);
        for (FlowConnection edge : edges) {
          if (edge.getDesiredStatus() == false) {
            continue;
          }

          Node node1 = model.getFirstNode(edge);
          Node node2 = model.getSecondNode(edge);

          if (availableNodes.contains(node1)) {
            toExplore.add(node1);
          }

          if (availableNodes.contains(node2)) {
            toExplore.add(node2);
          }
        }
      }
      islands.add(set);
    }
    return islands;
  }

}
