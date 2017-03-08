package gov.lanl.micot.infrastructure.ep.io.pfw;

import java.util.Vector;

import gov.lanl.micot.infrastructure.ep.model.pfw.PFWHeader;

/**
 * This is a factory for creating default pfw file headers
 * 
 * @author Russell Bent
 */
public class DefaultPFWHeaderFactory {

	private static DefaultPFWHeaderFactory	instance	= null;

	/**
	 * Singleton
	 * @return
	 */
	public static DefaultPFWHeaderFactory getInstance() {
		if (instance == null) {
			instance = new DefaultPFWHeaderFactory();
		}
		return instance;
	}

	/**
	 * Constructor
	 */
	private DefaultPFWHeaderFactory() {
	}

	public PFWHeader createDefaultPFWFileHeader() {
		Vector<String> headerLines = new Vector<String>();
		headerLines.add("PSAD 5.11");
		headerLines.add("HEADER CASE_IDENTIFICATION, TITLE1:255:T, TITLE2:255:T, TITLE3:255:T, TITLE4:255:T, TITLE5:255:T, VERSION:5:T");
		headerLines.add("\"WESTERN ELECTRICITY COORDINATING COUNCIL                                        \",");
		headerLines.add("\"2016 HS1A APPROVED BASE CASE  \",");
		headerLines.add("\"                                        \",");
		headerLines.add("\"\",");
		headerLines.add("\"\",");
		headerLines.add("0.0");
		headerLines.add("HEADER PARAMS_R, NAME:20:T, VALUE:8:R");
		headerLines.add("\"PTOL                \", 0.10000");
		headerLines.add("\"QTOL                \", 0.10000");
		headerLines.add("\"VMAXPU              \", 1.20000");
		headerLines.add("\"VMINPU              \", 0.88000");
		headerLines.add("\"FTOL                \", 1.00000");
		headerLines.add("\"VTOL                \", 0.00500");
		headerLines.add("\"BASEMVA             \", 100.000");
		headerLines.add("\"LZTHRES             \", 0.00010");
		headerLines.add("\"INFTOL              \", 1000000");
		headerLines.add("\"MVATOL              \", 0.01000");
		headerLines.add("HEADER PARAMS_I, NAME:20:T, VALUE:8:I");
		headerLines.add("\"DCTAP               \",       1");
		headerLines.add("\"GCD                 \",       1");
		headerLines.add("\"MAXITER             \",     100");
		headerLines.add("\"SVD                 \",       1");
		headerLines.add("HEADER PARAMS_B, NAME:20:T, VALUE:1:T");
		headerLines.add("\"FLATSTART           \",F");
		headerLines.add("\"AREACON             \",F");
		headerLines.add("\"VARCON              \",F");
		headerLines.add("\"RMTCON              \",T");
		headerLines.add("\"XFCON               \",T");
		headerLines.add("\"XVCON               \",F");
		headerLines.add("HEADER T2KPF_PARAMS_R, NAME:20:T, VALUE:8:R");
		headerLines.add("\"QDAMP               \", 0.80000");
		headerLines.add("\"PCTLTOL             \", 10.0000");
		headerLines.add("\"QCTLTOL             \", 10.0000");
		headerLines.add("\"BADXR               \", 0.50000");
		headerLines.add("\"RMTTOL              \", 0.08000");
		headerLines.add("\"RATE_FACTOR         \", 1.00000");
		headerLines.add("\"MAXACHANGE          \", 1.00000");
		headerLines.add("\"MAXVCHANGE          \", 0.50000");
		headerLines.add("\"AMP_FACTOR          \", 1.00000");
		headerLines.add("\"BQB0_FACTOR         \", 1.00000");
		headerLines.add("\"VCHANGEPU           \", 0.05000");
		headerLines.add("\"OLIMIT              \", 1.00000");
		headerLines.add("HEADER T2KPF_PARAMS_I, NAME:20:T, VALUE:8:I");
		headerLines.add("\"CTRLITER            \",     100");
		headerLines.add("\"PAGESIZE            \",      60");
		headerLines.add("\"SCREENSIZE          \",      24");
		headerLines.add("\"PAGENO              \",       0");
		headerLines.add("\"OL_OPTION           \",       0");
		headerLines.add("\"TAPITER             \",       5");
		headerLines.add("\"GENITER             \",       5");
		headerLines.add("\"RATENUMBER          \",       0");
		headerLines.add("\"RMTITER             \",     100");
		headerLines.add("HEADER T2KPF_PARAMS_B, NAME:20:T, VALUE:1:T");
		headerLines.add("\"SOLVEDEBUG          \",F");
		headerLines.add("\"AREADEBUG           \",F");
		headerLines.add("\"VARDEBUG            \",F");
		headerLines.add("\"RMTDEBUG            \",F");
		headerLines.add("\"XFDEBUG             \",F");
		headerLines.add("\"XVDEBUG             \",F");
		headerLines.add("\"SMOOTHSTEP          \",F");
		headerLines.add("\"OUTINCLUDE          \",F");
		headerLines.add("\"XFSKIP              \",F");
		headerLines.add("\"PRINTLOSS           \",T");
		headerLines.add("\"PRINTRATE           \",T");
		headerLines.add("\"ECHO                \",T");
		headerLines.add("\"SHOWMVA             \",T");
		headerLines.add("\"PRINTMAG            \",F");
		headerLines.add("\"PRINTTAP            \",F");
		headerLines.add("\"PRINTRX             \",F");
		headerLines.add("\"BYNAME              \",T");
		headerLines.add("\"DATA_ERROR          \",F");
		headerLines.add("\"BX_OPTION           \",F");
		headerLines.add("\"SHOWTITLES          \",F");
		headerLines.add("\"LOCALREMOTES        \",F");
		headerLines.add("\"BYREACTIVEPOWER     \",F");
		headerLines.add("\"BYREALPOWER         \",T");
		headerLines.add("\"BYFLOWS             \",F");
		headerLines.add("\"BYNOORDER           \",F");
		headerLines.add("\"BYRATING            \",F");
		headerLines.add("\"BYPERCENT           \",T");
		headerLines.add("HEADER MISC_PARAMS_I, NAME:20:T, VALUE:8:I");
		headerLines.add("\"SELECTED_SET        \",       7");
		headerLines.add("HEADER MISC_PARAMS_B, NAME:20:T, VALUE:1:T");
		headerLines.add("\"SOLVED              \",T");
		headerLines.add("\"BY_DSOLVE           \",T");
		headerLines.add("\"SHORTCUTS           \",F");
		headerLines.add("\"USEDIALOGS          \",F");
		headerLines.add("\"SCHEDULE_REMOTES    \",F");
		headerLines.add("\"OLD_FLATSTART       \",F");
		headerLines.add("\"SWITCHEDSHUNTSSMOOTH\",F");
		headerLines.add("\"SS_DEBUG            \",F");
		headerLines.add("HEADER SET_DEFINITIONS, DEFINITION:255:T");

		return new PFWHeader(headerLines);
	}

}
