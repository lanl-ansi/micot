package gov.lanl.micot.util.collection;

import java.util.TreeMap;

/**
 * Class RangeHashMap is a HashMap that maps a continuous 
 * range of Double values to a Double. Each range of values, 
 * from a to b, is [a,b), or closed on the lower bound and 
 * open on the upper, unless it is the final range (in which 
 * case it would be [a,b], where b is the final upper bound).
 * @author Eli Chertkov
 *
 */
public class RangeMap extends TreeMap<Double, Double> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Double lowerBound;
	
	/**
	 * Constructs a RangeHashMap with a default lower bound of zero.
	 */
	public RangeMap() {
		lowerBound = new Double(0.0);
	}
	
	/**
	 * Constructs a RangeHashMap with a given lower bound value. 
	 * A lower bound is needed to assure that the first mapping 
	 * added has a lower bound on its range.
	 * @param lowerBound the lowest value in the keySet of Doubles
	 */
	public RangeMap(Double lowerBound) {
		this.lowerBound = lowerBound;
	}
	
	/**
	 * Maps a Double v to Double k. Here k is the upper bound 
	 * on the range of values that v is mapped to. The lower 
	 * bound is the largest key value in the key set that is 
	 * less than k.
	 * @param k the upper bound of the range that v is mapped to
	 * @param v the value defined by the range
	 */
	@Override
	public Double put(Double k, Double v) {
		if(k < lowerBound)
			throw new RuntimeException("The upper bound "+k+" cannot be used because it is below the lower bound"+lowerBound+".");
		return super.put(k,v);
	}
	
	/**
	 * Returns the value that has the given key in its range. 
	 * Make sure that endpoints of the ranges are called correctly: 
	 * for the ranges [a,b) -> v and [b,c) -> v2, get(a) = v and get(b) = v2.
	 * The largest upperbound u is closed: [t,u], not [t,u).
	 * @param key a key > lowerBound
	 */
	@Override
	public Double get(Object key) {
		if(key==null || (Double)key < lowerBound)
			return null;
		
		if(super.get(key)!=null && higherKey((Double) key)!=null)
			return super.get(higherKey((Double) key));
		
		return super.get(ceilingKey((Double) key));
	}
	
	@Override
	public Double floorKey(Double key) {
		if(super.floorKey(key)==null && key>=lowerBound) {
			return lowerBound;
		}
		return super.floorKey(key);
	}
	
	@Override
	public Double lowerKey(Double key) {
		if(super.lowerKey(key)==null && key>lowerBound) {
			return lowerBound;
		}
		return super.lowerKey(key);
	}
	
}
