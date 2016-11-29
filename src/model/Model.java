package model;

import java.util.List;
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
	
	public void Search(List<SearchQuery> query) {
		QueryResultList result = Searcher.Search(query);
		setChanged();
		notifyObservers(result);
	}
}
