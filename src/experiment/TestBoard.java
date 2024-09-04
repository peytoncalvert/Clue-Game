package experiment;
import java.util.HashSet;
import java.util.Set;

/*
 * TestBoard: Represents the test board
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @author Zion Alabi
 * @sources
 * @collaborators
 */
public class TestBoard {
	private TestBoardCell[][] grid;
	private Set<TestBoardCell> targets;
	private Set<TestBoardCell> visited;
	
	private static final int[][] MOVES = { // An array of possible moves a character could make moving a single space
			{0,1},  //up
			{0,-1}, //down
			{1,0},  //right 
			{-1,0}   //left
	};
	final static int COLS = 4;
	final static int ROWS = 4;

	/*
	 * Default Constructor: Initializes the grid cells and their adjLists
	 */
	public TestBoard() {
		// Initialize grid and grid cells
		grid = new TestBoardCell[ROWS][COLS];
		for(int i=0;i<ROWS;i++){
			for(int j=0;j<COLS;j++){
				grid[i][j] = new TestBoardCell(i,j);
			}
		}
		
		// Initialize cell's adjLists
		for(int i=0;i<ROWS;i++){
			for(int j=0;j<COLS;j++){
				if (i < ROWS - 1) {
					grid[i][j].addAdjacency(grid[i + 1][j]);
				}
				if (j < COLS - 1) {
					grid[i][j].addAdjacency(grid[i][j + 1]);
				}
				if (i > 0) {
					grid[i][j].addAdjacency(grid[i - 1][j]);
				}
				if (j > 0) {
					grid[i][j].addAdjacency(grid[i][j - 1]);
				}
			}
		}
		
		targets = new HashSet<TestBoardCell>();
		visited = new HashSet<TestBoardCell>();
	}

	/*
	 * calcTargets: Calculates legal targets for a move from startCell of length pathLength
	 */
	public void calcTargets(TestBoardCell startCell, int pathLength){
		visited.add(startCell);
		findTargets(startCell, pathLength);
		visited.remove(startCell);
	}

	/*
	 * findTargets: DFS helper function for finding all possible targets
	 */
	private void findTargets(TestBoardCell currentCell, int pathRemaining) {
		
		// Base case: return and add cell to targets list if rollText value reached or cell is a room cell
		if (pathRemaining == 0 || currentCell.getRoom()) {
			if (!targets.contains(currentCell)) {
				targets.add(currentCell);
			}
			return;
		}
		
		// Look in all possible directions to make cells are valid, add valid cell to visited list and call recursive case,
		// finally remove cell from visited
		for (int[] move: MOVES) {
			
			int newRow = currentCell.getRow() + move[0];
			int newCol = currentCell.getCol() + move[1];
			
			// Determine if newRow and newCol are within grid bounds and create newCell if they are
			if (newRow >= 0 && newCol >= 0 && newRow < ROWS && newCol < COLS) {
			    TestBoardCell newCell = grid[newRow][newCol];
			    
				if (!visited.contains(newCell) && !newCell.getOccupied()) {
					visited.add(newCell);
					findTargets(newCell, pathRemaining - 1);
					visited.remove(newCell);
				}
			}
			
		}
	}

	// getters
	public TestBoardCell getCell(int row, int col){
		return this.grid[row][col];
	}

	public Set<TestBoardCell> getTargets(){
		return this.targets;
	}

}
