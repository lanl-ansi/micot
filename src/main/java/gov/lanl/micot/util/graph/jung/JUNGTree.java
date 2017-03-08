package gov.lanl.micot.util.graph.jung;

import java.util.Collection;

import edu.uci.ics.jung.graph.DelegateTree;
import gov.lanl.micot.util.graph.AbstractTree;

/**
 * Implementation of the abstract tree that use the JUNG 
 * package
 * @author Russell Bent
 *
 * @param <V>
 * @param <E>
 */
public class JUNGTree<V,E> extends DelegateTree<V,E> implements AbstractTree<V, E> {
  protected static final long serialVersionUID = 0;

  @Override
  public void setRoot(V root) {
    assert(getRoot() == null);
    super.setRoot(root);
  }
 
  @Override
  public V getRoot() {
    return super.getRoot();
  }

  @Override
  public boolean addChild(E edge, V parent, V child) {
    return super.addChild(edge, parent, child);
  }
  
  @Override
  public Collection<V> getChildren(V parent) {
    return super.getChildren(parent); 
  }

	@Override
	public boolean removeChild(V child) {
		return super.removeChild(child);
	}
  
}
