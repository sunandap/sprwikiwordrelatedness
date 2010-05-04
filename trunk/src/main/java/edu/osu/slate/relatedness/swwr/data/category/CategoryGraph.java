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

import it.unimi.dsi.fastutil.ints.*;
import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;
import edu.osu.slate.relatedness.swwr.data.graph.WikiInvGraph;

/**
 * Holds the acyclic category graph for Wikipedia/Wiktionary.
 * <p>
 * Graphs are 'rooted' at an individual node.
 * 
 * @author weale
 *
 */
public class CategoryGraph implements Serializable {

  /* Serialization ID */
  private static final long serialVersionUID = 1L;

  /* Array of CategoryNodes */
  private CategoryNode[] graph;
	
  /* Temporary TreeSet for initial growth */
  //private TreeSet<CategoryNode> ts;
	
  /* Name of the root CategoryNode */
  private int root;
	
 /**
  * Constructor.
  * <p>
  * Takes the name of the root category as input and creates a temporary graph structure to build upon.
  * 
  * @param root Name of the root category
  */
  public CategoryGraph(int rootID)
  {
    this.root = rootID;
		
    CategoryNode cn = new CategoryNode(root);
    graph = new CategoryNode[1];
    graph[0] = cn;	
  }//end: CategoryGraph(int)

 /**
  *  
  * @return
  */
  public int getTotalSize()
  {
    CategoryNode cn = new CategoryNode(root);
    int pos = Arrays.binarySearch(graph, cn);
    return graph[pos].getAllVertices().length;
  }
	
 /**
  * Adds a node to the graph with the given page name.
  * <p>
  * Only adds a {@link CategoryNodeOLD} if the node is not already present.
  * 
  * @param page Name of the page to add.
  */
  public void addNode(int catID)
  {
    CategoryNode cn = new CategoryNode(catID);

    if(Arrays.binarySearch(graph, cn) < 0)
    {
      CategoryNode[] tempGraph = new CategoryNode[graph.length+1];
      System.arraycopy(graph, 0, tempGraph, 0, graph.length);
      tempGraph[graph.length] = cn;
      graph = tempGraph;
      
      Arrays.sort(graph);
      tempGraph = null;
    }//end: if()
  }//end: addNode(String)
		
 /**
  * Add a link from the parent to child in the graph.
  * <p>
  * Graph nodes <b>must</b> be finalized before starting to add edges.
  * 
  * @param parent Category ID of the parent.
  * @param child Category ID of the child.
  */
  public void addEdge(int parent, int child)
  {
    CategoryNode p = new CategoryNode(parent);
    CategoryNode c = new CategoryNode(child);
    
    int parentIndex = Arrays.binarySearch(graph, p);
    int childIndex = Arrays.binarySearch(graph, c);
    
    if(parentIndex > -1 && childIndex > -1)
    {			
      graph[parentIndex].addChild(graph[childIndex]);
      graph[childIndex].addParent(graph[parentIndex]);
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
  public void addLeaf(int parentID, int vertexNum)
  {
    int parentPos = Arrays.binarySearch(graph, new CategoryNode(parentID));
    if(parentPos >= 0)
    {
      graph[parentPos].addImmediateLeaf(vertexNum);
    }
  }//end: addLeaf(int, int)
	
 /**
  * Creates coverage based on the in-bound edge count.
  * 
  * @param wg {@link WikiInvGraph} containing the vertex graph edges.
  */
  public void setInboundEdgeCounts(WikiInvGraph wg)
  {
    CategoryNode tmp = new CategoryNode(root);
    int index = Arrays.binarySearch(graph, tmp);
    
    graph[index].finalizeAllVertices();
    graph[index].setInboundVertexEdgeCount(wg);
  }//end: useInboundEdgeCounts(WikiInvGraph)
	
 /**
  * Creates coverage based on the out-bound edge count.
  * 
  * @param wg {@link WikiGraph} containing the vertex graph edges.
  */
  public void setOutboundEdgeCounts(WikiGraph wg)
  {
    CategoryNode tmp = new CategoryNode(root);
    int index = Arrays.binarySearch(graph, tmp);
     
    graph[index].finalizeAllVertices();
    graph[index].setOutboundVertexEdgeCount(wg);
  }//end: setOutboundEdgeCounts(WikiInvGraph)
  
 /**
  * Propagates leaf counts around the tree.
  */
  public void setVertexCounts()
  {
    CategoryNode tmp = new CategoryNode(root);
    int index = Arrays.binarySearch(graph, tmp);
	
    graph[index].finalizeAllVertices();
  }//end: propogateVertexCounts()
	
 /**
  * Recursive method to traverse the graph in a depth-first manner.
  * 
  * @param cn CategoryNode
  * @return Set of leaf IDs
  */
  private IntAVLTreeSet propogateLeaves(CategoryTitleNode cn) {
		
    /* Base Case: No Children */
    if(cn.getChildren() == null) {
      IntAVLTreeSet tmp = cn.getAllLeavesAVL();
      cn.finalizeAllLeaves();
      return tmp;
    }
		
    /* Recursive Case: Get the leaves of children */
    for(int i=0; i<cn.getChildren().length; i++) {
      
      IntAVLTreeSet ts = propogateLeaves(cn.getChildren()[i]);
			
      if(ts != null)
      {
        IntIterator it = ts.iterator();
        while(it.hasNext()) {
          cn.addAncestorLeaf(it.nextInt());
        }
      }
    }//end: for(i)
		
    IntAVLTreeSet tmp = cn.getAllLeavesAVL();
    cn.finalizeAllLeaves();
    return tmp;
  }
	
 /*
  * 
  */
//  private void finalizeImmediateLeaves()
//  {
//    for(int i = 0; i < graph.length; i++)
//    {
//      graph[i].finalizeImmediateLeaves();
//    }
//  }//end: finalizeImmediateLeaves()
	
 /**
  * Removes any cycles to create an acyclic category tree.
  * <p>
  * Traversal is done in a breadth-first manner.
  * Therefore, the removed cycles are movements upwards in the graph tree.
  */
  public void removeCycles()
  {
    LinkedList<VisitNode> ts = new LinkedList<VisitNode>();

    //Find the root in the tree
    CategoryNode tmp = new CategoryNode(root);
    int index = Arrays.binarySearch(graph, tmp);
    
    // Add the root to the VisitNode list
    ts.add(new VisitNode(index, new int[0]));

    // While there are nodes to visit
    while(!ts.isEmpty())
    {
      // Remove the first node from the list
      VisitNode vn = ts.removeFirst();
      
      // Make visit nodes for all the children
      VisitNode[] arr = vn.makeChildrenVisitNodes(graph);
      
      // Add visit nodes to the list
      for(int i = 0; arr != null && i < arr.length; i++)
      {
        ts.addLast(arr[i]);
      }//end: for(i)
      
    }//end: while(!ts)
    
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
  public void print()
  {
    for(int i=0; i<graph.length; i++)
    {
      System.out.print(i + "\t");
      graph[i].print();
    }
  }//end: print()
	
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
  public CategoryNode[] getParents(int vertex)
  {
    TreeSet<CategoryNode> ts = new TreeSet<CategoryNode>();

    for(int i = 0; i < graph.length; i++)
    {
      //IntAVLTreeSet iats = graph[i].getArrLeaves();
      //if(iats != null && iats.contains(leaf)) {
      if(graph[i].isImmediateVertex(vertex))
      {
        ts.add(graph[i]);
      }
    }//end: for(i)
    
    CategoryNode[] cn = new CategoryNode[ts.size()];
    Iterator<CategoryNode> it = ts.iterator();
    int i = 0;
    while(it.hasNext()) {
      cn[i] = it.next();
      i++;
    }
    ts = null;
    return cn;
  }
	
	
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
    /* Convert graph from CategoryNodes to ints */
    for(int i = 0; i < graph.length; i++)
    {
      graph[i].convertEdgesBeforeWrite(graph);
    }
		
    out.writeObject(graph);
    out.writeObject(root);
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
    graph = (CategoryNode[]) in.readObject();
    root = in.readInt();
		
    /* Convert graph from ints to CategoryNodes */
    for(int i = 0; i < graph.length; i++)
    {
      graph[i].convertEdgesAfterRead(graph);
    }//end:for(i)
  }//end: readObject()
}
