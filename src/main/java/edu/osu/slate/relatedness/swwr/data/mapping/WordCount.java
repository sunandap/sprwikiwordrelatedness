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
 * Container for (word, count) pairs.
 * 
 * @author weale
 * @version 1.0
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
  * Constructor.
  * 
  * @param w Word to count.
  * @param c Initial count for word.
  */
  public WordCount(String w, int c) {
    word = w;
    count = c;
  }
  
 /**
  * Gets the word for the (word, count) pair.
  * 
  * @return Word contained in this pair.
  */
  public String getWord() {
    return word;
  }
  
 /**
  * Gets the count for (word, count) pair.
  * 
  * @return Count contained in this pair.
  */
  public int getCount() {
    return count;
  }
  
 /**
  * Sets a new count for the word.
  * 
  * @param c New word count
  */
  public void setCount(int c) {
    count = c;
  }
  
 /**
  * Compares two {@link WordCount} objects.
  * 
  * @param wc2 Input {@link WordCount} object.
  * @return Comparison of the two objects.
  */
  public int compareTo(WordCount wc2) {
    return this.word.compareTo(wc2.word);
  }
}
