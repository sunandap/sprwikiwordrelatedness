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

public class GenerateMappingSupportFigure {

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
                             "matlab/mapcount_" +
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
    
    TreeMap<Integer,Integer> tm = word2VertexF.generateCoverageSupportHistogram();
    Set<Map.Entry<Integer,Integer>> set = tm.entrySet();
    Iterator<Map.Entry<Integer,Integer>> it = set.iterator();
    double[] coverage = new double[101];
    double totalSupport = 0.0;
    while(it.hasNext())
    {
      Map.Entry<Integer, Integer> me = it.next();
      int pos = me.getKey();
      int count = me.getValue();
      System.out.println(pos + "\t" + count);
      coverage[pos] = count;
      totalSupport += count;
    }//end: while(it)
    
    for(int i = coverage.length-2; i >= 0; i--)
    {
      coverage[i] = coverage[i] + coverage[i+1];
    }
    
    StringBuffer valsF = new StringBuffer("");
    for(int i = 0; i < coverage.length; i++)
    {
      coverage[i] = (coverage[i] / totalSupport) * 100;
      valsF = valsF.append(coverage[i] + ",");
    }//end: for(i)

    valsF = valsF.deleteCharAt(valsF.length()-1);
    
    tm = word2VertexT.generateCoverageSupportHistogram();
    set = tm.entrySet();
    it = set.iterator();
    coverage = new double[101];
    totalSupport = 0.0;
    while(it.hasNext())
    {
      Map.Entry<Integer, Integer> me = it.next();
      int pos = me.getKey();
      int count = me.getValue();
      System.out.println(pos + "\t" + count);
      coverage[pos] = count;
      totalSupport += count;
    }//end: while(it)
    
    for(int i = coverage.length-2; i >= 0; i--)
    {
      coverage[i] = coverage[i] + coverage[i+1];
    }
    
    StringBuffer valsT = new StringBuffer("");
    for(int i = 0; i < coverage.length; i++)
    {
      coverage[i] = (coverage[i] / totalSupport) * 100;
      valsT = valsT.append(coverage[i] + ",");
    }//end: for(i)

    valsT = valsT.deleteCharAt(valsT.length()-1);
    
    try
    {
      PrintWriter pw = new PrintWriter(matlabMapCountFileName);
      pw.println("A = [" + valsF + "];");
      pw.println("B = [" + valsT + "];");
      pw.println("h1 = plot(A, 'Color', 'blue');");
      pw.println("hold on");
      pw.println("h2 = plot(B, 'Color', 'red');");
      pw.println("title('Word-to-Vertex Mapping Distribution for the Word Pair Task Using " +
                 Configuration.type + "-" +
                 Configuration.mapsource + "')");
      pw.println("xlabel('Mapping Support Percentage')");
      pw.println("ylabel('Percentage of Vertex Maps')");
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