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

import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Implements PageRank for a graph, as given by Brin and Page.
 * 
 * @author weale
 * @version 1.0
 */
public class PageRank extends WikiGraph {

  /**
   *
   */
  private static final long serialVersionUID = 4484834055399872458L;

  /**
   *  Array of PageRank values
   */
  protected double[] PR;

  /**
   * Calculates PageRank values for a given graph using the default value for alpha (0.15).
   * 
   * @param graphFile {@link String} containing the path to the graph file.
   */
  public PageRank(String graphFile)
  {
    super(graphFile);
    calculatePageRank(0.85);
  }

  /**
   * Calculates PageRank values for a given graph using the default value for alpha (0.15).
   * 
   * @param graph Previously initialized {@link WikiGraph} structure
   */
  public PageRank(WikiGraph graph)
  {
    super(graph);
    calculatePageRank(0.85);
  }

  /**
   * Re-calculates PageRank values using a non-default value of alpha.
   * 
   * @param alpha Parameter to weight the Random Walk influence on PageRank
   */
  public void calculatePageRank(double alpha) {

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
            PR_new[graph[i][j]] += (PR[i] * tProb[i][j]);
          }//end: for(j)
        }
        else
        { // No outbound links, add to overall graph values
          randomSurfer += PR[i] / graph.length;
        }

        if(verbose && i%200000 == 0)
        {
          System.out.print(".");
        }
      }//end: for(i)
      
      if(verbose)
      {
        System.out.println();
      }

      for(int x = 0; x < PR_new.length; x++)
      {
        PR_new[x] = (alpha * (PR_new[x] + randomSurfer)) + ((1-alpha) / PR_new.length);
      }//end: for(x)

      // Calculate change between PR generations
      change = pageRankDiff(PR, PR_new);

      // Reset new PR array.
      for(int x = 0; x < PR.length; x++)
      {
        PR[x] = PR_new[x];
        PR_new[x] = 0;
      }

      // Calculate the magnitude for normalization
      double mag = 0;
      for(int i = 0; i < PR.length; i++)
      {
        mag += PR[i];
      }//end: for(i)
      
      if(verbose)
      { // Sanity Check
        System.out.println("This should be about one: " + mag);
      }
      
      // Normalize PR vector
      for(int i = 0; i < PR.length; i++)
      {
        PR[i] = (float) (PR[i] / mag);
      }//end: for(i)

      if(verbose)
      { // Sanity Check
        System.out.println(change);
      }

    }while(change > 0.001);		
  }//end: calculatePageRank(double)

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

}//end: PageRank