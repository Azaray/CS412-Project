package model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import datastructures.QueryResult;
import datastructures.QueryResultList;
import datastructures.SearchField;
import datastructures.SearchQuery;

public class Searcher {

	public static QueryResultList Search(List<SearchQuery> searchQueryList) {

		QueryResultList queryResults = new QueryResultList();

		if (!searchQueryList.isEmpty()) {
			try {
				Path path = Paths.get("index");
				IndexReader reader = DirectoryReader.open(FSDirectory.open(path));
				IndexSearcher searcher = new IndexSearcher(reader);

				Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());

				BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

				for (SearchQuery searchQuery : searchQueryList) {
					QueryParser parser = new QueryParser(searchQuery.getSearchField().field(), analyzer);
					Query query = parser.parse(searchQuery.getQueryString().trim());
					booleanQuery.add(query, BooleanClause.Occur.MUST);
				}

				TopDocs results = searcher.search(booleanQuery.build(), 10000);
				ScoreDoc[] hits = results.scoreDocs;

				for (ScoreDoc sd : hits) {

					QueryResult result = new QueryResult(sd.doc, searcher.doc(sd.doc).get(SearchField.DOCNO.field()));
					Terms vector = reader.getTermVector(sd.doc, SearchField.DOCCONTENT.field() + "vector");
					TermsEnum termsEnum = vector.iterator();

					BytesRef text = null;

					while ((text = termsEnum.next()) != null) {
						String term = text.utf8ToString().toLowerCase();

						for (SearchQuery searchQuery : searchQueryList) {
							if (searchQuery.getSearchField().equals(SearchField.DOCCONTENT)) {
								for (String querystr : searchQuery.getHighlightQueryList()) {

									if (searchQuery.isExactWord()) {
										if (term.equals(querystr.toLowerCase())) {
											int freq = (int) termsEnum.totalTermFreq();
											result.addResult(term, freq);
										}
									} else {
										if (term.contains(querystr.toLowerCase())) {
											int freq = (int) termsEnum.totalTermFreq();
											result.addResult(term, freq);
										}
									}
								}
							}

						}
					}

					queryResults.addResults(result);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return queryResults;
	}

}
