package clueGame;

import java.util.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;

/*
 * Board: Represents the game board, holding the game board information
 * @author Andrew Bernklau
 * @author Peyton Calvert
 * @sources
 * @collaborators
 */
public class Board extends JPanel {
	private static final long serialVersionUID = 1L;
	private static BoardCell[][] grid;
	private static int numRows;
	private static int numColumns;

	private String layoutConfigFile;
	private String setupConfigFile;
	private static Map<Character, Room> roomMap;
	private Set<BoardCell> targets; 
	private Set<BoardCell> visited;

	private ArrayList<BoardCell> toBeHandled;
	private static ArrayList<Player> players;
	private static ArrayList<String> weapons;
	private ArrayList<Card> deck;
	private static ArrayList<Card> fullDeck;
	private static Player currentPlayer;

	private static Solution solution;
	private static Solution suggestion;
	private static Solution accusation;

	private SuggestionDialog suggestionDialog;
	private static AccusationDialog accusationDialog;

	private static JTextField roomText;

	private boolean findTargetStart; // Signify the starting of the findTargets method
	private static boolean isStartOfTurn;
	private static Board theInstance = new Board();

	// variables for tests
	private static int numCells;
	private static int numCards;
	private static int numRooms;
	private static int numPeople;
	private static int numWeapons;

	private Random rand;

	/*
	 * Private Constructor to satisfy Singleton Design Pattern
	 */
	private Board() {
		super();
	}

	/*
	 * initialize: Initializes the grid cells and their adjLists
	 */
	public void initialize() {

		// Initialize Sets and ArrayLists
		roomMap = new HashMap<Character, Room>();
		targets = new HashSet<BoardCell>();
		visited = new HashSet<BoardCell>();
		toBeHandled = new ArrayList<BoardCell>();
		players = new ArrayList<Player>();
		weapons = new ArrayList<String>();
		deck = new ArrayList<Card>();
		fullDeck = new ArrayList<Card>();
		isStartOfTurn = true;
		suggestion = null;
		rand = new Random();

		numCards = 0;
		numRooms = 0;
		numPeople = 0;
		numWeapons = 0;

		// try loading the setup and layout config files
		try {
			loadSetupConfig();
			loadLayoutConfig();
		} catch( BadConfigFormatException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// set cells to occupied
		setOccupiedCells();
		//initialize the solution to the game
		initializeSolution();
		//deal cards out to the players
		dealCards();

		// Handle door that were set in grid before their respective rooms
		setDoorCells();

		// Initialize the adjLists of each cell
		initializeAdjLists();

		currentPlayer = players.get(5);
		MoveListener moveListener = new MoveListener();

		addMouseListener(moveListener);
		
		suggestionDialog = new SuggestionDialog();
		accusationDialog = new AccusationDialog();
	}
	/*
	 * setConfigFiles: Set config file names 
	 */
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile) {
		this.layoutConfigFile = "data/" + layoutConfigFile;
		this.setupConfigFile = "data/" + setupConfigFile;
	}

	/*
	 * loadSetupConfig: Loads the room types 
	 */
	public void loadSetupConfig() throws BadConfigFormatException {
		roomMap.clear();
		deck.clear();
		try {
			File setupFile = new File(setupConfigFile);
			Scanner reader = new Scanner(setupFile);
			String [] line = {};

			//read through while the file still has another line
			while (reader.hasNextLine()) {
				String rawLine = reader.nextLine();
				numCards++;
				line = rawLine.split(", ", -2);

				// Check for room or space
				if (rawLine.startsWith("R") || rawLine.startsWith("S")) {
					// Check setup format
					if (!line[0].equals("Room") && !line[0].equals("Space")) {
						throw new BadConfigFormatException();
					}

					roomMap.put(line[2].charAt(0), new Room(line[1]));

					// check for room and add it to the deck
					if (line[0].equals("Room")) {
						deck.add(new Card(line[1], CardType.ROOM));
						numRooms++;
					}

				}

				// check for player, check for type of player, add them to the deck and players ArrayList
				if (rawLine.startsWith("P")) {
					if (line[1].equals("Computer")) {
						players.add(new ComputerPlayer(line[2], line[3].charAt(0), Integer.parseInt(line[4]), Integer.parseInt(line[5])));
					} else {
						players.add(new HumanPlayer(line[2], line[3].charAt(0), Integer.parseInt(line[4]), Integer.parseInt(line[5])));
					}
					deck.add(new Card(line[2], CardType.PERSON));

					numPeople++;
				}

				// check for weapon, add them to deck and weapons ArrayList
				if (rawLine.startsWith("W")) {
					weapons.add(line[1]);
					deck.add(new Card(line[1], CardType.WEAPON));

					numWeapons++;
				}

				// create a copy of the deck that is not depleted
				fullDeck = (ArrayList<Card>) deck.clone();
			}
		} catch(FileNotFoundException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	/*
	 * loadLayoutConfig: Loads data of the layout of the game board
	 */
	public void loadLayoutConfig() throws BadConfigFormatException {
		numCells = 0; // number of total cells provided by the file, used to check proper dimensions of config file
		numRows = 0;
		numColumns = 0;

		try {
			File layoutFile = new File(layoutConfigFile);
			Scanner reader = new Scanner(layoutFile);

			List<String[]> rows = new ArrayList<String[]>();

			// Read data in by row/line
			while (reader.hasNextLine()) {
				String[] row = reader.nextLine().split(",", -2);
				rows.add(row);
				numRows++;

				/* 
				 * check if numColumns has been initialized,
				 * set it to the character length of the row if it hasn't been initialized,
				 * if it has been initialized, check if it is the same size as the row length,
				 * if it isn't, then throw an error that the file is bad format
				 */
				if (numColumns == 0) {
					numColumns = row.length;
				} else if (numColumns != row.length) {
					throw new BadConfigFormatException("Incorrect dimensions found");
				}
			}

			initializeGrid(rows);
			// Confirm dimensions
			if(numCells != numRows * numColumns) {
				throw new BadConfigFormatException("Incorrect dimensions found");
			}
			reader.close();
		} catch(FileNotFoundException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	/*
	 * initializeGrid: initializes the grid with the provided rows from the layout file
	 */
	private void initializeGrid(List<String[]> rows) throws BadConfigFormatException{
		grid = new BoardCell[numRows][numColumns];

		// Initialize grid with BoardCells
		for(int i = 0; i < numRows; i++) { 
			String[] row = rows.get(i);
			for(int j = 0; j < numColumns; j++) {
				String cellData = row[j];

				// throw exception if there isn't any cell data or the roomMap does not contain the character in the cell data
				if (cellData.isEmpty() || !roomMap.containsKey(cellData.charAt(0))) {
					throw new BadConfigFormatException("Incorrect character found");
				}
				grid[i][j] = new BoardCell(i, j, cellData.charAt(0));

				// check if the cell has a second char
				if (cellData.length() > 1) {
					handleSecondChar(grid[i][j], cellData);	
				}
				numCells++;
			}

		}
	}
	/*
	 * calcTargets: Calculates legal targets for a move from startCell of length pathLength
	 */ 
	public void calcTargets(BoardCell startCell, int pathLength) {
		visited.add(startCell);
		findTargetStart = true;
		targets.clear();
		findTargets(startCell, pathLength);
		visited.clear();
		if (currentPlayer.getWasMoved()){
			targets.add(startCell);
			currentPlayer.setWasMoved(false);
		}
	}

	/*
	 * findTargets: DFS helper function for finding all possible targets
	 */
	private void findTargets(BoardCell currentCell, int pathRemaining) {
		// Base case: return and add cell to targets list if rollText value reached or cell is a roomCenter()
		if (pathRemaining == 0 || (currentCell.isRoomCenter() && !findTargetStart)) {
			targets.add(currentCell);
			return;
		}
		findTargetStart = false;
		// Look in all possible directions using adjacency list
		for (BoardCell adj : currentCell.getAdjList()) {
			if (!visited.contains(adj) && (!adj.isOccupied() || adj.isRoomCenter())) {
				visited.add(adj);
				findTargets(adj, pathRemaining - 1);
				visited.remove(adj);
			}
		}
	}

	/*
	 * handleSecondChar: A private helper method that handles the case in which a cell read in is associated to 2 chars
	 */
	private void handleSecondChar(BoardCell cell, String cellData) {
		char firstChar = cellData.charAt(0);
		char secondChar = cellData.charAt(1);
		// determine second char
		switch(secondChar) {

		case '*':
			cell.setRoomCenter(true);
			roomMap.get(firstChar).setCenterCell(cell);
			break;
		case '#':
			cell.setRoomLabel(true);
			roomMap.get(firstChar).setLabelCell(cell);
			break;
		case '<':
			cell.setDoorway(true, DoorDirection.LEFT);
			roomMap.get(grid[cell.getRow()][cell.getCol() - 1].getInitial()).setDoorCell(cell);
			break;

		case '>':
			cell.setDoorway(true, DoorDirection.RIGHT);
			toBeHandled.add(cell);
			break;

		case 'v':
			cell.setDoorway(true, DoorDirection.DOWN);
			toBeHandled.add(cell);
			break;

		case '^':
			cell.setDoorway(true, DoorDirection.UP);
			roomMap.get(grid[cell.getRow() - 1][cell.getCol()].getInitial()).setDoorCell(cell);
			break;
		default:
			cell.setPassage(true, secondChar);
			roomMap.get(firstChar).setPassageCell(cell);
			break;
		}
	}

	/*
	 * initializeAdjLists: A private helper method to initialize the adjLists of each cell
	 */
	private void initializeAdjLists() {
		//double loop for every cell on the board
		for(int i = 0; i < numRows; i++){
			for(int j = 0; j < numColumns; j++) {
				BoardCell cell = grid[i][j];
				cell.getAdjList().clear();
				// Any room tile besides the center or secret passage should be inaccessible
				if (!cell.isRoom() || cell.isRoomCenter() || cell.isSecretPassage()) {

					// Initialize room center adjacency lists 
					if (cell.isRoomCenter()) {
						roomCenterAdjs(cell);
					}

					// Walkways
					if (cell.getInitial() == 'W') {
						walkwayAdjs(cell);
					}
				}
			}
		}
	}

	/*
	 * setDoorCells: set door cells to their respective room in room map
	 */
	private void setDoorCells() {
		for (BoardCell cell: toBeHandled) {
			if (cell.getDoorDirection() == DoorDirection.DOWN) {
				roomMap.get(grid[cell.getRow() + 1][cell.getCol()].getInitial()).setDoorCell(cell);
			} else {
				roomMap.get(grid[cell.getRow()][cell.getCol() + 1].getInitial()).setDoorCell(cell);
			}
		}
		toBeHandled.clear();
	}

	/*
	 * roomCenterAdjs: Determine adjs of room centers
	 */
	private void roomCenterAdjs(BoardCell roomCenter) {
		BoardCell passageCell = roomMap.get(roomCenter.getInitial()).getPassageCell();

		// Check if room has a passage
		if (passageCell != null) {

			// get the secret passage initial
			char passageInitial = passageCell.getSecretPassage();

			// get centerCell of room connected by secret passage
			BoardCell passageCenterCell = roomMap.get(passageInitial).getCenterCell();
			roomCenter.addAdj(passageCenterCell);
		}

		// Add all doors to room center adjacency list
		ArrayList<BoardCell> doorList = roomMap.get(roomCenter.getInitial()).getDoorList();
		for (BoardCell door: doorList) {
			roomCenter.addAdj(door);
		}
	}

	/*
	 * walkwayAdjs: Determine adjs of walkways
	 */
	private void walkwayAdjs(BoardCell cell) {
		if (cell.getRow() < numRows - 1 && grid[cell.getRow() + 1][cell.getCol()].getInitial() == 'W') {
			cell.addAdj(grid[cell.getRow() + 1][cell.getCol()]);
		}
		if (cell.getCol() < numColumns - 1 && grid[cell.getRow()][cell.getCol() + 1].getInitial() == 'W') {
			cell.addAdj(grid[cell.getRow()][cell.getCol() + 1]);
		}
		if (cell.getRow() > 0 && grid[cell.getRow() - 1][cell.getCol()].getInitial() == 'W') {
			cell.addAdj(grid[cell.getRow() - 1][cell.getCol()]);
		}
		if (cell.getCol() > 0&& grid[cell.getRow()][cell.getCol() - 1].getInitial() == 'W') {
			cell.addAdj(grid[cell.getRow()][cell.getCol() - 1]);
		}
		if (cell.isDoorway()) {
			doorwayAdjs(cell);
		}
	}

	/*
	 * doorwayAdjs: Determine adjs of doorways
	 */
	private void doorwayAdjs(BoardCell cell) {
		// Add center room cell to cell adjacency list if cell is a doorway
		BoardCell roomCell = null;

		// Determine door direction to acquire room cell initial and retrieve room cell from room map
		switch(cell.getDoorDirection()) {
		case DOWN:
			roomCell = roomMap.get(grid[cell.getRow() + 1][cell.getCol()].getInitial()).getCenterCell();
			break;
		case UP:
			roomCell = roomMap.get(grid[cell.getRow() - 1][cell.getCol()].getInitial()).getCenterCell();
			break;
		case RIGHT:
			roomCell = roomMap.get(grid[cell.getRow()][cell.getCol() + 1].getInitial()).getCenterCell();
			break;
		case LEFT:
			roomCell = roomMap.get(grid[cell.getRow()][cell.getCol() - 1].getInitial()).getCenterCell();
			break;
		default:
			break;
		}
		cell.addAdj(roomCell);
	}

	/*
	 * initializeSolution: Creates a random solution to the current game
	 */
	private void initializeSolution() {

		final int PEOPLE_START_IDX = 9;
		final int WEAPON_START_IDX = 15;

		Random rand = new Random();
		// Generate random integers for room, person, and weapon cards
		int randRoom = rand.nextInt(9);
		int randPerson = rand.nextInt(6);
		int randWeapon = rand.nextInt(6);

		solution = new Solution(deck.get(randRoom), deck.get(randPerson + PEOPLE_START_IDX), deck.get(randWeapon + WEAPON_START_IDX));

		deck.remove(randRoom);
		deck.remove(randPerson+PEOPLE_START_IDX - 1);
		deck.remove(randWeapon+WEAPON_START_IDX - 2);
	}

	/*
	 * dealCards: Deals the cards remaining after the solution has been chosen to the players. 
	 */
	private void dealCards() {

		shuffle();

		int player = 0;

		// Loops over entire deck of cards and hands them out to each player in a round-robin style
		for (int i = 0; i < deck.size(); i++) {
			if (player == players.size()) {
				player = 0;
			}
			players.get(player).updateHand(deck.get(i));
			deck.get(i).setOwner(players.get(player));
			player++;
		}
		deck.clear();
	}

	/*
	 * shuffleDeck: shuffle the deck
	 */
	public void shuffle(){
		ArrayList<Card> newDeck = new ArrayList<Card>();
		Random rand = new Random();
		int pick;
		int size = deck.size();
		//loops through the old deck picking a card at random until the shuffled deck is built
		for(int i=0;i<size;i++){
			pick = rand.nextInt(deck.size());
			newDeck.add(deck.get(pick));
			deck.remove(pick);
		}
		deck = newDeck;
	}
	
	/*
	 * checkAccusation: checks a player's accusation against the solution
	 */
	public static boolean checkAccusation(Solution accusation) {
		return solution.equals(accusation);
	}

	/*
	 * handleSuggestion: handles whether a player can dispute a suggestion
	 */
	public Card handleSuggestion(Solution suggestion, Player suggestingPlayer) {
		Card disproval = null;

		// Move player that was suggested
		Player toMove = getPlayer(suggestion.getPerson().getCardName());
		if (getCell(toMove.getRow(), toMove.getCol()).isRoom()) {
			getRoom(getCell(toMove.getRow(), toMove.getCol())).getPlayersInRoom().remove(toMove);
		}
		
		// Vacate player;s current cell before moving them, move them, and then set their cell occupied
		getCell(toMove.getRow(), toMove.getCol()).setOccupied(false);
		toMove.setCell(suggestingPlayer.getRow(), suggestingPlayer.getCol());
		getRoom(getCell(toMove.getRow(), toMove.getCol())).getPlayersInRoom().add(toMove);
		
		// Set the player was moved to a room
		toMove.setWasMoved(true);

		// Loop over every player's hand and finds all cards to disprove the suggestion
		for(Player player: players) {
			disproval = player.disproveSuggestion(suggestion);
			if(disproval != null && player != suggestingPlayer) {
				return disproval;
			}	
		}
		
		return null;
	}

	/*
	 * paintComponent: draws the board cells, players, and room labels
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Calculate cellHeight and cellWidth based on board panel height and width
		int cellHeight = this.getHeight() / numRows;
		int cellWidth = this.getWidth() / numColumns;

		// loop through entire grid so each cell can be drawn
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				// grab cell and call draw method with a board offset
				grid[i][j].draw(g, cellHeight, cellWidth, new Dimension(j * cellWidth, i * cellHeight));
			}
		}

		//draw all room labels
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				if (grid[i][j].isLabel()) {
					grid[i][j].drawRoomLabels(g, roomMap, new Dimension(j * cellWidth, i * cellHeight));
				}
			}
		}

		// Draw human player's targets
		if (currentPlayer instanceof HumanPlayer) {
			for (BoardCell cell: targets){
				cell.draw(g, cellHeight, cellWidth, new Dimension(cell.getCol() * cellWidth, cell.getRow() * cellHeight), Color.MAGENTA);
			}
		}

		//have all players paint themselves
		for (Player player: players) {
			player.draw(g, cellWidth - 3, new Dimension(player.getCol() * cellWidth + 1, player.getRow() * cellHeight + 1));
		}

		// draw number of players in room
		for (Map.Entry<Character, Room> entry: roomMap.entrySet()) {
			Room room = entry.getValue();
			if (!room.getName().equals("Walkway") && !room.getName().equals("Unused")) {
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(room.getPlayersInRoom().size()), room.getLabelCell().getCol() * cellWidth, room.getLabelCell().getRow() * cellHeight + 26);
			}
		}
	}

	/*
	 * rollDice: Simulate a dice roll
	 */
	public int rollDice() {
		return rand.nextInt(6)+1;	
	}

	/*
	 * MoveListener: listens for user to click on the board
	 */
	private class MoveListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// calculate what cell was clicked
			int cellRow = e.getY() / numRows;
			int cellCol = e.getX() / numColumns;
			setisStartOfTurn(false);
			BoardCell clickedCell = getCell(cellRow, cellCol);

			// display error if player did not click a valid target cell
			if (!targets.contains(clickedCell)) {
				JOptionPane.showMessageDialog(getParent(), "Not a valid target", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// check if player is in room and they have targets
			if (getCell(currentPlayer.getRow(), currentPlayer.getCol()).isRoom()) {
				if (!getTargets().isEmpty()) {

					// remove them from the playersInRoom list as they will have to leave the room anyway
					getRoom(getCell(currentPlayer.getRow(), currentPlayer.getCol())).getPlayersInRoom().remove(currentPlayer);
				}
			}

			// vacate player cell, set player cell to target, occupy player cell
			getCell(currentPlayer.getRow(),currentPlayer.getCol()).setOccupied(false);
			currentPlayer.setCell(clickedCell.getRow(), clickedCell.getCol());
			getCell(currentPlayer.getRow(),currentPlayer.getCol()).setOccupied(true);

			// add player to room list if just entered room
			if(getCell(currentPlayer.getRow(), currentPlayer.getCol()).isRoom()){
				getRoom(getCell(currentPlayer.getRow(), currentPlayer.getCol())).getPlayersInRoom().add(currentPlayer);
				roomText.setText(getRoom(getCell(currentPlayer.getRow(), currentPlayer.getCol())).getName());
				suggestionDialog.setVisible(true);
			}

			repaint();	
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

	/*
	 * SuggestionDialog: creates the panel for the user to enter in the player and weapon for their suggestion
	 */
	private static class SuggestionDialog extends JDialog {
	
		private static final long serialVersionUID = 1L;
		private JComboBox<String> people;
		private JComboBox<String> weaponBox;
	
		public SuggestionDialog() {
			setSize(400, 200);
			setLayout(new GridLayout(4, 1));
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			people = new JComboBox<String>();
			weaponBox = new JComboBox<String>();

			for (int i = 0; i < 6; i++) {
				people.addItem(players.get(i).getName());
				weaponBox.addItem(weapons.get(i));
			}

			// current room panel
			JPanel currentRoomPanel = new JPanel();
			roomText = new JTextField(10);
			roomText.setEditable(false);
			currentRoomPanel.add(new JLabel("Current room"), BorderLayout.WEST);
			currentRoomPanel.add(roomText, BorderLayout.EAST);
			
			//person panel
			JPanel personPanel = new JPanel();
			personPanel.add(new JLabel("Person"), BorderLayout.WEST);
			personPanel.add(people, BorderLayout.EAST);

			//weapon panel
			JPanel weaponPanel = new JPanel();
			weaponPanel.add(new JLabel("Weapon"), BorderLayout.WEST);
			weaponPanel.add(weaponBox, BorderLayout.EAST);

			//two buttons 
			JPanel buttonPanel = new JPanel();
			JButton submitButton = new JButton("Submit");
			JButton cancelButton = new JButton("Cancel");
			submitButton.addActionListener( new SubmitListener());
			cancelButton.addActionListener( new CancelListener());

			buttonPanel.add(submitButton);
			buttonPanel.add(cancelButton);

			add(currentRoomPanel);
			add(personPanel);
			add(weaponPanel);
			add(buttonPanel);
		}
		
		/*
		 * SubmitListener: Listen for player to submit their suggestion
		 */
		private class SubmitListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				Card roomCard = null;
				Card playerCard = null;
				Card weaponCard = null;

				// Loop through the deck based on the values selected
				for (Card card: fullDeck) {
					if (card.getCardName().equals(roomText.getText())) {
						roomCard = card;
					}

					else if (card.getCardName().equals(people.getSelectedItem())) {
						playerCard = card;
					}

					else if (card.getCardName().equals(weaponBox.getSelectedItem())) {
						weaponCard = card;
					}
				}

				suggestion = new Solution(roomCard, playerCard, weaponCard);
				setVisible(false);
			}
		}
		
		/*
		 * CancelListner: Listen for player to cancel their suggestion
		 */
		private class CancelListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}
		
	}
	
	/*
	 * AccusationDialog creates the panel for the user to enter their accusation
	 */
	public static class AccusationDialog extends JDialog {
		private static final long serialVersionUID = 1L;
		JComboBox<String> rooms;
		JComboBox<String> people;
		JComboBox<String> weaponBox;
		
		public AccusationDialog() {
			
			setSize(400, 200);
			setLayout(new GridLayout(4, 1));
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			
			people = new JComboBox<String>();
			weaponBox = new JComboBox<String>();
			rooms = new JComboBox<String>();

			for (int i = 0; i < 6; i++) {
				people.addItem(players.get(i).getName());
				weaponBox.addItem(weapons.get(i));
			}
			
			for (Map.Entry<Character, Room> entry: roomMap.entrySet()) {
				Room room = entry.getValue();
				if (!room.getName().equals("Walkway") && !room.getName().equals("Unused")) {
					rooms.addItem(room.getName());
				}
			}

			// current room panel
			JPanel roomPanel = new JPanel();
			roomPanel.add(new JLabel("Room"), BorderLayout.WEST);
			roomPanel.add(rooms, BorderLayout.EAST);

			//person panel
			JPanel personPanel = new JPanel();
			personPanel.add(new JLabel("Person"), BorderLayout.WEST);
			personPanel.add(people, BorderLayout.EAST);

			//weapon panel
			JPanel weaponPanel = new JPanel();
			weaponPanel.add(new JLabel("Weapon"), BorderLayout.WEST);
			weaponPanel.add(weaponBox, BorderLayout.EAST);

			//two buttons 
			JPanel buttonPanel = new JPanel();
			JButton submitButton = new JButton("Submit");
			JButton cancelButton = new JButton("Cancel");
			submitButton.addActionListener( new SubmitListener());
			cancelButton.addActionListener( new CancelListener());

			buttonPanel.add(submitButton);
			buttonPanel.add(cancelButton);

			add(roomPanel);
			add(personPanel);
			add(weaponPanel);
			add(buttonPanel);
		}
		
		/*
		 * SubmitListener: Listen for user to submit their accusation
		 */
		private class SubmitListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				Card roomCard = null;
				Card playerCard = null;
				Card weaponCard = null;

				// Loop through deck to acquire cards based on the values in selected
				for (Card card: fullDeck) {
					if (card.getCardName().equals(rooms.getSelectedItem())) {
						roomCard = card;
					}

					else if (card.getCardName().equals(people.getSelectedItem())) {
						playerCard = card;
					}

					else if (card.getCardName().equals(weaponBox.getSelectedItem())) {
						weaponCard = card;
					}
				}
				
				// create accusation and strings for display
				accusation = new Solution(roomCard, playerCard, weaponCard);
				String accusationMade = "Accusation made: " + accusation.getRoom().getCardName() + ", " + accusation.getPerson().getCardName() + ", " + accusation.getWeapon().getCardName() + "\n";
				String solutionView = "Solution: " + solution.getRoom().getCardName() + ", " + solution.getPerson().getCardName() + ", " + solution.getWeapon().getCardName() + "\n";
				
				//check the accusation
				if(checkAccusation(accusation)) {
					JOptionPane.showMessageDialog(getParent(), accusationMade + solutionView + "You won the game!\nHit the \'NEXT\' button one more time to quit", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(getParent(), accusationMade + solutionView + "You lost!\nHit the \'NEXT\' button one more time to quit", "Game Over", JOptionPane.INFORMATION_MESSAGE);
				}

				setVisible(false);

			}
		}
		
		/*
		 * CancelListner: Listen for user to cancel their accusation
		 */
		private class CancelListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}
		
	}
	
	/*
	 * setOccupiedCells: set player cell to occupied
	 */
	private void setOccupiedCells() {
		for (Player player: players) {
			grid[player.getRow()][player.getCol()].setOccupied(true);
		}
	}

	// getters
	public static Board getInstance() {
		return theInstance;
	}
	public static BoardCell getCell(int row, int col){
		return grid[row][col];
	}

	public Set<BoardCell> getTargets(){
		return this.targets;
	}

	public Room getRoom(char c) {
		return roomMap.get(c);
	}

	public static Room getRoom(BoardCell cell) {
		char symbol = cell.getInitial();
		return roomMap.get(symbol);
	}
	public int getNumRows() {
		return Board.numRows;
	}

	public int getNumColumns() {
		return Board.numColumns;
	}

	public Set<BoardCell> getAdjList(int cellRow, int cellColumn) {

		return grid[cellRow][cellColumn].getAdjList();
	}
	// test getters
	public int getNumPlayers() {

		return getPlayers().size();
	}

	public ArrayList<Player> getPlayers() {

		return Board.players;
	}

	public Player getPlayer(String name) {

		// search for player in players ArrayList based on name of player
		int index = -1;
		for(int i=0;i<players.size();i++){
			if(players.get(i).getName().equals(name)){
				index = i;
				break;
			}
		}
		return players.get(index);
	}

	public ArrayList<String> getWeapons() {

		return Board.weapons;
	}

	public String getWeapon(String weapon) {
		return weapons.get(weapons.indexOf(weapon));
	}

	public ArrayList<Card> getDeck() {
		return this.deck;
	}

	public ArrayList<Card> getFullDeck() {
		return Board.fullDeck;
	}

	public Solution getSolution() {

		return solution;
	}

	public int getNumCards() {
		return numCards;
	}

	public int getNumRooms() {
		return numRooms;
	}

	public int getNumPeople() {
		return numPeople;
	}

	public int getNumWeapons() {
		return numWeapons;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public Solution getSuggestion() {
		return suggestion;
	}
	
	public Solution getAccusation() {
		return accusation;
	}
	
	public static AccusationDialog getAccusationDialog() {
		return Board.accusationDialog;
	}
	public static boolean getisStartOfTurn(){
		return Board.isStartOfTurn;
	}
	public void setCurrentPlayer(Player player) {
		Board.currentPlayer = player;
	}
	
	public void setSuggestion(Solution suggestion) {
		Board.suggestion = suggestion;
	}
	
	public void setAccusation(Solution accusation) {
		Board.accusation = accusation;
	}
	
	public void setisStartOfTurn(boolean bol){
		Board.isStartOfTurn = bol;
	}
}
