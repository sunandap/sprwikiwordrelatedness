package edu.osu.slate.relatedness.swwr.data.category;

import it.unimi.dsi.fastutil.ints.*;
import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.swwr.data.category.CategoryGraph;

/**
 * Category graph nodes.
 * 
 * @author weale
 *
 */
public class CategoryIDNode implements Serializable, Comparable<Object> {

  /* Serialization variable */
  private static final long serialVersionUID = 1L;

  /* Page/Category Name */
  private int catID;

  /* CategoryNode array of positions of parents in acyclic graph */
  private CategoryIDNode[] parents;

  /* Integer array of positions of parents in acyclic graph */
  private int[] parentsIndex;

  /* CategoryNode array of positions of children in acyclic graph */
  private CategoryIDNode[] children;

  /* int array of positions of children in acyclic graph */
  private int[] childrenIndex;

  /* Coverage percentage of the category */
  private int coverage;

  /* */
  private int[] immediateLeaves;

  /* */
  private int[] allLeaves;

  /* Set of pages that fall under this category */
  private IntAVLTreeSet IMleaves;

  /* */
  private IntAVLTreeSet ALLleaves;

  private boolean IMfinalized;

  private boolean ALLfinalized;
	
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
    parents = null;
    children = null;
    IMleaves = null;
    ALLleaves = null;
    IMfinalized = false;
    ALLfinalized = false;
  }//end: CategoryNode(String)
	
 /**
  * Adds a parent to the current node.
  * <p>
  * Only adds the ID if the parent is not already listed.
  * 
  * @param parent {@link CategoryIDNode} of the parent.
  */
  public void addParent(CategoryIDNode parent)
  {
    if(parents == null)
    { //No existing parents
      parents = new CategoryIDNode[1];
      parents[0] = parent;
    }
    else if(Arrays.binarySearch(parents, parent) < 0)
    { // Parent not found in existing parents
      
      // Create new array, copy parent into new array
      CategoryIDNode[] tmp = new CategoryIDNode[parents.length+1];
      System.arraycopy(parents, 0, tmp, 0, parents.length);
      tmp[parents.length] = parent;
      parents = tmp;
      Arrays.sort(parents);
      tmp = null;
    }
  }//end: addParent(CategoryNode)
	
 /**
  * Adds a child ID to the current node.  Only adds the ID if the child is not already listed.
  * 
  * @param child {@link CategoryIDNode} of the child.
  */
  public void addChild(CategoryIDNode child)
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
    CategoryIDNode[] tmp = new CategoryIDNode[size];
    System.arraycopy(children, 0, tmp, 0, size-1);
    tmp[size-1] = child;
    children = tmp;
    Arrays.sort(children);
    tmp = null;
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
    CategoryIDNode[] tmp = new CategoryIDNode[children.length-1];
    int currIndex=0, newIndex=0;
    while(newIndex<tmp.length)
    {
      if(children[currIndex].catID != id)
      {
        tmp[newIndex] = children[currIndex];
        newIndex++;
      }
      currIndex++;
    }//end: while()
    children = tmp;
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
    CategoryIDNode[] tmp = new CategoryIDNode[parents.length-1];
    int currIndex=0, newIndex=0;
    while(newIndex<tmp.length)
    {
      if( parents[currIndex].catID  != id )
      {
        tmp[newIndex] = parents[currIndex];
        newIndex++;
      }
      currIndex++;
    }
    parents = tmp;
    Arrays.sort(parents);
  }//end: removeParent(String)
	
 /**
  * Gets the name of the node.
  * 
  * @return String containing the name of the node
  */
  public int getCategoryID()
  {
    return catID;
  }
	
 /**
  * Compares a given name to the name of this CategoryNode.
  * 
  * @param name Category name being searched for.
  * @return Boolean value of whether or not the searched name is the name of the node.
  */
  public boolean isCategoryID(int id)
  {
    return (catID == id);
  }
	
 /**
  * Adds the given leafID to the current list of category leaves.
  * 
  * @param leafNum ID of the leaf
  */
  public void addImmediateLeaf(int leafNum)
  {
    if(IMleaves == null)
    {
      IMleaves = new IntAVLTreeSet();
    }
		
    if(ALLleaves == null)
    {
      ALLleaves = new IntAVLTreeSet();
    }
		
    IMleaves.add(leafNum);
  }

 /**
  * Adds the given leafID to the current list of category leaves.
  * 
  * @param leafNum ID of the leaf
  */
  public void addAncestorLeaf(int leafNum)
  {
    if(ALLleaves == null)
    {
      ALLleaves = new IntAVLTreeSet();
    }
    ALLleaves.add(leafNum);
  }
	
 /**
  * Accessor for the list of category leaves.
  * 
  * @return {@link TreeSet} of integers.
  */
  public IntAVLTreeSet getAllLeavesAVL()
  {
    return ALLleaves;
  }

  public int[] getAllLeaves()
  {
    return allLeaves;
  }
	
  public int[] getImmediateLeaves()
  {
    return immediateLeaves;
  }
	
 /**
  * Given the inverse graph, count the number of edges that are in-bound to the leaves of the category.
  * 
  * @return count of the number of edges inbound to the leaves
  */
  public int getInboundLeafEdges(WikiInvGraph wg)
  {
    int count = 0;
    IntIterator it = ALLleaves.iterator();
    while(it != null && it.hasNext())
    {
      int[] arr = wg.getInboundLinks(it.nextInt());
      if(arr != null) {
        count += arr.length;
      }
    }
    return count;
  }
	
 /**
  * Sets the coverage based on the number of category leaves and the number of overall vertices.
  * 
  * @param den Number of vertices in the overall graph.
  */
  public void setVertexCoverage()
  {
    coverage = 0;
		
    //finalizeAllLeaves();
		
    if(allLeaves != null)
    {
      coverage = allLeaves.length;
    }
  }
	
 /**
  * Sets the coverage based on the number of edges into the node leaves and the number of overall edges.
  * 
  * @param den Number of edges in the overall graph.
  */
  public void setEdgeCoverage(WikiInvGraph wg)
  {
    coverage = 0;
		
    //finalizeAllLeaves();
		
    if(allLeaves != null)
    {
      coverage = getInboundLeafEdges(wg);
    }
  }
	
 public void finalizeImmediateLeaves()
 {
   if(IMleaves != null)
   {
     immediateLeaves = IMleaves.toIntArray();
     Arrays.sort(immediateLeaves);
			
     if(ALLleaves != null)
       ALLleaves.addAll(IMleaves);
     else
       ALLleaves = IMleaves;
			
     IMleaves = null;
   }
   
   IMfinalized = true;
 }//end: finalizeImmediateLeaves()

 public void finalizeAllLeaves()
 {  
   if(ALLleaves != null)
   {
     allLeaves = ALLleaves.toIntArray();
			
     int[] tmp = immediateLeaves;
     if(tmp == null)
     {
       tmp = new int[0]; 
     }
			
     int[] newLeaves = Arrays.copyOf(allLeaves, allLeaves.length + tmp.length);
     for(int i=0; i<tmp.length; i++) {
       newLeaves[allLeaves.length+i] = tmp[i];
     }
     Arrays.sort(newLeaves);
     allLeaves = newLeaves;
     ALLleaves = null;
   }
   ALLfinalized = true;
 }

	
 /**
  * 
  * @return
  */
 public int getCoverage()
 {
   return coverage;
 }

 /**
  * Determines if the given node is a leaf of the category.
  * 
  * @return boolean value based on if the leaf is found
  */
 public boolean isImmediateLeaf(int node)
 {
   if(IMfinalized)
   {
     if(immediateLeaves != null)
       return (Arrays.binarySearch(immediateLeaves, node) >= 0);
     else
       return false;
   }
   else
   {
     return IMleaves.contains(node);
   }
 }//end: isImmediateLeaf
	
 /**
  * Determines if the given node is a leaf of the category.
  * 
  * @return boolean value based on if the leaf is found
  */
 public boolean isLeaf(int node)
 {
   if(ALLfinalized)
   {
     if(allLeaves != null)
       return (Arrays.binarySearch(allLeaves, node) >= 0);
     else
       return false;
   }
   else
   {
     return ALLleaves.contains(node);
   }
 }//end: isLeaf(int)
	
 /**
  * Accessor for the list of children
  * 
  * @return {@link CategoryIDNode} array
  */
 public CategoryIDNode[] getChildren()
 {
   return children;
 }

 /**
  * Accessor for the list of parents
  * 
  * @return {@link CategoryIDNode} array
  */
 public CategoryIDNode[] getParents()
 {
   return parents;
 }

 /**
  * Prints the contents of the node.
  */
 public void print() {
   //Print name of the node
   System.out.print(catID);

   //Print parents of the node
   System.out.print("\tParents:");
   if(parents != null) {
     for(int j=0; j<parents.length; j++) {
       System.out.print(" "+ parents[j].getCategoryID());
     }
   } else {
     System.out.print("-");
   }

   //Print children of the node
   System.out.print("\tChildren:");
   if(children != null) {
     for(int j=0; j<children.length; j++) {
       System.out.print(" "+ children[j].getCategoryID());
     }
   } else {
     System.out.print("-");
   }

   //Print category coverage
   System.out.println("\t" + coverage);
 }

 /**
  * Used to convert the {@link CategoryIDNode} links to their integer positions for writing.
  * 
  * @param arr Array of {@link CategoryNodes} representing the category graph.
  */
 public void convertEdgesBeforeWrite(CategoryIDNode[] arr) {
   if(parents != null) {
     parentsIndex = new int[parents.length];
     for(int i=0; i<parents.length; i++) {
       parentsIndex[i] = Arrays.binarySearch(arr, parents[i]);
     }
   } else {
     parentsIndex = null;
   }

   if(children != null) {
     childrenIndex = new int[children.length];
     for(int i=0; i<children.length; i++) {
       childrenIndex[i] = Arrays.binarySearch(arr, children[i]);
     }
   } else {
     childrenIndex = null;
   }
 }

 /**
  * Used to convert the {@link CategoryIDNode} integer positions to their memory locations for use.
  * 
  * @param arr Array of {@link CategoryNodes} representing the category graph.
  */
 public void convertEdgesAfterRead(CategoryIDNode[] arr) {
   if(parentsIndex != null) {
     parents = new CategoryIDNode[parentsIndex.length];
     for(int i=0; i<parents.length; i++) {
       parents[i] = arr[parentsIndex[i]];
     }
   } else {
     parents = null;
   }

   if(childrenIndex != null) {
     children = new CategoryIDNode[childrenIndex.length];
     for(int i=0; i<children.length; i++) {
       children[i] = arr[childrenIndex[i]];
     }
   } else {
     children = null;
   }
 }

 public boolean isDescendant(CategoryIDNode cn) 
 {
   return checkIfDescendant(this, cn);
 }

 public boolean checkIfDescendant(CategoryIDNode parent, CategoryIDNode findMe)
 {
   if(parent.equals(findMe))
   {
     return true;
   }
   else if(parent.children == null)
   {
     return false;
   }

   boolean found = false;
   for(int i = 0; !found && i < parent.children.length; i++)
   {
     found = checkIfDescendant(parent.children[i], findMe);
   }//end: for(i)
   return found;
 }

 /**
  * Writes a {@link CategoryIDNode} to the file.
  * 
  * @param out Output file stream
  * @throws IOException
  */
 private void writeObject(ObjectOutputStream out) throws IOException {
   //System.out.println(nodeName);		
   out.writeInt(catID);
   out.writeObject(parentsIndex);
   out.writeObject(childrenIndex);
   out.writeInt(coverage);

   /* Write the list of all leaves as an         *
    * array or an IntAVLTreeSet                  */
   out.writeBoolean(ALLfinalized);
   if(ALLfinalized && allLeaves != null)
     out.writeObject(allLeaves);
   else if( ALLleaves != null)
     out.writeObject(ALLleaves.toIntArray());
   else
     out.writeObject(new int[0]);

   /* Write the list of immediate leaves as an   *
    * array or an IntAVLTreeSet                  */
   out.writeBoolean(IMfinalized);
   if(IMfinalized && immediateLeaves != null) {
     out.writeObject(immediateLeaves);
   }
   if(IMleaves != null)
     out.writeObject(IMleaves.toIntArray());
   else
     out.writeObject(new int[0]);
 }

 /**
  * Reads a {@link CategoryIDNode} from a file.
  * 
  * @param in Input file stream
  * @throws IOException
  * @throws ClassNotFoundException
  */
 private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
   catID         = in.readInt();
   parentsIndex  = (int[]) in.readObject();
   childrenIndex = (int[]) in.readObject();
   coverage      = in.readInt();

   ALLfinalized  = in.readBoolean();
   int[] tmp = (int[]) in.readObject();
   if(ALLfinalized) {
     if(tmp.length == 0) {
       allLeaves = null;
     } else {
       allLeaves = tmp;
     }			
   } else {
     if(tmp.length == 0) {
       ALLleaves = null;
     } else {
       ALLleaves = new IntAVLTreeSet(tmp);
     }
   }

   IMfinalized  = in.readBoolean();
   tmp = (int[]) in.readObject();
   if(IMfinalized) {
     if(tmp.length == 0) {
       immediateLeaves = null;
     } else {
       immediateLeaves = tmp;
     }			
   } else {
     if(tmp.length == 0) {
       IMleaves = null;
     } else {
       IMleaves = new IntAVLTreeSet(tmp);
     }
   }
 }

 /**
  * Comparator based on node name.
  */
 public int compareTo(Object cn) {
   return this.catID - ((CategoryIDNode)cn).catID;
 }

 /**
  * Comparator based on node name.
  * 
  * @param o1 {@link CategoryIDNode}
  * @param o2 {@link CategoryIDNode}
  * @return comparison of the two objects based on node name.
  */
 public int compare(Object o1, Object o2) {
   return ((CategoryIDNode)o1).catID - ((CategoryIDNode)o2).catID;
 }
}
