package edu.osu.slate.experiments.dissertation.chapter4;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.osu.slate.relatedness.swwr.algorithm.PageRank;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

public class ExampleGraph {

  /**
   * @param args
   * @throws IOException 
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException, IOException {
    int[][] tmpGraph = new int[6][];
    float[][] tmpProb = new float[6][];
    
    tmpGraph[0] = new int[1];
    tmpProb[0] = new float[1];
    tmpGraph[0][0] = 3;
    tmpProb[0][0] = (float) 1.0;
    
    tmpGraph[1] = new int[4];
    tmpProb[1] = new float[4];
    tmpGraph[1][0] = 0;
    tmpGraph[1][1] = 2;
    tmpGraph[1][2] = 3;
    tmpGraph[1][3] = 5;
    tmpProb[1][0] = (float) 0.25;
    tmpProb[1][1] = (float) 0.25;
    tmpProb[1][2] = (float) 0.25;
    tmpProb[1][3] = (float) 0.25;
    
    tmpGraph[2] = new int[2];
    tmpProb[2] = new float[2];
    tmpGraph[2][0] = 1;
    tmpGraph[2][1] = 3;
    tmpProb[2][0] = (float) 0.5;
    tmpProb[2][1] = (float) 0.5;
    
    tmpGraph[3] = new int[2];
    tmpProb[3] = new float[2];
    tmpGraph[3][0] = 0;
    tmpGraph[3][1] = 4;
    tmpProb[3][0] = (float) 0.5;
    tmpProb[3][1] = (float) 0.5;

    tmpGraph[4] = new int[2];
    tmpProb[4] = new float[2];
    tmpGraph[4][0] = 1;
    tmpGraph[4][1] = 3;
    tmpProb[4][0] = (float) 0.5;
    tmpProb[4][1] = (float) 0.5;

    tmpGraph[5] = new int[3];
    tmpProb[5] = new float[3];
    tmpGraph[5][0] = 1;
    tmpGraph[5][1] = 2;
    tmpGraph[5][2] = 4;
    tmpProb[5][0] = (float) 0.333;
    tmpProb[5][1] = (float) 0.333;
    tmpProb[5][2] = (float) 0.334;

    String tempFilename = "/scratch/weale/data/tmp/wikiex.tmp";
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFilename));
    out.writeObject(tmpGraph);
    out.writeObject(tmpProb);
    out.close();
    
    PageRank pr = new PageRank(tempFilename);
    pr.printPageRank();
    
    SourcedPageRank spr = new SourcedPageRank(tempFilename);
    
    double[] tmp = spr.getRelatedness(1);
    for(int i=0; i<tmp.length; i++)
    {
      System.out.println(i+ ":\t" + tmp[i]);
    }
    
    tmp = spr.getRelatedness(5);
    for(int i=0; i<tmp.length; i++)
    {
      System.out.println(i+ ":\t" + tmp[i]);
    }
  }

}
