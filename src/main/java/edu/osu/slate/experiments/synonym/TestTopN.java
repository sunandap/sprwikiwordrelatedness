package edu.osu.slate.experiments.synonym;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.TermCount;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class TestTopN {

  private static String vertexWordMapFile;
  private static String wordVertexMapFile;
  private static String graphFile;
  private static String taskFile;
  private static WikiGraph wgp;
  private static VertexToTermMapping vertex2Word;
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

    String transitionSource = "";
    if(!Configuration.transitions.equals(""))
    {
      transitionSource = "-" + Configuration.transitions;
    }

    vertexWordMapFile = dir + data + "-f.vwc";
    wordVertexMapFile = dir + data + "-f.wvc";
    graphFile = dir + data + transitionSource + ".wgp";
    taskFile = Configuration.taskDir + Configuration.task + ".txt";
  }
  
  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
    }
    
    setFiles();

    ObjectInputStream in = null;
    try
    {
      System.out.println("Opening Wiki Graph");
      in = new ObjectInputStream(new FileInputStream(graphFile));
      wgp = (WikiGraph) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + graphFile);
      e.printStackTrace();
      System.exit(1);
    }
    
    try
    {
      System.out.println("Opening Word To Vertex Mapping");
      in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      word2Vertex = (TermToVertexMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      System.exit(1);
    }
    
    try
    {
      System.out.println("Opening Vertex To Word Mapping");
      in = new ObjectInputStream(new FileInputStream(vertexWordMapFile));
      vertex2Word = (VertexToTermMapping) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + vertexWordMapFile);
      System.exit(1);      
    }
    
    SourcedPageRank ngd = new SourcedPageRank(wgp);
    
    VertexCount[] vertices = word2Vertex.getVertexMappings("apple");
    for(int i=0 ;i<vertices.length; i++)
    {
      int vertex = vertices[i].getVertex();
      double [] vals = ngd.getRelatedness(vertex);
      RelVertex[] rels = new RelVertex[vals.length];
      for(int j=0; j<rels.length; j++)
      {
        rels[j] = new RelVertex(vals[j], j);
      }
      Arrays.sort(rels, new RelVertexComparator());
      for(int j=0; j<20; j++)
      {
        TermCount[] wc = vertex2Word.getWordMappings(rels[j].getVertex());
        System.out.println(wc[0].getWord() + "\t"+ rels[j].getRel());
      }
      System.out.println();
    }
  }

}
