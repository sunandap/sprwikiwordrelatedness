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

import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;

/**
 * Interface for the term-to-vertex mappings.
 * 
 * @author weale
 */
public interface MappingInterface
{
  
 /**
  * Gets the vertices mapped to a given term.
  * <p>
  * Returns null if the term is not found in the mapping function.
  *  
  * @param term Term to be mapped.
  * @return An array of {@link VertexCount} objects.
  */
  public VertexCount[] getVertexMappings(String term);
  
 /** Gets the vertices mapped to the derived terms.
  * <p>
  * The original term was found to be not directly mappable,
  * therefore, this function breaks it into the individual words
  * (if a multi-word expression) and returns the mappings
  * for all the individual words.
  * <p>
  * If no appropriate mappings are found, null is returned.
  *  
  * @param term Term to be mapped.
  * @return An array of {@link TermToVertexCount} objects.
  */
  public TermToVertexCount[] getSubTermVertexMappings(String term);
  
}
