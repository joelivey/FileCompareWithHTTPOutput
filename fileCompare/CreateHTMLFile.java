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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

import fileCompare.OsUtils;

/**
 * @author jivey
 *
 */
public class CreateHTMLFile {
	
	public static void generateHTMLFile(String file1, String file2, String fileName, 
	          String date1str, String date2str, String outDirectory) throws Exception {
		file1 = file1.replaceAll("<","&lt;");
		file1 = file1.replaceAll(">","&gt;");
		file2 = file2.replaceAll("<","&lt;");
		file2 = file2.replaceAll(">","&gt;");

		String[] sequence = GetFileOrder.getFullSequence(file1, file2);
		String[] outLines = lineList(file1,file2,sequence);
		String outFile = buildPage(outLines, fileName, date1str, date2str);
		try {
			// try and use current location.
			String location = outDirectory+fileName+"_"+date1str+"_vs_"+date2str+".html";
			FileWriter fw;
			fw = new FileWriter(location);
			fw.write(outFile);
			fw.flush();
			fw.close();
			runCommand(location);
			Thread.sleep(2000);
		} catch (Exception e) {
			throw new Exception(e.getMessage()+" Routine Compare Error 001");
		}
	}
	
	static String[] lineList(String file1, String file2, String[] linNums) {
		String[] file1List = getList(file1);
		String[] file2List = getList(file2);
		String[] outFileList = new String[file1.length()+file2.length()];
		int last1 = 0;
		int last2 = 0;
		int L1 = 0;
		int L2 = 0;
		int count = 0;
		String value;
		for (int i=0; i<linNums.length; i++) {
			value = linNums[i];
			L1 = Integer.parseInt(PieceUtilities.getPiece(linNums[i],"^"));
			L2 = Integer.parseInt(PieceUtilities.getPiece(linNums[i],"^",2));
			count = listLine(last1,last2,L1,L2,file1List,file2List,outFileList);
			last1 = L1+1;
			last2 = L2+1;
		}
		// finish any lines not already processed
		for (L1++ ; L1<file1List.length; L1++) {
			outFileList[count++] = "> "+file1List[L1];	
		}
		for (L2++ ; L2<file2List.length; L2++) {
			outFileList[count++] = "< "+file2List[L2];	
		}

		String[] resultString = new String[count];
		for (int i=0; i<count; i++) {
			resultString[i] = outFileList[i];
		}
		return resultString;
	}
	
	public static String[] getList(String input) {
		String[] result = new String[1000];
		int count = 0;
		if (input.length() ==0) {
			return new String[0];
		}
		if (input.charAt(input.length()-1) != '\n') {
			input = input + "\r\n";
		}
		while (input.contains("\n")) {
			int loc = input.indexOf("\n");
			String str = input.substring(0,loc);
			input = input.substring(loc+1);
			result[count++] = str;
		}
		String[] finalString = new String[count];
		for (int i=0; i<count; i++) {
			finalString[i] = result[i];
		}
		return finalString;
	}

	static int listLine(int oldL1, int oldL2, int currL1, int currL2, 
			String[] file1, String[] file2, String[] outFileList) {

		int outCount = 0;
		while (outFileList[outCount] != null) {
			outCount++;
		}
		for (int lval = oldL1; lval<currL1; lval++) {
			outFileList[outCount++]="> "+file1[lval];
		}
		for (int lval = oldL2; lval<currL2; lval++) {
			outFileList[outCount++] = "< "+file2[lval];
		}
		if (file1[currL1].compareTo(file2[currL2]) == 0) {
				outFileList[outCount++] = "  "+ file1[currL1];
		}
		else {
			outFileList[outCount++]="> "+file1[currL1];
			outFileList[outCount++]="< "+file2[currL2];
		}
		return outCount;
	}

	static String startRemoved = "<span class=\"changed\"></span><span class=\"removed\" alt=\"start removed code\">";
	static String startNew = "<span class=\"changed\"></span><span class=\"new\" alt=\"start new code\">";
	static String startSame = "<span class=\"changed\"></span><span class=\"same\" alt=\"start unchanged code\">";
	static String endSet = "</span>";

	static String buildPage(String[] text, String fileName, String date1str, String date2str) {
		String outString ="<DOCTYPE! html>\n<html>\n<head>\n<title>Routine Comparison for "+fileName+"</title>\n<style type=\"text/css\">\n.removed, .both {\n   color: "+FileCompare.oldColor+";\n   font-weight: bold;\n}\n.new {\n   color: "+FileCompare.newColor+";\n   font-weight: bold;\n}\n.same {\n 	color: "+FileCompare.sameColor +";\n}\n.unexpected {\n	color: purple;\n}\n.pageBackground {\n	background-Color: "+FileCompare.backColor+";\n}\n</style>\n</head>\n<body class=\"pageBackground\" style=\"font-family:courier new;\"><h2>\n	<form>\n		<button type=\"button\" id=\"optionDisplayButton\" onclick=\"changeOptionDisplay()\">Show Options</button>\n<div id=\"optionDiv\" style=\"display: none\">\n<h6><div id=\"div1\">\n  <input type=\"radio\" value=\"both\" name=\"radio1\" onclick=\"radio1Changed(this)\" checked />show both old and new code<br>\n  <input type=\"radio\" value=\"old\" name=\"radio1\" onclick=\"radio1Changed(this)\" />show only old version of code<br>\n  <input type=\"radio\" value=\"new\" name=\"radio1\" onclick=\"radio1Changed(this)\" />show only new version of code<br>\n<input type=\"radio\" value=\"changed\" name=\"radio1\" onclick=\"radio1Changed(this)\" />show only changed code segments<br>\n</div>\n<br>\n<div>\n  select any desired colors, then press the button to activate them<br>\n  select background color:&nbsp;&nbsp;&nbsp;&nbsp;\n	<select name=\"backgroundcolor\" id=\"backgroundColor\" value=\""+FileCompare.backColor+"\">\n	<option>"+FileCompare.backColor+"</option><option>lightblue</option><option>white</option><option>aqua</option><option>black</option><option>blue</option><option>fuchsia</option><option>gray</option><option>green</option><option>lime</option><option>maroon</option><option>navy</option><option>olive</option><option>orange</option><option>purple</option><option>red</option><option>silver</option><option>teal</option><option>yellow</option>\n	</select>	<br>\n	\n	select old code color:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n	<select name=\"oldcolor\" id=\"oldColor\" value=\""+FileCompare.oldColor+"\">\n		<option>"+FileCompare.oldColor+"</option><option>red</option><option>aqua</option><option>black</option><option>blue</option><option>fuchsia</option><option>gray</option><option>green</option><option>lime</option><option>maroon</option><option>navy</option><option>olive</option><option>orange</option><option>purple</option><option>silver</option><option>teal</option><option>white</option><option>yellow</option>	</select><br>\n\n	select new code color:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n	<select name=\"newcolor\" id=\"newColor\" value=\""+FileCompare.newColor+"\">\n		<option>"+FileCompare.newColor+"</option><option>green</option>\n		<option>yellow</option><option>aqua</option><option>black</option><option>blue</option><option>fuchsia</option><option>gray</option><option>lime</option><option>maroon</option><option>navy</option><option>olive</option><option>orange</option><option>purple</option><option>red</option><option>silver</option><option>teal</option><option>white</option>\n	</select><br>\n	\n	select unchanged code color:\n	<select name=\"samecolor\" id=\"sameColor\" value=\""+FileCompare.sameColor+"\">\n		<option>"+FileCompare.sameColor+"</option><option>black</option><option>aqua</option><option>blue</option><option>fuchsia</option><option>gray</option><option>green</option><option>lime</option><option>maroon</option><option>navy</option><option>olive</option><option>orange</option><option>purple</option><option>red</option><option>silver</option><option>teal</option><option>white</option><option>yellow</option>	</select><br>\n\n<button type=\"button\" onclick=\"colorsChanged()\">Activate</button>\n</div>\n</h6>\n</div>\n</form>\n<div>\n\n" +	
		"Lines are indicated as <span class=\"removed\">NOT PRESENT<span class=\"both\">,</span></span> <span class=\"new\">NEWLY PRESENT</span> or <span class=\"same\">UNCHANGED</span> in the version of "+fileName+" from "+date1str+" when compared to the version from "+date2str+".<pre style=\"font-weight:bold\"><code>\n";
		// finish any lines not already processed
		boolean found = false;
		String type = "";
		String line = "";
		for (int i=0; i<text.length; i++) {
			if (text[i].indexOf("< ") == 0) {
				if (type.compareTo("<") != 0){
					if (type.compareTo("") != 0) {
						outString = outString + endSet;
					}
					outString = outString + startRemoved;
					type = "<";
				}
				line = text[i].substring(2);
				line = markBadCharsOnLine(line,false);
				outString = outString + line +"\n";
			}
			if (text[i].indexOf("> ") == 0) {
				if (type.compareTo(">") != 0){
					if (type.compareTo("") != 0) {
						outString = outString + endSet;
					}
					outString = outString + startNew;
					type = ">";
				}
				line = text[i].substring(2);
				line = markBadCharsOnLine(line,false);
				outString = outString + line +"\n";
			}
			if (text[i].indexOf("  ") == 0) {
				if (type.compareTo("  ") != 0){
					if (type.compareTo("") != 0) {
						outString = outString + endSet;
					}
					outString = outString + startSame;
					type = "  ";
				}
				line = text[i].substring(2);
				line = markBadCharsOnLine(line,true);
				outString = outString + line +"\n";
			}
		}
		outString = outString + endSet;
		outString = outString + "</code></pre><h2></div>";
		outString = outString + "<script>\n  document.getElementById(\"backgroundColor\").selectedIndex = 0;\n	document.getElementById(\"oldColor\").selectedIndex = 0;\n	document.getElementById(\"newColor\").selectedIndex = 0;\n	document.getElementById(\"sameColor\").selectedIndex = 0;\n	\n  function radio1Changed(myRadio) {\n  	var el, i;\n  	var value = myRadio.value;\n  	  		el = document.getElementsByClassName(\"both\");\n  		for (i = 0; i<el.length; i++) {\n  		   el[i].style.display = \"none\";\n  		 }\n  		el = document.getElementsByClassName(\"same\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  		el = document.getElementsByClassName(\"changed\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display=\"none\";\n  			el[i].innerHTML = \"\";\n  		}\n   	if (value == \"old\") {\n  		el = document.getElementsByClassName(\"new\");\n  		for (i = 0; i<el.length; i++) {\n  		   el[i].style.display = \"none\";\n  		 }\n  		el = document.getElementsByClassName(\"removed\");\n  		for (i = 0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  	}\n  	else if (value == \"new\") {\n  		el = document.getElementsByClassName(\"removed\")\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"none\";\n  		}\n  		el = document.getElementsByClassName(\"new\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  	}\n  	else if (value == \"changed\") {\n  		el = document.getElementsByClassName(\"new\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  		el = document.getElementsByClassName(\"removed\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  		el = document.getElementsByClassName(\"both\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  		el = document.getElementsByClassName(\"same\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"none\";\n  		}\n  		el = document.getElementsByClassName(\"changed\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display=\"inline\";\n  			el[i].innerHTML=\"\\n--------------------------\\n\";\n  		}\n  	}\n  	else {\n  		el = document.getElementsByClassName(\"removed\")\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  		el = document.getElementsByClassName(\"new\");\n  		for (i=0; i<el.length; i++) {\n  			el[i].style.display = \"inline\";\n  		}\n  		el = document.getElementsByClassName(\"both\");\n  		for (i = 0; i<el.length; i++) {\n  		   el[i].style.display = \"inline\";\n  		 }\n   	}\n  }\n  \n  function changeOptionDisplay() {\n  	var x = document.getElementById(\"optionDisplayButton\");\n  	if (x.innerHTML == \"Show Options\") {\n  		x.innerHTML = \"Hide Options\";\n  		document.getElementById(\"optionDiv\").style.display = \"inline\";\n  	}\n  	else {\n  		x.innerHTML = \"Show Options\";\n  		document.getElementById(\"optionDiv\").style.display = \"none\";\n  	}\n  }\n  \n  function colorsChanged() {\n  	function changeOneSet(listId, pageClassName, propertyName) {\n  		var i, x, y, opts;\n  		x = document.getElementById(listId).selectedIndex;\n  		opts = document.getElementById(listId).options;\n  		y = document.getElementsByClassName(pageClassName);\n  		for (i = 0; i < y.length; i++) {\n  			if (propertyName == \"background\") {\n  				y[i].style.background = opts[x].text;\n  			}\n  			else {\n  				y[i].style.color = opts[x].text;\n  			}\n  		}\n  	}\n  	\n  	changeOneSet(\"backgroundColor\", \"pageBackground\", \"background\");\n  	changeOneSet(\"newColor\", \"new\", \"color\");\n  	changeOneSet(\"oldColor\", \"removed\", \"color\");\n  	changeOneSet(\"sameColor\", \"same\", \"color\");\n/*\n  	var i, x, y, opts;\n  	x = document.getElementById(\"backgroundColor\").selectedIndex;\n  	opts = document.getElementById(\"backgroundColor\").options;\n  	y = document.getElementsByClassName(\"pageBackground\");\n  	for (i = 0; i < y.length; i++) {\n  		y[i].style.background=opts[x].text;\n  	}\n  	x = document.getElementById(\"newColor\").selectedIndex;\n  	opts = document.getElementById(\"newColor\").options;\n  	y = document.getElementsByClassName(\"new\");\n  	for (i = 0; i < y.length; i++) {\n  		y[i].style.color=opts[x].text;\n  		y[i].style.color=opts[x].text;\n  	}\n  	x = document.getElementById(\"oldColor\").selectedIndex;\n  	opts = document.getElementById(\"oldColor\").options;\n  	y = document.getElementsByClassName(\"removed\");\n  	for (i = 0; i < y.length; i++) {\n  		y[i].style.color=opts[x].text;\n  	}\n  	x = document.getElementById(\"sameColor\").selectedIndex;\n  	opts = document.getElementById(\"sameColor\").options;\n  	y = document.getElementsByClassName(\"same\");\n  	for (i = 0; i < y.length; i++) {\n  		y[i].style.color=opts[x].text;\n  	}\n*/\n  }\n</script>\n";
		outString = outString + "</BODY>\n</html>";
		return outString;
	}
	
	static void runCommand(String filename) {
   	if (OsUtils.isWindows()) {
   	    try {
            Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+filename);
        }
    	  catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
    }
   	else {
    	Runtime runtime = Runtime.getRuntime();
    	try {
    		runtime.exec("xdg-open " + filename);
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    }
  }

	public static String markBadChars(String codeToCheck) {
		String result = "";
		int loc = 0;
		while (codeToCheck.length()>0) {
			int loc1 = codeToCheck.indexOf("\r\n",loc);
			String str1 = codeToCheck.substring(loc,loc1+2);
			if (codeToCheck.length() > loc1+2)
				codeToCheck = codeToCheck.substring(loc1+2);
			else
				codeToCheck = "";
			result = result + markBadCharsOnLine(str1,true);
		}
		return result;
	}
	
	private static String markBadCharsOnLine(String inputLine, boolean markUnexpected) {
		// show spaces and/or tabs from end of lines
		int loc=inputLine.length()-2; // comes in with only a \r on end
		int count=0;
		char charVal = 0;
		if (loc > -1) {
			charVal = inputLine.charAt(loc);
			while (charVal <= ' ') {
				count++;
				loc--;
				if (loc < 0)
					break;
				charVal = inputLine.charAt(loc);
			}
		}
		if (count > 0) {
			String endStr = inputLine.substring(loc+1);
			inputLine = inputLine.substring(0,loc+1);
			for (int i=0; i<count; i++) {
				charVal = endStr.charAt(i);
				if (charVal == ' ') {
					inputLine += markChar(charVal,markUnexpected);
				}
				else if (charVal == '\t') {
					inputLine += markChar(charVal,markUnexpected);
				}
				else if (charVal == 0) {
					inputLine += markChar(charVal,markUnexpected);
				}
				else {
					char controlChar = (char)(charVal+64);
					inputLine += markChar(controlChar,markUnexpected);
				}
			}
			inputLine = inputLine + "\r";
		}
		boolean tabFound = false;
		String outputLine = "";
		for (int i=0; i<inputLine.length()-1; i++) {
			charVal = inputLine.charAt(i);
			if (charVal < ' ') {
				if (charVal == '\t') {
					if (! tabFound) {
						tabFound = true;
						outputLine += "\t";
					}
					else {
						outputLine += markChar(charVal,markUnexpected);
					}
				}
				else if (charVal == 0) {
					outputLine += markChar(charVal,markUnexpected);
				}
				else {
					outputLine += markChar(((char)(charVal+64)),markUnexpected);
				}
			}
			else {
				outputLine = outputLine + charVal;
			}
		}
		return outputLine + "\r";
	}
	
	private static String markChar(char inputChar, boolean markUnexpected) {
		String result = "<span ";
		if (markUnexpected) {
			result = result + "class=\"unexpected\" ";
		}
		result = result + "alt=\"unexpected character\">&lt;";
		if (inputChar > ' ') {
			result = result + "ctrl-";
		}
		if (inputChar == ' ') {
			result = result + "space";
		}
		else if (inputChar == '\t') {
			result = result + "tab";
		}
		else {
			result = result + inputChar; 
		}
		result = result + "&gt;</span>";
		return result;
	}
	
	static String setspaces(String input) {
		String output = "";
		while (input.length() > 0) {
			char charval = input.charAt(0);
			if (charval == '\t') {
				for (int i=0; i<4; i++) {
					output = output + "&nbsp;";
				}
			}
			else if (charval == ' ') {
				output = output + "&nbsp;";
			}
			else {
				output = output + charval;
			}
			input = input.substring(1);
		}
		return output;
	}
	

}
