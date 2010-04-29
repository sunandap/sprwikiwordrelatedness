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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Class to map a given word to a set of vertices.
 * <p>
 * Uses an array of {@link VertexCount} classes for the mapping.
 * 
 * @author weale
 *
 */
public class WordToVertexCount implements Serializable {
  
 /**
   * 
   */
  private static final long serialVersionUID = 7700956091903015001L;

 /**
  * Pairs of (vertex, count) Mappings
  */
  private VertexCount[] vertexCounts;
  
 /**
  * Word for Mapping 
  */
  private String word;
  
 /**
  * Constructor.
  *  
  * @param w Word to Map to {@link VertexCount} pairs.
  */
  public WordToVertexCount(String w) {
    word = w;
    vertexCounts = new VertexCount[0];
  }
  
 /**
  * Gets the Word for this mapping.
  * 
  * @return Word contained in this mapping
  */
  public String getWord() {
    return word;
  }
  
 /**
  * Gets the array of {@link VertexCount} pairs for the given word.
  * 
  * @return {@link VertexCount} array contained in this mapping
  */
  public VertexCount[] getVertexCounts() {
    return vertexCounts;
  }
  
 /**
  * Adds a new vertex for the given word.
  * 
  * If the vertex is found, we increment the existing count.
  * If the vertex is not found, we extend the given {@link VertexCount} array.
  * 
  * @param id New ID for the given word
  */
  public void addVertex(int v) {
    int pos = Arrays.binarySearch(vertexCounts, new VertexCount(v,1), new VertexCountComparator());
    if(pos >= 0)
    {
      //IDCount found!
      int count = vertexCounts[pos].getCount();
      vertexCounts[pos].setCount(count+1);
    }
    else
    {
      //Add new IDCount
      VertexCount[] tmp = new VertexCount[vertexCounts.length + 1];
      System.arraycopy(vertexCounts, 0, tmp, 0, vertexCounts.length);
      tmp[vertexCounts.length] = new VertexCount(v, 1);
      vertexCounts = tmp;
      Arrays.sort(vertexCounts, new VertexCountComparator());
    }
  }//end: addID
  
 /**
  * Compares two {@link WordToVertexCount} objects.
  *  
  * @param wc2 Input {@link WordToVertexCount} object.
  * @return Comparison between the two objects.
  */
  public int compareTo(WordToVertexCount wc2) {
    return this.word.compareTo(wc2.word);
  }
  
 /**
  * Write a {@link WordToVertexCount} class to a file.
  * <p>
  * Writes the number of {@link VertexCount} objects. Then, writes each object in the array to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    
    // Write String
    out.writeObject(word);
    
    // Write array of IDCounts
    out.writeInt(vertexCounts.length);
    for(int i = 0; i < vertexCounts.length; i++)
    {
      out.writeObject(vertexCounts[i]);
    }
  }
  
 /**
  * Reads a {@link WordToVertexCount} class from a file.
  * <p>
  * Reads the length of {@link VertexCount} objects. Then, creates and populates an appropriate array of objects.
  * 
  * @param in {@link ObjectInputStream} to read from.
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    
    // Read String
    word = (String) in.readObject();
    
    // Read array of IDCounts
    int len = in.readInt();
    vertexCounts = new VertexCount[len];
    
    for(int i = 0; i < len; i++)
    {
      vertexCounts[i] = (VertexCount) in.readObject();
    }//end: for(i)
  }//end: readObject
}
