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

package edu.osu.slate.relatedness.swwr.algorithm;

import edu.osu.slate.relatedness.swwr.data.*;

/**
 * 
 * @author weale
 *
 */
public class WordVertexLookup {

  private AliasStrings as;
  
  private AliasSFToID sf2ID; 

  private AliasIDToSF id2SF; 
  
  public WordVertexLookup(String aliasStringFile, String aliasSFIDFile) {
    as = new AliasStrings(aliasStringFile);
    sf2ID = new AliasSFToID(aliasSFIDFile);
    id2SF = new AliasIDToSF("");
  }
  
  public int[] getVertices(String word) {
    return sf2ID.getIDs(as.getID(word));
  }
  
  public String[] getWords(int vertex) {
    int [] arr = id2SF.getSFS(vertex);
    String[] words = new String[arr.length];
    for(int i = 0; i < words.length; i++) {
      words[i] = as.getSF(arr[i]);
    }
    return words;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
