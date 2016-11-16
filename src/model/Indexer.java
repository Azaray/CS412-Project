package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LegacyLongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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

			Analyzer analyzer = new StandardAnalyzer();
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

						addTagToDoc(SearchField.DOCNO.field(), el, doc);
						addTagToDoc(SearchField.DOCPARENT.field(), el, doc);
						addTagToDoc(SearchField.USDEPT.field(), el, doc);
						addTagToDoc(SearchField.USBUREAU.field(), el, doc);
						addTagToDoc(SearchField.DOCCONTENT.field(), el, doc);

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

	private static void addTagToDoc(String tag, Element e, Document doc) {

		Elements elements = e.getElementsByTag(tag);

		if (!elements.isEmpty()) {
				Field docnoField = new TextField(tag, elements.first().text(), Field.Store.YES);
				doc.add(docnoField);
		}
	}
}

// FieldType type = new FieldType();
// type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
// type.setStored(true);
// type.setStoreTermVectors(true);
// type.setTokenized(true);
// type.setStoreTermVectorOffsets(true);
// type.setStoreTermVectorPositions(true);
