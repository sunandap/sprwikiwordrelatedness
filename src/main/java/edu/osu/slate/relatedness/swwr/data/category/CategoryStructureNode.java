package edu.osu.slate.relatedness.swwr.data.category;

import java.util.Arrays;

public class CategoryStructureNode
{
  private CategoryIDCoverage cic;
  private CategoryStructureNode[] children;
  
  public CategoryStructureNode(CategoryIDCoverage c)
  {
    cic = c;
    children = new CategoryStructureNode[0];
  }
  
  public void addChild(CategoryStructureNode csn)
  {
    if(Arrays.binarySearch(children, csn, new CSNComparator()) < 0)
    {
      CategoryStructureNode[] tmp = new CategoryStructureNode[children.length+1];
      System.arraycopy(children, 0, tmp, 0, children.length);
      tmp[children.length] = csn;
      children = tmp;
      Arrays.sort(children, new CSNComparator());
    }//end: if()
  }//end: addChild(CategoryStructureNode)
  
  public int getCategoryID()
  {
    return cic.getCatID();
  }
  
  public int propogateCategoryCount() {
    for(int i = 0; i < children.length; i++)
    {
      cic.addToCategoryCount(propogateCategoryCount());
    }//end: for(i)
    
    return cic.getRawCategoryCount();
  }
}