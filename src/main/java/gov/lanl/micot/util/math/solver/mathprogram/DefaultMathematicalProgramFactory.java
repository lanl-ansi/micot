package gov.lanl.micot.util.math.solver.mathprogram;

import gov.lanl.micot.util.math.solver.integerprogram.IntegerProgramFactory;
import gov.lanl.micot.util.math.solver.linearprogram.LinearProgramFactory;


/**
 * A factory method for creating a optimization instances
 * @author Russell Bent
 */
public class DefaultMathematicalProgramFactory implements MathematicalProgramFactory {

	private static DefaultMathematicalProgramFactory INSTANCE = null;
	
	public static DefaultMathematicalProgramFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DefaultMathematicalProgramFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Constructor
	 */
	private DefaultMathematicalProgramFactory() {		
	}
	
	/**
	 * Function for creating a linear program
	 * @param flags
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public MathematicalProgram constructLinearProgram(MathematicalProgramFlags flags) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String factoryClass = flags.getString(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG);
		LinearProgramFactory factory = (LinearProgramFactory) Class.forName(factoryClass).newInstance();
		return factory.constructLinearProgram(flags);
	}
	
	/**
	 * Function for creating a linear program
	 * @param flags
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public MathematicalProgram constructIntegerProgram(MathematicalProgramFlags flags) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String factoryClass = flags.getString(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG);
		IntegerProgramFactory factory = (IntegerProgramFactory) Class.forName(factoryClass).newInstance();
		return factory.constructIntegerProgram(flags);
	}

	@Override
	public MathematicalProgram constructMathematicalProgram(MathematicalProgramFlags flags) {
		String factoryClass = flags.getString(MathematicalProgramFlags.MATHEMATICAL_PROGRAM_FACTORY_FLAG);
		MathematicalProgramFactory factory;
		try {
			factory = (MathematicalProgramFactory) Class.forName(factoryClass).newInstance();
			return factory.constructMathematicalProgram(flags);			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
