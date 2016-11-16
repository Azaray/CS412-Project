package datastructures;

import java.util.ArrayList;
import org.apache.lucene.document.Document;

public class QueryResult {

	private ArrayList<Document> mDocList = new ArrayList<Document>();
	
	public QueryResult() {
		
	}
	
	public ArrayList<Document> getDocuments() {
		return this.mDocList;
	}
	
	public void addDocument(Document doc) {
		if(!this.mDocList.contains(doc))
			this.mDocList.add(doc);
	}
}
