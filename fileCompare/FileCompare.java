package fileCompare;

/**
 * @author Joel L. Ivey (jivey@jiveysoft.com) December, 2015
 * 
 * This application takes the names of two directories as arguments, then obtains the list of files 
 * in the two directories, identifies those with the same names, and if they are not identical
 * will generate and display an html file which lists the files and shows the differences 
 * between them.  
 * 
 * A third optional argument is the name of the directory in which the html files will be saved.
 * If this argument is not specified, the file will be placed in the directory which contains
 * the most recent version of each of the files being compared.
 * 
 * If other colors are desired, the arguments -old:, -new:, and -same: can be used followed
 * immediately by a desired color (any of the 140 defined colors) (e.g., -old:blue) for an 
 * old or removed line (-old:), a newly entered line (-new:) or an unchanged line (-same:) 
 * [color specification can also be by hexadecimal notation (e.g., -old:#0000ff), or by rgb 
 * notation (e.g., -old:rgb(0,0,255)).
 * 
 * The concept and initial code was created as a part of the work on the M-Editor for Eclipse
 * that I created while I was an employee of the Department of Veteran Affairs.  This version 
 * was created as a stand alone command line application after retirement from that organization. 
 *
 * The program has been run in both Windows and Linux environments.
 * 
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import fileCompare.GetRoutineNames;

public class FileCompare {
	
	private static String directory1 = "";
	private static String directory2 = "";
	private static String directory3 = "";
	
	public static String oldColor = "red";
	public static String newColor = "green";
	public static String sameColor = "black";
	public static String backColor = "lightblue";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// identify and get argument values into the static variables
		handleArgs(args);

		if ((directory1.compareToIgnoreCase("help") == 0) || 
				directory1.toUpperCase().startsWith("-HE") || directory1.contains("?")) {
			System.out.println("It is presumed you have the class files in a directory named fileCompare and are at the \ncommand line in the directory above fileCompare.  Start the command line with something like\n  java -cp . fileCompare.FileCompare DirSpec1 DirSpec2\nwhere DirSpec1 and DirSpec2 are directory specifications for the top directories where files\nreside.  The application will search the directories (and descending ones) for files with the\nsame names, and will identify any that are not identical and will create html pages showing\nthe line that are different between the two files.\n\nA third directory can be specified, if desired, and would indicate where the html files showing\nthe differences should be stored.  If not specified, they will be placed in the directory\ncontaining the most recent version of each file pair.");
			System.exit(0);
		}
		if ((directory1.compareTo("") == 0) || (directory2.compareTo("") == 0)) {
			System.out.println("Need two arguments indicating directories for files to be compared if different.");
			System.exit(1);
		}

		// get a list of files in each of the specified directories
		File[] fileList1 = GetRoutineNames.getFileNames(directory1);
		File[] fileList2 = GetRoutineNames.getFileNames(directory2);
		// check for valid directories
		String error = "";
		String message = "";
		try {
			int length = fileList1.length;
		}
		catch (Exception e) {
			error = "y";
            System.out.println("Directory " + directory1 + " is not valid");
            message = e.getMessage();
		}
		try {
			int length = fileList2.length;
		}
		catch (Exception e) {
            if (error == "") {
            	error = "y";
            }
            else {
            	error = "ies";
            }
            message = e.getMessage();
            System.out.println("Directory "+ directory2 + " is not valid");
		}
		if (error != "") {
			throw new Exception(message+"\n\n invalid director"+error);
		}
		// determine which files are present in both directories
		File[] matchingFilesList = GetRoutineNames.getMatchingFiles(fileList1, fileList2);
		if (matchingFilesList.length > 0) {
			FileCompare.processFiles(matchingFilesList);
		}
	}

	/*
	 * handleArgs -- identify arguments those starting with a '-' character will identify
	 *               colors to be used.  Otherwise they identify the directories to be compared,
	 *               and if a third directory is present, where the output file should be placed.
	 */
	private static void handleArgs(String[] args) {
		for (int i=0; i<args.length; i++) {
			if (args[i].charAt(0) == '-') {
				if (args[i].startsWith("-old:")) {
					oldColor = args[i].substring(5);
				}
				else if (args[i].startsWith("-new:")) {
					newColor = args[i].substring(5);
				}
				else if (args[i].startsWith("-same:")) {
					sameColor = args[i].substring(6);
				}
				else if (args[i].startsWith("-back:")) {
					backColor = args[i].substring(6);
				}
				else {
					System.out.println("Unknown argument: "+args[i]);
				}
			}
			else {
				if (directory1.compareTo("") == 0) {
					directory1 = args[i];
					if ((! directory1.endsWith("\\")) || (! directory1.endsWith("/"))) {
						directory1 = directory1 + "/";
					}
				}
				else if (directory2.compareTo("") == 0) {
					directory2 = args[i];
					if ((! directory2.endsWith("\\")) || (! directory2.endsWith("/"))) {
						directory2 = directory2 + "/";
					}
				}
				else if (directory3.compareTo("") == 0) {
					directory3 = args[i];
					if ((! directory3.endsWith("\\")) || (! directory3.endsWith("/"))) {
						directory3 = directory3 + "/";
					}
					//; if directory3 (the optional folder for html files) doesn't exist create it
					new File(directory3.substring(0, directory3.length()-1)).mkdirs();
				}
				else {
					System.out.println("Unknown argument encountered: "+args[i]);
				}
			}
		}
		
	}
	
	public static void processFiles(File[] matchingFiles) {
		String file1 = "";
		String file2 = "";
		String fileName1 = "";
		String fileName2 = "";
		String tempStr = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd@kkmmss");
		for (int i=0; i<matchingFiles.length; i++) {
			String outDirectory = directory3;
			if (outDirectory.compareTo("") == 0) {
				outDirectory = directory1;
			}
			fileName1 = directory1+matchingFiles[i].getName();
			fileName2 = directory2+matchingFiles[i].getName();
			File filevar1 = new File(fileName1);
			File filevar2 = new File(fileName2);
			Date date1 = new Date(filevar1.lastModified());
			Date date2 = new Date(filevar2.lastModified());
			
			// make sure 1 is the newer version
			if (filevar2.lastModified() > filevar1.lastModified()) {
				tempStr = fileName1;
				fileName1 = fileName2;
				fileName2 = tempStr;
				Date tempDate = date1;
				date1 = date2;
				date2 = tempDate;
				if (directory3.compareTo("") == 0) {
					outDirectory = directory2;
				}
			}
			String date1Str = formatter.format(date1);
			String date2Str = formatter.format(date2);
			

			file1 = GetRoutineNames.fileToString(fileName1);
			file2 = GetRoutineNames.fileToString(fileName2);
			
			// may have characters with different line end characters
			file1 = catchReturns(file1);
			file2 = catchReturns(file2);
			
			String winEnd = "" + (char)0xd + (char)0xa;
			if (file1.contains(winEnd)) {
				if (! file2.contains(winEnd)) {
					file2 = toWindowsEnd(file2);
				}
			}
			else if (file2.contains(winEnd)) {
				file1 = toWindowsEnd(file1);
			}
			else {
				file1 = toWindowsEnd(file1);
				file2 = toWindowsEnd(file2);
			}
			
			// if .m extension convert leading spaces to tabs
			if (fileName1.contains(".m")) {
				file1 = GetRoutineNames.checkTabsForM(file1);
				file2 = GetRoutineNames.checkTabsForM(file2);
			}
			
			
			if (file1.compareTo(file2) != 0) {
				try {
					String name = matchingFiles[i].getName();
					CreateHTMLFile.generateHTMLFile(file1, file2, name, date1Str, date2Str, outDirectory);
				} catch (Exception e) {
		            System.out.println("exception happened - here's what I know: ");
		            e.printStackTrace();
				}
			}
		}
	}

	// catch any CR only and convert to CR-LF
	public static String catchReturns(String filetext) {
		String windowsEnd = String.valueOf((char)0xd) + String.valueOf((char)0xa);
		String windowsReturn = String.valueOf((char)0xd);
		String result = filetext;
		result = result.replaceAll(windowsEnd,windowsReturn);
		result = result.replaceAll(windowsReturn,windowsEnd);
		return result;
	}
	
	// convert linux newline to windows CR linefeed
	// find the one character and turn it into two
	public static String toWindowsEnd(String filetext) {
		String linuxEnd = String.valueOf((char)0xa);
		String windowsEnd = String.valueOf((char)0xd) + String.valueOf((char)0xa);
		String result = filetext;
		result = result.replaceAll(linuxEnd,windowsEnd);
		return result;
	}

	// convert windows CR linefeed to linux newline
	// find the two characters and turn them into one	
	public static String toLinuxEnd(String filetext) {
		String linuxEnd = String.valueOf((char)0xa);
		String windowsEnd = String.valueOf((char)0xd) + String.valueOf((char)0xa);
		String result = filetext;
		result = result.replaceAll(windowsEnd,linuxEnd);
		return result;
	}
		
		
	
	/**
	 * @param directoryPath
	 * @return
	 */
/*
	public static File[] getFileList(String directoryPath) {
		File[] fileList = null;
		int fileCount = 0;
		File dir = new File(directoryPath);
		File[] files = dir.listFiles();
		int count = 0;
		for (int i=0; i<files.length; i++)
			if (files[i].isFile()) {
				count++;
			}
		fileList = new File[count];
		for (int i=0; i < files.length; i++){
			if (files[i].isFile()) {
				fileList[fileCount++] = files[i];
			}
		}
		return fileList;
	}
*/

}
