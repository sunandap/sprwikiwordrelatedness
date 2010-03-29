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
import java.util.Comparator;

/**
 * 
 * @author weale
 *
 */
public class ConvertIDToTitle {
	
	/* */
	protected String[][] titles;
	protected int[] ids;
		
	/**
	 * 
	 * O(log(n)) lookup time
	 * 
	 * @param title
	 * @return
	 */
	public String[] getTitle(int id) {
		int i = Arrays.binarySearch(ids, id);
		if(i>=0 && i<ids.length) {
			return titles[i];
		} else {
			return null;
		}
	}
	
	public String printTitles(int id) {
		int i = Arrays.binarySearch(ids, id);
		if(i>=0 && i<ids.length) {
			String tmp = titles[i][0];
			for(int j=1; j<titles[i].length; j++){
				tmp = tmp + ", " + titles[i][j];
			}
			return tmp;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public boolean isLookupID(int id) {
		return (getTitle(id) != null);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumIDs() {
		return titles.length;
	}
	
	/**
	 * 
	 * @param filename
	 */
	public ConvertIDToTitle(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			TitleID[] titleID = (TitleID[]) fileIn.readObject();
			fileIn.close();
			IDComparator icom = new IDComparator();
			Arrays.sort(titleID, icom);
			
			ids = new int[titleID[titleID.length-1].getID()+1];
			titles = new String[ids.length][];
			
			for(int i=0; i<titles.length; i++) {
				titles[i] = null;
				ids[i] = i;
			}
			
			for(int i=0; i<titleID.length; i++) {
				int id = titleID[i].getID();

				if(titles[id] == null) {
					titles[id] = new String[1];
				} else {
					String[] tmp = new String[titles[id].length+1];
					for(int j=1; j<tmp.length;j++) {
						tmp[j] = titles[id][j-1];
					}
					titles[id] = tmp;
				}
				
				titles[id][0] = titleID[i].getTitle();
				Arrays.sort(titles[id]);
			}
			titleID = null;
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
