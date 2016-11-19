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
	private JLabel allResults;
	private JLabel selectedResults;
	
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

		allResults = new JLabel();
		selectedResults = new JLabel();
		JButton previous = new JButton("Previous");
		previous.addActionListener(Controller);
		JButton next = new JButton("Next");
		next.addActionListener(Controller);

		navigation.add(allResults);
		navigation.add(selectedResults);
		navigation.add(previous);
		navigation.add(next);


		contentPane.add(navigation, BorderLayout.SOUTH);

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

	public void fillSuggestions() {
		int finish;
		int start = pageNumber*10;
		if(10 > (suggestionsList.size() - start)) {
			finish = suggestionsList.size() - start;
		} else {
			finish = 10;
		}


		for(int i=0; i<finish; i++) {
			suggestionLabels.get(i).setText(suggestionsList.get(start+i).get("docno"));

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



