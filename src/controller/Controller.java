package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import datastructures.SearchQuery;
import model.Model;
import view.View;

public class Controller implements ActionListener {

	private Model Model;
	private View View;

	public Controller() {
		System.out.println("Controller: Controller()");
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

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Controller: actionPerformed() : The " + e.getActionCommand() + " button is clicked at "
				+ new java.util.Date(e.getWhen()) + " with e.paramString " + e.paramString());
		
		if (e.getActionCommand().equals("Refresh")) this.Model.Refresh();
		else if(e.getActionCommand().equals("Search")) {
			SearchQuery searchQuery = new SearchQuery(this.View.getSearchField());
			this.Model.Search(searchQuery);
		} else if(e.getActionCommand().equals("Next")) {
			if((this.View.getPageNumber()+1) * 10 < this.View.getSuggestionsList().size()) {
				this.View.setPageNumber(this.View.getPageNumber()+1);
				this.View.fillSuggestions();
			} else {
				System.out.println("End reached");
			}

		} else if(e.getActionCommand().equals("Previous")) {
			if((this.View.getPageNumber()) > 0) {
				this.View.setPageNumber(this.View.getPageNumber()-1);
				this.View.fillSuggestions();
			} else {
				System.out.println("End reached");
			}

		}
	}

	public void addModel(Observable m) {
		System.out.println("Controller: adding Model");
		this.Model = (Model) m;
	}

	public void addView(Observer v) {
		System.out.println("Controller: adding view");
		this.View = (View) v;
	}

}
