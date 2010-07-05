package edu.osu.slate.experiments.dissertation.chapter7;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

import edu.osu.slate.experiments.synonym.RelVertex;
import edu.osu.slate.experiments.synonym.RelVertexComparator;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.graph.IDIDRedirect;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexToTermMapping;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class TopNGraphManipulation {

  private static String termVertexMapFile, vertexTermMapFile;
  
  private static WikiGraph wgp;
  private static String wgpFileName;
  private static String wgpOutputFileName;
  private static TreeMap<Integer,Integer> catToVertexCount;
  
  private static Scanner in;
  private static ObjectOutputStream out;
  private static TermToVertexMapping term2Vertex;
  private static VertexToTermMapping vertex2Term;
  
  private static void setFiles()
  {
    wgpFileName = Configuration.baseDir + "/" + 
                  Configuration.binaryDir + "/" +
                  Configuration.type+ "/" +
                  Configuration.date + "/" +
                  Configuration.type + "-"+
                  Configuration.date + "-" +
                  Configuration.graph + ".wgp";
    
    wgpOutputFileName = Configuration.baseDir + "/" + 
                        Configuration.binaryDir + "/" +
                        Configuration.type+ "/" +
                        Configuration.date + "/" +
                        Configuration.type + "-"+
                        Configuration.date + "-" +
                        Configuration.graph + "-simple2.wgp";
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
                 Configuration.binaryDir + "/" +
                 Configuration.type + "/" +
                 Configuration.date + "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;
    
    termVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".tvc";
    
    vertexTermMapFile = dir + data + "-" +
    "extitle-f" + ".vtc";
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    
    Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/WordPairTask.xml");

    setFiles();
    
    // Open Mapping
    System.out.println("Opening Word To Vertex Mapping: " + 
        Configuration.mapsource + "-" + Configuration.stemming);
    term2Vertex = TermToVertexMapping.getMapping(termVertexMapFile);
    term2Vertex.setCutoff(0.01);
    
    TermToVertexCount [] arr = term2Vertex.getVertexMappings("gehrig");
    if(arr == null || arr.length==0)
    {
      System.out.println("Invalid Page");
      System.exit(1);
    }
    // Open Wiki Graph
    try
    {
      System.out.println("Opening Vertex To Word Mapping");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(vertexTermMapFile));
      vertex2Term = (VertexToTermMapping) in.readObject(); 
      in.close();

      System.out.println("Opening Wiki Graph");
      in = new ObjectInputStream(new FileInputStream(wgpFileName));
      wgp = (WikiGraph) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wgpFileName);
      e.printStackTrace();
      System.exit(1);
    }
    
    SourcedPageRank spr = new SourcedPageRank(wgp);
    int size = spr.getNumVertices();
    RelVertex[] rels = new RelVertex[size];
    for(int i = 0; i < rels.length; i++)
    {
      rels[i] = new RelVertex(-10, i);
    }
      

    VertexCount [] vc = arr[0].getVertexCounts();
    for(int i = 0; i < vc.length; i++)
    {
      int v = vc[i].getVertex();
      double[] vals = spr.getRelatedness(v);
      for(int j = 0; j < vals.length; j++)
      {
        if(rels[j].getRel() < vals[j] && j != v)
        {
          rels[j].setRel(vals[j]);
        }
      }
    }
    
    Arrays.sort(rels, new RelVertexComparator());
    for(int i = 0; i < 20; i++)
    {
      for(int j=0; j<vertex2Term.getTermMappings(rels[i].getVertex()).length; j++)
      {
        System.out.print(vertex2Term.getTermMappings(rels[i].getVertex())[j].getTerm() + " ");
      }
      System.out.println();
    }
    
  }
}
