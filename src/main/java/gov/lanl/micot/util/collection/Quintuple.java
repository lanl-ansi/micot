package gov.lanl.micot.util.collection;

/**
 * 
 *  A Tuple of size 5.
 * 
 * @author Russell Bent
 *
 * @param <I1>
 * @param <I2>
 * @param <I3>
 * @param <I4>
 * @param <I5>
 */
public class Quintuple<I1, I2, I3, I4, I5> extends Tuple {
	public Quintuple(I1 one, I2 two, I3 three, I4 four, I5 five){
		super(one, two, three, four, five);
	}
	
	@SuppressWarnings("unchecked")
	public I1 getOne() {return (I1)get(0);}
	
	@SuppressWarnings("unchecked")
	public I2 getTwo() {return (I2)get(1);}
	
	@SuppressWarnings("unchecked")
	public I3 getThree() {return (I3)get(2);}
	
	@SuppressWarnings("unchecked")
	public I4 getFour() {return (I4)get(3);}
	
	@SuppressWarnings("unchecked")
	public I5 getFive() {return (I5)get(4);}

}
