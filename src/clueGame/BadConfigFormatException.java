package clueGame;

import java.io.PrintWriter;
import java.io.FileNotFoundException;
/*
 * BadConfigFormatException: An exception to handle when a Clue board configuration file provides an incorrect format
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class BadConfigFormatException extends Exception {

	private static final long serialVersionUID = 1L;

	public BadConfigFormatException() {
		super("Error: bad config format");
		errorToFile("Incorrect format produced");
	}
	
	public BadConfigFormatException(String badFormat) {
		super("Incorrect format produced:" + badFormat);
		errorToFile("Incorrect format produced:" + badFormat);
	}

	private void errorToFile(String toPrint) {
		// Tries to create a PrintWriter object for logfile.txt, write error to file, then close PrintWriter object. 
		// Otherwise prints the Exception.getMessage() value
		try {
			PrintWriter exceptionLog = new PrintWriter("data/logfile.txt");
			exceptionLog.println(toPrint);
			exceptionLog.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
