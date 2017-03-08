package gov.lanl.micot.infrastructure.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Vector;

import gov.lanl.micot.util.graph.AbstractTree;
import gov.lanl.micot.util.graph.GraphFactory;

/**
 * Abstract class for representing nodes
 * @author Russell Bent
 */
public abstract class NodeImpl implements Serializable, Node {

	protected static final long serialVersionUID = 0;
	
	private AbstractTree<Component,NodeImplEdge> tree = null;
	
	/**
	 * Constructor
	 * @param root
	 */
	public NodeImpl(Component root) {
	  GraphFactory<Component,NodeImplEdge> factory = new GraphFactory<Component,NodeImplEdge>();
    tree = factory.createTree();	  
		tree.setRoot(root);
	}

	
	@Override
	public void addComponent(Component component) {
	  tree.addChild(new NodeImplEdge(tree.getRoot(),component),tree.getRoot(),component);
	}
	
	@Override
	public void removeComponent(Component component) {
	  tree.removeChild(component);
	}
	
	/**
	 * Gets the main object stored at this node
	 * @return
	 */
	protected Component getNodeObject() {
	  return tree.getRoot();
	}
	
	@Override
	public String toString() {
	  return getNodeObject().toString();
	}

	@Override
	public int compareTo(Node arg0) {
	  if (!(arg0 instanceof NodeImpl)) {
	    return -100;
	  }	  
	  return getNodeObject().compareTo(((NodeImpl)arg0).getNodeObject());
	}

	
	@Override
	public int hashCode() {
		return getNodeObject().hashCode();
	}

	@Override
	public boolean equals(Object obj) {	
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		NodeImpl other = (NodeImpl) obj;
		return getNodeObject().equals(other.getNodeObject());
	}

	@Override
  @SuppressWarnings("unchecked")
  public <E extends Component> Collection<E> getComponents(Class<E> cls) {
    Collection<Component> components = tree.getChildren(tree.getRoot());
    Vector<E> vector = new Vector<E>();
    for (Component comp : components) {
      if (cls.isAssignableFrom(comp.getClass())) {
        vector.add((E)comp);
      }
    }
    if (cls.isAssignableFrom(tree.getRoot().getClass())) {
      vector.add((E)tree.getRoot());
    }    
    return vector;
  }
}
