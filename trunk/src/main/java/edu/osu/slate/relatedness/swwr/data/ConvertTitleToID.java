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
public class ConvertTitleToID {
	
	/* */
	protected String[] titles;
	protected int[] ids;
		
	/**
	 * 
	 * O(log(n)) lookup time
	 * 
	 * @param title
	 * @return
	 */
	public int getID(String title) {
		int i = Arrays.binarySearch(titles, title);
		if(i>=0) {
			return ids[i];
		} else {
			return i;
		}
	}
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public boolean isLookupTitle(String title) {
		return (getID(title) >= 0);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumTitles() {
		return titles.length;
	}
	
	
	/**
	 * 
	 * @param temp
	 */
	public ConvertTitleToID(TitleID[] temp) {
		TitleID[] titleID = (TitleID[]) temp.clone();
		createArrays(titleID);
		titleID = null;
	}
	
	/**
	 * 
	 * @param filename
	 */
	public ConvertTitleToID(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			TitleID[] titleID = (TitleID[]) fileIn.readObject();
			fileIn.close();
			createArrays(titleID);
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
	
	/**
	 * 
	 * @param titleID
	 */
	private void createArrays(TitleID[] titleID) {
		TitleComparator tcom = new TitleComparator();
		Arrays.sort(titleID, tcom);
		titles = new String[titleID.length];
		ids = new int[titleID.length];
		for(int i=0; i<titles.length; i++) {
			titles[i] = titleID[i].getTitle();
			ids[i] = titleID[i].getID();
		}
	}//end: createArrays(TitleID[])
}
