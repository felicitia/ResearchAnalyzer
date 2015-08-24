package leo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ConcernAnalyzer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("######Menu######");
		System.out.println("1. convert mallet output (.txt) to concerns.json");
		System.out.println("2. convert agglomeration (.csv) to concerns.json");
		System.out.println("###############");
		System.out.println("your choice? ( ◞´•௰•`)◞");
		Scanner consoleScanner = new Scanner(System.in);
		int choice;
		String inputFile = null;
		String outputFile = null;
		choice = consoleScanner.nextInt();
		System.out.println("input file (full path with extension):");
		inputFile = consoleScanner.next();
		System.out.println("output file (full path with extension):");
		outputFile = consoleScanner.next();
		switch (choice) {
		case 1:
			mallet2Json(inputFile, outputFile);
			break;
		case 2:
			csv2json(inputFile, outputFile);
			break;
		default:
			System.out.println("command not found!");
		}
	}

	public static JSONObject getJsonFromCsv(final String csvFile){
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		JSONObject concerns = new JSONObject();
		try {
			br = new BufferedReader(new FileReader(csvFile));
			//skip headers (first two lines)
			br.readLine();
			br.readLine();
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] lineArray = line.split(cvsSplitBy);
				String className = lineArray[2];
				String concernNumber = lineArray[1];
				addConcern(concerns, className, concernNumber);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return concerns;
	}
	
	public static void csv2json(final String csvFile, final String jsonFile) {
		JSONObject concerns = getJsonFromCsv(csvFile);
		writeJson2File(concerns, jsonFile);
	}

	public static void writeJson2File(JSONObject obj, final String jsonFile){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(jsonFile, "UTF-8");
			writer.println(obj.toJSONString());
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}
	
	public static void mallet2Json(final String malletFile,
			final String jsonFile) {
		JSONObject concerns = new JSONObject();
		try {
			Scanner fileScanner = new Scanner(new FileReader(malletFile));
			fileScanner.nextLine();
			while (fileScanner.hasNextLine()) {
				fileScanner.next();
				String className = fileScanner.next();
				String concernNumber = fileScanner.next();
				addConcern(concerns, className, concernNumber);
				fileScanner.nextLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeJson2File(concerns, jsonFile);
	}
	
	public static void addConcern(JSONObject concerns, String className, String concernNumber){
		if (concerns.containsKey(concernNumber)) {
			// ((JSONArray)concerns.get(concernNumber)).add(className);
			JSONObject concern = (JSONObject) (concerns
					.get(concernNumber));
			concern.put("number", (Integer) concern.get("number") + 1);
			((JSONArray) concern.get("classes")).add(className);
		} else {
			JSONObject concern = new JSONObject();
			concern.put("number", 1);
			JSONArray classList = new JSONArray();
			classList.add(className);
			concern.put("classes", classList);
			concerns.put(concernNumber, concern);
		}
	}
}
