package edu.osu.slate.relatedness.swwr.data.category;

import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.swwr.data.category.CategoryGraph;
import edu.osu.slate.relatedness.swwr.data.graph.WikiInvGraph;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Category graph nodes.
 * 
 * @author weale
 *
 */
public class CategoryNode implements Serializable, Comparable<Object> {

  /* Serialization variable */
  private static final long serialVersionUID = 1L;

  /* Category ID */
  private int catID;

  /* CategoryNode array of category parents in acyclic graph */
  private CategoryNode[] parents;

  /* CategoryNode array of category children in acyclic graph */
  private CategoryNode[] children;
  
  /* Integer array of positions of parents in acyclic graph.
   * 
   * Used in reading/writing the object.
   */
  private int[] parentsIndex;

  /* int array of positions of children in acyclic graph.
   * 
   * Used in reading/writing the object.
   */
  private int[] childrenIndex;

  /* Coverage count of the leaves of the category */
  private int vertexCoverage;

  private int inboundEdgeCoverage;
  
  private int outboundEdgeCoverage;
  
  /* Array of all immediate category leaves 
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
  public CategoryNode(int id)
  {
    catID = id;
    
    parents = null;
    children = null;
    
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
   * @param parent {@link CategoryNode} of the parent.
   */
  public void addParent(CategoryNode parent)
  {
    if(parents == null)
    { //No existing parents
      parents = new CategoryNode[1];
      parents[0] = parent;
    }
    else if(Arrays.binarySearch(parents, parent) < 0)
    { // Parent not found in existing parents

      // Create new array, copy parent into new array
      CategoryNode[] tmpParents = new CategoryNode[parents.length+1];
      System.arraycopy(parents, 0, tmpParents, 0, parents.length);
      tmpParents[parents.length] = parent;
      parents = tmpParents;
      
      Arrays.sort(parents);
      tmpParents = null;
    }
  }//end: addParent(CategoryNode)

  /**
   * Adds a child ID to the current node.  Only adds the ID if the child is not already listed.
   * 
   * @param child {@link CategoryNode} of the child.
   */
  public void addChild(CategoryNode child)
  {
    int size = 0;
    if(children != null)
    {		
      //Check for existing children
      if(Arrays.binarySearch(children, child) > 0)
      {
        return;
      }

      //Set new size
      size = children.length;
    }
    size++;

    //Create Child
    CategoryNode[] tmpChildren = new CategoryNode[size];
    System.arraycopy(children, 0, tmpChildren, 0, size-1);
    tmpChildren[size-1] = child;
    children = tmpChildren;
    
    Arrays.sort(children);
    tmpChildren = null;
  }//end: addChild(CategoryNode)

  /**
   * Removes the named child from the list of children.
   * <p>
   * Used by {@link CategoryGraph} and {@link VisitNode} to break cycles in the graph.
   * 
   * @param name Name of the child to remove.
   */
  public void removeChild(int id)
  {
    CategoryNode[] tmpChildren = new CategoryNode[children.length-1];
    int currIndex=0, newIndex=0;
    while(newIndex < tmpChildren.length)
    {
      if(children[currIndex].catID != id)
      {
        tmpChildren[newIndex] = children[currIndex];
        newIndex++;
      }
      currIndex++;
    }//end: while(newIndex)
    
    children = tmpChildren;
    Arrays.sort(children);
  }//end: removeChild(String)

  /**
   * Removes the named parent from the list of parents.
   * <p>
   * Used by {@link CategoryGraph} to remove non-root reachable nodes in the graph.
   * 
   * @param name Name of the parent to remove.
   */
  public void removeParent(int id)
  {
    CategoryNode[] tmpParents = new CategoryNode[parents.length-1];
    int currIndex=0, newIndex=0;
    while(newIndex < tmpParents.length)
    {
      if( parents[currIndex].catID  != id )
      {
        tmpParents[newIndex] = parents[currIndex];
        newIndex++;
      }
      currIndex++;
    }//end: while(newIndex)
    
    parents = tmpParents;
    Arrays.sort(parents);
  }//end: removeParent(String)

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
   * @param name Category ID being searched for.
   * @return Boolean value of whether or not the searched name is the ID of the node.
   */
  public boolean isCategoryID(int id)
  {
    return (catID == id);
  }

  /**
   * Adds the given leafID to the current list of category leaves.
   * 
   * @param vertexNum Graph vertex number.
   */
  public void addImmediateLeaf(int vertexNum)
  {
    if(immediateVertices == null)
    {
      immediateVertices = new int[1];
      immediateVertices[0] = vertexNum;
    }
    else if(Arrays.binarySearch(immediateVertices, vertexNum) < 0)
    {// Leaf not already in category
      int[] tmpLeaves = new int[immediateVertices.length+1];
      System.arraycopy(immediateVertices, 0, tmpLeaves, 0, immediateVertices.length);
      tmpLeaves[immediateVertices.length] = vertexNum;
      immediateVertices = tmpLeaves;
      
      Arrays.sort(immediateVertices);
      tmpLeaves = null;
    }
  }//end: addImmediateLeaf(int)

 /**
  * Adds the given vertex number to the current list of category vertices.
  * 
  * @param vertexNum Graph vertex number.
  */
  private void addAncestorVertex(int vertexNum)
  {
    if(allVertices == null)
    {
      allVertices = new int[1];
      allVertices[0] = vertexNum;
    }
    else if(Arrays.binarySearch(allVertices, vertexNum) < 0)
    {//Leaf not already an ancestor
      int[] tmpLeaves = new int[allVertices.length+1];
      System.arraycopy(allVertices, 0, tmpLeaves, 0, allVertices.length);
      tmpLeaves[allVertices.length] = vertexNum;
      allVertices = tmpLeaves;
      
      Arrays.sort(allVertices);
      tmpLeaves = null;
    }
  }//end: addAncestorLeaf(int)

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
  public void setOutboundVertexEdgeCount(WikiGraph wg)
  {
    int count = 0;
    
    if(children != null)
    {
      // Visit all children first
      for(int i = 0; i < children.length; i++)
      {
        
        if(!children[i].outboundEdgeCountsFinalized)
        {
          children[i].setOutboundVertexEdgeCount(wg);
        }
        
        count += children[i].inboundEdgeCoverage;
      }//end: for(i)
    }//end: children

    // Add children vertex counts
    for(int i = 0; immediateVertices != null &&
                   i < immediateVertices.length; i++)
    {
      // For each graph vertex, get the in-bound link array
      int[] arr = wg.getOutboundLinks(immediateVertices[i]);

      if(arr != null)
      { 
        //Valid inbound link set
        count += arr.length;
      }
    }//end: for(i)
    
    outboundEdgeCountsFinalized = true;
    outboundEdgeCoverage = count;
  }//end: setInboundLeaveEdges(WikiInvGraph)
  
  /**
   * Finds the in-bound edge count for a the category.
   * <p>
   * Given the inverse graph, count the number of edges that are in-bound to the leaves of the category.
   * 
   * @param wg {@link WikiInvGraph}
   * @return Count of the number of edges in-bound to the leaves.
   */
  public void setInboundVertexEdgeCount(WikiInvGraph wg)
  {
    int count = 0;
    
    if(children != null)
    {
      // Visit all children first
      for(int i = 0; i < children.length; i++)
      {
        
        if(!children[i].inboundEdgeCountsFinalized)
        {
          children[i].setInboundVertexEdgeCount(wg);
        }
        
        count += children[i].inboundEdgeCoverage;
      }//end: for(i)
    }//end: children

    // Add children vertex counts
    for(int i = 0; immediateVertices != null &&
                   i < immediateVertices.length; i++)
    {
      // For each graph vertex, get the in-bound link array
      int[] arr = wg.getInboundLinks(immediateVertices[i]);

      if(arr != null)
      { 
        //Valid inbound link set
        count += arr.length;
      }
    }//end: for(i)
    
    inboundEdgeCountsFinalized = true;
    inboundEdgeCoverage = count;
  }//end: setInboundLeaveEdges(WikiInvGraph)

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

 /**
  * Finalize all the category vertices.
  */
  public void finalizeAllVertices()
  {
    if(this.children != null)
    { 
      // Finalize all children
      for(int i = 0; i < children.length; i++)
      {
        // Check to see if child is finalized
        if(!children[i].AllChildrenVerticesFinalized)
        {
          children[i].finalizeAllVertices();
        }
        this.addAncestorVertices(children[i].allVertices);
      }//end: for(i)
    }//end: if(this.children)
    
    // Set Leaves Finalized Flag
    this.finalizeImmediateVertices();
    this.addAncestorVertices(this.immediateVertices);
    AllChildrenVerticesFinalized = true;
  }//end: finalizeAllLeaves()

 /**
  * Gets the vertex coverage amount of the {@link CategoryNode}.
  * 
  * @return Vertex coverage amount of the {@link CategoryNode}.
  */
  public int getVertexCoverage()
  {
    return vertexCoverage;
  }// getCoverage()

  /**
   * Gets the in-bound edge coverage amount of the {@link CategoryNode}.
   * 
   * @return In-bound edge coverage amount of the {@link CategoryNode}.
   */
   public int getInboundEdgeCoverage()
   {
     return inboundEdgeCoverage;
   }// getCoverage()
   
   /**
    * Gets the out-bound edge coverage amount of the {@link CategoryNode}.
    * 
    * @return Out-bound edge coverage amount of the {@link CategoryNode}.
    */
    public int getOutboundEdgeCoverage()
    {
      return outboundEdgeCoverage;
    }// getCoverage()
    
 /**
  * Determines if the given vertex number is a leaf vertex of the category.
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
   * Determines if the given vertex number is a leaf vertex of the category.
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
   * Accessor for the list of children
   * 
   * @return {@link CategoryNode} array
   */
  public CategoryNode[] getChildrenCategories()
  {
    return children;
  }//end: getChildren()

  /**
   * Accessor for the list of parents
   * 
   * @return {@link CategoryNode} array
   */
  public CategoryNode[] getParentCategories()
  {
    return parents;
  }//end: getParents()

  /**
   * Prints the contents of the node.
   */
  public void print()
  {
    //Print name of the node
    System.out.print(catID);

    //Print parents of the node
    System.out.print("\tParents:");
    if(parents != null)
    {
      for(int j = 0; j < parents.length; j++)
      {
        System.out.print(" "+ parents[j].getCategoryID());
      }
    }
    else
    {
      System.out.print("-");
    }

    //Print children of the node
    System.out.print("\tChildren:");
    if(children != null)
    {
      for(int j = 0; j < children.length; j++)
      {
        System.out.print(" "+ children[j].getCategoryID());
      }
    }
    else
    {
      System.out.print("-");
    }

    //Print category coverage
    System.out.println("\t" + vertexCoverage);
  }//end: print()

 /**
  * Determines if the given {@link CategoryNode} is a descendant of the current node.
  * 
  * @param cn {@link CategoryNode} to find.
  * @return Whether the given category is a descendant.
  */
  public boolean isDescendant(CategoryNode cn) 
  {
    return checkIfDescendant(this, cn);
  }

 /**
  * Determines if a given {@link CategoryNode} is a descendant of another category.
  * 
  * @param category Current {@link CategoryNode} to search.
  * @param findMe {@link CategoryNode} to find.
  * @return Boolean as the the decendant nature of the given {@link CategoryNode}.
  */
  public boolean checkIfDescendant(CategoryNode category, CategoryNode findMe)
  {
    /* BASE CASES */
    if(category.equals(findMe))
    {//Found!
      return true;
    }
    else if(category.children == null)
    {//No more children
      return false;
    }

    // Recursively search descendants
    boolean found = false;
    for(int i = 0; !found && i < category.children.length; i++)
    {
      found = checkIfDescendant(category.children[i], findMe);
    }//end: for(i)
    
    return found;
  }//end: checkIfDescendant(CategoryNode, CategoryNode)

 /**
  * Writes a {@link CategoryNode} to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    /* Write node information */
    out.writeInt(catID);
    out.writeObject(parentsIndex);
    out.writeObject(childrenIndex);
    
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
   * Used to convert the {@link CategoryNode} links to their integer positions for writing.
   * 
   * @param arr Array of {@link CategoryNodes} representing the category graph.
   */
  public void convertEdgesBeforeWrite(CategoryNode[] arr)
  {
    if(parents != null)
    {
      parentsIndex = new int[parents.length];
      for(int i=0; i<parents.length; i++)
      {
        parentsIndex[i] = Arrays.binarySearch(arr, parents[i]);
      }
    }
    else
    {
      parentsIndex = null;
    }

    if(children != null)
    {
      childrenIndex = new int[children.length];
      for(int i=0; i<children.length; i++)
      {
        childrenIndex[i] = Arrays.binarySearch(arr, children[i]);
      }
    }
    else
    {
      childrenIndex = null;
    }
  } 
  
  /**
   * Reads a {@link CategoryNode} from a file.
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    /* Read node information */
    catID                = in.readInt();
    parentsIndex         = (int[]) in.readObject();
    childrenIndex        = (int[]) in.readObject();
    
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
   * Used to convert the {@link CategoryNode} integer positions to their memory locations for use.
   * 
   * @param arr Array of {@link CategoryNodes} representing the category graph.
   */
  public void convertEdgesAfterRead(CategoryNode[] arr)
  {
    if(parentsIndex != null)
    {
      parents = new CategoryNode[parentsIndex.length];
      for(int i = 0; i < parents.length; i++)
      {
        parents[i] = arr[parentsIndex[i]];
      }//end: for(i)
    }
    else
    {
      parents = null;
    }

    if(childrenIndex != null)
    {
      children = new CategoryNode[childrenIndex.length];
      for(int i = 0; i < children.length; i++)
      {
        children[i] = arr[childrenIndex[i]];
      }//end: for(i)
    }
    else
    {
      children = null;
    }
  }//end: convertEdgesAfterRead(CategoryNode[])
  
  /**
   * Comparator based on node IDs.
   */
  public int compareTo(Object cn)
  {
    return this.catID - ((CategoryNode)cn).catID;
  }//end: compareTo(Object)

  /**
   * Comparator based on node IDs.
   * 
   * @param o1 {@link CategoryNode}
   * @param o2 {@link CategoryNode}
   * @return comparison of the two objects based on node name.
   */
  public int compare(Object o1, Object o2)
  {
    return ((CategoryNode)o1).catID - ((CategoryNode)o2).catID;
  }//end: compare(Object, Object)
}
