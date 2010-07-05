package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;

import edu.osu.slate.relatedness.Configuration;

public class ZeschCompare
{

  private static String task;
  private static String taskFile, zeschFile;
  
  private static String resultAvgFileAll;

  private static String resultMaxFileAll;
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

    resultAvgFileAll = resultFile + "_avg_all_";
    resultMaxFileAll = resultFile + "_max_all_";
    taskFile = "/u/weale/data/wordpair/" + task + ".txt";
    zeschFile = "/u/weale/Desktop/" + task + "Z.csv";
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
      Configuration.resultDir = "/scratch/weale/results/baseresults/";
    }    
    String[] tasks = {"WS1", "WS2", "YP130"};
    
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      task = tasks[currTask];
      
      setFiles();
      
      String transitionSource = "";
      if(!Configuration.transitions.equals(""))
      {
        transitionSource = Configuration.transitions + "_";
      }
      
      TreeSet<String> zeschList = new TreeSet<String>();
      Scanner s = new Scanner(new FileReader(zeschFile));
      while(s.hasNext())
      {
        String line = s.nextLine();
        String line2;
        
        String[] arr = line.split(",");
        if(arr[0].compareTo(arr[1]) < 0)
        {
          line2 = arr[0] + "," + arr[1];
        }
        else
        {
          line2 = arr[1] + "," + arr[0];
        }
        
        zeschList.add(line2);
      }
      
//      int i=0;
//      int overlap=0;
//      while(s.hasNext())
//      {
//        String line = s.nextLine();
//        String line2;
//        String line3;
//        String[] arr = line.split(",");
//        if(arr[0].compareTo(arr[1]) < 0)
//        {
//          line2 = arr[0] + "," + arr[1];
//          line3 = arr[1] + "," + arr[0];
//        }
//        else
//        {
//          line2 = arr[1] + "," + arr[0];
//          line3 = arr[0] + "," + arr[1];
//        }
//        
//        if(arr[0].equals(arr[1])) {}
//        if(arr[0].equals("money") && arr[1].equals("cash")) {}
//        else if(zeschList.contains(line2))
//        {
//          overlap++;
//        }
//        else
//        {
//          System.out.println(line2);
//        }
//
//        i++;
//      }
//      System.out.println(overlap);
//      System.out.println(zeschList.size());
      
      String wikiAvg, wikiMax;
      String human, corr1, corr2;
      s = new Scanner(new FileReader(resultAvgFileAll + transitionSource + task + ".m"));
      wikiAvg = s.nextLine();
      human = s.nextLine();
      corr1 = s.nextLine();
      corr2 = s.nextLine();
      s.close();

      s = new Scanner(new FileReader(resultMaxFileAll + transitionSource + task + ".m"));
      wikiMax = s.nextLine();
      
      wikiAvg = wikiAvg.substring(wikiAvg.indexOf("[")+1, wikiAvg.indexOf("]"));
      human = human.substring(human.indexOf("[")+1, human.indexOf("]"));
      wikiMax = wikiMax.substring(wikiMax.indexOf("[")+1, wikiMax.indexOf("]"));
      
      String[] wikiAvgArr = wikiAvg.split(";");
      String[] wikiMaxArr = wikiMax.split(";");
      String[] humanArr = human.split(";");
      
      String humanVals = "human = [";
      String maxVals = "max = [";
      String avgVals = "avg = [";
      
      s = new Scanner(new FileReader(taskFile));
      for(int i = 0; i < wikiAvgArr.length; i++)
      {
        String line = s.nextLine(), line2;
        
        String[] arr = line.split(",");
        if(arr[0].compareTo(arr[1]) < 0)
        {
          line2 = arr[0] + "," + arr[1];
        }
        else
        {
          line2 = arr[1] + "," + arr[0];
        }
        
        if(arr[0].equals(arr[1])) {}
        if(arr[0].equals("money") && arr[1].equals("cash")) {}
        else if(zeschList.contains(line2))
        {
          System.out.println(i + "\t" + line2);
          
          if(wikiAvgArr[i].equals("-10.0"))
          {
            wikiAvgArr[i] = "0";
          }

          if(wikiMaxArr[i].equals("-10.0"))
          {
            wikiMaxArr[i] = "0";
          }

          avgVals = avgVals + wikiAvgArr[i] + ";";
          humanVals = humanVals + humanArr[i] + ";";
          maxVals = maxVals + wikiMaxArr[i] + ";";          
        }
      }//end: for(i)
      
      humanVals = humanVals.substring( 0, humanVals.length()-1 ) + "];";
      maxVals = maxVals.substring( 0, maxVals.length()-1 ) + "];";
      avgVals = avgVals.substring( 0, avgVals.length()-1 ) + "];";

      String resultFile = Configuration.resultDir +
                          "/wordpair/" +
                          "join/" +
                          task + "_" +
                          "zesch.m";
      System.out.println(resultFile);
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
