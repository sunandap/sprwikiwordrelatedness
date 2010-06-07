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

package edu.osu.slate.relatedness.swwr.setup.category;

import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.Configuration;

import edu.osu.slate.relatedness.swwr.data.TitleID;
import edu.osu.slate.relatedness.swwr.data.category.CategoryTitleToIDTranslation;
import edu.osu.slate.relatedness.swwr.data.category.IDToCategoryTitleTranslation;

/**
 * Creates Category-to-ID translation classes.
 * <p>
 * Creates the .cid file for determination.<br>
 * This is a file of ID->Titles and Titles->ID.<br>
 * Titles are saved without beginning and trailing quotes.
 * <p>
 * Output .cid file contains two binary classes:
 * <ul>
 * <li>{@link CategoryTitleToIDTranslation}</li>
 * <li>{@link IDToCategoryTitleTranslation}</li>
 * </ul>
 * <p>
 * In the Category Tree creation pipeline, this program is:
 * <ul>
 *   <li>Preceded by -none-
 *   <li>Followed by {@link CreateCategoryAcyclicGraph}
 * </ul>
 * 
 * @author weale
 *
 */
public class CreateCategoryList {

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
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateMappings.xml");
    }
    
    /* STEP: 1
     * Find the number of categories.
     */
    System.out.println("Finding Number of Categories");
    int numCats = 0;

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

          // Extract namespace and redirect information
          String namespace = info[1];
          String redirect = info[info.length-6];

          // Add the ID if it's in the needed namespace and not a redirect
          if(namespace.equals("14") && redirect.equals("0"))
          {
            numCats++;
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
    System.out.println("Creating Category Title/ID Array");
    TitleID[] tids = new TitleID[numCats];

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

    System.out.println("Filling Category Title/ID Array");
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

          // Extract page, namespace and redirect information
          String page = info[0];
          String namespace = info[1];
          String redirect = info[info.length-6];

          // Add the ID if it's in the needed namespace and not a redirect
          if(namespace.equals("14") && redirect.equals("0"))
          {
            
            // Get full title information
            String title = info[2];
            for(int j = 3; j < info.length - 8; j++ )
            {
              title = title + "," + info[j]; 
            }//end: for(j)
            
            title = title.substring(1, title.length()-1);
            
            /* Add TitleID to array */
            TitleID tid = new TitleID(title,Integer.parseInt(page));
            tids[currTitleID] = tid;
            currTitleID++;
          }//end: if(ns && !redirect)
        }//end: if(info.length)
      }//end: for(i)

      str = in.nextLine();
    }//end: while()
    in.close();

    /* STEP: 4
     * 
     * Create category-id classes.
     */
    System.out.println("Num Cats: " + tids.length);

    System.out.println("Creating .cid classes");
    CategoryTitleToIDTranslation Cat2ID = new CategoryTitleToIDTranslation(tids);
    IDToCategoryTitleTranslation ID2Cat = new IDToCategoryTitleTranslation(tids);

    int tmp = Cat2ID.getID("Fundamental");
    System.out.println(tmp);
    String[] arrs = ID2Cat.getTitle(tmp);
    for(int i=0; i<arrs.length; i++)
    {
      System.out.println(arrs[i]);
    }
    
    /* STEP: 5
     * 
     * Write .cid file.
     */
    System.out.println("Writing .cid file");
    outputFileName = Configuration.baseDir + "/" +
                     Configuration.binaryDir + "/" +
                     Configuration.type+ "/" +
                     Configuration.date+ "/" +
                     Configuration.type+ "-" + Configuration.date +
                     "-page.cid";
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFileName));
      out.writeObject(Cat2ID);
      out.writeObject(ID2Cat);
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
