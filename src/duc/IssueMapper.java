package duc;

import java.io.FileReader;
import java.util.HashSet;
//import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import util.JSONUtil;
import util.StringUtil;

public class IssueMapper {

	static JSONArray issues = null;
	static JSONArray smells = null;
	static String[] pkgPrefixs = { "src/java/main/org", "src/java/org",
			"src/core/org" };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Scanner consoleScanner = new Scanner(System.in);
		// String inputIssueFile = "/Users/felicitia/Desktop/sample_issue.json";
		// String inputSmellFile =
		// "/Users/felicitia/Google_Drive/Arcade/ICSE_2016_data/yixue_arch_result/class_smell_json/hadoop_svn_pkg.json";
		// String outputFile = "/Users/felicitia/Desktop/output.json";
		String inputIssueFile = args[0];
		String inputSmellFile = args[1];
		String outputFile = args[2];
		// System.out.println("input issue file (full path with extension):");
		// inputIssueFile = consoleScanner.next();
		// System.out.println("input smell file (full path with extension):");
		// inputSmellFile = consoleScanner.next();
		// System.out.println("output file (full path with extension):");
		// outputFile = consoleScanner.next();
		issues = (JSONArray) readJsonFromFile(inputIssueFile);
		// printStringSet(getDirectoryPrefix());
		smells = (JSONArray) readJsonFromFile(inputSmellFile);
		addSmess2All();
		JSONUtil.writeJSONArray2File(issues, outputFile);
		System.out.println("done! (๑•ᴗ•๑)♡‼");
	}

	public static void addSmess2All() {
		for (int i = 0; i < issues.size(); i++) {
			JSONObject issue = (JSONObject) issues.get(i);
			addSmell2Issue(issue);
		}
	}

	public static void addSmell2Issue(JSONObject issue) {
		JSONArray commits = (JSONArray) issue.get("commits");
		// iterate commits
		for (int commitIdx = 0; commitIdx < commits.size(); commitIdx++) {
			JSONObject commit = (JSONObject) commits.get(commitIdx);
			JSONArray files = (JSONArray) commit.get("files");
			// iterate files in each commit
			for (int fileIdx = 0; fileIdx < files.size(); fileIdx++) {
				JSONObject file = (JSONObject) files.get(fileIdx);
				String filename = (String) file.get("filename");
				// check if the file is valid, skip those files from 'example',
				// 'test' package
				// && only find .java file
				if (StringUtil.containStr(
						StringUtil.extractPkgPrefix(filename), pkgPrefixs)
						&& filename.endsWith(".java")) {
//					System.out.println("filename2: " + filename);
					addSmell2File(issue, file, filename);
				}
			}
		}
	}

	/**
	 * find the smells related to one file, and update file object
	 * 
	 * @param issue
	 * @param filename
	 */
	public static void addSmell2File(JSONObject issue, JSONObject file,
			String dir) {
		String version = (String) issue.get("affect:");
		if (version == null) {
			return;
		}
		String extractedName = StringUtil.dir2pkg(dir);
		for (int i = 0; i < smells.size(); i++) {
			JSONObject smell = (JSONObject) smells.get(i);

			if (version.equals(smell.get("version"))) {
				JSONArray smellsPerVersion = (JSONArray) smell.get("smells");
				for (int j = 0; j < smellsPerVersion.size(); j++) {
					JSONObject smellPerVersion = (JSONObject) smellsPerVersion
							.get(j);
					String classname = (String) smellPerVersion
							.get("classname");
					// System.out.println("class name: "+ classname);
					// found the smells related to particular file
					if (classname.contains(extractedName)) {
						// add the smells to this file!!
						JSONObject tmpSmell = null;
						if (file.containsKey("smells")) {
							tmpSmell = (JSONObject) file.get("smells");
							tmpSmell.put("bco", smellPerVersion.get("bco"));
							tmpSmell.put("bdc", smellPerVersion.get("bdc"));
							tmpSmell.put("buo", smellPerVersion.get("buo"));
							tmpSmell.put("spf", smellPerVersion.get("spf"));
						} else {
							tmpSmell = new JSONObject();
							tmpSmell.put("bco", smellPerVersion.get("bco"));
							tmpSmell.put("bdc", smellPerVersion.get("bdc"));
							tmpSmell.put("buo", smellPerVersion.get("buo"));
							tmpSmell.put("spf", smellPerVersion.get("spf"));
							file.put("smells", tmpSmell);
						}

					}
				}
				return;
			}
		}
	}

	/**
	 * 
	 * @param filename
	 *            full file name
	 * @return
	 */
	public static Object readJsonFromFile(String filename) {
		Object obj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			obj = parser.parse(new FileReader(filename));
			;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public static void printStringSet(Set<String> strs) {
		for (String str : strs) {
			System.out.println(str);
		}
	}

	public static Set<String> getDirectoryPrefix() {
		Set<String> prefixs = new HashSet<String>();
		for (int i = 0; i < issues.size(); i++) {
			JSONObject issue = (JSONObject) issues.get(i);
			JSONArray commits = (JSONArray) issue.get("commits");
			// iterate commits
			for (int commitIdx = 0; commitIdx < commits.size(); commitIdx++) {
				JSONObject commit = (JSONObject) commits.get(commitIdx);
				JSONArray files = (JSONArray) commit.get("files");
				// iterate files in each commit
				for (int fileIdx = 0; fileIdx < files.size(); fileIdx++) {
					JSONObject file = (JSONObject) files.get(fileIdx);
					String filename = (String) file.get("filename");
					prefixs.add(StringUtil.extractPkgPrefix(filename));
				}
			}
		}
		return prefixs;
	}
}
