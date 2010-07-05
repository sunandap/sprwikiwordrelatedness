package edu.osu.slate.experiments.dissertation.chapter7;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.graph.IDIDRedirect;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

public class ModifiedRootGraphCreation {

  private static String vxcFileName;
  
  private static WikiGraph wgp;
  private static String wgpFileName;
  private static String wgpOutputFileName;
  private static TreeMap<Integer,Integer> catToVertexCount;
  
  private static Scanner in;
  private static ObjectOutputStream out;
  
  private static void setFiles()
  {
    vxcFileName = Configuration.baseDir + "/" + 
                  Configuration.binaryDir + "/" +
                  Configuration.type+ "/" +
                  Configuration.date + "/" +
                  Configuration.type + "-"+
                  Configuration.date + "-" +
                  Configuration.graph + ".vxc";
    
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
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateTitleWordMapping.xml");

    setFiles();
    
    // Open Wiki Graph
    try
    {
      System.out.println("Opening Wiki Graph");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(wgpFileName));
      wgp = (WikiGraph) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wgpFileName);
      e.printStackTrace();
      System.exit(1);
    }
    
    int[][] vertexToCategories = null;
    try
    {
      System.out.println("Initializing Valid ID List.");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(vxcFileName));
      vertexToCategories = (int[][]) in.readObject();
      in.close();
    }//end: try {}
    catch(Exception e)
    {
      System.out.println("Problem reading file: " + vxcFileName);
      System.exit(1);
    }//end: catch(IOException)
    
    System.out.println(wgp.getNumEdges());
    int count = 0;
    
    catToVertexCount = new TreeMap<Integer,Integer>();
    for(int i = 0; i < vertexToCategories.length; i++)
    {
      for(int j = 0; vertexToCategories[i] != null && 
                     j < vertexToCategories[i].length; j++)
      {
        int vertexCount = 1;
        
        if(catToVertexCount.containsKey(vertexToCategories[i][j]))
        {
          vertexCount += catToVertexCount.get(vertexToCategories[i][j]);
        }
        
        catToVertexCount.put(vertexToCategories[i][j], vertexCount);
      }
    }
    
    for(int i = 0; i < wgp.getNumVertices(); i++)
    {
      int[] outVertices = wgp.getOutboundLinks(i);
      float[] prevTrans = wgp.getOutboundTransitions(i);
      if(outVertices != null)
      {
        float[] newValues = new float[outVertices.length];
        float sum = 0;
        for(int j = 0; j < outVertices.length; j++)
        {
          float overlap = (float) categoryOverlap(i, outVertices[j], vertexToCategories);
          newValues[j] = 1 + overlap;
          sum += newValues[j];
        }//end: for(j)
        
        for(int j = 0; j < outVertices.length; j++)
        {
          newValues[j] = newValues[j] / sum;
        }//end: for(j)
        
        wgp.setOutboundTransitions(i, newValues);
      }//end: if(outVertices)
      
    }//end: for(i)
    
    System.out.println(count);
    
    try
    {
      out = new ObjectOutputStream(new FileOutputStream(wgpOutputFileName));
      out.writeObject(wgp);
      out.close();
    }
    catch(Exception e)
    {
      System.out.println("Problem writing to file: " + wgpOutputFileName);
      System.exit(1);
    }//end: catch(IOException)
  }
  
//  private static double categoryOverlap(int x, int y, int[][] vertexToCategories)
//  {
//    if(vertexToCategories[x] == null || vertexToCategories[y] == null)
//    {
//      return 0;
//    }
//    
//    int count = wgp.getNumVertices();
//    for(int i=0; i < vertexToCategories[x].length; i++)
//    {
//      if(Arrays.binarySearch(vertexToCategories[y], vertexToCategories[x][i]) >= 0)
//      {
//        count = Math.min(count, catToVertexCount.get(vertexToCategories[x][i]));
//      }
//    }//end: for(i)
//    
//    double val = 1.0 - ( (Math.log(count)+1) / Math.log(wgp.getNumVertices()) );
//    return Math.max(0.0, val);
//  }
  
  private static int categoryOverlap(int x, int y, int[][] vertexToCategories)
  {
    
    if(vertexToCategories[x] == null || vertexToCategories[y] == null)
    {
      return 0;
    }
    
    int count = 0;
    for(int i=0; i < vertexToCategories[x].length; i++)
    {
      if(Arrays.binarySearch(vertexToCategories[y], vertexToCategories[x][i]) >= 0)
      {
        count++;
      }
    }//end: for(i)
    
    return count;
  }
}
