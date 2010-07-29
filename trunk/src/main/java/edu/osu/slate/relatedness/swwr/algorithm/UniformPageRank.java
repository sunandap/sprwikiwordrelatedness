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

package edu.osu.slate.relatedness.swwr.algorithm;

import java.io.*;
import java.util.Arrays;

import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Implements PageRank for a graph, as given by Brin and Page.
 * 
 * @author weale
 * @version 1.0
 */
public class UniformPageRank extends WikiGraph
{
  /**
   *
   */
  private static final long serialVersionUID = 4484834055399872458L;

  /* Array of PageRank values */
  protected double[] PR;

  /* Array of transition probabilities */
  private float[][] uniformTrans;
  
  /* Model parameter */
  protected double alpha;
  
  /**
   * Calculates PageRank values for a given graph using the default value for alpha (0.15).
   * 
   * @param graphFile {@link String} containing the path to the graph file.
   */
  public UniformPageRank(String graphFile)
  {
    super(graphFile);
    
    uniformTrans = new float[tProb.length][];
    
    // Set uniform transition probabilities
    for(int i = 0; i < tProb.length; i++)
    {
      if(tProb[i] != null)
      {
        uniformTrans[i] = new float[tProb[i].length];
        
        for(int j = 0; j < tProb[i].length; j++)
        {
          uniformTrans[i][j] = (float) 1.0 / tProb[i].length;
        }//end: for(j)
      }//end: if(!null)
      
    }//end: for(i)
    
    calculatePageRank(0.85);
  }

  /**
   * Calculates PageRank values for a given graph using the default value for alpha (0.15).
   * 
   * @param graph Previously initialized {@link WikiGraph} structure
   */
  public UniformPageRank(WikiGraph graph)
  {
    super(graph);

    uniformTrans = new float[tProb.length][];
    
    // Set PR
    for(int i = 0; i < tProb.length; i++)
    {
      if(tProb[i] != null)
      {
        uniformTrans[i] = new float[tProb[i].length];
        
        for(int j = 0; j < tProb[i].length; j++)
        {
          uniformTrans[i][j] = (float) 1.0 / tProb[i].length;
        }//end: for(j)
      }//end: if(!null)
      
    }//end: for(i)
    
    calculatePageRank(0.85);
  }
  
  /**
   * Re-calculates PageRank values using a non-default value of alpha.
   * 
   * @param alpha Parameter to weight the Random Walk influence on PageRank
   */
  public void calculatePageRank(double alpha)
  {
    this.alpha = alpha;

    // Create new/old PageRank vectors for iteration
    PR = new double[graph.length];
    double [] PR_new = new double[graph.length];

    // Initialize Uniform PageRank Vector
    for(int i = 0; i < PR.length; i++)
    {
      PR[i] = (float) (1.0 / (float) PR.length);
    }//end: for(i)

    // PageRank!!!
    double change;
    do {
      
      // Set the additional randomSurfer parameter
      double randomSurfer = 0;
      
      for(int i = 0; i < graph.length; i++)
      {
        // Valid out-bound graph
        if(graph[i] != null && graph[i].length != 0)
        {
          // Update new values for neighbor vertices
          for(int j = 0; j < graph[i].length; j++)
          {
            PR_new[graph[i][j]] += (PR[i] * uniformTrans[i][j]);
          }//end: for(j)
        }
        else
        { // No outbound links, add to overall graph values
          randomSurfer += PR[i] / graph.length;
        }

      }//end: for(i)
      
      // Combine the two models
      for(int x = 0; x < PR_new.length; x++)
      {
        PR_new[x] = (alpha * (PR_new[x] + randomSurfer)) + ((1-alpha) / PR_new.length);
      }//end: for(x)

      // Calculate change between PR generations
      change = pageRankDiff(PR, PR_new);

      // Reset new PR array.
      System.arraycopy(PR_new, 0, PR, 0, PR.length);
      Arrays.fill(PR_new, 0.0);

      // Calculate the magnitude for normalization
      double mag = 0;
      for(int i = 0; i < PR.length; i++)
      {
        mag += PR[i];
      }//end: for(i)
      
      // Normalize PR vector
      for(int i = 0; i < PR.length; i++)
      {
        PR[i] = (float) (PR[i] / mag);
      }//end: for(i)

    } while(change > 0.001);
  }//end: calculatePageRank(double)

  /**
   * Calculates the absolute change between two PageRank value arrays.
   * 
   * @param i Old PageRank array
   * @param j New PageRank array
   * @return Sum of the absolute value of the difference between all corresponding element pairs.
   */
  protected static double pageRankDiff(double[] oldPR, double[] newPR)
  {
    float diff = 0;
    
    for(int x = 0; x < oldPR.length; x++)
    {
      diff += Math.abs( oldPR[x] - newPR[x] );
    }//end: for(x)
    
    return diff;
  }//end: pageRankDiff(double[], double[])

  /**
   * Gets the array of PageRank values.
   * 
   * @return double[] of PageRank values.
   */
  public double[] getPageRankValues()
  {
    return PR;
  }
  
  /**
   * Returns the PageRank value for the given vertex.
   * 
   * @param v Vertex number.
   * @return PageRank value.
   */
  public double getPageRankValue(int v)
  {
    return PR[v];
  }

  /**
   * Prints the PageRank values in a readable file format.
   * 
   * @param outputFile Name of the file to print the PageRank values to.
   */
  public void printPageRank(String outputFile)
  {
    try
    {
      PrintWriter pw = new PrintWriter(outputFile);
      
      for(int i = 0; i < PR.length; i++)
      {
        pw.println( PR[i] );
      }//end: for(i)
      
      pw.flush();
      pw.close();
    }//end: try {}
    catch(IOException e)
    {
      e.printStackTrace();
      System.out.println("Problem in printPageRanks");
    }
  }//end: printPageRank(String)

  /**
   * Prints the PageRank values to the console.
   */
  public void printPageRank()
  {
    for(int i = 0;  i< PR.length; i++)
    {
      System.out.println(i + ":\t" + PR[i]);
    }//end: for(i)
  }//end: printPageRank()

}//end: UniformPageRank