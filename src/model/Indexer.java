package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LegacyLongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datastructures.SearchField;

@SuppressWarnings("deprecation")
public class Indexer {

	static ArrayList<String> departments = new ArrayList<String>();
	static ArrayList<String> bureaus = new ArrayList<String>();
	static ArrayList<String> agencies = new ArrayList<String>();
	
	public static void indexFiles(String indexPath, String docsPath, boolean create) {
		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.out.println("Document directory '" + docDir.getAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Path path = Paths.get(indexPath);
			Directory dir = FSDirectory.open(path);

			Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			IndexWriter writer = new IndexWriter(dir, iwc);
			index(writer, docDir);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total liseconds");
			
			File file1 = new File("departments.txt");
			File file2 = new File("bureaus.txt");
			File file3 = new File("agencies.txt");
			
			writeFile(file1, departments);
			writeFile(file2, bureaus);
			writeFile(file3, agencies);

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
		
	}

	private static void index(IndexWriter writer, File file) throws IOException {
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						index(writer, new File(file, files[i]));
					}
				}
			} else {

				InputStream is = null;
				try {
					is = new FileInputStream(file);
					org.jsoup.nodes.Document fedreg = Jsoup.parse(file, "UTF-8", "");

					Elements elementList = fedreg.getElementsByTag("doc");
					
					for (Element el : elementList) {
						Document doc = new Document();

						doc.add(new LegacyLongField(SearchField.LASTMODIFIED.field(), file.lastModified(), Field.Store.NO));
						Field pathField = new StringField(SearchField.PATH.field(), file.getPath(), Field.Store.YES);
						doc.add(pathField);
											
						for(SearchField sf : SearchField.values())
							addTagToDoc(sf.field(), el, doc);

						if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
							writer.addDocument(doc);
						} else {
							writer.updateDocument(new Term("path", file.getPath()), doc);
						}
					}

				} finally {
					is.close();
				}
			}
		}
	}

	private static void addTagToDoc(String tag, Element e, Document doc) throws IOException {

		Elements elements = e.getElementsByTag(tag);

		if (!elements.isEmpty()) {
			if(tag.equals(SearchField.DOCCONTENT.field())) {

				String str = new String(elements.first().text().getBytes(), Charset.forName("UTF-8"));
				
				FieldType ft = new FieldType();
				ft.setStored(true);
				ft.setTokenized(true);
				ft.setStoreTermVectors(true);
				ft.setStoreTermVectorOffsets(true);
				ft.setStoreTermVectorPositions(true);
				ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		
				Field field = new Field(tag+"vector", str, ft);
				doc.add(field);

				Field docnoField = new TextField(tag, str, Field.Store.YES);
				doc.add(docnoField);
		
			} else {	
				
				String str = new String(elements.first().text().getBytes(), Charset.forName("UTF-8"));
			
				if(tag.equals(SearchField.USDEPT.field()))
					if(!departments.contains(str))
						departments.add(str);
				
				if(tag.equals(SearchField.USBUREAU.field()))
					if(!bureaus.contains(str))
						bureaus.add(str);
				
				if(tag.equals(SearchField.AGENCY.field()))
					if(!agencies.contains(str))
						agencies.add(str);
				
				Field docnoField = new TextField(tag, str, Field.Store.YES);
				doc.add(docnoField);
			}
		}
	}
	
	private static void writeFile(File file, ArrayList<String> list) {
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i =0; i<list.size(); i++){
			bw.write(list.get(i));
			bw.newLine();
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}