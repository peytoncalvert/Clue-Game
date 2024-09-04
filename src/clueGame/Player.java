package clueGame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/*
 * Player: Abstract class that holds the shared info and behavior of concrete player types
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public abstract class Player {
	private String name;
	private Color color;
	private int row,column;
	private ArrayList<Card> hand;
	private Set<Card> seenCards;
	private boolean wasMoved;

	public Player(String name, char color, int row, int col) {
		this.name = name;
		assignColor(color);
		this.row = row;
		this.column = col;
		hand = new ArrayList<Card>();
		seenCards = new HashSet<Card>();
		wasMoved = false;
	}

	/*
	 * updateHand: Adds the provided card to the player's hand
	 */
	public void updateHand(Card card) {
		hand.add(card);
		seenCards.add(card);
	}
	
	/*
	 * updateSeenCards: Adds the provided card to the player's seenCards set
	 */
	public void updateSeenCards(Card card) {
		if (!hand.contains(card)) {
			seenCards.add(card);
		}
		
	}

	/*
	 * assignColor: used to assign the color object based on the provided character in the ClueSetup.txt file
	 */
	private void assignColor(char color) {
		switch(color) {

		case 'y':
			this.color = Color.yellow;
			break;
		case 'b':
			this.color = Color.blue;
			break;
		case 'g':
			this.color = Color.green;
			break;
		case 'c':
			this.color = Color.cyan;
			break;
		case 'r':
			this.color = Color.red;
			break;
		case 'o':
			this.color = Color.orange;
			break;
		case 'w':
			this.color = Color.white;
			break;
		default:
			this.color = Color.black;
			break;
		}
	}
	
	/*
	 * disproveSuggestion: checks if player can disprove a suggestion
	 */
	public Card disproveSuggestion(Solution suggestion) {
		ArrayList<Card> disproves = new ArrayList<Card>();
		Card room = suggestion.getRoom();
		Card person = suggestion.getPerson();
		Card weapon = suggestion.getWeapon();
		
		if(hand.contains(room)){
			disproves.add(room);
		}
		if(hand.contains(person)){
			disproves.add(person);
		}
		if(hand.contains(weapon)){
			disproves.add(weapon);
		}

		//return null if there are no cards to disprove the suggestion
		if(disproves.size() == 0){
			return null;
		}
		
		//return a random card from 0 to the size of card found
		Random rand = new Random();
		int cardToPick = rand.nextInt(disproves.size());
		return disproves.get(cardToPick);
	}
	
	/*
	* tells each player how to draw themselves
	*/
	public void draw(Graphics g, int radius, Dimension boardOffset) {
		
		// draws a circle of player's color
		g.setColor(color);
		g.fillOval(boardOffset.width, boardOffset.height, radius, radius);
		g.drawOval(boardOffset.width, boardOffset.height, radius, radius);
	}
	
	// getters
	public String getName(){
		return this.name;
	}
	public Color getColor() {
		return this.color;
	}

	public int getRow() {

		return this.row;
	}

	public int getCol() {
		return this.column;
	}

	public ArrayList<Card> getHand() {
		return hand;
	}
	
	public Set<Card> getSeenCards() {
		return seenCards;
	}
	
	public boolean getWasMoved() {
		return wasMoved;
	}
	
	public void setCell(int row, int col) {
		this.row = row;
		this.column = col;
	}
	
	public void setWasMoved(boolean wasMoved) {
		this.wasMoved = wasMoved;
	}
}
