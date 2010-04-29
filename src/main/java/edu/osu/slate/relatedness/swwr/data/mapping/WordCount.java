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

import java.io.Serializable;

/**
 * Class to contain
 * 
 * @author weale
 *
 */
public class WordCount implements Serializable {
  
 /**
  * 
  */
  private static final long serialVersionUID = 1L;
  
 /**
  * 
  */
  private int count;
  private String word;
  
 /**
  *  
  * @param w Word to map
  * @param i ID to 
  * @param c
  */
  public WordCount(String w, int c) {
    word = w;
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
  * Compares two {@link WordCount} classes.
  * 
  * @param wc2 Input {@link WordCount}
  * @return Comparison of the two words
  */
  public int compareTo(WordCount wc2) {
    return this.word.compareTo(wc2.word);
  }
}
