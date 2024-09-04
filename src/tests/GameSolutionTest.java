package tests;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.CardType;
import clueGame.HumanPlayer;
import clueGame.Player;
import clueGame.Solution;
import clueGame.Card;

/*
 * GameSolutionTest: Test accusations and suggestions are handled properly
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */

/*
 *   test for checkAccusation(), disproveSuggestion() and handleSuggestion().
 */
class GameSolutionTest {

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
	 * testCheckAccusation: tests whether the checkAccusation method in Board.java is properly checking accusations against the solution
	 */
	@Test
	public void testCheckAccusation() {

		//check a solution that is correct
		Solution accusation = board.getSolution();
		boolean accusationResult = Board.checkAccusation(accusation);
		assertTrue(accusationResult);

		//check a solution with wrong weapon
		Card weapon = new Card("Incorrect Weapon", CardType.WEAPON);
		Solution incorrectWeapon = new Solution(accusation.getRoom(), accusation.getPerson(), weapon);
		assertFalse(Board.checkAccusation(incorrectWeapon));

		//check a solution with wrong room
		Solution incorrectRoom = new Solution(board.getPlayers().get(0).getHand().get(0), accusation.getPerson(), accusation.getWeapon());
		assertFalse(Board.checkAccusation(incorrectRoom));

		//check a solution with wrong person
		Card person = new Card("Incorrect Person", CardType.PERSON);
		Solution incorrectPerson = new Solution(accusation.getRoom(), person, accusation.getWeapon());
		assertFalse(Board.checkAccusation(incorrectPerson));

	}

	/*
	 * testDisproveSuggestion: tests whether the disproveSuggestion method properly disproves suggestions
	 */
	@Test
	public void testDisproveSuggestion() {
		Solution solution = board.getSolution();
		board.getPlayers().add(new HumanPlayer("Greg", 'b', 1, 4));
		Player tester = board.getPlayer("Greg");

		// test return of a person card
		tester.updateHand(solution.getPerson());
		tester.updateHand(new Card("Closet", CardType.ROOM));
		tester.updateHand(new Card("Gun", CardType.WEAPON));
		Solution suggestion = new Solution(solution.getRoom(), solution.getPerson(), solution.getWeapon());
		assertTrue(tester.disproveSuggestion(suggestion).equals(solution.getPerson()));
		tester.getHand().clear();

		// test return of a room
		tester.updateHand(new Card("Man", CardType.PERSON));
		tester.updateHand(solution.getRoom());
		tester.updateHand(new Card("Gun", CardType.WEAPON));
		suggestion = new Solution(solution.getRoom(), solution.getPerson(), solution.getWeapon());
		assertTrue(tester.disproveSuggestion(suggestion).equals(solution.getRoom()));
		tester.getHand().clear();

		// test return of a weapon
		tester.updateHand(new Card("Man", CardType.PERSON));
		tester.updateHand(new Card("Closet", CardType.ROOM));
		tester.updateHand(solution.getWeapon());
		suggestion = new Solution(solution.getRoom(), solution.getPerson(), solution.getWeapon());
		assertTrue(tester.disproveSuggestion(suggestion).equals(solution.getWeapon()));
		tester.getHand().clear();

		// test return of null
		tester.updateHand(new Card("Man", CardType.PERSON));
		tester.updateHand(new Card("Closet", CardType.ROOM));
		tester.updateHand(new Card("Gun", CardType.WEAPON));
		suggestion = new Solution(solution.getRoom(), solution.getPerson(), solution.getWeapon());
		assertTrue(tester.disproveSuggestion(suggestion) == null);
		tester.getHand().clear();

		// test multiple cards match
		ArrayList<Card> disproves = new ArrayList<Card>();
		tester.updateHand(new Card("Man", CardType.PERSON));
		tester.updateHand(solution.getRoom());
		tester.updateHand(solution.getWeapon());
		suggestion = new Solution(solution.getRoom(), solution.getPerson(), solution.getWeapon());
        
		// Loop to add many disproves to a list to test randomness
		for (int i = 0; i < 25; i++) {
			disproves.add(tester.disproveSuggestion(suggestion));
		}
		
		assertTrue(disproves.contains(solution.getWeapon()));
		assertTrue(disproves.contains(solution.getRoom()));

		tester.getHand().clear();

		// remove Greg from the player list
		board.getPlayers().remove(board.getPlayers().size() - 1);
	}

	/*
	 * testHandleSuggestion: tests whether the handleSuggestion method properly checks a players ability to disprove a suggestion
	 */
	@Test
	public void testHandleSuggestion(){
		Solution solution = board.getSolution();
		Player player = board.getPlayers().get(0);
		Card C1 = null;
		Card C2 = null;
		Solution suggestion = null;
		
		// Test a suggestion only the suggester can disprove	
		suggestion = new Solution(board.getPlayers().get(0).getHand().get(0), solution.getPerson(), solution.getWeapon());
		player = board.getPlayers().get(0);

		assertEquals(null, board.handleSuggestion(suggestion, player));
		
		// Test suggestion only human player can disprove
		C1 = board.getPlayers().get(5).getHand().get(0);
		suggestion = new Solution(board.getPlayers().get(5).getHand().get(0), solution.getPerson(), solution.getWeapon());
		
		assertEquals(C1, board.handleSuggestion(suggestion, player));
		
		// Test suggestion that two players can disprove and correct player returns answer
		player = board.getPlayers().get(0);
		for (int i = 1; i < board.getPlayers().size(); i++) {
			for (Card card: board.getPlayers().get(i).getHand()) {
				if (C1 != null && C2 != null) {
					break;
				} else if (C1 != null && card.getCardType() == CardType.ROOM) {
					C1 = card;
				} else if (card.getCardType() == CardType.PERSON) {
					C2 = card;
				}
			}
		}
		suggestion = new Solution(C1, C2, solution.getWeapon());
		assertTrue(C1 == board.handleSuggestion(suggestion, player) || C2 == board.handleSuggestion(suggestion, player));
	}

}
