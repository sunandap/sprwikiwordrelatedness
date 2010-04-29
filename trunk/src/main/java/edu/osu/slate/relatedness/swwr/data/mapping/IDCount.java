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
 * Container for (ID, count) pairs.
 * 
 * @author weale
 * @version 1.0
 */
public class IDCount implements Serializable{
  
 /**
   * 
   */
  private static final long serialVersionUID = 1L;
/**
  * 
  */
  private int id, count;
  
 /**
  * Constructor.
  * 
  * @param i ID for counting.
  * @param c Initial count for ID.
  */
  public IDCount(int i, int c) {
    id = i;
    count = c;
  }
  
 /**
  * Gets the ID for the (ID, count) pair.
  * 
  * @return ID contained in this pair.
  */
  public int getID() {
    return id;
  }
  
 /**
  * Gets the count for the (ID, count) pair.
  * 
  * @return Count contained in this pair.
  */
  public int getCount() {
    return count;
  }
  
 /**
  * Sets a new count for the ID.
  * 
  * @param c New ID count.
  */
  public void setCount(int c) {
    count = c;
  }

 /**
  * Compares two {@link IDCount} objects.
  * 
  * @param idc2 Input {@link IDCount} object.
  * @return Comparison of the two objects.
  */
  public int compareTo(IDCount idc2) {
    return this.id - idc2.id;
  }
}
