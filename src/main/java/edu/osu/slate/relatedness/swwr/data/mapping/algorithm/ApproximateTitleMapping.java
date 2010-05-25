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

import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;

import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCountComparator;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;

/**
 * Lookup algorithm for Title-based term-to-vertex mappings.
 * <p>
 * This mapping class first checks to see if there's an exact
 * match with the given terms (stemmed or not).  If so,
 * that vertex mapping is returned.
 * <p>
 * If not, it backs off to the individual words in the given
 * term (if the term is a multi-word expression).
 * <p>
 * If no match is found, null is returned.
 * 
 * @author weale
 * @version 1.01
 */
public class ApproximateTitleMapping extends TermToVertexMapping
{
  private static final long serialVersionUID = 5395182204888235246L;

 /**
  * Constructor.
  * 
  * @param tvc Array of {@link TermToVertexCount} objects.
  */
  public ApproximateTitleMapping(TermToVertexCount[] tvc)
  {
    super(tvc);
  }//end: ApproximateTitleMapping(TermToVertexCount[])
  
 /**
  * Constructor.
  * <p>
  * Reads the {@link TermToVertexCount} array from the given <i>.wic file</i>.
  * 
  * @param filename Input file name.
  */
  public ApproximateTitleMapping(String filename)
  {
    super(filename);
  }//end: ApproximateTitleMapping(String)
  
 /**
  * Gets the vertices mapped to a given term.
  * <p>
  * Returns null if the term is not found in the mapping function.
  *  
  * @param term Term to be mapped.
  * @return An array of {@link VertexCount} objects.
  */
  public VertexCount[] getVertexMappings(String term)
  {
    if(stem)
    {
      term = PorterStemmerTokenizerFactory.stem(term);
    }
    
    int pos = Arrays.binarySearch(terms, new TermToVertexCount(term),
                                  new TermToVertexCountComparator());

    if(pos >= 0)
    { // FOUND!
      return terms[pos].getVertexCounts();
    }
    else
    {
      return null;
    }
  }
  
 /**
  * Gets the vertices mapped to the derived terms.
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
  public TermToVertexCount[] getSubTermVertexMappings(String term)
  {
    /* String is not valid as-is.
     * 
     * Replace and split words into parts.
     */
    term = term.replace('-', ' ');
    String [] arr = term.split(" ");

    /* Remove common words */
    for(int i = 0; i < arr.length; i++)
    {
      if(arr[i].equals("and"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("or"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("to"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("be"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("the"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("a"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("of"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("on"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("in"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("for"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("with"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("by"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("into"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("an"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("is"))
      {
        arr[i] = "";
      }
      else if(arr[i].equals("no"))
      {
        arr[i] = "";
      }
    }//end: for(i)

    /* Create String array without common terms */
    int size = 0;
    for(int i = 0; i < arr.length; i++)
    {
      // Check for valid term
      if(!arr[i].equals("") && getVertexMappings(arr[i]) != null)
      {
        size++;
      }
    }//end: for(i)
    
    if(size == 0)
    {
      return null;
    }
    
    /* At least one valid term was found */
    TermToVertexCount[] words = new TermToVertexCount[size];
    int pos = 0;
    for(int i = 0; i < arr.length; i++)
    {
      // Check for valid term
      if(!arr[i].equals(""))
      {
        VertexCount[] temp = getVertexMappings(arr[i]);
        if(temp != null)
        {
          // Term maps to vertices
          // Create new TermToVertexCount w/ vertex mappings
          TermToVertexCount tvc = new TermToVertexCount(arr[i]);
          for(int j = 0; j < temp.length; j++)
          {
            tvc.addVertex(temp[j].getVertex(), temp[j].getCount());
          }//end: for(j)
          
          // Add to the returned object
          words[pos] = tvc;
          pos++;
        }//end: if(temp)
      }//end: if(!arr[i])
    }//end: for(i)
    return words;    
  }//end: getSubTermVertexMappings(String)
  
}
