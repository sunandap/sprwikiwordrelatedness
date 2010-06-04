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

import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.*;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

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
public class SPRThread implements Runnable
{
  // Synonym Task File
  private String taskFile;

  // File for Resulting Relatedness Values
  private String resultFile;

  // File for Vertices Used to Determine Relatedness Values
  private String vertexFile;
  
  // Term-to-Vertex mapping object
  private TermToVertexMapping term2Vertex;

  // Relatedness Algorithm
  private SourcedPageRank ngd;
  
 /**
  * Constructor.
  * <p>
  * Initializes a {@link SourcedPageRank} object from the given graph
  * and writes relatedness/vertex information to files.
  * 
  * @param t2v {@link TermToVertexMapping} containing mapping algorithm.
  * @param spr {@link WikiGraph} with Wiki graph.
  * @param tFile Task file name.
  * @param rFile Results file name.
  * @param vFile Vertex number file name.
  */
  public SPRThread(TermToVertexMapping t2v, WikiGraph spr, String tFile, String rFile, String vFile)
  {
    term2Vertex = t2v;
    ngd = new SourcedPageRank(spr);
    taskFile = tFile;
    resultFile = rFile;
    vertexFile = vFile;
  }
  
 /**
  * Runs Sourced PageRank for the given task.
  */
  @Override
  public void run()
  {
    System.out.println(taskFile);
    
    /* Open the files for Reading / Writing */
    Scanner s = null;
    PrintWriter pw = null;
    PrintWriter vPW = null;
    try {
      s = new Scanner(new FileReader(taskFile));
      pw = new PrintWriter(resultFile);
      vPW = new PrintWriter(vertexFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    /* Run Synonym Task */
    int corr=0, att=0;
    int questionNum = 1;
    int[] sVertex = new int[4];
    int[] tVertex = new int[4];
    
    while(s.hasNext()) {

      /* Get Next Question */
      String str = s.nextLine();
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

      /* Get vertices for the given terms */
      VertexCount[] vcSource = getVertices(arr[0]);
      VertexCount[][] vcTerms = new VertexCount[4][];
      vcTerms[0] = getVertices(arr[1]);
      vcTerms[1] = getVertices(arr[2]);
      vcTerms[2] = getVertices(arr[3]);
      vcTerms[3] = getVertices(arr[4]);
      
      for(int x = 0; vcSource != null && x < vcSource.length; x++)
      {
        /* Get relatedness distributions for the vertex */
        double [] sprValues = ngd.getRelatedness(vcSource[x].getVertex());
  
        /* For each target word */
        for(int i = 0; sprValues != null && i < vcTerms.length; i++)
        {
          tVertex[i] = -1;
          
          /* Check each vertex for the terms */
          for(int y = 0; vcTerms[i] != null && y < vcTerms[i].length; y++)
          {
            int currentVertex = vcTerms[i][y].getVertex();
                
            /* if the vertex is valid */
            if(currentVertex >= 0)
            {
              // Top value so far, update value and vertices.
              if(sprValues[currentVertex] > vals[i])
              {
                tVertex[i] = currentVertex;
                sVertex[i] = vcSource[x].getVertex();
              }
              
              vals[i] = Math.max(vals[i], sprValues[currentVertex]);
            }
            else
            {
              System.err.println("invalid: (" + i + ")\t" + currentVertex);
            }
          }//end: for(k)
        }//end: for(j)
      }//end: for(x)

      /* Check results */
  
      /* First item in the array is the answer */
      double answer = vals[0];
  
      /* Check it compared to the other items */
      if (answer > vals[1] && answer > vals[2] && answer > vals[3])
      {
        corr++;
      }
      
      if(vals[0] != -10 || vals[1] != -10 ||
         vals[2] != -10 || vals[3] != -10 )
      {
        att++;
      }
  
      // Print relatedness values to file
      for(int i=0; i<vals.length; i++)
      {
        pw.print(vals[i] + " ");
      }//end: for(i)
      pw.println();
      
      // Print vertex numbers to file
      for(int i=0; i<tVertex.length; i++)
      {
        vPW.print(sVertex[i] + " " + tVertex[i] + " ");
      }//end: for(i)
      vPW.println();
      
    }//end while(hasNext())

    /* Print Results */
    System.out.println(taskFile);
    System.out.println("* Correct   : " + corr);
    System.out.println("* Attempted : " + att);
    pw.close();
    vPW.close();
  }
  
  /**
   * Gets all vertices for the given task term. 
   *  
   * @param term Task term.
   * @return Array of {@link VertexCount} objects.
   */
   private VertexCount[] getVertices(String term)
   {
     TermToVertexCount[] t1vc = term2Vertex.getVertexMappings(term);
     if(t1vc == null)
     {
       t1vc = term2Vertex.getSubTermVertexMappings(term);
     }
     
     TreeSet<VertexCount> ts = new TreeSet<VertexCount>();
     for(int i = 0; t1vc != null && i < t1vc.length; i++)
     {
       VertexCount[] vc1 = t1vc[i].getVertexCounts();
       for(int j = 0; vc1 != null && j < vc1.length; j++)
       {
         ts.add(vc1[j]);
       }//end: for(j)
     }//end: for(i)

     if(ts.size() == 0)
     {
       return null;
     }
     
     VertexCount[] vc = new VertexCount[ts.size()];
     Iterator<VertexCount> it = ts.iterator();
     int pos = 0;
     while(it.hasNext())
     {
       vc[pos] = it.next();
       pos++;
     }//end: while(it)
     
     return vc;
   }
}
