package gov.lanl.micot.infrastructure.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;

/**
 * This is a general class for finding all cycles in an infrastructure model
 * @author Russell Bent
 *
 */
public class CycleGenerator {

  private ArrayList<Stack<Node>> cycles = null;
  
  private Map<Node, Boolean> visited = null;
  private Map<Node, Integer> depth = null;
  private Map<Node, Integer> low = null;
  private Map<Node, Node> parent = null;
  
  /**
   * Empty constructor
   * @param m
   */
  public CycleGenerator() {    
  }
  
    
  /**
   * This is an implementation of finding all simple cycles in a graph that is based on this 
   * paper
   * 
   * K. Paton, "An algorithm for finding a fundamental set of cycles
   * for an undirected linear graph", Communications of the ACM, pp. 514-518, 1969.
   * 
   * Note that Paton's original algorithm does not find "super cycles", i.e. those that share
   * links with other cycles
   * 
   * @param model The electric power model
   * @return
   */
  public ArrayList<Stack<Node>> getCyclesPaton(Model model) {
    Map<Node, Set<Node>> seenNodes = new HashMap<Node, Set<Node>>();
    Map<Node, Node> parentNodes = new HashMap<Node, Node>();
    Stack<Node> stack = new Stack<Node>();
    cycles = new ArrayList<Stack<Node>>();

    for (Node node : model.getNodes()) {
      // don't go backwards
      if (parentNodes.containsKey(node)) {
        continue;
      }
      
      seenNodes.clear();
          
      // set up the spanning tree
      parentNodes.put(node, node);
      seenNodes.put(node, new HashSet<Node>());
      stack.push(node);
          
      // the core of Paton's algorithm that searches the tree for its cycles.  
      // It only finds simple cycles (those without chords)
      while (!stack.isEmpty()) {
        Node current = stack.pop();
        Set<Node> currentUsed = seenNodes.get(current);
        for (Node neighbor : model.getNeighbors(current)) {
          // new node
          if (!seenNodes.containsKey(neighbor)) {
            parentNodes.put(neighbor, current);
            Set<Node> neighborUsed = new HashSet<Node>();
            neighborUsed.add(current);
            seenNodes.put(neighbor, neighborUsed);
            stack.push(neighbor);
          }
          // a cycle is discovered, so add it to our list
          else if (!currentUsed.contains(neighbor)) {
            Set<Node> neighborUsed = seenNodes.get(neighbor);
            Stack<Node> cycle = new Stack<Node>();
            cycle.add(neighbor);
            cycle.add(current);
            Node p = parentNodes.get(current);
            while (!neighborUsed.contains(p)) {
              cycle.add(p);
              p = parentNodes.get(p);
            }
           cycle.add(p);
           cycles.add(cycle);
           neighborUsed.add(current);
          }
        }
      }
    }
    
    // make it an explict loop
    for (Stack<Node> cycle : cycles) {
      cycle.push(cycle.get(0));
    }
    return cycles;
  }

  /**
   * The following code performs a biconnected component decomposition of an undirected graph 
   * and uses a DFS variant to find all cycles within each biconnected component.  
   */
  public ArrayList<Stack<Node>> getCyclesBiConnected(Model model) {
    cycles = new ArrayList<Stack<Node>>();

    visited = new HashMap<Node, Boolean>();
    depth = new HashMap<Node, Integer>();
    low = new HashMap<Node, Integer>();
    parent = new HashMap<Node, Node>();
    
    for (Node node : model.getNodes()) {
      visited.put(node, false);
      depth.put(node, -1);
      low.put(node, -1);
    }
        
    ArrayList<ArrayList<ArrayList<Node>>> c = new ArrayList<ArrayList<ArrayList<Node>>>();
    Stack<ArrayList<Node>> s = new Stack<ArrayList<Node>>();
    dfs(model, s, c);
          
    for (int i = 0; i < c.size(); ++i) {
      Set<Node> V = new HashSet<Node>();
      generateSubGraph(c.get(i), V);
      ArrayList<ArrayList<Node>> E = new ArrayList<ArrayList<Node>>();
      subGraphDFS(model, E, V);
      for (int j = 0; j < E.size(); ++j) {
        Stack<Node> path = new Stack<Node>();
        path.add(E.get(j).get(0));
        findAllstPaths(model, cycles, path, E.get(j).get(0), E.get(j).get(1), V);
      }
    } 
    
    // make it an explict loop
    for (Stack<Node> cycle : cycles) {
      cycle.push(cycle.get(0));
    }

    // clear out memory
    visited = new HashMap<Node, Boolean>();
    depth = new HashMap<Node, Integer>();
    low = new HashMap<Node, Integer>();
    parent = new HashMap<Node, Node>(); 
    return cycles;
  }
  
  /**
   * Perform the depth first search
   * @param model
   * @param s
   * @param c
   */
  private void dfs(Model model, Stack<ArrayList<Node>> s, ArrayList<ArrayList<ArrayList<Node>>> c) {
    int depth = 0;
    for (Node v : model.getNodes()) {
      if (!visited.get(v)) {
        dfs_visit (model, s, v, depth, c); 
      }
    }
  }

  private void generateSubGraph(ArrayList<ArrayList<Node>> C, Set<Node> V) {
    for (ArrayList<Node> it : C) {
      V.add(it.get(0));
      V.add(it.get(1));
    } 
  }

  /**
   * Do a depth first search on a sub graph
   * @param model
   * @param s
   * @param v
   */
  private void subGraphDFS(Model model, ArrayList<ArrayList<Node>> s, Set<Node> v) {
    int depth = 0;
    for (Node node : model.getNodes()) {
      visited.put(node, false);
    }    
    for (Node it : v) {
      if (!visited.get(it)) {
        subGraphDFS_visit (model, s, it, depth, v); 
      }
    }
  }

  /**
   * Find all paths between v and t
   * @param model
   * @param cycles
   * @param path
   * @param u
   * @param t
   * @param vSet
   */
  private void findAllstPaths(Model model, ArrayList<Stack<Node>> cycles, Stack<Node> path, Node u, Node t, Set<Node> vSet) {
    for (Node v : model.getNeighbors(u)) {
      if (v.equals(t) && path.size() == 1) {
        continue;
      }
      if (!visited(path, v) && vSet.contains(v)) {
        if (v.equals(t)) {
          path.add(v);
          insertPath(cycles, path);
          path.pop();
        } 
        else {
          path.push(v);
          findAllstPaths(model, cycles, path, v, t, vSet);
          path.pop();
        }
      }
    }
  }

  /**
   * Visit the depth first search of a sub graph
   * @param model
   * @param s
   * @param u
   * @param d
   * @param vSet
   */
  private void subGraphDFS_visit(Model model, ArrayList<ArrayList<Node>> s, Node u, int d, Set<Node> vSet) {
    d = d+1;
    visited.put(u, true);
    depth.put(u,d);
    low.put(u,d);
    
    for (Node v : model.getNeighbors(u)) {
      if (!(visited.get(v)) && vSet.contains(v)) {
        parent.put(v, u);
        subGraphDFS_visit(model, s, v, d, vSet);
      } 
      else if (parent.get(u) != null && vSet.contains(v) && !parent.get(u).equals(v)) {
        // uv is a back edge
        ArrayList<Node> e = new ArrayList<Node>();
        e.add(u); e.add(v);
        s.add(e); 
      }
    }
  }
  
  /**
   * Visit the depth first search
   * @param model
   * @param s
   * @param u
   * @param d
   * @param c
   */
  private void dfs_visit(Model model, Stack<ArrayList<Node>> s, Node u, int d, ArrayList<ArrayList<ArrayList<Node>>> c) {
    d = d+1;
    visited.put(u, true);
    depth.put(u, d);
    low.put(u, d);
    
    for (Node v : model.getNeighbors(u)) {
      if (!(visited.get(v))) {
        ArrayList<Node> e = new ArrayList<Node>();
        e.add(u); e.add(v);
        s.push(e);
        parent.put(v,u);
        dfs_visit(model, s, v, d, c);
        if (low.get(v) >= depth.get(u)) {
          outputComponent(s, e, c);
        }
        low.put(u, Math.min(low.get(u), low.get(v)));
      } 
      else if (parent.get(u) != null && depth.get(v) < depth.get(u) && !parent.get(u).equals(v)) {
        // uv is a back edge from u to its ancestor v
        ArrayList<Node> e = new ArrayList<Node>();
        e.add(u); e.add(v);
        s.push(e);
        low.put(u,Math.min(low.get(u), depth.get(v)));
      }
    }
  }
  
  /**
   * Determine if a node has been visited or not
   * @param path
   * @param v
   * @return
   */
  private boolean visited(Stack<Node> path, Node v) {
    for (int i = 0; i < path.size(); ++i) {
      if (path.get(i).equals(v)) {
        return true;
      }
    }
    return false; 
  }

  @SuppressWarnings("unchecked")
  /**
   * Add a path to the list of cycles
   * @param cycles
   * @param path
   */
  private void insertPath(ArrayList<Stack<Node>> cycles, Stack<Node> path) {
    TreeSet<Node> sorted = new TreeSet<Node>();
    sorted.addAll(path);    
    if (isNewPath(cycles, path)) {
      cycles.add((Stack<Node>) path.clone());
    }
  }

  /**
   * Output a component of the graph
   * @param s
   * @param e
   * @param c
   */
  private void outputComponent (Stack<ArrayList<Node>> s, ArrayList<Node> e, ArrayList<ArrayList<ArrayList<Node>>> c) {
    ArrayList<ArrayList<Node>> Comp = new ArrayList<ArrayList<Node>>();
    ArrayList<Node> f = new ArrayList<Node>();
    do {
      f = s.peek();
      Comp.add(f);
      s.pop();
    } 
    while (!f.get(0).equals(e.get(0)) || !f.get(1).equals(e.get(1)));
    c.add(Comp);
  }

  /**
   * Determine if a path is new or not
   * @param cycles
   * @param path
   * @return
   */
  private boolean isNewPath(ArrayList<Stack<Node>> cycles, Stack<Node> path) {
    int n = path.size();
    for (int i = 0; i < cycles.size(); ++i) {
      if (n == cycles.get(i).size()) {
        if (equalPath(path, cycles.get(i))) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Are two paths equal or not
   * @param p_1
   * @param p_2
   * @return
   */
  private boolean equalPath (Stack<Node> path1, Stack<Node> path2) {
    int n = path1.size();
    for (int j = 0; j < n; ++j) {
      if (path1.get(j) != path2.get(j)) {
        return false;
      }
    }
    return true;
  }

  
}
