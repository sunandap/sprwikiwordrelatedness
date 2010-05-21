package edu.osu.slate.experiments.dissertation;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
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
public class CheckSynonymMappingCoverage {

  private static String wordVertexMapFile;
  private static String taskFile;
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
    
    taskFile = Configuration.taskDir + Configuration.task + ".txt";
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
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
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
    //word2Vertex.stem = true;

    String[] tasks = {"ESL", "TOEFL", "RDWP300", "RDWP1K"};
    
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      setFiles();
      
      System.out.println("Setting Synonym Task: " + Configuration.task);    
      Scanner s = null;
      try
      {
        s = new Scanner(new FileReader(taskFile));
      }
      catch(Exception e)
      {
        System.err.println("Problem with file: " + taskFile);
        e.printStackTrace();
        System.exit(1);
      }
          
      TreeSet<String> words = new TreeSet<String>();
      TreeSet<String> missedWords = new TreeSet<String>();
      TreeSet<String> exactMatches = new TreeSet<String>();
      
      int missedWordCount = 0;
      int totalQuestions = 0;
      int foundQuestions = 0;
      int foundSources = 0;
      int foundCandidates = 0;
      
      while(s.hasNext()) {
  
        /* Get Next Question */
        String str = s.nextLine();
        totalQuestions++;
        /* Split the input string */          
        String[] arr = str.split("\\|");
        for(int i=0;i<arr.length; i++) {
          arr[i] = arr[i].trim();
        }
        
        int count = 0;
        /* Find the Surface Form ID of the given word */
        for(int i = 0; i < arr.length; i++)
        {
          String[] candidateWords = Common.resolveExactly(arr[i], word2Vertex);
          
          words.add(arr[i]);
          
          if(candidateWords != null && i == 0)
          {
            foundSources++;
            count++;
          }
          else if(candidateWords != null && i > 0)
          {
            foundCandidates++;
            count++;
          }
          else
          {
            missedWords.add(arr[i]);
            missedWordCount++;
          }
          
          candidateWords = Common.resolveExactly(arr[i], word2Vertex);
          
          if(candidateWords != null)
          {
            exactMatches.add(arr[i]);
          }
        }//end: for(i)
        
        if(count == arr.length)
        {
          foundQuestions++;
        }
      }//end while(hasNext())
      
      int total = foundSources + foundCandidates + missedWordCount;
      System.out.println("Total Words:\t\t" + words.size());
      System.out.println("Total Words Found:\t" + (words.size() - missedWords.size()) );
      System.out.println("Exact Matches:\t\t" + exactMatches.size());
      System.out.println("Total Words Missed:\t" + missedWords.size());
      
      System.out.println("Total Questions: \t" + totalQuestions);
      System.out.println("Questions Matched:\t" + foundQuestions);
      
      System.out.println("Total Word Count:\t" + total);
      System.out.println("Source Words Found:\t" + foundSources);
      System.out.println("Candidate Words Found:\t" + foundCandidates);
      System.out.println("Words Missed:\t\t" + missedWordCount);
      
      System.out.println(missedWords);
    }//end: for(currTask)
  }//end: main(String)
}