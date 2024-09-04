package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/*
 * GameControlPanel: GUI to display the current turn state and house the make accusation and next buttons.
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class GameControlPanel extends JPanel {
	private static final long serialVersionUID = 8;
	private JTextField whoseTurnText;
	private JTextField rollText;
	private JTextField guessText;
	private JTextField guessResultText;
	private Board board;
	private boolean gameEnded;
	
	private KnownCardsPanel cardPanel;

	/**
	 * Constructor for the panel, it does 90% of the work
	 */
	public GameControlPanel()  {
		board = Board.getInstance();
		cardPanel = KnownCardsPanel.getInstance();
		gameEnded = false;
		// set the layout of the GameControlPanel
		setLayout(new GridLayout(2, 0));

		// create the top half panel of the GameControlPanel
		JPanel topHalf = new JPanel();
		topHalf.setLayout(new GridLayout(1, 4));

		// add whose turn, roll, and button panels to top half
		JPanel whoseTurnPanel = createWhoseTurnPanel();
		topHalf.add(whoseTurnPanel);
		JPanel rollPanel = createRollPanel();
		topHalf.add(rollPanel);

		// accusation button
		JButton button = createButton("MAKE ACCUSATION");
		button.addActionListener(new AccusationListener());
		topHalf.add(button);

		// next button
		button = createButton("NEXT");

		// add next button listener to next button
		button.addActionListener(new NextButtonListener());

		topHalf.add(button);

		add(topHalf);

		// create lower half of GameControlPanel
		JPanel lowHalf = new JPanel();
		lowHalf.setLayout(new GridLayout(0, 2));

		// add guess and guess result panels to lower half
		JPanel guessPanel = createGuessPanel("Guess", guessText);
		lowHalf.add(guessPanel);
		JPanel guessResultPanel = createGuessPanel("Guess Result", guessResultText);
		lowHalf.add(guessResultPanel);
		add(lowHalf);
	}

	/* 
	 *	createWhoseTurnPanel: creates the whose turn panel that displays whose turn it is
	 */
	private JPanel createWhoseTurnPanel() {
		JPanel panel = new JPanel();
		// Use a grid layout, 1 row, 2 elements (label, text)
		JLabel nameLabel = new JLabel("Whose turn?");
		whoseTurnText = new JTextField(15);
		whoseTurnText.setEditable(false);
		panel.add(nameLabel);
		panel.add(whoseTurnText, BorderLayout.SOUTH);
		return panel;
	}

	/*
	 * createRollPanel: creates the dice roll panel
	 */
	private JPanel createRollPanel() {
		JPanel panel = new JPanel();
		JLabel nameLabel = new JLabel("Roll:");
		rollText = new JTextField(10);
		rollText.setEditable(false);
		panel.add(nameLabel, BorderLayout.CENTER);
		panel.add(rollText, BorderLayout.CENTER);
		return panel;
	}
	//
	/*
	 * createGuessPanel: creates the guess panels w/ an etched border
	 */
	private JPanel createGuessPanel(String title, JTextField textField) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		if (title.equals("Guess")) {
			guessText = new JTextField(5);
			guessText.setEditable(false);
			panel.add(guessText);
		} else {
			guessResultText = new JTextField(5);;
			guessResultText.setEditable(false);
			panel.add(guessResultText);
		}
		panel.setBorder(new TitledBorder (new EtchedBorder(), title));
		return panel;
	}

	/*
	 * createButton: creates button with provided string
	 */
	private JButton createButton(String string) {
		// create button with provided string
		JButton button = new JButton(string);
		return button;
	}
	private class AccusationListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if(!Board.getisStartOfTurn() || !(board.getCurrentPlayer() instanceof HumanPlayer)){
				JOptionPane.showMessageDialog(getParent(),"Cannot make accusation past the start of your turn","Error", JOptionPane.ERROR_MESSAGE);
				return;
			} else {
				Board.getAccusationDialog().setVisible(true);
				gameEnded = true;
			}
		}
	}
	/*
	 * ButtonListener: A private class to implement the ActionListener interface and handle when a button is pressed on the GameControlPanel
	 */
	private class NextButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			// clear previous suggestions
			guessText.setText("");
			guessText.setBackground(Color.white);
			guessResultText.setText("");
			guessResultText.setBackground(Color.white);
			
			// shutdown app after game is over
			if( gameEnded ){
				System.exit(0);
			}
			
			Player player = board.getCurrentPlayer();
			
			BoardCell playerCell = Board.getCell(player.getRow(), player.getCol());
			
			Solution possibleSuggestion = board.getSuggestion();
			
			// check if player is in room and they have targets
			if (playerCell.isRoom()) {
				if (!board.getTargets().isEmpty()) {

					// remove them from the playersInRoom list as they will have to leave the room anyway
					Board.getRoom(playerCell).getPlayersInRoom().remove(player);
				}
			}
			
			Set<BoardCell> currentTargets = board.getTargets();
			
			// check if player is a computer player
			if(player instanceof ComputerPlayer) {
				// Check if computer is ready to make an accusation
				if (player.getSeenCards().size() == 18) {
					Solution accusation = ((ComputerPlayer) player).makeAccusation();
					if (Board.checkAccusation(accusation)) {
						gameEnded = true;
						String accusationMade = "Accusation made: " + accusation.getRoom().getCardName() + ", " + accusation.getPerson().getCardName() + ", " + accusation.getWeapon().getCardName() + "\n";
						String solutionView = "Solution: " + board.getSolution().getRoom().getCardName() + ", " + board.getSolution().getPerson().getCardName() + ", " + board.getSolution().getWeapon().getCardName() + "\n";
						
						JOptionPane.showMessageDialog(getParent(), accusationMade + solutionView + "You lost!\nHit the \'NEXT\' button one more time to quit", "Game Over", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				// if the player can make a move
				if (!currentTargets.isEmpty()) {
					// randomly choose a target
					BoardCell target = ((ComputerPlayer) player).selectTarget(currentTargets);

					// vacate player cell, set player cell to target, occupy player cell
					Board.getCell(player.getRow(), player.getCol()).setOccupied(false);
					player.setCell(target.getRow(), target.getCol());
					Board.getCell(player.getRow(),player.getCol()).setOccupied(true);
				}
				
				if (Board.getCell(player.getRow(),player.getCol()).isRoom()) {
					possibleSuggestion = ((ComputerPlayer)player).createSuggestion(Board.getRoom(Board.getCell(player.getRow(),player.getCol())));
					
				}
			}
			
			// check if current player is human player and has finished
			if (player instanceof HumanPlayer) {
				// if player is not on a cell from the targets list then they have not moved
				if(!currentTargets.contains(playerCell) && !currentTargets.isEmpty()) {
					JOptionPane.showMessageDialog(getParent(), "You haven't finished your turn!", "Error", JOptionPane.ERROR_MESSAGE);
					return;	
				}
			}
			
			if (possibleSuggestion != null) {
				// handle suggestion
				Card disprovingCard = board.handleSuggestion(possibleSuggestion, player);
				
				setGuess(player, possibleSuggestion.getRoom().getCardName() + ", " + possibleSuggestion.getPerson().getCardName() + ", " + possibleSuggestion.getWeapon().getCardName());
				
				// display disproving card
				if (disprovingCard != null) {
					player.getSeenCards().add(disprovingCard);
					if (player instanceof ComputerPlayer) {
						
						//show player name who disproved
						setGuessResult(disprovingCard.getOwner(), "Suggestion Disproven");
					} else {
						//show player name who disproved
						setGuessResult(disprovingCard.getOwner(), disprovingCard.getCardName());
					}
				}
				else {
					setGuessResult(player, "No new clue");
				}
			}

			board.getTargets().clear();
			
			board.setSuggestion(null);
			
			// Get player board cell a second time after they've moved
			playerCell = Board.getCell(player.getRow(), player.getCol());

			// add player to room list if just entered room
			if(playerCell.isRoom()){
				Board.getRoom(playerCell).getPlayersInRoom().add(player);
			}

			// update player
			int newPlayerIdx = board.getPlayers().indexOf(player) + 1;
			if (newPlayerIdx > 5) {
				newPlayerIdx = 0;
			}

			// set the current player to the new player
			board.setCurrentPlayer(board.getPlayers().get(newPlayerIdx));
			Player nextPlayer = board.getCurrentPlayer();

			// roll the die
			int roll = board.rollDice();

			// set the turn and roll value in GameControlPanel
			setTurn(nextPlayer, roll);

			// calculate targets
			BoardCell currentCell = Board.getCell(nextPlayer.getRow(), nextPlayer.getCol());
			board.calcTargets(currentCell, roll);		

			// update the board to paint targets or computer player's move
			board.repaint();
			cardPanel.updateCardPanels();
			board.setisStartOfTurn(true);
		}
	}

	/**
	 * Main to test the panel
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//GameControlPanel panel = new GameControlPanel();  // create the panel
		JFrame frame = new JFrame();  // create the frame 
		//frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(750, 180);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible

		// test filling in the data
		//		panel.setTurn(new ComputerPlayer( "Colonel Mustard", 'o', 0, 0), 5);
		//		panel.setGuess( "I have no guess!");
		//		panel.setGuessResult( "So you have nothing?");
	}

	// setters
	public void setGuessResult(Player player, String guess) {
		guessResultText.setText(guess);
		guessResultText.setBackground(player.getColor());
	}

	public void setGuess(Player player, String guessResult) {
		guessText.setText(guessResult);
		guessText.setBackground(player.getColor());
	}

	public void setTurn(Player player, int roll) {
		whoseTurnText.setText(player.getName());
		whoseTurnText.setBackground(player.getColor());
		rollText.setText(String.valueOf(roll));
	}

}
