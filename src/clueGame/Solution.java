package clueGame;

import java.util.Objects;

/*
 * Solution: Holds the solution information for the current game
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class Solution {
    private Card room, person, weapon;
    
    public Solution(Card room, Card person, Card weapon) {
    	this.room = room;
    	this.person = person;
    	this.weapon = weapon;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Solution other = (Solution) obj;
		return Objects.equals(person, other.person) && Objects.equals(room, other.room)
				&& Objects.equals(weapon, other.weapon);
	}

	// getters and setters
    public Card getRoom() {
        return room;
    }
    public Card getPerson(){
        return person;
    }
    public Card getWeapon(){
        return weapon;
    }
    
    public void setRoom(Card room) {
    	this.room = room;
    }
    
    public void setPerson(Card person){
        this.person = person;
    }
    public void setWeapon(Card weapon){
        this.weapon = weapon;
    }
}
