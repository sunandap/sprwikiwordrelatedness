package edu.osu.slate.experiments.dissertation.chapter4;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
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
public class CheckWordPairMappingCounts {

  /* */
  private static String wordVertexMapFile;

  /* */
  private static String taskFile;
  
  private static String matlabWordMapFileName;
  private static String matlabMapCountFileName;

  /* */
  private static TermToVertexMapping word2VertexF;
  private static TermToVertexMapping word2VertexT;
  
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

    wordVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".wvc";
    
    taskFile = Configuration.taskDir + Configuration.task + ".csv";
    matlabWordMapFileName = Configuration.resultDir +
                              "matlab/wp_wordmap_" +
                              Configuration.type.substring(0,6) + "_" +
                              Configuration.mapsource + ".m";
    matlabMapCountFileName = Configuration.resultDir +
                             "matlab/wp_mapcount_" +
                             Configuration.type.substring(0,6) + "_" +
                             Configuration.mapsource + ".m";
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
    
    /* Open non-stemmed mapping object.
     * 
     */
    Configuration.stemming = "f";
    setFiles();
    
    System.out.println("Opening Word To Vertex Mapping: " + 
                       Configuration.mapsource + "-" + Configuration.stemming);
    ObjectInputStream in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      word2VertexF = (TermToVertexMapping) in.readObject();
      in.close();
      word2VertexF.setStemming(false);
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      e.printStackTrace();
      System.exit(1);
    }
    
    /* Open stemmed mapping object.
     * 
     */
    Configuration.stemming = "t";
    setFiles();
    
    System.out.println("Opening Word To Vertex Mapping: " + 
                       Configuration.mapsource + "-" + Configuration.stemming);
    in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      word2VertexT = (TermToVertexMapping) in.readObject();
      in.close();
      word2VertexT.setStemming(true);
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      e.printStackTrace();
      System.exit(1);
    }

    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    
    TreeSet<String> terms = new TreeSet<String>();
    TreeMap<Integer,Integer> countsF = new TreeMap<Integer,Integer>();
    TreeMap<Integer,Integer> countsT = new TreeMap<Integer,Integer>();
    
    TreeMap<Integer,Integer> mcCountsF = new TreeMap<Integer,Integer>();
    TreeMap<Integer,Integer> mcCountsT = new TreeMap<Integer,Integer>();
    
    double totalTermMapsF = 0;
    double totalTermMapsT = 0;
    double totalMapCountsF = 0;
    double totalMapCountsT = 0;
    
    int foundWords = 0;
    int subWords = 0;
    
    // Get all terms across all word pair tasks
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      setFiles();
      
      Scanner s = null;
      try
      {
        s = new Scanner(new FileReader(taskFile));
        while(s.hasNext())
        {
          // Add all terms to the Set
          String tmp = s.nextLine();
          String[] tmparr = tmp.split(",");
          terms.add(tmparr[0]);
          terms.add(tmparr[1]);
        }//end: while(s)
        s.close();
      }//end: try {}
      catch(Exception e)
      {
        System.err.println("Problem with file: " + taskFile);
        e.printStackTrace();
        System.exit(1);
      }
    }//end: for(currTask)
    
    Iterator<String> it = terms.iterator();
    while(it.hasNext())
    {
      // Extract a term from our Set
      String term = it.next();
      
      // Break the term into its minimum parts
      String[] candidateWords = Common.resolve(term, word2VertexF);
          
      if(candidateWords != null)
      {
        
        subWords += candidateWords.length;
        
        // For each sub-part of the term
        for(int i=0; i<candidateWords.length; i++)
        {
          // Get the vertices for the sub-term
          VertexCount[] tmp = word2VertexF.getVertexMappings(candidateWords[i]);
          if(tmp != null)
          {
            totalTermMapsF++;
            int count = 1;
            if(countsF.containsKey(tmp.length))
            {
              count += countsF.get(tmp.length);
            }

            countsF.put(tmp.length, count);
            
            for(int j = 0; j < tmp.length; j++)
            {
              totalMapCountsF++;
              int maps = tmp[j].getCount();
              count = 1;
              if(mcCountsF.containsKey(maps))
              {
                count += mcCountsF.get(maps);
              }
              mcCountsF.put(maps, count);
            }//end: for(j)
          }//end: if(tmp)
        }//end: for(i)
      }//end: non-stemmed mapping
       
      candidateWords = Common.resolve(term, word2VertexT);
      
      if(candidateWords != null)
      {
        foundWords++;
        subWords += candidateWords.length;
        for(int i=0; i<candidateWords.length; i++)
        {
          VertexCount[] tmp = word2VertexT.getVertexMappings(candidateWords[i]);
          if(tmp != null)
          {
            totalTermMapsT++;
            int count = 1;
            if(countsT.containsKey(tmp.length))
            {
              count += countsT.get(tmp.length);
            }
            countsT.put(tmp.length, count);
            
            for(int j = 0; j < tmp.length; j++)
            {
              totalMapCountsT++;
              int maps = tmp[j].getCount();
              count = 1;
              if(mcCountsT.containsKey(maps))
              {
                count += mcCountsT.get(maps);
              }
              mcCountsT.put(maps, count);
            }
          }
        }//end: for(i)
      }
    }//end while(hasNext())      
        
    Map.Entry<Integer, Integer> topF = countsF.lastEntry();
    Map.Entry<Integer, Integer> topT = countsT.lastEntry();
    
    int length = Math.max(topF.getKey(), topT.getKey());
    
    StringBuilder vals = new StringBuilder("");
    for(int i = 1; i <= length; i++)
    {
      if(countsF.containsKey(i))
      {
        vals = vals.append( ((countsF.get(i)/totalTermMapsF)*100) + ",");
      }
      else
      {
        vals = vals.append("0,");
      }
      
      if(countsT.containsKey(i))
      {
        vals = vals.append( ((countsT.get(i)/totalTermMapsT)*100) + ";");
      }
      else
      {
        vals = vals.append("0;");
      }
    }//end: for(i)
    
    vals = vals.deleteCharAt(vals.length()-1);
    
    try
    {
      PrintWriter pw = new PrintWriter(matlabWordMapFileName);
      pw.println("A = [" + vals +"];");
      pw.println("h = bar(A,'hist');");
      pw.println("colormap copper");
      pw.println("title('Word-to-Vertex Mapping Distribution for the Word Pair Task Using " +
                 Configuration.type + "-" +
                 Configuration.mapsource + "')");
      pw.println("xlabel('# of Potential Vertices')");
      pw.println("ylabel('Percentage of Words')");
      pw.println("legend('-stem','+stem');");
      pw.println("axis tight");
      pw.close();
    }
    catch(Exception e)
    {
      System.out.println("Problem with file: " + matlabWordMapFileName);
      e.printStackTrace();
    }
    
    topF = mcCountsF.lastEntry();
    topT = mcCountsT.lastEntry();
    
    length = Math.max(topF.getKey(), topT.getKey());
    
    vals = new StringBuilder("");
    for(int i = 1; i <= length; i++)
    {
      if(mcCountsF.containsKey(i))
      {
        vals = vals.append( ((mcCountsF.get(i)/totalMapCountsF)*100) + ",");
      }
      else
      {
        vals = vals.append("0,");
      }
      
      if(mcCountsT.containsKey(i))
      {
        vals = vals.append( ((mcCountsT.get(i)/totalMapCountsT)*100) + ";");
      }
      else
      {
        vals = vals.append("0;");
      }
    }//end: for(i)
    
    vals = vals.deleteCharAt(vals.length()-1);
    
    try
    {
      PrintWriter pw = new PrintWriter(matlabMapCountFileName);
      pw.println("A = [" + vals +"];");
      pw.println("h = bar(A,'hist');");
      pw.println("colormap copper");
      pw.println("title('Word-to-Vertex Mapping Distribution for the Word Pair Task Using " +
                 Configuration.type + "-" +
                 Configuration.mapsource + "')");
      pw.println("xlabel('Vertex Mapping Counts')");
      pw.println("ylabel('Percentage of Vertex Maps')");
      pw.println("legend('-stem','+stem');");
      pw.println("axis tight");
      pw.close();
    }
    catch(Exception e)
    {
      System.out.println("Problem with file: " + matlabMapCountFileName);
      e.printStackTrace();
    }
    //System.out.println(counts);
  }//end: main(String)
}