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

package edu.osu.slate.relatedness.swwr.data.graph;

import java.io.*;

/**
 * Contains an inverted graph of the Wiki vertex-vertex link structure.
 * <p>
 * This keeps track of which pages link to a particular page.
 * That is, this contains the inbound list to a page (to, from, from, from,...)
 * <p>
 * Requires the <i>.iwgp file</i> created by the {@link WikiInvGraph} class.
 * 
 * @author weale
 * @version 1.0
 */
public class WikiInvGraph implements Serializable
{	
  /* Used for serialization */
  private static final long serialVersionUID = 1L;

  /* Array of arrays to keep the graph structure in memory. */
  private int [][] igraph;

  /* Array of transition probabilities */
  private float [][] tProb;

  /* Keeps track of the number of graph edges */
  private int numEdges;

  /* Is it a uniform transition matrix? */
  private boolean isUniform;

 /**
  * Constructor.
  * <p>
  * Uses the <i>.iwgp file</i> created by the {@link WikiInvGraph} class.
  * 
  * @param fileName File name of the <i>.iwgp</i> file.
  */
  public WikiInvGraph(String filename)
  {
    try
    {
      ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
      igraph = (int[][]) fileIn.readObject();
      tProb = (float[][]) fileIn.readObject();
      fileIn.close();

      /* Check for uniform transition probabilities */
      isUniform = true;
      for(int i = 0; isUniform && i < tProb.length; i++)
      {
        if(tProb[i] != null && tProb[i].length > 1)
        {
          isUniform = false;
        }
      }//end: for(i)

      /* Initialize the number of edges */
      numEdges = 0;
      for(int i = 0; i < igraph.length; i++)
      {
        if(igraph[i] != null)
        {
          numEdges += igraph[i].length;
        }
      }//end: for(i)
    }//end: try{}
    catch (ClassNotFoundException e)
    {
      System.err.println("Problem converting to an integer array: " + filename);
      e.printStackTrace();
    }
    catch (FileNotFoundException e)
    {
      System.err.println("File not found: " + filename);
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.err.println("Problem reading from file: " + filename);
      e.printStackTrace();
    }
  }//end: WikiInvGraph(String)
  
  /**
   * Returns the inbound links for a given vertex.
   * 
   * @param v Vertex number.
   * @return Array of vertex numbers linking to given vertex.
   */
  public int[] getInboundLinks(int v)
  {
    if(v> -1 && v < igraph.length)
    {
      return igraph[v];
    }
    else
    {
      return null;
    }
  }//end: getInboundLinks(int)

  /**
   * Returns the inbound transition values for a given vertex.
   * 
   * @param v Vertex number.
   * @return Array of vertex transition probabilities.
   */
  public float[] getInboundTransitions(int v)
  {
    if(v > -1 && v < igraph.length)
    {
      if(igraph[v] == null)
      { // no in-bound links
        return null;
      }
      if(isUniform)
      {// uniform inbound transition probabilities
        float[] f = new float[ igraph[v].length ];
        for(int i=0; i<f.length; i++)
        {
          f[i] = (float) (1.0 / f.length);
        }//end: for(i)
        return f;
      }
      else
      {// non-uniform transition found
        return tProb[v];
      }
    }
    else
    { //invalid vertex num given
      return null;
    }
  }//end: getInboundTransitions(int)

  /**
   * Gets the inverted vertex-vertex arrays.
   * 
   * @return Arrays of in-bound vertex-vertex links.
   */
  public int[][] getInvertedLinkArrays()
  {
    return igraph;
  }//end: getInvertedLinkArrays()

 /**
  * Gets the number of graph vertices.
  * 
  * @return Number of graph vertices.
  */
  public int getNumVertices()
  {
    return igraph.length;
  }//end: getNumVertices()

  /**
   * Gets the number of graph edges.
   * 
   * @return Number of graph edges.
   */
  public int getNumEdges()
  {
    return numEdges;
  }//end: getNumEdges()

  /**
   * Writes the object to the given {@link ObjectOutputStream}.
   * 
   * @param out {@link ObjectOutputStream} to write to.
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(igraph);	
    out.writeObject(tProb);
  }//end: writeObject(ObjectOutputStream)

  /**
   * Reads the object from the given {@link ObjectInputStream}.
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    igraph = (int [][]) in.readObject();
    tProb = (float [][]) in.readObject();
  }//end: readObject(ObjectInputStream)
}
