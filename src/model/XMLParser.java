package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class XMLParser {

	public static void parse() {
		
		try {
		File xmlFile = new File("S:/4th Year/CS412/CS412-Project/docs/data set 10/FR94/01/FR940104.0");
		InputStream is = new FileInputStream(xmlFile);
		
		Document doc = Jsoup.parse(xmlFile, "UTF-8", "http://example.com/");
		
		Elements e2 = doc.getElementsByTag("doc");
		
		for(Element e : e2) {
			Element docno = e.getElementsByTag("docno").first();
			Element parent = e.getElementsByTag("parent").first();
			Element usdept = e.getElementsByTag("usdept").first();
			Element usbureau = e.getElementsByTag("usbureau").first();
			Element text = e.getElementsByTag("text").first();
		}
		
		} catch (Exception e) {	
			e.printStackTrace();
		}
	
	}
}
