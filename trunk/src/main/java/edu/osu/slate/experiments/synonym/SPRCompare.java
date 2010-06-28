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

import edu.osu.slate.experiments.Common;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.*;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
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
public class SPRCompare {

  private static String termVertexMapFile;
  private static String graphFile;
  private static String taskFile;
  private static String resultFile;
  private static WikiGraph wgp;
  private static TermToVertexMapping term2Vertex;
  
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
    
    taskFile = Configuration.taskDir + Configuration.task + ".txt";
    resultFile = Configuration.resultDir +
                 "synonym/" +
                 Configuration.type + "/" +
                 Configuration.task + "-(" +
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
    Configuration.transitions = "simple";
    setFiles();

    System.out.println("Setting Synonym Task: " + Configuration.task);
    Scanner s = new Scanner(new FileReader(taskFile));

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
    
    System.out.println("Opening Word To Vertex Mapping: " + 
        Configuration.mapsource + "-" + Configuration.stemming);
    term2Vertex = TermToVertexMapping.getMapping(termVertexMapFile);
//    try
//    {
//      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
//      word2Vertex = (TermToVertexMapping) in.readObject();
//      in.close();
//      word2Vertex.stem = Configuration.stemming.equals("t");
//    }
//    catch(Exception e)
//    {
//      System.err.println("Problem with file: " + wordVertexMapFile);
//      System.exit(1);
//    }
    

//    try
//    {
//      in = new ObjectInputStream(new FileInputStream(vertexWordMapFile));
//      vertex2Word = (VertexToWordMapping) in.readObject();
//      in.close();
//      
//    }
//    catch(Exception e)
//    {
//      System.err.println("Problem with file: " + vertexWordMapFile);
//      System.exit(1);      
//    }
    
    PrintWriter pw = new PrintWriter(resultFile);
    
    SourcedPageRank ngd = new SourcedPageRank(wgp);

    /* Run Synonym Task */
    int corr=0, att=0;
    int questionNum = 1;

    while(s.hasNext()) {

      /* Get Next Question */
      String str = s.nextLine();
      
      // Update progress bar(s)
      System.out.print(".");
      if(questionNum % 50 == 0)
      {
        System.out.println();
      }
      questionNum++;

      /* Split the input string */			
      String[] arr = str.split("\\|");
      for(int i=0;i<arr.length; i++) {
        arr[i] = arr[i].trim();
      }

      /* Make placeholders for relatedness values */
      double [] vals = new double[4];
      for(int i = 0; i < vals.length; i++)
      {
        vals[i] = -10;
      }

      VertexCount[] vcSource = getVertices(arr[0]);
      VertexCount[][] vcTerms = new VertexCount[4][];
      vcTerms[0] = getVertices(arr[1]);
      vcTerms[1] = getVertices(arr[2]);
      vcTerms[2] = getVertices(arr[3]);
      vcTerms[3] = getVertices(arr[4]);
      
      for(int x = 0; vcSource != null && x < vcSource.length; x++)
      {
          
        /* Get relatedness distributions for the ID */
        double [] sprValues = ngd.getRelatedness(vcSource[x].getVertex());
  
        /* For each potential confusion item */
        for(int i = 0; sprValues != null && i < vcTerms.length; i++)
        {
          for(int y = 0; vcTerms[i] != null && y < vcTerms[i].length; y++)
          {
            int currentVertex = vcTerms[i][y].getVertex();
                
            if(currentVertex >= 0) {
              /* For each potential ID for the surface form */
              vals[i] = Math.max(vals[i], sprValues[currentVertex]);
            }
            else {
              System.err.println("invalid: (" + i + ")\t" + currentVertex);
            }
          }//end: for(k)
        }//end: for(j)
      }//end: for(x)

      /* Check results */
  
      /* First item in the array is the answer */
      double answer = vals[0];
  
      /* Check it compared to the other items */
      if (answer > vals[1] && answer > vals[2] && answer > vals[3])
      {
        corr++;
      }
      
      if(vals[0] != -10 || vals[1] != -10 ||
         vals[2] != -10 || vals[3] != -10 )
      {
        att++;
      }
  
      for(int i=0; i<vals.length; i++)
      {
        pw.print(" " + vals[i]);
      }//end: for(i)
      pw.println();
      
    }//end while(hasNext())

    /* Print Results */
    System.out.println("* Correct   : " + corr);
    System.out.println("* Attempted : " + att);
    pw.close();
  }//end: main
  
 /**
  *  
  * @param term
  * @return
  */
  private static VertexCount[] getVertices(String term)
  {
    TermToVertexCount[] t1vc = term2Vertex.getVertexMappings(term);
    if(t1vc == null)
    {
      t1vc = term2Vertex.getSubTermVertexMappings(term);
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
