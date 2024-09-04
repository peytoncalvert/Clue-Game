package clueGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/*
 * Room: Represents a room on the clue board
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class Room {
    private String name;
    private BoardCell centerCell;
    private BoardCell labelCell;
    private BoardCell passageCell;
    private ArrayList<BoardCell> doors;
    private Set<Player> playersInRoom;
    
    public Room(String name) {
    	this.name = name;
    	passageCell = null;
        doors = new ArrayList<BoardCell>();
        playersInRoom = new HashSet<Player>();
    }
    
    // getters and setters
    public String getName() {
    	return this.name;
    }

	public BoardCell getLabelCell() {
		return this.labelCell;
	}
    public BoardCell getCenterCell(){
        return this.centerCell;
    }
    
    public BoardCell getPassageCell() {
    	return this.passageCell;
    }
    
    public ArrayList<BoardCell> getDoorList() {
    	return this.doors;
    }
    
    public Set<Player> getPlayersInRoom() {
    	return playersInRoom;
    }
    
    public void setLabelCell(BoardCell label){
        this.labelCell = label;
    }
    
    public void setCenterCell(BoardCell center){
        this.centerCell = center;
    }
    
    public void setPassageCell(BoardCell passage) {
    	this.passageCell = passage;
    }
    
    public void setDoorCell(BoardCell door) {
    	if (!doors.contains(door)) {
    		doors.add(door);
    	}
    	
    }
}
