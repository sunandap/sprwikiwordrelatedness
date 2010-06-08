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
import java.util.*;

/**
 * This class is contains the mapping of Wiki IDs to Vertex numbers.
 * <p>
 * Vertex numbers are always less than or equal to their Wiki ID.
 * <p>
 * <ul>
 *   <li><b>ID</b> -- Original Page number assigned to in the Wiki data set.</li>
 *   <li><b>Vertex</b> -- Internal vertex number used in the PageRank graph.</li>
 * </ul>
 * <p>
 * 
 * @author weale
 * @version 2.0-alpha
 * 
 */
public class IDVertexTranslation implements Serializable
{

 /**
  * 
  */
  private static final long serialVersionUID = 771210386820926081L;
  
 /**
  * Integer array containing the values of the valid,
  * non-redirected IDs from the Wiki data set.
  * 
  * The position of an ID in the array corresponds to
  * its vertex number.
  */
  private int[] validList;

 /**
  * Constructor.
  * <p>
  * Reads in a new list of vertex-to-ID translations as an integer array.
  * 
  * @param vertexList Integer array of vertex-to-ID translations.
  */
  public IDVertexTranslation(int[] vertexList)
  {
    validList = new int[vertexList.length];
    
    for(int i = 0; i < validList.length; i++)
    {
      validList[i] = vertexList[i];
    }
  }//end: IDVertexTranslation(int[])
  
 /**
  * Constructor.
  * <p>
  * Takes the name of the <i>.vid file</i> generated from {@link CreateIDToVertexFile} as input.
  * 
  * @param filename File name of the <i>.vid file</i>.
  */
  public IDVertexTranslation(String filename)
  {
    try
    {
      ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
      validList = (int[]) fileIn.readObject();
      fileIn.close();
      
      //Ensure a sorted list
      Arrays.sort(validList);
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
  }//end: IDVertexTranslation(String)

 /**
  * Gets the number of ID to Vertex translations.
  * 
  * @return The number of graph vertices.
  */
  public int numVertices()
  {
    return validList.length;
  }//end: numVertices()
	
 /**
  * Determines if a Wiki ID value has a corresponding Vertex number.
  * 
  * @param id Wiki ID Value
  * @return True if Wiki ID Value has a Vertex number. False otherwise.
  */
  public boolean isValidWikiID(int id)
  {
    return (Arrays.binarySearch(validList, id) >= 0);
  }//end: isValidWikiID(int)
	
 /**
  * This method takes a Wiki ID and returns the graph vertex number.
  * <p>
  * If the ID is valid, the method will return a positive number (>=0).  Invalid IDs return a negative value (>0).
  * 
  * @param id Wiki ID value
  * @return Graph vertex value
  */
  public int getVertex(int id)
  {
    return Arrays.binarySearch(validList, id);
  }//end: getVertex(int)
	
 /**
  * Translates a Vertex number to a Wiki ID.
  * 
  * @param vertex Vertex to translate.
  * @return Original Wiki ID.
  */
  public int getID(int vertex)
  {
    if(vertex > -1 && vertex < validList.length)
    {
      return validList[vertex];
    }
    else
    {
      return -1;
    }
  }//end: getID(int)
	
  /**
   * Writes the object to the given {@link ObjectOutputStream}.
   * <p>
   * Writes the vertex-id integer array to the file.
   * 
   * @param out {@link ObjectOutputStream} to be written to.
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(validList);
  }//end: writeObject(ObjectOutputStream)

  /**
   * Reads the object from the given {@link ObjectInputStream}.
   * <p>
   * Reads the vertex-id integer array.
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    validList = (int []) in.readObject();
  }//end: readObject(ObjectInputStream)
  
  public static void main(String[] args) {
    //IDVertexTranslation vid = new IDVertexTranslation("/scratch/weale/data/binary/enwiki/20080103/enwiki-20080103-M.vid");
  }//end: main()
}
