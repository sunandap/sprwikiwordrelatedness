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
import edu.osu.slate.relatedness.swwr.data.category.CategoryIDGraph;
import edu.osu.slate.relatedness.swwr.data.category.CategoryTitleToIDTranslation;
import edu.osu.slate.relatedness.swwr.data.category.IDToCategoryTitleTranslation;

/**
 * Program to create the category tree.
 * 
 * <ul>
 *   <li> Preceded by {@link CreateCategoryList} </li>
 *   <li> Followed by {@link CreateInboundEdgeCounts} </li>
 * </ul>
 * 
 * @author weale
 */
public class CreateCategoryGraph {

  /* Name of the input file generated by Wiki (page.sql) */
  private static String inputFileName;

  /* Name of the output file (.vid) */
  private static String outputFileName;

  /* Category file name (.cid) */
  private static String catFileName;
  
  /* Translates category titles to category IDs */
  private static CategoryTitleToIDTranslation Cat2ID;
  
  /* Translates category IDs to category titles */
  private static IDToCategoryTitleTranslation ID2Cat;
  
  /**
   * Runs the program.
   * 
   * @param args Name of the configuration file (if needed)
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws ClassNotFoundException
  {
    //Read Configuration File
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateMappings.xml");
    }
    
    /* Set file names */
    catFileName = Configuration.baseDir + "/" +
                  Configuration.sourceDir + "/" +
                  Configuration.type+ "/" +
                  Configuration.date+ "/" +
                  Configuration.type+ "-" + Configuration.date +
                  "-categorylinks.sql";
    
    inputFileName = Configuration.baseDir + "/" +
                    Configuration.binaryDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/" +
                    Configuration.type+ "-" + Configuration.date +
                    "-page.cid";

    /* Open .cid file */
    ObjectInputStream in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(inputFileName));		
      System.out.println("Opening .cid file.");
      Cat2ID = (CategoryTitleToIDTranslation)in.readObject();
      ID2Cat = (IDToCategoryTitleTranslation)in.readObject();
      in.close();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file: " + inputFileName);
      System.exit(1);
    }
    
    /* Set Category Root */
    String seed = "Fundamental";
    
    if( !Cat2ID.isLookupCategory(seed) )
    {
      System.err.println("Invalid root category: " + seed);
      System.exit(1);
    }
    String str;
    int tmp;

    //Seed the category graph
    CategoryIDGraph categoryGraph = new CategoryIDGraph(Cat2ID.getID(seed));

   /* STEP 1:
    * 
    * Add all CatGraph nodes
    */
    System.out.println("Adding Graph Nodes");
    int[] catIDs = Cat2ID.getCategoryIDs();
    categoryGraph.addNodes(catIDs);

   /* STEP 2:
    * 
    * Add all edges to the category graph.
    */
    System.out.println("Adding Edges");
    Scanner categoryLinks = null;
    try
    {
      categoryLinks = new Scanner(new FileReader(catFileName));
    }
    catch(IOException e)
    {
      System.err.println("Problem reading from file: " + catFileName);
      System.exit(1);
    }
    
    str = categoryLinks.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = categoryLinks.nextLine();
    }

    tmp = 0;
    while(tmp < 3 && str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the page information
      String [] arr = str.split("\\d\\),\\(");
      for(int i = 0; i < arr.length;i ++)
      {
        //System.out.println(arr[i]);
        String [] info = arr[i].split(",");

        // Check if the information is in the correct format
        if(info.length >= 4)
        {

          // Extract FROM Page ID
          String childPageID = info[0];

          // Extract TO Category Title
          int breakSpot = -1;
          String categoryTitle = info[1];
          for(int j=2; breakSpot < 0 && j<info.length; j++)
          {
            if(info[j-1].charAt(info[j-1].length()-1) == '\'' &&
                info[j].charAt(0) == '\'')
            {
              breakSpot = j;
            }
            else
            {
              categoryTitle = categoryTitle + "," + info[j];
            }
          }//end: for(j)
          categoryTitle = categoryTitle.substring(1, categoryTitle.length()-1);
          
          /* Add the edge to the category graph if:
           *  -- CHILD Page ID is a valid category page ID
           *  -- PARENT Page ID is a valid category page ID
           */
          boolean childIsCategory = ID2Cat.isLookupID(Integer.parseInt(childPageID));
          boolean parentIsCategory = Cat2ID.isLookupCategory(categoryTitle);
          if(childIsCategory && parentIsCategory)
          {
            // Get the "TO" Category ID
            int parentID = Cat2ID.getID(categoryTitle);

            // Get all "FROM" IDs
            int childID = Integer.parseInt(childPageID);
            
            categoryGraph.addEdge(parentID, childID, ID2Cat);
          }//end: if()
        }//end: if(info.length)
      }//end: for(i)
      
      str = categoryLinks.nextLine();
    }//end: while()    
    categoryLinks.close();
    
    //Write Initial .cgraph file
    System.out.println("Writing .cgraph file.");
    outputFileName = Configuration.baseDir + "/" +
                     Configuration.binaryDir + "/" +
                     Configuration.type + "/" +
                     Configuration.date + "/" +
                     Configuration.type + "-" + Configuration.date +
                     "-" + Configuration.graph + ".cgraph";
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputFileName));
      out.writeObject(categoryGraph);
      out.close();
    }//end: try {}
    catch(IOException e)
    {
      System.out.println("Problem writing file: " + outputFileName);
      System.exit(1);
    }//end: catch(IOException)
    
  }//end: main()
}
