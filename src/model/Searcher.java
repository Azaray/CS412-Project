package model;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import datastructures.QueryResult;
import datastructures.SearchField;
import datastructures.SearchQuery;

public class Searcher {

	public static QueryResult Search(SearchQuery searchQuery) {
	
		QueryResult result = new QueryResult();
		
		if(!searchQuery.isEmpty()) {
			try {
				Path path = Paths.get("index");
				IndexReader reader = DirectoryReader.open(FSDirectory.open(path));
				IndexSearcher searcher = new IndexSearcher(reader);
				
				Analyzer analyzer = new StandardAnalyzer();
				//Analyzer analyzer = new EnglishAnalyzer();
				QueryParser parser = new QueryParser(SearchField.DOCCONTENT.field(), analyzer);
				Query query = parser.parse(searchQuery.getQueryString().trim());
				
				TopDocs results = searcher.search(query, 10000);
				ScoreDoc[] hits = results.scoreDocs;
		
				for(ScoreDoc sd : hits) {
					result.addDocument(searcher.doc(sd.doc));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
}
