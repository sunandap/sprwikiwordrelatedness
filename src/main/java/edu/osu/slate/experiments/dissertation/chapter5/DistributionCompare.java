package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.algorithm.PersonalizedPageRank;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.algorithm.VectPersonalizedPageRank;
import edu.osu.slate.relatedness.swwr.algorithm.VectSourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class DistributionCompare
{

  // Names of the input mapping and graph files
  private static String termVertexMapFile;
  private static String graphFile;
  
  private static String task;
  private static int numSplits;
  
  // Mapping and Graph Objects
  private static WikiGraph wgp;
  private static TermToVertexMapping term2Vertex;
  
  /**
   * Sets the names of the files used in this program.
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

    termVertexMapFile = dir + data + "-" +
                        Configuration.mapsource + "-" + 
                        Configuration.stemming + ".tvc";
    
    graphFile = dir + data + transitionSource + ".wgp";   
  }
  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException
  {
    if(args.length > 0)
    {
      Configuration.parseConfigurationFile(args[1]);
      Configuration.resultDir = args[0];
    }
    else 
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
      Configuration.resultDir = "/scratch/weale/results/pprresults/";
    }
    
    setFiles();
    
    // Open Wiki Graph
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
    
    // Open Mapping
    System.out.println("Opening Word To Vertex Mapping: " + 
        Configuration.mapsource + "-" + Configuration.stemming);
    term2Vertex = TermToVertexMapping.getMapping(termVertexMapFile);
    term2Vertex.setCutoff(0.01);

//    VectPersonalizedPageRank spr = new VectPersonalizedPageRank(wgp);
    VectSourcedPageRank spr = new VectSourcedPageRank(wgp);
    int v1 = term2Vertex.getVertexMappings("smile")[0].getVertexCounts()[0].getVertex();
    int v2 = term2Vertex.getVertexMappings("chord")[0].getVertexCounts()[0].getVertex();
    System.out.println(spr.getRelatedness(v1, v2));
    
//    System.out.println("coast");
//    double [] d1 = spr.getRelatedness(term2Vertex.getVertexMappings("coast")[0].getVertexCounts()[0].getVertex());
//
//    System.out.println("shore");
//    double [] d2 = spr.getRelatedness(term2Vertex.getVertexMappings("shore")[0].getVertexCounts()[0].getVertex());
//
//    StringBuffer str1 = new StringBuffer("A = [");
//    StringBuffer str2 = new StringBuffer("B = [");
//    for(int i = 0; i < d1.length; i++)
//    {
//      if(Math.random() < 0.2)
//      {
//        str1 = str1.append(d1[i] + ";");
//        str2 = str2.append(d2[i] + ";");
//      }
//    }
//    
//    str1 = str1.deleteCharAt(str1.length()-1).append("];");
//    str2 = str2.deleteCharAt(str2.length()-1).append("];");
//    
//    PrintWriter pw = new PrintWriter("/u/weale/Desktop/disttest.m");
//    pw.println(str1);
//    pw.println(str2);
//    pw.close();
  }
}
