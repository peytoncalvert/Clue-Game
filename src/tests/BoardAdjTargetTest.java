package tests;

/*
 * BoardAdjTargetTests: Tests the adjacency and target lists are properly populated
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

class BoardAdjTargetTest {

	private static Board board;

	@BeforeAll
	public static void setUp() {
		
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");		
		// Initialize will load config files 
		board.initialize();
	}

	// Ensure that the generic room cells have an empty adjList and center cells have a few adjacent cells
	@Test
	public void testAdjacenciesRooms()
	{
		// Test a couple of non-center room cells
		// First, a Dining Room cell
		Set<BoardCell> testList= new HashSet<BoardCell>();
		testList.clear();
		testList = board.getAdjList(17, 11);
		assertEquals(0, testList.size());
		assertFalse(testList.contains(Board.getCell(12, 11)));
		assertFalse(testList.contains(Board.getCell(17, 12)));

		// now test the Greenhouse
		testList.clear();
		testList = board.getAdjList(16, 2);
		assertEquals(0, testList.size());
		assertFalse(testList.contains(Board.getCell(15, 2)));
		assertFalse(testList.contains(Board.getCell(17, 2)));
		assertFalse(testList.contains(Board.getCell(16, 1)));
		assertFalse(testList.contains(Board.getCell(16, 3)));

		// one more room, the Throne Room
		testList.clear();
		testList = board.getAdjList(3, 10);
		assertEquals(0, testList.size());
		assertFalse(testList.contains(Board.getCell(3, 9)));
		assertFalse(testList.contains(Board.getCell(3, 11)));
		assertFalse(testList.contains(Board.getCell(4, 10)));

		// The Enchantment Tower that only has a single door but a secret room
		testList.clear();
		testList = board.getAdjList(3, 4);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(Board.getCell(1, 4)));
		assertTrue(testList.contains(Board.getCell(6, 10)));

		// The Armory with just a door
		testList.clear();
		testList = board.getAdjList(7, 20);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(Board.getCell(13, 21)));
	}

	// Ensure door locations include their rooms and also additional walkways
	// These cells are LIGHT ORANGE on the planning spreadsheet
	@Test
	public void testAdjacencyDoor()
	{
		Set<BoardCell> testList = board.getAdjList(5, 1);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(Board.getCell(5, 0)));
		assertTrue(testList.contains(Board.getCell(6, 1)));
		assertTrue(testList.contains(Board.getCell(5, 2)));
		assertTrue(testList.contains(Board.getCell(2, 0)));

		testList = board.getAdjList(13, 21);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(Board.getCell(13, 20)));
		assertTrue(testList.contains(Board.getCell(13, 22)));
		assertTrue(testList.contains(Board.getCell(7, 20)));

		testList = board.getAdjList(14, 4);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(Board.getCell(13, 4)));
		assertTrue(testList.contains(Board.getCell(14, 5)));
		assertTrue(testList.contains(Board.getCell(15, 4)));
		assertTrue(testList.contains(Board.getCell(14, 2)));
	}

	// Test a variety of walkway scenarios
	// These tests are Dark Orange on the planning spreadsheet
	@Test
	public void testAdjacencyWalkways()
	{
		// Test on double door bottom
		Set<BoardCell> testList = board.getAdjList(14, 4);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(Board.getCell(15, 4)));
		assertTrue(testList.contains(Board.getCell(14, 5)));
		assertTrue(testList.contains(Board.getCell(13, 4))); 
		assertTrue(testList.contains(Board.getCell(14, 2))); 

		// Test near a door but not adjacent
		testList = board.getAdjList(22, 20); 
		assertEquals(3, testList.size());
		assertTrue(testList.contains(Board.getCell(21, 20))); 
		assertTrue(testList.contains(Board.getCell(23, 20)));
		assertTrue(testList.contains(Board.getCell(22, 19))); 

		// Test adjacent to walkways
		testList = board.getAdjList(23, 8);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(Board.getCell(22, 8))); 
		assertTrue(testList.contains(Board.getCell(23, 7))); 
		assertTrue(testList.contains(Board.getCell(24, 8)));
		assertTrue(testList.contains(Board.getCell(23, 9))); 

		// Test next to wall
		testList = board.getAdjList(20, 10);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(Board.getCell(21, 10)));
		assertTrue(testList.contains(Board.getCell(19, 10))); 

		// Test near a door but not adjacent in corner between walls
		testList = board.getAdjList(3, 13); 
		assertEquals(2, testList.size());
		assertTrue(testList.contains(Board.getCell(4, 13))); 
		assertTrue(testList.contains(Board.getCell(3, 12))); 
	}

	/*
	 * testEachEdge: test each edge of the board
	 */
	@Test
	public void testEachEdge() {
		// Top
		Set<BoardCell> testList = board.getAdjList(0, 12);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(Board.getCell(1, 12)));
		assertTrue(testList.contains(Board.getCell(0, 11)));
		assertTrue(testList.contains(Board.getCell(0, 13)));

		// Bottom
		testList = board.getAdjList(25, 20);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(Board.getCell(25, 19)));
		assertTrue(testList.contains(Board.getCell(25, 21)));
		assertTrue(testList.contains(Board.getCell(24, 20)));

		// Left
		testList = board.getAdjList(14, 0);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(Board.getCell(15, 0)));
		assertTrue(testList.contains(Board.getCell(13, 0)));

		// Right
		testList = board.getAdjList(19, 24);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(Board.getCell(20, 24)));
		assertTrue(testList.contains(Board.getCell(18, 24)));

	}

	// Tests out of room center, 1, 3 and 4
	// These are LIGHT BLUE on the planning spreadsheet
	@Test
	public void testTargetsAtDoor() {
		// test a rollText of 1, at door
		board.calcTargets(Board.getCell(13, 4), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(Board.getCell(14, 2)));
		assertTrue(targets.contains(Board.getCell(12, 4)));	
		assertTrue(targets.contains(Board.getCell(13, 5)));
		assertTrue(targets.contains(Board.getCell(14, 4)));

		// test a rollText of 4
		board.calcTargets(Board.getCell(13, 4), 4);
		targets= board.getTargets();
		assertEquals(9, targets.size());
		assertTrue(targets.contains(Board.getCell(11, 4)));
		assertTrue(targets.contains(Board.getCell(12, 5)));
		assertTrue(targets.contains(Board.getCell(15, 4)));	
		assertTrue(targets.contains(Board.getCell(17, 4)));	
		assertTrue(targets.contains(Board.getCell(10, 5)));	}

	@Test
	public void testTargetsInWalkway() {
		// test a rollText of 1
		board.calcTargets(Board.getCell(21, 17), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(Board.getCell(21, 18)));
		assertTrue(targets.contains(Board.getCell(21, 16)));	
		assertTrue(targets.contains(Board.getCell(22, 17)));
		assertTrue(targets.contains(Board.getCell(20, 17)));	
		//test a rollText of 2
		board.calcTargets(Board.getCell(21, 17), 2);
		targets = board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(Board.getCell(21, 19)));
		assertTrue(targets.contains(Board.getCell(21, 15)));	
		assertTrue(targets.contains(Board.getCell(20, 16)));
		assertTrue(targets.contains(Board.getCell(22, 18)));
	}

	@Test
	// test to make sure targetLists are correct when exiting rooms with and without a secret passage
	public void testTargetsSecretPassage() {
		// test a rollText of 1 with a secret passage
		board.calcTargets(Board.getCell(2, 0), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(Board.getCell(18, 22)));
		assertTrue(targets.contains(Board.getCell(5, 1)));

		// test a rollText of 1 without a secret passage
		board.calcTargets(Board.getCell(17, 8), 1);
		targets = board.getTargets();
		assertEquals(1, targets.size());
		assertTrue(targets.contains(Board.getCell(13, 8)));

		// test a rollText of 3 with a secret passage
		board.calcTargets(Board.getCell(6, 10), 3);
		targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(Board.getCell(3, 4)));
		assertTrue(targets.contains(Board.getCell(10, 8)));
		assertTrue(targets.contains(Board.getCell(10, 12)));

		// test a rollText of 3 without a secret passage
		board.calcTargets(Board.getCell(14, 2), 3);
		targets = board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(Board.getCell(15, 5)));
		assertTrue(targets.contains(Board.getCell(14, 5)));
		assertTrue(targets.contains(Board.getCell(13, 5)));
		assertTrue(targets.contains(Board.getCell(12, 5)));
		assertTrue(targets.contains(Board.getCell(16, 4)));
		assertTrue(targets.contains(Board.getCell(15, 4)));
		assertTrue(targets.contains(Board.getCell(12, 4)));
		assertTrue(targets.contains(Board.getCell(11, 4)));
		
		// test a rollText of 6 without a secret passage
		board.calcTargets(Board.getCell(7, 20), 6);
		targets = board.getTargets();
		assertEquals(14, targets.size());
		assertTrue(targets.contains(Board.getCell(17, 20)));
		assertTrue(targets.contains(Board.getCell(10, 23)));
		assertTrue(targets.contains(Board.getCell(15, 24)));
		assertTrue(targets.contains(Board.getCell(11, 18)));
	}

	@Test
	// test to make sure occupied locations do not cause problems
	public void testTargetsOccupied() {
		// test a rollText of 4 on all sides
		Board.getCell(13, 20).setOccupied(true);
		Board.getCell(13, 22).setOccupied(true);
		board.calcTargets(Board.getCell(13, 21), 1);
		Board.getCell(13, 20).setOccupied(false);
		Board.getCell(13, 22).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(1, targets.size());
		assertTrue(targets.contains(Board.getCell(7, 20)));

		// test inside castle with rollText of 2
		Board.getCell(13, 12).setOccupied(true);
		board.calcTargets(Board.getCell(13, 10), 2);
		Board.getCell(13, 12).setOccupied(false);
		targets = board.getTargets();
		assertEquals(0, targets.size());
		assertFalse(targets.contains(Board.getCell(13, 8)));
		assertFalse(targets.contains(Board.getCell(15, 10)));
	}

}
