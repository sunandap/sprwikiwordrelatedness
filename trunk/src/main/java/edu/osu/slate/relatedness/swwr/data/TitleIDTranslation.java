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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This file creates a TreeMap<String,Integer> that is used to link Page Titles with Page IDs for translation.
 * 
 * @author weale
 *
 */
public class TitleIDTranslation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * TreeMap containing String to Integer conversions
	 */
	private TreeMap<String,Integer> tm;
	
	/**
	 * Constructor where the class is initialized from an existing lookup file
	 * <p>
	 * File format is assumed to be ID,TITLE.  Each lookup record is contained on its own line.
	 * 
	 * @param filename Name of the existing lookup file
	 */
	public TitleIDTranslation(String filename) {
		tm = new TreeMap<String,Integer>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String str = br.readLine();
			while(str != null) {
				str = str.toLowerCase();
				int comma = str.indexOf(",");
				int id = Integer.parseInt(str.substring(0,comma));
				String title = str.substring(comma+1);
				tm.put(title, id);
				str = br.readLine();
			}
			br.close();
		} catch(IOException e) {
			System.out.println("Error in LookupFile(String)");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	/**
	 * Converts the given title to the appropriate page ID.
	 * 
	 * @param title Name of the page
	 * @return ID corresponding to the page title.
	 */
	public int getID(String title) {
		if(isLookupTitle(title)) 
			return tm.get(title);
		else
			return -1;
	}
	
	/**
	 * Checks to see if the page title is found in the lookup table.
	 * 
	 * @param title Name of the page
	 * @return If title is found in the lookup table
	 */
	public boolean isLookupTitle(String title) {
		return tm.containsKey(title);
	}
	
	/**
	 * Checks to see if the page ID is found in the lookup table.
	 * 
	 * @param id Page ID value
	 * @return If ID is found in the lookup table.
	 */
	public boolean isTitleID(int id) {
		return tm.containsValue(id);
	}
	
	public TreeMap<String,Integer> getLookupMap() {
		return tm;
	}
	
	/**
	 * writeObject used in Serialization
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(tm);	
	}
	
	/**
	 * readObject used in Serialization
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		tm = (TreeMap<String,Integer>) in.readObject();
	}
}
