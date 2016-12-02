package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import datastructures.SearchField;
import datastructures.SearchQuery;
import model.HistoryWriter;
import model.Indexer;
import model.Model;
import view.ResultsView;
import view.SearchFieldPanel;
import view.SearchView;

import javax.swing.JPanel;
import javax.swing.text.BadLocationException;

import org.apache.lucene.store.MMapDirectory;

public class Controller implements ActionListener {

	private Model mModel;
	private SearchView mSearchView;
	private ResultsView mResultsView;
	private List<SearchQuery> mSearchQueryList;

	public Controller() {
		System.out.println("Controller: Controller()");
	}

	public void addModel(Observable m) {
		System.out.println("Controller: adding Model");
		this.mModel = (Model) m;
	}

	public void addView(Observer v) {
		System.out.println("Controller: adding view");
		this.mSearchView = (SearchView) v;
	}

	public void addResultsView(Observer v) {
		System.out.println("Controller: adding view");
		this.mResultsView = (ResultsView) v;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("Index Dataset"))
			Index();
		else if (e.getActionCommand().equals("Search"))
			Search();
		else if (e.getActionCommand().equals("Add Field"))
			mSearchView.addSearchField();
		else if (e.getActionCommand().equals("Next"))
			NextPage();
		else if (e.getActionCommand().equals("Previous"))
			PreviousPage();
		else if (e.getActionCommand().equals("Go"))
			Go();
		else if (e.getActionCommand().equals("Back"))
			Back();	
		else if (e.getActionCommand().equals("Load History"))
			mSearchView.loadHistory();
		else {
			for (SearchField sf : SearchField.values()) {
				if (e.getActionCommand().equals("Remove" + sf.field()))
					mSearchView.removeSearchField(sf);
			}
		}

		System.out.println("Controller: actionPerformed() : The " + e.getActionCommand() + " button is clicked at "
				+ new java.util.Date(e.getWhen()) + " with e.paramString " + e.paramString());
	}

	public void Search() {
		
		mResultsView = new ResultsView(this, mSearchView);
		mModel.addObserver(mResultsView);
		mResultsView.setPageNumber(0);
		
		mSearchQueryList = new ArrayList<SearchQuery>();

		Map<SearchField, SearchFieldPanel> items = mSearchView.getSearchItems();

		for (SearchField sf : items.keySet()) {
			SearchFieldPanel item = items.get(sf);
			if (!item.getSearchValue().equals(""))
				mSearchQueryList.add(new SearchQuery(item.getSearchValue(), sf, item.isExact(), item.isSentence()));
		}
		
		HistoryWriter.write(mSearchQueryList);

		this.mModel.Search(mSearchQueryList);
		mResultsView.setVisible(true);
	}
	
	public void Search(List<SearchQuery> list) {
		
		mResultsView = new ResultsView(this, mSearchView);
		mModel.addObserver(mResultsView);
		mResultsView.setPageNumber(0);
		
		mSearchQueryList = list;

		this.mModel.Search(mSearchQueryList);
		mResultsView.setVisible(true);
	}


	private void NextPage() {

		if ((this.mResultsView.getPageNumber() + 1) * 10 < this.mResultsView.getSuggestionsList().size()) {
			this.mResultsView.setPageNumber(this.mResultsView.getPageNumber() + 1);
			this.mResultsView.fillSuggestions();
			this.mResultsView.setSelectedResults(
					this.mResultsView.getPageNumber() * 10 + "-" + (this.mResultsView.getPageNumber() + 1) * 10);
		}
	}

	public void Go() {
		try {
			this.mResultsView.setSearchString(mSearchQueryList);
			this.mResultsView.expandSuggestion();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void Back() {
		this.mResultsView.fillSuggestions();
	}

	private void PreviousPage() {
		if ((this.mResultsView.getPageNumber()) > 0) {
			this.mResultsView.setPageNumber(this.mResultsView.getPageNumber() - 1);
			this.mResultsView.fillSuggestions();
			this.mResultsView.setSelectedResults(
					this.mResultsView.getPageNumber() * 10 + "-" + (this.mResultsView.getPageNumber() + 1) * 10);
		}
	}

	private void Index() {
		Indexer.indexFiles("index", "docs", true);
	}

	public static void main(String[] args) {

		// Create Model and Controller
		Model Model = new Model();
		Controller myController = new Controller();

		// tell View about Controller ,
		SearchView myView = new SearchView(myController);// Create View

		// tell Model about View.
		Model.addObserver(myView);

		// create Controller. tell it about Model and View, initialise model
		myController.addModel(Model);
		myController.addView(myView);
	}
	
	public ArrayList<String> getComboBoxValues(SearchField field) {
		return mModel.getComboBoxValues(field);
	}
}
