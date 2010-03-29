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

package edu.osu.slate.relatedness.zesch.wiktionary;

import java.util.Iterator;
import java.util.List;

import de.tudarmstadt.ukp.wiktionary.api.RelationType;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;

import edu.osu.slate.relatedness.WordRelatedness;

/**
 * Implements Pseudo Gloss-based Relatedness Using Wiktionary
 * <p>
 * Described in <i>"Study of Semantic Relatedness of Words Using Collaboratively Constructed Semantic Resources"</i>, Torsten Zesch 2009
 * 
 * @author weale
 *
 */
public class WiktionaryPseudoGlossRelatedness implements WordRelatedness {

	private String directory;
	private Wiktionary wkt;
	private static String type;
	
	private LinkedList<WordEntry> getEntries(String w) {
	  List<WordEntry> w1Entries = wkt.getWordEntries(w);
	  Iterator<WordEntry> weit1 = w1Entries.iterator();
	  while(weit1.hasNext()) {
        
        // Grab the gloss
        // Create gloss multi-set
        WordEntry we1 = weit1.next();
        we1.getAssignedRelatedWords(arg0, arg1)
	  }
	  
	  return null;
	}
	
	public double getRelatedness(String w1, String w2) {
	  w1 = w1.toLowerCase();
	  w2 = w2.toLowerCase();
	        
	  List<WordEntry> w1Entries = wkt.getWordEntries(w1);
	        
	  // For each word1 Entry
	  Iterator<WordEntry> weit1 = w1Entries.iterator();
	  while(weit1.hasNext()) {
	    
	    // Grab the gloss
	    // Create gloss multi-set
	    WordEntry we1 = weit1.next();
	  }//end: while(weit1)	            
	  return 0.0;
	}

	public double getRelatednessOLD(String w1, String w2) {
		w1 = w1.toLowerCase();
		w2 = w2.toLowerCase();
		
		List<WordEntry> w1Entries = wkt.getWordEntries(w1);
		List<WordEntry> w2Entries = wkt.getWordEntries(w2);		
		
		// Overlap number
		double overlap = 0.0;
		
		// For each word1 Entry
		Iterator<WordEntry> weit1 = w1Entries.iterator();
		while(weit1.hasNext()) {
			
			// Grab the gloss
			// Create gloss multi-set
			WordEntry we1 = weit1.next();
			
			for(int word1Sense=0; word1Sense < we1.getNumberOfSenses(); word1Sense++) {
				// Add original word to multi-set
				System.out.print(w2);
				
				List<String> list1 = we1.getAssignedRelatedWords(RelationType.HYPERNYM, word1Sense);
				for(int i=0;i<list1.size(); i++) {
					System.out.print(" " + list1.get(i).toLowerCase());
				}
				
				List<String> list2 = we1.getAssignedRelatedWords(RelationType.HYPONYM, word1Sense);
				for(int i=0;i<list2.size(); i++) {
					System.out.print(" " + list2.get(i).toLowerCase());
				}
				System.out.println();
			}
		}
		
		return 0.0;
	}

	public double[] getRelatedness(String w) {
		// TODO Auto-generated method stub
		return null;
	}

	public WiktionaryPseudoGlossRelatedness(String dir) {
		if(dir == null) {
			dir = "/scratch/weale/data/binary/zesch/enwiktionary/20071016";
			//dir = "/scratch/weale/data/binary/zesch/enwiktionary/20090203";
		}
		
		 wkt = new Wiktionary(dir);
	}
	
	public static void main(String [] args) {
		WiktionaryPseudoGlossRelatedness fr = new WiktionaryPseudoGlossRelatedness(null);
		List<WordEntry> entries = fr.wkt.getWordEntries("good");
		System.out.println(Wiktionary.getEntryInformation(entries));
		//System.out.println(fr.getRelatedness("apple", "orange"));
	}
	
}
