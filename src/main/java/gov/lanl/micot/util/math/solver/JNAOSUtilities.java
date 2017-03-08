package gov.lanl.micot.util.math.solver;

/**
 * The idea for this class comes from http://stackoverflow.com/a/18417382/1269634. This facilitates easy OS detection.
 * 
 * @author gfairchild
 */
public final class JNAOSUtilities {
	public enum OSType {
		Windows32, Windows64, Mac, Linux, Other
	};

	// cached result of OS detection
	private static OSType detectedOS;

	private JNAOSUtilities() {
		// DO NOT INSTANTIATE
	}

	/**
	 * Get the operating system type (including architecture if under Windows).
	 * 
	 * @return {@link OSType}
	 */
	public static OSType getOSType() {
		if (detectedOS == null) {
			String osName = System.getProperty("os.name").toLowerCase();

			if (osName.contains("mac")) {
				detectedOS = OSType.Mac;
			} else if (osName.contains("win")) {
				// decide between 32-bit and 64-bit Windows
				// note that os.arch returns the architecture of the JVM, *not* of the Windows install
				// e.g., if you call this code from 64-bit Windows using a 32-bit JVM, this code will select Windows32
				String architecture = System.getProperty("os.arch");
				detectedOS = architecture.equals("x86") ? OSType.Windows32 : OSType.Windows64;
			} else if (osName.contains("nux")) {
				detectedOS = OSType.Linux;
			} else {
				detectedOS = OSType.Other;
			}
		}

		return detectedOS;
	}

	/**
	 * Get the OS/architecture-specific directory where libraries will live.
	 * 
	 * @param osType
	 *            {@link OSType}
	 * @return
	 */
	public static String getOSDirectory(OSType osType) {
		switch (osType) {
			case Windows32:
				return "Win32";
			case Windows64:
				return "Win64";
			case Mac:
				return "Mac";
			case Linux:
				return "Linux";
			case Other:
			default:
				throw new RuntimeException("Your OS (" + System.getProperty("os.name") + ") is not yet supported.");
		}
	}

	/**
	 * Get the OS/architecture-specific directory where libraries will live.
	 * 
	 * @return
	 */
	public static String getOSDirectory() {
		return getOSDirectory(getOSType());
	}
}