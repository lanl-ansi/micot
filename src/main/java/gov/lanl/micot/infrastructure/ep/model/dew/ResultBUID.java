package gov.lanl.micot.infrastructure.ep.model.dew;

import com.sun.jna.Pointer;

public class ResultBUID {
	
    private String UID;
    private boolean singleValuedResult;    
    private double[] phaseValue;
    private Pointer pNextResult;

    public ResultBUID(Pointer pointer) {
        int offset = 0;
        UID = pointer.getString(offset);
        //System.out.println(UID.length());           
        offset += 36; 
        singleValuedResult = pointer.getInt(offset) == 0 ? false : true;
        offset += 4; // size of a c BOOL?
        phaseValue = pointer.getDoubleArray(offset, 3);
        offset += 8*3;
        pNextResult = pointer.getPointer(offset);
    }

    public String getUID() {
      return UID;  
    }
    
    public boolean isSingelValuedResult() {
        return singleValuedResult;
    }
    
    public double[] getPhaseValue() {
        return phaseValue;
    }
    
    public Pointer getNextResult() {
        return pNextResult;
    }
}