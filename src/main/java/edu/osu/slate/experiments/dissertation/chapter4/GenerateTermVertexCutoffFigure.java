package edu.osu.slate.experiments.dissertation.chapter4;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.mapping.algorithm.TermToVertexMapping;

public class GenerateTermVertexCutoffFigure {

  /* */
  private static String wordVertexMapFile;

  /* */
  private static String matlabMapCountFileName;

  private static TermToVertexMapping word2VertexF;
  private static TermToVertexMapping word2VertexT;

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
    
    matlabMapCountFileName = Configuration.resultDir +
                             "matlab/ttvfigure_" +
                             Configuration.type.substring(0,6) + "_" +
                             Configuration.mapsource + ".m";
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
    
    // Get Trimmed Histogram
    TreeMap<Integer,Integer> tmF = word2VertexF.generateTrimmedTermVertexHistogram(0.01);

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
    
    // Get Trimmed Histogram
    TreeMap<Integer,Integer> tmT = word2VertexT.generateTrimmedTermVertexHistogram(0.01);
    
    // Get largest mapping count
    int maxVertexCount = Math.max(tmF.lastKey(), tmT.lastKey());
    
    // Cumulative Coverage array
    double[] coverageF = new double[maxVertexCount];
    double[] coverageT = new double[maxVertexCount];
    
    // Find the total number of non-stemmed vertex mappings
    double totalVertexMappingsF = 0.0;
    
    // Iterate through non-stemmed mappings
    Set<Map.Entry<Integer,Integer>> set = tmF.entrySet();
    Iterator<Map.Entry<Integer,Integer>> it = set.iterator();    
    while(it.hasNext())
    {
      Map.Entry<Integer, Integer> me = it.next();
      
      // Get the number of vertices mapped to
      int pos = me.getKey()-1;
      
      // Get the number of terms that have that # of mappings
      int count = me.getValue();
      
      coverageF[pos] = count;
      totalVertexMappingsF += count;
    }//end: while(it)
    
    // Set up cumulative totals
    for(int i = coverageF.length-2; i >= 0; i--)
    {
      coverageF[i] = coverageF[i] + coverageF[i+1];
    }//end: for(i)
    
    // Create matlab array
    StringBuffer valsF = new StringBuffer("");
    for(int i = 0; i < coverageF.length; i++)
    {
      coverageF[i] = (coverageF[i] / totalVertexMappingsF) * 100;
      valsF = valsF.append( coverageF[i] + "," );
    }//end: for(i)
    valsF = valsF.deleteCharAt(valsF.length()-1);
    
    // Find the total number of non-stemmed vertex mappings
    double totalVertexMappingsT = 0.0;
    
    // Iterate through stemmed mappings
    set = tmT.entrySet();
    it = set.iterator();
    while(it.hasNext())
    {
      Map.Entry<Integer, Integer> me = it.next();
      
      // Get the number of vertices mapped to
      int pos = me.getKey() - 1;
      
      // Get the number of terms that have that # of mappings
      int count = me.getValue();
      
      coverageT[pos] = count;
      totalVertexMappingsT += count;
    }//end: while(it)
    
    // Set up cumulative totals
    for(int i = coverageT.length-2; i >= 0; i--)
    {
      coverageT[i] = coverageT[i] + coverageT[i+1];
    }
    
    // Create matlab array
    StringBuffer valsT = new StringBuffer("");
    for(int i = 0; i < coverageT.length; i++)
    {
      coverageT[i] = (coverageT[i] / totalVertexMappingsT) * 100;
      valsT = valsT.append(coverageT[i] + ",");
    }//end: for(i)

    valsT = valsT.deleteCharAt(valsT.length()-1);
    
    StringBuffer positions = new StringBuffer();
    int vals = 10;
    for(int i = 1; i < maxVertexCount; i = i * 10)
    {
      positions = positions.append(vals + ",");
      vals = vals * 10;
    }//end: for(i)
    positions = positions.deleteCharAt(positions.length()-1);
    
    try
    {
      PrintWriter pw = new PrintWriter(matlabMapCountFileName);
      pw.println("A = [" + valsF + "];");
      pw.println("B = [" + valsT + "];");
      pw.println("h1 = loglog(A, 'Color', 'blue', 'LineStyle', '--');");
      pw.println("hold on");
      pw.println("h2 = loglog(B, 'Color', 'red');");
      pw.println("title('Term-to-Vertex Mapping Distribution Using Trimmed " +
                 Configuration.type + "-" +
                 Configuration.mapsource + "')");
      pw.println("xlabel('# Vertices')");
      pw.println("ylabel('Percentage of Terms')");
      pw.println("legend([h1,h2],'-stem','+stem');");
      pw.println("axis tight");
      pw.close();
    }
    catch(Exception e)
    {
      System.out.println("Problem with file: " + matlabMapCountFileName);
      e.printStackTrace();
    }
  }
}