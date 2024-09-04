package tests;

/*
 * FileInitTests: Tests the initialization of config files into the grid
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.Room;

public class FileInitTests {
	// Constants that I will use to test whether the file was loaded correctly
	public static final int NUM_ROOMS = 9;
	public static final int LEGEND_SIZE = 11;
	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 25;

	// NOTE: I made Board static because I only want to set it up one
	// time (using @BeforeAll), no need to do setup before each test.
	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load BOTH config files
		board.initialize();
	}

	@Test
	public void testRoomLabels() {
		// To ensure data is correctly loaded, test retrieving a few rooms
		// from the hash, including the first and last in the file and a few others
		assertEquals("Dining Room", board.getRoom('R').getName() );
		assertEquals("Alchemy Lab", board.getRoom('L').getName() );
		assertEquals("Greenhouse", board.getRoom('G').getName() );
		assertEquals("Library", board.getRoom('Y').getName() );
		assertEquals("Throne Room", board.getRoom('T').getName() );
	}

	//test to make sure the file was loaded correctly
	@Test
	public void testLoading(){
		//first test for the correct number of rooms
		int countRooms = 0;
		for(int row=0;row<board.getNumRows();row++){
			for(int col=0;col<board.getNumColumns();col++){
				if(Board.getCell(row, col).isRoomCenter()){
					countRooms++;
				}
			}
		}
		assertTrue(countRooms == NUM_ROOMS);
		//assert that the first and last entries are correct
		//last is a walkway
		assertTrue(Board.getCell(NUM_ROWS - 1, NUM_COLUMNS - 1).getInitial() == 'W'); // Index error, reduced by one
		//first is a secret passage
		assertTrue(Board.getCell(0,0).getInitial() == 'L');
		assertTrue(Board.getCell(0,0).isSecretPassage());
		//test a wall
		assertTrue(Board.getCell(3,6).getInitial() == 'X');
	}
	//tests if the board meets expected dimensions
	@Test
	public void testDimensions(){
		assertTrue(board.getNumRows() == NUM_ROWS);
		assertTrue(board.getNumColumns() == NUM_COLUMNS);
	}

	// Test a doorway in each direction (RIGHT/LEFT/UP/DOWN), plus
	// two cells that are not a doorway.
	// These cells are white on the planning spreadsheet
	@Test
	public void fourDoorDirections() {
		BoardCell cell = Board.getCell(5, 1);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());
		cell = Board.getCell(13, 4);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
		cell = Board.getCell(1, 4);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());
		cell = Board.getCell(20, 20);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());
		// Test that walkways are not doors
		cell = Board.getCell(12, 14);
		assertFalse(cell.isDoorway());
	}

	// Test that we have the correct number of doors
	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = Board.getCell(row, col);
				if (cell.isDoorway())
					numDoors++;
			}
		Assert.assertEquals(10, numDoors);
	}


	// Test a few room cells to ensure the room initial is correct.
	@Test
	public void testRooms() {
		// just test a standard room location in Alchemy Lab
		BoardCell cell = Board.getCell(0, 1);
		Room room = Board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Alchemy Lab" ) ;
		assertFalse( cell.isLabel() );
		assertFalse( cell.isRoomCenter() ) ;
		assertFalse( cell.isDoorway()) ;

		// this is a label cell to test
		cell = Board.getCell(13, 2);
		room = Board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Greenhouse" ) ;
		assertTrue( cell.isLabel() );
		assertTrue( room.getLabelCell() == cell );

		// this is a room center cell to test
		cell = Board.getCell(14, 2);
		room = Board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Greenhouse" ) ;
		assertTrue( cell.isRoomCenter() );
		assertTrue( room.getCenterCell() == cell );

		// this is a secret passage test
		cell = Board.getCell(3, 9);
		room = Board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Throne Room" ) ;
		assertTrue( cell.getSecretPassage() == 'E' );

		// test a walkway
		cell = Board.getCell(5, 0);
		room = Board.getRoom( cell ) ;
		// Note for our purposes, walkways and closets are rooms
		assertTrue( room != null );
		assertEquals( room.getName(), "Walkway" ) ;
		assertFalse( cell.isRoomCenter() );
		assertFalse( cell.isLabel() );

		// test a closet
		cell = Board.getCell(11, 9);
		room = Board.getRoom( cell ) ;
		assertTrue( room != null );
		assertEquals( room.getName(), "Unused" ) ;
		assertFalse( cell.isRoomCenter() );
		assertFalse( cell.isLabel() );

	}
}
