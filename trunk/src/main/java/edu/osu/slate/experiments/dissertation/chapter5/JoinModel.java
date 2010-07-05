package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;

public class JoinModel
{

  private static String task;
  
  private static String resultAvgFileAll;

  private static String resultMaxFile;
  private static String resultAvgFile;
  
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
                        Configuration.type + "_" +
                        Configuration.date + "_" +
                        Configuration.graph + "_" +
                        Configuration.mapsource + "_" + 
                        Configuration.stemming;

    resultAvgFile = resultFile + "_avg_";    
    resultMaxFile = resultFile + "_max_";

    resultAvgFileAll = resultFile + "_avg_all_";
  }
  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException
  {
    Configuration.parseConfigurationFile(args[1]);
    Configuration.resultDir = args[0];
    
    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      task = tasks[currTask];
      
      Configuration.type = "enwiki";
      Configuration.date = "20080103";
      Configuration.mapsource = "link";
      Configuration.stemming = "f";
      setFiles();
      
      String transitionSource = "";
      if(!Configuration.transitions.equals(""))
      {
        transitionSource = Configuration.transitions + "_";
      }
      
      String wikiAvg, wiktAvg;
      String human, corr1, corr2;
      Scanner s = new Scanner(new FileReader(resultAvgFileAll + transitionSource + task + ".m"));
      wikiAvg = s.nextLine();
      human = s.nextLine();
      corr1 = s.nextLine();
      corr2 = s.nextLine();
      s.close();
      
      
      Configuration.type = "enwiktionary";
      Configuration.date = "20090203";
      Configuration.mapsource = "title";
      Configuration.stemming = "f";
      setFiles();
      
      s = new Scanner(new FileReader(resultAvgFileAll + transitionSource + task + ".m"));
      wiktAvg = s.nextLine();
      s.close();
      
      wikiAvg = wikiAvg.substring(wikiAvg.indexOf("[")+1, wikiAvg.indexOf("]"));
      wiktAvg = wiktAvg.substring(wiktAvg.indexOf("[")+1, wiktAvg.indexOf("]"));
      human = human.substring(human.indexOf("[")+1, human.indexOf("]"));
      
      String[] wikiArr = wikiAvg.split(";");
      String[] wiktArr = wiktAvg.split(";");
      String[] humanArr = human.split(";");
      
      String humanVals = "human = [";
      String maxVals = "max = [";
      String avgVals = "avg = [";
      
      int[] wikiVals = new int[wikiArr.length];
      for(int i = 0; i < wikiVals.length; i++)
      {
        double wikiVal = Double.parseDouble(wikiArr[i]);
        double wiktVal = Double.parseDouble(wiktArr[i]);
        
        double max = Math.max(wikiVal, wiktVal);
        if(max != -10)
        {
          humanVals = humanVals + humanArr[i] + ";";
          maxVals = maxVals + max + ";";
          
          if(wikiVal == -10)
          {
            avgVals = avgVals + wiktVal + ";";
          }
          else if(wiktVal == -10)
          {
            avgVals = avgVals + wikiVal + ";";
          }
          else
          {
            avgVals = avgVals + ((wiktVal + wikiVal) / 2.0) + ";";
          }
        }
      }//end: for(i)
      
      humanVals = humanVals.substring( 0, humanVals.length()-1 ) + "];";
      maxVals = maxVals.substring( 0, maxVals.length()-1 ) + "];";
      avgVals = avgVals.substring( 0, avgVals.length()-1 ) + "];";

      String resultFile = Configuration.resultDir +
                          "/wordpair/" +
                          "join/" +
                          task + "_" +
                          "avg_max.m";
      
      PrintWriter modelJoin = new PrintWriter(resultFile);
      modelJoin.println(humanVals);
      modelJoin.println(avgVals);
      modelJoin.println(maxVals);
      modelJoin.println("corr(human, avg, 'type', 'Pearson')");
      modelJoin.println("corr(human, avg, 'type', 'Spearman')");
      modelJoin.println("corr(human, max, 'type', 'Pearson')");
      modelJoin.println("corr(human, max, 'type', 'Spearman')");
      modelJoin.close();
    }//end: for(currTask)
  }

}
