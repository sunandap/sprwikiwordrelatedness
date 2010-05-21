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

package edu.osu.slate.relatedness.swwr.data.mapping;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Simplified lookup class for the {@link VertexToTermCount} class.
 * 
 * @author weale
 * @version 1.01
 */
public class VertexToTermMapping implements Serializable
{  
  /**
   * 
   */
  private static final long serialVersionUID = -4574771377730886056L;

  private VertexToTermCount[] vertices;
  /**
   * Constructor.
   *  
   * @param vc Array of {@link VertexToTermCount} objects.
   */
  public VertexToTermMapping(VertexToTermCount[] vc)
  {
    vertices = new VertexToTermCount[vc.length];
    for(int i = 0; i < vc.length; i++)
    {
      vertices[i] = vc[i];
    }
  }//end: VertexToTermMapping(VertexToTermCount[])

  /**
   * Constructor.
   * <p>
   * Reads the {@link VertexToTermCount} array from the given <i>.iwc file</i>.
   * 
   * @param filename Input file name.
   */
  public VertexToTermMapping(String filename)
  {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));

      // Read array length
      int len = in.readInt();

      // Create and initialize array
      vertices = new VertexToTermCount[len];
      for(int i = 0; i < len; i++)
      {
        vertices[i] = (VertexToTermCount) in.readObject();
      }//end: for(i)

      in.close();
    }//end: try{}
    catch(IOException e)
    {
      System.err.println("Problem reading from file: " + filename);
      e.printStackTrace();
      System.exit(1);
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Problem with class conversion from file: " + filename);
      e.printStackTrace();
      System.exit(1);
    }
  }//end: VertexToTermMapping()

  /**
   * Gets the words mapped to a given vertex.
   * <p>
   * Returns null if vertex is not found.
   *  
   * @param v Vertex to be mapped.
   * @return An array of {@link TermCount} objects.
   */
  public TermCount[] getTermMappings(int v)
  {
    int pos = Arrays.binarySearch(vertices, new VertexToTermCount(v), new VertexToTermCountComparator());

    if(pos >= 0)
    { // FOUND!
      return vertices[pos].getWordCounts();
    }

    return null;
  }//end: getTermMappings(int)

  public void joinMappings(VertexToTermMapping vtm)
  {
    int numToAdd = 0;
    boolean[] addMe = new boolean[vtm.vertices.length];

    for(int i = 0; i < vtm.vertices.length; i++)
    {
      int pos = Arrays.binarySearch(vertices, vtm.vertices[i], new VertexToTermCountComparator());

      if(pos >= 0)
      {
        // Add to old Object
        vertices[pos].addObject(vtm.vertices[i]);
        addMe[i] = false;
      }
      else
      {
        numToAdd++;
        addMe[i] = true;
      }
    }//end: for(i)
    VertexToTermCount[] temp = new VertexToTermCount[vertices.length + numToAdd];

    System.arraycopy(vertices, 0, temp, 0, vertices.length);
    int addPos = vertices.length;

    for(int i = 0; i < vtm.vertices.length; i++)
    {
      if(addMe[i])
      {
        temp[addPos] = vtm.vertices[i];
        addPos++;
      }
    }//end: for(i)

    vertices = temp;
    temp = null;

    Arrays.sort(vertices, new VertexToTermCountComparator());
  }//end: joinMappings(VertexToTermMapping)

  /**
   * Write a {@link VertexToTermMapping} class to a file.
   * <p>
   * Writes the number of {@link VertexToTermCount} objects. Then, writes each object in the array to the file.
   * 
   * @param out {@link ObjectOutputStream} to write to.
   * @throws IOException
   */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException
  {
    // Write array length
    out.writeInt(vertices.length);

    // Write array of WordToVertexCount objects
    for(int i = 0; i < vertices.length; i++)
    {
      out.writeObject(vertices[i]);
    }
  }//end: writeObject(ObjectOutputStream)

  /**
   * Reads an {@link VertexToTermMapping} class from a file.
   * <p>
   * Reads the length of {@link VertexToTermCount} objects. Then, creates and populates an appropriate array of objects.
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    // Read array length
    int len = in.readInt();

    // Create and populate array
    vertices = new VertexToTermCount[len];
    for(int i = 0; i < len; i++)
    {
      vertices[i] = (VertexToTermCount) in.readObject();
    }//end: for(i)
  }//end: readObject(ObjectInputStream)
}
