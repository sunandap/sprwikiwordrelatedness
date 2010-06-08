package edu.osu.slate.experiments.dissertation.chapter6;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class ValueSpreadComparison {

  private static String vtcFile;
  private static String resultFile;
  
  private static VertexToTermMapping vtc;
  
  /**
   * Sets the file names for the task.
   */
  private static void setFiles()
  {
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";

    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;

    vtcFile = dir + data + "-" +
              Configuration.mapsource + "-" + 
              Configuration.stemming + ".vtc";
    
    resultFile = Configuration.resultDir +
                 "/synonym/" +
                 Configuration.type + "/" +
                 Configuration.task + "-(" +
                 Configuration.mapsource + "-" + 
                 Configuration.stemming + ")-(" +
                 Configuration.type + "-" +
                 Configuration.date + "-" +
                 Configuration.graph + ").txt";
  }//end: setFiles()
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/WordPair.xml");
    }
    
    boolean fullResults = false;
    
    setFiles();
//    try
//    {
//      System.out.println("Opening Vertex to Term File");
//      ObjectInputStream in = new ObjectInputStream(new FileInputStream(vtcFile));
//      vtc = (VertexToTermMapping) in.readObject();
//      in.close();
//    }
//    catch(Exception e)
//    {
//      e.printStackTrace();
//      System.exit(1);
//    }
    System.out.println(Configuration.mapsource + "-" + Configuration.stemming);
    
    String[] tasks = {"ESL", "TOEFL", "RDWP300", "RDWP1K"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      System.out.println(tasks[currTask]);
      Configuration.task = tasks[currTask];
      setFiles();
      
      Scanner s = null;
      try
      {
        s = new Scanner(new FileReader(resultFile));
      }
      catch(IOException e)
      {
        e.printStackTrace();
        System.exit(1);
      }
      
      double numCorrect = 0;
      double sumCorrectT1 = 0;
      double sumCorrect = 0;
      double numWrong = 0;
      double sumWrongT1 = 0;
      double sumWrong = 0;
      
      while(s.hasNext())
      {
        String line = s.nextLine();
        String[] arr = line.split(" ");
        
        double term1 = Double.parseDouble(arr[0]);
        double term2 = Double.parseDouble(arr[1]);
        double term3 = Double.parseDouble(arr[2]);
        double term4 = Double.parseDouble(arr[3]);
        
        double max = Math.max(term2, term3);
        max = Math.max(max, term4);
        
        if(Math.min(term1, max) != -10 && term1 > max)
        {
          numCorrect++;
          sumCorrectT1 += term1;
          sumCorrect += max;
        }
        else if(Math.min(term1, max) != -10)
        {
          numWrong++;
          sumWrongT1 += term1;
          sumWrong += max;          
        }
      }
      
      double t1C = sumCorrectT1/numCorrect;
      double tC = sumCorrect/numCorrect;

      double t1W = sumWrongT1/numWrong;
      double tW = sumWrong/numWrong;

      System.out.println(t1C/tC);
      System.out.println(tW/t1W);
    }    
  }
}
