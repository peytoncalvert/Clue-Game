package clueGame;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * ClueGame: A JFrame extension that displays the game board panel, game control panel, and the known cards panel
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class ClueGame extends JFrame {
	private static final long serialVersionUID = 1L;

	private static Board board;

	private GameControlPanel gameControlPanel;
	private KnownCardsPanel knownCardsPanel;

	private Player currentPlayer;

	public ClueGame() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");		
		// Initialize will load config files 
		board.initialize();

		// grab the human player
		currentPlayer = board.getCurrentPlayer();

		// add game panels
		gameControlPanel = new GameControlPanel();
		knownCardsPanel = KnownCardsPanel.getInstance();
		knownCardsPanel.initialize();
		
		// add panels to jFrame
		add(board, BorderLayout.CENTER);
		add(knownCardsPanel, BorderLayout.EAST);
		add(gameControlPanel, BorderLayout.SOUTH);

		// Add splash screen
		JOptionPane.showMessageDialog(this, "You are " + currentPlayer.getName() + ".\nCan you find the solution\nbefore the Computer players?", "Welcome to Clue", JOptionPane.QUESTION_MESSAGE);

		// process first player's turn after splash screen
		int initialDiceRoll = board.rollDice();
		gameControlPanel.setTurn(currentPlayer, initialDiceRoll);
		board.calcTargets(Board.getCell(currentPlayer.getRow(), currentPlayer.getCol()), initialDiceRoll);

		// set JFrame attributes, size, title, close, visible
		setSize(800, 800);
		setTitle("Clue Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	/*
	 * main: display ClueGame
	 */
	public static void main(String[] args) {
		JFrame gameGUI = new ClueGame();
		gameGUI.setVisible(true);
	}

}
