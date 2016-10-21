package model;

import java.util.Observable;

public class Model extends Observable {

	public Model(){
		System.out.println("Model: Model()");
	}
	
	public void Refresh() {
		System.out.println("Model: Refresh()");
		setChanged();
		notifyObservers("Refresh");
	}
}
