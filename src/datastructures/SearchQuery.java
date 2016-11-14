package datastructures;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

public class SearchQuery {

	private String mQueryString = null;
	private List<String> mQueryList = new ArrayList<String>();

	public SearchQuery(String query) {
		FormatQuery(query);
	}

	public String getQueryString() {
		return this.mQueryString;
	}
	
	public boolean isEmpty() {
		return mQueryString.isEmpty();
	}
	
	private void FormatQuery(String query) {
		try {
			queryStemming(removeStopwords(query));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generateQueryString();
	}
	
	private String removeStopwords(String query) throws IOException {
		
		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
		
		CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();

		StandardTokenizer tokenizer = new StandardTokenizer(factory);
		tokenizer.setReader(new StringReader(query));

		TokenStream tokenStream = new StopFilter(tokenizer, stopWords);

		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		
		StringBuilder strb = new StringBuilder();

		while (tokenStream.incrementToken()) {
			String term = charTermAttribute.toString();
			strb.append(term + " ");
			
			if(!mQueryList.contains(term))
				mQueryList.add(term);
		}

		tokenStream.close();
		return strb.toString();
	}

	private void queryStemming(String query) throws IOException {

		AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
		
		StandardTokenizer tokenizer = new StandardTokenizer(factory);
		tokenizer.setReader(new StringReader(query));
		tokenizer.reset();

		PorterStemFilter psf = new PorterStemFilter(tokenizer);
		CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
		
		while (psf.incrementToken()) {		
			String term = attr.toString();
				
			if(!mQueryList.contains(term))
				mQueryList.add(term);
		}
		
		psf.close();
	}
	
	private void generateQueryString() {
		StringBuilder strb = new StringBuilder();
		
		for(String str : mQueryList)
			strb.append(str + " ");
		
		mQueryString = strb.toString();
	}

}
