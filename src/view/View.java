package view;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


import javax.swing.*;

import controller.Controller;
import datastructures.QueryResult;
import org.apache.lucene.document.Document;

public class View extends JFrame implements Observer {

	private Controller Controller;
	private JTextField searchField;
	private ArrayList<Document> suggestionsList;
	private ArrayList<JLabel> suggestionLabels;
	private int pageNumber;
	private final JMenuBar menuBar = new JMenuBar();
	private static final long serialVersionUID = -7574733018145634162L;
	
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
		setVisible(true);
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
			JLabel suggestion = new JLabel(iStr);
			suggestionLabels.add(suggestion);
			suggestionLabels.get(i).setAlignmentX(Component.LEFT_ALIGNMENT);
			suggestionListPanel.add(suggestionLabels.get(i));

		}

		suggestionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(suggestionListPanel, BorderLayout.WEST);

		JPanel navigation = new JPanel();
		navigation.setLayout(new FlowLayout());

		JButton previous = new JButton("Previous");
		previous.addActionListener(Controller);
		JButton next = new JButton("Next");
		next.addActionListener(Controller);

		navigation.add(previous);
		navigation.add(next);

		contentPane.add(navigation, BorderLayout.SOUTH);

		//Display the window.

		//pack();
		//frame.setSize(700, 700);
		//setVisible(true);
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void fillSuggestions() {
		int finish;
		System.out.println(suggestionsList.size());
		if(10 > suggestionsList.size()) {
			finish = suggestionsList.size();
		} else {
			finish = 10;
		}
		int start = pageNumber*10;

		for(int i=0; i<finish; i++) {
			suggestionLabels.get(i).setText(suggestionsList.get(start+i).toString().substring(60));
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
			setSuggestionsList(((QueryResult) obj).getDocuments());
			fillSuggestions();
		}
	}
}



