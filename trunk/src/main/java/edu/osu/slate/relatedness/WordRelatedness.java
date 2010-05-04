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

package edu.osu.slate.relatedness;

/**
 * Interface for determining relatedness from input word(s).
 * 
 * @author weale
 * @version 0.9
 * 
 */
public interface WordRelatedness {

 /**
  * Calculate the relatedness between a pair of words.
  * <p>
  * Relatedness value returned is in the range [0..1]
  * 
  * @param w1 Initial word to compare
  * @param w2 Secondary word to compare
  * @return Relatedness value
  */
  public double getRelatedness(String w1, String w2);

 /**
  * Calculate the relatedness value between a word and all other words for the data source.
  * <p>
  * Returns the values as {@link RelatednessTerm} elements.
  * 
  * @param w Word for comparison
  * @return Array of RelatednessTerms
  */
  public RelatednessTerm[] getRelatedness(String w);
}
