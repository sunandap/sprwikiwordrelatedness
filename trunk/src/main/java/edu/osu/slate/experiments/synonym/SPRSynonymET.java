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

import java.io.*;

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
public class SPRSynonymET
{

  // Names of the input mapping and graph files
  private static String termVertexMapFile;
  private static String graphFile;

  // ESL files
  private static String eslTaskFile;
  private static String eslResultFile;
  private static String eslVertexFile;

  // TOEFL files
  private static String toeflTaskFile;
  private static String toeflResultFile;
  private static String toeflVertexFile;
  
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
    
    eslTaskFile = Configuration.taskDir + "/ESL.part";
    toeflTaskFile = Configuration.taskDir + "/TOEFL.part";
    
    eslResultFile = Configuration.resultDir +
                 "/synonym/" +
                 Configuration.type + "/" +
                 "ESL-(" +
                 Configuration.mapsource + "-" +
                 Configuration.stemming + ")-(" +
                 data+transitionSource + ").part";
    
    toeflResultFile = Configuration.resultDir +
                      "/synonym/" +
                      Configuration.type + "/" +
                      "TOEFL-(" +
                      Configuration.mapsource + "-" +
                      Configuration.stemming + ")-(" +
                      data+transitionSource + ").part";
    
    eslVertexFile = Configuration.resultDir +
                    "/synonym/" +
                    Configuration.type + "/vertex/" +
                    "ESL-(" +
                    Configuration.mapsource + "-" +
                    Configuration.stemming + ")-(" +
                    data+transitionSource + ").part";

    toeflVertexFile = Configuration.resultDir +
                      "/synonym/" +
                      Configuration.type + "/vertex/" +
                      "TOEFL-(" +
                      Configuration.mapsource + "-" +
                      Configuration.stemming + ")-(" +
                      data+transitionSource + ").part";
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
    
    if(args.length == 2)
    {
      Configuration.parseConfigurationFile(args[1]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
    }
    Configuration.baseDir = args[0];
    
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
    
    // Start Task Threads
    (new Thread(new SPRThread(term2Vertex, wgp, eslTaskFile + "0",
                              eslResultFile + "0", eslVertexFile + "0"))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, eslTaskFile + "1",
                              eslResultFile + "1", eslVertexFile + "1"))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, eslTaskFile + "2",
                              eslResultFile + "2", eslVertexFile + "2"))).start();
    
    (new Thread(new SPRThread(term2Vertex, wgp, toeflTaskFile + "0",
                              toeflResultFile + "0", toeflVertexFile + "0"))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, toeflTaskFile + "1",
                              toeflResultFile + "1", toeflVertexFile + "1"))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, toeflTaskFile + "2",
                              toeflResultFile + "2", toeflVertexFile + "2"))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, toeflTaskFile + "3",
                              toeflResultFile + "3", toeflVertexFile + "3"))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, toeflTaskFile + "4",
                              toeflResultFile + "4", toeflVertexFile + "4"))).start();
  }//end: main
  
}
