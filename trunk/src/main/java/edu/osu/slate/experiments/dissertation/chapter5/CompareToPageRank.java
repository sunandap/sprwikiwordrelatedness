package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.PageRank;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

public class CompareToPageRank {

  private static String task;
  
  private static int numSplits;
  
  private static String resultAvgFile, resultAvgFileAll;
  private static String resultMaxFile, resultMaxFileAll;
  private static String resultHumanFile, resultHumanFileAll;
  private static String graphFile;
  
  private static String resultVectFile;
  
  private static void setFiles()
  {
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
                     Configuration.stemming + "-";
    
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;
    
    graphFile = dir + data + transitionSource + ".wgp"; 
    
    if(task.equals("MC30"))
    {
      numSplits = 3;
    }
    else if(task.equals("RG65"))
    {
      numSplits = 5;
    }
    else if(task.equals("WS1"))
    {
      numSplits = 8;
    }
    else if(task.equals("WS2"))
    {
      numSplits = 8;
    }
    else if(task.equals("YP130"))
    {
      numSplits = 8;
    }
  }
  
  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException
  {
    if(args.length > 0)
    {
      Configuration.parseConfigurationFile(args[1]);
      Configuration.resultDir = args[0];
    }
    else 
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/WordPair.xml");
      Configuration.resultDir = "/scratch/weale/results/ppr2results/";
    }
    
    task = "";
    setFiles();
    PageRank pr = null;
    try
    {
      System.out.println("Reading PageRank Values");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(graphFile));
      WikiGraph wg = (WikiGraph) in.readObject();  
      pr = new PageRank(wg);
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    

    
    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      task = tasks[currTask];
      setFiles();
      String transitionSource = "";
      if(!Configuration.transitions.equals(""))
      {
        transitionSource = Configuration.transitions + "_";
      }
      
      // Open output files
      PrintWriter pwAvg = new PrintWriter(resultAvgFile.replace('-', '_') + task + "_pr.m");
      PrintWriter pwMax = new PrintWriter(resultMaxFile.replace('-', '_') + task + "_pr.m");
      
      // Set-up string buffers
      StringBuffer humanValues = new StringBuffer("human = [");
      StringBuffer maxValues = new StringBuffer("max = [");
      StringBuffer avgValues = new StringBuffer("avg = [");
      StringBuffer prValues = new StringBuffer("pr = [");
      
      int div = 1;
      if(task.contains("WS"))
      {
        div = 2;
      }

      for(int currDiv = 1; currDiv <= div; currDiv++)
      {
        String add = "";
        if(task.contains("WS"))
        {
          add = "." + currDiv;
        }
        for(int part = 0; part < numSplits; part++)
        {
          System.out.println(task + ".part" + part);
          Scanner humanS = new Scanner(new FileReader(resultHumanFile + task + add + ".part" + part));
          while(humanS.hasNext())
          {
            String line = humanS.nextLine();
            //humanValues = humanValues.append(line + ";");
            //humanValues = humanValues.append(line + ";");
            humanValues = humanValues.append(line + ";");
            humanValues = humanValues.append(line + ";");
          }
          humanS.close();
          
          Scanner maxS = new Scanner(new FileReader(resultMaxFile + task + add + ".part" + part));
          while(maxS.hasNext())
          {
            String line = maxS.nextLine();
            //maxValues = maxValues.append(line + ";");
            //maxValues = maxValues.append(line + ";");
            maxValues = maxValues.append(line + ";");
            maxValues = maxValues.append(line + ";");
          }
          maxS.close();
          
          Scanner avgS = new Scanner(new FileReader(resultAvgFile + task + add + ".part" + part));
          while(avgS.hasNext())
          {
            String line = avgS.nextLine();
            //avgValues = avgValues.append(line + ";");
            //avgValues = avgValues.append(line + ";");
            avgValues = avgValues.append(line + ";");
            avgValues = avgValues.append(line + ";");
          }
          avgS.close();
          
          Scanner vectorS = new Scanner(new FileReader(resultVectFile + task + add + ".part" + part));
          while(vectorS.hasNext())
          {
            String line = vectorS.nextLine();
            System.out.println(line);
            String[] arr = line.split(",");
            for(int i=0; i<arr.length; i++)
            {
              int vertex = Integer.parseInt(arr[i]);
              if(vertex != -1)
              {
                double d = pr.getPageRankValue(vertex);
                prValues = prValues.append(d + ";");
              }
            }
          }
          vectorS.close();
          
        }//end: for(part)
      }
      
      /* Print the standard files */
      maxValues = maxValues.deleteCharAt(maxValues.length()-1);
      avgValues = avgValues.deleteCharAt(avgValues.length()-1);
      humanValues = humanValues.deleteCharAt(humanValues.length()-1);
      prValues = prValues.deleteCharAt(prValues.length()-1);
      
      maxValues = maxValues.append("];");
      avgValues = avgValues.append("];");
      humanValues = humanValues.append("];");
      prValues = prValues.append("];");
      
      pwAvg.println(avgValues);
      pwAvg.println(humanValues);
      pwAvg.println(prValues);
      pwAvg.println("corr(pr, avg, 'type', 'Pearson')");
      pwAvg.println("corr(pr, avg, 'type', 'Spearman')");
      pwAvg.close();

      pwMax.println(maxValues);
      pwMax.println(humanValues);
      pwAvg.println(prValues);
      pwMax.println("corr(pr, max, 'type', 'Pearson')");
      pwMax.println("corr(pr, max, 'type', 'Spearman')");
      pwMax.close();
    }
  }

}
