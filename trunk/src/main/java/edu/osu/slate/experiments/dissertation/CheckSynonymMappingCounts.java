package edu.osu.slate.experiments.dissertation;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.osu.slate.experiments.Common;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.WordToVertexMapping;

/**
 * Checks the mapping coverage of a data set for an experiment.
 * 
 * @author weale
 *
 */
public class CheckSynonymMappingCounts {

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
          
      TreeMap<String,Integer> words = new TreeMap<String,Integer>();
      TreeSet<String> missedWords = new TreeSet<String>();
      
      int missedWordCount = 0;
      int totalQuestions = 0;
      int foundQuestions = 0;
      int foundSources = 0;
      int foundCandidates = 0;
      int temp = 0;
      
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
          String[] candidateWords = resolve(arr[i], word2Vertex);
          
          int wordCount = 0;
          for(int j = 0; candidateWords != null && j < candidateWords.length; j++)
          {
            VertexCount[] vc = word2Vertex.getVertexMappings(candidateWords[j]);
            //System.out.println(vc[0].getCount());
            if(vc != null)
              wordCount += vc.length;
          }
          
          if(words.containsKey(arr[i]))
          {
            wordCount += words.get(arr[i]);
          }
          words.put(arr[i], wordCount);
          temp += wordCount;
          
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
        }//end: for(i)
        
        if(count == arr.length)
        {
          foundQuestions++;
        }
      }//end while(hasNext())
      
      System.out.println("Mapping Count:\t\t" + temp);
      System.out.println("Total Words:\t\t" + words.size());
      
    }//end: for(currTask)
    
  }//end: main(String)
  
  public static String[] resolve(String w, WordToVertexMapping wvm) {

    /* Check to see if current string is valid  *
     * If valid, return it.                     */
    if(wvm.getVertexMappings(w) != null)
    {
      String[] tmp = new String[1];
      tmp[0] = w;
      return tmp;
    }
    
    return null;    
  }//end:resolve(AliasStrings, String)
}