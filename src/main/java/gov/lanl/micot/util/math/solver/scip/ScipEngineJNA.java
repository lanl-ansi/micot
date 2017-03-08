package gov.lanl.micot.util.math.solver.scip;

import java.io.File;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

import gov.lanl.micot.util.math.solver.JNAOSUtilities;

/**
 * The JNA wrapper of the SCIP libraries
 * 
 * The SCIP libraries themselves are "almost" complete enough to use directly. The only thing missing is a function to
 * allocate memory for the SCIP* object (and some other objects). So, we created our own library that is essentially a
 * pass through except for one method that creates the SCIP object
 * 
 * Use the command "ldd" on linux to find library dependencies.
 * 
 * @author Russell Bent
 *
 */
public interface ScipEngineJNA extends Library {
	public final static JNAOSUtilities.OSType osType = JNAOSUtilities.getOSType();
	public final static String osDirectory = JNAOSUtilities.getOSDirectory(osType);
	public final static String userDir = System.getProperty("user.dir");
	public final static String path = userDir.substring(0, userDir.lastIndexOf(File.separatorChar) + 1) + "micot-libraries"
			+ File.separatorChar + "nativelib" + File.separatorChar + "scip" + File.separatorChar + osDirectory
			+ File.separatorChar;
	public final static String library = osType.equals(JNAOSUtilities.OSType.Mac) ? "libscip.dylib" : (osType
			.equals(JNAOSUtilities.OSType.Linux) ? "scipLibrary.so" : "scipLibrary.dll");

	public final static String dependentLibs[] = (osType.equals(JNAOSUtilities.OSType.Linux)) ? new String[] {"libz.so.1", "libgmp.so.10", "libreadline.so.6", "libgfortran.so.3", "libgcc_s.so.1","libc.so.6"/*,"ld-linux-x86-64.so.2"*/} : new String[]{};
	
	/**
	 * Instance of the native dynamic library.
	 */
	public ScipEngineJNA INSTANCE = ScipLoader.loadScip(library, path+library, dependentLibs); 
	
	public double LibSCIPversion();

	public Pointer LibSCIPcreate();

	public int LibSCIPincludeDefaultPlugins(Pointer scip);

	public int LibSCIPcreateProbBasic(Pointer scip, String name);

	public int LibSCIPmajorVersion();

	public int LibSCIPminorVersion();

	public int LibSCIPtechVersion();

	public int LibSCIPsubversion();

	public Pointer LibSCIPcreateVarBasic(Pointer scip, String name, double lb, double ub, double obj, int vartype);

	public double LibSCIPinfinity(Pointer scip);

	public int LibSCIPchgVarLb(Pointer scip, Pointer var, double newbound);

	public int LibSCIPchgVarUb(Pointer scip, Pointer var, double newbound);

	public int LibSCIPaddVar(Pointer scip, Pointer var);

	public Pointer LibSCIPexprCreateLinear(Pointer scip, int nchildren, Pointer children[], double coefs[],
			double constant);

	public Pointer LibSCIPexprCreate(Pointer scip, Pointer op);

	public Pointer LibSCIPexprCreateMonomial(Pointer scip, double coef, int nfactors, int childidxs[],
			double exponents[]);

	public Pointer LibSCIPexprCreatePolynomial(Pointer scip, int nchildren, Pointer children[], int nmonomials,
			Pointer monomials[], double constant, int copymonomials);

	public Pointer LibSCIPexprtreeCreate(Pointer scip, Pointer root, int nvars, int nparams, double params[]);

	public int LibSCIPexprtreeSetVars(Pointer tree, int nvars, Pointer vars[]);

	public Pointer LibSCIPcreateConsBasicNonlinear(Pointer scip, String name, int nlinvars, Pointer linvars[],
			double linecoefs[], int nexprtrees, Pointer exprtrees[], double nonlincoefs[], double lhs, double rhs);

	public int LibSCIPexprtreeFree(Pointer tree[]);

	public Pointer LibSCIPcreateConsBasicQuadratic(Pointer scip, String name, int nlinvars, Pointer linvars[],
			double lincoefs[], int nquadterms, Pointer quadvars1[], Pointer quadvars2[], double quadcoefs[],
			double lhs, double rhs);

	public int LibSCIPaddBilinTermQuadratic(Pointer scip, Pointer cons, Pointer var1, Pointer var2, double coef);

	public int LibSCIPaddSquareCoefQuadratic(Pointer scip, Pointer cons, Pointer var, double coef);

	public int LibSCIPaddQuadVarLinearCoefQuadratic(Pointer scip, Pointer cons, Pointer var, double coef);

	public int LibSCIPaddCoefLinear(Pointer scip, Pointer cons, Pointer var, double val);

	public Pointer LibSCIPcreateConsBasicSetpart(Pointer scip, String name, int nvars, Pointer vars[]);

	public int LibSCIPaddCons(Pointer scip, Pointer cons);

	public int LibSCIPreleaseVar(Pointer scip, Pointer var);

	public int LibSCIPreleaseCons(Pointer scip, Pointer cons);

	public int LibSCIPpresolve(Pointer scip);

	public int LibSCIPsolve(Pointer scip);

	public int LibSCIPfreeTransform(Pointer scip);

	public int LibSCIPgetNSols(Pointer scip);

	public int LibSCIPfree(Pointer scip);

	public Pointer LibSCIPgetBestSol(Pointer scip);

	public double LibSCIPgetObj(Pointer scip, Pointer sol);

	public int LibSCIPgetStatus(Pointer scip);

	public Pointer LibSCIPcreateConsBasicAbspower(Pointer scip, String name, Pointer x, Pointer z, double exponent,
			double xoffset, double zcoef, double lhs, double rhs);

	public Pointer LibSCIPcreateConsBasicLinear(Pointer scip, String name, int nvars, Pointer vars[], double vals[],
			double lhs, double rhs);

	public double LibSCIPsolGetVal(Pointer scip, Pointer sol, Pointer var);

	public int LibSCIPgetContinuousVarType();

	public int LibSCIPgetBinaryVarType();

	public int LibSCIPgetDiscreteVarType();

	public int LibSCIPchgVarObj(Pointer scip, Pointer var, double newobj);

	public int LibSCIPsetObjsense(Pointer scip, int sense);

	public int LibSCIPgetObjsenseMax();

	public int LibSCIPgetObjsenseMin();

	/**
	 * Prints the problem in cip format to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintProblemCIP(Pointer scip);

	/**
	 * Prints the problem in cip format to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintProblemMPS(Pointer scip);

	/**
	 * Prints the problem in lp format to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintProblemLP(Pointer scip);

	/**
	 * Prints the problem in cip format to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintProblemCIPToFile(Pointer scip, String filename);

	/**
	 * Prints the problem in cip format to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintProblemMPSToFile(Pointer scip, String filename);

	/**
	 * Prints the problem in lp format to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintProblemLPToFile(Pointer scip, String filename);

	/**
	 * Prints the best solution to the screen
	 * 
	 * @param scip
	 * @return
	 */
	public int LibSCIPprintSolution(Pointer scip);

	public int LibSCIPsetBoolParam(Pointer scip, String name, int value);

	public int LibSCIPsetIntParam(Pointer scip, String name, int value);

	public int LibSCIPsetLongParam(Pointer scip, String name, long value);

	public int LibSCIPsetDoubleParam(Pointer scip, String name, double value);

	public int LibSCIPsetCharParam(Pointer scip, String name, char value);

	public int LibSCIPsetStringParam(Pointer scip, String name, String value);

	/**
	 * Outputs the integer value for solver status "unknown"
	 */
	public int LibSCIPgetScipStatusUnknown();

	public int LibSCIPgetScipStatusUserInterrupt();

	public int LibSCIPgetScipStatusNodeLimit();

	public int LibSCIPgetScipStatusTotalNodeLimit();

	public int LibSCIPgetScipStatusStallNodeLimit();

	public int LibSCIPgetScipStatusTimeLimit();

	public int LibSCIPgetScipStatusMemLimit();

	public int LibSCIPgetScipStatusGapLimit();

	public int LibSCIPgetScipStatusSolLimit();

	public int LibSCIPgetScipStatusBestSolLimit();

	public int LibSCIPgetScipStatusOptimal();

	public int LibSCIPgetScipStatusInfeasible();

	public int LibSCIPgetScipStatusUnbounded();

	public int LibSCIPgetScipStatusInforunbd();
	
}
