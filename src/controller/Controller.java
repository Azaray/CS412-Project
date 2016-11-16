package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import datastructures.SearchField;
import datastructures.SearchQuery;
import model.Model;
import view.View;

public class Controller implements ActionListener {

	private Model Model;
	private View View;

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

		System.out.println("Controller: actionPerformed() : The " + e.getActionCommand() + " button is clicked at "
				+ new java.util.Date(e.getWhen()) + " with e.paramString " + e.paramString());
	}

	private void Search() {
		SearchQuery searchQuery = new SearchQuery(this.View.getSearchField(), SearchField.DOCCONTENT, false);
		this.Model.Search(searchQuery);
	}

	private void NextPage() {
		if ((this.View.getPageNumber() + 1) * 10 < this.View.getSuggestionsList().size()) {
			this.View.setPageNumber(this.View.getPageNumber() + 1);
			this.View.fillSuggestions();
		} else {
			System.out.println("End reached");
		}
	}

	private void PreviousPage() {
		if ((this.View.getPageNumber()) > 0) {
			this.View.setPageNumber(this.View.getPageNumber() - 1);
			this.View.fillSuggestions();
		} else {
			System.out.println("End reached");
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
	}
}
