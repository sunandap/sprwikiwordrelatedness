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

package edu.osu.slate.relatedness.swwr.algorithm;

import java.util.TreeSet;

import edu.osu.slate.relatedness.RelatednessInterface;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;


/**
 * Implementation of the Ollivier-Senellart <b>(OS)</b> version of vertex relatedness on a graph.
 * <p>
 * <i>Exact</i> relatedness methods omit the jump vertex from the calculation and only take the link structure into account when calculating OS values.
 * These run slower and are not guaranteed to converge, but may produce higher-quality results.
 * <p>
 * See paper: Y. Ollivier and P. Senellart, Finding Related Pages Using Green Measures: An Illustration with Wikipedia.
 * 
 * @author weale
 * @version 1.0
 */
public class SPRValidVertexRelatedness extends PageRank implements RelatednessInterface{
	
	private int numValid;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6168622709678063605L;
	
	/**
	 * 
	 */
	private double [] GM_old;

	/**
	 * 
	 */
	private double [] GM_new;

	/**
	 * 
	 */
	private double [] PR_init;
	
	private boolean[] validVertex;
	
	/**
	 * Indicates the use of approximate (faster) or exact (more accurate) calculations.
	 * <br>
	 * Approximate calculations are guaranteed to converge, while exact calculations are not.
	 */
	protected boolean approximate = false;
	
	/**
	 * Constructor for GreenRelatedness.  Calls the {@link PageRank} constructor.
	 * 
	 * @param graphFileName {@link java.lang.String} containing the path to the graph file.
	 */
	public SPRValidVertexRelatedness(String graphFileName) {
		super(graphFileName);
	}

	/**
	 * Constructor for GreenRelatedness.  Calls the {@link PageRank} constructor.
	 * 
	 * @param graph Previously initialized {@link WikiGraph} structure
	 */
	public SPRValidVertexRelatedness(WikiGraph graph, TreeSet<Integer> validVertices) {
		super(graph);
		numValid = 0;
		validVertex = new boolean[graph.getNumVertices()];
		for(int i=0; i<validVertex.length; i++) {
			validVertex[i] = validVertices.contains(i);
			if(validVertex[i]) { numValid++; }
		}//end: for(i)
	}
		
	/**
	 * Finds the relatedness value between two vertices (compressed value) using the approximate inference routine.
	 * <p>
	 * This runs faster than getExactRelatedness, but may return less accurate results.
	 * This is guaranteed to converge.
	 * <p>
	 * Requires relatedness calculations on the full-graph.
	 * 
	 * @param from Vertex ID number (compressed)
	 * @param to Vertex ID number (compressed)
	 * @return GreenMeasure resulting from running relatedness measure.
	 */
//	public double getRelatedness(int from, int to) {
//		
//		// Set Approximate Flag
//		approximate = true;
//		
//		// Return Results of getRelatedness
//		return getExactRelatedness(from, to);
//	}
	
	/**
	 * Finds the relatedness value between two vertices (compressed value) using the approximate inference routine.
	 * <p>
	 * This runs faster than getExactRelatedness, but may return less accurate results.
	 * This is guaranteed to converge.
	 * <p>
	 * Requires relatedness calculations on the full-graph.
	 * 
	 * @param from Vertex ID number (compressed)
	 * @param to Vertex ID number (compressed)
	 * @return GreenMeasure resulting from running relatedness measure.
	 */
	public double getRelatedness(int[] from, int to) {
		
		// Set Approximate Flag
		approximate = true;
		
		// Return Results of getRelatedness
		return getRelatedness(from, to);
	}
	
	/**
	 * Finds the relatedness value between two vertices using the exact inference routine.
	 * <p>
	 * This runs slower than getRelatedness, but should return more accurate results.
	 * This is NOT guaranteed to converge.
	 * <p>
	 * Requires relatedness calculations on the full-graph.
	 *
	 * @param from Vertex ID number (compressed)
	 * @param to Vertex ID number (compressed)
	 * @return GreenMeasure resulting from running relatedness measure.
	 */
	public double getRelatedness(int from, int to) {
		
		// Return value at the 'to' index
		return getRelatedness()[to];
	}
	
	/**
 	 * Finds the relatedness distribution sourced at a vertex using the approximate inference routine.
	 * <p>
	 * This runs faster than getExactRelatedness, but should return less accurate results.
	 * <p>
	 * This is guaranteed to converge.
	 *
	 * @param from Vertex ID number (compressed)
	 * @return Array containing relatedness distribution
	 */
	//public double[] getRelatedness(int from) {

		// Set Approximate Flag
//		approximate = true;
		
		// Return Results of getRelatednessDistribution
//		return getRelatedness(from);
//	}
	
	/**
 	 * Finds the relatedness distribution sourced at a vertex using the approximate inference routine.
	 * <p>
	 * This runs faster than getExactRelatedness, but should return less accurate results.
	 * <p>
	 * This is guaranteed to converge.
	 *
	 * @param from Vertex ID number (compressed)
	 * @return Array containing relatedness distribution
	 */
	public double[] getRelatedness(int[] from) {

		// Set Approximate Flag
		approximate = true;
		
		GM_old = new double[graph.length];
		GM_new = new double[graph.length];
		PR_init = new double[graph.length];

		for(int j=0;j<PR_init.length;j++) {
			PR_init[j] = PR[j] * -1;
			GM_old[j] = PR_init[j];
		}
		
		for(int i=0; i<from.length; i++) {
			PR_init[from[i]] = PR_init[from[i]] + (1.0/from.length);
			GM_old[from[i]] = GM_old[from[i]] + (1.0/from.length);
		}
		
		// Return Results of getRelatednessDistribution
		return getRelatedness();
	}
	
	/**
	 * 
	 */
	public double[] getRelatedness(int[] from, float[] vals) {
		
		approximate = true;
		
		GM_old = new double[graph.length];
		GM_new = new double[graph.length];
		PR_init = new double[graph.length];

		for(int j=0;j<PR_init.length;j++) {
			PR_init[j] = PR[j] * -1;
			GM_old[j] = PR_init[j];
		}
		
		for(int i=0; i<from.length; i++) {
			PR_init[from[i]] = PR_init[from[i]] + vals[i];
			GM_old[from[i]] = GM_old[from[i]] + vals[i];
		}
		
		return getRelatedness();
	}
	
	/**
 	 * Finds the relatedness distribution sourced at a vertex using the exact inference routine.
	 * <p>
	 * This runs slower than getRelatedness, but should return more accurate results.
	 * <p>
	 * This is NOT guaranteed to converge.
	 *
	 * @param from Vertex ID number (compressed)
	 * @return Array containing relatedness distribution
	 */
	public double[] getRelatedness(int from) {
		
		GM_old = new double[graph.length];
		GM_new = new double[graph.length];
		PR_init = new double[graph.length];

		for(int j=0;j<PR_init.length;j++) {
			PR_init[j] = PR[j] * -1;
			GM_old[j] = PR_init[j];
		}
		
		PR_init[from] = PR_init[from] + 1;
		GM_old[from] = GM_old[from] + 1;
		
		return getRelatedness();
	}
	
	/**
 	 * Finds the relatedness distribution sourced at a vertex using the exact inference routine.
	 * <p>
	 * This runs slower than getRelatedness, but should return more accurate results.
	 * <p>
	 * This is NOT guaranteed to converge.
	 *
	 * @param from Vertex ID number (compressed)
	 * @return Array containing relatedness distribution
	 */
	public double[] getRelatedness() {
		
		int numIterations = 0;
		double change;
		do {
			double randomSurfer = 0;
			
			for(int j = 0; j < graph.length; j++) {
				
				if(graph[j] != null && graph[j].length != 0) {
					// Valid transition array
					
					for(int k=0; k<graph[j].length; k++) {
						GM_new[graph[j][k]] += (GM_old[j] * tProb[j][k]);
					}//end: for(k)
				}
				else {
					// Add transition values to randomSurfer
					randomSurfer += GM_old[j] / graph.length;
				}
				
			}//end: for(j)

			for(int x=0; x<GM_new.length; x++) {
				GM_new[x] = .85 * ((GM_new[x] + randomSurfer) + PR_init[x]) + (.15 / PR_init.length);
			}

			change = PRDiff(GM_old, GM_new);

			double tmp = 0.0;
			for(int x=0; x<GM_new.length; x++) {
				GM_old[x] = GM_new[x];
				tmp += GM_old[x];
				GM_new[x] = 0;
			}


			numIterations++;
		}while(change > 0.002);
		
		for(int i=0; i<GM_new.length; i++) {
			GM_new[i] = GM_old[i];
		}//end: for(i)
		
		/* Converged. Spread weight to valid vertices. */
		//do {
		double randomSurfer = 0;
		change = 0.0;
		for(int i=0; i<graph.length; i++) {
				
			/* if it's not a valid vertex */
			if(!validVertex[i]) {
					
				/* Spread weight to all valid neighbors */
				if(graph[i] != null && graph[i].length != 0) {
						
					// Valid transition array
					int numLocalValid = 0;
					for(int j=0; j<graph[i].length; j++) {
						if(validVertex[graph[i][j]]) {
							numLocalValid++;
						}//end: if valid
					}//end: for(j)
												
					if(numLocalValid > 0) {
						for(int j=0; j<graph[i].length; j++) {
							if(validVertex[graph[i][j]]) {
								GM_new[graph[i][j]] += GM_old[i] / numLocalValid;
							}//end: if valid
						}//end: for(j)
					}
				}
					//else {
						// Add transition values to randomSurfer
					//	randomSurfer += GM_old[i] / numValid;	
					//}
				GM_new[i] = 0;
			}
				
		}//end: for(i)
			
		// Spread all weight to valid vertices
		for(int x=0; x<GM_new.length; x++) {
			if(validVertex[x]) {
				GM_new[x] = GM_new[x] + randomSurfer;
			}
		}
		//change = PRDiff(GM_old, GM_new);
		
		double magnitude = 0.0;
		for(int i=0; i<GM_new.length; i++) {
			magnitude += GM_new[i];
		}//end: for(i)
					
		for(int i=0; i<GM_new.length; i++) {
			GM_new[i] = GM_new[i] / magnitude;
			GM_new[i] = GM_new[i] * Math.log10(1.0/PR[i]);
		}//end: for(i)
		
		return GM_new;
	}
	
	/**
	 * Calculates the absolute change between two GreenMeasure value arrays.
	 * 
	 * @param i Old GreenMeasure array
	 * @param j New GreenMeasure array
	 * @return Sum of the absolute value of the difference between all corresponding element pairs.
	 */
	private static double PRDiff(double [] i, double[] j) {
		double diff = 0;
		
		for(int x=0; x<i.length; x++) {
			diff += Math.abs(i[x] - j[x]);
		}
		
		return diff;
	}
	
}//end: GreenRelatedness