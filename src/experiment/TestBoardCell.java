package experiment;

import java.util.HashSet;
import java.util.Set;

/*
 * TestBoardCell: Represents one test cell on the grid
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @author Zion Alabi
 * @sources
 * @collaborators
 */
public class TestBoardCell {
	private int cellRow,cellCol;
	private boolean isRoom, isOccupied;

	private Set<TestBoardCell> adjList;

	public TestBoardCell(int cellRow, int cellCol) {
		this.cellRow = cellRow;
		this.cellCol = cellCol;
		isRoom = false;
		isOccupied = false;
		adjList = new HashSet<TestBoardCell>();
	}

	// getters and setters
	public int getRow() {
		return cellRow;
	}

	public int getCol() {
		return cellCol;
	}
	
	public void addAdjacency(TestBoardCell cell) {
		if (!adjList.contains(cell)) {
			adjList.add(cell);
		}
	}

	public Set<TestBoardCell> getAdjList() {
		return adjList;
	}

	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}
	public boolean getRoom(){
		return isRoom;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}
	public boolean getOccupied(){
		return isOccupied;
	}

}
