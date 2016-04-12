package duc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.JSONUtil;

public class IssueMerger {

	final static String inputFile = "/Users/felicitia/Google_Drive/Arcade/ICSE_2016_data/ASE_2016/Hadoop/all_smells/hadoop_arc_all_filter_versions.json";
	final static String outputFile = "/Users/felicitia/Google_Drive/Arcade/ICSE_2016_data/ASE_2016/Hadoop/all_smells/hadoop_arc_all_merged_commits.json";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mergeIssue(inputFile, outputFile);
		System.out.println("done! (๑•̀ω•́๑)");
	}

	/**
	 * merge each commits in the filtered issues, count the total smell number
	 * for each issues, remove the duplicate files in different commits, remove
	 * non-java files
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
						if((!newFiles.containsKey(filename)) && filename.endsWith(".java")){
							issueSmells += Math.toIntExact((long) file.get("total_smell"));
							file.remove("filename");
							newFiles.put(filename, file);
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
