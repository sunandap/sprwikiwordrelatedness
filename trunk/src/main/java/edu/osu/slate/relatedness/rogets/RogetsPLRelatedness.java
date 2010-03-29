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

package edu.osu.slate.relatedness.rogets;

import java.util.ArrayList;

import ca.site.elkb.RogetELKB;
import edu.osu.slate.relatedness.WordRelatedness;

/*******************************************************************************
 * 
 * Ca
 * 
 * Includes code from the original SemDist java class by
 * Mario Jarmasz & Alistair Kennedy
 * <p>
 * Similar to the program used to obtain the results of Roget's Thesaurus
 * and Semantic Similarity paper which can be found at:
 * http://www.site.uottawa.ca/~mjarmasz/pubs/jarmasz_roget_sim.pdf
 * 
 * @version 1.0
 * @author Mario Jarmasz
 * @author Alistair Kennedy
 * @author Tim Weale
 *
 *******************************************************************************/

public class RogetsPLRelatedness implements WordRelatedness {

	/**
	 * 
	 */
	private RogetELKB elkb;
	
	/**
	 * 
	 */
	private boolean MORPHOLOGY;
	
	/**
	 * 
	 */
	public RogetsPLRelatedness(String path) {
		elkb = new RogetELKB(path);
	}

	/**
	 * 
	 */
	public double getRelatedness(String w1, String w2) {
		MORPHOLOGY = true;
		return getSimilarity(w1, w2);
	}
	
	/**
 	 *
	 */
	public double[] getRelatedness(String w1) {
		return null;
	}
		
	/**
	 * Obtains the maximum similarity between two strings, passed as parameters.  
	 * All Parts of speech are considered.  The returned value is an integer valued
	 * 0, 2, ..., 16, where 16 is the most similar.
	 * 
	 * @param word1
	 * @param word2
	 * @return semantic relatedness between the words
	 */
	public int getSimilarity(String word1, String word2){
		ArrayList<int[]> list1 = elkb.index.getEntryListNumerical(word1, MORPHOLOGY);
		ArrayList<int[]> list2 = elkb.index.getEntryListNumerical(word2, MORPHOLOGY);
		
		if(list1.size() == 0 || list2.size() == 0){
			//return -1;
			return 0;
		}
		int best = 0;
		for (int i = 0; i < list1.size(); i++) {
			int[] entry1 = list1.get(i);
			for (int j = 0; j < list2.size(); j++) {
				int[] entry2 = list2.get(j);
				int diff = 16;
				for (int k = 0; k < 8; k++) {
					if (entry1[k] != entry2[k]){
						if(2*k < diff){
							diff = 2*k;
						}
					}
				}
				if(best < diff){
					best = diff;
				}
			}
		}
		return best;
	}//end: getSimilarity(String, String)
	
	/**
	 * Obtains the part of speech for the closest of these two words
	 * 
	 * @param word1
	 * @param word2
	 * @return string array with POS's
	 */
	public String[] getClosestPOS(String word1, String word2){
		String[] toReturn = new String[2];
		ArrayList<int[]> list1 = elkb.index.getEntryListNumerical(word1, MORPHOLOGY);
		ArrayList<int[]> list2 = elkb.index.getEntryListNumerical(word2, MORPHOLOGY);
		if(list1.size() == 0 || list2.size() == 0){
			return toReturn;
			//return 0;
		}
		int best = 0;
		String pos1 = "";
		String pos2 = "";
		for (int i = 0; i < list1.size(); i++) {
			int[] entry1 = list1.get(i);
			for (int j = 0; j < list2.size(); j++) {
				int[] entry2 = list2.get(j);
				int diff = 16;
				for (int k = 0; k < 8; k++) {
					if (entry1[k] != entry2[k]){
						if(2*k < diff){
							diff = 2*k;
						}
					}
				}
				if(best < diff){
					pos1 = elkb.index.convertToPOS(entry1[5]);
					pos2 = elkb.index.convertToPOS(entry2[5]);
					best = diff;
				}
			}
		}
		toReturn[0] = pos1;
		toReturn[1] = pos2;
		return toReturn;
	}//end: getClosestPOS(String, String)
	
	
	/**
	 * Obtains the maximum similarity between two strings, passed as parameters.  
	 * Only words of a given part of speech  are considered.  The returned value 
	 * is an integer valued 0, 2, ..., 16, where 16 is the most similar.
	 * 
	 * @param word1
	 * @param word2
	 * @param pos
	 * @return semantic relatedness between the words
	 */
	public int getSimilarity(String word1, String word2, String pos) {
		return getSimilarity(word1, pos, word2, pos);
	}//end: getSimilarity(String, String, String)
	
	/**
	 * Obtains the maximum similarity between two strings, passed as parameters.  
	 * Each word has a specified part of speech, other POS's are not considered.  
	 * The returned value is an integer valued 0, 2, ..., 16, where 16 is the most similar.
	 * 
	 * @param word1
	 * @param pos1
	 * @param word2
	 * @param pos2
	 * @return semantic relatedness between the words
	 */
	public int getSimilarity(String word1, String pos1, String word2, String pos2) {
		ArrayList<int[]> list1 = elkb.index.getEntryListNumerical(word1, MORPHOLOGY);
		ArrayList<int[]> list2 = elkb.index.getEntryListNumerical(word2, MORPHOLOGY);
		if(list1.size() == 0 || list2.size() == 0){
			//return -1;
			return 0;
		}
		int best = 0;
		for (int i = 0; i < list1.size(); i++) {
			int[] entry1 = list1.get(i);
			if(elkb.index.convertToPOS(entry1[5]).equals(pos1)){
				for (int j = 0; j < list2.size(); j++) {
					int[] entry2 = list2.get(j);
					if(elkb.index.convertToPOS(""+entry2[5]).equals(pos2)){
						int diff = 16;
						for (int k = 0; k < 8; k++) {
							if (entry1[k] != entry2[k]){
								if(2*k < diff){
									diff = 2*k;
								}
							}
						}
						if(best < diff){
							best = diff;
						}
					}
				}
			}
		}
		return best;
	}//end: getSimilarity(String, String, String, String)
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		RogetsPLRelatedness rr = new RogetsPLRelatedness("/u/weale/roget_elkb/1911");
		System.out.println(rr.getRelatedness("forest", "woods"));
	}
}
