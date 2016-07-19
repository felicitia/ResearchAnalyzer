package util;

import java.io.File;
import java.io.FilenameFilter;

public class FileUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String dirpath = "/Users/felicitia/Documents/Research/Youn/apps_new/APKS/Previous_Set/39_UR/";
		File[] files = getFilesWithExtension(dirpath, ".xml");
		for(File file: files){
			System.out.println(file.getAbsolutePath());
		}
//		String[] dirs = getSubdirectories(dirpath);
//		System.out.println(dirs.length);
	}

	/**
	 * find all the files in the "dirpath" that have "extension"
	 * extention with ".", e.g. ".xml"
	 * @param dirpath
	 * @param extension
	 * @return
	 */
	public static File[] getFilesWithExtension(String dirpath, String extension){
		File[] files = null;
		try{
		File dir = new File(dirpath);
		files = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name){
				return name.endsWith(extension);
			}
		});}catch(Exception e){
			e.printStackTrace();
		}
		return files;
	}
	
	/**
	 * get all the subdirectory paths in dirpath
	 * @param dirpath
	 * @return subdirectory name, not the full path
	 */
	public static String[] getSubdirectories(String dirpath){
		String[] directories = null;
		try{
			File dir = new File(dirpath);
			directories = dir.list(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
				    return new File(current, name).isDirectory();
				  }
				});
		}catch(Exception e){
			e.printStackTrace();
		}
		return directories;
	}
}
