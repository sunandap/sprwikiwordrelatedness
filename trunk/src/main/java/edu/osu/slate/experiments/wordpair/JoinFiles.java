package edu.osu.slate.experiments.wordpair;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;

public class JoinFiles
{

  private static String task;
    
  private static int numSplits;
  
  private static String resultAvgFile;
  private static String resultAvgFileAll;

  private static String resultMaxFile;
  private static String resultMaxFileAll;
  
  private static String resultHumanFile;
  private static String resultHumanFileAll;
  
  private static String resultVectFile;
  
  private static void setFiles()
  {
    String resultFile = Configuration.resultDir +
                        "/wordpair/" +
                        Configuration.type + "/" +
                        Configuration.type + "-" +
                        Configuration.date + "-" +
                        Configuration.graph + "-" +
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
                     Configuration.mapsource + "-" + 
                     Configuration.stemming + "-";
    
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
      numSplits = 16;
    }
    else if(task.equals("WS2"))
    {
      numSplits = 32;
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
    Configuration.baseDir = args[0];
    Configuration.parseConfigurationFile(args[1]);
    
    LinkedList<String> tasks = new LinkedList<String>();
    for(int i=2; i<args.length; i++)
    {
      tasks.add(args[i]);
    }
    
    for(int currTask = 0; currTask < tasks.size(); currTask++)
    {
      task = tasks.get(currTask);
      setFiles();
      
      // Open output files
      PrintWriter pwAvg = new PrintWriter(resultAvgFile.replace('-', '_') + task + ".m");
      PrintWriter pwMax = new PrintWriter(resultMaxFile.replace('-', '_') + task + ".m");
      PrintWriter pwAvgAll = new PrintWriter(resultAvgFileAll.replace('-', '_') + task + ".m");
      PrintWriter pwMaxAll = new PrintWriter(resultMaxFileAll.replace('-', '_') + task + ".m");
      PrintWriter pwVector = new PrintWriter(resultVectFile + task + ".txt");
      
      // Set-up string buffers
      StringBuffer humanValues = new StringBuffer("human = [");
      StringBuffer humanValuesAll = new StringBuffer("humanAll = [");
      StringBuffer maxValues = new StringBuffer("max = [");
      StringBuffer maxValuesAll = new StringBuffer("maxAll = [");
      StringBuffer avgValues = new StringBuffer("avg = [");
      StringBuffer avgValuesAll = new StringBuffer("avgAll = [");
      
      for(int part = 0; part < numSplits; part++)
      {
        System.out.println(task + ".part" + part);
        Scanner humanS = new Scanner(new FileReader(resultHumanFile + task + ".part" + part));
        while(humanS.hasNext())
        {
          humanValues = humanValues.append(humanS.nextLine() + ";");
        }
        humanS.close();
        
        Scanner humanAS = new Scanner(new FileReader(resultHumanFileAll + task + ".part" + part));
        while(humanAS.hasNext())
        {
          humanValuesAll = humanValuesAll.append(humanAS.nextLine() + ";");
        }
        humanAS.close();
        
        Scanner maxS = new Scanner(new FileReader(resultMaxFile + task + ".part" + part));
        while(maxS.hasNext())
        {
          maxValues = maxValues.append(maxS.nextLine() + ";");
        }
        maxS.close();
        
        Scanner maxAS = new Scanner(new FileReader(resultMaxFileAll + task + ".part" + part));
        while(maxAS.hasNext())
        {
          maxValuesAll = maxValuesAll.append(maxAS.nextLine() + ";");
        }
        maxAS.close();
        
        Scanner avgS = new Scanner(new FileReader(resultAvgFile + task + ".part" + part));
        while(avgS.hasNext())
        {
          avgValues = avgValues.append(avgS.nextLine() + ";");
        }
        avgS.close();
        
        Scanner avgAS = new Scanner(new FileReader(resultAvgFileAll + task + ".part" + part)); 
        while(avgAS.hasNext())
        {
          avgValuesAll = avgValuesAll.append(avgAS.nextLine() + ";");
        }
        avgAS.close();
        
        Scanner vectorS = new Scanner(new FileReader(resultVectFile + task + ".part" + part));
        while(vectorS.hasNext())
        {
          pwVector.println(vectorS.nextLine());
        }
        vectorS.close();
        
      }//end: for(part)
      
      pwVector.close();
      
      /* Print the standard files */
      maxValues = maxValues.deleteCharAt(maxValues.length()-1);
      avgValues = avgValues.deleteCharAt(avgValues.length()-1);
      humanValues = humanValues.deleteCharAt(humanValues.length()-1);
      
      maxValues = maxValues.append("];");
      avgValues = avgValues.append("];");
      humanValues = humanValues.append("];");
      
      pwAvg.println(avgValues);
      pwAvg.println(humanValues);
      pwAvg.println("corr(human, avg, 'type', 'Pearson')");
      pwAvg.println("corr(human, avg, 'type', 'Spearman')");
      pwAvg.close();

      pwMax.println(maxValues);
      pwMax.println(humanValues);
      pwMax.println("corr(human, max, 'type', 'Pearson')");
      pwMax.println("corr(human, max, 'type', 'Spearman')");
      pwMax.close();
      
      /* Print the 'ALL' files */
      maxValuesAll = maxValuesAll.deleteCharAt(maxValuesAll.length()-1);
      avgValuesAll = avgValuesAll.deleteCharAt(avgValuesAll.length()-1);
      humanValuesAll = humanValuesAll.deleteCharAt(humanValuesAll.length()-1);
      
      maxValuesAll = maxValuesAll.append("];");
      avgValuesAll = avgValuesAll.append("];");
      humanValuesAll = humanValuesAll.append("];");
      
      pwAvgAll.println(avgValuesAll);
      pwAvgAll.println(humanValuesAll);
      pwAvgAll.println("corr(humanAll, avgAll, 'type', 'Pearson')");
      pwAvgAll.println("corr(humanAll, avgAll, 'type', 'Spearman')");
      pwAvgAll.close();

      pwMaxAll.println(maxValuesAll);
      pwMaxAll.println(humanValuesAll);
      pwMaxAll.println("corr(humanAll, maxAll, 'type', 'Pearson')");
      pwMaxAll.println("corr(humanAll, maxAll, 'type', 'Spearman')");
      pwMaxAll.close();

    }//end: for(currTask)
  }//end: main()
}
