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
 * Class to map a given word to a set of IDs.
 * <p>
 * Uses an array of {@link IDCount} classes for the mapping.
 * 
 * @author weale
 *
 */
public class WordToIDCount implements Serializable {
  
 /**
   * 
   */
  private static final long serialVersionUID = 7700956091903015001L;

 /**
  * Pairs of (ID, count) Mappings
  */
  private IDCount[] idcounts;
  
 /**
  * Word for Mapping 
  */
  private String word;
  
 /**
  * Constructor.
  *  
  * @param w Word to Map to {@link IDCount} pairs
  */
  public WordToIDCount(String w) {
    word = w;
    idcounts = new IDCount[0];
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
  * Gets the array of {@link IDCount} pairs for the given word.
  * 
  * @return {@link IDCount} array contained in this mapping
  */
  public IDCount[] getIDCounts() {
    return idcounts;
  }
  
 /**
  * Adds a new ID for the given word.
  * 
  * If the ID is found, we increment the existing count.
  * If the ID is not found, we extend the given {@link IDCount} array.
  * 
  * @param id New ID for the given word
  */
  public void addID(int id) {
    int pos = Arrays.binarySearch(idcounts, new IDCount(id,1), new IDCountComparator());
    if(pos >= 0)
    {
      //IDCount found!
      int count = idcounts[pos].getCount();
      idcounts[pos].setCount(count+1);
    }
    else
    {
      //Add new IDCount
      IDCount[] tmp = new IDCount[idcounts.length + 1];
      System.arraycopy(idcounts, 0, tmp, 0, idcounts.length);
      tmp[idcounts.length] = new IDCount(id, 1);
      idcounts = tmp;
      Arrays.sort(idcounts, new IDCountComparator());
    }
  }//end: addID
  
 /**
  * Compares two {@link WordToIDCount} objects.
  *  
  * @param wc2 Input {@link WordToIDCount} object.
  * @return Comparison between the two objects.
  */
  public int compareTo(WordToIDCount wc2) {
    return this.word.compareTo(wc2.word);
  }
  
 /**
  * Write a {@link WordToIDCount} class to a file.
  * <p>
  * Writes the number of {@link IDCount} objects. Then, writes each object in the array to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    
    // Write String
    out.writeObject(word);
    
    // Write array of IDCounts
    out.writeInt(idcounts.length);
    for(int i=0; i<idcounts.length; i++) {
      out.writeObject(idcounts[i]);
    }
  }
  
 /**
  * Reads a {@link WordToIDCount} class from a file.
  * <p>
  * Reads the length of {@link IDCount} objects. Then, creates and populates an appropriate array of objects.
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
    idcounts = new IDCount[len];
    for(int i=0; i<len; i++) {
      idcounts[i] = (IDCount) in.readObject();
    }//end: for(i)
  }//end: readObject
}
