import java.io.PrintStream;

/** Used to make printing multi-line strings work properly on all platforms. */
public interface MultilinePrinter {
	public static final String NEW_LINE= System.getProperty("line.separator"); // \n or \r\n
	
	/** Prints a new line to stream. */
	default void println(PrintStream stream) {
		stream.println();
	}
	
	/** Print text plus \n to stream with appropriate interpretation of \n character.
	 *  Assume text has no occurrences of \r. */
	default void println(PrintStream stream, Object text) {
		if (text == null)
			stream.println("null");
		stream.println(text.toString().replaceAll("\n", NEW_LINE));
	}

	/** Print text to stream with appropriate interpretation of \n character.
	 *  Assume text has no occurrences of \r. */
	default void print(PrintStream stream, Object text) {
		if (text == null)
			stream.println("null");
		stream.print(text.toString().replaceAll("\n", NEW_LINE));
	}
}
