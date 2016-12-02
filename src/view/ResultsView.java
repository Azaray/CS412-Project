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

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import controller.Controller;
import controller.SuggestionsExpansion;
import datastructures.QueryResult;
import datastructures.QueryResultList;
import datastructures.SearchField;
import datastructures.SearchQuery;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class ResultsView extends JFrame implements Observer {

	private List<SearchQuery> searchString = new ArrayList<SearchQuery>();
	private Controller Controller;
	private QueryResultList suggestionsList;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> listSuggestions;
	private int pageNumber;
	private static final long serialVersionUID = -7574733018145634162L;
	private JLabel allResults;
	private JLabel selectedResults;
	private JTextArea expandedResult;
	private JScrollPane scroll;
	private JScrollPane scroll2;
	private JLabel expandedID;
	private JPanel resultExpanded;

	private SearchView SearchView;

	public ResultsView(Controller myController, SearchView searchView) {

		Controller = myController;
		SearchView = searchView;

		createAndShowGUI();
	}

	private JPanel createSuggestionsPanel() {

		JPanel suggestionListPanel = new JPanel();
		suggestionListPanel.setLayout(new BoxLayout(suggestionListPanel, BoxLayout.Y_AXIS));

		JLabel resultsLabel = new JLabel("Results..");

		listModel = new DefaultListModel<>();
		ArrayList<JButton> actionsList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			String iStr = Integer.toString(i + 1);
			listModel.addElement("");
		}

		listSuggestions = new JList<String>(listModel);
		listSuggestions.addMouseListener(new SuggestionsExpansion(listSuggestions, Controller));

		scroll2 = new JScrollPane(listSuggestions);
		//scroll2.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		scroll2.setMaximumSize(new Dimension(330, 900));

		suggestionListPanel.add(scroll2);
		suggestionListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		suggestionListPanel.add(resultsLabel);

		return suggestionListPanel;
	}

	private JPanel createNavigationPanel() {

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

		return navigation;
	}

	private void createAndShowGUI() {

		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(610, 380));

		// Create and set up the window.
		Container contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());
		contentPane.add(createSuggestionsPanel(), BorderLayout.WEST);
		contentPane.add(createNavigationPanel(), BorderLayout.SOUTH);

		expandedResult = new JTextArea(5, 50);
		expandedResult.setEditable(false);
		expandedResult.setLineWrap(true);
		expandedResult.setWrapStyleWord(true);

		scroll = new JScrollPane(expandedResult, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				HORIZONTAL_SCROLLBAR_NEVER);

		pack();
		setSize(800, 500);
		setVisible(false);
		setLocationRelativeTo(null);
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
		int index = listSuggestions.getSelectedIndex() + (getPageNumber() * 10);

		Document doc = readDocument(suggestionsList.getResults().get(index).getmDocID());

		if (doc != null) {
			String title = doc.get(SearchField.DOCNO.field());
			String date = doc.get(SearchField.DATE.field());
			if (date != null) {

			} else {
				date = "Date unavailable";
			}
			String text2 = doc.get(SearchField.DOCCONTENT.field());
			text2 = date + "\r\n\r\n" + text2.replaceAll("&hyph;", "-");



			expandedResult.setText(text2);

			String text = text2.toLowerCase();
			List<String> splitted = new ArrayList<String>();

			for (SearchQuery squery : searchString)
				if(squery.getSearchField().equals(SearchField.DOCCONTENT))
					splitted.addAll(squery.getHighlightQueryList());

			Highlighter highlighter = expandedResult.getHighlighter();
			Highlighter.HighlightPainter painterExact = new DefaultHighlighter.DefaultHighlightPainter(Color.green);
			Highlighter.HighlightPainter painterNotExact = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);

			Map<SearchField, SearchFieldPanel> searchitems = SearchView.getSearchItems();
			for (SearchField sf : searchitems.keySet()) {
				if(sf.equals(SearchField.DOCCONTENT)) {
					SearchFieldPanel searchpanel = searchitems.get(sf);
					
					for (String str : splitted) {
						String word = str.toLowerCase();
						int p0 = 0;

						int p1 = p0 + word.length();
						while (true) {
							p0 = text.indexOf(word, p0 + 1);
							p1 = p0 + word.length();
							if (p0 < 0) {
								break;
							}

							if (Character.isLetter(text.charAt(p0 - 1)) || Character.isDigit(text.charAt(p0 - 1))
									|| Character.isLetter(text.charAt(p1)) || Character.isDigit(text.charAt(p1))) {
								if (!searchpanel.isExact()) {
									highlighter.addHighlight(p0, p1, painterNotExact);
								}
							} else {
								highlighter.addHighlight(p0, p1, painterExact);
							}
						}
					}
				}

			}
			
			expandedID = new JLabel(title);

			resultExpanded.removeAll();
			resultExpanded.add(expandedID);
			resultExpanded.add(scroll);

			//scroll.setSize(new Dimension(250, 600));

			invalidate();
			validate();
			repaint();
		}
	}

	public void fillSuggestions() {
		int finish;
		int start = pageNumber * 10;
		if (10 > (suggestionsList.size() - start)) {
			finish = suggestionsList.size() - start;
		} else {
			finish = 10;
		}

		for (int i = 0; i < finish; i++) {
			QueryResult result = suggestionsList.getResults().get(start + i);
			listModel.set(i, "<html><font color='blue'>" + result.getmDocName() + "</font><br>" + getMentions(result, 7)
					+ "</html>");
		}

		for (int i = finish; i < 10; i++) {
			listModel.set(i, "");
		}

		if (suggestionsList.size() == 0) {
			listModel.set(0, "No results");
		}

		invalidate();
		validate();
		repaint();
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

	@Override
	public void update(Observable o, Object obj) {
		if (obj instanceof QueryResultList) {
			setPageNumber(0);
			setSuggestionsList(((QueryResultList) obj));
			fillSuggestions();
			setAllResults(Integer.toString(suggestionsList.size()));
			setSelectedResults("0-10");
		}
	}

	private Document readDocument(int docId) {

		Path path = Paths.get("index");
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
		for (String word : mentions.keySet()) {
			if (i > limit) {
				break;
			}
			strb.append(word + " mentioned " + mentions.get(word) + " time(s). <br>");
			i++;
		}

		return strb.toString();
	}
}
