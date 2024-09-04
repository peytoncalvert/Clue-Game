package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.Solution;

/*
 * ComputerAITest: Test the computer's capability to make suggestions and select targets
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */


//test for selectTargets() and createSuggestion() 
class ComputerAITest {
	private static Board board;
	
	
	@BeforeAll
	public static void setup(){
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");		
		// Initialize will load config files 
		board.initialize();
		
	}

	/*
	 * testCreateSuggestion: tests the computer player's capability to make a suggestion
	 	Room matches current location
		If only one weapon not seen, it's selected
		If only one person not seen, it's selected
		
	 */
	@Test
	public void testCreateSuggestionOne() {
		ComputerPlayer computer = new ComputerPlayer("Bot",'r',14,2);
		Solution suggestion = computer.createSuggestion(board.getRoom('G'));
		// test that room matches current room the player is in
		assertEquals(suggestion.getRoom().getCardName().charAt(0),  Board.getCell(computer.getRow(), computer.getCol()).getInitial());

		// test if only one weapon not seen
		for(int i=0;i<board.getFullDeck().size() - 1;i++){
			if (board.getFullDeck().get(i).getCardType() == CardType.WEAPON) {
				computer.updateHand(board.getFullDeck().get(i));
			}	
		}
		assertTrue(computer.createSuggestion(board.getRoom('G')).getWeapon().getCardName().equals("Dagger"));

		computer.getHand().clear();
		computer.getSeenCards().clear();
		//test if only one player not seen
		for(int i=9;i<board.getNumPeople() + 8;i++){
			if (board.getFullDeck().get(i).getCardType() == CardType.PERSON) {
				computer.updateHand(board.getFullDeck().get(i));
			}
		}
		
		assertTrue(computer.createSuggestion(board.getRoom('G')).getPerson().getCardName().equals("Billy the Kid"));

	}
	/*
	 * testCreateSuggestionMulti: tests the computer's capability to randomly suggest an card of a weapon or person
	 * If multiple weapons not seen, one of them is randomly selected
	 * if multiple persons not seen, one of them is randomly selected
	 */
	@Test
	public void testCreateSuggestionMulti() {
		ComputerPlayer computer = new ComputerPlayer("Bot",'r',14,2);
		Solution suggestion = computer.createSuggestion(board.getRoom('G'));
				// test if multiple weapons not seen
		for(int i=0;i<board.getFullDeck().size() - 2;i++){
			if (board.getFullDeck().get(i).getCardType() == CardType.WEAPON) {
				computer.updateHand(board.getFullDeck().get(i));
			};
		}
		suggestion = computer.createSuggestion(board.getRoom('G'));
		assertTrue(suggestion.getWeapon().getCardName().equals("Dagger") || suggestion.getWeapon().getCardName().equals("Bow"));

		// test if multiple people not seen
		computer.getHand().clear();
		computer.getSeenCards().clear();
		for(int i=9;i<board.getNumPeople() + 7;i++){
			computer.updateHand(board.getFullDeck().get(i));
		}

		suggestion = computer.createSuggestion(board.getRoom('G'));
		assertTrue(suggestion.getPerson().getCardName().equals("Billy the Kid") || suggestion.getPerson().getCardName().equals("Miss Scarlett"));
	}
	
	/*
	 * testSelectTargets: test that the computer correctly travels to a target
	 * if no rooms in list, select randomly
	 * if room in list that has not been seen, select it
	 * if room in list that has been seen, each target (including room) selected randomly
	 */
	@Test
	public void testSelectTargets() {
		ArrayList<BoardCell> targets = new ArrayList<BoardCell>();
		
		// test for no room, select randomly rollText 1
		ComputerPlayer computer = new ComputerPlayer("Bot",'r',6,3);
		board.calcTargets(Board.getCell(6, 3), 1);
		for(int i=0;i<1000;i++){
			targets.add(computer.selectTarget(board.getTargets()));
			
		}
		for (BoardCell cell: board.getTargets()) {
			assertTrue(targets.contains(cell));
		}
		
		targets.clear();
		
		// test for no room, select randomly rollText 6
		computer = new ComputerPlayer("Bot",'r',25,3);
		board.calcTargets(Board.getCell(25, 3), 6);
		computer.selectTarget(board.getTargets());
		//rollText 1000 times to ensure all options are being picked
		for(int i=0;i<1000;i++){
			targets.add(computer.selectTarget(board.getTargets()));
			
		}
		for (BoardCell cell: board.getTargets()) {
			assertTrue(targets.contains(cell));
		}
		targets.clear();
		
		// test room that has not been seen
		computer = new ComputerPlayer("Bot",'r',14,4);
		board.calcTargets(Board.getCell(14, 4), 1);
		BoardCell target = computer.selectTarget(board.getTargets());
		assertTrue(Board.getCell(target.getRow(), target.getCol()).getInitial() != 'W');
		
		// test room that has been seen
		computer = new ComputerPlayer("Bot",'r',14,4);
		computer.updateHand(board.getFullDeck().get(4));
		board.calcTargets(Board.getCell(14, 4), 1);
		for(int i=0;i<1000;i++){
			targets.add(computer.selectTarget(board.getTargets()));
			
		}
		for (BoardCell cell: board.getTargets()) {
			assertTrue(targets.contains(cell));
		}
		
	}
}
