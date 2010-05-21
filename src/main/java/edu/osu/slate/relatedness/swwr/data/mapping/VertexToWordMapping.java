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
 * Simplified lookup class for the {@link VertexToWordCount} class.
 * 
 * @author weale
 * @version 1.01
 */
public class VertexToWordMapping implements Serializable
{  
  /**
   * 
   */
  private static final long serialVersionUID = -4574771377730886056L;

  private VertexToWordCount[] vertices;
  
  public VertexToWordMapping(VertexToWordCount[] vc)
  {
    vertices = new VertexToWordCount[vc.length];
    for(int i = 0; i < vc.length; i++)
    {
      vertices[i] = vc[i];
    }
  }
  
  /**
   * Constructor.
   * <p>
   * Reads the {@link VertexToWordCount} array from the given <i>.iwc file</i>.
   * 
  * @param filename Input file name.
  */
  public VertexToWordMapping(String filename)
  {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
      
      // Read array length
      int len = in.readInt();
      
      // Create and initialize array
      vertices = new VertexToWordCount[len];
      for(int i = 0; i < len; i++)
      {
        vertices[i] = (VertexToWordCount) in.readObject();
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
  }//end: IDToWordMapping()
  
 /**
  * Gets the words mapped to a given vertex.
  * <p>
  * Returns null if vertex is not found.
  *  
  * @param v Vertex to be mapped.
  * @return An array of {@link WordCount} objects.
  */
  public WordCount[] getWordMappings(int v) {
    int pos = Arrays.binarySearch(vertices, new VertexToWordCount(v), new VertexToWordCountComparator());

    if(pos >= 0)
    { // FOUND!
      return vertices[pos].getWordCounts();
    }
    
    return null;
  }//end: getWordMappings(int)
  
  public void joinMappings(VertexToWordMapping vwm)
  {
    int numToAdd = 0;
    boolean[] addMe = new boolean[vwm.vertices.length];
    
    for(int i = 0; i < vwm.vertices.length; i++)
    {
      int pos = Arrays.binarySearch(vertices, vwm.vertices[i], new VertexToWordCountComparator());

      if(pos >= 0)
      {
        // Add to old Object
        vertices[pos].addObject(vwm.vertices[i]);
        addMe[i] = false;
      }
      else
      {
        numToAdd++;
        addMe[i] = true;
      }
    }//end: for(i)
    VertexToWordCount[] temp = new VertexToWordCount[vertices.length + numToAdd];

    System.arraycopy(vertices, 0, temp, 0, vertices.length);
    int addPos = vertices.length;
    
    for(int i = 0; i < vwm.vertices.length; i++)
    {
      if(addMe[i])
      {
        temp[addPos] = vwm.vertices[i];
        addPos++;
      }
    }//end: for(i)
    
    vertices = temp;
    temp = null;

    Arrays.sort(vertices, new VertexToWordCountComparator());
  }//end: joinMappings(VertexToWordMapping)
  
  /**
   * Write a {@link VertexToWordMapping} class to a file.
   * <p>
   * Writes the number of {@link VertexToWordCount} objects. Then, writes each object in the array to the file.
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
    * Reads an {@link VertexToWordMapping} class from a file.
    * <p>
    * Reads the length of {@link VertexToWordCount} objects. Then, creates and populates an appropriate array of objects.
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
      vertices = new VertexToWordCount[len];
      for(int i = 0; i < len; i++)
      {
        vertices[i] = (VertexToWordCount) in.readObject();
      }//end: for(i)
    }//end: readObject(ObjectInputStream)
}
