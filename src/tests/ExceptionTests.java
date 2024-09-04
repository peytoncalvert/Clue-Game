package tests;

/*
 * ExceptionTests: Tests the custom exception BadConfigFormatException is properly used and working
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import clueGame.BadConfigFormatException;
import clueGame.Board;

class ExceptionTests {

	// Test that an exception is thrown for a layout file that does not
	// have the same number of columns for each row
	@Test
	public void testBadColumns() throws BadConfigFormatException, FileNotFoundException {
		assertThrows(BadConfigFormatException.class, () -> {
			// Note that we are using a LOCAL Board variable, because each
			// test will load different files
			Board board = Board.getInstance();
			board.setConfigFiles("ClueLayoutBadColumns.csv", "ClueSetup.txt");
			// Instead of initialize, we call the two load functions directly.
			// This is necessary because initialize contains a try-catch.
			board.loadSetupConfig();
			// This one should throw an exception
			board.loadLayoutConfig();
		});
	}

	// Test that an exception is thrown for a Layout file that specifies
	// a room that is not in the legend. 
	@Test
	public void testBadRoom() throws BadConfigFormatException, FileNotFoundException {
		assertThrows(BadConfigFormatException.class, () -> {
			Board board = Board.getInstance();
			board.setConfigFiles("ClueLayoutBadRoom.csv", "ClueSetup.txt");
			board.loadSetupConfig();
			board.loadLayoutConfig();
		});
	}

	// Test that an exception is thrown for a bad format Setup file
	@Test
	public void testBadRoomFormat() throws BadConfigFormatException, FileNotFoundException {
		assertThrows(BadConfigFormatException.class, () -> {
			Board board = Board.getInstance();
			board.setConfigFiles("ClueLayout.csv", "ClueSetupBadFormat.txt");
			board.loadSetupConfig();
			board.loadLayoutConfig();
		});
	}


}
