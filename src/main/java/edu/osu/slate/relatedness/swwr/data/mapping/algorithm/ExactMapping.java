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

package edu.osu.slate.relatedness.swwr.data.mapping.algorithm;

import java.util.Arrays;

import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCountComparator;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;

/**
 * Strictest lookup class using title information only.
 * <p>
 * This mapping class requires the input term to match the
 * seen mappings <i>exactly</i> in order to get a vertex mapping.
 * 
 * @author weale
 * @version 1.01
 */
public class ExactMapping extends TermToVertexMapping
{
  
  private static final long serialVersionUID = 7357697379024943638L;

 /**
  * Constructor.
  * 
  * @param tvc Array of {@link TermToVertexCount} object.
  */
  public ExactMapping(TermToVertexCount[] tvc)
  {
    super(tvc);
  }//end: ExactTitleMapping(TermtoVertexCount[])

  /**
   * Constructor.
   * <p>
   * Reads the {@link TermToVertexCount} array from the given <i>.tvc file</i>.
   * 
   * @param filename Input file name.
   */
   public ExactMapping(String filename)
   {
     super(filename);
   }//end: WordToIDMapping()

   /**
    * Constructor.
    * <p>
    * Reads the {@link TermToVertexCount} array from the given <i>.tvc file</i>.
    * 
    * @param filename Input file name.
    */
    public ExactMapping(TermToVertexMapping tvm)
    {
      super(tvm.terms);
    }//end: WordToIDMapping()
   
 /**
  * Gets the vertices mapped to a given term.
  * <p>
  * Returns null if the word is not found in the mapping function.
  *  
  * @param term Term to be mapped.
  * @return An array of {@link VertexCount} objects.
  */
  public TermToVertexCount[] getVertexMappings(String term)
  {
    int pos = Arrays.binarySearch(terms, new TermToVertexCount(term),
                                  new TermToVertexCountComparator());

    if(pos >= 0)
    { // FOUND!
      TermToVertexCount[] arr = new TermToVertexCount[1];
      arr[0] = new TermToVertexCount(term, terms[pos].getVertexCounts());
      return arr;
    }
    
    return null;
  }//end: getWordMappings(String)
}
