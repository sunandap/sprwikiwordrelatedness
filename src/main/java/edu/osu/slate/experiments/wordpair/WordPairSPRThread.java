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
public class WordPairSPRThread  implements Runnable
{

  private String taskFile;
  private String task;

  private String resultAvgFile;
  private String resultAvgFileAll;

  private String resultMaxFile;
  private String resultMaxFileAll;
  
  private String resultVectFile;

  private Scanner s;
  private PrintWriter pwAvg, pwMax;
  private PrintWriter pwAvgAll, pwMaxAll;
  /* */
  private TermToVertexMapping term2Vertex;
  // Relatedness Algorithm
  private SourcedPageRank spr;
  
  /**
   * Sets the names of the files used in this task.
   */
  private void setFiles()
  {
    taskFile = Configuration.taskDir + task + ".txt";
    
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
   public WordPairSPRThread(TermToVertexMapping t2v, WikiGraph wg, String task)
   {
     term2Vertex = t2v;
     spr = new SourcedPageRank(wg);
     this.task = task;
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

  @Override
  public void run()
  {
    System.out.println("Setting Synonym Task: " + task);
    try 
    {
      s = new Scanner(new FileReader(taskFile));
      pwAvg = new PrintWriter(resultAvgFile + task + ".m");
      pwMax = new PrintWriter(resultMaxFile + task + ".m");
      
      pwAvgAll = new PrintWriter(resultAvgFileAll + task + ".m");
      pwMaxAll = new PrintWriter(resultMaxFileAll + task + ".m");
    }
    catch(IOException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    int i=0;
    
    StringBuffer humanVals = new StringBuffer("human = [");
    StringBuffer sprMaxVals = new StringBuffer("spr = [");
    StringBuffer sprAvgVals = new StringBuffer("spr = [");

    StringBuffer humanValsAll = new StringBuffer("human = [");
    StringBuffer sprMaxValsAll = new StringBuffer("spr = [");
    StringBuffer sprAvgValsAll = new StringBuffer("spr = [");

    while(s.hasNext())
    {
      String str = s.nextLine();
      String[] arr = str.split(",");

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
          d12 = Math.max(d12, relValues[v2]);
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
          d21 = Math.max(d21, relValues[v1]);
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
        sprMaxVals = sprMaxVals.append(max + ";");
        sprAvgVals = sprAvgVals.append(avg + ";");
        humanVals = humanVals.append(arr[2] + ";");
      }
      
      sprMaxValsAll = sprMaxValsAll.append(max + ";");
      sprAvgValsAll = sprAvgValsAll.append(avg + ";");
      humanValsAll = humanValsAll.append(arr[2] + ";");

      i++;
    }//end while(hasNext())
    System.out.println();
    
    sprMaxVals = sprMaxVals.deleteCharAt(sprMaxVals.length()-1);
    sprAvgVals = sprAvgVals.deleteCharAt(sprAvgVals.length()-1);
    humanVals = humanVals.deleteCharAt(humanVals.length()-1);
    
    sprMaxVals = sprMaxVals.append("];");
    sprAvgVals = sprAvgVals.append("];");
    humanVals = humanVals.append("];");
    
    pwAvg.println(sprAvgVals);
    pwAvg.println(humanVals);
    pwAvg.println("corr(human, spr, 'type', 'Pearson')");
    pwAvg.println("corr(human, spr, 'type', 'Spearman')");
    pwAvg.close();

    pwMax.println(sprMaxVals);
    pwMax.println(humanVals);
    pwMax.println("corr(human, spr, 'type', 'Pearson')");
    pwMax.println("corr(human, spr, 'type', 'Spearman')");
    pwMax.close();
    
    sprMaxValsAll = sprMaxValsAll.deleteCharAt(sprMaxValsAll.length()-1);
    sprAvgValsAll = sprAvgValsAll.deleteCharAt(sprAvgValsAll.length()-1);
    humanValsAll = humanValsAll.deleteCharAt(humanValsAll.length()-1);
    
    sprMaxValsAll = sprMaxValsAll.append("];");
    sprAvgValsAll = sprAvgValsAll.append("];");
    humanValsAll = humanValsAll.append("];");
    
    pwAvgAll.println(sprAvgValsAll);
    pwAvgAll.println(humanValsAll);
    pwAvgAll.println("corr(human, spr, 'type', 'Pearson')");
    pwAvgAll.println("corr(human, spr, 'type', 'Spearman')");
    pwAvgAll.close();

    pwMaxAll.println(sprMaxValsAll);
    pwMaxAll.println(humanValsAll);
    pwMaxAll.println("corr(human, spr, 'type', 'Pearson')");
    pwMaxAll.println("corr(human, spr, 'type', 'Spearman')");
    pwMaxAll.close();
  }
  

}
