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
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
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
public class MergeWordMappings {
  
  private static boolean stem;
  
  private static String wordVertexMapFile1, wordVertexMapFile2;
  private static String wordVertexMapFileOut;
  
  private static TermToVertexMapping word2Vertex1, word2Vertex2;
  
  private static String vertexWordMapFile1, vertexWordMapFile2;
  private static String vertexWordMapFileOut;

  private static VertexToTermMapping vertex2Word1, vertex2Word2;
  
  /**
   * Sets the names of:<br>
   * 
   * <ul>
   * <li> {@link AliasStrings} file</li>
   * <li> {@link AliasSFToID} file</li>
   * <li> {@link WikiGraph} file</li>
   * <li> Synonym Task file</li>
   * </ul>
   */
  private static void setFiles()
  {
    
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;

    wordVertexMapFile1 = dir + data + "-title-"+Configuration.stemming + ".tvc";
    wordVertexMapFile2 = dir + data + "-link-"+Configuration.stemming + ".tvc";
    wordVertexMapFileOut = dir + data + "-titlelink-"+Configuration.stemming + ".tvc";
    vertexWordMapFile1 = dir + data + "-title-"+Configuration.stemming + ".vtc";
    vertexWordMapFile2 = dir + data + "-link-"+Configuration.stemming + ".vtc";
    vertexWordMapFileOut = dir + data + "-titlelink-"+Configuration.stemming + ".vtc";
  }
  
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
    
    setFiles();
    
    System.out.println(vertexWordMapFile1);
    System.out.println(vertexWordMapFile2);
    System.out.println(vertexWordMapFileOut);
    
    /* STEP 1:
     * 
     * Word to Vertex Mapping Merge
     */
    System.out.println("Opening Word To Vertex Mapping");
    ObjectInputStream in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile1));
      word2Vertex1 = (TermToVertexMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile1);
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("Opening Word To Vertex Mapping");
    in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile2));
      word2Vertex2 = (TermToVertexMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile2);
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("Merging");
    word2Vertex2.joinMappings(word2Vertex1);
    
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(wordVertexMapFileOut));
    out.writeObject(word2Vertex2);
    out.close();
    
    /* STEP 2:
     * 
     * Vertex to Word Mapping Merge
     */
    System.out.println("Opening Vertex To Word Mapping");
    in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(vertexWordMapFile1));
      vertex2Word1 = (VertexToTermMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + vertexWordMapFile1);
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("Opening Vertex To Word Mapping");
    in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(vertexWordMapFile2));
      vertex2Word2 = (VertexToTermMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + vertexWordMapFile2);
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("Merging");
    vertex2Word2.joinMappings(vertex2Word1);
    
    out = new ObjectOutputStream(new FileOutputStream(vertexWordMapFileOut));
    out.writeObject(vertex2Word2);
    out.close();

    
  }//end: main()
}//end: CreateSimpleWordMapping
