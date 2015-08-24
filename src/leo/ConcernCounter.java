package leo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ConcernCounter {

	private static final String rootPath = "/Users/felicitia/Google_Drive/Design_Code/MobileMedia/";
	private static final String codeSmellFile = rootPath + "codeAnomalies.csv";
	private static final String archSmellFile = rootPath
			+ "archtecturalProblemsMM.csv";
	private static final String concernCsvFile = rootPath + "agglomeration.csv";
	private static final String outputFolder = "/Users/felicitia/Google_Drive/Design_Code/yixue_results/MobileMedia/";
	private static final String codeSmellOutput = outputFolder+"code_smell.csv";
	private static final String COMMA_SEPARATOR = ",";
	private static final String SEMI_SEPARATOR = ";";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		countCodeSmell(codeSmellOutput);
	}

	public static void countArchSmell(final String outputFile){
		JSONObject concerns = ConcernAnalyzer.getJsonFromCsv(concernCsvFile);
		String[] header = {"concern#", "#classes", "#related", "#not_related", "related_classes", "not_related_classes"};
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setCsvHeader(writer, ";", header);
		
	}
	
	public static void setCsvHeader(PrintWriter writer, String SEPARATOR, final String[] header){
		if(!SEPARATOR.equals(",")){
			writer.println("sep="+SEPARATOR);
		}
		for(int i=0; i<header.length-1; i++){
			writer.print(header[i]+SEPARATOR);
		}
		writer.println(header[header.length-1]);
	}
	
	public static void countCodeSmell(final String outputFile) {
		HashMap<String, HashSet<String>> classSmellResult = (HashMap<String, HashSet<String>>) getCodeSmells();
		String[] header = {"concern#", "#code_smells"};
		JSONObject concerns = ConcernAnalyzer.getJsonFromCsv(concernCsvFile);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		writer.println("concern#"+COMMA_SEPARATOR+"#code_smells");
		setCsvHeader(writer, ",", header);
		for (Iterator iterator = concerns.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			JSONObject concern = (JSONObject) concerns.get(key);
			JSONArray classes = (JSONArray) concern.get("classes");
			int num=0;
			for (int i = 0; i < classes.size(); i++) {
				String className = classes.get(i).toString().trim();
				if(classSmellResult.containsKey(className)){
					num += classSmellResult.get(className).size();
				}
			}
			writer.println(key+COMMA_SEPARATOR+num);
		}
		writer.close();
		System.out.println("code smell done! ╭( ･ㅂ･)و ");
	}

	/**
	 * HashMap<String, Set<String>> is the result of arch smell set for each class
	 * @return
	 */
	public static Map getArchSmells() {
		Map<String, HashSet<String>> classSmellResult = new HashMap<String, HashSet<String>>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		try {
			br = new BufferedReader(new FileReader(archSmellFile));
			// skip sep=; (first line)
			br.readLine();
			//get archSmellList
			line = br.readLine();
			String[] tmpLine = line.split(cvsSplitBy);
			String[] archSmellList = Arrays.copyOfRange(tmpLine, 2, tmpLine.length);
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] lineArray = line.split(cvsSplitBy);
				String elementName = lineArray[0];
				String className = getClassName(elementName);
				if (classSmellResult.containsKey(className)) {
					HashSet<String> tmpSet = classSmellResult.get(className);
					tmpSet.addAll(getSmellSet(lineArray));
					classSmellResult.put(className, tmpSet);
				} else {
					classSmellResult.put(className, getSmellSet(lineArray));
				}
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
		return classSmellResult;
	}
	
	/**
	 * HashMap<String, HashSet<String>> is the result of code smell set for each class
	 * @return
	 */
	public static Map getCodeSmells() {
		Map<String, HashSet<String>> classSmellResult = new HashMap<String, HashSet<String>>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		try {
			br = new BufferedReader(new FileReader(codeSmellFile));
			// skip headers (first line)
			br.readLine();
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] lineArray = line.split(cvsSplitBy);
				String elementName = lineArray[0];
				String className = getClassName(elementName);
				if (classSmellResult.containsKey(className)) {
					HashSet<String> tmp = classSmellResult.get(className);
					tmp.addAll(getSmellSet(lineArray));
					classSmellResult.put(className, tmp);
				} else {
					classSmellResult.put(className, getSmellSet(lineArray));
				}
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
		return classSmellResult;
	}

	public static HashSet<String> getSmellSet(String[] lineArray) {
		// process the code smells, to avoid redundancy
		HashSet<String> smellSet = new HashSet<String>();
		for (int i = 1; i < lineArray.length; i++) {
			// ignore 'External_Addictor_per_Method', it's not a code smell
			if (lineArray[i].startsWith("External")) {
				continue;
			}
			if ('0' <= lineArray[i].charAt(lineArray[i].length() - 1)
					&& lineArray[i].charAt(lineArray[i].length() - 1) <= '9') {
				smellSet.add(lineArray[i].substring(0,
						lineArray[i].length() - 1));
			} else {
				smellSet.add(lineArray[i]);
			}
		}
		return smellSet;
	}

	public static String getClassName(String elementName) {
		String[] splitNames = elementName.split("\\.");
		//find duplicate name (to deal with the bug of input file)
		Set<String> uniqueNames = new HashSet<String>(Arrays.asList(splitNames));
		if(splitNames.length!=uniqueNames.size()){
			System.out.println("duplicate name (original) = "+elementName);
			if(splitNames[splitNames.length-1].equals(splitNames[splitNames.length-2])){
				//remove last element in spiltNames
				splitNames = Arrays.copyOf(splitNames, splitNames.length-1);
			}
		}
		char firstChar = splitNames[splitNames.length - 1].charAt(0);
		if ('A' <= firstChar && firstChar <= 'Z') {
			StringBuilder className = new StringBuilder();
			for (int i = 0; i < splitNames.length - 1; i++) {
				className.append(splitNames[i]);
				className.append('.');
			}
			className.append(splitNames[splitNames.length - 1]);
			return className.toString().trim();
		} else if ('a' <= firstChar && firstChar <= 'z') {
			StringBuilder className = new StringBuilder();
			for (int i = 0; i < splitNames.length - 2; i++) {
				className.append(splitNames[i]);
				className.append('.');
			}
			className.append(splitNames[splitNames.length - 2]);
			return className.toString().trim();
		} else {
			System.out.println("first char is not alphabet");
			return null;
		}
	}
}
