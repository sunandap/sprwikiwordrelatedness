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

package edu.osu.slate.relatedness.swwr.data;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 * This contains the graph structure of the Wikipedia data set.  This class keeps track of which pages are linked to by particular page.
 * <p>
 * That is, this contains the outbound list to a page (from, to, to, to,...)
 * 
 * @author weale
 * @version 1.0; beta
 */

public class WikiGraph implements Serializable {
	
	/* Used for serialization */
	private static final long serialVersionUID = 1L;

	/**
	 *  Array of arrays to keep the graph structure in memory.
	 */
	protected int [][] graph;
	
	/** 
	 * Array of transition probabilities
	 */
	protected float [][] tProb;
	
	/* Keeps track of the number of graph edges */
	protected int numEdges;
	
	/* */
	protected boolean isUniform;
	
	/* */
	protected boolean isDirected = true;
	
	/**
	 * 
	 */
	protected static boolean verbose = false;
	
	/**
	 * 
	 * @param v
	 */
	public static void setVerbose(boolean v) {
		verbose = v;
	}
	
	/**
	 * Creates a new WikiGraph from an existing WikiGraph in memory.
	 * <p>
	 * Creates new graph and probability matrices.
	 * 
	 * @param wg Existing WikiGraph.
	 */
	public WikiGraph(WikiGraph wg) {
		this.graph = wg.graph.clone();
		this.tProb = wg.tProb.clone();
		this.numEdges = wg.numEdges;
		this.isUniform = wg.isUniform;
		this.isDirected = wg.isDirected;
	}
	
	/**
	 * Reads a WikiGraph from an existing .wgp file.
	 * 
	 * @param filename Name of the (.wgp) file
	 */
	public WikiGraph(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			graph = (int[][]) fileIn.readObject();
			tProb = (float[][]) fileIn.readObject();
			fileIn.close();
			
			/* Check for uniform transition probabilities */
			isUniform = true;
			for(int i=0; isUniform && i<tProb.length; i++) {
				if(tProb[i] != null && tProb[i].length > 1) {
					isUniform = false;
				}
			}
			
			if(isUniform) {
				for(int i=0; i<tProb.length; i++) {
					if(graph[i] != null) {
						tProb[i] = new float[graph[i].length];
						for(int j=0; j<tProb[i].length; j++) {
							tProb[i][j] = (float)(1.0 / tProb[i].length);
						}
					}
				}
			}
			
			/* Initialize the number of edges */
			numEdges = 0;
			for(int i=0; i<graph.length; i++) {
				if(graph[i] != null) {
					numEdges += graph[i].length;
				}
			}
			
		} catch (ClassNotFoundException e) {
			System.err.println("Problem converting to an integer array: " + filename);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filename);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem reading from file: " + filename);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param id ID number of the node
	 * @return Outbound array of links for the node or null if ID is invalid.
	 */
	public int[] getOutboundLinks(int id) {
		if(id > -1 && id < graph.length) {
			return graph[id];
		} else {
			return null;
		}
	}
		
	/**
	 * 
	 * @param id ID number of the node
	 * @return Transition array for the ID or null if ID is invalid.
	 */
	public float[] getOutboundTransitions(int id) {
		if(id > -1 && id < graph.length) {
			return tProb[id];
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumEdges() {
		return numEdges;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumVertices() {
		return graph.length;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isUniformTransition() {
		return isUniform;
	}
	
	/**
	 * Add new edges to the graph to make it bi-directional
	 */
	public void makeUndirected() {
		
		/* For each vertex in the graph */
		for(int i=0; i<graph.length; i++){
			
			/*  Check to make sure there's an inbound link.
			 *  For each inbound edge in the graph */
			for(int j=0; graph[i]!=null && j<graph[i].length; j++) {
				
				int to = graph[i][j];
			
				/* Check that there's inbound links on the other side */
				if(graph[to] != null) {
					
					/* Check for symmetric link */
					int pos = Arrays.binarySearch(graph[to], i);
					
					if(pos < 0) {
						// Does not exist, add link
						int[] tmp = new int[graph[to].length+1];
						tmp[0] = i;
						for(int k=1; k<tmp.length; k++) {
							tmp[k] = graph[to][k-1];
						}
						Arrays.sort(tmp);
						graph[to] = tmp;					
					}//end: if(pos)
				} else {
					
					// No inbound links, add new array
					graph[to] = new int[1];
					graph[to][0] = i;
				}//end: null check
			}//end: for(j)
		}//end: for(i)
		
		/* Update transition probabilities based on new graph */
		for(int i=0; i<graph.length; i++){
			tProb[i] = new float[graph[i].length];
			
			for(int j=0; j<graph[i].length; j++) {
				tProb[i][j] = (float) 1.0 / tProb[i].length;
			}//end: for(j)
		}//end: for(i)
		
		isDirected = false;
	}
	
	/** 
	 * Returns whether or not the graph has been specifically set to be undirected or not.
	 * <p>
	 * False values indicate that the graph has been forced to be bi-directional.
	 * 
	 * @return Boolean value for directedness of the graph.
	 */
	public boolean isDirected() {
		return isDirected;
	}
	
	/**
	 * 
	 * Writes the graph (int[][]) followed by the transition matrix (float[][]) and the edge count (int).
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(graph);	
		out.writeObject(tProb);	
		out.writeInt(numEdges);	
	}
	
	/**
	 * 
	 * Reads the graph (int[][]) followed by the transition matrix (float[][]) and the count of edges (int).
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		graph = (int [][]) in.readObject();
		tProb = (float [][]) in.readObject();
		numEdges = in.readInt();
	}
}