package gov.lanl.micot.infrastructure.naturalgas.optimize.ogf;

import java.io.IOException;

import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizer;
import gov.lanl.micot.infrastructure.naturalgas.optimize.NaturalGasMathProgramOptimizerFactory;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * A factory for creating Optimal expansion planning mathematical programs for natural gas expansion
 * This is our default
 * @author Russell Bent
 *  Modified/extended by Conrado
 */
public class SOCFactory extends NaturalGasMathProgramOptimizerFactory {
	
	/**
	 *  Constructor
	 */
	public SOCFactory() {		
	}
	
	@Override
	public NaturalGasMathProgramOptimizer createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		return super.createOptimizer(new SOCFlags(flags));
	}
 
}
