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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * 
 * @author weale
 * @version 1.0
 */
public class RedirectList {
	
	private int[] from;
	private int[] to;

	/**
	 * 
	 * @param id 'uncompressed' ID value
	 * @return redirected id value (-1 if not found)
	 */
	public int redirect(int id) {
		int pos = Arrays.binarySearch(from, id);
		if(pos >=0) {
			return to[pos];
		} else {
			return -1;
		}
	}


	/**
	 * 
	 * 
	 * @param id 'uncompressed' ID value
	 * @return boolean based on if the ID value is found in the redirect list
	 */
	public boolean isRedirectID(int id) {
		return (Arrays.binarySearch(from, id) >= 0);
	}
	
	/**
	 * Given the name of the file, this constructor parses the file and stores the valid ids for future use.
	 * 
	 * @param filename
	 */
	public RedirectList(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			from = (int[]) fileIn.readObject();
			to = (int[]) fileIn.readObject();
			fileIn.close();
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
}
