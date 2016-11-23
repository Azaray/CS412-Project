package datastructures;

import java.util.HashMap;
import java.util.Map;

public class QueryResult {
	
	private int mDocID;
	private String mDocName;
	private Map<String, Integer> mQueryMentions;
	
	public QueryResult(int docId, String docName) {
		this.mDocID = docId;
		this.mDocName = docName;
		this.mQueryMentions = new HashMap<>();
	}
	
	public void addResult(String word, int mentions) {
		this.mQueryMentions.put(word, mentions);
	}

	public int getmDocID() {
		return mDocID;
	}

	public String getmDocName() {
		return mDocName;
	}

	public Map<String, Integer> getmQueryMentions() {
		return mQueryMentions;
	}
	
	

}
