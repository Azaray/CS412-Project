package model;

import java.io.IOException;
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

import datastructures.SearchQuery;;

public class Searcher {

	public static ScoreDoc[] Search(SearchQuery searchQuery) throws IOException {
	
		if(!searchQuery.isEmpty()) {
			try {
				Path path = Paths.get("index");
				IndexReader reader = DirectoryReader.open(FSDirectory.open(path));
				IndexSearcher searcher = new IndexSearcher(reader);
				
				/* Could use the EnglishAnalyzer instead of the StandardAnalyzer because it already has a default
				 * list of stopwords, and also includes stemming. */
				// Analyzer analyzer = new EnglishAnalyzer();
				Analyzer analyzer = new StandardAnalyzer();

				QueryParser parser = new QueryParser("contents", analyzer);
				Query query = parser.parse(searchQuery.getQueryString().trim());

				TopDocs results = searcher.search(query, 100);
				return results.scoreDocs; // Still to implement a data structure for returning results.
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}
	
}
