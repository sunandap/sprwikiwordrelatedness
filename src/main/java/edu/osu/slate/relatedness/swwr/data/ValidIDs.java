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
 * This class is used to contain a list of the valid pages found in the graph.
 * <p>
 * File input is a simple list of valid page numbers.
 * <p>
 * 
 * @author weale
 * @version 1.0
 * 
 */
public class ValidIDs {

	/**
	 * Integer array containing the values of the valid ids in the graph.
	 * The position of the ids in the array correspond to their 'compressed' id.
	 * 
	 */
	private int[] validList;
	
	/**
	 * Given the name of the file, this constructor parses the file and stores the valid ids for future use.
	 * 
	 * @param filename
	 */
	public ValidIDs(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			validList = (int[]) fileIn.readObject();
			fileIn.close();
			
			//Ensure a sorted list
			Arrays.sort(validList);
			
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
	
	public int numValidIDs() {
		return validList.length;
	}
	
	/**
	 * This method takes a non-'compressed' ID value and returns whether or not that id was considered a 'valid' id in the graph.
	 * 
	 * @param id non-'compressed' ID value
	 * @return boolean value corresponding to whether or not the id is classifies as 'valid'
	 */
	public boolean isValidID(int id) {
		return (Arrays.binarySearch(validList, id) >= 0);
	}
	
	/**
	 * This method takes a non-'compressed' ID value and returns the 'compressed' ID value.
	 * <p>
	 * If the ID is valid, the method will return a positive number (>=0).  Invalid IDs return a negative value (>0).
	 * 
	 * @param id non-'compressed' ID value
	 * @return integer value of the 'compressed' ID value
	 */
	public int getCompressedID(int id) {
		return Arrays.binarySearch(validList, id);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public int getOriginalID(int id) {
		if(id > -1 && id < validList.length) {
			return validList[id];
		} else {
			return -1;
		}
	}
	
	public static void main(String[] args) {
		ValidIDs vid = new ValidIDs("/scratch/weale/data/binary/enwiki-20080103-M2.vid");
		System.out.println(vid.isValidID(12));
		System.out.println(vid.isValidID(13));
		System.out.println(vid.isValidID(156));
		System.out.println(vid.getCompressedID(12));
		System.out.println(vid.getCompressedID(12234));
		System.out.println(vid.getCompressedID(1221612));
		for(int i=0; i<vid.validList.length; i++) {
			System.out.println(vid.validList[i]);
		}
	}
}
