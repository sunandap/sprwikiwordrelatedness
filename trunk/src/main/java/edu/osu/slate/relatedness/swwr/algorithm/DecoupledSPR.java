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
 * In this version, uniform PageRank transition probabilities are used to calculate the PR values.
 * Non-uniform transition probabilities are used for SPR calculation.
 * If this is not desired, use the {@link SourcedPageRank} class.
 * <p>
 * Source Paper: Y. Ollivier and P. Senellart, <i>Finding Related Pages Using Green Measures: An Illustration with Wikipedia.</i>
 * 
 * @author weale
 * @version 2.0
 */
public class DecoupledSPR extends UniformPageRank implements RelatednessInterface
{
  
  /* Serialization UID  */
  private static final long serialVersionUID = 6168622709678063605L;

  /* Previous iteration SPR values */
  private double [] SPR_old;

  /* Current iteration SPR values */
  private double [] SPR_new;

  /* Sourcing Vector */
  private double [] SourceVect;

  /* Sourcing Vector Weight */
  private double beta;

  /**
   * Constructor.  Calls the {@link UniformPageRank} constructor.
   * 
   * @param graphFileName {@link java.lang.String} containing the path to the graph file.
   */
  public DecoupledSPR(String graphFileName)
  {
    super(graphFileName);
    beta = alpha;
  }

  /**
   * Constructor.  Calls the {@link UniformPageRank} constructor.
   * 
   * @param graph Previously initialized {@link WikiGraph} structure.
   */
  public DecoupledSPR(WikiGraph graph)
  {
    super(graph);
    beta = alpha;
  }
  
  /**
   * Constructor. Calls the {@link UniformPageRank} constructor.
   * 
   * @param graph Previously initialized {@link WikiGraph} structure.
   * @param beta Sourcing vector weight
   */
  public DecoupledSPR(WikiGraph graph, double beta)
  {
    super(graph);
    this.beta = beta;
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

    // Return Results of getRelatedness
    return getRelatedness(from, to);
  }

  /**
   * Finds the relatedness value between two vertex numbers.
   * <p>
   * Requires relatedness calculations on the full-graph.
   * 
   * @param from Source vertex number.
   * @param to Destination vertex number.
   * @return DecoupledSPR value.
   */
  public double getRelatedness(int from, int to) {

    //Get Sourced PageRank distribution
    double [] SPRVals = getRelatedness(from);		

    // Return value at the 'to' index
    return SPRVals[to];
  }
  /**
   * Finds the relatedness distribution sourced at a vertex using the approximate inference routine.
   *
   * @param from Vertex ID number (compressed)
   * @return Array containing relatedness distribution
   */
  public double[] getRelatedness(int from) {

    SPR_old = new double[graph.length];
    SPR_new = new double[graph.length];
    SourceVect = new double[graph.length];

    for(int j = 0; j < SourceVect.length; j++)
    {
      SourceVect[j] = PR[j] * -1;
    }
    
    System.arraycopy(SourceVect, 0, SPR_old, 0, SourceVect.length);

    SourceVect[from] = SourceVect[from] + 1;
    SPR_old[from] = SPR_old[from] + 1;

    int numIterations = 0;
    double change;
    
    // SOURCED PAGERANK ALGORITHM
    do
    {
      double randomSurfer = 0;

      // for each graph vertex
      for(int j = 0; j < graph.length; j++)
      {
        if(graph[j] != null && graph[j].length != 0)
        {
          // Valid transition array
          // Propagate values forward in graph
          for(int k = 0; k < graph[j].length; k++)
          {
            SPR_new[graph[j][k]] += (SPR_old[j] * tProb[j][k]);
          }//end: for(k)
        }
        else
        {
          // No out-bound edges
          // Add transition values to randomSurfer for universal weight dispersion
          randomSurfer += SPR_old[j] / graph.length;
        }

      }//end: for(j)

      // Combine three models
      for(int x=0; x<SPR_new.length; x++)
      {
        SPR_new[x] = alpha * (SPR_new[x] + randomSurfer) + ((1-alpha) / graph.length) + beta * SourceVect[x];
      }

      change = pageRankDiff(SPR_old, SPR_new);
      System.arraycopy(SPR_new, 0, SPR_old, 0, SPR_new.length);
      Arrays.fill(SPR_new, 0.0);

      numIterations++;
    }while(change > 0.002);

    for(int j=0; j<SPR_old.length; j++)
    {
      SPR_old[j] = SPR_old[j] * Math.log10(1.0/PR[j]);
    }//end: for(j)

    return SPR_old;
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


    SPR_old = new double[graph.length];
    SPR_new = new double[graph.length];
    SourceVect = new double[graph.length];

    for(int j = 0; j < SourceVect.length; j++)
    {
      SourceVect[j] = PR[j] * -1;
    }
    
    System.arraycopy(SourceVect, 0, SPR_old, 0, SourceVect.length);
    
    for(int i = 0; i < from.length; i++)
    {
      SourceVect[from[i]] = SourceVect[from[i]] + (1.0/from.length);
      SPR_old[from[i]] = SPR_old[from[i]] + (1.0/from.length);
    }

    // Return Results of getRelatednessDistribution
    return getRelatedness();
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
  public double[] getRelatedness(int[] from, float[] vals)
  {
    SPR_old = new double[graph.length];
    SPR_new = new double[graph.length];
    SourceVect = new double[graph.length];

    for(int j = 0; j < SourceVect.length; j++)
    {
      SourceVect[j] = PR[j] * -1;
    }
    
    System.arraycopy(SourceVect, 0, SPR_old, 0, SourceVect.length);

    for(int i = 0; i < from.length; i++)
    {
      SourceVect[from[i]] = SourceVect[from[i]] + vals[i];
      SPR_old[from[i]] = SPR_old[from[i]] + vals[i];
    }

    return getRelatedness();
  }

  /**
   * Finds the relatedness distribution sourced at a vertex using the exact inference routine.
   * <p>
   * This runs slower than getRelatedness, but should return more accurate results.
   * <p>
   * This is NOT guaranteed to converge.
   *
   * @return Array containing relatedness distribution
   */
  public double[] getRelatedness()
  {
    int numIterations = 0;
    double change;
    
    // SOURCED PAGERANK ALGORITHM
    do
    {
      double randomSurfer = 0;

      // for each graph vertex
      for(int j = 0; j < graph.length; j++)
      {
        if(graph[j] != null && graph[j].length != 0)
        {
          // Valid transition array
          // Propagate values forward in graph
          for(int k = 0; k < graph[j].length; k++)
          {
            SPR_new[graph[j][k]] += (SPR_old[j] * tProb[j][k]);
          }//end: for(k)
        }
        else
        {
          // No out-bound edges
          // Add transition values to randomSurfer for universal weight dispersion
          randomSurfer += SPR_old[j] / graph.length;
        }

      }//end: for(j)

      // Combine three models
      for(int x = 0; x < SPR_new.length; x++)
      {
        SPR_new[x] = alpha * (SPR_new[x] + randomSurfer) + ((1-alpha) / graph.length) + beta * SourceVect[x];
      }

      change = pageRankDiff(SPR_old, SPR_new);
      System.arraycopy(SPR_new, 0, SPR_old, 0, SPR_new.length);
      Arrays.fill(SPR_new, 0.0);

      numIterations++;
    }while(change > 0.002);

    for(int j = 0; j < SPR_old.length; j++)
    {
      SPR_old[j] = SPR_old[j] * Math.log10(1.0/PR[j]);
    }//end: for(j)

    return SPR_old;
  }


}//end: DecoupledSPR