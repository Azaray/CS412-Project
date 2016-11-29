package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import datastructures.QueryResultList;
import datastructures.SearchQuery;

public class Model extends Observable {
	
	private ArrayList<String> departments = new ArrayList<String>();
	private ArrayList<String> bureaus = new ArrayList<String>();
	private ArrayList<String> agencies = new ArrayList<String>();

	public Model(){
		System.out.println("Model: Model()");
		readFiles(departments, "departments.txt");
		readFiles(bureaus, "bureaus.txt");
		readFiles(agencies, "agencies.txt");
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
	
	public void readFiles(ArrayList<String> list, String targetFile) {
		try{
		    InputStream fis=new FileInputStream(targetFile);
		    BufferedReader br=new BufferedReader(new InputStreamReader(fis));

		    for (String line = br.readLine(); line != null; line = br.readLine()) {
		       list.add(line);
		    }

		    br.close();
		}
		catch(Exception e){
		    System.err.println("Error: Target File Cannot Be Read");
		}
	}

}