package clueGame;

import java.util.Set;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Map;
/*
 * BoardCell: Represents a single cell on the game board, holding the cell's information
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class BoardCell {
    private int row, col;
    private char initial, secretPassage;
    private DoorDirection doorDirection;
    private boolean isLabel, isCenter ,isRoom, isPassage, isDoorway;
    private Set<BoardCell> adjList;
	private boolean isOccupied;

    /*
    * Constructor for BoardCell
    */
    public BoardCell(int row, int col, char initial){
    	this.row = row;
    	this.col = col;
        adjList = new HashSet<BoardCell>();
		this.initial = initial;
		
		if (initial != 'X' && initial != 'W') {
			this.isRoom = true;
		}
    }

    /*
    * Adds the passed BoardCell to the adjacency list
    */
    public void addAdj(BoardCell adj) {
    	if (!adjList.contains(adj)) {
    		adjList.add(adj);
    	}
    }
	/*
	*	Defines how each BoardCell should draw itself on the board
	*/
    public void draw(Graphics g, int height, int width, Dimension boardOffset) {
    	//checks to see what type of cell a cell is and draws it accordingly
    	if (this.isRoom) {
			//rooms are light gray
    		g.setColor(Color.LIGHT_GRAY);
    		g.fillRect(boardOffset.width, boardOffset.height, width, height);
    	} else if (this.initial == 'X') {
			//borders are black
    		g.setColor(Color.BLACK);
    		g.fillRect(boardOffset.width, boardOffset.height, width, height);
    	} else if (this.isDoorway) {
			//doorways are yellow but with blue border to indicate direction
    		g.setColor(Color.YELLOW);
    		g.fillRect(boardOffset.width, boardOffset.height, width, height);
			g.setColor(Color.BLUE);
			switch (doorDirection) {
			case UP:
				g.fillRect(boardOffset.width, boardOffset.height, width, 5);
				break;
			case DOWN:
				g.fillRect(boardOffset.width, boardOffset.height + height - 5, width, 5);
				break;
			case LEFT:
				g.fillRect(boardOffset.width, boardOffset.height, 5, height);
				break;
			case RIGHT:
				g.fillRect(boardOffset.width + width - 5, boardOffset.height, 5, height);
				break;
			default:
				break;
			}
    	} else {
			//otherwise its a walkway
    		g.setColor(Color.YELLOW);
    		g.fillRect(boardOffset.width, boardOffset.height, width, height);
    		g.setColor(Color.BLACK);
    	}
    	//check for secret passage
    	if(this.isSecretPassage()){
			g.setColor(Color.RED);
			g.fillRect(boardOffset.width, boardOffset.height, width, height);
		} 
		g.drawRect(boardOffset.width, boardOffset.height, width, height);
	}
    
    /*
     * 
     */
    public void draw(Graphics g, int height, int width, Dimension boardOffset, Color color) {
    	g.setColor(color);
    	g.fillRect(boardOffset.width, boardOffset.height, width, height);
    	g.drawRect(boardOffset.width, boardOffset.height, width, height);
    }
    
    /*
     * drawRoomLabels: draw the room name over the label cell
     */
    public void drawRoomLabels(Graphics g, Map<Character, Room> roomMap, Dimension boardOffset) {
    	g.setColor(Color.BLUE);
    	g.drawString(roomMap.get(initial).getName().substring(0,5)+".", boardOffset.width, boardOffset.height + 15);
    }
    // getters and setters
    public Set<BoardCell> getAdjList(){
    	return adjList;
    }
    
	public boolean isDoorway(){
		return this.isDoorway;
	}

	public DoorDirection getDoorDirection() {
		return this.doorDirection;
	}

	public boolean isLabel() {
		return this.isLabel;
	}

	public boolean isRoomCenter() {
		return this.isCenter;
	}
	
	public boolean isSecretPassage() {
		return this.isPassage;
	}
	
	public boolean isRoom() {
		return this.isRoom;
	}
	
	public boolean isOccupied() {
		return isOccupied;
	}
	
	public char getSecretPassage() {
		return this.secretPassage;
	}
	
	public char getInitial() {
		return this.initial;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public int getCol() {
		return this.col;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public void setRoomCenter(boolean isRoomCenter) {
		this.isCenter = isRoomCenter;
	}
	public void setRoomLabel(boolean isRoomLabel) {
		this.isLabel = isRoomLabel;
	}
	
	public void setDoorway(boolean isDoorway, DoorDirection direction) {
		this.isDoorway = isDoorway;
		this.doorDirection = direction;
	}
	
	public void setPassage(boolean isPassage, char secretPassage) {
		this.isPassage = isPassage;
		this.secretPassage = secretPassage;
	}
}
