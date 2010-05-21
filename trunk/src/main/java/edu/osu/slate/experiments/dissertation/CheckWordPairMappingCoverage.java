package edu.osu.slate.experiments.dissertation;

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
import edu.osu.slate.relatedness.swwr.data.mapping.WordToVertexMapping;

/**
 * Checks the mapping coverage of a data set for an experiment.
 * 
 * @author weale
 *
 */
public class CheckWordPairMappingCoverage {

  /* */
  private static String wordVertexMapFile;

  /* */
  private static String taskFile;

  /* */
  private static WordToVertexMapping word2Vertex;
  
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
                        Configuration.stemming + ".wvc";
    
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
      word2Vertex = (WordToVertexMapping) in.readObject();
      in.close();
      word2Vertex.stem = Configuration.stemming.equals("t");
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
      TreeSet<String> missedWords = new TreeSet<String>();
      TreeSet<String> exactMatches = new TreeSet<String>();
      
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
      while(it.hasNext()) {
  
        String word = it.next();
        String[] candidateWords = Common.resolveExactly(word, word2Vertex);
          
        if(candidateWords != null)
        {
          foundWords++;
        }
        else
        {
          missedWords.add(word);
        }
        
        candidateWords = Common.resolveExactly(word, word2Vertex);
        if(candidateWords != null)
        {
          exactMatches.add(word);
        }
        
      }//end while(hasNext())
      
      int foundQuestions = 0;
      int totalQuestions = 0;
      try
      {
        s = new Scanner(new FileReader(taskFile));
        while(s.hasNext())
        {
          String tmp = s.nextLine();
          String[] tmparr = tmp.split(",");
          
          String[] resolve0 = Common.resolve(tmparr[0], word2Vertex);
          String[] resolve1 = Common.resolve(tmparr[1], word2Vertex);

          if(resolve0 != null && resolve1 != null)
          {
            foundQuestions++;
          }
                    
          totalQuestions++;
        }//end: while(s)
        s.close();
      }
      catch(Exception e)
      {
        System.err.println("Problem with file: " + taskFile);
        e.printStackTrace();
        System.exit(1);
      }
      
      System.out.println("Total Words:\t\t" + totalWords);
      System.out.println("Words Found:\t\t" + foundWords);
      System.out.println("Exact Words:\t\t" + exactMatches.size());
      
      System.out.println("Total Questions:\t" + totalQuestions);
      System.out.println("Questions Matched:\t" + foundQuestions);
      System.out.println(missedWords);
    }//end: for(currTask)
  }//end: main(String)
}