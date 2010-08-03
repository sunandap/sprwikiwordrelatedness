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

import java.util.*;
import java.io.*;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.DecoupledSPR;
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
public class DSPRExperimentThread extends Thread
{

  private String taskFile;
  private String task;
  private int part;

  private String resultAvgFile;
  private String resultAvgFileAll;

  private String resultMaxFile;
  private String resultMaxFileAll;
  
  private String resultHumanFile;
  private String resultHumanFileAll;
  
  private String resultVectFile;

  private Scanner s;
  private PrintWriter pwAvg, pwMax;
  private PrintWriter pwAvgAll, pwMaxAll;
  private PrintWriter pwHuman, pwHumanAll;
  private PrintWriter vertices;
  
  /* */
  private TermToVertexMapping term2Vertex;
  // Relatedness Algorithm
  private DecoupledSPR spr;
  
  /**
   * Sets the names of the files used in this task.
   */
  private void setFiles()
  {
    taskFile = Configuration.taskDir + "/" + task + ".part" + part;
    
    String transitionSource = "";
    if(!Configuration.transitions.equals(""))
    {
      transitionSource = Configuration.transitions + "-";
    }
    
    String resultFile = Configuration.resultDir +
                        "/wordpair/" +
                        Configuration.type + "/" +
                        Configuration.type + "-" +
                        Configuration.date + "-" +
                        Configuration.graph + "-" +
                        transitionSource +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming;
    
    resultAvgFile = resultFile + "-avg-";    
    resultMaxFile = resultFile + "-max-";
    resultHumanFile = resultFile + "-human-";

    resultAvgFileAll = resultFile + "-avg-all-";
    resultMaxFileAll = resultFile + "-max-all-";
    resultHumanFileAll = resultFile + "-human-all-";

    resultVectFile = Configuration.resultDir +
                     "/wordpair/" +
                     Configuration.type + "/" +
                     "/vertex/" +
                     Configuration.type + "-" +
                     Configuration.date + "-" +
                     Configuration.graph + "-" +
                     transitionSource +
                     Configuration.mapsource + "-" + 
                     Configuration.stemming + "-" +
                     task + ".part" + part;
  }
  
  /**
   * Constructor.
   * <p>
   * Initializes a {@link SourcedPageRank} object from the given graph
   * and writes relatedness/vertex information to files.
   * 
   * @param t2v {@link TermToVertexMapping} containing mapping algorithm.
   * @param spr {@link WikiGraph} with Wiki graph.
   * @param tFile Task file name.
   * @param rFile Results file name.
   * @param vFile Vertex number file name.
   */
   public DSPRExperimentThread(TermToVertexMapping t2v, WikiGraph wg, String task, int part, double SPRbeta)
   {
     term2Vertex = t2v;
     spr = new DecoupledSPR(wg, SPRbeta);
     this.task = task;
     this.part = part;
     setFiles();
   }
  
  /**
   * Gets all vertices for the given task term. 
   *  
   * @param term Task term.
   * @return Array of {@link VertexCount} objects.
   */
  private VertexCount[] getVertices(String term)
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

  public void run()
  {
    System.out.println("Setting Synonym Task: " + task + ".part" + part);
    try 
    {
      s = new Scanner(new FileReader(taskFile));
      pwAvg = new PrintWriter(resultAvgFile + task + ".part"+ part);
      pwMax = new PrintWriter(resultMaxFile + task + ".part"+ part);
      
      pwAvgAll = new PrintWriter(resultAvgFileAll + task + ".part"+ part);
      pwMaxAll = new PrintWriter(resultMaxFileAll + task + ".part"+ part);
      
      pwHuman = new PrintWriter(resultHumanFile + task + ".part"+ part);
      pwHumanAll = new PrintWriter(resultHumanFileAll + task + ".part"+ part);
      
      vertices = new PrintWriter(resultVectFile);
    }
    catch(IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    int i=0;
    
    while(s.hasNext())
    {
      String str = s.nextLine();
      String[] arr = str.split(",");
      
      int maxV11 = -1, maxV12 = -1;
      int maxV21 = -1, maxV22 = -1;

      double d12 = -10.0;
      VertexCount[] vc1 = getVertices(arr[0]);
      VertexCount[] vc2 = getVertices(arr[1]);
      
      for(int x = 0; vc1 != null && x <vc1.length; x++)
      {
        int v1 = vc1[x].getVertex();
        double[] relValues = spr.getRelatedness(v1);
          
        for(int y = 0; vc2 != null && y <vc2.length; y++)
        {
          int v2 = vc2[y].getVertex();
          if(relValues[v2] > d12)
          {
            maxV11 = v1;
            maxV12 = v2;
            d12 = relValues[v2];
          }
        }
      }//end: for(x)
      
      double d21 = -10;
      for(int x = 0; vc2 != null && x <vc2.length; x++)
      {
        int v2 = vc2[x].getVertex();
        double[] relValues = spr.getRelatedness(v2);
          
        for(int y = 0; vc1 != null && y <vc1.length; y++)
        {
          int v1 = vc1[y].getVertex();
          if(relValues[v1] > d21)
          {
            maxV21 = v1;
            maxV22 = v2;
            d21 = relValues[v1];
          }
        }
      }//end: for(x)
      double max = Math.max(d12, d21);        

      double avg = -10;
      
      // Set the average if both values are valid
      if(d21 != -10 && d12 != -10)
      {
        avg = (d12 + d21) / 2.0;          
      }
      else
      {// Set the average to the valid values
        avg = max;
      }
      
      if(max != -10)
      {
        pwMax.println(max);
        pwAvg.println(avg);
        pwHuman.println(arr[2]);
        vertices.println(maxV11 + "," + maxV12 + "," + maxV21 + "," + maxV22);
      }
      else
      {
        vertices.println("-1,-1,-1,-1");
      }
      
      pwMaxAll.println(max);
      pwAvgAll.println(avg);
      pwHumanAll.println(arr[2]);
      i++;
    }//end while(hasNext())
    vertices.close();
    pwAvg.close();
    pwMax.close();
    pwAvgAll.close();
    pwMaxAll.close();
    pwHuman.close();
    pwHumanAll.close();
    System.out.println("Finished Synonym Task: " + task + ".part" + part);
  }
}
