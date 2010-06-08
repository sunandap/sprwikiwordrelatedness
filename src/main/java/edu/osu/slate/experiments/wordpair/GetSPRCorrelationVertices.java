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
public class GetSPRCorrelationVertices
{

  private static String graphFile;

  private static String taskFile;

  private static String vertexFile;

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
                        "/vertex/" +
                        Configuration.type + "-" +
                        Configuration.date + "-" +
                        Configuration.graph + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming;
    
    vertexFile = resultFile;    
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
    
    SourcedPageRank spr = new SourcedPageRank(wgp);


    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      setFiles();
      
      System.out.println("Setting Synonym Task: " + Configuration.task);
      Scanner s = new Scanner(new FileReader(taskFile));
//      PrintWriter pwAvg = new PrintWriter(resultAvgFile + tasks[currTask] + ".csv");
//      PrintWriter pwMax = new PrintWriter(resultMaxFile + tasks[currTask] + ".csv");
      
      PrintWriter pwVertices = new PrintWriter(vertexFile + "-" + tasks[currTask] + ".csv");
//      PrintWriter pwMaxAll = new PrintWriter(resultMaxFileAll + tasks[currTask] + ".csv");
      
      int i=0;
      
//      StringBuffer humanVals = new StringBuffer("human = [");
//      StringBuffer sprMaxVals = new StringBuffer("spr = [");
//      StringBuffer sprAvgVals = new StringBuffer("spr = [");
//
//      StringBuffer humanValsAll = new StringBuffer("human = [");
//      StringBuffer sprMaxValsAll = new StringBuffer("spr = [");
//      StringBuffer sprAvgValsAll = new StringBuffer("spr = [");

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
            }
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
            if(relValues[v1] > d21)
            {
              maxV21 = v1;
              maxV22 = v2;
            }
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
          pwVertices.println(maxV11 + "," + maxV12 + "," + maxV21 + "," + maxV22);
//          sprMaxVals = sprMaxVals.append(max + ";");
//          sprAvgVals = sprAvgVals.append(avg + ";");
//          humanVals = humanVals.append(arr[2] + ";");
        }
        else
        {
          pwVertices.println("-1,-1,-1,-1");
        }
        
//        sprMaxValsAll = sprMaxValsAll.append(max + ";");
//        sprAvgValsAll = sprAvgValsAll.append(avg + ";");
//        humanValsAll = humanValsAll.append(arr[2] + ";");
  
        i++;
        System.out.print(".");
        if(i%50 == 0)
        {
          System.out.println();
        }
      }//end while(hasNext())
      System.out.println();
      pwVertices.close();
      
//      sprMaxVals = sprMaxVals.deleteCharAt(sprMaxVals.length()-1);
//      sprAvgVals = sprAvgVals.deleteCharAt(sprAvgVals.length()-1);
//      humanVals = humanVals.deleteCharAt(humanVals.length()-1);
//      
//      sprMaxVals = sprMaxVals.append("];");
//      sprAvgVals = sprAvgVals.append("];");
//      humanVals = humanVals.append("];");
      
//      pwAvg.println(sprAvgVals);
//      pwAvg.println(humanVals);
//      pwAvg.println("corr(human, spr, 'type', 'Pearson')");
//      pwAvg.println("corr(human, spr, 'type', 'Spearman')");
//      pwAvg.close();
//
//      pwMax.println(sprMaxVals);
//      pwMax.println(humanVals);
//      pwMax.println("corr(human, spr, 'type', 'Pearson')");
//      pwMax.println("corr(human, spr, 'type', 'Spearman')");
//      pwMax.close();
//      
//      sprMaxValsAll = sprMaxValsAll.deleteCharAt(sprMaxValsAll.length()-1);
//      sprAvgValsAll = sprAvgValsAll.deleteCharAt(sprAvgValsAll.length()-1);
//      humanValsAll = humanValsAll.deleteCharAt(humanValsAll.length()-1);
//      
//      sprMaxValsAll = sprMaxValsAll.append("];");
//      sprAvgValsAll = sprAvgValsAll.append("];");
//      humanValsAll = humanValsAll.append("];");
//      
//      pwAvgAll.println(sprAvgValsAll);
//      pwAvgAll.println(humanValsAll);
//      pwAvgAll.println("corr(human, spr, 'type', 'Pearson')");
//      pwAvgAll.println("corr(human, spr, 'type', 'Spearman')");
//      pwAvgAll.close();
//
//      pwMaxAll.println(sprMaxValsAll);
//      pwMaxAll.println(humanValsAll);
//      pwMaxAll.println("corr(human, spr, 'type', 'Pearson')");
//      pwMaxAll.println("corr(human, spr, 'type', 'Spearman')");
//      pwMaxAll.close();
    }//end: for(currTask)
    
    // Calculate the relatedness correlation
    //System.out.println(Pearson(X,Y));
    //System.out.println(LogPearson(X,Y));
    //System.out.println(Spearman.GetCorrelation(X, Y));
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
