package edu.osu.slate.experiments.synonym;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;

public class JoinFiles {

  private static String taskFile, task;
  
  // ESL files
  private static String eslResultFile;
  private static String eslVertexFile;

  // TOEFL files
  private static String toeflResultFile;
  private static String toeflVertexFile;
  
  // RDWP300 files
  private static String rdwp300ResultFile;
  private static String rdwp300VertexFile;  

  // RDWP1K files
  private static String rdwp1kResultFile;
  private static String rdwp1kVertexFile;
  
  private static String currResultFile;
  private static String currVertexFile;
  private static int numSplits;
  
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
    
    String transitionSource = "";
    if(!Configuration.transitions.equals(""))
    {
      transitionSource = "-" + Configuration.transitions;
    }
    
    eslResultFile = Configuration.resultDir +
                 "/synonym/" +
                 Configuration.type + "/" +
                 "ESL-(" +
                 Configuration.mapsource + "-" +
                 Configuration.stemming + ")-(" +
                 data+transitionSource + ")";
    
    toeflResultFile = Configuration.resultDir +
                      "/synonym/" +
                      Configuration.type + "/" +
                      "TOEFL-(" +
                      Configuration.mapsource + "-" +
                      Configuration.stemming + ")-(" +
                      data+transitionSource + ")";
    
    eslVertexFile = Configuration.resultDir +
                    "/synonym/" +
                    Configuration.type + "/vertex/" +
                    "ESL-(" +
                    Configuration.mapsource + "-" +
                    Configuration.stemming + ")-(" +
                    data+transitionSource + ")";

    toeflVertexFile = Configuration.resultDir +
                      "/synonym/" +
                      Configuration.type + "/vertex/" +
                      "TOEFL-(" +
                      Configuration.mapsource + "-" +
                      Configuration.stemming + ")-(" +
                      data+transitionSource + ")";
    
    rdwp300ResultFile = Configuration.resultDir +
                        "/synonym/" +
                         Configuration.type + "/" +
                         "RDWP300-(" +
                         Configuration.mapsource + "-" +
                         Configuration.stemming + ")-(" +
                         data+transitionSource + ")";

    rdwp1kResultFile = Configuration.resultDir +
                       "/synonym/" +
                       Configuration.type + "/" +
                       "RDWP1K-(" +
                       Configuration.mapsource + "-" +
                       Configuration.stemming + ")-(" +
                       data+transitionSource + ")";

    rdwp300VertexFile = Configuration.resultDir +
                        "/synonym/" +
                        Configuration.type + "/vertex/" +
                        "RDWP300-(" +
                        Configuration.mapsource + "-" +
                        Configuration.stemming + ")-(" +
                        data+transitionSource + ")";

    rdwp1kVertexFile = Configuration.resultDir +
                       "/synonym/" +
                       Configuration.type + "/vertex/" +
                       "RDWP1K-(" +
                       Configuration.mapsource + "-" +
                       Configuration.stemming + ")-(" +
                       data+transitionSource + ")";
    
    if(task.equals("ESL"))
    {
      currResultFile = eslResultFile;
      currVertexFile = eslVertexFile;
      numSplits = 3;
    }
    else if(task.equals("TOEFL"))
    {
      currResultFile = toeflResultFile;
      currVertexFile = toeflVertexFile;
      numSplits = 5;
    }
    else if(task.equals("RDWP300"))
    {
      currResultFile = rdwp300ResultFile;
      currVertexFile = rdwp300VertexFile;
      numSplits = 16;
    }
    else if(task.equals("RDWP1K"))
    {
      currResultFile = rdwp1kResultFile;
      currVertexFile = rdwp1kVertexFile;
      numSplits = 16;
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
    
      PrintWriter result = new PrintWriter(currResultFile + ".txt");
      PrintWriter vertex = new PrintWriter(currVertexFile + ".txt");
      
      for(int part = 0; part < numSplits; part++)
      {
        Scanner resultPart = new Scanner(new FileReader(currResultFile + ".part" + part));
        Scanner vertexPart = new Scanner(new FileReader(currVertexFile + ".part" + part));
        
        while(resultPart.hasNext())
        {
          result.println(resultPart.nextLine());
        }
        resultPart.close();
        
        while(vertexPart.hasNext())
        {
          vertex.println(vertexPart.nextLine());
        }
        vertexPart.close();
      }//end: for(part)
      
      result.close();
      vertex.close();

    }//end: for(currTask)
  }//end: main()
}
