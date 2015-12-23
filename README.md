# FileCompareWithHTTPOutput

This project provides functionality for generating HTTP files showing differences between files with the same names 
in different directories with different date/times.  The directories or folders are compared and those files with different
date/times are compared.  If any differences are found an html file is generated showing the text and highlighting the 
differences in different colored fonts.  This file is saved in either the folder with the file version with the most 
recent date/time or in a folder specifed by a third optional argument.

If the files are saved in a directory fileCompare, then using a opening a command line (e.g., either the dos command prompt
in Windows or terminal in Linux) in the folder containing fileCompare, a command such as:

   java -cp . fileCompare/FileCompare ./Folder1 ./Folder2 ./FolderSave
   
where ./Folder1 and ./FolderPath2 are the paths to the folders to be searched for routines with differences, and 
./FolderSave is an optional argument providing the path to the folder in which generated html files are to be stored.
After each html file is saved, the contents are opened in the systems default browser (if there a number of files,
it would be a good idea to open a new browser window before starting).

The colors of the fonts used may be changed from the default at run time by specifying arguments on the command line or 
when viewing in the browser there is an options button to specify different colors and to determine how much of the 
changes will be displayed.


