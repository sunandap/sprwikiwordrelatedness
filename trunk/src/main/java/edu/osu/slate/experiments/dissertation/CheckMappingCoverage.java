package edu.osu.slate.experiments.dissertation;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.Scanner;

import edu.osu.slate.experiments.Common;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToWordMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.WordToVertexMapping;

/**
 * Checks the mapping coverage of a data set for an experiment.
 * 
 * @author weale
 *
 */
public class CheckMappingCoverage {

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

    String transitionSource = "";
    if(!Configuration.transitions.equals(""))
    {
      transitionSource = "-" + Configuration.transitions;
    }

    wordVertexMapFile = dir + data + "-f.wvc";
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
    
    System.out.println("Opening Word To Vertex Mapping");
    ObjectInputStream in = null;
    try
    {
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      word2Vertex = (WordToVertexMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      e.printStackTrace();
      System.exit(1);
    }

    while(s.hasNext()) {

      /* Get Next Question */
      String str = s.nextLine();

      /* Split the input string */          
      String[] arr = str.split("\\|");
      for(int i=0;i<arr.length; i++) {
        arr[i] = arr[i].trim();
      }

      /* Make placeholders for relatedness values */
      double [] vals = new double[4];
      for(int i = 0; i < vals.length; i++)
      {
        vals[i] = -10;
      }

      /* Find the Surface Form ID of the given word */
      for(int i = 0; i < arr.length; i++)
      {
        String[] candidateWords = Common.resolve(arr[i], word2Vertex);
        if(candidateWords == null)
        {
          System.out.println(arr[i] + " not found");
        }
        else 
        {
          for(int j = 0; j < candidateWords.length; j++)
          {
            VertexCount[] vertices = word2Vertex.getVertexMappings(candidateWords[j]);
            for(int k = 0; k < vertices.length; k++)
            {
              if(vertices[k].getVertex() < 0)
              {
                System.out.println(candidateWords[j] + ":" + k);
              }
            }
          }//end: for(j)
        }
      }//end: for(i)  
    }//end while(hasNext())
    
  }//end: main(String)

}
