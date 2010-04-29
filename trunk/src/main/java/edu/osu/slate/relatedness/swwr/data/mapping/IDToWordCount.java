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
 * Class used to Map a given ID to a set of words.
 * <p>
 * Uses an array of {@link WordCount} classes for the mapping.
 * 
 * @author weale
 *
 */
public class IDToWordCount implements Serializable {
  
 /**
   * 
   */
  private static final long serialVersionUID = 7700956091903015001L;

 /**
  * Pairs of (Word, Count) Mappings
  */
  private WordCount[] wordcounts;
  
 /**
  * ID for Mapping 
  */
  private int id;
  
 /**
  * Constructor.
  *  
  * @param i ID to Map to {@link WordCount} pairs
  */
  public IDToWordCount(int i) {
    id = i;
    wordcounts = new WordCount[0];
  }
  
 /**
  * Gets the ID for this mapping.
  * 
  * @return ID contained in this mapping
  */
  public int getID() {
    return id;
  }
  
 /**
  * Gets the array of {@link WordCount} pairs for the given word.
  * 
  * @return {@link WordCount} array contained in this mapping
  */
  public WordCount[] getIDCounts() {
    return wordcounts;
  }
  
 /**
  * Adds a new ID for the given word.
  * 
  * If the ID is found, we increment the existing count.
  * If the ID is not found, we extend the given {@link IDCount} array.
  * 
  * @param id New ID for the given word
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
  * 
  * @param out
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    
    // Write String
    out.writeInt(id);
    
    // Write array of IDCounts
    out.writeInt(wordcounts.length);
    for(int i=0; i<wordcounts.length; i++) {
      out.writeObject(wordcounts[i]);
    }
  }
  
 /**
  *  
  * @param in
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    
    // Read String
    id = in.readInt();
    
    // Read array of IDCounts
    int len = in.readInt();
    wordcounts = new WordCount[len];
    for(int i=0; i<len; i++) {
      wordcounts[i] = (WordCount) in.readObject();
    }//end: for(i)
  }//end: readObject

  public int compareTo(IDToWordCount wc2) {
    return this.id - wc2.id;
  }
}
