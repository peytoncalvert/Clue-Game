package clueGame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/*
 * ComputerPlayer: Represents the computer players and their behavior
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class ComputerPlayer extends Player {

	public ComputerPlayer(String name, char color, int row, int col) {
		super(name,color,row,col);
	}

	/*
	 * createSuggestion: Computer player checks what cards it has not seen to create a suggestion after entering a room
	 */
	public Solution createSuggestion(Room room) {
		// check if room is room entered
		if (room.getCenterCell().getInitial() != Board.getCell(this.getRow(), this.getCol()).getInitial()) {
			return null;
		}

		ArrayList<Card> weaponsNotSeen = new ArrayList<Card>();
		ArrayList<Card> peopleNotSeen = new ArrayList<Card>();
		ArrayList<Card> fullDeck = Board.getInstance().getFullDeck();
		// create lists of weapons and people not seen
		for(int i=0;i<fullDeck.size();i++){
			if (!this.getSeenCards().contains(fullDeck.get(i)) && fullDeck.get(i).getCardType() == CardType.WEAPON) {
				weaponsNotSeen.add(fullDeck.get(i));
			}
			if (!this.getSeenCards().contains(fullDeck.get(i)) && fullDeck.get(i).getCardType() == CardType.PERSON) {
				peopleNotSeen.add(fullDeck.get(i));
			}		
		}

		// randomly choose a person and a weapon from the lists created
		Random rand = new Random();

		int weaponInd = rand.nextInt(weaponsNotSeen.size());
		int personInd = rand.nextInt(peopleNotSeen.size());

		// loop over deck to find card based on provided room name
		for(int i=0;i<fullDeck.size();i++){
			if (fullDeck.get(i).getCardName().equals(room.getName())) {
				return new Solution(fullDeck.get(i), peopleNotSeen.get(personInd), weaponsNotSeen.get(weaponInd));
			}
		}
		return null;
	}
	
	/*
	 * selectTarget: Computer player mostly randomly selects a target to move to
	 */
	public BoardCell selectTarget(Set<BoardCell> targets) {
		//check for unvisited room
		boolean isSeen = false;
		for (BoardCell target: targets) {
			if (target.getInitial() != 'W') {
				for (Card card: this.getSeenCards()) {
					if (card.getInitial() == target.getInitial()) {
						isSeen = true;
					}
				}
				if (!isSeen) {
					return target;
				}
			}
		}

		//if not pick random
		Random rand = new Random();
		int rand1 = rand.nextInt(targets.size());
		int counter = 0;
		for (BoardCell target: targets) {
			if (counter == rand1) {
				return target;
			}
			counter++;
		}

		return targets.iterator().next();
	}

	public Solution makeAccusation() {

		return null;
	}
}
