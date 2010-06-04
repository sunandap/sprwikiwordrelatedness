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

package edu.osu.slate.experiments.synonym;

import java.util.*;
import java.io.*;

import edu.osu.slate.relatedness.Configuration;
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
public class GetSPRSourceTermCounts
{
  
  private static String taskFile;

  private static double numSourceTerms, numSourceWords, numSourceMaps;
  
  private static double numTargetTerms, numTargetWords, numTargetMaps;

  private static boolean evalSource;
  
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

    String[] tasks = {"ESL", "TOEFL", "RDWP300", "RDWP1K"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      setFiles();
      
      TreeSet<String> source = new TreeSet<String>();
      numSourceTerms = 0;
      numSourceWords = 0;
      numSourceMaps = 0;
      
      TreeSet<String> target = new TreeSet<String>();
      numTargetTerms = 0;
      numTargetWords = 0;
      numTargetMaps = 0;
      
      System.out.println("Setting Synonym Task: " + Configuration.task);
      Scanner s = new Scanner(new FileReader(taskFile));
      
      while(s.hasNext())
      {
        String str = s.nextLine();
        String[] arr = str.split("\\|");
        source.add(arr[0].trim());
        target.add(arr[1].trim());
        target.add(arr[2].trim());
        target.add(arr[3].trim());
        target.add(arr[4].trim());
      }
      s.close();
      
      evalSource = true;
      Iterator<String> it = source.iterator();
      while(it.hasNext())
      {
        String term = it.next();
        VertexCount[] vc1 = getVertices(term);
        if(vc1 != null)
        {
          numSourceTerms++;
          numSourceMaps += vc1.length;
        }
      }//end while(hasNext())
      
      evalSource = false;
      it = target.iterator();
      while(it.hasNext())
      {
        String term = it.next();
        VertexCount[] vc1 = getVertices(term);
        if(vc1 != null)
        {
          numTargetTerms++;
          numTargetMaps += vc1.length;
        }
      }//end while(hasNext())
      System.out.println("Source");
      System.out.println("Total Terms: " + source.size());
      System.out.println("Resolved   : " + numSourceTerms);
      System.out.println("Total Words: " + numSourceWords);
      System.out.println("Total Maps : " + numSourceMaps);
      
      System.out.println("Target");
      System.out.println("Total Terms: " + target.size());
      System.out.println("Resolved   : " + numTargetTerms);
      System.out.println("Total Words: " + numTargetWords);
      System.out.println("Total Maps : " + numTargetMaps);
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
    
    if(evalSource)
    {
      numSourceWords += t1vc.length;
    }
    else
    {
      numTargetWords += t1vc.length;
    }
    
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
