package gov.lanl.micot.util.collection;

/**
 * 
 *  A Tuple of size 4.
 * 
 * @author 236322
 *
 * @param <I1>
 * @param <I2>
 * @param <I3>
 * @param <I4>
 */
public class Quadruple<I1, I2, I3, I4> extends Tuple {
	public Quadruple(I1 one, I2 two, I3 three, I4 four){
		super(one, two, three, four);
	}
	
	@SuppressWarnings("unchecked")
	public I1 getOne() {return (I1)get(0);}
	
	@SuppressWarnings("unchecked")
	public I2 getTwo() {return (I2)get(1);}
	
	@SuppressWarnings("unchecked")
	public I3 getThree() {return (I3)get(2);}
	
	@SuppressWarnings("unchecked")
	public I4 getFour() {return (I4)get(3);}
	
}
