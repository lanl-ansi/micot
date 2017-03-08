package gov.lanl.micot.util.math.solver.bonmin;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import gov.lanl.micot.util.math.solver.JNAOSUtilities;

/**
 * The JNA wrapper of the Bonmin libraries
 * 
 * @author Russell Bent
 *
 */
public interface BonminEngineJNA extends Library {
	public final static JNAOSUtilities.OSType osType = JNAOSUtilities.getOSType();
	public final static String osDirectory = JNAOSUtilities.getOSDirectory(osType);
	public final static String userDir = System.getProperty("user.dir");
	public final static String path = userDir.substring(0, userDir.lastIndexOf(File.separatorChar) + 1) + "micot-libraries"
			+ File.separatorChar + "nativelib" + File.separatorChar + "coinor" + File.separatorChar + osDirectory
			+ File.separatorChar;
	public final static String library = osType.equals(JNAOSUtilities.OSType.Mac) ? "NO LIBRARY YET" : (osType
			.equals(JNAOSUtilities.OSType.Linux) ? "NO LIBRARY YET" : "liblanlbonmin.dll");

	/**
	 * Instance of the native dll library.
	 */
	BonminEngineJNA INSTANCE = (BonminEngineJNA) Native.loadLibrary(path + library, BonminEngineJNA.class);

	// a couple test functions
	public double bonmintest();

	// quadratic program functions
	public Pointer createBonminQuadraticProgram();

	public int destroyBonminQuadraticProgram(Pointer program);

	public Pointer createBonminVariable(Pointer qp, String name, double lb, double ub, int isDiscrete);

	public Pointer createBonminConstraint(Pointer qp, String name, int nlinvars, Pointer linvars[], double linecoefs[],
			int nquadterms, Pointer quadvars1[], Pointer quadvars2[], double quadcoefs[], double lhs, double rhs);

	public Pointer createBonminObjective(Pointer qp, int nlinvars, Pointer linvars[], double linecoefs[],
			int nquadterms, Pointer quadvars1[], Pointer quadvars2[], double quadcoefs[], boolean isMin);

	public void solveBonmin(Pointer qp);

	public int getBonminStatus(Pointer qp);

	public double getBonminObjectiveValue(Pointer qp);

	public double getBonminSolutionValue(Pointer qp, Pointer variable);

	public void setBonminInitialSolution(Pointer qp, int nvars, Pointer vars[], double solution[]);

	// lots of status indicators
	public int getBonminSolveSuccessStatus();

	public int getBonminSolveInfeasibleStatus();

	public int getBonminSolveContinuousUnboundedStatus();

	public int getBonminSolveLimitExceededStatus();

	public int getBonminSolveMINLPErrorStatus();
}
