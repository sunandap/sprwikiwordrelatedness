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
import edu.osu.slate.relatedness.swwr.data.mapping.*;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

/**
 * Turns temporary (String, ID) file into Word-ID and ID-Word Mappings.
 * <p>
 * Requires the output of {@link CreateTitleWordMapping} or {@link CreateLinkTermMapping}.  It also requires that <a href="http://alias-i.com/lingpipe/">LingPipe</a> be installed and the jar file accessible to the java environment.
 * <p>
 * If desired, stemming for more robust mapping functions may be applied to the words here.
 * <p>
 * Configuration File Requirements:
 * <ul>
 * <li><b>basedir</b> -- base directory for the files </li>
 * <li><b>sourcedir</b> -- raw sql/xml data file directory (default: source)</li>
 * <li><b>binarydir</b> -- generated binary file directory (default: binary)</li>
 * <li><b>tempdir</b> -- directory to store temporary files (default: tmp)</li>
 * <li><b>type</b> -- type of wiki to read (enwiki or enwiktionary)</li>
 * <li><b>date</b> -- date of wiki dump</li>
 * <li><b>graph</b> -- graph source information</li>
 * <li><b>stem</b> -- Allow Porter Stemming? (default: false)</li>
 * </ul>
 * 
 * The output of this program is a <i>.wic file</i> and an <i>.iwc file</i> placed in the binary directory.  These files will be used a input files for the {@link VertexToTermMapping} and {@link TermToVertexMapping} classes.
 * 
 * @author weale
 *
 */
public class CreateMappings {

//  private static String baseDir, sourceDir, binaryDir, tempDir;
//  private static String type, date, graph, stemChar;
  private static boolean stem;
  
  /**
   * Creates a final word-to-vertex mapping from the (word, vertex) pairs from a wiki data source.
   *
   * @param args
   * @throws IOException 
   * @throws FileNotFoundException 
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
    
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/CreateMappings.xml");
    }
    
    System.out.println("Opening Temporary File for Reading");
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                           Configuration.baseDir + "/" +
                           Configuration.tempDir + "/" +
                           Configuration.type + "-"+
                           Configuration.date + "-" +
                           Configuration.graph + ".titlewordmap"));
    stem = Configuration.stemming.equals("true");
    
    System.out.println("Creating Mappings:" + Configuration.mapsource + "-" + Configuration.stemming);
    
   /* STEP 1
    * 
    * Create the word set in order to know how many words
    * are in our initial file.
    */
    System.out.println("Creating Word Array");
    TreeSet<String> ts = new TreeSet<String>();
    try
    {
      while(true)
      {
        String s = (String) in.readObject();
        if(stem)
        {
          ts.add(PorterStemmerTokenizerFactory.stem(s));
        }
        else
        {
          ts.add(s);
        }
        
        in.readInt(); //throw away vertex values
      }//end: while(true)
    }//end: try{} 
    catch(IOException e) {} //EOF found
    
   /* STEP 2
    * 
    * Create the WordToIDCount array of the appropriate size
    * and initialize objects.
    * 
    * We use arrays for space efficiency -- these can get large.
    */
    TermToVertexCount[] words = new TermToVertexCount[ts.size()];
    Iterator<String> set = ts.iterator();
    int i=0;
    while(set.hasNext())
    {
      words[i] = new TermToVertexCount(set.next());
      i++;
    }//end: while(set)
    Arrays.sort(words, new TermToVertexCountComparator());
    ts = null; // Free Up Memory (?)
    in.close();
    
   /* STEP 3
    *  
    * Add IDs to our WordToIDCount objects.
    */
    System.out.println("Creating Vertex Mappings");
    in = new ObjectInputStream(new FileInputStream(
        Configuration.baseDir + "/" +
        Configuration.tempDir + "/" +
        Configuration.type + "-"+
        Configuration.date + "-" +
        Configuration.graph + ".titlewordmap"));
    
    try
    {
      while(true)
      {
        // Read word and vertex pair
        String term = (String) in.readObject();
        int vertex = in.readInt();
        
        if(stem)
        {
          term = PorterStemmerTokenizerFactory.stem(term);
        }
        
        if(vertex < 0)
        {
          System.err.println("PROBLEM: " + term);
        }
                
        int pos = Arrays.binarySearch(words, new TermToVertexCount(term), new TermToVertexCountComparator());
        if(pos >= 0)
        {
          words[pos].addVertex(vertex);
        }
        else
        {
          System.err.println("Invalid Word Found: "+ term);
        }
      }//end: while(true)
    }//end: try {}
    catch(IOException e) {} //EOF found
    
    in.close();
    
   /* STEP 4
    * 
    * Write objects to the .wvc file
    */
    System.out.println("Writing Mappings To File");
    TermToVertexMapping wvm = new TermToVertexMapping(words);
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                             Configuration.baseDir + "/" +
                             Configuration.binaryDir + "/" +
                             Configuration.type+ "/" +
                             Configuration.date + "/" +
                             Configuration.type + "-"+
                             Configuration.date + "-" +
                             Configuration.graph + "-" +
                             Configuration.mapsource + "-" +
                             Configuration.stemming + 
                             ".tvc"));
    
    out.writeObject(wvm);
    out.close();
    words = null; // Free Up Memory (?)
    
    /* STEP 5
     * 
     * Create the vertex set in order to know how many verticies
     * are in our initial file.
     */
    in = new ObjectInputStream(new FileInputStream(
                               Configuration.baseDir + "/" + 
                               Configuration.tempDir + "/" + 
                               Configuration.type + "-"+ 
                               Configuration.date + "-" + 
                               Configuration.graph + 
                               ".titlewordmap"));

    System.out.println("Creating Integer Array");
    TreeSet<Integer> vertexSet = new TreeSet<Integer>();
    try
    {
      while(true)
      {
        in.readObject();
        vertexSet.add(in.readInt());
      }//end: while(true)
    }//end: try {}
    catch(IOException e) {} //EOF found

   /* STEP 6
    * 
    * Create the VertexToWordCount array of the appropriate size
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
    * Add words to our IDToWordCount objects.
    */
    System.out.println("Creating Word Mappings");
    in = new ObjectInputStream(new FileInputStream(
                               Configuration.baseDir + "/" + 
                               Configuration.tempDir + "/" + 
                               Configuration.type + "-"+ 
                               Configuration.date + "-" + 
                               Configuration.graph + ".titlewordmap"));
    try {
      while(true)
      {
        String word = (String) in.readObject();
        int vertex = in.readInt();
        
        if(stem)
        {
          word = PorterStemmerTokenizerFactory.stem(word);
        }
        
        int pos = Arrays.binarySearch(verticies, new VertexToTermCount(vertex), new VertexToTermCountComparator());
        if(pos >= 0)
        {
          verticies[pos].addTerm(word);
        }
        else
        {
          System.err.println("Invalid ID Found: "+ vertex);
        }
      }//end: while(true)
    } //end: try {}
    catch(IOException e) {} //EOF found
    
    in.close();
    
    /* STEP 8
     * 
     * Write objects to the .vwc file
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
          Configuration.mapsource + "-" +
          Configuration.stemming + ".vtc"));
    
    out.writeObject(vwm);
    out.close();
    verticies = null; // Free Up Memory (?)
    
  }//end: main()

}
