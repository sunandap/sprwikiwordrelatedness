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

import java.util.Arrays;

import edu.osu.slate.relatedness.RelatednessInterface;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Implementation of the Sourced PageRank <b>(SPR)</b> version of vertex relatedness on a graph.
 * <p>
 * <i>Exact</i> relatedness methods omit the jump model from the calculation and only take the link structure into account when calculating values.
 * These run slower and are not guaranteed to converge, but may produce higher-quality results.
 * <p>
 * Source Paper: Y. Ollivier and P. Senellart, <i>Finding Related Pages Using Green Measures: An Illustration with Wikipedia.</i>
 * 
 * @author weale
 * @version 1.0
 */
public class PersonalizedPageRank extends PageRank implements RelatednessInterface {

  /**
   * 
   */
  private static final long serialVersionUID = 6168622709678063605L;

  /**
   * 
   */
  private double [] PPR_old;

  /**
   * 
   */
  private double [] PPR_new;

  /**
   * 
   */
  private double [] PR_jump;

  /**
   * Indicates the use of approximate (faster) or exact (more accurate) calculations.
   * <br>
   * Approximate calculations are guaranteed to converge, while exact calculations are not.
   */
  protected boolean approximate = false;

  /**
   * Constructor for GreenRelatedness.  Calls the {@link PageRank} constructor.
   * 
   * @param graphFileName {@link java.lang.String} containing the path to the graph file.
   */
//  public PersonalizedPageRank(String graphFileName)
//  {
//    super(graphFileName);
//  }

  /**
   * Constructor for GreenRelatedness.  Calls the {@link PageRank} constructor.
   * 
   * @param graph Previously initialized {@link WikiGraph} structure
   */
  public PersonalizedPageRank(WikiGraph graph)
  {
    super(graph);
  }

  /**
   * Finds the relatedness value between two vertices (compressed value) using the approximate inference routine.
   * <p>
   * This runs faster than getExactRelatedness, but may return less accurate results.
   * This is guaranteed to converge.
   * <p>
   * Requires relatedness calculations on the full-graph.
   * 
   * @param from Vertex ID number (compressed)
   * @param to Vertex ID number (compressed)
   * @return GreenMeasure resulting from running relatedness measure.
   */
  public double getRelatedness(int from, int to)
  {
    // Set Approximate Flag
    approximate = true;

    // Return Results of getRelatedness
    return getExactRelatedness(from, to);
  }

  /**
   * Finds the relatedness value between two vertices (compressed value) using the approximate inference routine.
   * <p>
   * This runs faster than getExactRelatedness, but may return less accurate results.
   * This is guaranteed to converge.
   * <p>
   * Requires relatedness calculations on the full-graph.
   * 
   * @param from Vertex ID number (compressed)
   * @param to Vertex ID number (compressed)
   * @return GreenMeasure resulting from running relatedness measure.
   */
  public double getRelatedness(int[] from, int to) {

    // Set Approximate Flag
    approximate = true;

    // Return Results of getRelatedness
    return getRelatedness(from, to);
  }

  /**
   * Finds the relatedness value between two vertices using the exact inference routine.
   * <p>
   * This runs slower than getRelatedness, but should return more accurate results.
   * This is NOT guaranteed to converge.
   * <p>
   * Requires relatedness calculations on the full-graph.
   *
   * @param from Vertex ID number (compressed)
   * @param to Vertex ID number (compressed)
   * @return GreenMeasure resulting from running relatedness measure.
   */
  public double getExactRelatedness(int from, int to) {

    //Get distribution
    double [] GM = getExactRelatedness(from);		

    // Return value at the 'to' index
    return GM[to];
  }

  /**
   * Finds the relatedness distribution sourced at a vertex using the approximate inference routine.
   * <p>
   * This runs faster than getExactRelatedness, but should return less accurate results.
   * <p>
   * This is guaranteed to converge.
   *
   * @param from Vertex ID number (compressed)
   * @return Array containing relatedness distribution
   */
  public double[] getRelatedness(int from) {

    // Set Approximate Flag
    approximate = true;

    // Return Results of getRelatednessDistribution
    return getExactRelatedness(from);
  }

  /**
   * Finds the relatedness distribution sourced at a vertex using the approximate inference routine.
   * <p>
   * This runs faster than getExactRelatedness, but should return less accurate results.
   * <p>
   * This is guaranteed to converge.
   *
   * @param from Vertex ID number (compressed)
   * @return Array containing relatedness distribution
   */
  public double[] getRelatedness(int[] from) {

    // Set Approximate Flag
    approximate = true;

    PPR_old = new double[graph.length];
    PPR_new = new double[graph.length];
    PR_jump = new double[graph.length];

    for(int j=0;j<PR_jump.length;j++) {
      PR_jump[j] = 0;
      PPR_old[j] = PR_jump[j];
    }

    for(int i=0; i<from.length; i++) {
      PR_jump[from[i]] = PR_jump[from[i]] + (1.0/from.length);
      PPR_old[from[i]] = PPR_old[from[i]] + (1.0/from.length);
    }

    // Return Results of getRelatednessDistribution
    return getExactRelatedness();
  }

  public double[] getRelatedness(int[] from, float[] vals)
  {
    approximate = true;

    PPR_old = new double[graph.length];
    PPR_new = new double[graph.length];
    PR_jump = new double[graph.length];

    for(int j=0;j<PR_jump.length;j++) {
      PR_jump[j] = PR[j] * -1;
      PPR_old[j] = PR_jump[j];
    }

    for(int i=0; i<from.length; i++) {
      PR_jump[from[i]] = PR_jump[from[i]] + vals[i];
      PPR_old[from[i]] = PPR_old[from[i]] + vals[i];
    }

    return getExactRelatedness();
  }

  /**
   * Finds the relatedness distribution sourced at a vertex using the exact inference routine.
   * <p>
   * This runs slower than getRelatedness, but should return more accurate results.
   * <p>
   * This is NOT guaranteed to converge.
   *
   * @param from Vertex ID number (compressed)
   * @return Array containing relatedness distribution
   */
  public double[] getExactRelatedness(int from)
  {
    PPR_old = new double[graph.length];
    PPR_new = new double[graph.length];
    PR_jump = new double[graph.length];

    for(int j = 0; j < PR_jump.length; j++)
    {
      PR_jump[j] = 0;
      PPR_old[j] = PR_jump[j];
    }

    PR_jump[from] = 1;
    PPR_old[from] = 1;

    int numIterations = 0;
    double change;
    do {
      double randomSurfer = 0;

      for(int j = 0; j < graph.length; j++) {

        if(graph[j] != null && graph[j].length != 0)
        {
          // Valid transition array
          for(int k = 0; k < graph[j].length; k++)
          {
            PPR_new[graph[j][k]] += (PPR_old[j] * tProb[j][k]);
          }//end: for(k)
        }
        else
        {
          // Add transition values to randomSurfer
          randomSurfer += PPR_old[j] / PR_jump.length;
        }

      }//end: for(j)

      for(int x = 0; x < PPR_new.length; x++)
      {
        if(PR_jump[x] == 0.0)
        {
          PPR_new[x] = (.85 * PPR_new[x]);
        }
        else
        {
          PPR_new[x] = (.85 * PPR_new[x]) + (.15 * (PR_jump[x] + randomSurfer));
        }
      }

      change = pageRankDiff(PPR_old, PPR_new);
      
//      double tmp = 0.0;
//      for(int x=0; x<PPR_new.length; x++)
//      {
//        PPR_old[x] = PPR_new[x];
//        tmp += PPR_old[x];
//        PPR_new[x] = 0;
//      }
//      System.out.println(tmp);
      
      System.arraycopy(PPR_new, 0, PPR_old, 0, PPR_new.length);
      Arrays.fill(PPR_new, 0.0);
      
      numIterations++;
    }while(change > 0.002);

//    for(int j = 0; j < PPR_old.length; j++)
//    {
//      PPR_old[j] = PPR_old[j] * Math.log10(1.0/PR[j]);
//    }

    approximate = false;
    return PPR_old;
  }

  /**
   * Finds the relatedness distribution sourced at a vertex using the exact inference routine.
   * <p>
   * This runs slower than getRelatedness, but should return more accurate results.
   * <p>
   * This is NOT guaranteed to converge.
   *
   * @param from Vertex ID number (compressed)
   * @return Array containing relatedness distribution
   */
  public double[] getExactRelatedness() {

    int numIterations = 0;
    double change;
    do {
      double randomSurfer = 0;

      for(int j = 0; j < graph.length; j++)
      {
        if(graph[j] != null && graph[j].length != 0)
        {
          // Valid transition array
          for(int k = 0; k < graph[j].length; k++)
          {
            PPR_new[graph[j][k]] += (PPR_old[j] * tProb[j][k]);
          }//end: for(k)
        }
        else
        {
          // Add transition values to randomSurfer
          randomSurfer += PPR_old[j] / PR_jump.length;
        }

      }//end: for(j)

      for(int x = 0; x < PPR_new.length; x++)
      {
        if(PR_jump[x] == 0.0)
        {
          PPR_new[x] = (.85 * PPR_new[x]);
        }
        else
        {
          PPR_new[x] = (.85 * PPR_new[x]) + (.15 * (PR_jump[x] + randomSurfer));
        }
      }

      change = pageRankDiff(PPR_old, PPR_new);

//      double tmp = 0.0;
//      for(int x=0; x<PPR_new.length; x++) {
//        PPR_old[x] = PPR_new[x];
//        tmp += PPR_old[x];
//        PPR_new[x] = 0;
//      }

      System.arraycopy(PPR_new, 0, PPR_old, 0, PPR_new.length);
      Arrays.fill(PPR_new, 0.0);
      
      numIterations++;
    }while(change > 0.002);

//    for(int j = 0; j < PPR_old.length; j++)
//    {
//      PPR_old[j] = PPR_old[j] * Math.log10(1.0/PR[j]);
//    }

    approximate = false;
    return PPR_old;
  }//end: getExactRelatedness()

}//end: PersonalizedPageRank