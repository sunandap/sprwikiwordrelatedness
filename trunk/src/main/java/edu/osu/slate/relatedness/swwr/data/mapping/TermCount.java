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
 * Container for (term, count) pairs.
 * 
 * @author weale
 * @version 1.0
 */
public class TermCount implements Serializable
{
  
  private static final long serialVersionUID = 1L;  
  private int count;
  private String term;
  
 /**
  * Constructor.
  * 
  * @param t Term to count.
  * @param c Initial count for term.
  */
  public TermCount(String t, int c) {
    term = t;
    count = c;
  }
  
 /**
  * Gets the term for the (term, count) pair.
  * 
  * @return Term contained in this pair.
  */
  public String getTerm() {
    return term;
  }
  
 /**
  * Gets the count for (term, count) pair.
  * 
  * @return Count contained in this pair.
  */
  public int getCount() {
    return count;
  }
  
 /**
  * Sets a new count for the term.
  * 
  * @param c New term count
  */
  public void setCount(int c) {
    count = c;
  }
  
 /**
  * Compares two {@link TermCount} objects.
  * 
  * @param wc2 Input {@link TermCount} object.
  * @return Comparison of the two objects.
  */
  public int compareTo(TermCount wc2) {
    return this.term.compareTo(wc2.term);
  }
}