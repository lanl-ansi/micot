package gov.lanl.micot.util.collection;

/**
 * A Tuple of size 2.
 * 
 * @author 236322
 * 
 * @param <I1>
 * @param <I2>
 */
public class Pair<I1, I2> extends Tuple {
	public Pair(I1 one, I2 two) {
		super(one, two);
	}

	@SuppressWarnings("unchecked")
	public I1 getOne() {
		return (I1) get(0);
	}

	@SuppressWarnings("unchecked")
	public I2 getTwo() {
		return (I2) get(1);
	}
	
	/**
	 * Get the right element
	 * @return
	 */
	public I2 getRight() {
		return getTwo();
	}

	/**
	 * Get the left element
	 * @return
	 */
	public I1 getLeft() {
		return getOne();
	}
}
