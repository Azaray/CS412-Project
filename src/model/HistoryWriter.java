package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import datastructures.SearchQuery;

public class HistoryWriter {

	public static void write(List<SearchQuery> queries) {

		File dir = new File("history");
		// attempt to create the directory here
		dir.mkdir();

		int length = dir.listFiles().length + 1;

		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream("history/history" + length + ".txt"))) {

			oos.writeObject(queries);
			System.out.println("Done");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<List<SearchQuery>> returnHistory() {

		List<List<SearchQuery>> history = new  ArrayList<List<SearchQuery>>();
		File dir = new File("history");
		// attempt to create the directory here
		dir.mkdir();

		int length = dir.listFiles().length;
		
		for(int i = 1; i <= length; i++) {
			history.add(read(i));
		}
		
		return history;
	}

	private static List<SearchQuery> read(int queryno) {
		FileInputStream fis;
		try {
			fis = new FileInputStream("history/history" + queryno + ".txt");
			ObjectInputStream ois = new ObjectInputStream(fis);

			List<SearchQuery> queries = (List<SearchQuery>) ois.readObject();
			return queries;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Can't find file");
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
