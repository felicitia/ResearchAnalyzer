package duc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.Constant;
import util.JSONUtil;
import util.StringUtil;

public class IssueCommitMerger {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(Constant.PROJECT == Constant.HADOOP){
			mergeIssue(Constant.HADOOP_ACDC_FILTER, Constant.HADOOP_ACDC_MERGE);
			mergeIssue(Constant.HADOOP_ARC_FILTER, Constant.HADOOP_ARC_MERGE);
			mergeIssue(Constant.HADOOP_PKG_FILTER, Constant.HADOOP_PKG_MERGE);
		}else if(Constant.PROJECT == Constant.IVY){
			mergeIssue(Constant.IVY_ACDC_FILTER, Constant.IVY_ACDC_MERGE);
			mergeIssue(Constant.IVY_ARC_FILTER, Constant.IVY_ARC_MERGE);
			mergeIssue(Constant.IVY_PKG_FILTER, Constant.IVY_PKG_MERGE);
		}else if(Constant.PROJECT == Constant.LUCENE){
			mergeIssue(Constant.LUCENE_ACDC_FILTER, Constant.LUCENE_ACDC_MERGE);
			mergeIssue(Constant.LUCENE_ARC_FILTER, Constant.LUCENE_ARC_MERGE);
			mergeIssue(Constant.LUCENE_PKG_FILTER, Constant.LUCENE_PKG_MERGE);
		}else if(Constant.PROJECT == Constant.STRUTS2){
			mergeIssue(Constant.STRUTS2_ACDC_FILTER, Constant.STRUTS2_ACDC_MERGE);
			mergeIssue(Constant.STRUTS2_ARC_FILTER, Constant.STRUTS2_ARC_MERGE);
			mergeIssue(Constant.STRUTS2_PKG_FILTER, Constant.STRUTS2_PKG_MERGE);
		}else{
			System.out.println("Project invalid...");
		}
		System.out.println("done! (๑•̀ω•́๑)");
	}

	/**
	 * merge each commits in the filtered issues, count the total smell number
	 * for each issues, remove the duplicate files in different commits, remove
	 * non-java files, skip files that are not valid, e.g. in test package
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	public static void mergeIssue(final String inputFile,
			final String outputFile) {
		JSONArray issues = (JSONArray) JSONUtil.readJsonFromFile(inputFile);
		for (int issueIdx = 0; issueIdx < issues.size(); issueIdx++) {
			int issueSmells = 0;
			JSONObject issue = (JSONObject) issues.get(issueIdx);
			JSONArray commits = (JSONArray) issue.get("commits");
			JSONObject newFiles = new JSONObject();
			for (int commitIdx = 0; commitIdx < commits.size(); commitIdx++) {
				JSONObject commit = (JSONObject) commits.get(commitIdx);
				JSONArray files = (JSONArray) commit.get("files");
				for (int fileIdx = 0; fileIdx < files.size(); fileIdx++) {
						JSONObject file = (JSONObject) files.get(fileIdx);
						String filename = (String) file.get("filename");
						if(Constant.PROJECT == Constant.HADOOP){
							if((!newFiles.containsKey(filename)) && StringUtil.isValidFilename(filename, Constant.HADOOP_PKG_PREFIX)){
								issueSmells += Math.toIntExact((long) file.get("total_smell"));
								file.remove("filename");
								newFiles.put(filename, file);
							}	
						}else if(Constant.PROJECT == Constant.IVY){
							if((!newFiles.containsKey(filename)) && StringUtil.isValidFilename(filename, Constant.IVY_PKG_PREFIX)){
								issueSmells += Math.toIntExact((long) file.get("total_smell"));
								file.remove("filename");
								newFiles.put(filename, file);
							}
						}else if(Constant.PROJECT == Constant.LUCENE){
							if((!newFiles.containsKey(filename)) && StringUtil.isValidFilename(filename, Constant.LUCENE_PKG_PREFIX)){
								issueSmells += Math.toIntExact((long) file.get("total_smell"));
								file.remove("filename");
								newFiles.put(filename, file);
							}
						}else if(Constant.PROJECT == Constant.STRUTS2){
							if((!newFiles.containsKey(filename)) && StringUtil.isValidFilename(filename, Constant.STRUTS2_PKG_PREFIX)){
								issueSmells += Math.toIntExact((long) file.get("total_smell"));
								file.remove("filename");
								newFiles.put(filename, file);
							}
						}
						
				}
			}
			issue.put("issue_smells", issueSmells);
			issue.put("files", newFiles);
			issue.remove("commits");
		}
		JSONUtil.writeJSONArray2File(issues, outputFile);
	}
}
