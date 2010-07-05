package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.PageRank;
import edu.osu.slate.relatedness.swwr.algorithm.PersonalizedPageRank;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

public class CompareToPageRank2 {

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
      Configuration.resultDir = "/scratch/weale/results/baseresults/";
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
    
    System.out.println("Creating Personalized PageRank");
    PersonalizedPageRank ppr = new PersonalizedPageRank(pr);
    
    System.out.println("Creating Sourced PageRank");
    SourcedPageRank spr = new SourcedPageRank(pr);
    
    PrintWriter pw = new PrintWriter("/u/weale/Desktop/CompareToPageRank.m");

    double [] PR = pr.getPageRankValues();
    StringBuffer PRBuffer = new StringBuffer("PR = [");
    for(int x=0; x<PR.length; x++)
    {
      PRBuffer = PRBuffer.append(PR[x] + ";");
    }
    PRBuffer = PRBuffer.deleteCharAt(PRBuffer.length()-1).append("];");
    pw.println(PRBuffer);
    
    for(int i = 0; i < 5; i++)
    {
      System.out.print(i + "\t");
      double[] PPR = ppr.getExactRelatedness(i);
      System.out.print(".\t");
      double[] SPR = spr.getExactRelatedness(i);
      System.out.print(".\n");
      
      StringBuffer PPRBuffer = new StringBuffer("PPR"+ i +" = [");
      StringBuffer SPRBuffer = new StringBuffer("SPR"+ i +" = [");
      for(int x=0; x<PR.length; x++)
      {
        PPRBuffer = PPRBuffer.append(PPR[x] + ";");
        SPRBuffer = SPRBuffer.append(SPR[x] + ";");
      }
      PPRBuffer = PPRBuffer.deleteCharAt(PPRBuffer.length()-1).append("];");
      SPRBuffer = SPRBuffer.deleteCharAt(SPRBuffer.length()-1).append("];");
      pw.println(PPRBuffer);
      pw.println(SPRBuffer);
    }
    pw.close();
  }

}
