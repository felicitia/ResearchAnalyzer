package duc;

import java.util.HashSet;
//import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.Constant;
import util.JSONUtil;
import util.StringUtil;

public class IssueSmellMapper {

	static JSONArray issues = null;
	static JSONArray smells = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (Constant.PROJECT == Constant.HADOOP) {
			issues = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.HADOOP_ISSUE_FILE);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.HADOOP_SMELL_PKG);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.HADOOP_DEP_CON_PKG);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.HADOOP_SMELL_ARC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.HADOOP_DEP_CON_ARC);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.HADOOP_SMELL_ACDC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.HADOOP_DEP_CON_ACDC);
		} else if (Constant.PROJECT == Constant.IVY) {
			issues = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.IVY_ISSUE_FILE);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.IVY_SMELL_PKG);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.IVY_DEP_CON_PKG);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.IVY_SMELL_ARC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.IVY_DEP_CON_ARC);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.IVY_SMELL_ACDC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.IVY_DEP_CON_ACDC);
		} else if (Constant.PROJECT == Constant.LUCENE) {
			issues = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.LUCENE_ISSUE_FILE);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.LUCENE_SMELL_PKG);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.LUCENE_DEP_CON_PKG);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.LUCENE_SMELL_ARC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.LUCENE_DEP_CON_ARC);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.LUCENE_SMELL_ACDC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.LUCENE_DEP_CON_ACDC);
		} else if (Constant.PROJECT == Constant.STRUTS2) {
			issues = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.STRUTS2_ISSUE_FILE);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.STRUTS2_SMELL_PKG);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.STRUTS2_DEP_CON_PKG);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.STRUTS2_SMELL_ARC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.STRUTS2_DEP_CON_ARC);
			smells = (JSONArray) JSONUtil
					.readJsonFromFile(Constant.STRUTS2_SMELL_ACDC);
			addSmess2All();
			JSONUtil.writeJSONArray2File(issues, Constant.STRUTS2_DEP_CON_ACDC);
		} else {
			System.out.println("Project invalid..");
		}

		// StringUtil.printStringSet(getDirectoryPrefix());
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
				switch (Constant.PROJECT) {
				case Constant.HADOOP:
					if (StringUtil.isValidFilename(filename,
							Constant.HADOOP_PKG_PREFIX)) {
						addSmell2File(issue, file, filename);
					}
					break;
				case Constant.STRUTS2:
					if (StringUtil.isValidFilename(filename,
							Constant.STRUTS2_PKG_PREFIX)) {
						addSmell2File(issue, file, filename);
					}
					break;
				case Constant.IVY:
					if (StringUtil.isValidFilename(filename,
							Constant.IVY_PKG_PREFIX)) {
						addSmell2File(issue, file, filename);
					}
					break;
				case Constant.LUCENE:
					if (StringUtil.isValidFilename(filename,
							Constant.LUCENE_PKG_PREFIX)) {
						addSmell2File(issue, file, filename);
					}
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
		String version = (String) issue.get("affect");
		if (version == null || version.equals("")) {
			return;
		}
		version = StringUtil.formatIssueVersion(version);
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
					// found the smells related to particular file
					if (classname.contains(extractedName)) {
						// add the smells to this file!!
						JSONObject tmpSmell = null;
						if (file.containsKey("smells")) {
							tmpSmell = (JSONObject) file.get("smells");
							int tmpSmellNum = Integer.parseInt(smellPerVersion
									.get("bco").toString());
							if (tmpSmellNum != 0) {
								tmpSmell.put("Concern_Overload", tmpSmellNum);
							}
							// exclude dependency cycle for now
							// tmpSmell.put("bdc", smellPerVersion.get("bdc"));
							tmpSmellNum = Integer.parseInt(smellPerVersion.get(
									"buo").toString());
							if (tmpSmellNum != 0) {
								tmpSmell.put("Link_Overload", tmpSmellNum);
							}
							tmpSmellNum = Integer.parseInt(smellPerVersion.get(
									"spf").toString());
							if (tmpSmellNum != 0) {
								tmpSmell.put(
										"Scattered_Parasitic_Functionality",
										tmpSmellNum);
							}
						} else {
							tmpSmell = new JSONObject();
							int tmpSmellNum = Integer.parseInt(smellPerVersion
									.get("bco").toString());
							if (tmpSmellNum != 0) {
								tmpSmell.put("Concern_Overload", tmpSmellNum);
							}
							// exclude dependency cycle for now
							// tmpSmell.put("bdc", smellPerVersion.get("bdc"));
							tmpSmellNum = Integer.parseInt(smellPerVersion.get(
									"buo").toString());
							if (tmpSmellNum != 0) {
								tmpSmell.put("Link_Overload", tmpSmellNum);
							}
							tmpSmellNum = Integer.parseInt(smellPerVersion.get(
									"spf").toString());
							if (tmpSmellNum != 0) {
								tmpSmell.put(
										"Scattered_Parasitic_Functionality",
										tmpSmellNum);
							}
							file.put("smells", tmpSmell);
						}
					}
				}
				return;
			}
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
