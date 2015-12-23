/**
 * 
 */
package fileCompare;

import fileCompare.PieceUtilities;

/**
 * @author Joel
 *
 */
public class GetFileOrder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String file1 = "C:/m1/XTMUNIT.m";
		String file2 = "C:/m2/XTMUNIT.m";
		
		String[] file1List = CreateHTMLFile.getList(file1);
		String[] file2List = CreateHTMLFile.getList(file2);
	}
	
	public static String[] getFullSequence(String file1, String file2) {
		String[] file1List = CreateHTMLFile.getList(file1);
		String[] file2List = CreateHTMLFile.getList(file2);
		boolean reverse = false;
		if (file1List.length > file2List.length) {
			reverse = true;
			file1List = CreateHTMLFile.getList(file2);
			file2List = CreateHTMLFile.getList(file1);
		}

		String[] list1 = getUniqueSequence(file1List, file2List);

/*
System.out.println("getFullSequence - list1 from getUniqueSequence:");
for (int i=0; i<list1.length; i++) {
	System.out.println(i + "  " + list1[i]);
		}
*/
		String part1, part2;
		String[] partList;
		String[] fullList = new String[file1List.length];
		int listCount = 0;
		int base1 = 0;
		int base2 = 0;
		for (int i=0; i<list1.length; i++) {
			int next1 = Integer.parseInt(PieceUtilities.getPiece(list1[i],"^"));
			int next2 = Integer.parseInt(PieceUtilities.getPiece(list1[i],"^",2));
			part1 = "";
			part2 = "";
			for (int j=base1; j<(next1+1); j++) {
				part1 = part1 + file1List[j];
				if (! file1List[j].contains("\n")) {
					part1 = part1 + "\n";
				}
			}
// System.out.println(" base1: "+base1+"  next1: "+next1);
// System.out.println(" base2: "+base1+"  next2: "+next1);
			for (int j=base2; j<(next2+1); j++) {
				part2 = part2 + file2List[j];
				if (! file2List[j].contains("\n")) {
					part2 = part2 + "\n";
				}
			}
/*
			System.out.println("i = " + i + "  "+part1.length()+"  "+part2.length());
			System.out.println("  base1: "+base1+"  next1: "+next1+"  base2="+base2+"  next2="+next2);
*/
			partList = getSequence(part1, part2);
/*
			System.out.println("Back from getSequence i="+i+" partList = "+partList.length);
			for (int j=0; j<partList.length; j++) {
				System.out.println(j + "  " + partList[j]);
				int temp = Integer.parseInt(PieceUtilities.getPiece(partList[j], "^"));
				System.out.print("  "+file1List[temp]);
				temp = Integer.parseInt(PieceUtilities.getPiece(partList[j], "^", 2));
				System.out.print("  "+file2List[temp]);
			}
*/
//			listCount = 0;
			for (int j=0; j<partList.length; j++) {
				int num1 = base1 + Integer.parseInt(PieceUtilities.getPiece(partList[j],"^"));
				int num2 = base2 + Integer.parseInt(PieceUtilities.getPiece(partList[j],"^",2));
//				System.out.println("j = "+j+"  listCount="+listCount+"  num1="+num1+"  num2="+num2);
				fullList[listCount++] = num1 + "^" + num2;
//System.out.println("j: "+j+"  "+partList[j]+"  "+fullList[listCount-1]);
			}
			base1 = next1 + 1;
			base2 = next2 + 1;
		}
		String[] result = new String[listCount];
		for (int i=0; i<listCount; i++) {
			String temp = fullList[i];
			if (reverse) {
				String val1 = PieceUtilities.getPiece(temp, "^");
				String val2 = PieceUtilities.getPiece(temp, "^", 2);
				temp = val2 + "^"+ val1;
			}
			result[i] = temp;
		}
		return result;
	}
	
	/*
	 * getUniqueSequence - takes in lists of the lines in order of the
	 *        new and older version of the file, and identifies those
	 *        lines that match in the files and are unique in each file.
	 *        They are then compared to check that whether a line appears
	 *        later in the the file than others following it, which would
	 *        indicate that it has been moved.  If these lines are found,
	 *        they are removed from the list, since they can cause the
	 *        ordering to be disrupted significantly.
	 * 
	 * @arg - String[] newList - newer version of file - list contains
	 *        separate lines of the files in order
	 * @arg - String[] oldList - older version of file - list contains
	 *        separate lines of the files in order
	 */
	public static String[] getUniqueSequence(String[] newList, String[] oldList) {
		String[] newSequence = new String[newList.length];
		String[] result = null;

		// for a line in new list get a comma separated list of
		// all lines in old list that match a line in new list
		int count = 0;
		for (int i=0; i < newList.length; i++) {
			newSequence[i] = "";
			for (int j=0; j<oldList.length; j++) {
				if (newList[i].compareTo(oldList[j]) == 0) {
					newSequence[i] = newSequence[i] + String.valueOf(j) + ",";
					count++;
				}
			}
		}
		
		// generate a new list containing only those pairs which are found only
		// once, so only one number is in the entry for newSequence
		// the new list contains the indexes in the line list separated by "^"
		String[] matchSequence = new String[count];
		String[] uniqueSequence = new String[count];
		count = 0;
		for (int i=0; i<newSequence.length; i++) {
			if ((newSequence[i].compareTo("") != 0) &&
				(PieceUtilities.getPiece(newSequence[i], ",",2,1000).compareTo("") == 0)) {
				uniqueSequence[count++] = i + "^" + PieceUtilities.getPiece(newSequence[i],",");
			}
		}
/*
		System.out.println("uniqueSequence values:");
		for (int i=0; i<count; i++) {
			System.out.println(i+"  "+uniqueSequence[i]);
		}
*/		
		// remove any where the pair follows subsequent pairs
		// such that the line has been added there or a match
		// removed
		int val1, val2;
		String str;
		for (int i=0; i<count; i++) {
			val1 = Integer.parseInt(PieceUtilities.getPiece(uniqueSequence[i],"^",2));
			for (int j=i+1; j<count; j++) {
				val2 = Integer.parseInt(PieceUtilities.getPiece(uniqueSequence[j],"^",2));
				if (val2 < val1) {
					for (int k=i; k<count; k++) {
						uniqueSequence[k] = uniqueSequence[k+1];
					}
					i = i-1;
					count = count - 1;
					break;
				}
			}
		}
		// add the last lines to make sure the full list is covered
		uniqueSequence[count++] = (newList.length-1)+"^"+(oldList.length-1);
		// generate the list for return
		result = new String[count];
		for (int i=0; i<count; i++) {
			result[i] = uniqueSequence[i];
		}
		return result;
	}

	/*
	 * getSequence - give arrays of input lines of two files,
	 *        it compares them and returns an array which contains
	 *        an indicator of lines in the two files that match in
	 *        order of matching (the indices of the the two lines
	 *        separated by "^"
	 */
	public static String[] getSequence(String file1, String file2) {
		String[] file1List = CreateHTMLFile.getList(file1);
		String[] file2List = CreateHTMLFile.getList(file2);
		String[] working1 = new String[file1List.length];
		String[] working2 = new String[file2List.length];
		
		String[] file1Match = new String[file1List.length];
		String[] file2Match = new String[file2List.length];
		
		for (int i=0; i<file1Match.length; i++) {
			file1Match[i] ="";
			for (int j=0; j<file2Match.length; j++) {
				if (file1List[i].compareTo(file2List[j]) == 0) {
					file1Match[i] = file1Match[i] + j + ",";
				}
			}
		}	
		for (int i=0; i<file2Match.length; i++) {
			file2Match[i] ="";
			for (int j=0; j<file1Match.length; j++) {
				if (file2List[i].compareTo(file1List[j]) == 0) {
					file2Match[i] = file2Match[i] + j + ",";
				}
			}
		}
		
		listTestSteps(file1List, file2List);
		listTestSteps2(file1List, file2List);
		
/*
System.out.println("file1List: ");
		for (int i=0; i<file1List.length; i++) {
			System.out.println(i + "  " + file1List[i]);
		}
System.out.println("\n\nfile2List: ");
		for (int i=0; i<file2List.length; i++) {
			System.out.println(i + "  " + file2List[i]);
		}
*/
		int last1 = 0;
		int last2bas = 0;
		int count1 = 0;
		while (last1 < file1List.length) {
			String str1 = file1List[last1++];
			int last2 = last2bas;
			while (last2 < file2List.length) {
				String str2 = file2List[last2++];
				if (str1.compareTo(str2) == 0) {
					working1[count1++] = (last1-1)+"^"+(last2-1);
					last2bas = last2;
					break;
				}
			}
		}
		int last2 = 0;
		int last1bas = 0;
		int count2 = 0;
		while (last2 < file2List.length) {
			String str2 = file2List[last2++];
			last1 = last1bas;
			while (last1 < file1List.length) {
				String str1 = file1List[last1++];
				if (str2.compareTo(str1) == 0) {
					working2[count2++] = (last1-1)+"^"+(last2-1);
					last1bas = last1;
					break;
				}
				last1++;
			}
		}
/*
		System.out.println("\ncount1 = " + count1);
		for (int i=0; i<(count1-1); i++) {
			System.out.println(i + "  " + working1[i]);
			System.out.println(file1List[Integer.parseInt(PieceUtilities.getPiece(working1[i], "^"))-1]);
		}
		System.out.println("\ncount2 = " + count2);
		for (int i=0; i<(count2-1); i++) {
			System.out.println(i + "  " + working2[i]);
			System.out.println(file1List[Integer.parseInt(PieceUtilities.getPiece(working2[i], "^"))-1]);
		}
*/
		int count;
		if (count1 > count2) {
			count = count1;
		}
		else {
			count = count2;
		}
		if (count == 0) {
			return new String[0];
			
		}
		String[] result = new String[count];
		if (count1 > count2) {
			for (int i=0; i<count1; i++) {
				result[i] = working1[i];
			}
		}
		else {
			for (int i=0; i<count2; i++) {
				result[i] = working2[i];
			}
		}
		return result;
	}
	public static void listTestSteps(String[] file1List, String[] file2List) {
		/*
				String[] file1Match = new String[file1List.length];
				String[] file2Match = new String[file2List.length];
				for (int i=0; i<file1Match.length; i++) {
					file1Match[i] ="";
					for (int j=0; j<file2Match.length; j++) {
						if (file1List[i].compareTo(file2List[j]) == 0) {
							file1Match[i] = file1Match[i] + j + ",";
						}
					}
				}	
				for (int i=0; i<file2Match.length; i++) {
					file2Match[i] ="";
					for (int j=0; j<file1Match.length; j++) {
						if (file2List[i].compareTo(file1List[j]) == 0) {
							file2Match[i] = file2Match[i] + j + ",";
						}
					}
				}
				System.out.println("file1Match list: ");
				for (int i=0; i<file1Match.length; i++) {
					System.out.println(i + "  " + file1Match[i]);
					System.out.println("   " + file1List[i]);
				}
				System.out.println("\n\nfile2Match list: ");
				for (int i=0; i<file1Match.length; i++) {
					System.out.println(i + "  " + file1Match[i]);
					System.out.println("   " + file2List[i]);
				}
		*/
			}
			
			public static void listTestSteps2(String[] file1List, String[] file2List) {
				String[] file1Match = new String[file1List.length];
				String[] file2Match = new String[file2List.length];
				for (int i=0; i<file1Match.length; i++) {
					file1Match[i] ="";
					for (int j=0; j<file2Match.length; j++) {
						if (file1List[i].compareTo(file2List[j]) == 0) {
							file1Match[i] = file1Match[i] + j + ",";
						}
					}
				}	
				for (int i=0; i<file2Match.length; i++) {
					file2Match[i] ="";
					for (int j=0; j<file1Match.length; j++) {
						if (file2List[i].compareTo(file1List[j]) == 0) {
							file2Match[i] = file2Match[i] + j + ",";
						}
					}
				}
		/*
				System.out.println("file1Match list: ");
				for (int i=0; i<file1Match.length; i++) {
					if ((file1Match[i].compareTo("") != 0) && (PieceUtilities.getPiece(file1Match[i], ",",2,1000).compareTo("") == 0)) {
						System.out.println(i + "  " + file1Match[i]);
						System.out.print("   " + file1List[i]);
					}
				}
				System.out.println("\n\nfile2Match list: ");
				for (int i=0; i<file2Match.length; i++) {
					if ((file2Match[i].compareTo("") != 0) && (PieceUtilities.getPiece(file2Match[i], ",",2,1000).compareTo("") == 0)) {
						System.out.println(i + "  " + file2Match[i]);
						System.out.print("   " + file2List[i]);
					}
				}
		*/
	}
}
