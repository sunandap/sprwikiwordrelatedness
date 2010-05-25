package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import edu.osu.slate.experiments.Common;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

/**
 * Checks the mapping coverage of a data set for an experiment.
 * 
 * @author weale
 *
 */
public class WordPairMappingAverages {

  /* */
  private static String wordVertexMapFile;

  /* */
  private static String taskFile;

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
  private static void setFiles() {
    
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;

    wordVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".tvc";
    
    taskFile = Configuration.taskDir + Configuration.task + ".csv";
  }
  
  /**
   * Runs the program.
   * 
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
    
    setFiles();
    
    System.out.println("Opening Word To Vertex Mapping: " + 
                       Configuration.mapsource + "-" + Configuration.stemming);
    ObjectInputStream in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      word2Vertex = (TermToVertexMapping) in.readObject();
      in.close();
      word2Vertex.setStemming(Configuration.stemming.equals("t"));
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      e.printStackTrace();
      System.exit(1);
    }

    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      setFiles();
      
      System.out.println("Setting Synonym Task: " + Configuration.task);    
      Scanner s = null;
      TreeSet<String> words = new TreeSet<String>();
      
      // Collect all words
      try
      {
        s = new Scanner(new FileReader(taskFile));
        while(s.hasNext())
        {
          String tmp = s.nextLine();
          String[] tmparr = tmp.split(",");
          words.add(tmparr[0]);
          words.add(tmparr[1]);
        }//end: while(s)
        s.close();
      }
      catch(Exception e)
      {
        System.err.println("Problem with file: " + taskFile);
        e.printStackTrace();
        System.exit(1);
      }
          
      int foundWords = 0;
      
      Iterator<String> it = words.iterator();
      int totalWords = words.size();
      double totalVertices = 0;
      while(it.hasNext()) {
  
        String word = it.next();
        VertexCount[] candidateVertices = word2Vertex.getVertexMappings(word);
          
        if(candidateVertices != null)
        {
          totalVertices += candidateVertices.length;
          foundWords++;
        }
//        else
//        {
//          candidateWords = Common.resolve(word, word2Vertex);
//          if(candidateWords != null)
//          {
//          }
//        }        
      }//end while(hasNext())
      System.out.println(foundWords);
      System.out.println(Configuration.task + "\t" + (totalVertices/foundWords));
      
      
    }//end: for(currTask)
  }//end: main(String)
}