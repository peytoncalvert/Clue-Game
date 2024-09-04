package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import experiment.TestBoard;
import experiment.TestBoardCell;

/*
 * BoardTestsExp: Tests the board experiments
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @author Zion Alabi
 * @sources
 * @collaborators
 */
class BoardTestsExp {
	TestBoard board;

	@BeforeEach
	void setUp() throws Exception { 
		board = new TestBoard();
	}

	/*
	 * testAdjList: Tests the creation of adjacency list for a 4x4 board
	 */
	@Test
	public void testAdjList() {
		// Retrieve test cells and create adjacency list objects
		TestBoardCell topLCorner = board.getCell(0, 0);
		TestBoardCell botRCorner = board.getCell(3, 3);
		TestBoardCell rEdge = board.getCell(1, 3);
		TestBoardCell botLCorner = board.getCell(3, 0);
		TestBoardCell midGrid = board.getCell(2, 2);

		Set<TestBoardCell> testList = topLCorner.getAdjList();
		Set<TestBoardCell> testList2 = botRCorner.getAdjList();
		Set<TestBoardCell> testList3 = rEdge.getAdjList();
		Set<TestBoardCell> testList4 = botLCorner.getAdjList();
		Set<TestBoardCell> testList5 = midGrid.getAdjList();
		
		// Test topLCorner adjacent list
		assertTrue(testList.contains(board.getCell(1,  0)));
		assertTrue(testList.contains(board.getCell(0,  1)));
		assertEquals(2, testList.size());
		
		// Test botRCorner adjacent list
		assertTrue(testList2.contains(board.getCell(2,  3)));
		assertTrue(testList2.contains(board.getCell(3,  2)));
		assertEquals(2, testList2.size());
		
		// Test rEdge adjacent list
		assertTrue(testList3.contains(board.getCell(0,  3)));
		assertTrue(testList3.contains(board.getCell(2,  3)));
		assertTrue(testList3.contains(board.getCell(1,  2)));
		assertEquals(3, testList3.size());
		
		// Test lEdge adjacent list
		assertTrue(testList4.contains(board.getCell(2,  0)));
		assertTrue(testList4.contains(board.getCell(3,  1)));
		assertEquals(2, testList4.size());
		
		// Test midGrid adjacent list
		assertTrue(testList5.contains(board.getCell(1,  2)));
		assertTrue(testList5.contains(board.getCell(2,  1)));
		assertTrue(testList5.contains(board.getCell(3,  2)));
		assertTrue(testList5.contains(board.getCell(2,  3)));
		assertEquals(4, testList5.size());
		
	}
	
	/*
	 * Test targets with several rolls and start locations
	 */
	@Test
	public void testTargetsNormal() {
		// Tests for rollText of 3
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(3, 0)));
		assertTrue(targets.contains(board.getCell(2, 1)));
		assertTrue(targets.contains(board.getCell(0, 1)));
		assertTrue(targets.contains(board.getCell(1, 2)));
		assertTrue(targets.contains(board.getCell(0, 3)));
		assertTrue(targets.contains(board.getCell(1, 0)));
		
		//tests for rollText of 2
		targets.clear(); // Add this to clear targets before recalculating them
		cell = board.getCell(1, 1);
		board.calcTargets(cell, 2);
		targets = board.getTargets();
		
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(3, 1)));
		assertTrue(targets.contains(board.getCell(1, 3)));
		assertTrue(targets.contains(board.getCell(0, 0)));
		assertTrue(targets.contains(board.getCell(2, 2)));
		assertTrue(targets.contains(board.getCell(2, 0)));
		assertTrue(targets.contains(board.getCell(0, 2)));
		
		// Tests for rollText of 6
		targets.clear(); // Add this to clear targets before recalculating them
		cell = board.getCell(2, 2);
		board.calcTargets(cell, 6);
		targets = board.getTargets();
		
		assertEquals(7, targets.size()); // Size should be 7 not 6
		assertTrue(targets.contains(board.getCell(0, 2)));
		assertTrue(targets.contains(board.getCell(2, 0)));
		assertTrue(targets.contains(board.getCell(3, 3)));
		assertTrue(targets.contains(board.getCell(3, 1)));
		assertTrue(targets.contains(board.getCell(1, 3)));
		assertTrue(targets.contains(board.getCell(0, 0)));
		assertTrue(targets.contains(board.getCell(1, 1))); // Added assert to make sure a target is here as well
		
	}
	
	/*
	 * Tests targets where a cell is a room
	 */
	@Test
	public void testTargetsRoom() {
		// Set up room cell
		board.getCell(1, 2).setRoom(true);
		TestBoardCell cell = board.getCell(0, 3);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		
		assertEquals(5, targets.size()); // Size should 5 not 3. A room tile does not mean we can't move anywhere
		assertTrue(targets.contains(board.getCell(0, 0))); // Add assertTrue for (0, 0) case
		assertTrue(targets.contains(board.getCell(1, 1))); // Add assertTrue for (1, 1) case
		assertTrue(targets.contains(board.getCell(1, 2)));
		assertTrue(targets.contains(board.getCell(2, 2)));
		assertTrue(targets.contains(board.getCell(3, 3)));
		// Remove assertTrue for (0,2) because it should not be able to step here
	}
	
	/*
	 * Test targets where a cell is occupied
	 */
	@Test
	public void testTargetsOccupied() {
		// Set up occupied cell
		board.getCell(0, 2).setOccupied(true);
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 2);
		Set<TestBoardCell> targets = board.getTargets();
		
		assertEquals(2, targets.size()); // Size should be 2 not 4
		assertTrue(targets.contains(board.getCell(1, 1)));
		assertTrue(targets.contains(board.getCell(2, 0)));
		// Remove assertTrue for (0,0) because it should not be able to return there
		assertFalse(targets.contains(board.getCell(0, 2)));
	}
	
	/*
	 * Tests targets where a cell represents a room and a cell occupied by an opponent
	 */
	@Test
	public void testTargetsMixed() {
		// Set up occupied cells and calculate targets
		board.getCell(0, 2).setOccupied(true);
		board.getCell(1, 2).setRoom(true);
		TestBoardCell cell = board.getCell(0, 3);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(1, 2)));
		assertTrue(targets.contains(board.getCell(2, 2)));
		assertTrue(targets.contains(board.getCell(3, 3)));
	}
}
