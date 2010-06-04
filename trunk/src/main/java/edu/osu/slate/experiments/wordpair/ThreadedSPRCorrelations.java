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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class ThreadedSPRCorrelations
{

  private static String graphFile;

  private static String taskFile;

  private static String resultAvgFile;

  private static String resultMaxFile;

  private static String resultAvgFileAll;

  private static String resultMaxFileAll;
  
  private static String resultVectFile;

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
    
    graphFile = dir + data + ".wgp";
    
    wordVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".tvc";

    taskFile = Configuration.taskDir + Configuration.task + ".txt";
    
    String resultFile = Configuration.resultDir +
                        "/wordpair/" +
                        Configuration.type + "/" +
                        Configuration.type + "_" +
                        Configuration.date + "_" +
                        Configuration.graph + "_" +
                        Configuration.mapsource + "_" + 
                        Configuration.stemming;
    
    resultAvgFile = resultFile + "_avg_";
    
    resultMaxFile = resultFile + "_max_";

    resultAvgFileAll = resultFile + "_avg_all_";
    
    resultMaxFileAll = resultFile + "_max_all_";

    resultVectFile = resultFile + "_vect_";
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
    
    // Set up Wiki graph and relatedness algorithm
    System.out.println("Opening Wiki Graph");
   
    ObjectInputStream in = null;
    WikiGraph wgp = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(graphFile));
      wgp = (WikiGraph) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    ExecutorService execSvc = Executors.newFixedThreadPool(4);

    /*
     * place six tasks in the work queue for the thread pool
     */

    String[] tasks = {"MC30", "WS1", "WS2", "YP130", "RG65"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
        execSvc.execute( new WordPairSPRThread(word2Vertex, wgp, tasks[currTask]) );
    }
    /*
     * prevent other tasks from being added to the queue
     */
    execSvc.shutdown();
  }//end: main
}
