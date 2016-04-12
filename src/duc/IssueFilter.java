package duc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.JSONUtil;

public class IssueFilter {

	final static String classSmells = "/Users/felicitia/Google_Drive/Arcade/ICSE_2016_data/yixue_arch_result/class_smell_json/hadoop_svn_pkg.json";
	final static String inputFile = "/Users/felicitia/Google_Drive/Arcade/ICSE_2016_data/ASE_2016/Hadoop/all_smells/hadoop_pkg_all.json";
	final static String ouputFile = "/Users/felicitia/Google_Drive/Arcade/ICSE_2016_data/ASE_2016/Hadoop/all_smells/hadoop_pkg_all_filter_versions.json";
	static Set<String> versions;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		versions = getVersions(classSmells);
		filterIssues(inputFile, ouputFile);
		System.out.println("done! (๑•ᴗ•๑)♡‼");
	}

	/**
	 * filter the issues
	 * only output the issues whose affected version have smells
	 * remove dependency cycles
	 * add total smell numbers for each file
	 * @param inputIssues
	 * @param outputIssues
	 */
	public static void filterIssues(final String inputIssues, final String outputIssues){
		JSONArray issues = (JSONArray) JSONUtil.readJsonFromFile(inputIssues);
		for(int i=0; i<issues.size(); i++){
			JSONObject issue = (JSONObject) issues.get(i);
			String affectVersion = (String) issue.get("affect");
			String issueID = (String) issue.get("issue_id");
			if(!versions.contains(affectVersion)){
				issues.remove(i);
				//remove one issue, the index should be reduced by 1!!!
				i--;
				continue;
			}
			JSONArray commits = (JSONArray) issue.get("commits");
			for(int j=0; j<commits.size(); j++){
				JSONObject commit = (JSONObject) commits.get(j);
				JSONArray files = (JSONArray) commit.get("files");
				for(int k=0; k<files.size(); k++){
					JSONObject file = (JSONObject) files.get(k);
					if(file.get("smells") == null){
						file.put("total_smell", 0);
					}else{
						JSONObject smellsPerFile = (JSONObject) file.get("smells");
						if(smellsPerFile.containsKey("bdc")){
							smellsPerFile.remove("bdc");
						}
						int total_smell = 0;
						Iterator it = smellsPerFile.entrySet().iterator();
						while(it.hasNext()){
							Map.Entry pair = (Map.Entry)it.next();
							total_smell += Integer.parseInt(pair.getValue().toString());
						}
						file.put("total_smell", total_smell);
					}
				}
			}
		}
		JSONUtil.writeJSONArray2File(issues, outputIssues);
	}
	
	/**
	 * read the class smell file, get all the versions which have the smells data
	 * @return
	 */
	public static Set<String> getVersions(final String inputFile){
		JSONArray smells = (JSONArray) JSONUtil.readJsonFromFile(inputFile);
		Set<String> versions = new HashSet<String>();
		for(int i=0; i<smells.size(); i++){
			JSONObject smell = (JSONObject) smells.get(i);
			versions.add(smell.get("version").toString());
		}
		return versions;
	}
}
