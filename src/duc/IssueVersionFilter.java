package duc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.Constant;
import util.JSONUtil;
import util.StringUtil;

public class IssueVersionFilter {

	static Set<String> versions;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(Constant.PROJECT == Constant.HADOOP){
			versions = getVersionsFromSmell(Constant.HADOOP_SMELL_ACDC);
			filterIssues(Constant.HADOOP_ALL_ACDC, Constant.HADOOP_ACDC_FILTER);
			versions = getVersionsFromSmell(Constant.HADOOP_SMELL_ARC);
			filterIssues(Constant.HADOOP_ALL_ARC, Constant.HADOOP_ARC_FILTER);
			versions = getVersionsFromSmell(Constant.HADOOP_SMELL_PKG);
			filterIssues(Constant.HADOOP_ALL_PKG, Constant.HADOOP_PKG_FILTER);
		}else if(Constant.PROJECT == Constant.IVY){
			versions = getVersionsFromSmell(Constant.IVY_SMELL_ACDC);
			filterIssues(Constant.IVY_ALL_ACDC, Constant.IVY_ACDC_FILTER);
			versions = getVersionsFromSmell(Constant.IVY_SMELL_ARC);
			filterIssues(Constant.IVY_ALL_ARC, Constant.IVY_ARC_FILTER);
			versions = getVersionsFromSmell(Constant.IVY_SMELL_PKG);
			filterIssues(Constant.IVY_ALL_PKG, Constant.IVY_PKG_FILTER);
		}else if(Constant.PROJECT == Constant.LUCENE){
			versions = getVersionsFromSmell(Constant.LUCENE_SMELL_ACDC);
			filterIssues(Constant.LUCENE_ALL_ACDC, Constant.LUCENE_ACDC_FILTER);
			versions = getVersionsFromSmell(Constant.LUCENE_SMELL_ARC);
			filterIssues(Constant.LUCENE_ALL_ARC, Constant.LUCENE_ARC_FILTER);
			versions = getVersionsFromSmell(Constant.LUCENE_SMELL_PKG);
			filterIssues(Constant.LUCENE_ALL_PKG, Constant.LUCENE_PKG_FILTER);
		}else if(Constant.PROJECT == Constant.STRUTS2){
			versions = getVersionsFromSmell(Constant.STRUTS2_SMELL_ACDC);
			filterIssues(Constant.STRUTS2_ALL_ACDC, Constant.STRUTS2_ACDC_FILTER);
			versions = getVersionsFromSmell(Constant.STRUTS2_SMELL_ARC);
			filterIssues(Constant.STRUTS2_ALL_ARC, Constant.STRUTS2_ARC_FILTER);
			versions = getVersionsFromSmell(Constant.STRUTS2_SMELL_PKG);
			filterIssues(Constant.STRUTS2_ALL_PKG, Constant.STRUTS2_PKG_FILTER);
		}else{
			System.out.println("Project invalid...");
		}
		System.out.println("done! (๑•ᴗ•๑)♡‼");
		
//		versions = getVersionsFromIssue(Constant.STRUTS2_ISSUE_FILE);
//		StringUtil.printStringSet(versions);
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
	public static Set<String> getVersionsFromSmell(final String inputFile){
		JSONArray smells = (JSONArray) JSONUtil.readJsonFromFile(inputFile);
		Set<String> versions = new HashSet<String>();
		for(int i=0; i<smells.size(); i++){
			JSONObject smell = (JSONObject) smells.get(i);
			versions.add(smell.get("version").toString());
		}
		return versions;
	}
	
	/**
	 * read the original issue file, get all the versions in "affect" field
	 * @param inputFile
	 * @return
	 */
	public static Set<String> getVersionsFromIssue(final String issueFile){
		JSONArray issues = (JSONArray) JSONUtil.readJsonFromFile(issueFile);
		Set<String> versions = new HashSet<String>();
		for(int i=0; i<issues.size(); i++){
			JSONObject issue = (JSONObject) issues.get(i);
			versions.add(issue.get("affect").toString());
		}
		return versions;
	}
}
