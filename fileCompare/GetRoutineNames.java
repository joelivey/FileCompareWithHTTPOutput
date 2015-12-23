/**
 * @author Joel L. Ivey (jivey@jiveysoft.com) December, 2015
 * 
 * This file provides a supporting class for the FileCompare functionality in FileCompare.java.
 * 
 * The concept and initial code was created as a part of the work on the M-Editor for Eclipse
 * that I created while I was an employee of the Department of Veteran Affairs.  This version 
 * was created as a stand alone command line application after retirement from that organization. 
 *
 * The program has been run in both Windows and Linux environments.
 * 
 */

package fileCompare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jivey
 *
 */
public class GetRoutineNames {

	/**
	 * @param args
	 */
	public static File[] getDirectoryNames(String directory) {
		File[] bigList = new File[1000];
		File directName = new File(directory);
		File[] currentNames = directName.listFiles();
		File[] tempNames = GetRoutineNames.directoryList(currentNames);
		int listCount = 0;
		for (int i=0; i<tempNames.length; i++) {
			bigList[listCount++] = tempNames[i];
		}
		for (int i=0; i<tempNames.length; i++) {
			File[] tempNames1 = GetRoutineNames.getDirectoryNames(bigList[i].toString());
			for (int j=0; j<tempNames1.length; j++) {
				bigList[listCount++] = tempNames1[j];
			}
		}
		File[] directoryNames = new File[listCount];
		for (int i=0; i<listCount; i++) {
			directoryNames[i] = bigList[i];
		}
		return directoryNames;
	}
	
	public static File[] directoryList(File[] inputList) {
		File[] tempNames = new File[1000];
		int count = 0;
		for (int i=0; i<inputList.length; i++) {
			if (inputList[i].isDirectory()) {
				tempNames[count++] = inputList[i];
			}
		}
		File[] returnList = new File[count];
		for (int i=0; i<count; i++) {
			returnList[i] = tempNames[i];
		}
		return returnList;
	}
	
	public static File[] getFileNames(String directory) {
		File folder = new File(directory);
		File[] fileList = folder.listFiles();
		return fileList;
	}
	
	/*
	 * getMatchingFiles - given two lists of files, it returns a list of files that
	 *                    are present in both lists.
	 */
	public static File[] getMatchingFiles(File[] list1, File[] list2) {
		File[] tempList = new File[2000];
		int count=0;
		String name1 = null;
		String name2 = null;
		for (int i=0; i<list1.length; i++) {
			name1 = list1[i].getName();
			for (int j=0; j<list2.length; j++) {
				name2 = list2[j].getName();
				if (name1.matches(name2)) {
					tempList[count++] = list1[i];
				}
			}
		}
		File[] returnList = new File[count];
		for (int i=0; i<count;i++) {
			returnList[i] = tempList[i];
		}
		return returnList;
	}
	
	/*
	 * areFilesDifferent -- returns an indicator or whether two text files are identical or not 
	 */
	public static boolean areFilesDifferent(File file1, File file2) throws Exception {
		boolean result = false;
		FileInputStream fis1 = new FileInputStream(file1);
		FileInputStream fis2 = new FileInputStream(file2);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(fis1));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
		String line1 = "";
		String line2 = "";
		boolean notDone = true;
		while (notDone) {
			line1 = br1.readLine();
			line2 = br2.readLine();
			if (line1 == null) {
				if (line2 == null) {
					notDone = false;
				}
				else {
					result = true;
					notDone = false;
				}
			}
			else if (line2 == null) {
				result = true;
				notDone = false;
			}
			else if (! (line1.compareTo(line2) == 0)) {
				result = true;
				notDone = false;
			}
		}
		return result;
	}
	
	public String[] filesToCompare(String dir1, String dir2) {
		String[] fileList = new String[20000];
		fileList[0] = "mystring";
		return fileList;
 	}
	
	public static String[] getFileNamesWithTimes(File[] fileList) {
		String[] namesWithTimes = new String[fileList.length];
		for (int i=0; i<fileList.length; i++) {
			Date date = new Date(fileList[i].lastModified());
			SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd@kk:mm:ss:SSS");
			namesWithTimes[i] = fileList[i].getName() + "_" + formatter.format(date);
		}
		return namesWithTimes;
	}

	public static String fileToString(String fileName) {
		String strval;
		try {
			FileReader fr = new FileReader(fileName);
			char[] charbuf = new char[128000];
			int val = fr.read(charbuf,0,128000);
			String strvala = new String(charbuf,0,val);
			strval = strvala;
		} catch (Exception e) {
			strval = "";
		}
		return strval;
	}
	
	public static String checkTabsForM(String data) {
		String result = "\n" + data;

		result = result.replaceAll("\n ", "\n\t");
		result = result.replaceAll("\n([^\t ]+) ", "\n$1\t");
		result = result.substring(1);
		return result;
	}

}
