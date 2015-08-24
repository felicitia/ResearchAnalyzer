package leo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.javafx.tk.FocusCause;

public class DependencyAnalyzer {

	private static final String SEPARATOR = ";";
	private static final String rootPath = "/Users/felicitia/Google_Drive/Design_Code/yixue_results/MobileMedia/";
	private static final String depFile = rootPath+"class_dep.xml";
	private static final String concernFile = rootPath+"new_concerns.json";
	private static final String outputFile = rootPath+"syntactic_dep.csv";
	
	public static void main(String[] args) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.println("sep="+SEPARATOR);
		writer.println("concern#"+SEPARATOR+"#classes"+SEPARATOR+"#dependencies"+SEPARATOR+"#pairs"+SEPARATOR+"related"+SEPARATOR+"not_related");
		// get class dependency from xml file
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;
		NodeList classListFromDep = null;
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(depFile));
			classListFromDep = doc.getElementsByTagName("class");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject concerns = (JSONObject) (jsonParser.parse(new FileReader(
					concernFile)));
			// iterate each concern in the concerns.json
			for (Iterator iterator = concerns.keySet().iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				JSONObject concern = (JSONObject) concerns.get(key);
				Long numberLong = (Long) concern.get("number");
				JSONArray classesFromConcern = (JSONArray) concern
						.get("classes");
				int numberFromConcern = Integer.valueOf(numberLong.intValue());
				int[][] matrix = new int[numberFromConcern][numberFromConcern];
				for (int i = 0; i < numberFromConcern; i++) {
					for (int j = 0; j < numberFromConcern; j++) {
						matrix[i][j] = 0;
					}
				}
				// check dependencies for each class within one concern group
				for (int classIdxFromConcern = 0; classIdxFromConcern < classesFromConcern
						.size(); classIdxFromConcern++) {
					// iterate each class in the dependency xml file
					for (int classIdxFromDep = 0; classIdxFromDep < classListFromDep
							.getLength(); classIdxFromDep++) {
						Element classElement = (Element) classListFromDep
								.item(classIdxFromDep);
						String focusClass = classElement
								.getElementsByTagName("name").item(0)
								.getTextContent().trim();
						if (focusClass.equals(classesFromConcern
								.get(classIdxFromConcern).toString().trim())) {
							// System.out.println("focusClass=" + focusClass);
							NodeList children = classElement.getChildNodes();
							// iterate each dependency of the focusClass
							for (int childIdx = 1; childIdx < children
									.getLength(); childIdx++) {
								String depClass = children.item(childIdx)
										.getTextContent().trim();
								if (classesFromConcern.contains(depClass)) {
									// find the index of the dependency class
									// and set result matrix
									for (int depClassIdx = 0; depClassIdx < classesFromConcern
											.size(); depClassIdx++) {
										if (classesFromConcern.get(depClassIdx)
												.toString().trim()
												.equals(depClass)) {
											int low = classIdxFromConcern;
											int high = depClassIdx;
											if (classIdxFromConcern > depClassIdx) {
												low = depClassIdx;
												high = classIdxFromConcern;
											}
											matrix[low][high] = 1;
										}
									}
								}
							}
						}
					}
				}
				int numOfDep = countDepFromMatrix(matrix, numberFromConcern);
				int numOfPairs = (numberFromConcern * (numberFromConcern - 1)) / 2; // might be zero if there's only one concern
				boolean[] relatedFlags = checkRelated(matrix, numberFromConcern);
				JSONArray relatedClasses = new JSONArray();
				JSONArray notRelatedClasses = new JSONArray();
				for(int flagIdx=0; flagIdx<numberFromConcern; flagIdx++){
					if(relatedFlags[flagIdx]){
						relatedClasses.add(classesFromConcern.get(flagIdx));
					}else{
						notRelatedClasses.add(classesFromConcern.get(flagIdx));
					}
				}
				writer.println(key+SEPARATOR+numberFromConcern+SEPARATOR+numOfDep+SEPARATOR+numOfPairs+SEPARATOR+relatedClasses.toJSONString()+SEPARATOR+notRelatedClasses.toJSONString());
			}
			writer.close();
			System.out.println("done! ٩(｡•ω•｡)﻿و");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean[] checkRelated(int[][] matrix, int number){
		boolean[] flags = new boolean[number];
		for(int i=0; i<number; i++){
			flags[i] = false;
		}
		if (number <= 0) {
			System.out.println("matrix error in checkRelated");
			return null;
		} else {
			for (int i = 0; i < number - 1; i++) {
				for (int j = 1 + i; j < number; j++) {
					if(1==matrix[i][j]){
						flags[i] = true;
						flags[j] = true;
					}
				}
			}
			return flags;
		}
	}
	public static void printMatrix(int[][] matrix, int number) {
		for (int i = 0; i < number; i++) {
			for (int j = 0; j < number; j++) {
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	public static int countDepFromMatrix(int[][] matrix, int number) {
		if (number <= 0) {
			System.out.println("matrix error in countDepFromMatrix");
			return 0;
		} else if (1 == number) {
			return 0;
		} else {
			int count = 0;
			for (int i = 0; i < number - 1; i++) {
				for (int j = 1 + i; j < number; j++) {
					count += matrix[i][j];
				}
			}
			return count;
		}
	}
}
