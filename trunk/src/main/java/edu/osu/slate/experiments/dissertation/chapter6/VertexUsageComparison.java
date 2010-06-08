package edu.osu.slate.experiments.dissertation.chapter6;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.TreeSet;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class VertexUsageComparison {

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
                 Configuration.type + "/vertex/" +
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
      
      double numVertices = 0;
      double[] numDiffVertices = new double[5];
      
      while(s.hasNext())
      {
        String line = s.nextLine();
        String[] arr = line.split(" ");
        numVertices++;
        
        TreeSet<String> ts = new TreeSet<String>();
        
        if(!arr[1].equals("-1"))
        {
          ts.add(arr[0]);
        }

        if(!arr[3].equals("-1"))
        {
          ts.add(arr[2]);
        }

        if(!arr[5].equals("-1"))
        {
          ts.add(arr[4]);
        }

        if(!arr[7].equals("-1"))
        {
          ts.add(arr[6]);
        }

        numDiffVertices[ts.size()]++;
      }
      
      System.out.println(numDiffVertices[0]/numVertices);
      System.out.println(numDiffVertices[1]/numVertices);
      System.out.println(numDiffVertices[2]/numVertices);
      System.out.println(numDiffVertices[3]/numVertices);
      System.out.println(numDiffVertices[4]/numVertices);
    }    
  }
}
