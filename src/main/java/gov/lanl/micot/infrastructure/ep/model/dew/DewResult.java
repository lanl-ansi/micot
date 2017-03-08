package gov.lanl.micot.infrastructure.ep.model.dew;

public class DewResult {

	private String _uid;
	
	private boolean _isSingleValued;
	
	private double[] _phaseValue;
	
	public DewResult(String uid, boolean isSingleValued, double[] phaseValue) {
		_uid = uid;
		_isSingleValued = isSingleValued;
		_phaseValue = phaseValue;
	}
	
	public String getUID() {
		return _uid;  
	}

	public boolean isSingelValued() {
		return _isSingleValued;
	}

	public double[] getPhaseValue() {
		return _phaseValue;
	}

}
