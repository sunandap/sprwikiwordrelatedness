package edu.osu.slate.relatedness.swwr.data.category;

import java.io.*;
import java.util.*;

/**
 * Category graph nodes.
 * <p>
 * Each contains a list of parent IDs, children IDs.
 * 
 * @author weale
 *
 */
public class CategoryIDNode implements Serializable, Comparable<Object>
{
  /* Serialization variable */
  private static final long serialVersionUID = 1L;

  /* Category ID */
  private int catID;
  
  /* Integer array of the Wiki IDs for the parents of the current node. */
  private int[] parentIDs;

  /* Integer array of the Wiki IDs for the children of the current node.*/
  private int[] childrenIDs;

  /* Coverage count of the leaves of the category */
  private int vertexCoverage;
  private int inboundEdgeCoverage;
  private int outboundEdgeCoverage;
  
  /* Array of all vertices that are immediate children of the category. 
   * 
   * Array contains vertex numbers from the graph.
   */
  private int[] immediateVertices;

  /* Array of all vertices that belong under this category.
   * 
   * Array contains vertex numbers from the graph.
   */
  private int[] allVertices;

  private boolean AllChildrenVerticesFinalized;
  private boolean inboundEdgeCountsFinalized;
  private boolean outboundEdgeCountsFinalized;
  
  /**
   * Creates a category node for the tree.
   * <p>
   * No parents and children are initialized on the node.
   * 
   * @param name Category name.
   */
  public CategoryIDNode(int id)
  {
    catID = id;
    
    parentIDs = null;
    childrenIDs = null;
    
    immediateVertices = null;
    allVertices = null;
    
    AllChildrenVerticesFinalized = false;
    inboundEdgeCountsFinalized = false;
    outboundEdgeCountsFinalized = false;
  }//end: CategoryNode(String)

  /**
   * Adds a parent to the current node.
   * <p>
   * Only adds the ID if the parent is not already listed.
   * 
   * @param parentID Integer category ID of the parent.
   */
  public void addParent(int parentID)
  {
    //No existing parents
    if(parentIDs == null)
    { 
      parentIDs = new int[1];
      parentIDs[0] = parentID;
    }
    else if(Arrays.binarySearch(parentIDs, parentID) < 0)
    { // Parent not found in existing parents

      // Create new array, copy parent into new array
      int[] tmpParents = new int[parentIDs.length+1];
      System.arraycopy(parentIDs, 0, tmpParents, 0, parentIDs.length);
      tmpParents[parentIDs.length] = parentID;
      parentIDs = tmpParents;
      
      Arrays.sort(parentIDs);
      tmpParents = null;
    }
  }//end: addParent(int)

  /**
   * Adds a child category ID to the current node.
   * <p>
   * Only adds the ID if the child is not already listed.
   * 
   * @param childID Integer category ID of the child.
   */
  public void addChild(int childID)
  {
    //No existing children
    if(childrenIDs == null)
    {
      childrenIDs = new int[1];
      childrenIDs[0] = childID;
    }
    else if(Arrays.binarySearch(childrenIDs, childID) < 0)
    { // Child not found in existing children
      
      // Create new array, copy child into new array
      int[] tmpChildren = new int[childrenIDs.length+1];
      System.arraycopy(childrenIDs, 0, tmpChildren, 0, childrenIDs.length);
      tmpChildren[childrenIDs.length] = childID;
      childrenIDs = tmpChildren;
      
      Arrays.sort(childrenIDs);
      tmpChildren = null;
    }
  }//end: addChild(int)

  /**
   * Removes the child ID from the list of children.
   * <p>
   * Used by {@link CategoryGraph} and {@link VisitNode}
   * to break cycles in the graph.
   * <p>
   * Does NOT do error checking. Child ID is assumed
   * to be present in the array.
   * 
   * @param id Category ID of the child to remove.
   */
  public void removeChild(int id)
  {
    int[] tmpChildren = new int[childrenIDs.length-1];
    int currIndex=0, newIndex=0;
    while(newIndex < tmpChildren.length)
    {
      if(childrenIDs[currIndex] != id)
      {
        tmpChildren[newIndex] = childrenIDs[currIndex];
        newIndex++;
      }
      currIndex++;
    }//end: while(newIndex)
    
    childrenIDs = tmpChildren;
    Arrays.sort(childrenIDs);
  }//end: removeChild(int)

  /**
   * Removes the parent ID from the list of parents.
   * <p>
   * Used by {@link CategoryGraph} to remove non-root reachable nodes in the graph.
   * <p>
   * Does NOT do error checking. Parent ID is assumed
   * to be present in the array.
   * 
   * @param id Category ID of the parent to remove.
   */
  public void removeParent(int id)
  {
    int[] tmpParents = new int[parentIDs.length-1];
    int currIndex=0, newIndex=0;
    while(newIndex < tmpParents.length)
    {
      if(parentIDs[currIndex]  != id )
      {
        tmpParents[newIndex] = parentIDs[currIndex];
        newIndex++;
      }
      currIndex++;
    }//end: while(newIndex)
    
    parentIDs = tmpParents;
    Arrays.sort(parentIDs);
  }//end: removeParent(int)

  /**
   * Gets the ID of the category.
   * 
   * @return Integer containing the ID of the category.
   */
  public int getCategoryID()
  {
    return catID;
  }

  /**
   * Compares a given Wiki ID to the ID of this CategoryNode.
   * 
   * @param id Category ID being searched for.
   * @return Boolean value of whether or not the searched ID is the ID of the node.
   */
  public boolean isCategoryID(int id)
  {
    return (catID == id);
  }

  /**
   * Adds the given vertex number to the current list of category leaves.
   * 
   * @param vertexNum Graph vertex number.
   */
  public void addImmediateVertex(int vertexNum)
  {
    // No immediate vertices
    if(immediateVertices == null)
    {
      immediateVertices = new int[1];
      immediateVertices[0] = vertexNum;
    }
    else if(Arrays.binarySearch(immediateVertices, vertexNum) < 0)
    {  // Vertex not already in category
      int[] tmpVertices = new int[immediateVertices.length+1];
      System.arraycopy(immediateVertices, 0, tmpVertices, 0, immediateVertices.length);
      tmpVertices[immediateVertices.length] = vertexNum;
      immediateVertices = tmpVertices;
      
      Arrays.sort(immediateVertices);
      tmpVertices = null;
    }
  }//end: addImmediateVertex(int)

 /**
  * Adds the given vertex number to the current list of category vertices.
  * 
  * @param vertexNum Graph vertex number.
  */
  private void addAncestorVertex(int vertexNum)
  {
    // No ancestor vertices
    if(allVertices == null)
    {
      allVertices = new int[1];
      allVertices[0] = vertexNum;
    }
    else if(Arrays.binarySearch(allVertices, vertexNum) < 0)
    {  // Vertex not already an ancestor
      int[] tmpLeaves = new int[allVertices.length+1];
      System.arraycopy(allVertices, 0, tmpLeaves, 0, allVertices.length);
      tmpLeaves[allVertices.length] = vertexNum;
      allVertices = tmpLeaves;
      
      Arrays.sort(allVertices);
      tmpLeaves = null;
    }
  }//end: addAncestorVertex(int)

 /**
  * Adds the given leafID to the current list of category leaves.
  * 
  * @param leafNum ID of the leaf
  */
  private void addAncestorVertices(int[] vertices)
   {
     if(allVertices == null)
     {
       allVertices = vertices;
     }
     else
     {
       for(int i = 0; i < vertices.length; i++)
       {
         addAncestorVertex(vertices[i]);
       }//end: for(i)
     }
   }//end: addAncestorLeaf(int)
  
 /**
  * Gets the all graph vertices found under this category.
  * 
  * @return Array of graph vertex numbers.
  */
  public int[] getAllVertices()
  {
    return allVertices;
  }//end: getAllLeaves()

 /**
  * Gets the immediate graph vertices under this category.
  * 
  * @return Array of graph vertex numbers.
  */
  public int[] getImmediateVertices()
  {
    return immediateVertices;
  }//end: getImmediateLeaves()

  /**
   * Finds the in-bound edge count for a the category.
   * <p>
   * Given the inverse graph, count the number of edges that are in-bound to the leaves of the category.
   * 
   * @param wg {@link WikiInvGraph}
   * @return Count of the number of edges in-bound to the leaves.
   */
//  public void setOutboundVertexEdgeCount(WikiGraph wg)
//  {
//    int count = 0;
//    
//    if(childrenIDs != null)
//    {
//      // Visit all children first
//      for(int i = 0; i < childrenIDs.length; i++)
//      {
//        
//        if(!childrenIDs[i].outboundEdgeCountsFinalized)
//        {
//          childrenIDs[i].setOutboundVertexEdgeCount(wg);
//        }
//        
//        count += childrenIDs[i].inboundEdgeCoverage;
//      }//end: for(i)
//    }//end: children
//
//    // Add children vertex counts
//    for(int i = 0; immediateVertices != null &&
//                   i < immediateVertices.length; i++)
//    {
//      // For each graph vertex, get the in-bound link array
//      int[] arr = wg.getOutboundLinks(immediateVertices[i]);
//
//      if(arr != null)
//      { 
//        //Valid inbound link set
//        count += arr.length;
//      }
//    }//end: for(i)
//    
//    outboundEdgeCountsFinalized = true;
//    outboundEdgeCoverage = count;
//  }//end: setInboundLeaveEdges(WikiInvGraph)
  
  /**
   * Finds the in-bound edge count for a the category.
   * <p>
   * Given the inverse graph, count the number of edges that are in-bound to the leaves of the category.
   * 
   * @param wg {@link WikiInvGraph}
   * @return Count of the number of edges in-bound to the leaves.
   */
//  public void setInboundVertexEdgeCount(WikiInvGraph wg)
//  {
//    int count = 0;
//    
//    if(childrenIDs != null)
//    {
//      // Visit all children first
//      for(int i = 0; i < childrenIDs.length; i++)
//      {
//        
//        if(!childrenIDs[i].inboundEdgeCountsFinalized)
//        {
//          childrenIDs[i].setInboundVertexEdgeCount(wg);
//        }
//        
//        count += childrenIDs[i].inboundEdgeCoverage;
//      }//end: for(i)
//    }//end: children
//
//    // Add children vertex counts
//    for(int i = 0; immediateVertices != null &&
//                   i < immediateVertices.length; i++)
//    {
//      // For each graph vertex, get the in-bound link array
//      int[] arr = wg.getInboundLinks(immediateVertices[i]);
//
//      if(arr != null)
//      { 
//        //Valid inbound link set
//        count += arr.length;
//      }
//    }//end: for(i)
//    
//    inboundEdgeCountsFinalized = true;
//    inboundEdgeCoverage = count;
//  }//end: setInboundLeaveEdges(WikiInvGraph)

  /**
   * Sets the coverage based on the number of category leaves and the number of overall vertices.
   */
  public void setVertexCoverage()
  {
    vertexCoverage = 0;

    if(allVertices != null)
    {
      vertexCoverage = allVertices.length;
    }
  }//end: setVertexCoverage()
  
 /**
  * Sets the ImmediateLeavesFinalized flag.
  */
  public void finalizeImmediateVertices()
  {
    //ImmediateLeavesFinalized = true;
  }//end: finalizeImmediateLeaves()

// /**
//  * Finalize all the category vertices.
//  */
//  public void finalizeAllVertices()
//  {
//    if(this.childrenIDs != null)
//    { 
//      // Finalize all children
//      for(int i = 0; i < childrenIDs.length; i++)
//      {
//        // Check to see if child is finalized
//        if(!childrenIDs[i].AllChildrenVerticesFinalized)
//        {
//          childrenIDs[i].finalizeAllVertices();
//        }
//        this.addAncestorVertices(childrenIDs[i].allVertices);
//      }//end: for(i)
//    }//end: if(this.children)
//    
//    // Set Leaves Finalized Flag
//    this.finalizeImmediateVertices();
//    this.addAncestorVertices(this.immediateVertices);
//    AllChildrenVerticesFinalized = true;
//  }//end: finalizeAllLeaves()

 /**
  * Gets the vertex coverage amount of the {@link CategoryIDNode}.
  * 
  * @return Vertex coverage amount of the {@link CategoryIDNode}.
  */
  public int getVertexCoverage()
  {
    return vertexCoverage;
  }// getCoverage()

  /**
   * Gets the in-bound edge coverage amount of the {@link CategoryIDNode}.
   * 
   * @return In-bound edge coverage amount of the {@link CategoryIDNode}.
   */
   public int getInboundEdgeCoverage()
   {
     return inboundEdgeCoverage;
   }// getCoverage()
   
   /**
    * Gets the out-bound edge coverage amount of the {@link CategoryIDNode}.
    * 
    * @return Out-bound edge coverage amount of the {@link CategoryIDNode}.
    */
    public int getOutboundEdgeCoverage()
    {
      return outboundEdgeCoverage;
    }// getCoverage()
    
 /**
  * Determines if the given vertex number is an immediate vertex of the category.
  * 
  * @return Boolean value based on if the leaf is found.
  */
  public boolean isImmediateVertex(int vertex)
  {
    if(immediateVertices != null)
    {
      return (Arrays.binarySearch(immediateVertices, vertex) >= 0);
    }
    else
    {
      return false;
    }
  }//end: isImmediateVertex(int)

 /**
  * Determines if the given vertex number is a vertex of the category.
  * 
  * @return Boolean value based on if the leaf is found.
  */
  public boolean isAncestorVertex(int vertex)
  {
    if(allVertices != null)
    {
      return (Arrays.binarySearch(allVertices, vertex) >= 0);
    }
    else
    {
      return false;
    }
  }//end: isAncestorVertex(int)

 /**
  * Gets the list of children IDs
  * 
  * @return Integer array of children IDs.
  */
  public int[] getChildrenIDs()
  {
    return childrenIDs;
  }//end: getChildren()

 /**
  * Gets the list of parent IDs
  * 
  * @return Integer array of parent IDs.
  */
  public int[] getParentIDs()
  {
    return parentIDs;
  }//end: getParents()

  /**
   * Prints the contents of the node.
   */
//  public void print()
//  {
//    //Print name of the node
//    System.out.print(catID);
//
//    //Print parents of the node
//    System.out.print("\tParents:");
//    if(parents != null)
//    {
//      for(int j = 0; j < parents.length; j++)
//      {
//        System.out.print(" "+ parents[j].getCategoryID());
//      }
//    }
//    else
//    {
//      System.out.print("-");
//    }
//
//    //Print children of the node
//    System.out.print("\tChildren:");
//    if(children != null)
//    {
//      for(int j = 0; j < children.length; j++)
//      {
//        System.out.print(" "+ children[j].getCategoryID());
//      }
//    }
//    else
//    {
//      System.out.print("-");
//    }
//
//    //Print category coverage
//    System.out.println("\t" + vertexCoverage);
//  }//end: print()

 /**
  * Determines if the given {@link CategoryIDNode} is a descendant of the current node.
  * 
  * @param cn {@link CategoryIDNode} to find.
  * @return Whether the given category is a descendant.
  */
//  public boolean isDescendant(CategoryIDNode cn) 
//  {
//    return checkIfDescendant(this, cn);
//  }

 /**
  * Determines if a given {@link CategoryIDNode} is a descendant of another category.
  * 
  * @param category Current {@link CategoryIDNode} to search.
  * @param findMe {@link CategoryIDNode} to find.
  * @return Boolean as the the decendant nature of the given {@link CategoryIDNode}.
  */
//  public boolean checkIfDescendant(CategoryIDNode category, CategoryIDNode findMe)
//  {
//    /* BASE CASES */
//    if(category.equals(findMe))
//    {//Found!
//      return true;
//    }
//    else if(category.children == null)
//    {//No more children
//      return false;
//    }
//
//    // Recursively search descendants
//    boolean found = false;
//    for(int i = 0; !found && i < category.children.length; i++)
//    {
//      found = checkIfDescendant(category.children[i], findMe);
//    }//end: for(i)
//    
//    return found;
//  }//end: checkIfDescendant(CategoryNode, CategoryNode)

 /**
  * Writes a {@link CategoryIDNode} to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    /* Write node information */
    out.writeInt(catID);
    out.writeObject(parentIDs);
    out.writeObject(childrenIDs);
    
    /* Write various counts */
    out.writeInt(vertexCoverage);
    out.writeInt(inboundEdgeCoverage);
    out.writeInt(outboundEdgeCoverage);

    /* Write the count status */
    out.writeBoolean(AllChildrenVerticesFinalized);
    out.writeBoolean(inboundEdgeCountsFinalized);
    out.writeBoolean(outboundEdgeCountsFinalized);
    
    /* Write the list of all vertices */
    if(allVertices != null)
      out.writeObject(allVertices);
    else
      out.writeObject(new int[0]);

    /* Write the list of immediate vertices */
    if(immediateVertices != null)
    {
      out.writeObject(immediateVertices);
    } 
    else
    {
      out.writeObject(new int[0]);
    }
  }//end: writeObject(ObjectOutputStream)

  /**
   * Used to convert the {@link CategoryIDNode} links to their integer positions for writing.
   * 
   * @param arr Array of {@link CategoryNodes} representing the category graph.
   */
//  public void convertEdgesBeforeWrite(CategoryIDNode[] arr)
//  {
//    if(parents != null)
//    {
//      parentsIndex = new int[parents.length];
//      for(int i=0; i<parents.length; i++)
//      {
//        parentsIndex[i] = Arrays.binarySearch(arr, parents[i]);
//      }
//    }
//    else
//    {
//      parentsIndex = null;
//    }
//
//    if(children != null)
//    {
//      childrenIndex = new int[children.length];
//      for(int i=0; i<children.length; i++)
//      {
//        childrenIndex[i] = Arrays.binarySearch(arr, children[i]);
//      }
//    }
//    else
//    {
//      childrenIndex = null;
//    }
//  } 
  
  /**
   * Reads a {@link CategoryIDNode} from a file.
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    /* Read node information */
    catID                = in.readInt();
    parentIDs            = (int[]) in.readObject();
    childrenIDs          = (int[]) in.readObject();
    
    /* Read count information */
    vertexCoverage       = in.readInt();
    inboundEdgeCoverage  = in.readInt();
    outboundEdgeCoverage = in.readInt();

    /* Read status information */
    AllChildrenVerticesFinalized  = in.readBoolean();
    inboundEdgeCountsFinalized  = in.readBoolean();
    outboundEdgeCountsFinalized  = in.readBoolean();
    
    /* Read all vertex coverage information */
    int[] tmp = (int[]) in.readObject();
    if(tmp.length == 0)
    { //Not Defined
      allVertices = null;
    }
    else
    { //Already Defined
      allVertices = tmp;
    }			
 
    /* Read immediate vertex coverage information */
    tmp = (int[]) in.readObject();
    if(tmp.length == 0)
    { //Not Defined
      immediateVertices = null;
    }
    else
    { //Already Defined
      immediateVertices = tmp;
    }			
  }//end: readObject(ObjectInputStream)

  /**
   * Used to convert the {@link CategoryIDNode} integer positions to their memory locations for use.
   * 
   * @param arr Array of {@link CategoryNodes} representing the category graph.
   */
//  public void convertEdgesAfterRead(CategoryIDNode[] arr)
//  {
//    if(parentsIndex != null)
//    {
//      parents = new CategoryIDNode[parentsIndex.length];
//      for(int i = 0; i < parents.length; i++)
//      {
//        parents[i] = arr[parentsIndex[i]];
//      }//end: for(i)
//    }
//    else
//    {
//      parents = null;
//    }
//
//    if(childrenIndex != null)
//    {
//      children = new CategoryIDNode[childrenIndex.length];
//      for(int i = 0; i < children.length; i++)
//      {
//        children[i] = arr[childrenIndex[i]];
//      }//end: for(i)
//    }
//    else
//    {
//      children = null;
//    }
//  }//end: convertEdgesAfterRead(CategoryNode[])
  
 /**
  * Comparator based on node IDs.
  */
  public int compareTo(Object cn)
  {
    return this.catID - ((CategoryIDNode)cn).catID;
  }//end: compareTo(Object)

 /**
  * Comparator based on node IDs.
  * 
  * @param o1 {@link CategoryIDNode}
  * @param o2 {@link CategoryIDNode}
  * @return comparison of the two objects based on node name.
  */
  public int compare(Object o1, Object o2)
  {
    return ((CategoryIDNode)o1).catID - ((CategoryIDNode)o2).catID;
  }//end: compare(Object, Object)
}
