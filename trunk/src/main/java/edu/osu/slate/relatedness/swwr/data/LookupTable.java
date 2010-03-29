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
import java.util.*;

/**
 * 
 * @author weale
 *
 */
public class LookupTable {
	
	private double[][] table;
	private int[] IDS;
	
	/**
	 * 
	 * @param filename
	 */
	public LookupTable(String filename) {
		try {
			double [] tmp;
			TreeSet<Integer> ts = new TreeSet<Integer>();
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			try {
				while(true) {
					int id = fileIn.readInt();
					ts.add(id);
					tmp = (double[]) fileIn.readObject();
				}
			} catch(IOException e) {}
			fileIn.close();
			
			/* Add words to list */
			IDS = new int[ts.size()];
			Iterator<Integer> it = ts.iterator();
			int pos = 0;
			while(it.hasNext()) {
				IDS[pos] = it.next();
				pos++;
			}
			
			/* Add transition table */
			table = new double[ts.size()][];
			fileIn = new ObjectInputStream(new FileInputStream(filename));
			try {
				while(true) {
					int id = fileIn.readInt();
					pos = Arrays.binarySearch(IDS, id);
					tmp = (double[]) fileIn.readObject();
					table[pos] = tmp;
				}
			} catch(IOException e) {}
			
			fileIn.close();
			//System.out.println("Num Vertex:" + table[0].length);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public double[] getDistribution(int id) {
		
		int pos = Arrays.binarySearch(IDS, id);
		if(pos > -1) {
			return table[pos];
		} else {
			return null;
		}
		
	}
	
}
