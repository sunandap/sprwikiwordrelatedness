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

package edu.osu.slate.experiments.wordpair;

import java.util.*;
import java.io.*;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

/**
 * Calculates the Pearson correlation coefficient between a given data set and the output of a given relatedness data set.
 * 
 * @author weale
 *
 */
public class GetSPRWordCounts
{
  
  private static String taskFile;

  private static double numTerms, numWords, numMaps;

  /* */
  private static String wordVertexMapFile;

  /* */
  private static TermToVertexMapping word2Vertex;

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
  private static void setFiles()
  {
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";

    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;
    
    wordVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".tvc";

    taskFile = Configuration.taskDir + Configuration.task + ".txt";
  }

  /**
   * 
   * @param args
   * @throws IOException
   * @throws ClassNotFoundException 
   */
  public static void main(String [] args) throws IOException, ClassNotFoundException
  {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/WordPair.xml");
    }

    setFiles();
    System.out.println("Opening Word To Vertex Mapping: " + 
        Configuration.mapsource + "-" + Configuration.stemming);    
    word2Vertex = TermToVertexMapping.getMapping(wordVertexMapFile);

    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      setFiles();
      
      TreeSet<String> ts = new TreeSet<String>();
      numTerms = 0;
      numWords = 0;
      numMaps = 0;
      
      System.out.println("Setting Synonym Task: " + Configuration.task);
      Scanner s = new Scanner(new FileReader(taskFile));
      
      while(s.hasNext())
      {
        String str = s.nextLine();
        String[] arr = str.split(",");
        ts.add(arr[0]);
        ts.add(arr[1]);
      }
      
      Iterator<String> it = ts.iterator();
      while(it.hasNext())
      {
        String term = it.next();
        VertexCount[] vc1 = getVertices(term);
        if(vc1 != null)
        {
          numTerms++;
          numMaps += vc1.length;
        }
      }//end while(hasNext())
      
      System.out.println("Total Terms: " + ts.size());
      System.out.println("Resolved   : " + numTerms);
      System.out.println("Total Words: " + numWords);
      System.out.println("Total Maps : " + numMaps);
    }//end: for(currTask)
  }//end: main
  
 /**
  *  
  * @param term
  * @return
  */
  private static VertexCount[] getVertices(String term)
  {
    TermToVertexCount[] t1vc = word2Vertex.getVertexMappings(term);
    if(t1vc == null)
    {
      t1vc = word2Vertex.getSubTermVertexMappings(term);
    }
    
    TreeSet<VertexCount> ts = new TreeSet<VertexCount>();
    for(int i = 0; t1vc != null && i < t1vc.length; i++)
    {
      VertexCount[] vc1 = t1vc[i].getVertexCounts();
      for(int j = 0; vc1 != null && j < vc1.length; j++)
      {
        ts.add(vc1[j]);
      }//end: for(j)
    }//end: for(i)

    if(ts.size() == 0)
    {
      return null;
    }
    
    numWords += t1vc.length;
    
    VertexCount[] vc = new VertexCount[ts.size()];
    Iterator<VertexCount> it = ts.iterator();
    int pos = 0;
    while(it.hasNext())
    {
      vc[pos] = it.next();
      pos++;
    }//end: while(it)
    
    return vc;
  }
  

}
