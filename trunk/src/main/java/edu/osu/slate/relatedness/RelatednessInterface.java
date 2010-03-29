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

package edu.osu.slate.relatedness;

public interface RelatednessInterface {
	
	/**
	 * Calculates and returns the relatedness value for a single vertex in the graph.
	 * <br>
	 * This may or may not entail calculating the values for all vertices in the graph.
	 * Check with the implementing class for details.
	 * 
	 * @param from Vertex ID number (compressed)
	 * @param to Vertex ID number (compressed)
	 * @return Value of relatedness for the <i>to</i> vertex, given the <i>from</i> vertex.
	 */
	public double getRelatedness(int from, int to);
	
	/**
	 * Calculates and returns the relatedness for all vertices in the graph.
	 * 
	 * @param from Vertex ID number (compressed)
	 * @return A double[] of values for each graph vertex, given the <i>from</i> vertex.
	 */
	public double[] getRelatedness(int from);

	/**
	 * Calculates and returns the relatedness for all vertices in the graph.
	 * 
	 * @param from Vertex ID numbers (compressed)
	 * @return A double[] of values for each graph vertex, given the <i>from</i> vertex list.
	 */
	public double[] getRelatedness(int[] from);
	
	/**
	 * Calculates and returns the relatedness for all vertices in the graph.
	 * 
	 * @param from from Vertex ID numbers (compressed)
	 * @param vals non-uniform weighting values for the vertex numbers
	 * @return A double[] of values for each graph vertex, given the <i>from</i> vertex list.
	 */
	public double[] getRelatedness(int[] from, float[] vals);
}
