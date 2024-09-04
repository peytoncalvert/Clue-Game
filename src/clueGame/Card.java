package clueGame;
/*
 * Card: Represents a game card
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class Card {
    private String cardName;
    private CardType cardType;
    private Player owner;

    public Card(String name, CardType cardType){
        this.cardName = name;
        this.cardType = cardType;
    }
    
    /*
     * equals: return true if the target's name is equal to this card's name
     */
    public boolean equals(Card target) {
    	if (target.getCardName() == this.cardName) {
    		return true;
    	}
        return false;
    }
    
    // getters
    public String getCardName() {
    	return cardName;
    }
    
	public CardType getCardType() {
		
		return cardType;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public char getInitial(){
		switch(cardName) {
		case "Library":
			// code block
			return 'Y';
		default:
			return cardName.charAt(0);
			// code block
		}
	}
	
    //setters
    public void setOwner(Player owner){
        this.owner = owner;
    }
}
