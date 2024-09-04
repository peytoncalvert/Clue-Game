package clueGame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/*
 * KnownCardsPanel: GUI to display the current player's cards in hand and seen
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class KnownCardsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int TEXT_FIELD_SIZE = 12;
	private Player currentPlayer;

	private JPanel peoplePanel;
	private JPanel roomPanel;
	private JPanel weaponPanel;

	private Board board;

	private static KnownCardsPanel theInstance = new KnownCardsPanel();

	/*
	 * default constructor: creates the KnownCardsPanel format and adds 3 panels for each type of card
	 */
	public KnownCardsPanel() {
		super();
	}

	public void initialize() {
		board = Board.getInstance();
		this.currentPlayer = board.getCurrentPlayer();

		setLayout(new GridLayout(3, 0));

		// add panels
		peoplePanel = createCardPanel("People", CardType.PERSON);
		add(peoplePanel);
		roomPanel = createCardPanel("Rooms", CardType.ROOM);
		add(roomPanel);
		weaponPanel = createCardPanel("Weapons", CardType.WEAPON);
		add(weaponPanel);

		setBorder(new TitledBorder (new EtchedBorder(), "Known Cards", TitledBorder.CENTER, TitledBorder.TOP));
	}

	/*
	 * createCardPanel: creates card panels w/ labels for in hand cards and seen cards
	 */
	private JPanel createCardPanel(String title, CardType cardType) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(new JLabel("In Hand:"), BorderLayout.EAST);
		JPanel inHandPanel = createCardSubPanel(currentPlayer.getHand(), cardType);
		panel.add(inHandPanel);

		Set<Card> seenCards = currentPlayer.getSeenCards();
		seenCards.removeIf(currentPlayer.getHand()::contains);

		panel.add(new JLabel("Seen:"), BorderLayout.EAST);
		JPanel seenPanel = createCardSubPanel(seenCards, cardType);
		panel.add(seenPanel);

		panel.setBorder(new TitledBorder (new EtchedBorder(), title));
		return panel;
	}

	/*
	 * createCardSubPanel: creates the 'In Hand' and 'Seen' subpanels
	 */
	private JPanel createCardSubPanel(Iterable<Card> cards, CardType cardType) {
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		JTextField textField;

		boolean hasCards = false;

		// loops through cards in current players seen cards and creates a textfield for it
		for (Card card: cards) {

			if (card.getCardType() == cardType) {
				textField = createCardTextField();
				textField.setText(card.getCardName());
				textField.setBackground(card.getOwner().getColor());
				subPanel.add(textField);
				hasCards = true;
			}
		}
		// creates a 'none' text field if there aren't any cards in the player's seen cards of this type
		if (!hasCards) {
			textField = createCardTextField();
			textField.setText("None");
			textField.setBackground(currentPlayer.getColor());
			subPanel.add(textField);
		}

		subPanel.revalidate();
		subPanel.repaint();
		return subPanel;
	}

	/*
	 * updates all of the panels containing cards seen and cards in hand
	 */
	public void updateCardPanels() {
		remove(peoplePanel);
		remove(roomPanel);
		remove(weaponPanel);

		peoplePanel = createCardPanel("People", CardType.PERSON);
		add(peoplePanel);

		roomPanel = createCardPanel("Rooms", CardType.ROOM);
		add(roomPanel);

		weaponPanel = createCardPanel("Weapons", CardType.WEAPON);
		add(weaponPanel);

		revalidate();
		repaint();
	}

	private JTextField createCardTextField() {
		JTextField textField = new JTextField(TEXT_FIELD_SIZE);
		textField.setEditable(false);
		textField.setBorder(new EtchedBorder());
		textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		return textField;
	}
	
	// getters
	public static KnownCardsPanel getInstance() {
		return theInstance;
	}
	
	/**
	 * Main to test the panel
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// test filling in the data
		Board board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");	
		board.initialize();
		// grab the human player
		Player player = board.getPlayers().get(5);

		KnownCardsPanel panel = new KnownCardsPanel();  // create the panel
		panel.initialize();
		JFrame frame = new JFrame();  // create the frame 
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(180, 750);  // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible

		// update the human player's seen cards with other player's cards
		for(int i=0;i<board.getPlayers().size();i++){
			for(int j=0;j<board.getPlayers().get(i).getHand().size();j++){
				player.updateSeenCards(board.getPlayers().get(i).getHand().get(j));
			}
		}

		panel.updateCardPanels();

	}

}
