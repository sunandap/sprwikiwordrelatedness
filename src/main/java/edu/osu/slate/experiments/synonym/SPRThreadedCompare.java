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
public class SPRThreadedCompare
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

  // RDWP300 files
  private static String rdwp300TaskFile;
  private static String rdwp300ResultFile;
  private static String rdwp300VertexFile;  

  // RDWP1K files
  private static String rdwp1kTaskFile;
  private static String rdwp1kResultFile;
  private static String rdwp1kVertexFile;
  
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
    
    eslTaskFile = Configuration.taskDir + "ESL.txt";
    toeflTaskFile = Configuration.taskDir + "TOEFL.txt";
    rdwp300TaskFile = Configuration.taskDir + "RDWP300.txt";
    rdwp1kTaskFile = Configuration.taskDir + "RDWP1K.txt";
    
    eslResultFile = Configuration.resultDir +
                 "synonym/" +
                 Configuration.type + "/" +
                 "ESL-(" +
                 Configuration.mapsource + "-" +
                 Configuration.stemming + ")-(" +
                 data+transitionSource + ").txt";
    
    toeflResultFile = Configuration.resultDir +
                      "synonym/" +
                      Configuration.type + "/" +
                      "TOEFL-(" +
                      Configuration.mapsource + "-" +
                      Configuration.stemming + ")-(" +
                      data+transitionSource + ").txt";

    rdwp300ResultFile = Configuration.resultDir +
                        "synonym/" +
                        Configuration.type + "/" +
                        "RDWP300-(" +
                        Configuration.mapsource + "-" +
                        Configuration.stemming + ")-(" +
                        data+transitionSource + ").txt";
    
    rdwp1kResultFile = Configuration.resultDir +
                        "synonym/" +
                        Configuration.type + "/" +
                        "RDWP1K-(" +
                        Configuration.mapsource + "-" +
                        Configuration.stemming + ")-(" +
                        data+transitionSource + ").txt";
    
    eslVertexFile = Configuration.resultDir +
                    "synonym/" +
                    Configuration.type + "/vertex/" +
                    "ESL-(" +
                    Configuration.mapsource + "-" +
                    Configuration.stemming + ")-(" +
                    data+transitionSource + ").txt";

    toeflVertexFile = Configuration.resultDir +
                      "synonym/" +
                      Configuration.type + "/vertex/" +
                      "TOEFL-(" +
                      Configuration.mapsource + "-" +
                      Configuration.stemming + ")-(" +
                      data+transitionSource + ").txt";

    rdwp300VertexFile = Configuration.resultDir +
                        "synonym/" +
                        Configuration.type + "/vertex/" +
                        "RDWP300-(" +
                        Configuration.mapsource + "-" +
                        Configuration.stemming + ")-(" +
                        data+transitionSource + ").txt";

    rdwp1kVertexFile = Configuration.resultDir +
                       "synonym/" +
                       Configuration.type + "/vertex/" +
                       "RDWP1K-(" +
                       Configuration.mapsource + "-" +
                       Configuration.stemming + ")-(" +
                       data+transitionSource + ").txt";

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
    
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
    }
    
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
    (new Thread(new SPRThread(term2Vertex, wgp, eslTaskFile, eslResultFile, eslVertexFile))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, toeflTaskFile, toeflResultFile, toeflVertexFile))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, rdwp300TaskFile, rdwp300ResultFile, rdwp300VertexFile))).start();
    (new Thread(new SPRThread(term2Vertex, wgp, rdwp1kTaskFile, rdwp1kResultFile, rdwp1kVertexFile))).start();
  }//end: main
  
}
