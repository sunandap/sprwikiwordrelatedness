package edu.osu.slate.experiments.dissertation.chapter4;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

/**
 * Program that returns the number of Terms in a mapping file.
 * 
 * @author weale
 *
 */
public class GetNumberOfTerms {

  /* */
  private static String wordVertexMapFile;

  private static TermToVertexMapping word2Vertex;

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
                        Configuration.stemming + ".tvc";
  }//end: setFiles()

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
      System.out.println(word2Vertex.getNumTerms());
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      e.printStackTrace();
      System.exit(1);
    }
  }

}
