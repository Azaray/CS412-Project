package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import datastructures.SearchField;
import datastructures.SearchQuery;
import model.Indexer;
import model.Model;
import view.View;

import javax.swing.text.BadLocationException;

public class Controller implements ActionListener {

	private Model Model;
	private View View;
	private List<SearchQuery> searchQuery;

	public Controller() {
		System.out.println("Controller: Controller()");
	}

	public void addModel(Observable m) {
		System.out.println("Controller: adding Model");
		this.Model = (Model) m;
	}

	public void addView(Observer v) {
		System.out.println("Controller: adding view");
		this.View = (View) v;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("Refresh"))
			this.Model.Refresh();
		else if (e.getActionCommand().equals("Search"))
			Search();
		else if (e.getActionCommand().equals("Next"))
			NextPage();
		else if (e.getActionCommand().equals("Previous"))
			PreviousPage();
		else if(e.getActionCommand().equals("Go"))
			Go();
		else if(e.getActionCommand().equals("Back"))
			Back();
		else if(e.getActionCommand().equals("Advanced Search"))
			AdvancedSearch();
		else if (e.getActionCommand().equals("Save History"))
			SearchQuery.saveSearch();

		System.out.println("Controller: actionPerformed() : The " + e.getActionCommand() + " button is clicked at "
				+ new java.util.Date(e.getWhen()) + " with e.paramString " + e.paramString());
	}

	private void Search() {
		this.View.setIsAdvanced(false);
		searchQuery = new ArrayList<SearchQuery>();
		searchQuery.add(new SearchQuery(this.View.getSearchField(), SearchField.DOCCONTENT, false, false));
		this.Model.Search(searchQuery);
	}

	private void AdvancedSearch() {
		this.View.setIsAdvanced(true);
		searchQuery = new ArrayList<SearchQuery>();
		searchQuery.add(new SearchQuery(this.View.getAdvancedSearchField(), SearchField.DOCCONTENT, this.View.advancedIsExact(), this.View.advancedIsSentence()));
		this.Model.Search(searchQuery);
	}

	private void NextPage() {
		if ((this.View.getPageNumber() + 1) * 10 < this.View.getSuggestionsList().size()) {
			this.View.setPageNumber(this.View.getPageNumber() + 1);
			this.View.fillSuggestions();
			this.View.setSelectedResults(this.View.getPageNumber()*10 + "-" + (this.View.getPageNumber()+1)*10);
		}
	}

	public void Go() {
		try {
			this.View.setSearchString(searchQuery);
			this.View.expandSuggestion();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void Back() {
		this.View.fillSuggestions();
	}

	private void PreviousPage() {
		if ((this.View.getPageNumber()) > 0) {
			this.View.setPageNumber(this.View.getPageNumber() - 1);
			this.View.fillSuggestions();
			this.View.setSelectedResults(this.View.getPageNumber()*10 + "-" + (this.View.getPageNumber()+1)*10);
		}
	}

	public static void main(String[] args) {

		// Create Model and Controller
		Model Model = new Model();
		Controller myController = new Controller();

		// tell View about Controller ,
		View myView = new View(myController);// Create View
		myView.setPageNumber(0);

		// tell Model about View.
		Model.addObserver(myView);

		// create Controller. tell it about Model and View, initialise model
		myController.addModel(Model);
		myController.addView(myView);
		
		//Doesn't have to be called every time
//		Indexer.indexFiles("index", "docs", true);
	}
}
