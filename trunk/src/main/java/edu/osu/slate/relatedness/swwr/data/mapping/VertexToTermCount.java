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


/**
 * Class used to Map a given vertex to a set of words.
 * <p>
 * Uses an array of {@link TermCount} classes for the mapping.
 * 
 * @author weale
 * @version 1.0
 */
public class VertexToTermCount implements Serializable
{
  
 /**
   * 
   */
  private static final long serialVersionUID = 7700956091903015001L;

 /**
  * Pairs of (Term, Count) Mappings
  */
  private TermCount[] wordcounts;
  
 /**
  * vertex for Mapping 
  */
  private int vertex;
  
 /**
  * Constructor.
  * <p>
  * Also creates a {@link TermCount} array of length 0.
  * 
  * @param v Vertex to map to {@link TermCount} pairs
  */
  public VertexToTermCount(int v)
  {
    vertex = v;
    wordcounts = new TermCount[0];
  }
  
 /**
  * Gets the vertex for this mapping.
  * 
  * @return Vertex contained in this mapping
  */
  public int getVertex()
  {
    return vertex;
  }
  
 /**
  * Gets the array of {@link TermCount} pairs for the given word.
  * 
  * @return {@link TermCount} array contained in this mapping
  */
  public TermCount[] getWordCounts() {
    return wordcounts;
  }
  
 /**
  * Adds a new term for the given vertex.
  * <p>
  * If the term is found, we increment the existing count.<br>
  * If the term is not found, we extend the given {@link TermCount} array.
  * 
  * @param w New Term for the given vertex
  */
  public void addTerm(String t)
  {
    int pos = Arrays.binarySearch(wordcounts, new TermCount(t,1), new TermCountComparator());
    if(pos >= 0)
    {
      //IDCount found!
      int count = wordcounts[pos].getCount();
      wordcounts[pos].setCount(count+1);
    }
    else
    {
      //Add new IDCount
      TermCount[] tmp = new TermCount[wordcounts.length + 1];
      System.arraycopy(wordcounts, 0, tmp, 0, wordcounts.length);
      tmp[wordcounts.length] = new TermCount(t, 1);
      wordcounts = tmp;
      Arrays.sort(wordcounts, new TermCountComparator());
    }
  }//end: addID
 /**
  * Adds a new term for the given vertex.
  * <p>
  * If the term is found, we increment the existing count the given number.<br>
  * If the term is not found, we extend the given {@link TermCount} array.
  * 
  * @param t Term to add.
  * @param c Count for the term.
  */
  public void addTerm(String t, int c) {
    int pos = Arrays.binarySearch(wordcounts, new TermCount(t,1), new TermCountComparator());
    if(pos >= 0)
    {
      //IDCount found!
      int count = wordcounts[pos].getCount();
      wordcounts[pos].setCount(count+c);
    }
    else
    {
      //Add new IDCount
      TermCount[] tmp = new TermCount[wordcounts.length + 1];
      System.arraycopy(wordcounts, 0, tmp, 0, wordcounts.length);
      tmp[wordcounts.length] = new TermCount(t, c);
      wordcounts = tmp;
      Arrays.sort(wordcounts, new TermCountComparator());
    }
  }//end: addID
  
 /**
  * Adds all {@link TermCount} for a given term to this object.
  * <p>
  * If the vertex numbers are not identical, nothing is added.
  * 
  * @param vtc {@link VertexToTermCount} object.
  */
  public void addObject(VertexToTermCount vtc)
  {
    for(int i = 0; vtc.vertex == vertex && i < vtc.wordcounts.length; i++)
    {
      String t = vtc.wordcounts[i].getTerm();
      int c = vtc.wordcounts[i].getCount();
      this.addTerm(t, c);
    }//end: for(i)
  }//end: addObject(VertexToWordCount)
  
 /**
  * Compares the two {@link VertexToTermCount} objects.
  * 
  * @param wc2 Input {@link VertexToTermCount} object.
  * @return Comparison of the two object IDs.
  */
  public int compareTo(VertexToTermCount wc2)
  {
    return this.vertex - wc2.vertex;
  }
  
 /**
  * Writes an {@link VertexToTermCount} class to a file.
  * <p>
  * Writes the number of {@link TermCount} objects. Then, outputs each individual object to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException
  {
    // Write Vertex Number
    out.writeInt(vertex);
    
    // Write array of IDCounts
    out.writeInt(wordcounts.length);
    for(int i = 0; i < wordcounts.length; i++)
    {
      out.writeObject(wordcounts[i]);
    }
  }
  
 /**
  * Reads an {@link VertexToTermCount} class from a file.
  * <p>
  * Reads the length of {@link TermCount} objects. Then, creates and populates an appropriate array of objects.
  * 
  * @param in {@link ObjectInputStream} to read from.
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    // Read Vertex Number
    vertex = in.readInt();
    
    // Read array of IDCounts
    int len = in.readInt();
    wordcounts = new TermCount[len];
    for(int i = 0; i < len; i++)
    {
      wordcounts[i] = (TermCount) in.readObject();
    }//end: for(i)
  }//end: readObject
}
