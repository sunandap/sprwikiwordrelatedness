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

package edu.osu.slate.experiments.synonym;

import java.util.*;
import java.io.*;

import lemurproject.lemur.Index;
import lemurproject.lemur.IndexManager;
import lemurproject.lemur.TermInfo;
import lemurproject.lemur.TermInfoList;

import edu.osu.slate.experiments.Common;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.*;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.TermCount;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;
import edu.osu.slate.relatedness.swwr.setup.textcompare.CompareTextVectors;

/**
 * Runs the standard PageRank-inspired relatedness metric on the given source.
 * <p>
 * Input arguments:
 * <ul>
 * <li>type (required): enwiki or enwiktionary</li>
 * <li>date (required)</li>
 * <li>source (required): M or english_language (wiktionary)</li>
 * <li>seed (optional): name of top-level category for category graph</li>
 * <li>countSource (optional): v or e</li>
 * <li>algorithm (optional)</li>
 * <li>task (required): name of the synonym task (RDWP300, TOEFL, ESL)</li>
 * </ul>
 * <p>
 * Uses the following classes:<br>
 * <ul>
 * <li> {@link AliasStrings}</li>
 * <li> {@link AliasSFToID}</li>
 * <li> {@link WikiGraph}</li>
 * </ul>
 * @author weale
 *
 */
public class TextVectorCompare {

  private static String vertexWordMapFile;
  private static String wordVertexMapFile;
  private static String taskFile;
  private static VertexToTermMapping vertex2Word;
  private static TermToVertexMapping word2Vertex;
  private static String indexLocation;
  
  private static float[] IDF;
  private static float[] docLength;
  
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
  private static void setFiles() {
    
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;

    indexLocation = Configuration.baseDir   + "/" + 
                    Configuration.indexDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/";
    
    vertexWordMapFile = dir + data + "-f.vwc";
    wordVertexMapFile = dir + data + "-f.wvc";
    taskFile = Configuration.taskDir + Configuration.task + ".txt";
  }
  
  /**
   * Main portion of the program
   * <p>
   * If no arguments are provided, uses defaults hard-coded by user.
   * 
   * @param args 0, 4 or 7 arguments accepted
   * @throws IOException General IO errors
   * @throws ClassNotFoundException Problems with the {@link WikiGraph} file
   */
  public static void main(String [] args) throws IOException, ClassNotFoundException {
    
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
    }
    
    setFiles();

    boolean report = true;

    System.out.println("Setting Synonym Task: " + Configuration.task);
    Scanner s = new Scanner(new FileReader(taskFile));
    ObjectInputStream in = null;
    
    try
    {
      System.out.println("Opening Word To Vertex Mapping");
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      word2Vertex = (TermToVertexMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      System.exit(1);
    }
    
    try
    {
      System.out.println("Opening Vertex To Word Mapping");
      in = new ObjectInputStream(new FileInputStream(vertexWordMapFile));
      vertex2Word = (VertexToTermMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + vertexWordMapFile);
      System.exit(1);      
    }
    
    TextVectorComparison tvc = new TextVectorComparison(indexLocation);
    
    /* Run Synonym Task */
    int corr=0;
    int questionNum = 1;

    while(s.hasNext()) {

      /* Get Next Question */
      String str = s.nextLine();

      /* Print Question */
      if(report) {
        System.out.println(str);
      }
      questionNum++;

      /* Split the input string */			
      String[] arr = str.split("\\|");
      for(int i=0;i<arr.length; i++) {
        arr[i] = arr[i].trim();
      }

      /* Make placeholders for relatedness values */
      double [] vals = new double[4];
      for(int i = 0; i < vals.length; i++)
      {
        vals[i] = -10;
      }

      /* Find the Surface Form ID of the given word */
      String[] candidateSourceWords = Common.resolve(arr[0], word2Vertex);
      
//      for(int x = 0; candidateSourceWords != null && x < candidateSourceWords.length; x++)
//      {
//        
//        VertexCount[] originalVertex = word2Vertex.getVertexMappings(candidateSourceWords[x]);
//        
//        for(int y = 0; originalVertex != null && y < originalVertex.length; y++)
//        {
//          
//          /* Get relatedness distributions for the ID */
//          int fromVertex = originalVertex[y].getVertex();
//  
//          /* For each potential confusion item */
//          for(int i = 1; i <= vals.length; i++)
//          {
//            /* Break string into all possible resolvable words */
//            String[] candidateWords = Common.resolve(arr[i], word2Vertex);
//  
//            for(int j = 0; candidateWords != null && j < candidateWords.length; j++)
//            {
//              /* For each potential ID for the surface form */
//              VertexCount[] candidateVertices = word2Vertex.getVertexMappings(candidateWords[j]);
//              
//              for(int k = 0; candidateVertices != null && k < candidateVertices.length; k++)
//              {
//                int toVertex = candidateVertices[k].getVertex();
//                
//                if(toVertex >= 0)
//                {
//                  double val = tvc.getCOSMeasure(fromVertex, toVertex);
//                  /* For each potential ID for the surface form */
//                  vals[i-1] = Math.max(vals[i-1], val);
//                }
//              }//end: for(k)
//            }//end: for(j)
//          }//end: for(i)
//        }//end: for(y)
//      }//end: for(x)

      /* Check results */

      /* First item in the array is the answer */
      double answer = vals[0];

      /* Check it compared to the other items */
      if (answer > vals[1] && answer > vals[2] && answer > vals[3])
      {
        corr++;
      } 

      if(report)
      {
        for(int i=0; i<vals.length; i++)
        {
          System.out.print(" " + vals[i]);
        }//end: for(i)
        System.out.println();
      }
    }//end while(hasNext())

    /* Print Results */
    System.out.println("Correct : " + corr);
  }//end: main
}
