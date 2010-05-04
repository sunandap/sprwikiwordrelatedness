/* Copyright 2010 Speech and Language Technologies Lab, The Ohio State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.osu.slate.relatedness.swwr.setup.graph;

import java.io.*;
import java.util.*;

/**
 * This program generates a list of valid IDs for a Wikipedia data set.
 * <p>
 * The input is a Wikipedia page.sql file.
 * <p>
 * The output of this program is a .vid file containing an integer array object.  This is used to determine the valid IDs (non-redirect, in the given namespace) for our graph.  The .vid file is used as input for the {@link IDVertexTranslation.data.ValidIDs} constructor.  Additionally, this file will be used to 'compress' the IDs into a contiguous numbering system.
 * <p>
 * <b>usage:</b> java CreateValidIDFile HVMCT inputfile outputfile
 * <p>
 * <i>General Options:</i>
 * <UL>
 * <LI>[H] Print Help (this information)
 * <LI>[V] Verbose Output
 * </UL>
 * <i>Namespace Options:</i> (at least one of these must be specified)
 * <UL>
 * <LI>[M] Include Main Namespace
 * <LI>[C] Include Category Namespace
 * <LI>[T] Include Template Namespace
 * </UL>
 * <i>File Names:</i>
 * <UL>
 * <LI>[inputfile] Input page.sql file
 * <LI>[outputfile] Output .vid file
 * </UL>
 * 
 * @author weale
 * @version 1.0;alpha
 */
public class CreateIDToVertexFile {

	/* Name of the input file generated by Wikipedia (page.sql) */
	private static String inputFileName;

	/* Name of the output file (.vid) */
	private static String outputFileName;
	
	/* Verbose output flag */
	private static boolean verbose;

	private static String baseDir, sourceDir, binaryDir, tempDir;
	private static String type, date, graph;
	  
  private static void parseConfigurationFile(String filename) {
    try {
      Scanner config = new Scanner(new FileReader(filename));
      
      sourceDir = "source";
      binaryDir = "binary";
      tempDir = "tmp";

      while(config.hasNext())
      {
        String s = config.nextLine();
        if(s.contains("<basedir>"))
        {
          baseDir = s.substring(s.indexOf("<basedir>") + 9,
                                s.indexOf("</basedir>"));
        }
        else if(s.contains("<sourcedir>"))
        {
          sourceDir = s.substring(s.indexOf("<sourcedir>") + 11,
                                  s.indexOf("</sourcedir>"));          
        }
        else if(s.contains("<binarydir>"))
        {
          binaryDir = s.substring(s.indexOf("<binarydir>") + 11,
                                  s.indexOf("</binarydir>"));
        }
        else if(s.contains("<tempdir>"))
        {
          tempDir = s.substring(s.indexOf("<tempdir>") + 9,
                                s.indexOf("</tempdir>"));
        }
        else if(s.contains("<type>"))
        {
          type = s.substring(s.indexOf("<type>") + 6,
                             s.indexOf("</type>"));
        }
        else if(s.contains("<date>"))
        {
          date = s.substring(s.indexOf("<date>") + 6,
                             s.indexOf("</date>"));
        }
        else if(s.contains("<graph>"))
        {
          graph = s.substring(s.indexOf("<graph>") + 7,
                              s.indexOf("</graph>"));
        }
      }//end: while(config)
    }//end: try {}
    catch (IOException e) {
      System.err.println("Problem reading from file: " + filename);
      System.exit(1);
    }
    
    inputFileName = baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/"
                  + type + "-"+ date + "-" + "page.sql";
    
    outputFileName = baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/"
                   + type + "-"+ date + "-" + graph + ".vid";
  }
	  
 /**
  * Checks and opens the input file.
  * 
  * @return Scanner for reading the input file
  */
  private static Scanner openInputFile() {
    try {
      Scanner in = new Scanner(new FileReader(inputFileName));
      return in;		
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + inputFileName);
      e.printStackTrace();
    }
		
    System.exit(1);
    return null;	
  }//end: openInputFile()
	
 /**
  * Checks and opens the output file.
  * 
  * @return ObjectOutputStream for writing
  */
  private static ObjectOutputStream openOutputFile() {
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFileName));
      return out;		
    }//end: try {}
    catch (FileNotFoundException e) {
      System.out.println("File not found: " + outputFileName);
      e.printStackTrace();
    }
    catch (IOException e) {
      System.out.println("Problem with file: " + outputFileName);
      e.printStackTrace();
    }
		
    System.exit(1);
    return null;
  }//end: openOutputFile()

 /**
  * Runs the program.
  * 
  * @param args Command-line parameters
  * 
  */
  public static void main(String[] args) {
		
   /* Open input and output files */
    Scanner in = openInputFile();
    ObjectOutputStream out = openOutputFile();
		
   /* STEP 1
    * 
    * Create the linked list of valid ids.
    * 
    * IDs are valid if they are:
    * 1. In the main namespace
    * 2. Not redirect pages
    * 
    */
    LinkedList<Integer> ll = new LinkedList<Integer>();
		
    String str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = in.nextLine();
    }
	    
    int tmp = 0;
    while(tmp < 3 && str != null && !str.trim().equals("")) {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the page information
      String [] arr = str.split("\\d\\),\\(");
      
      for(int i = 0; i < arr.length; i++)
      {
        //System.out.println(arr[i]);
        String [] info = arr[i].split(",");
	    	  
        // Check if the information is in the correct format
        if(info.length >= 11) {
		    	  
          // Extract page, namespace and redirect information
          String page = info[0];
          String namespace = info[1];
          String redirect = info[info.length-6];
		  		          
          // Add the ID if it's in the needed namespace and not a redirect
          if(namespace.equals("0") && redirect.equals("0"))
          {
            ll.add(new Integer(page));
          }
        }//end: if(info.length)
      }//end: for(i)
	      
      str = in.nextLine();
    }//end: while()
	    
   /* STEP 2
    * 
    * Create new integer array of identical length.
    * Copy values into array and sort array.
    * 
    */
    int[] arr = new int[ll.size()];
    Iterator<Integer> it = ll.iterator();
    for(int i = 0; i < arr.length; i++)
    {
      arr[i] = it.next();
    }
    Arrays.sort(arr);
		
   /* STEP 3
    * 
    * Write int [] to object file.
    */
    try
    {
      out.writeObject(arr);
    }
    catch (IOException e)
    {
      System.err.println("Problem writing IDs to file.");
      e.printStackTrace();
    }
    
    //Close files
    try
    {
      in.close();
      out.close();
    }
    catch (IOException e)
    {
      System.err.println("Problem closing input/output files.");
      e.printStackTrace();
    }
  }//end: main(args)
}//end: CreateIDToVertexFile