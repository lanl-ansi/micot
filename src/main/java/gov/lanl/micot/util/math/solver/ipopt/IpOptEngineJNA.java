package gov.lanl.micot.util.math.solver.ipopt;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import gov.lanl.micot.util.math.solver.JNAOSUtilities;

/**
 * The JNA wrapper of the SCIP libraries
 * 
 * The SCIP libraries themselves are "almost" complete enough to use directly. The only thing missing is a function to
 * allocate memory for the SCIP* object (and some other objects). So, we created our own library that is essentially a
 * pass through except for one method that creates the SCIP object
 * 
 * @author Russell Bent
 *
 */
public interface IpOptEngineJNA extends Library {
	public final static JNAOSUtilities.OSType osType = JNAOSUtilities.getOSType();
	public final static String osDirectory = JNAOSUtilities.getOSDirectory(osType);
	public final static String userDir = System.getProperty("user.dir");
	public final static String path = userDir.substring(0, userDir.lastIndexOf(File.separatorChar) + 1) + "micot-libraries"
			+ File.separatorChar + "nativelib" + File.separatorChar + "coinor" + File.separatorChar + osDirectory
			+ File.separatorChar;
	public final static String library = osType.equals(JNAOSUtilities.OSType.Mac) ? "NO LIBRARY YET" : (osType
			.equals(JNAOSUtilities.OSType.Linux) ? "NO LIBRARY YET" : "liblanlipopt.dll");

	/**
	 * Instance of the native dll library.
	 */
	IpOptEngineJNA INSTANCE = (IpOptEngineJNA) Native.loadLibrary(path + library, IpOptEngineJNA.class);

	// a couple test functions
	public double simpleTest1234(double d);

	public double ipopttest();

	// quadratic program functions
	public Pointer createIpoptQuadraticProgram();

	public int destroyIpoptQuadraticProgram(Pointer program);

	public Pointer createIpoptVariable(Pointer qp, String name, double lb, double ub, boolean isDiscrete);

	public Pointer createIpoptConstraint(Pointer qp, String name, int nlinvars, Pointer linvars[], double linecoefs[],
			int nquadterms, Pointer quadvars1[], Pointer quadvars2[], double quadcoefs[], double lhs, double rhs);

	public Pointer createIpoptObjective(Pointer qp, int nlinvars, Pointer linvars[], double linecoefs[],
			int nquadterms, Pointer quadvars1[], Pointer quadvars2[], double quadcoefs[], boolean isMin);

	public void solveIpopt(Pointer qp);

	public int getIpoptStatus(Pointer qp);

	public double getIpoptObjectiveValue(Pointer qp);

	public double getIpoptSolutionValue(Pointer qp, Pointer variable);

	public void setIpoptInitialSolution(Pointer qp, int nvars, Pointer vars[], double solution[]);

	// lots of status indicators
	public int getIpoptSolveSucceededStatus();

	public int getIpoptSolvedToAcceptableLevelStatus();

	public int getIpoptInfeasibleProblemDetectedStatus();

	public int getIpoptSearchDirectionBecomesTooSmallStatus();

	public int getIpoptDivergingIteratesStatus();

	public int getIpoptUserRequestedStopStatus();

	public int getIpoptFeasiblePointFoundStatus();

	public int getIpoptMaximumIterationsExceededStatus();

	public int getIpoptRestorationFailedStatus();

	public int getIpoptErrorInStepComputationStatus();

	public int getIpoptMaximumCPUTimeExceeedStatus();

	public int getIpoptNotEnoughDegreesOfFreedomStatus();

	public int getIpoptInvalidProblemDefinitionStatus();

	public int getIpoptInvalidOptionStatus();

	public int getIpoptInvalidNumberDetectedStatus();

	public int getIpoptUnrecoverableExceptionStatus();

	public int getIpoptNonIpoptExceptionThrownStatus();

	public int getIpoptInsufficientMemoryStatus();

	public int getIpoptInternalErrorStatus();
}
