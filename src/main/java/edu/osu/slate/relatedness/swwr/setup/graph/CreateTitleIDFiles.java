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

import edu.osu.slate.relatedness.Configuration;

import edu.osu.slate.relatedness.swwr.data.ConvertIDToTitle;
import edu.osu.slate.relatedness.swwr.data.ConvertTitleToID;
import edu.osu.slate.relatedness.swwr.data.TitleID;

/**
 * Creates Title-to-ID translation classes for main graph nodes.
 * <p>
 * Creates the .tid file for determination.<br>
 * This is a file of ID->Titles and Titles->ID.<br>
 * Titles are saved without beginning and trailing quotes.
 * <p>
 * Output .tid file contains two binary classes:
 * <ul>
 * <li>{@link ConvertTitleToID}</li>
 * <li>{@link ConvertIDToTitle}</li>
 * </ul>
 * <p>
 * In the Graph creation pipeline, this program is:
 * <ul>
 *   <li>Preceded by -none-
 *   <li>Followed by {@link CreateRedirectFiles}
 * </ul>
 * 
 * @author weale
 *
 */
public class CreateTitleIDFiles {

  /* Name of the input file created by Wikipedia (page.sql) */
  private static String inputFileName;

  /* Name of the output file (.cid) */
  private static String outputFileName;

 /**
  * Checks and opens the input file.
  * 
  * @return Scanner for reading the input file
  */
  private static Scanner openInputFile()
  {
    inputFileName = Configuration.baseDir + "/" +
                    Configuration.sourceDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/" +
                    Configuration.type + "-" + Configuration.date +
                    "-page.sql";
    try
    {
      Scanner in = new Scanner(new FileReader(inputFileName));
      return in;		
    }
    catch (FileNotFoundException e)
    {
      System.out.println("File not found: " + inputFileName);
      e.printStackTrace();
    }

    System.exit(1);
    return null;	
  }

  /**
   * Runs the program.
   * 
   * @param args Command-line parameters
   * @throws ClassNotFoundException 
   * @throws IOException 
   * 
   */
  public static void main(String[] args) {
    
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/CreateMappings.xml");
    }
    
    /* STEP: 1
     * Find the number of categories.
     */
    System.out.println("Finding Number of Titles");
    int numTitles = 0;

    /* Open input file */
    Scanner in = openInputFile();
    
    /* Burn intro lines */
    String str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = in.nextLine();
    }

    while(str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the page information
      String [] arr = str.split("\\d\\),\\(");
      for(int i = 0; i < arr.length; i++)
      {
        String [] info = arr[i].split(",");

        // Check if the information is in the correct format
        if(info.length >= 11)
        {

          // Extract namespace  information
          String namespace = info[1];

          // Add the ID if it's in the needed namespace
          if(namespace.equals("0"))
          {
            numTitles++;
          }
        }//end: if(info.length)
      }//end: for(i)
      
      str = in.nextLine();
    }//end: while()
    in.close();

    /* STEP: 2
     * 
     * Create TitleID array
     */
    System.out.println("Creating Title/ID Array");
    TitleID[] tids = new TitleID[numTitles];

    /* STEP: 3
     * 
     * Fill TitleID[].
     */
    in = openInputFile();
    
    /* Burn intro lines */
    str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = in.nextLine();
    }

    System.out.println("Filling Title/ID Array");
    int currTitleID = 0;

    while(str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the page information
      String [] arr = str.split("\\d\\),\\(");
      for(int i = 0; i < arr.length; i++)
      {
        String [] info = arr[i].split(",");

        // Check if the information is in the correct format
        if(info.length >= 11)
        {

          // Extract page and namespace
          String page = info[0];
          String namespace = info[1];

          // Add the ID if it's in the needed namespace
          if(namespace.equals("0"))
          {
            
            // Get full title information
            String title = info[2];
            for(int j = 3; j < info.length - 8; j++ )
            {
              title = title + "," + info[j]; 
            }//end: for(j)
            
            title = title.substring(1, title.length()-1);
            
            /* Add TitleID to array */
            TitleID tid = new TitleID(title, Integer.parseInt(page));
            tids[currTitleID] = tid;
            currTitleID++;
          }//end: if(namespace == 0)
        }//end: if(info.length)
      }//end: for(i)

      str = in.nextLine();
    }//end: while()
    in.close();

    /* STEP: 4
     * 
     * Create category-id classes.
     */
    System.out.println("Num Titles: " + tids.length);

    System.out.println("Creating .tid classes");
    ConvertTitleToID Title2ID = new ConvertTitleToID(tids);
    ConvertIDToTitle ID2Title = new ConvertIDToTitle(tids);
    
    /* STEP: 5
     * 
     * Write .cid file.
     */
    System.out.println("Writing .tid file");
    outputFileName = Configuration.baseDir + "/" +
                     Configuration.binaryDir + "/" +
                     Configuration.type+ "/" +
                     Configuration.date+ "/" +
                     Configuration.type+ "-" +
                     Configuration.date + "-" +
                     Configuration.graph + ".tid";
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFileName));
      out.writeObject(Title2ID);
      out.writeObject(ID2Title);
      out.close();
    }
    catch (FileNotFoundException e)
    {
      System.out.println("File not found: " + outputFileName);
      e.printStackTrace();
    }
    catch (IOException e1)
    {
      System.err.println("Problem creating output file.");
      e1.printStackTrace();
    }

  }//end: main(args)
}
