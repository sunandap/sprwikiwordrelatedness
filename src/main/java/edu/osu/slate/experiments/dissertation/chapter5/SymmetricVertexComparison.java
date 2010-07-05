package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class SymmetricVertexComparison {

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
                 "/wordpair/" +
                 Configuration.type + "/" +
                 "/vertex/" +
                 Configuration.type + "-" +
                 Configuration.date + "-" +
                 Configuration.graph + "-" +
                 Configuration.mapsource + "-" + 
                 Configuration.stemming + "-" +
                 Configuration.task + ".csv";
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
    try
    {
      System.out.println("Opening Vertex to Term File");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(vtcFile));
      vtc = (VertexToTermMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println(Configuration.mapsource + "-" + Configuration.trimming);
    
    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
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
      double matchedVertices = 0;
      while(s.hasNext())
      {
        String line = s.nextLine();
        String[] arr = line.split(",");
        numVertices += 2;
        
        if(arr[0].equals(arr[2]))
        {
          matchedVertices++;
        }
        else if(fullResults)
        {
          System.out.println(arr[0] + "," + arr[2]);
          System.out.println(vtc.getTermMappings(Integer.parseInt(arr[0]))[0].getTerm());
          System.out.println(vtc.getTermMappings(Integer.parseInt(arr[2]))[0].getTerm());
        }
  
        if(arr[1].equals(arr[3]))
        {
          matchedVertices++;
        }
        else if(fullResults)
        {
          System.out.println(arr[1] + "," + arr[3]);
          System.out.println(vtc.getTermMappings(Integer.parseInt(arr[1]))[0].getTerm());
          System.out.println(vtc.getTermMappings(Integer.parseInt(arr[3]))[0].getTerm());
        }
  
      }
      
      System.out.println(matchedVertices/numVertices);
    }    
  }
}
