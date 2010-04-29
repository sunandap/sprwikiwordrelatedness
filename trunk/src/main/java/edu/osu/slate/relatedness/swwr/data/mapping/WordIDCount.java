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

/**
 * Class to contain
 * 
 * @author weale
 *
 */
public class WordIDCount implements Serializable {
  
 /**
   * 
   */
  private static final long serialVersionUID = 7700956091903015001L;
/**
  * 
  */
  private int id, count;
  private String word;
  
 /**
  *  
  * @param w Word to map
  * @param i ID to 
  * @param c
  */
  public WordIDCount(String w, int i, int c) {
    word = w;
    id = i;
    count = c;
  }
  
 /**
  * Gets the Word for the (Word,ID) pair.
  * 
  * @return Word contained in this mapping
  */
  public String getWord() {
    return word;
  }
  
 /**
  * Gets the ID for the (Word,ID) pair.
  * 
  * @return ID contained in this mapping
  */
  public int getID() {
    return id;
  }
  
 /**
  * Gets the count for the (Word,ID) pair.
  * 
  * @return (Word,ID) pair count in the data set
  */
  public int getCount() {
    return count;
  }
  
 /**
  * Sets a new (Word,ID) count for the given pair.
  * 
  * @param c New (Word,ID) count
  */
  public void setCount(int c) {
    count = c;
  }
  
 /**
  * 
  * @param out
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.writeObject(word);
    out.writeInt(id);
    out.writeInt(count);
  }
 /**
  *  
  * @param in
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    word = (String) in.readObject();
    id = in.readInt();
    count = in.readInt();
  }
}
