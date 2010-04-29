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

package edu.osu.slate.relatedness.swwr.setup.wordmapping;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import edu.osu.slate.relatedness.swwr.data.*;

/**
 * Creates a simple word-to-vertex mapping for the Wiki graph using the Page title.
 * 
 * Requires initialized {@link ValidIDs} and {@link RedirectList} classes.  All output text is lowercase.
 * 
 * Configuration File Requirements
 * <ul>
 * <li><b>basedir</b> -- base directory for the files </li>
 * <li><b>sourcedir</b> -- raw sql/xml data file directory (default: source)</li>
 * <li><b>binarydir</b> -- generated binary file directory (default: binary)</li>
 * <li><b>tempdir</b> -- directory to store temporary files (default: tmp)</li>
 * <li><b>type</b> -- type of wiki to read (enwiki or enwiktionary)</li>
 * <li><b>date</b> -- date of wiki dump</li>
 * <li><b>graph</b> -- graph source information</li>
 * </ul>
 * 
 * The output of this program is a .titlewordmap file placed in the temp directory.  It will be used as an input file for the {@link CreateMappings} program.
 * 
 * @author weale
 *
 */
public class CreateTitleWordMapping {

  private static String baseDir, sourceDir, binaryDir, tempDir;
  private static String type, date, graph;

 /**
  * Parse the configuration file.
  *  
  * @param filename Configuration file name.
  */
  private static void parseConfigurationFile(String filename) {
    try {
      Scanner config = new Scanner(new FileReader(filename));
      sourceDir = "source";
      binaryDir = "binary";
      tempDir = "tmp";
      while(config.hasNext()) {
        
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
    catch (IOException e)
    {
      System.err.println("Problem reading from file: " + filename);
      System.exit(1);
    }
  }//end: parseConfigurationFile(String)
  
  /**
   * Creates a (word, ID) pairs from the titles of a wiki data source.
   * 
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    
      
    parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateTitleWordMapping.xml");
    
    System.out.println("Initializing Valid ID List.");
    ValidIDs vid = new ValidIDs(baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + graph + ".vid");
    
    System.out.println("Initializing Redirect List.");
    RedirectList rdl = new RedirectList(baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + graph + ".rdr");
    
    System.out.println("Opening page.sql File");
    Scanner in = new Scanner(new FileReader(baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + "page.sql"));
    
    System.out.println("Opening .titlewordmap File for Writing");
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(baseDir + "/" + tempDir + "/" + type + "-"+ date + "-" + graph + ".titlewordmap"));
    
   /* STEP 1
    * 
    * Create the List of Surface Forms.
    */
    System.out.println("Writing Page Titles and Vertex IDs to File");
    Pattern p = Pattern.compile("\\([\\p{Graph}\\p{Blank}]+\\)");

    // Strip beginning lines from file
    String str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1) {
      str = in.nextLine();
    }
    
    int tmp = 0;
    while(tmp < 3 && str != null && !str.trim().equals("")) {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the individual page information
      String [] arr = str.split("\\d\\),\\(");
      for(int i=0;i<arr.length;i++) {
        
        String [] info = arr[i].split(",");
          
        // Information is in the correct format if the length == 11
        if(info.length >= 11)
        {
          // Extract page, namespace and redirect information
          String page = info[0];
          String namespace = info[1];
          String redirect = info[info.length-6];
              
          // Extract title information
          String title = info[2];
          for(int j=3; j<info.length-8;j++)
          {
            title = title + "," + info[j];
          }
          title = title.substring(1,title.length()-1).replace('_', ' ').toLowerCase();
          int pageID = Integer.parseInt(page);
              
          // Add the ID if it's in the needed namespace and not a redirect
          if(namespace.equals("0") && redirect.equals("0") && vid.isValidID(pageID))
          {
            title = addAmbiguity(title, p);
            out.writeObject(title);
            out.writeInt(pageID);
          }//end: if()
              
          // Add the ID after redirect
          else if(namespace.equals("0") && redirect.equals("1") &&
                  rdl.isRedirectID(pageID))
          {
            title = addAmbiguity(title, p);
            pageID = rdl.redirect(pageID);
            out.writeObject(title);
            out.writeInt(pageID);
          }//end: else if()
              
        }//end: if(info.length)
      }//end: for(i)
      
      str = in.nextLine();
    }//end: while()
    out.close();
    
  }//end: main()
  
 /**
  * Strips disambiguating information from a Page Title.
  * 
  * This is done in order to maximize the number of potential Page matches to a given word.
  *  
  * @param title Page Title
  * @return Potentially ambiguous Page Title
  */
  private static String addAmbiguity(String title, Pattern p) {
    boolean b = Pattern.matches(".+\\([\\p{Graph}\\p{Blank}]+\\)", title);
    
    // Remove escape characters in title
    title = title.replace("\\", "");

    if(b)
    {
      // Disambiguating section found. Strip it out.
      try
      {
        title = p.split(title)[0].trim();
      }//end: try {}
      catch(Exception e) {
        /* Problems with multiple ( ) expressions.
         * Minor hack to deal with most issues.
         * Further refinements would yield minimal improvement.
         */
        if(title.contains("film)") || title.contains("song)") ||
           title.contains("album)") || title.contains("train)") ||
           title.contains("band)") || title.contains("movie)"))
        {
          title = title.substring(0,title.lastIndexOf("("));
        }
      }//end: catch{}
    }//end: if(b)
    
    return title;
  }//end: addAmbiguity()
}//end: CreateSimpleWordMapping
