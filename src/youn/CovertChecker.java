package youn;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import util.FileUtil;

public class CovertChecker {

	static String dir = "/Users/felicitia/Documents/Research/Youn/apps_new/APKS/";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String[] subdirs = FileUtil.getSubdirectories(dir);
		for(String subdir: subdirs){
			File[] xmlFiles = FileUtil.getFilesWithExtension(dir+subdir, ".xml");
			for(File xmlFile: xmlFiles){
				if(checkPath(xmlFile.getAbsolutePath())){
					System.out.println(xmlFile.getName());
				}else{
					System.out.println(xmlFile.getName());
				}
			}
		}
	}

	/**
	 * check whether there's any vulneribilityElements that have children > 1
	 * @param filename whole path of the xml file, including extension
	 * @return if exists, return 1
	 */
	public static boolean checkPath(String filename){
		try{
		File file = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList vulnerabilities = doc.getDocumentElement().getElementsByTagName("vulnerabilities");
		if(vulnerabilities == null){
			return false;
		}
		NodeList vulnerability = doc.getDocumentElement().getElementsByTagName("vulnerability");
		for(int i=0; i<vulnerability.getLength(); i++){
			Node vulnerabilityNode = vulnerability.item(i);
			Element vulnerabilityElement = (Element) vulnerabilityNode;
			if(vulnerabilityElement.getElementsByTagName("vulnerabilityElements").getLength() >1){
				return true;
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
