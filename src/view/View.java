package view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import controller.SuggestionsExpansion;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import controller.Controller;
import datastructures.QueryResult;
import datastructures.QueryResultList;
import datastructures.SearchField;
import datastructures.SearchQuery;

public class View extends JFrame implements Observer {

	private List<SearchQuery> searchString = new ArrayList<SearchQuery>();

	private Controller Controller;
	private JTextField searchField;
	private QueryResultList suggestionsList;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList listSuggestions;
	private int pageNumber;
	private final JMenuBar menuBar = new JMenuBar();
	private static final long seerialVersionUID = -7574733018145634162L;
	private JLabel allResults;
	private JLabel selectedResults;
	private JTextArea expandedResult;
	private JScrollPane scroll;
	private JScrollPane scroll2;
	private JCheckBox isExact;
	private JCheckBox isSentence;
	private boolean isAdvanced;
	private JLabel expandedID;
	private JPanel resultExpanded;

	private JTextField advancedSearchField;

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

	public boolean isAdvanced() {
		return isAdvanced;
	}

	 public void setIsAdvanced(boolean isAdvanced) {
		 this.isAdvanced = isAdvanced;
	 }

	public List<SearchQuery> getSearchQuery() {
		return searchString;
	}

	public void addSearchString(SearchQuery searchString) {
		this.searchString.add(searchString);
	}
	
	public void setSearchString(List<SearchQuery> searchString) {
		this.searchString = (searchString);
	}

	private void setSuggestionsList(QueryResultList documents) {
		suggestionsList = documents;
	}

	public QueryResultList getSuggestionsList() {
		return suggestionsList;
	}

	public String getSearchField() {
		return searchField.getText();
	}

	public String getAdvancedSearchField() {
		return advancedSearchField.getText();
	}

	/**
	 * A method to create a simple file menu
	 */
	private void createFileMenu() {
		
		System.out.println("View: createFileMenu()");
		
		// Create menu
		JMenu menuFile = new JMenu("File");
		
		// Create menu items
		JMenuItem SaveHistory = new JMenuItem("Save History");
		JMenuItem menuRefresh = new JMenuItem("Refresh");
		JMenuItem menuExit = new JMenuItem("Exit");
		
		// Add the menu bar to the frame, and the menu items to the menu
		setJMenuBar(menuBar);
		menuBar.add(menuFile);
		menuFile.add(SaveHistory);
		menuFile.add(menuRefresh);
		menuFile.add(menuExit);
		
		// Add action listeners to the menu items
		menuRefresh.addActionListener(Controller);
		SaveHistory.addActionListener(Controller);
		menuExit.addActionListener(Controller);
	}



	private void createAndShowGUI() {
		//Create and set up the window.


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());


		//search panel

		JPanel searchPanel = new JPanel();
		JPanel advancedSearchPanel = new JPanel();
		searchPanel.setLayout(new FlowLayout());
		advancedSearchPanel.setLayout(new FlowLayout());

		JTabbedPane tabbedPane = new JTabbedPane();


		JLabel searchDescription = new JLabel("Query..");
		JLabel advancedSearchDescription = new JLabel("Query...");
		searchField = new JTextField();
		searchField.setColumns(30);
		advancedSearchField = new JTextField();
		advancedSearchField.setColumns(30);
		expandedResult = new JTextArea(5, 50);
		expandedResult.setEditable(false);
		expandedResult.setLineWrap(true);
		expandedResult.setWrapStyleWord(true);
		scroll = new JScrollPane (expandedResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JButton searchButton = new JButton("Search");
		JButton advancedSearchButton = new JButton("Advanced Search");
		searchButton.addActionListener(Controller);
		advancedSearchButton.addActionListener(Controller);
		searchPanel.add(searchDescription);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);

		advancedSearchPanel.add(advancedSearchDescription);
		advancedSearchPanel.add(advancedSearchField);

		isExact = new JCheckBox("Exact words");
		isSentence = new JCheckBox("Exact sentence");
		advancedSearchPanel.add(isExact);
		advancedSearchPanel.add(isSentence);

		advancedSearchPanel.add(advancedSearchButton);


		tabbedPane.addTab("Simple Search", null, searchPanel, "Simple Search");
		tabbedPane.addTab("Advanced Search", null, advancedSearchPanel, "Advanced Search");


		contentPane.add(tabbedPane, BorderLayout.NORTH);

		JLabel suggestions = new JLabel("Suggestions..");




		JPanel suggestionListPanel = new JPanel();
		suggestionListPanel.setLayout(new BoxLayout(suggestionListPanel, BoxLayout.Y_AXIS));
		listModel = new DefaultListModel<>();
		ArrayList<JButton> actionsList = new ArrayList<>();

		for(int i=0; i<10; i++) {
			String iStr = Integer.toString(i+1);
			listModel.addElement("");

		}

		listSuggestions = new JList(listModel);
		listSuggestions.addMouseListener(new SuggestionsExpansion(listSuggestions, Controller));
		scroll2 = new JScrollPane(listSuggestions);
		suggestionListPanel.add(scroll2);

		suggestionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(suggestionListPanel, BorderLayout.WEST);
		resultExpanded = new JPanel();
		resultExpanded.setLayout(new BoxLayout(resultExpanded, BoxLayout.Y_AXIS));
		getContentPane().add(resultExpanded, BorderLayout.EAST);

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
		setSize(600,600);

	}

	public boolean advancedIsExact() {
		return isExact.isSelected();
	}

	public boolean advancedIsSentence() {
		return isSentence.isSelected();
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
		int index = listSuggestions.getSelectedIndex()+(getPageNumber()*10);

		Document doc = readDocument(suggestionsList.getResults().get(index).getmDocID());
		
		if(doc != null) {
			String title = doc.get(SearchField.DOCNO.field());
			String date = doc.get(SearchField.DATE.field());
			if(date != null) {

			} else {
				date = "Date unavailable";
			}
			String text2 = doc.get(SearchField.DOCCONTENT.field());
			text2 = date + "\r\n\r\n" + text2.replaceAll("&hyph;", "-");



			expandedResult.setText(text2);
			String text = text2.toLowerCase();
			List<String> splitted = new ArrayList<String>();
			
			for(SearchQuery squery : searchString)
				splitted.addAll(squery.getHighlightQueryList());

			Highlighter highlighter = expandedResult.getHighlighter();
			Highlighter.HighlightPainter painterExact = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
			Highlighter.HighlightPainter painterNotExact = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

			for(String str : splitted) {
				String word = str.toLowerCase();
				int p0 = 0;

				int p1 = p0 + word.length();
				while(true) {
					p0 = text.indexOf(word, p0+1);
					p1 = p0 + word.length();
					System.out.println(word + ":" + p0 + ":" + p1);
					if(p0 < 0) {
						break;
					}

					if(Character.isLetter(text.charAt(p0-1)) || Character.isDigit(text.charAt(p0-1))
							|| Character.isLetter(text.charAt(p1)) || Character.isDigit(text.charAt(p1))) {
						if(!advancedIsExact() || !isAdvanced) {
							highlighter.addHighlight(p0, p1, painterNotExact);
						}
					} else {
						highlighter.addHighlight(p0, p1, painterExact);
					}
				}
			}



			expandedID = new JLabel(title);


			resultExpanded.removeAll();
			resultExpanded.add(expandedID);
			//resultExpanded.add(expandedDate);
			resultExpanded.add(scroll);

			scroll.setSize(new Dimension(600, 600));

			invalidate();
			validate();
			repaint();
		}
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
			QueryResult result = suggestionsList.getResults().get(start+i);
			listModel.set(i, "<html><font color='blue'>" + result.getmDocName() + "</font><br>" + getMentions(result, 7) + "</html>");
		}
		
		for(int i=finish; i<10; i++) {
			listModel.set(i, "");
		}

		if(suggestionsList.size() == 0) {
			listModel.set(0, "No results");
		}

		invalidate();
		validate();
		repaint();
	}

	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof QueryResultList) {
			setPageNumber(0);
			setSuggestionsList(((QueryResultList) obj));
			fillSuggestions();
			setAllResults(Integer.toString(suggestionsList.size()));
			setSelectedResults("0-10");
		}
	}
	
	private Document readDocument(int docId) {
		 
		Path path = Paths.get("index/live");
		IndexReader reader;
		IndexSearcher searcher;
		try {
			reader = DirectoryReader.open(FSDirectory.open(path));
			searcher = new IndexSearcher(reader);
			return searcher.doc(docId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	private String getMentions(QueryResult result, int limit) {
		int i = 0;
		StringBuilder strb = new StringBuilder();
		Map<String, Integer> mentions = result.getmQueryMentions();
		for(String word : mentions.keySet()) {
			if(i > limit) {
				break;
			}
			strb.append(word + " mentioned " + mentions.get(word) + " time(s). <br>");
			i++;
		}
		
		return strb.toString();
	}
}



