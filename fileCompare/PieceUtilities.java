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

public class PieceUtilities {

	public static String getPiece(String input, String separator) {
		return getPiece(input, separator, 1);
	}
	
	public static String getPiece(String input, String separator, int pieceNumber) {
		return getPiece(input, separator, pieceNumber, pieceNumber);
	}
	
	public static String getPiece(String input, String separator, int startingPiece, int endingPiece) {
		String inputString;
		String value = "";
		inputString = input;
		int currPiece = 1;
		while (currPiece < startingPiece) {
			int loc = inputString.indexOf(separator);
			if (loc > -1) {
				if (inputString.length() < (loc+separator.length()))
					inputString = "";
				else
					inputString = inputString.substring(loc+separator.length());
			}
            else {
                inputString = "";
            }
			currPiece++;
		}
		int pieces = 0;
		while ((! (currPiece > endingPiece)) && (! (inputString.compareTo("") == 0))) {
			int loc = inputString.indexOf(separator);
			if (loc > -1) {
				if (pieces > 0)
					value = value + separator;
				if (loc > 0)
					value = value + inputString.substring(0,loc);
				if (inputString.length() < (loc + separator.length()))
					inputString = "";
				else
					inputString = inputString.substring(loc+separator.length());
			}
			else {
				if (pieces > 0)
					value = value + separator;
				value = value + inputString;
				inputString = "";
			}
			currPiece++;
			pieces++;
		}
		return value;
	}
	
	public static int numberOfPieces(String inputString, String separator) {
		int loc = inputString.indexOf(separator);
		if (loc == -1)
			return 1;
		int pieces = 0;
		while (! (inputString.compareTo("") == 0)) {
			pieces++;
			if (inputString.length() < (loc + separator.length()))
				inputString = "";
			else
				inputString = inputString.substring(loc+separator.length());
		}
		return pieces;
	}
}
