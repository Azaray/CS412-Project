package view;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import controller.Controller;
import datastructures.QueryResult;
import datastructures.SearchField;
import datastructures.SearchQuery;
import org.apache.lucene.document.Document;

public class View extends JFrame implements Observer {

	private String searchString;

	private Controller Controller;
	private JTextField searchField;
	private ArrayList<Document> suggestionsList;
	private ArrayList<JLabel> suggestionLabels;
	private int pageNumber;
	private final JMenuBar menuBar = new JMenuBar();
	private static final long serialVersionUID = -7574733018145634162L;
	private JLabel allResults;
	private JLabel selectedResults;
	private JComboBox resultsToGo;
	private JButton go;
	private JTextArea expandedResult;
	private JScrollPane scroll;

	private boolean isExpanded;
	
	public View(Controller myController) {

		System.out.println("View: View()");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(610, 380));
		
		System.out.println("View: adding controller");
		Controller = myController;	
		
		// Create the file menu
		createFileMenu();
		
		setResizable(false);
		createAndShowGUI();

		pack();
		setSize(900,600);
		setVisible(true);
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	private void setSuggestionsList(ArrayList<Document> documents) {
		suggestionsList = documents;
	}

	public ArrayList<Document> getSuggestionsList() {
		return suggestionsList;
	}

	public String getSearchField() {
		return searchField.getText();
	}

	/**
	 * A method to create a simple file menu
	 */
	private void createFileMenu() {
		
		System.out.println("View: createFileMenu()");
		
		// Create menu
		JMenu menuFile = new JMenu("File");
		
		// Create menu items
		JMenuItem menuRefresh = new JMenuItem("Refresh");
		JMenuItem menuExit = new JMenuItem("Exit");
		
		// Add the menu bar to the frame, and the menu items to the menu
		setJMenuBar(menuBar);
		menuBar.add(menuFile);
		menuFile.add(menuRefresh);
		menuFile.add(menuExit);
		
		// Add action listeners to the menu items
		menuRefresh.addActionListener(Controller);

		menuExit.addActionListener(Controller);
	}

	public boolean getIsExpanded() {
		return isExpanded;
	}


	private void createAndShowGUI() {
		//Create and set up the window.
		//JFrame frame = new JFrame("Search...");


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());


		//search panel

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout());

		JLabel searchDescription = new JLabel("Enter keywords..");
		searchField = new JTextField();
		searchField.setColumns(30);
		expandedResult = new JTextArea(5, 50);
		expandedResult.setEditable(false);
		expandedResult.setLineWrap(true);
		expandedResult.setWrapStyleWord(true);
		scroll = new JScrollPane (expandedResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(Controller);
		searchPanel.add(searchDescription);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		contentPane.add(searchPanel, BorderLayout.NORTH);

		JLabel suggestions = new JLabel("Suggestions..");




		JPanel suggestionListPanel = new JPanel();
		suggestionListPanel.setLayout(new BoxLayout(suggestionListPanel, BoxLayout.Y_AXIS));

		suggestionLabels = new ArrayList<JLabel>();
		ArrayList<JButton> actionsList = new ArrayList<>();

		for(int i=0; i<10; i++) {
			String iStr = Integer.toString(i+1);
			JLabel suggestion = new JLabel();
			suggestionLabels.add(suggestion);
			suggestionLabels.get(i).setAlignmentX(Component.LEFT_ALIGNMENT);
			suggestionListPanel.add(suggestionLabels.get(i));

		}

		suggestionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(suggestionListPanel, BorderLayout.WEST);

		JPanel navigation = new JPanel();
		navigation.setLayout(new FlowLayout());

		allResults = new JLabel();
		selectedResults = new JLabel();
		JLabel resulsToGoLabel = new JLabel("Result :");
		resultsToGo = new JComboBox();
		resultsToGo.addItem("No results");
		JButton previous = new JButton("Previous");
		previous.addActionListener(Controller);
		JButton next = new JButton("Next");
		next.addActionListener(Controller);
		go = new JButton("Go");
		go.addActionListener(Controller);

		navigation.add(allResults);
		navigation.add(selectedResults);
		navigation.add(previous);
		navigation.add(next);
		navigation.add(resulsToGoLabel);
		navigation.add(resultsToGo);
		navigation.add(go);


		contentPane.add(navigation, BorderLayout.SOUTH);
		setSize(600,600);

	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getAllResults() {
		return allResults.getText();
	}

	public void setAllResults(String results) {
		this.allResults.setText("Results: " + results);
	}

	public String getSelectedResults() {
		return selectedResults.getText();
	}

	public void setSelectedResults(String results) {
		this.selectedResults.setText("; " + results);
	}


	public void expandSuggestion() throws BadLocationException {
		int index = resultsToGo.getSelectedIndex()+(getPageNumber()*10);
		String text = suggestionsList.get(index).get(SearchField.DOCCONTENT.field());
		text = text.toLowerCase();
		expandedResult.setText(text);

		String[] splitted = searchString.split(" ");;//searchField.getText().split(" ");
		for(int y=0; y<splitted.length; y++) {
			splitted[y] = splitted[y].substring(0, splitted[y].length()-1);
		}
		System.out.println(searchString);
		Highlighter highlighter = expandedResult.getHighlighter();
		Highlighter.HighlightPainter painterExact = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
		Highlighter.HighlightPainter painterNotExact = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

		for(int i=0; i<splitted.length; i++) {
			String word = splitted[i].toLowerCase();
			int p0 = 0;

			int p1 = p0 + word.length();
			while(true) {
				p0 = text.indexOf(word, p0+1);
				p1 = p0 + word.length();
				//System.out.println(word + ":" + p0 + ":" + p1);
				if(p0 < 0) {
					break;
				}




				if(Character.isLetter(text.charAt(p0-1)) || Character.isDigit(text.charAt(p0-1))
						|| Character.isLetter(text.charAt(p1)) || Character.isDigit(text.charAt(p1))) {
					highlighter.addHighlight(p0, p1, painterNotExact);
				} else {
					highlighter.addHighlight(p0, p1, painterExact);
				}



				//counter2++;
			}


		}


		getContentPane().add(scroll, BorderLayout.EAST);
		invalidate();
		validate();
		repaint();
	}

	public void fillSuggestions() {
		int finish;
		int start = pageNumber*10;
		if(10 > (suggestionsList.size() - start)) {
			finish = suggestionsList.size() - start;
		} else {
			finish = 10;
		}

		resultsToGo.removeAllItems();

		for(int i=0; i<finish; i++) {
			suggestionLabels.get(i).setText(suggestionsList.get(start+i).get(SearchField.DOCNO.field()));
			resultsToGo.addItem(suggestionsList.get(start+i).get(SearchField.DOCNO.field()));
			//suggestionLabels.get(i).setText(suggestionsList.get(start+i).toString().substring(20));
		}

		for(int i=finish; i<10; i++) {
			suggestionLabels.get(i).setText("");
		}

		invalidate();
		validate();
		repaint();
	}

	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof QueryResult) {
			setPageNumber(0);
			setSuggestionsList(((QueryResult) obj).getDocuments());
			fillSuggestions();
			setAllResults(Integer.toString(suggestionsList.size()));
			setSelectedResults("0-10");
		}
	}
}



