package edu.osu.slate.relatedness.swwr.data.category;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import edu.osu.slate.relatedness.swwr.data.category.CategoryGraph;

/**
 * Breadth/Depth-first search nodes.
 * <p>
 * Used in the {@link CategoryGraph} class.
 * 
 * @author weale
 *
 */
public class VisitIDNode
{

  /* Array of category IDs of the parents of the node */
  private int[] parentIndices;

  /* Current category index */
  private int currIndex;

  /* Current child position */
  private int currChild;
  
  private int depth;

  /**
   * Creates a new {@link VisitIDNode} with given parents and the current {@link CategoryTitleNode}.
   * 
   * @param cn Current {@link CategoryNode} index
   * @param p indices of the parents of the current category
   */
  public VisitIDNode(int cn, int[] p)
  {
    currIndex = cn;
    parentIndices = p;
    currChild = 0;
  }

 /**
  * Returns the ID of the current category.
  * <p>
  * Uses the {@link CategoryNode} class.
  * 
  * @return ID of the current category.
  */
  public int getCurrentCategoryIndex()
  {
    return currIndex;
  }

 /**
  * Determines if there is another child to visit.
  * 
  * @param arr Array of {@link CategoryNode} objects.
  * @return Boolean determination of whether there's more children to visit.
  */
  public boolean hasNextChild(CategoryNode[] graph)
  {
    if(graph[currIndex].getChildrenCategories() == null)
    {
      return false;
    }
    else
    {
      return (currChild < graph[currIndex].getChildrenCategories().length);
    }
  }//end: hasNextChild(CategoryNode[])

 /**
  * Constructs the children of the current VisitNode for further search.
  * <p>
  * If a child is found to have already been visited, the edge is removed from
  * the graph, as a cycle has been detected.
  * 
  * @return Array of children VisitNodes
  */
  public VisitIDNode getNextChild(CategoryIDNode[] graph)
  {
    /* Get all children in of the current node */
    int childID = graph[currIndex].getChildrenIDs()[currChild];
    currChild++;

    // Get the next child
    //int childID = tmp.getCategoryID();

    // Check if a cycle would be created by expanding this child
    boolean validChild = true;
    for(int j = 0; parentIndices != null && j < parentIndices.length; j++)
    {
      /* If we've already visited the child, we have a cycle */
      if(graph[parentIndices[j]].getCategoryID() == childID)
      {
        validChild = false; // Cycle detected!
      }
    }//end: for(j)

    // Child not found in parent list, therefore eligible for expansion.
    if(validChild)
    {
      // Add current node to the child's parent list
      int[] newParents = new int[parentIndices.length+1];
      System.arraycopy(parentIndices, 0, newParents, 0, parentIndices.length);
      newParents[parentIndices.length] = currIndex;

      Arrays.sort(newParents);
      CategoryIDNode cn = new CategoryIDNode(childID);
      // Add the new VisitNode to the LinkedList
      return new VisitIDNode(Arrays.binarySearch(graph, cn), newParents);
    }
    else
    {
      // Current edge causes a cycle. Remove it.
      graph[currIndex].removeChild(childID);
      return null;
    }

  }//end: makeChildrenVisitNodes()

  /**
   * Constructs the children of the current VisitNode for further search.
   * <p>
   * If a child is found to have already been visited, the edge is removed from
   * the graph, as a cycle has been detected.
   * 
   * @return Array of children VisitNodes
   */
  public VisitIDNode[] makeChildrenVisitNodes(CategoryIDNode[] graph)
  {
    /* Get all children of the current node */
    int[] children = graph[currIndex].getChildrenIDs();
    LinkedList<VisitIDNode> ll = new LinkedList<VisitIDNode>();

    for(int i = 0; children != null && i < children.length; i++)
    {
      int childID = children[i];
      CategoryIDNode cn = new CategoryIDNode(childID);
      int childIndex = Arrays.binarySearch(graph, cn);
      // Check if a cycle would be created by expanding the next child
      boolean validChild = (Arrays.binarySearch(parentIndices, childIndex) < 0);

      // Child not found in parent list, therefore eligible for expansion.
      if(validChild)
      {
        // Add current node to the child's parent list
        int[] newParents = new int[parentIndices.length+1];
        System.arraycopy(parentIndices, 0, newParents, 0, parentIndices.length);
        newParents[parentIndices.length] = currIndex;

        // Sort the array of parents
        Arrays.sort(newParents);
        
        // Add the new VisitNode to the LinkedList
        ll.add(new VisitIDNode(childIndex, newParents));
      }
      else
      {
        // Current edge causes a cycle. Remove it.
        graph[currIndex].removeChild(children[i]);
      }
    }//end: for(i)

    // Convert the LinkedList into a more manageable array.
    VisitIDNode[] vna = new VisitIDNode[ll.size()];
    Iterator<VisitIDNode> it = ll.iterator();
    for(int i = 0; i < vna.length; i++)
    {
      vna[i] = it.next();
    }
    ll.clear();

    return vna;
  }//end: makeChildrenVisitNodes()
}