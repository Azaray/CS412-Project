package datastructures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

public class SearchQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9124254337770826714L;
	private SearchField mSearchField;
	private String mQueryString;
	private List<String> mFullQueryList = new ArrayList<String>();
	private List<String> mHighlighQueryList = new ArrayList<String>();
	private static List<String> mHistoryList = new ArrayList<String>();
	private boolean mExactWord;
	private boolean mSentenceSearch;

	public SearchQuery(String query, SearchField field, boolean exact, boolean sentenceSearch) {
		this.mSearchField = field;
		this.mExactWord = exact;
		this.mSentenceSearch = sentenceSearch;
		
		if(mSentenceSearch) {
			this.mQueryString = "\"" + query + "\"";
			mHighlighQueryList.add(query);
		}
		else
			FormatQuery(query);
	}

	public String getQueryString() {
		return this.mQueryString;
	}
	
	public SearchField getSearchField() {
		return this.mSearchField;
	}
	
	public boolean isSentenceSearch() {
		return this.mSentenceSearch;
	}
	
	public boolean isExactWord() {
		return this.mExactWord;
	}
	
	public List<String> getFullQueryList() {
		return this.mFullQueryList;
	}
	
	public List<String> getHighlightQueryList() {
		return this.mHighlighQueryList;
	}
	
	public boolean isEmpty() {
		return this.mQueryString.isEmpty();
	}
	
	private void FormatQuery(String query) {
		try {
			queryStemming(removeStopwords(query));
		} catch (IOException e) {
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

		String lastWord = "";
		
		while (tokenStream.incrementToken()) {
			String term = charTermAttribute.toString();
			
			mFullQueryList.add(term);
			
			if(!lastWord.equals("NOT") && !(term.equals("NOT") || term.equals("AND") || term.equals("OR"))) {
				mHighlighQueryList.add(term);
				strb.append(term + " ");
			}
			
			lastWord = term;
		}

		tokenStream.close();
		return strb.toString();
	}

	private void queryStemming(String query) throws IOException {

		if(!mExactWord) {
			AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
			
			StandardTokenizer tokenizer = new StandardTokenizer(factory);
			tokenizer.setReader(new StringReader(query));
			tokenizer.reset();

			PorterStemFilter psf = new PorterStemFilter(tokenizer);
			CharTermAttribute attr = tokenizer.addAttribute(CharTermAttribute.class);
			
			while (psf.incrementToken()) {		
				String term = attr.toString();
					
				if(!mHighlighQueryList.contains(term) && !(term.equals("NOT") || term.equals("AND") || term.equals("OR")))
					mHighlighQueryList.add(term);
			}
			
			psf.close();
		}
	}
	
	private void generateQueryString() {
		StringBuilder strb = new StringBuilder();
		List<String> newList = new ArrayList<String>();
		
		for(String word : mFullQueryList) { 
			newList.add(word);
			
			for(String str : mHighlighQueryList) {
				if(!word.equals(str) && word.contains(str)) {
					newList.add(str);
				}
			}
		}
		
		for(String word : mFullQueryList){
			saveTerm(word);
		}
		mFullQueryList = newList;
		
		for(String str : mFullQueryList) {
			if(!this.mExactWord) {
				if(str.equals("AND") || str.equals("OR")|| str.equals("NOT"))
					strb.append(str + " ");
				else
					strb.append(str + "* ");
			} else {
				strb.append(str + " ");
			}
		}
		
		mQueryString = strb.toString();
	}

	public void saveTerm(String w){
		mHistoryList.add(w);
	}
	public static void saveSearch(){
		JPanel savePanel = new JPanel();
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(savePanel);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		if (returnVal == JFileChooser.APPROVE_OPTION){
			try
			{
				PrintWriter pw = new PrintWriter(new FileWriter(new File(fc.getSelectedFile()+ ".txt")));   
				for(int i = 0; i<mHistoryList.size(); i++)
				{
					pw.println(mHistoryList.get(i));
					System.out.println("saved");
				}
				pw.close();
				mHistoryList.clear();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else if (returnVal == JFileChooser.CANCEL_OPTION) {
			System.out.println("Cancelled ");
		}
	}
	
	public String fileString() {
		
		return mSearchField.field() + ":" + mQueryString;
	}
	
}
