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
 * Uses an array of {@link WordCount} classes for the mapping.
 * 
 * @author weale
 * @version 1.0
 */
public class VertexToWordCount implements Serializable {
  
 /**
   * 
   */
  private static final long serialVersionUID = 7700956091903015001L;

 /**
  * Pairs of (Word, Count) Mappings
  */
  private WordCount[] wordcounts;
  
 /**
  * vertex for Mapping 
  */
  private int vertex;
  
 /**
  * Constructor.
  * <p>
  * Also creates a {@link WordCount} array of length 0.
  * 
  * @param v Vertex to map to {@link WordCount} pairs
  */
  public VertexToWordCount(int v) {
    vertex = v;
    wordcounts = new WordCount[0];
  }
  
 /**
  * Gets the vertex for this mapping.
  * 
  * @return Vertex contained in this mapping
  */
  public int getVertex() {
    return vertex;
  }
  
 /**
  * Gets the array of {@link WordCount} pairs for the given word.
  * 
  * @return {@link WordCount} array contained in this mapping
  */
  public WordCount[] getWordCounts() {
    return wordcounts;
  }
  
 /**
  * Adds a new word for the given vertex.
  * <p>
  * If the word is found, we increment the existing count.<br>
  * If the word is not found, we extend the given {@link WordsCount} array.
  * 
  * @param w New Word for the given vertex
  */
  public void addWord(String w) {
    int pos = Arrays.binarySearch(wordcounts, new WordCount(w,1), new WordCountComparator());
    if(pos >= 0)
    {
      //IDCount found!
      int count = wordcounts[pos].getCount();
      wordcounts[pos].setCount(count+1);
    }
    else
    {
      //Add new IDCount
      WordCount[] tmp = new WordCount[wordcounts.length + 1];
      System.arraycopy(wordcounts, 0, tmp, 0, wordcounts.length);
      tmp[wordcounts.length] = new WordCount(w, 1);
      wordcounts = tmp;
      Arrays.sort(wordcounts, new WordCountComparator());
    }
  }//end: addID
  
 /**
  * Compares the two {@link VertexToWordCount} objects.
  * 
  * @param wc2 Input {@link VertexToWordCount} object.
  * @return Comparison of the two object IDs.
  */
  public int compareTo(VertexToWordCount wc2) {
    return this.vertex - wc2.vertex;
  }
  
 /**
  * Writes an {@link VertexToWordCount} class to a file.
  * <p>
  * Writes the number of {@link WordCount} objects. Then, outputs each individual object to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    
    // Write String
    out.writeInt(vertex);
    
    // Write array of IDCounts
    out.writeInt(wordcounts.length);
    for(int i=0; i<wordcounts.length; i++) {
      out.writeObject(wordcounts[i]);
    }
  }
  
 /**
  * Reads an {@link VertexToWordCount} class from a file.
  * <p>
  * Reads the length of {@link WordCount} objects. Then, creates and populates an appropriate array of objects.
  * 
  * @param in {@link ObjectInputStream} to read from.
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    
    // Read String
    vertex = in.readInt();
    
    // Read array of IDCounts
    int len = in.readInt();
    wordcounts = new WordCount[len];
    for(int i=0; i<len; i++) {
      wordcounts[i] = (WordCount) in.readObject();
    }//end: for(i)
  }//end: readObject


}
