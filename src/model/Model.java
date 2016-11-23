package model;

import java.util.Observable;

import datastructures.QueryResultList;
import datastructures.SearchQuery;

public class Model extends Observable {

	public Model(){
		System.out.println("Model: Model()");
	}
	
	public void Refresh() {
		System.out.println("Model: Refresh()");
		setChanged();
		notifyObservers("Refresh");
	}
	
	public void Search(SearchQuery query) {
		QueryResultList result = Searcher.Search(query);
		setChanged();
		notifyObservers(result);
	}
}
