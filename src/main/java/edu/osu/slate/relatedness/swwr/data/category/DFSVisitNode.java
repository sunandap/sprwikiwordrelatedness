package edu.osu.slate.relatedness.swwr.data.category;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import edu.osu.slate.relatedness.swwr.data.category.CategoryGraph;

/**
 * Depth-first search nodes.
 * <p>
 * Used in the {@link CategoryGraph} class.
 * 
 * @author weale
 *
 */
public class DFSVisitNode
{

  /* Array of category IDs of the parents of the node */
  private int[] parents;

  /* Current category ID */
  private int currNode;

  /* Current child position */
  private int currChild;
  
  /* Number of times this node has been visited */
  private int visitCount;

  /**
   * Creates a new {@link DFSVisitNode} with given parents and the current {@link CategoryTitleNode}.
   * 
   * @param cn Current category ID
   * @param p IDs of the parents of the current category
   */
  public DFSVisitNode(int cn, int[] p)
  {
    currNode = cn;
    parents = p;
    currChild = 0;
    if(Arrays.binarySearch(p, cn) >= 0)
    {
      visitCount = 1;
    }
    else
    {
      visitCount = 0;
    }
  }
  
  public boolean isDuplicateEdge()
  {
    return (visitCount == 1);
  }
  
 /**
  * Returns the ID of the current category.
  * <p>
  * Uses the {@link CategoryNode} class.
  * 
  * @return ID of the current category.
  */
  public int getCurrentCategoryID()
  {
    return currNode;
  }

 /**
  * Determines if there is another child to visit.
  * 
  * @param arr Array of {@link CategoryNode} objects.
  * @return Boolean determination of whether there's more children to visit.
  */
  public boolean hasNextChild(CategoryNode[] arr)
  {
    if(arr[currNode].getChildrenCategories() == null)
    {
      return false;
    }
    else
    {
      return (currChild < arr[currNode].getChildrenCategories().length);
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
  public DFSVisitNode getNextChild(CategoryNode[] arr)
  {
    /* Get all children in of the current node */
    CategoryNode tmp = arr[currNode].getChildrenCategories()[currChild];
    currChild++;

    // Get the next child
    int childID = tmp.getCategoryID();

    // Check if a cycle would be created by expanding this child
    boolean validChild = !isDuplicateEdge();
    for(int j = 0; parents != null && j < parents.length; j++)
    {
      /* If we've already visited the child, we have a cycle */
      if(arr[parents[j]].getCategoryID() == childID)
      {
        validChild = false; // Cycle detected!
      }
    }//end: for(j)

    // Child not found in parent list, therefore eligible for expansion.
    if(validChild)
    {
      // Add current node to the child's parent list
      int[] newParents = new int[parents.length+1];
      System.arraycopy(parents, 0, newParents, 0, parents.length);
      newParents[parents.length] = currNode;

      Arrays.sort(newParents);
      
      // Add the new VisitNode to the LinkedList
      return new DFSVisitNode(Arrays.binarySearch(arr, tmp), newParents);
    }
    else
    {
      // Current edge causes a cycle. Remove it.
      arr[currNode].removeChild(childID);
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
  public DFSVisitNode[] makeChildrenVisitNodes(CategoryNode[] arr)
  {
    /* Get all children in of the current node */
    CategoryNode[] tmp = arr[currNode].getChildrenCategories();
    LinkedList<DFSVisitNode> ll = new LinkedList<DFSVisitNode>();

    for(int i = 0; tmp != null && i < tmp.length; i++)
    {
      // Get the next child
      int childID = tmp[i].getCategoryID();

      // Check if a cycle would be created by expanding this child
      boolean validChild = !isDuplicateEdge();
      for(int j = 0; parents != null && j < parents.length; j++)
      {
        /* If we've already visited the child, we have a cycle */
        if(arr[parents[j]].getCategoryID() == childID )
        {
          validChild = false; // Cycle detected!
        }
      }//end: for(j)

      // Child not found in parent list, therefore eligible for expansion.
      if(validChild)
      {
        // Add current node to the child's parent list
        int[] newParents = new int[parents.length+1];
        System.arraycopy(parents, 0, newParents, 0, parents.length);
        newParents[parents.length] = currNode;

        // Sort the array of parents
        Arrays.sort(newParents);
        
        // Add the new VisitNode to the LinkedList
        ll.add(new DFSVisitNode(Arrays.binarySearch(arr, tmp[i]), newParents));
      }
      else
      {
        // Current edge causes a cycle. Remove it.
        arr[currNode].removeChild(childID);
      }
    }//end: for(i)

    // Convert the LinkedList into a more manageable array.
    DFSVisitNode[] vna = new DFSVisitNode[ll.size()];
    Iterator<DFSVisitNode> it = ll.iterator();
    for(int i = 0; i < vna.length; i++)
    {
      vna[i] = it.next();
    }
    ll.clear();

    return vna;
  }//end: makeChildrenVisitNodes()
}