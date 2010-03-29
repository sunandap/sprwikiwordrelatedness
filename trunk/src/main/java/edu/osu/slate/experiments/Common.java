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

package edu.osu.slate.experiments;

import java.util.*;

import edu.osu.slate.relatedness.swwr.data.AliasStrings;

public class Common {
	
	public static String type, date, source;
	public static String seed, countSource, algorithm;
	public static String task;
	
	/**
	 * Returns the surface form IDs for the given string.
	 * 
	 * @param as {@link AliasStrings} containing string->id translations
	 * @param w String to match
	 * @return array of possible IDs
	 */
	public static int[] getCandidateSFIDS(AliasStrings as, String w) {
		int id = as.getID(w);
		int [] idArr = null;
		if(id > -1) {
			idArr = new int[1];
			idArr[0] = id;
		} else {
			String[] SFArr = w.split(" ");
			TreeSet<Integer> tmpTree = new TreeSet<Integer>();
			for(int j=0; j<SFArr.length; j++) {
				int tmp = as.getID(SFArr[j]);
				if(tmp > 0) {
					tmpTree.add(tmp);
				}
			}
			idArr = new int[tmpTree.size()];
			Iterator<Integer> it = tmpTree.iterator();
			int j=0;
			while(it.hasNext()) {
				idArr[j] = it.next();
				j++;
			}
		}
		return idArr;
	}
	
	/**
	 * 
	 * @param as
	 * @param w
	 * @return
	 */
	public static String resolve(AliasStrings as, String w) {
		
		/* Check to see if current string is valid  *
		 * If valid, return it.                     */
		if(as.getID(w) != -1)
			return w;
		
		/* String is not valid. */
		w = w.replace('-', ' ');
			
		String [] arr = w.split(" ");
		
		/* Remove common words */
		for(int i=0;i<arr.length;i++) {
			if(arr[i].equals("and")) {
				arr[i] = "";
			}
			else if(arr[i].equals("or")) {
				arr[i] = "";
			}
			else if(arr[i].equals("to")) {
				arr[i] = "";
			}
			else if(arr[i].equals("be")) {
				arr[i] = "";
			}
			else if(arr[i].equals("the")) {
				arr[i] = "";
			}
			else if(arr[i].equals("a")) {
				arr[i] = "";
			}
			else if(arr[i].equals("of")) {
				arr[i] = "";
			}
			else if(arr[i].equals("on")) {
				arr[i] = "";
			}
			else if(arr[i].equals("in")) {
				arr[i] = "";
			}
			else if(arr[i].equals("for")) {
				arr[i] = "";
			}
			else if(arr[i].equals("with")) {
				arr[i] = "";
			}
			else if(arr[i].equals("by")) {
				arr[i] = "";
			}
			else if(arr[i].equals("into")) {
				arr[i] = "";
			}
			else if(arr[i].equals("an")) {
				arr[i] = "";
			}
			else if(arr[i].equals("is")) {
				arr[i] = "";
			}
			else if(arr[i].equals("no")) {
				arr[i] = "";
			}
		}
			
		/* Create new string from the String array */
		w = "";
		for(int i=0;i<arr.length;i++) {
			w = w + arr[i] + " ";
		}
		w = w.trim();
		return w;
	}//end:resolve(AliasStrings, String)
}
