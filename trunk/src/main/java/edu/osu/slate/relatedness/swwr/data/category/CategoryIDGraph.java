/* Copyright 2010 Speech and Language Technologies Lab, The Ohio State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.osu.slate.relatedness.swwr.data.category;

import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.Configuration;

/**
 * Holds the category graph for our Wiki data set.
 * <p>
 * Graphs are 'rooted' at an individual node.
 * 
 * @author weale
 *
 */
public class CategoryIDGraph implements Serializable
{
  /* Serialization ID */
  private static final long serialVersionUID = 1L;

  /* Array of CategoryIDNodes */
  private CategoryIDNode[] graph;
	
  /* Name of the root CategoryNode */
  private int rootID;
	
 /**
  * Constructor.
  * <p>
  * Takes the name of the root category as input and 
  * creates a temporary graph structure to build upon.
  * 
  * @param root Name of the root category
  */
  public CategoryIDGraph(int rootID)
  {
    this.rootID = rootID;
		
    CategoryIDNode cn = new CategoryIDNode(rootID);
    graph = new CategoryIDNode[1];
    graph[0] = cn;	
  }//end: CategoryGraph(int)

 /**
  * Gets the root node.
  * 
  * @return {@link CategoryIDNode} of the root.
  */
  public CategoryIDNode getRoot()
  {
    CategoryIDNode tmp = new CategoryIDNode(rootID);
    int index = Arrays.binarySearch(graph, tmp);

    return graph[index];
  }//end: getRoot()
  
 /**
  * 
  * @return
  */
  public int getTotalNumVertices()
  {
    CategoryNode cn = new CategoryNode(rootID);
    int pos = Arrays.binarySearch(graph, cn);
    return graph[pos].getAllVertices().length;
  }
	
 /**
  * Adds a node to the graph with the given category ID.
  * <p>
  * Only adds a {@link CategoryNode} if the node is not already present.
  * 
  * @param page Name of the page to add.
  */
  public void addNode(int catID)
  {
    CategoryIDNode cn = new CategoryIDNode(catID);

    if(Arrays.binarySearch(graph, cn) < 0)
    {
      CategoryIDNode[] tempGraph = new CategoryIDNode[graph.length+1];
      System.arraycopy(graph, 0, tempGraph, 0, graph.length);
      tempGraph[graph.length] = cn;
      graph = tempGraph;
      
      Arrays.sort(graph);
      tempGraph = null;
    }//end: if()
  }//end: addNode(String)

  /**
   * Adds a set of nodes to the graph with the given page IDs.
   * <p>
   * Only adds a {@link CategoryNode} if the node is not already present.
   * 
   * @param catIDs Sorted array of Category IDs.
   */
   public void addNodes(int[] catIDs)
   {
     Arrays.sort(catIDs);
     int prevID = -1;
     
     for(int i = 0; i < catIDs.length; i++)
     {
       int currentID = catIDs[i];
       
       if(prevID != currentID)
       {
         CategoryIDNode cn = new CategoryIDNode(catIDs[i]);
         CategoryIDNode[] tempGraph = new CategoryIDNode[graph.length+1];
         System.arraycopy(graph, 0, tempGraph, 0, graph.length);
         tempGraph[graph.length] = cn;
         graph = tempGraph;
       }
       prevID = currentID;
     }//end: for(i)
     
     Arrays.sort(graph);
   }//end: addNode(String)
   
 /**
  * Add a link from the parent to child in the graph.
  * <p>
  * Graph nodes <b>must</b> be finalized before starting to add edges.
  * 
  * @param parent Category ID of the parent.
  * @param child Category ID of the child.
  */
  public void addEdge(int parent, int child, IDToCategoryTitleTranslation ID2Cat)
  {
    CategoryIDNode p = new CategoryIDNode(parent);
    CategoryIDNode c = new CategoryIDNode(child);
        
    int parentIndex = Arrays.binarySearch(graph, p);
    int childIndex = Arrays.binarySearch(graph, c);
    
    if(parentIndex > -1 && childIndex > -1)
    {
      graph[parentIndex].addChild(child);
      graph[childIndex].addParent(parent);
    }
    else
    {
      if(parentIndex < 0)
      {
        System.err.println("Problem with (" + parent + ")");
      }
      if(childIndex < 0)
      {
        System.err.println("Problem with (" + child + ")");
      }
    }
  }//end: addEdge(int, int)
	
 /**
  * Adds graph vertex numbers to the category graph.
  * 
  * @param parentID Category ID.
  * @param vertexNum Graph vertex number.
  */
  public void addVertex(int parentID, int vertexNum)
  {
    int parentPos = Arrays.binarySearch(graph, new CategoryNode(parentID));
    if(parentPos >= 0)
    {
      graph[parentPos].addImmediateVertex(vertexNum);
    }
  }//end: addLeaf(int, int)
	
 /**
  * Creates coverage based on the in-bound edge count.
  * 
  * @param wg {@link WikiInvGraph} containing the vertex graph edges.
  */
//  public void setInboundEdgeCounts(WikiInvGraph wg)
//  {
//    CategoryNode tmp = new CategoryNode(rootID);
//    int index = Arrays.binarySearch(graph, tmp);
//    
//    graph[index].finalizeAllVertices();
//    graph[index].setInboundVertexEdgeCount(wg);
//  }//end: useInboundEdgeCounts(WikiInvGraph)
	
 /**
  * Creates coverage based on the out-bound edge count.
  * 
  * @param wg {@link WikiGraph} containing the vertex graph edges.
  */
//  public void setOutboundEdgeCounts(WikiGraph wg)
//  {
//    CategoryNode tmp = new CategoryNode(rootID);
//    int index = Arrays.binarySearch(graph, tmp);
//     
//    graph[index].finalizeAllVertices();
//    graph[index].setOutboundVertexEdgeCount(wg);
//  }//end: setOutboundEdgeCounts(WikiInvGraph)
  
 /**
  * Propagates leaf counts around the tree.
  */
  public void setVertexCounts()
  {
    CategoryNode tmp = new CategoryNode(rootID);
    int index = Arrays.binarySearch(graph, tmp);
	
    //graph[index].finalizeAllVertices();
  }//end: propogateVertexCounts()
	
 /**
  * Removes any cycles to create an acyclic category tree.
  * <p>
  * Traversal is done in a breadth-first manner.
  * Therefore, the removed cycles are movements upwards in the graph tree.
  */
//  public void DFSremoveCycles()
//  {
//    //Find the root in the tree
//    CategoryNode tmp = new CategoryNode(rootID);
//    int index = Arrays.binarySearch(graph, tmp);
//    
//    // Add the root to the VisitNode list
//    DFSremoveCycles(new DFSVisitNode(index, new int[0]), 0);    
//  }//end: removeCycles()
//
//  public void DFSremoveCycles(DFSVisitNode vn, int depth)
//  {
//    System.out.println(depth);
//    DFSVisitNode[] arr = vn.makeChildrenVisitNodes(graph);
//    // Add visit nodes to the list
//    for(int i = 0; arr != null && i < arr.length; i++)
//    {
//      if(arr[i].isDuplicateEdge()) {
//        System.out.println(vn.getCurrentCategoryID() + "->" + arr[i].getCurrentCategoryID());
//      }
//      DFSremoveCycles(arr[i], depth+1);
//    }//end: for(i)
//  }
  
  public BFSNode[] setBFSNodes()
  {
    
    BFSNode[] bfs = new BFSNode[graph.length];
    
    for(int i=0; i<bfs.length; i++)
    {
      bfs[i] = new BFSNode();
    }
    int depth = 0;
    int setCount = 1;
    TreeSet<Integer> ts = new TreeSet<Integer>();
    ts.add(rootID);
    while(setCount != 0)
    {
      setCount = 0;
      TreeSet<Integer> newTS = new TreeSet<Integer>();
      Iterator<Integer> it = ts.iterator();
      while(it.hasNext())
      {
        // Get the category ID
        int cat = it.next();
        
        // Find the index of the CategoryIDNode
        CategoryIDNode tmp = new CategoryIDNode(cat);
        int index = Arrays.binarySearch(graph, tmp);
        
        // Check to see if we've visited this yet
        if(bfs[index].depth == -1)
        {
          // Set depth, ID
          bfs[index].catID = cat;
          bfs[index].depth = depth;
          
          // Check the children of the ID
          int[] children = graph[index].getChildrenIDs();
          for(int i = 0; children != null && i < children.length; i++)
          {
            tmp = new CategoryIDNode(children[i]);
            index = Arrays.binarySearch(graph, tmp);
            if(bfs[index].depth == -1)
            {
              newTS.add(children[i]);
            }
          }
        }//end: if(bfs[index])
      }//end: while(it)
      
      setCount = newTS.size();
      ts = newTS;
      newTS = null;
    }//end: while(setCount)
    return bfs;
  }
  
  /**
   *  
   */
   public void removeCycles()
   {
     int depth = 0;
     int prevCount = -1;
     int currCount = 0;
     
     // Get the root node
     CategoryIDNode tmp = new CategoryIDNode(rootID);
     int index = Arrays.binarySearch(graph, tmp);

     while(currCount != prevCount)
     {
       prevCount = currCount;
       
       // Update max depth
       depth++;
       System.out.println("Depth: " + depth);
       
       // Run Iterative Deepening Algorithm
       currCount = checkDFS(graph[index], new int[0], 1, depth);
       
       System.out.println("Count: " + currCount);
     }//end: while(currCount)
   }//end: removeCycles()
  
   private int checkDFS(CategoryIDNode cin, int[] parents, int currDepth, int maxDepth)
   {
     // Cut off at the bottom of the search
     if(currDepth > maxDepth)
     {
       return 0;
     }
     
     // Get children of current node
     int[] children = cin.getChildrenIDs();
     
     // No Children
     if(children == null)
     { 
       return 1;
     }
     // Set up new parent array
     int[] newParents = new int[parents.length+1];
     System.arraycopy(parents, 0, newParents, 0, parents.length);
     newParents[parents.length] = cin.getCategoryID();
     Arrays.sort(newParents);
     
     // For each child
     int numAncestors = 0;
     for(int i = 0; i < children.length; i++)
     {
       
       if(Arrays.binarySearch(newParents, children[i]) >= 0)
       {
         // Child is in the list of parents.
         // Remove the child to eliminate cycles
         cin.removeChild(children[i]);
       }
       else
       {
         // Find the index of the child
         CategoryIDNode cin2 = new CategoryIDNode(children[i]);
         int index = Arrays.binarySearch(graph, cin2);
         
         // Run DFS at the new depth for the child
         numAncestors += checkDFS(graph[index], newParents, currDepth+1, maxDepth);
       }
       
     }//end: for(i)
     return numAncestors + 1;
   }//end: checkDFS(CategoryIDNode, int[], int, int)
   
 /**
  *  
  * @param bfs Array of {@link BFSNode} objects.
  */
  public void removeCycles(BFSNode[] bfs)
  {
    // Get the root node
    CategoryIDNode tmp = new CategoryIDNode(rootID);
    int index = Arrays.binarySearch(graph, tmp);
    
    // Check starting at the root node
    checkDFS(new int[0], graph[index], bfs, 0);
  }
  
  public void checkDFS(int[] parents, CategoryIDNode cin, BFSNode[] bfs, int depth)
  {
    // Get the children of the node
    int[] children = cin.getChildrenIDs();
    int currID = cin.getCategoryID();
    
    // Find the child in the graph array
    CategoryIDNode tmp = new CategoryIDNode(currID);
    int index = Arrays.binarySearch(graph, tmp);
    
    // Check for children
    if(children == null)
    {
      return;
    }
    else if(Arrays.binarySearch(parents, currID) >= 0)
    { // Loop Found
      System.out.println("Cycle Found: " + cin.getCategoryID());
      
      // If the child 
      //if(bfs[index].depth <= depth)
      //{
        cin.removeChild(currID);
      //}
      return;
    }
    
    int[] newParents = new int[parents.length+1];
    System.arraycopy(parents, 0, newParents, 0, parents.length);
    newParents[parents.length] = cin.getCategoryID();
    Arrays.sort(newParents);
    // Check all children
    for(int i = 0; i < children.length; i++)
    {
      checkDFS(newParents, graph[index], bfs, depth+1);
    }//end: for(i)
  }
  
 /**
  * Removed cycles from the category tree. 
  */
  public void removeCycles2()
  {
    //Find the root in the tree
    CategoryIDNode tmp = new CategoryIDNode(rootID);
    int index = Arrays.binarySearch(graph, tmp);
    String tempFile = Configuration.baseDir + Configuration.tempDir;
    try
    {
      ObjectInputStream in;
      
      int count = 1;
      boolean readOut1 = true;

      ObjectOutputStream out = new ObjectOutputStream(
                               new FileOutputStream(tempFile + "1"));
      
      // Add the root to the VisitNode File
      out.writeObject(new VisitIDNode(index, new int[0]));
      out.close();
  
      // While there are nodes to visit
      while(count > 0)
      {
        count = 0;
        if(readOut1)
        {
          out = new ObjectOutputStream(new FileOutputStream(tempFile + "2"));
          in = new ObjectInputStream(new FileInputStream(tempFile + "1"));
          readOut1 = false;
        }
        else
        {
          out = new ObjectOutputStream(new FileOutputStream(tempFile + "1"));
          in = new ObjectInputStream(new FileInputStream(tempFile + "2"));
          readOut1 = true;
        }
        
        try
        {
          while(true)
          {
            // Remove the first node from the list
            VisitIDNode vn = (VisitIDNode) in.readObject();
            
            // Make visit nodes for all the children
            VisitIDNode[] arr = vn.makeChildrenVisitNodes(graph);
            
            // Add visit nodes to the list
            for(int i = 0; arr != null && i < arr.length; i++)
            {
              out.writeObject(arr[i]);
              count++;
            }//end: for(i)
          }
        }
        catch (IOException e) { }
        catch (ClassNotFoundException e) { }
        
        in.close();
        out.close();
      }//end: while(!ts)  
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }//end: removeCycles()

 /*
  * Removes excess categories.
  * <p>
  * Excess categories are those that do not connect to the graph root.
  */
//  public void trimGraphToRoot()
//  {
//    TreeSet<Integer> ts = new TreeSet<Integer>();
//		
//    CategoryNode tmp = new CategoryNode(root);
//    LinkedList<VisitNode> ll = new LinkedList<VisitNode>();
//    int index = Arrays.binarySearch(graph, tmp);
//    ll.add(new VisitNode(graph[index].getCategoryID(), new int[0]));
//		
//    while(!ll.isEmpty())
//    {
//      VisitNode vn = ll.removeFirst();
//			
//      ts.add(vn.getCurrentCategoryID());
//      VisitNode[] arr = vn.makeChildrenVisitNodes(arr);
//      for(int i = 0; arr != null && i < arr.length; i++)
//      {
//        ll.addFirst(arr[i]);
//      }//end: for(i)
//    }//end: while(!ll.isEmpty)
//		
//    for(int i=0; i<graph.length; i++)
//    {
//      CategoryTitleNode[] cn2arr = graph[i].getParents();
//      for(int j = 0; cn2arr != null && j < cn2arr.length; j++)
//      {
//        if(Arrays.binarySearch(graph, cn2arr[j]) < 0) {
//          graph[i].removeParent(cn2arr[j].getName());
//        }
//      }//end: for(j)
//    }//end: for(i)
//  }//end: trimGraphToRoot()
	
 /**
  * Prints the graph in ascending order by category name.
  */
//  public void print()
//  {
//    for(int i=0; i<graph.length; i++)
//    {
//      System.out.print(i + "\t");
//      graph[i].print();
//    }
//  }//end: print()
	
 /**
  * Checks to see if a given category name is a member of the graph.
  * 
  * @param name Name of the category
  * @return true/false based on if the category name is found in the graph.
  */
  public boolean isMember(int categoryID)
  {
    for(int i = 0; i < graph.length; i++)
    {
      if(graph[i].isCategoryID(categoryID))
      {
        return true;
      }//end: if(graph[i]
    }//end: for(i)
		
    return false;
  }//end: isMember(String)
  
  /**
   * Checks to see if a given category name is a member of the graph.
   * 
   * @param name Name of the category
   * @return true/false based on if the category name is found in the graph.
   */
   public CategoryIDNode getMember(int categoryID)
   {
     for(int i = 0; i < graph.length; i++)
     {
       if(graph[i].isCategoryID(categoryID))
       {
         return graph[i];
       }//end: if(graph[i]
     }//end: for(i)
         
     return null;
   }//end: isMember(String)
	
 /**
  * Gets the number of categories in the graph.
  * 
  * @return Size of the category graph.
  */
  public int numCategories()
  {
    return graph.length;
  }//end: numCategories()
	
 /**
  * Finds all immediate categories of a given vertex.
  * 
  * @param vertex Vertex number.
  * @return Array of {@link CategoryNode} objects.
  */
  public CategoryIDNode[] getParents(int vertex)
  {
    TreeSet<CategoryIDNode> ts = new TreeSet<CategoryIDNode>();

    for(int i = 0; i < graph.length; i++)
    {
      //IntAVLTreeSet iats = graph[i].getArrLeaves();
      //if(iats != null && iats.contains(leaf)) {
      if(graph[i].isImmediateVertex(vertex))
      {
        ts.add(graph[i]);
      }
    }//end: for(i)
    
    CategoryIDNode[] cn = new CategoryIDNode[ts.size()];
    Iterator<CategoryIDNode> it = ts.iterator();
    int i = 0;
    while(it.hasNext()) {
      cn[i] = it.next();
      i++;
    }
    ts = null;
    return cn;
  }//end: getParents(int)
	
 /**
  * Writes the graph to a file.
  * <p>
  * Graph links are broken and stored as int[].
  * 
  * @param out Output file stream
  * @throws IOException
  */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(graph);
    out.writeInt(rootID);
  }//end: writeObject()
	
 /**
  * Reads a graph from a file.
  * <p>
  * Recreates the full graph structure from the stored graph links.
  * 
  * @param in Input file stream
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    graph = (CategoryIDNode[]) in.readObject();
    rootID = in.readInt();
  }//end: readObject()
}
