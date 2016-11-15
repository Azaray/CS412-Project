package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import datastructures.SearchQuery;
import view.View;

import model.Model;
import model.Searcher;

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

		// tell Model about View.
		Model.addObserver(myView);

		// create Controller. tell it about Model and View, initialise model
		myController.addModel(Model);
		myController.addView(myView);
		
		// Test query
		Model.Search(new SearchQuery("but to shrouding"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Controller: actionPerformed() : The " + e.getActionCommand() + " button is clicked at "
				+ new java.util.Date(e.getWhen()) + " with e.paramString " + e.paramString());
		
		if (e.getActionCommand().equals("Refresh")) this.Model.Refresh();
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
