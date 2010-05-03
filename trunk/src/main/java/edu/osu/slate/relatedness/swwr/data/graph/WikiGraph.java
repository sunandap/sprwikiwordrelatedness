package edu.osu.slate.relatedness.swwr.data.graph;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Contains the graph structure of the Wiki data set.
 * <p>
 * This class keeps track of which pages are linked to by particular page.
 * That is, this contains the outbound list to a page (from, to, to, to,...)
 * <p>
 * Requres the <i>.wgp file</i> created from {@link WikiGraph}.
 * 
 * @author weale
 * @version 1.0
 */

public class WikiGraph implements Serializable
{

  /* Used for serialization */
  private static final long serialVersionUID = 1L;

  /**
   *  Array of arrays to keep the graph structure in memory.
   */
  protected int [][] graph;

  /** 
   * Array of transition probabilities
   */
  protected float [][] tProb;

  /* Keeps track of the number of graph edges */
  protected int numEdges;

  /* */
  protected boolean isUniform;

  /* */
  protected boolean isDirected = true;

  /**
   * 
   */
  protected static boolean verbose = false;

  /**
   * Sets the verbose output flag.
   * 
   * @param v Verbose output flag setting.
   */
  public static void setVerbose(boolean v)
  {
    verbose = v;
  }

 /**
  * Creates a new WikiGraph from an existing WikiGraph in memory.
  * <p>
  * Creates new graph and probability matrices.
  * 
  * @param wg Existing WikiGraph.
  */
  public WikiGraph(WikiGraph wg)
  {
    this.graph = wg.graph.clone();
    this.tProb = wg.tProb.clone();
    this.numEdges = wg.numEdges;
    this.isUniform = wg.isUniform;
    this.isDirected = wg.isDirected;
  }//end: WikiGraph(WikiGraph)

 /**
  * Reads a WikiGraph from an existing .wgp file.
  * 
  * @param filename Name of the <i>.wgp file</i>.
  */
  public WikiGraph(String filename)
  {
    try
    {
      ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
      graph = (int[][]) fileIn.readObject();
      tProb = (float[][]) fileIn.readObject();
      fileIn.close();

      /* Check for uniform transition probabilities */
      isUniform = true;
      for(int i = 0; isUniform && i<tProb.length; i++)
      {
        if(tProb[i] != null && tProb[i].length > 1)
        {
          isUniform = false;
        }
      }//end: for(i)

      if(isUniform)
      { 
        //Create uniform transition matrix
        for(int i=0; i<tProb.length; i++)
        {
          if(graph[i] != null)
          {
            tProb[i] = new float[graph[i].length];
            for(int j=0; j<tProb[i].length; j++)
            {
              tProb[i][j] = (float)(1.0 / tProb[i].length);
            }//end: for(j)
          }//end: if(graph[i])
        }//end: for(i)
      }//end: if(isUniform)

      /* Initialize the number of edges */
      numEdges = 0;
      for(int i=0; i<graph.length; i++)
      {
        if(graph[i] != null)
        {
          numEdges += graph[i].length;
        }
      }//end: for(i)

    }//end: try {}
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
  }//end: WikiGraph(String)

 /**
  * Get the outbound vertices for a given vertex.
  * <p>
  * Returns null if there are no outbound vertices.
  * 
  * @param v Vertex number.
  * @return Integer array of outbound vertices.
  */
  public int[] getOutboundLinks(int v) {
    if(v > -1 && v < graph.length)
    {
      return graph[v];
    }
    else
    {
      return null;
    }
  }//end: getOutbountLinks(int)

 /**
  * Get the outbound transition probabilities for a given vertex.
  * <p>
  * Returns null if there are no outbound vertices.
  * 
  * @param v Vertex number.
  * @return Float array of outbound transition probabilities.
  */
  public float[] getOutboundTransitions(int v)
  {
    if(v > -1 && v < graph.length)
    {
      return tProb[v];
    }
    else
    {
      return null;
    }
  }//end: getOutboundTransitions(int)

 /**
  * Gets the number of edges in the graph.
  * 
  * @return Number of edges.
  */
  public int getNumEdges()
  {
    return numEdges;
  }

 /**
  * Gets the number of vertices in the graph.
  * 
  * @return Number of vertices.
  */
  public int getNumVertices()
  {
    return graph.length;
  }

 /**
  * Gets whether transitions are uniform or not.
  * 
  * @return Boolean based on whether transitions are uniform or not.
  */
  public boolean isUniformTransition()
  {
    return isUniform;
  }

  /**
   * Add new edges to the graph to make it bi-directional.
   * <p>
   * Is not reversible. (And may not actually work!)
   */
  public void makeUndirected()
  {
    /* For each vertex in the graph */
    for(int i = 0; i < graph.length; i++)
    {
      /*  Check to make sure there's an inbound link.
       *  For each inbound edge in the graph */
      for(int j = 0; graph[i] != null && j < graph[i].length; j++)
      {
        int to = graph[i][j];

        /* Check that there's inbound links on the other side */
        if(graph[to] != null)
        {
          /* Check for symmetric link */
          int pos = Arrays.binarySearch(graph[to], i);

          if(pos < 0)
          {
            // Does not exist, add link
            int[] tmp = new int[graph[to].length+1];
            tmp[0] = i;
            for(int k = 1; k < tmp.length; k++)
            {
              tmp[k] = graph[to][k-1];
            }//end: for(k)
            
            Arrays.sort(tmp);
            graph[to] = tmp;					
          }//end: if(pos)
        }
        else
        {
          // No inbound links, add new array
          graph[to] = new int[1];
          graph[to][0] = i;
        }//end: null check
      }//end: for(j)
    }//end: for(i)

    /* Update transition probabilities based on new graph */
    for(int i = 0; i < graph.length; i++)
    {
      tProb[i] = new float[graph[i].length];

      for(int j = 0; j < graph[i].length; j++)
      {
        tProb[i][j] = (float) 1.0 / tProb[i].length;
      }//end: for(j)
    }//end: for(i)

    isDirected = false;
  }//end: makeUndirected()

  /** 
   * Returns whether or not the graph has been specifically set to be undirected or not.
   * <p>
   * False values indicate that the graph has been forced to be bi-directional.
   * 
   * @return Boolean value for directedness of the graph.
   */
  public boolean isDirected()
  {
    return isDirected;
  }

  /**
   * Writes the object to the given {@link ObjectOutputStream}.
   * <p>
   * Writes the graph (int[][]) followed by the transition matrix (float[][]) and the edge count (int).
   * 
   * @param out {@link ObjectOutputStream} to be written to.
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(graph);	
    out.writeObject(tProb);	
    out.writeInt(numEdges);	
  }//end: writeObject(ObjectOutputStream)

  /**
   * Reads the object from the given {@link ObjectInputStream}.
   * <p>
   * Reads the graph (int[][]) followed by the transition matrix (float[][]) and the count of edges (int).
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    graph = (int [][]) in.readObject();
    tProb = (float [][]) in.readObject();
    numEdges = in.readInt();
  }//end: readObject(ObjectInputStream)
}