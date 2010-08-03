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

package edu.osu.slate.experiments.wordpair.OSC;

import java.io.*;
import java.util.LinkedList;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.*;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

/**
 * Runs the standard PageRank-inspired relatedness metric on the given source.
 * <p>
 * Input arguments:
 * <ul>
 * <li>type (required): enwiki or enwiktionary</li>
 * <li>date (required)</li>
 * <li>source (required): M or english_language (wiktionary)</li>
 * <li>seed (optional): name of top-level category for category graph</li>
 * <li>countSource (optional): v or e</li>
 * <li>algorithm (optional)</li>
 * <li>task (required): name of the synonym task (RDWP300, TOEFL, ESL)</li>
 * </ul>
 * <p>
 * Uses the following classes:<br>
 * <ul>
 * <li> {@link AliasStrings}</li>
 * <li> {@link AliasSFToID}</li>
 * <li> {@link WikiGraph}</li>
 * </ul>
 * @author weale
 *
 */
public class DSPRExperiment
{

  // Names of the input mapping and graph files
  private static String termVertexMapFile;
  private static String graphFile;
  
  private static String task;
  private static int numSplits;
  
  // Mapping and Graph Objects
  private static WikiGraph wgp;
  private static TermToVertexMapping term2Vertex;
  
  /**
   * Sets the names of the files used in this program.
   */
  private static void setFiles() {
    
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;

    String transitionSource = "";
    if(!Configuration.transitions.equals(""))
    {
      transitionSource = "-" + Configuration.transitions;
    }

    termVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".tvc";
    
    graphFile = dir + data + transitionSource + ".wgp";   
    
    if(task.contains("MC30"))
    {
      numSplits = 8;
    }
    else if(task.contains("RG65"))
    {
      numSplits = 8;
    }
    else if(task.contains("WS"))
    {
      numSplits = 8;
    }
    else if(task.contains("YP130"))
    {
      numSplits = 8;
    }
  }
  
  /**
   * Main portion of the program
   * <p>
   * If no arguments are provided, uses defaults hard-coded by user.
   * 
   * @param args 0 or 1 argument accepted
   * @throws IOException General IO errors
   * @throws ClassNotFoundException Problems with the {@link WikiGraph} file
   */
  public static void main(String [] args) throws IOException, ClassNotFoundException {
    
    Configuration.parseConfigurationFile(args[1]);
    Configuration.baseDir = args[0];
    double cutoff = Double.parseDouble(args[2]);
    double beta = Double.parseDouble(args[3]);
    Configuration.SPRbeta = beta;
    Configuration.taskDir = Configuration.baseDir + "/" + Configuration.taskDir;
    task = "";
    setFiles();

    // Open Wiki Graph
    ObjectInputStream in = null;
    try
    {
      System.out.println("Opening Wiki Graph");
      in = new ObjectInputStream(new FileInputStream(graphFile));
      wgp = (WikiGraph) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + graphFile);
      e.printStackTrace();
      System.exit(1);
    }
    
    // Open Mapping
    System.out.println("Opening Word To Vertex Mapping: " + 
        Configuration.mapsource + "-" + Configuration.stemming);
    term2Vertex = TermToVertexMapping.getMapping(termVertexMapFile);
    term2Vertex.setCutoff(cutoff);
    System.out.println("Setting Cutoff: " + cutoff);
    
    // Start Task Threads
    LinkedList<String> tasks = new LinkedList<String>();
    for(int i=4; i<args.length; i++)
    {
      tasks.add(args[i]);
    }
    
    for(int currTask = 0; currTask < tasks.size(); currTask++)
    {
      task = tasks.get(currTask);
      setFiles();
      for(int part = 0; part < numSplits; part++)
      {
        (new Thread(new DSPRExperimentThread(term2Vertex, wgp, task, part, Configuration.SPRbeta))).start();
      }//end: for(part)
    }//end: for(currTask)
  }//end: main
  
}
