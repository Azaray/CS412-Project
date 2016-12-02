package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controller.Controller;
import datastructures.SearchField;
import datastructures.SearchQuery;

public class SearchView extends JFrame implements Observer {

	private List<SearchQuery> mSearchQueryList = new ArrayList<SearchQuery>();
	private Map<SearchField, SearchFieldPanel> searchItems = new HashMap<SearchField, SearchFieldPanel>();

	private SearchField searchFieldToAdd;
	private Controller Controller;
	private final JMenuBar menuBar = new JMenuBar();
	private static final long serialVersionUID = -7574733018145634162L;
	private JScrollPane searchFieldsScroller;
	private JPanel searchFields;
	private GridLayout searchFieldslayout;

	public SearchView(Controller myController) {
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
		setSize(800, 400);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	public List<SearchQuery> getSearchQuery() {
		return mSearchQueryList;
	}

	public void addSearchString(SearchQuery searchString) {
		this.mSearchQueryList.add(searchString);
	}

	public void setSearchString(List<SearchQuery> searchString) {
		this.mSearchQueryList = (searchString);
	}

	public Map<SearchField, SearchFieldPanel> getSearchItems() {
		return searchItems;
	}

	private void createAndShowGUI() {

		// Create and set up the window.
		Container contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout());

		searchFieldsScroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		searchFields = new JPanel();

		int h = 800;
		int w = 300;

		searchFieldsScroller.setPreferredSize(new Dimension(h, w));
		searchFieldsScroller.setSize(new Dimension(h, w));

		searchFieldslayout = new GridLayout(4, 1);
		searchFields.setLayout(searchFieldslayout);
		searchFieldsScroller.setViewportView(searchFields);
		contentPane.add(searchFieldsScroller);

		JButton addField = new JButton("Add Field");
		addField.addActionListener(Controller);

		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(Controller);

		contentPane.add(searchButton);
		contentPane.add(addField);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SearchFieldPanel searchField = new SearchFieldPanel(SearchField.DOCCONTENT, Controller);

		searchFieldslayout.setRows(searchFieldslayout.getRows() + 1);
		searchFields.add(searchField);
		searchItems.put(SearchField.DOCCONTENT, searchField);

	}

	private void createFileMenu() {

		System.out.println("View: createFileMenu()");

		// Create menu
		JMenu menuFile = new JMenu("File");

		// Create menu items
		JMenuItem SaveHistory = new JMenuItem("Save History");
		JMenuItem menuIndex = new JMenuItem("Index Dataset");
		JMenuItem menuExit = new JMenuItem("Exit");

		// Add the menu bar to the frame, and the menu items to the menu
		setJMenuBar(menuBar);
		menuBar.add(menuFile);
		menuFile.add(SaveHistory);
		menuFile.add(menuIndex);
		menuFile.add(menuExit);

		// Add action listeners to the menu items
		menuIndex.addActionListener(Controller);
		SaveHistory.addActionListener(Controller);
		menuExit.addActionListener(Controller);
	}

	public void addSearchField() {

		showAddFieldOptions();

		if (searchFieldToAdd != null) {
			if (!searchItems.containsKey(searchFieldToAdd)) {

				SearchFieldPanel searchField = new SearchFieldPanel(searchFieldToAdd, Controller);

				if(searchItems.size() > 4)
					searchFieldslayout.setRows(searchFieldslayout.getRows() + 1);
				searchFields.add(searchField);
				searchItems.put(searchFieldToAdd, searchField);
				revalidate();
			}
		}

	}

	public void removeSearchField(SearchField field) {

		if (searchItems.containsKey(field)) {
			if(searchItems.size() > 4)
				searchFieldslayout.setRows(searchFieldslayout.getRows() - 1);
			searchFields.remove(searchItems.get(field));
			searchItems.remove(field);
			revalidate();
		}
	}

	@Override
	public void update(Observable o, Object obj) {
	}

	private void showAddFieldOptions() {

		JPanel panel = new JPanel();
		panel.add(new JLabel("Choose a field to add:"));
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();

		for (SearchField sf : SearchField.values()) {
			if (!searchItems.containsKey(sf) && !sf.equals(SearchField.ALL) && !sf.equals(SearchField.PATH)
					&& !sf.equals(SearchField.LASTMODIFIED))
				model.addElement(sf.description());
		}

		JComboBox<String> comboBox = new JComboBox<String>(model);
		panel.add(comboBox);

		int result = JOptionPane.showConfirmDialog(null, panel, "New Search Field", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		switch (result) {
		case JOptionPane.OK_OPTION:
			System.out.println("You selected " + comboBox.getSelectedItem());
			searchFieldToAdd = SearchField.getItem((String) comboBox.getSelectedItem());
			break;
		default:
			searchFieldToAdd = null;
		}

	}

}
