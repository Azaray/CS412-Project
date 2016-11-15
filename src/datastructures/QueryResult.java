package datastructures;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;

public class QueryResult {

	private List<Document> mDocList = new ArrayList<Document>();
	
	public QueryResult() {
		
	}
	
	public List<Document> getDocuments() {
		return this.mDocList;
	}
	
	public void addDocument(Document doc) {
		this.mDocList.add(doc);
	}
}
