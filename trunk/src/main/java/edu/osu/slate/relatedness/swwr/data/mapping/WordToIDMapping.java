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

import java.io.*;
import java.util.Arrays;

/**
 * Simplified lookup class for the {@link WordToIDCount} class.
 * 
 * @author weale
 * @version 1.0
 */
public class WordToIDMapping {
  private WordToIDCount[] words;
  
 /**
  * Constructor.
  * <p>
  * Reads the {@link WordToIDCount} array from the given <i>.wic file</i>.
  * 
  * @param filename Input file name.
  */
  public WordToIDMapping(String filename)
  {
    try
    {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
      
      // Read array length
      int len = in.readInt();
      
      // Create and initialize array
      words = new WordToIDCount[len];
      for(int i = 0; i < len; i++)
      {
        words[i] = (WordToIDCount) in.readObject();
      }//end: for(i)
      
      in.close();
    }//end: try{}
    catch(IOException e)
    {
      System.err.println("Problem reading from file: " + filename);
      e.printStackTrace();
      System.exit(1);
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Problem with class conversion from file: " + filename);
      e.printStackTrace();
      System.exit(1);
    }
  }//end: WordToIDMapping()
  
 /**
  * Gets the IDs mapped to a given word.
  * <p>
  * Returns null if the word is not found in the mapping function.
  *  
  * @param word Word to be mapped.
  * @return An array of {@link IDCount} objects.
  */
  public IDCount[] getIDMappings(String word) {
    int pos = Arrays.binarySearch(words, new WordToIDCount(word),
                                  new WordToIDCountComparator());

    if(pos >= 0)
    { // FOUND!
      return words[pos].getIDCounts();
    }
     
    return null;
  }//end: getWordMappings(int)
}
