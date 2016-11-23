package datastructures;

import java.util.ArrayList;
import java.util.List;

public class QueryResultList {
	
	private List<QueryResult> mResults = new ArrayList<QueryResult>();

	public List<QueryResult> getResults() {
		return mResults;
	}

	public void addResults(QueryResult result) {
		this.mResults.add(result);
	}
	
	public int size() {
		return mResults.size();
	}
	
}
