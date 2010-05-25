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

import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.*;
import edu.osu.slate.relatedness.swwr.data.graph.IDIDRedirect;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermCountComparator;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCountComparator;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

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
public class CreateLinkTermMapping {
  
  private static boolean stem;
    
  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateTitleWordMapping.xml");
    }
    
    System.out.println("Creating File: link-" + Configuration.stemming);
    
    System.out.println("Initializing Valid ID List.");
    
    String vidFileName = Configuration.baseDir + "/" + 
                         Configuration.binaryDir + "/" +
                         Configuration.type+ "/" +
                         Configuration.date + "/" +
                         Configuration.type + "-"+ 
                         Configuration.date + "-" + 
                         Configuration.graph + ".vid";
    
    IDVertexTranslation vid = null;
    try
    {
      ObjectInputStream oos = new ObjectInputStream( new FileInputStream(vidFileName));
      vid = (IDVertexTranslation) oos.readObject();
      oos.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file:" + vidFileName);
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("Initializing Redirect List.");
    IDIDRedirect rdl = null;
    String rdlFileName = Configuration.baseDir + "/" +
                         Configuration.binaryDir + "/" + 
                         Configuration.type+ "/" + 
                         Configuration.date + "/" + 
                         Configuration.type + "-"+ 
                         Configuration.date + "-" + 
                         Configuration.graph + ".rdr";
    try
    {
      ObjectInputStream oos = new ObjectInputStream( new FileInputStream(rdlFileName));
      rdl = (IDIDRedirect) oos.readObject();
      oos.close();
    }//end: try{}
    catch(Exception e)
    {
      System.err.println("Problem with file:" + rdlFileName);
      e.printStackTrace();
      System.exit(1);
    }
    
    String csvFile = Configuration.baseDir + "/" + 
                     Configuration.sourceDir + "/" + 
                     Configuration.type+ "/" + 
                     Configuration.date + "/" + 
                     "anchor.csv";
    
    System.out.println("Opening .csv File");
    Scanner in = new Scanner(new FileReader(csvFile));
    
    // Set stem flag
    stem = Configuration.stemming.equals("true");
    
    /* STEP 1
     * 
     * Create the word set in order to know how many terms
     * are in our initial file.
     */
    System.out.println("Creating Term Array");
    TreeSet<String> ts = new TreeSet<String>();
    while(in.hasNext())
    {
      String s = in.nextLine();
      int lastComma = s.lastIndexOf(',');
      int secondLastComma = s.substring(0,lastComma-1).lastIndexOf(',');
      String term = s.substring(1, secondLastComma-1);
      term = term.toLowerCase();

      int count = Integer.parseInt(s.substring(lastComma+1));
      int id = Integer.parseInt(s.substring(secondLastComma+1,lastComma));

      int vertex = -1;
      if(vid.isValidWikiID(id))
      {
        vertex = vid.getVertex(id);
      }
      else if(rdl.isRedirectID(id))
      {
        vertex = vid.getVertex(rdl.redirectIDToValidID(id));
      }
        
      if(vertex >= 0)
      {
        if(stem)
        {
          ts.add(PorterStemmerTokenizerFactory.stem(term));
        }
        else
        {
          ts.add(term);
        }
      }
    }//end: while(in)
    in.close();
    System.out.println(ts.size());
    
    /* STEP 2
     * 
     * Create the TermToVertexCount array of the appropriate size
     * and initialize objects.
     * 
     * We use arrays for space efficiency -- these can get large.
     */
    int valid = 0;
    TermToVertexCount[] terms = new TermToVertexCount[ts.size()];
    Iterator<String> set = ts.iterator();
    int i=0;
    while(set.hasNext())
    {
      terms[i] = new TermToVertexCount(set.next());
      i++;
    }//end: while(set)
    Arrays.sort(terms, new TermToVertexCountComparator());
    ts = null; // Free Up Memory (?)
    
    in = new Scanner(new FileReader(csvFile));
    
    while(in.hasNext())
    {
      String s = in.nextLine();
      int lastComma = s.lastIndexOf(',');
      int secondLastComma = s.substring(0,lastComma-1).lastIndexOf(',');
      String term = s.substring(1, secondLastComma-1);
      term = term.toLowerCase();
      
      int count = Integer.parseInt(s.substring(lastComma+1));
      int id = Integer.parseInt(s.substring(secondLastComma+1,lastComma));

      int vertex = -1;
      if(vid.isValidWikiID(id))
      {
        vertex = vid.getVertex(id);
      }
      else if(rdl.isRedirectID(id))
      {
        vertex = vid.getVertex(rdl.redirectIDToValidID(id));
      }
        
      if(vertex >= 0)
      {
        valid++;
        if(stem)
        {
          term = PorterStemmerTokenizerFactory.stem(term);
        }
        
        int pos = Arrays.binarySearch(terms, new TermToVertexCount(term), new TermToVertexCountComparator());
        
        if(pos >= 0)
        {
          terms[pos].addVertex(vertex, count);
        }
        else
        {
          System.err.println("Invalid Term Found: "+ term);
        }
      }//end: if(vertex)
    }//end: while(in)
    
    /* STEP 4
     * 
     * Write objects to the .tvc file
     */
     System.out.println("Writing Mappings To File");
     TermToVertexMapping tvm = new TermToVertexMapping(terms);
     ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                              Configuration.baseDir + "/" +
                              Configuration.binaryDir + "/" +
                              Configuration.type+ "/" +
                              Configuration.date + "/" +
                              Configuration.type + "-"+
                              Configuration.date + "-" +
                              Configuration.graph + "-" +
                              "link-" +
                              Configuration.stemming + ".tvc"));
     
     out.writeObject(tvm);
     out.close();
     terms = null; // Free Up Memory (?)
     
     /* STEP 5
      * 
      * Create the vertex set in order to know how many verticies
      * are in our initial file.
      */
     in = new Scanner(new FileReader(csvFile));
     System.out.println("Creating Integer Array");
     TreeSet<Integer> vertexSet = new TreeSet<Integer>();
     while(in.hasNext())
     {
       String s = in.nextLine();
       int lastComma = s.lastIndexOf(',');
       int secondLastComma = s.substring(0,lastComma-1).lastIndexOf(',');
       String term = s.substring(1, secondLastComma-1);
       
       int count = Integer.parseInt(s.substring(lastComma+1));
       int id = Integer.parseInt(s.substring(secondLastComma+1,lastComma));
       
       int vertex = -1;
       if(vid.isValidWikiID(id))
       {
         vertex = vid.getVertex(id);
       }
       else if(rdl.isRedirectID(id))
       {
         vertex = vid.getVertex(rdl.redirectIDToValidID(id));
       }
         
       if(vertex >= 0)
       {
         vertexSet.add(vertex);
       }
     }
    /* STEP 6
     * 
     * Create the VertexToTermCount array of the appropriate size
     * and initialize objects.
     * 
     * We use arrays for space efficiency -- these can get large.
     */ 
     VertexToTermCount[] verticies = new VertexToTermCount[vertexSet.size()];
     Iterator<Integer> it = vertexSet.iterator();
     i=0;
     while(it.hasNext())
     {
       verticies[i] = new VertexToTermCount(it.next());
       i++;
     }//end: while(set)
     Arrays.sort(verticies, new VertexToTermCountComparator());
     vertexSet = null; // Free Up Memory (?)
     in.close();
     
    /* STEP 7
     *  
     * Add terms to our VertexToTermCount objects.
     */
     System.out.println("Creating Term Mappings");
     in = new Scanner(new FileReader(csvFile));
     while(in.hasNext())
     {
       String s = in.nextLine();
       int lastComma = s.lastIndexOf(',');
       int secondLastComma = s.substring(0,lastComma-1).lastIndexOf(',');
       String term = s.substring(1, secondLastComma-1);
       term = term.toLowerCase();
       
       int count = Integer.parseInt(s.substring(lastComma+1));
       int id = Integer.parseInt(s.substring(secondLastComma+1,lastComma));
       
       int vertex = -1;
       if(vid.isValidWikiID(id))
       {
         vertex = vid.getVertex(id);
       }
       else if(rdl.isRedirectID(id))
       {
         vertex = vid.getVertex(rdl.redirectIDToValidID(id));
       }
       
       if(vertex >= 0)
       {
         int pos = Arrays.binarySearch(verticies, new VertexToTermCount(vertex), new VertexToTermCountComparator());
         if(pos >= 0)
         {
           verticies[pos].addTerm(term, count);
         }
         else
         {
           System.err.println("Invalid vertex Found: "+ vertex);
         }
       }
     }//end: while(true)
     
     in.close();
     
     /* STEP 8
      * 
      * Write objects to the .vtc file
      */
     System.out.println("Writing Mappings to File");
     VertexToTermMapping vwm = new VertexToTermMapping(verticies);
     out = new ObjectOutputStream(new FileOutputStream(
           Configuration.baseDir + "/" +
           Configuration.binaryDir + "/" +
           Configuration.type+ "/" +
           Configuration.date + "/" +
           Configuration.type + "-"+
           Configuration.date + "-" +
           Configuration.graph + "-" +
           "link-" + 
           Configuration.stemming + ".vtc"));
     
     out.writeObject(vwm);
     out.close();
     verticies = null; // Free Up Memory (?)
     
  }//end: main()
}//end: CreateSimpleWordMapping
