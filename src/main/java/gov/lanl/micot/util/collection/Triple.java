package gov.lanl.micot.util.collection;

/**
 *  A Tuple of size 3.
 *  
 * @author 236322
 *
 * @param <I1>
 * @param <I2>
 * @param <I3>
 */
public class Triple<I1, I2, I3> extends Tuple {
	public Triple(I1 one, I2 two, I3 three){
		super(one, two, three);
	}
	
	@SuppressWarnings("unchecked")
	public I1 getOne() {return (I1)get(0);}
	
	@SuppressWarnings("unchecked")
	public I2 getTwo() {return (I2)get(1);}
	
	@SuppressWarnings("unchecked")
	public I3 getThree() {return (I3)get(2);}
}
