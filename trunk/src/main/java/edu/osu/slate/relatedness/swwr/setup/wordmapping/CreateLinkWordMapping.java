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
import edu.osu.slate.relatedness.swwr.data.graph.IDIDRedirect;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;

/**
 * Creates a more complicated word-to-vertex mapping for the Wiki graph using the Page-Page link text from the xml file.
 * 
 * Requires initialized {@link IDVertexTranslation}, {@link IDIDRedirect} and {@link ConvertTitleToID} classes.
 * 
 * Configuration File Requirements:
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
 * The output of this program is a .linkwordmap file placed in the temp directory.  It will be used as an input file for the {@link CreateMappings} program.
 * 
 * @author weale
 *
 */
public class CreateLinkWordMapping {
  
  private static String baseDir, sourceDir, binaryDir, tempDir;
  private static String type, date, graph;
  
  private static void parseConfigurationFile(String filename) {
    try {
      Scanner config = new Scanner(new FileReader(filename));
      sourceDir = "source";
      binaryDir = "binary";
      tempDir = "tmp";
      while(config.hasNext()) {
        String s = config.nextLine();
        if(s.contains("<basedir>")) {
          baseDir = s.substring(s.indexOf("<basedir>") + 9, s.indexOf("</basedir>"));
        }
        else if(s.contains("<sourcedir>")) {
          sourceDir = s.substring(s.indexOf("<sourcedir>") + 11, s.indexOf("</sourcedir>"));          
        }
        else if(s.contains("<binarydir>")) {
          binaryDir = s.substring(s.indexOf("<binarydir>") + 11, s.indexOf("</binarydir>"));
        }
        else if(s.contains("<tempdir>")) {
          tempDir = s.substring(s.indexOf("<tempdir>") + 9, s.indexOf("</tempdir>"));
        }
        else if(s.contains("<type>")) {
          type = s.substring(s.indexOf("<type>") + 6, s.indexOf("</type>"));
        }
        else if(s.contains("<date>")) {
          date = s.substring(s.indexOf("<date>") + 6, s.indexOf("</date>"));
        }
        else if(s.contains("<graph>")) {
          graph = s.substring(s.indexOf("<graph>") + 7, s.indexOf("</graph>"));
        }
      }
    }
    catch (IOException e) {
      System.err.println("Problem reading from file: " + filename);
      System.exit(1);
    }
  }
  
  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    
    parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateTitleWordMapping.xml");
    
    System.out.println("Initializing Valid ID List.");
    IDVertexTranslation vid = new IDVertexTranslation(baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + graph + ".vid");
    
    System.out.println("Initializing Redirect List.");
    IDIDRedirect rdl = new IDIDRedirect(baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + graph + ".rdr");
    
    System.out.println("Opening .xml File");
    Scanner in = new Scanner(new FileReader(baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/" + type + "-"+ "20080312" + "-" + "pages-articles.xml"));
    
    System.out.println("Opening .linkwordmap File for Writing");
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(baseDir + "/" + tempDir + "/" + type + "-"+ date + "-" + graph + ".linkwordmap"));
    
   /* STEP 1
    * 
    * Create the List of Surface Forms.
    */
    System.out.println("Writing Link Text and Vertex IDs to File");
    
    String s = "";
    
  // Break Label
  linksearch:
    while(in.hasNext()) {
      StringBuffer sb = new StringBuffer();
      s = in.nextLine();
      
      //Find the next page text
      while(!s.contains("<text")) {
        s = in.nextLine();
        if(s.contains("</mediawiki>")) {
          break linksearch;
        }
      }//end: while("<text")
      
      // Set text to initial string of page text
      sb.append(s.substring(s.indexOf("<text")+27));
      
      // Append to the end until closing tag is found
      while(!s.contains("</text")) {
        s = in.nextLine();
        sb.append(s);
      }//end: while("</text")
      
      String text = sb.substring(0,sb.length()-7);
      //System.out.println(text.substring(0, Math.min(30, text.length())));
    }//end: while(in) [linksearch]
    out.close();
    
  }//end: main()
}//end: CreateSimpleWordMapping
