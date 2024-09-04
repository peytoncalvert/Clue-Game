package tests;

/*
 * GameSetupTests: Tests the game setup; dealing of the cards, setting up of the players and weapons
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;

class GameSetupTests {

	private static Board board;

	@BeforeAll
	static void setUp()
	{
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load BOTH config files
		board.initialize();
	}

	//test to make sure that the human and computer players are setup correctly
	@Test
	void testLoadingPeople() {
		ArrayList<Player> playerList = board.getPlayers();
		// test proper number of players are loaded
		assertEquals(6, board.getPlayers().size());

		// test a computer was loaded properly
		Player boddy = board.getPlayer("Mr. Boddy");
		assertTrue(playerList.contains(boddy));
		assertTrue(ComputerPlayer.class == boddy.getClass());
		assertTrue(boddy.getRow() == 14);
		assertTrue(boddy.getCol() == 10);
		assertTrue(boddy.getColor() == Color.cyan);

		// test human player
		Player billy = board.getPlayer("Billy the Kid");
		assertTrue(playerList.contains(billy));
		assertTrue(HumanPlayer.class == board.getPlayer("Billy the Kid").getClass());
		assertTrue(billy.getRow() == 12);
		assertTrue(billy.getCol() == 8);
		assertTrue(billy.getColor() == Color.white);
	}

	// tests to confirm that the weapons are loaded properly
	@Test
	void testLoadingWeapons() {
		ArrayList<String> weaponList = board.getWeapons();
		// test for proper number of weapons loaded
		assertEquals(6, weaponList.size());

		// test that a few weapons loaded properly
		assertTrue(weaponList.contains(board.getWeapon("Mage Staff")));
		assertTrue(weaponList.contains(board.getWeapon("Trebuchet")));
		assertTrue(weaponList.contains(board.getWeapon("Dagger")));
	}

	// tests to confirm proper card and deck creation
	@Test
	void testCardCreation() {
		// test deck size (all cards are created)
		assertEquals(board.getNumCards() - 6, board.getNumRooms() + board.getNumPeople() + board.getNumWeapons());

		// test number of rooms
		assertEquals(9, board.getNumRooms());
		assertEquals(6, board.getNumPeople());
		assertEquals(6, board.getNumWeapons());
	}

	// tests that the solution has been dealt
	@Test
	void testSolution() {
		//Test the cards to make sure they are the right type i.e room solution is CardType ROOM
		assertEquals(CardType.ROOM, board.getSolution().getRoom().getCardType());
		assertEquals(CardType.PERSON, board.getSolution().getPerson().getCardType());
		assertEquals(CardType.WEAPON, board.getSolution().getWeapon().getCardType());

		//test to make sure none of the players have the solution card
		for(int i=0;i<board.getPlayers().size();i++){
			assertFalse(board.getPlayers().get(i).getHand().contains(board.getSolution().getPerson()));
			assertFalse(board.getPlayers().get(i).getHand().contains(board.getSolution().getRoom()));
			assertFalse(board.getPlayers().get(i).getHand().contains(board.getSolution().getWeapon()));
		}
	}

	// tests the deck is dealt properly
	@Test
	void testDeckDealing() {
		// test all cards are dealt
		assertEquals(0, board.getDeck().size());

		// test players have roughly equal number of cards
		assertEquals(3, board.getPlayer("Miss Scarlett").getHand().size());
		assertTrue(Math.abs(board.getPlayer("Billy the Kid").getHand().size() - board.getPlayer("Professor Plum").getHand().size()) < 2 );
		assertTrue(Math.abs(board.getPlayer("Mr. Green").getHand().size() - board.getPlayer("Colonel Mustard").getHand().size()) < 2 );

		// test for cards dealt more than once
		for (int i  = 0; i < 3; i++) {
			assertFalse(board.getPlayer("Mr. Green").getHand().contains(board.getPlayer("Colonel Mustard").getHand().get(i)));
		}

		for (int i  = 0; i < 3; i++) {
			assertFalse(board.getPlayer("Billy the Kid").getHand().contains(board.getPlayer("Miss Scarlett").getHand().get(i)));
		}

		for (int i  = 0; i < 3; i++) {
			assertFalse(board.getPlayer("Professor Plum").getHand().contains(board.getPlayer("Mr. Boddy").getHand().get(i)));
		}
	}
}
