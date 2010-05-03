package edu.osu.slate.relatedness.swwr.data.category;

import java.util.Arrays;

public class VertexComparison {
  
  private VertexToCategoryIDCoverage[] vcics;
  
  public VertexComparison()
  {
    vcics = new VertexToCategoryIDCoverage[0];
  }
  
  public void addVertex(int v)
  {
    VertexToCategoryIDCoverage vcc = new VertexToCategoryIDCoverage(v);
    if(Arrays.binarySearch(vcics, vcc, new VCICComparator()) < 0)
    {
      VertexToCategoryIDCoverage[] tmp = new VertexToCategoryIDCoverage[vcics.length+1];
      System.arraycopy(vcics, 0, tmp, 0, vcics.length);
      tmp[vcics.length] = vcc;
      vcics = tmp;
    }//end: if(Arrays)
  }//end: addVertex(int)
  
  public void addCategoryIDToVertex(int v, int cid)
  {
    VertexToCategoryIDCoverage vcc = new VertexToCategoryIDCoverage(v);
    int pos = Arrays.binarySearch(vcics, vcc, new VCICComparator());
    if(pos >= 0)
    { // valid position
      // Add category ID to vertex
      CategoryIDCoverage cic = new CategoryIDCoverage(cid);
      vcics[pos].addCategory(cic);
    }//end: if(pos)
  }//end: addCategoryIDToVertex(int,int)
}
