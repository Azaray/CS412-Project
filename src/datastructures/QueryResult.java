package datastructures;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;

public class QueryResult {

	private ArrayList<Document> mDocList = new ArrayList<Document>();
	
	public QueryResult() {
		
	}
	
	public ArrayList<Document> getDocuments() {
		return this.mDocList;
	}
	
	public void addDocument(Document doc) {
		this.mDocList.add(doc);
	}
}
