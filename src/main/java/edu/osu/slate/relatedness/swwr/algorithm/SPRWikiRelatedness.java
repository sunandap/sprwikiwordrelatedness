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

import edu.osu.slate.relatedness.RelatednessTerm;
import edu.osu.slate.relatedness.WordRelatedness;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.WikiGraph;

/**
 * Main class for Sourced PageRank word relatedness.
 * <p>
 * Encapsulates {@link SourcedPageRank} graph and the {@link WordToVertexMapping} and {@link VertexToWordMapping} objects.
 * 
 * @author weale
 *
 */
public class SPRWikiRelatedness implements WordRelatedness {

 /**
  * 
  */
  private static final long serialVersionUID = 5047145851378614181L;

  private static String aliasStringFile;
  private static String aliasSFIDFile;
  private static String graphFile;
  private SourcedPageRank spr;
    
  public SPRWikiRelatedness(String dir, String wikiName) {
    setFiles(dir, wikiName, "");
    spr = new SourcedPageRank(graphFile);
    AliasStrings as = new AliasStrings(aliasStringFile);
    AliasSFToID sf2ID = new AliasSFToID(aliasSFIDFile); 
    
  }

 /**
  *  
  */
  public double getRelatedness(String w1, String w2) {
    // TODO Auto-generated method stub
    return 0;
  }

  public RelatednessTerm[] getRelatedness(String w) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

  /**
   * Sets the names of:<br>
   * 
   * <ul>
   * <li> {@link AliasStrings} file</li>
   * <li> {@link AliasSFToID} file</li>
   * <li> {@link WikiGraph} file</li>
   * <li> Synonym Task file</li>
   * </ul>
   */
  private static void setFiles(String dir, String prefix, String trans) {
    /* Set directory, data source */        
    aliasStringFile = dir + prefix + ".raf";
    aliasSFIDFile = dir + prefix + ".alf";
    graphFile = dir + prefix + trans + ".wgp";
  }
}
