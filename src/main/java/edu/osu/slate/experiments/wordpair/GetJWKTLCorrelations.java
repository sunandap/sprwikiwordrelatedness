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

package edu.osu.slate.experiments.wordpair;

import java.util.*;
import java.io.*;

import de.tudarmstadt.ukp.wiktionary.api.Quotation;
import de.tudarmstadt.ukp.wiktionary.api.RelationType;
import de.tudarmstadt.ukp.wiktionary.api.WikiString;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;
import edu.osu.slate.experiments.Common;
import edu.osu.slate.experiments.Pearson;
import edu.osu.slate.experiments.Spearman;

/**
 * Calculates the Pearson correlation coefficient between a given data set and the output of a given relatedness data set.
 * 
 * @author weale
 *
 */
public class GetJWKTLCorrelations {

	private static boolean verbose = true;
	private static String taskFile;
	private static boolean valid = true;
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static void main(String [] args) throws IOException, ClassNotFoundException {
		
		/* Set parameters */
		if(args.length == 0) {
			Common.type = "enwiktionary";
			//Common.type = "enwiki";
			Common.date = "20090203";
			//Common.date = "20080103";
			Common.source = "M";
			//Common.source = "english_language";
			//Common.seed = "'*Topics'";
			Common.seed = "";
			//Common.countSource = "v";
			Common.countSource = "";
			Common.algorithm = "";
			//Common.task = "MC30";
			Common.task = "RG65";
			//Common.task = "WS1";
			//Common.task = "WS2";
			//Common.task = "WS353";
		} else {
			setParameters(args);
		}
		setFiles();
		
		boolean report = true;
		
		if(verbose) {
			System.out.println("Setting Synonym Task: " + Common.task);
		}
		Scanner s = new Scanner(new FileReader(taskFile));
		
		String outputDirectory = "/scratch/weale/data/binary/zesch/enwiktionary/20071016";
		Wiktionary wkt = new Wiktionary(outputDirectory);
		
		// Relatedness value vectors
		Vector<Double> X = new Vector<Double>();
		Vector<Double> Y = new Vector<Double>();
		int i=0;
		
		while(s.hasNext()) {
			String str = s.nextLine();
			String[] arr = str.split(",");
			System.out.println(str);
			//double d = getGlossRelatedness(arr[0], arr[1], wkt);
			double d = getGlossRelatednessZ(arr[0], arr[1], wkt);
			
			// if the word pair is found
			if(valid) {

				// Trim off the top and bottom of the range.
				if(d<0.000000001) {
					//d=0.000000001;
				} else if(d > 1) {
					//d = 1;
				}
				
				if(report) {
					//System.out.println(arr[2] + " " + d);
				}
				
				X.add(Double.parseDouble(arr[2]));
				Y.add(d);
			}
			
			i++;
		}//end while(hasNext())
		
		// Calculate the relatedness correlation
		System.out.println("p: " + Spearman.GetCorrelation(X,Y));
		System.out.println("r: " + Pearson.GetCorrelation(X,Y));
	}//end: main

	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 */
	private static double getCVRelatedness(String word1, String word2, Wiktionary wkt) {
		Iterator<WordEntry> it = wkt.wordEntryIterator();
		int index=1;
		System.out.println(word1);
		TreeMap<Integer, Double> word1TM = new TreeMap<Integer,Double>();
		TreeMap<Integer, Double> word2TM = new TreeMap<Integer,Double>();
		
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ws = we.getGlosses();
			for(int i=0; i<ws.size(); i++) {
				WikiString w = ws.get(i);
				if(w.getPlainText().indexOf(word1) != -1) {
					word1TM.put(index, 1.0);
				}
				if(w.getPlainText().indexOf(word2) != -1) {
					word2TM.put(index, 1.0);
				}
			}//end: for(i)
			
			index++;
		}//end: while(it)
		System.out.println(word1);
		double d = 0.0;
		Set<Map.Entry<Integer,Double>> set = word1TM.entrySet();
		Iterator<Map.Entry<Integer, Double>> setIterator = set.iterator();
		while(setIterator.hasNext()) {
			Map.Entry<Integer,Double> me = setIterator.next();
			if(word2TM.containsKey(me.getKey())) {
				d = d + 1.0;
			}
		}
		return d;
	}
	
	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 */
	private static double getPseudoGlossRelatedness(String word1, String word2, Wiktionary wkt) {
		List<WordEntry> w1Entries = wkt.getWordEntries(word1);
		List<WordEntry> w2Entries = wkt.getWordEntries(word2);

		valid = (w1Entries.size()!=0 && w2Entries.size()!=0);
		
		double d = -10.0;
		
		Iterator<WordEntry> it = w1Entries.iterator();
		String w1String = "";
		while(it.hasNext()) {
			WordEntry we = it.next();

			List<String> ts = we.getAllRelatedWords(RelationType.SYNONYM);
			Iterator<String> it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.HYPERNYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.HYPONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.MERONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.HOLONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.TROPONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.SEE_ALSO);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}

			ts = we.getAllRelatedWords(RelationType.DESCENDANT);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.ANTONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w1String = w1String + " " + ws;
			}
		}//end: while(it)
		w1String = w1String.trim();
		
		it = w2Entries.iterator();
		String w2String = "";
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<String> ts = we.getAllRelatedWords(RelationType.SYNONYM);
			Iterator<String> it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.HYPERNYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.HYPONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.MERONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.HOLONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.TROPONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
			
			ts = we.getAllRelatedWords(RelationType.SEE_ALSO);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}

			ts = we.getAllRelatedWords(RelationType.DESCENDANT);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}

			
			ts = we.getAllRelatedWords(RelationType.ANTONYM);
			it2 = ts.iterator();
			while(it2.hasNext()) {
				String ws = it2.next();
				w2String = w2String + " " + ws;
			}
		}//end: while(it)
		w2String = w2String.trim();

		System.out.println(w1String);
		System.out.println(w2String);

		double num = 0.0;
		String [] arr1 = w1String.split(" ");
		String [] arr2 = w2String.split(" ");
		TreeSet<String> ts = new TreeSet<String>();
		for(int j=0; j<arr2.length; j++) {
			ts.add(arr2[j]);
		}
		
		for(int j=0; j<arr1.length; j++) {
			if(ts.contains(arr1[j])) {
				num = num + 1;
			}
		}
		
		double den = w1String.split(" ").length + w2String.split(" ").length;
		System.out.println(num);
		d = num / den;
		return d;
	}
	
	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 */
	private static double getGlossRelatedness(String word1, String word2, Wiktionary wkt) {
		word1 = word1.toLowerCase();
		word2 = word2.toLowerCase();
		
		List<WordEntry> w1Entries = wkt.getWordEntries(word1);
		List<WordEntry> w2Entries = wkt.getWordEntries(word2);

		valid = (w1Entries.size()!=0 && w2Entries.size()!=0);
		
		// Get Word 1 Glosses from Wiktionary
		Iterator<WordEntry> it = w1Entries.iterator();		
		LinkedList<WikiString> w1Strings = new LinkedList<WikiString>();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ts = we.getGlosses();
			
			Iterator<WikiString> it2 = ts.iterator();
			while(it2.hasNext()) {
				w1Strings.add(it2.next());
			}
		}//end: while(it)
		
		// Get Word 2 Glosses from Wiktionary
		it = w2Entries.iterator();
		LinkedList<WikiString> w2Strings = new LinkedList<WikiString>();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ts = we.getGlosses();
			
			Iterator<WikiString> it2 = ts.iterator();
			while(it2.hasNext()) {
				w2Strings.add(it2.next());
			}
		}//end: while(it)
		
		// Overlap number
		double overlap = 0.0;
		
		int word1Sense = 0;
		
		// For each word1 Gloss
		Iterator<WikiString>wit1 = w1Strings.iterator();
		while(wit1.hasNext()) {
			
			// Grab the gloss
			// Create gloss multi-set
			WikiString ws1 = wit1.next();
			TreeMap<String, Integer> ts1 = new TreeMap<String,Integer>();
			
			// Add original word to multi-set
			
			ts1.put(word1, 1);

			// Add gloss words to multi-set
			//String [] words1 = ws1.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase().split(" ");
			String [] words1 = ws1.getPlainText().toLowerCase().replaceAll("\\p{Punct}", " ").split(" ");
			for(int i=0; i<words1.length; i++) {
				if(!words1[i].equals("")) {
					int count = 1;
					if(ts1.containsKey(words1[i])) {
						count += ts1.get(words1[i]);
					}
					ts1.put(words1[i], count);
				}
			}		
			// For each word2 Gloss
			Iterator<WikiString>wit2 = w2Strings.iterator();
			while(wit2.hasNext()) {
				
				// Grab the gloss
				// Create gloss multi-set
				WikiString ws2 = wit2.next();
				TreeMap<String, Integer> ts2 = new TreeMap<String,Integer>();

				// Add original word to multi-set
				ts2.put(word2, 1);
				
				// Add gloss words to multi-set
				//String [] words2 = ws2.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase().split(" ");
				String [] words2 = ws2.getPlainText().toLowerCase().replaceAll("\\p{Punct}", " ").split(" ");
				for(int i=0; i<words2.length; i++) {
					if(!words2[i].equals("")) {
						int count = 1;
						if(ts2.containsKey(words2[i])) {
							count += ts2.get(words2[i]);
						}
						ts2.put(words2[i], count);
					}
				}
				// Temporary overlap number
				double tmp = 0.0;
				
				// Find overlap between multi-sets

				// Convert TreeMap to Set
				Set<Map.Entry<String, Integer>> set = ts2.entrySet();
				Iterator<Map.Entry<String, Integer>> setit = set.iterator();
				while(setit.hasNext()) {

					// Get Map Entry
					Map.Entry<String, Integer> me = setit.next();
					if(ts1.containsKey(me.getKey())) {
						tmp += Math.min(me.getValue(), ts1.get(me.getKey()));
					}//end: if()
				}//end: while(setit)

				System.out.println(ts1.toString());
				System.out.println(ts2.toString());
				System.out.println(tmp);
				// Set overlap to largest overlap found
				overlap = Math.max(overlap, tmp);
				
//				for(int i=0; i<words2.length; i++) {
//					if(ts1.containsKey(words2[i])) {
//						tmp = tmp + ts1.get(words2[i]);
//					}//end: if()
//				}//end: for(i)
//				
//				if(ts1.containsKey(word2.toLowerCase())) {
//					tmp = tmp + ts1.get(word2.toLowerCase());
//				}//end: if()

			}//end: while(wit2)
			
			word1Sense++;
		}//end: while(wit1)
		System.out.println(overlap + "\n");
		
		return overlap;
	}
	
	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 */
	private static double getGlossRelatednessZ(String word1, String word2, Wiktionary wkt) {
		word1 = word1.toLowerCase();
		word2 = word2.toLowerCase();
		
		List<WordEntry> w1Entries = wkt.getWordEntries(word1);
		List<WordEntry> w2Entries = wkt.getWordEntries(word2);

		valid = (w1Entries.size()!=0 && w2Entries.size()!=0);
		
		
		
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
				System.out.print(word1);
				
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

	
	private static List<TreeMap<String,Integer>> convertToMultiSet(List<WordEntry> wordEntries) {
		LinkedList<TreeMap<String,Integer>> list = new LinkedList<TreeMap<String,Integer>>();
		Iterator<WordEntry> it = wordEntries.iterator();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<Quotation> l = we.getAllQuotations();
			System.out.println(l.get(0).getSource().getPlainText());
		}
		return list;
	}
	
	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 */
	private static double getGlossRelatedness2(String word1, String word2, Wiktionary wkt) {
		List<WordEntry> w1Entries = wkt.getWordEntries(word1);
		List<WordEntry> w2Entries = wkt.getWordEntries(word2);

		valid = (w1Entries.size()!=0 && w2Entries.size()!=0);
		
		// Get Word 1 Glosses
		Iterator<WordEntry> it = w1Entries.iterator();		
		LinkedList<WikiString> w1Strings = new LinkedList<WikiString>();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ts = we.getGlosses();
			
			Iterator<WikiString> it2 = ts.iterator();
			while(it2.hasNext()) {
				w1Strings.add(it2.next());
			}
		}//end: while(it)
		
		// Get Word 2 Glosses
		it = w2Entries.iterator();
		LinkedList<WikiString> w2Strings = new LinkedList<WikiString>();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ts = we.getGlosses();
			
			Iterator<WikiString> it2 = ts.iterator();
			while(it2.hasNext()) {
				w2Strings.add(it2.next());
			}
		}//end: while(it)
		
		double num = 0.0;
		Iterator<WikiString>wit1 = w1Strings.iterator();
		while(wit1.hasNext()) {
			WikiString ws1 = wit1.next();
			//String [] words1 = ws1.getPlainText().replaceAll("\\p{Punct}", "").split(" ");
			String [] words1 = ws1.getTextIncludingWikiMarkup().replaceAll("\\p{Punct}", "").split(" ");
			TreeSet<String> ts1 = new TreeSet<String>();
			for(int i=0; i<words1.length; i++) {
				ts1.add(words1[i]);
			}
			
			Iterator<WikiString>wit2 = w2Strings.iterator();
			while(wit2.hasNext()) {
				WikiString ws2 = wit2.next();
				String [] words2 = ws2.getTextIncludingWikiMarkup().replaceAll("\\p{Punct}", "").split(" ");
				double tmp = 0.0;
				for(int i=0; i<words2.length; i++) {
					if(ts1.contains(words2[i])) {
						tmp = tmp + 1.0;
					}//end: if()
				}//end: for(i)
				num = Math.max(num, tmp);
			}//end: while(wit2)
		}//end: while(wit1)
		//System.out.println(num);
		return num;
	}
	
	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 */
	private static double getGlossRelatedness3(String word1, String word2, Wiktionary wkt) {
		List<WordEntry> w1Entries = wkt.getWordEntries(word1);
		List<WordEntry> w2Entries = wkt.getWordEntries(word2);

		valid = (w1Entries.size()!=0 && w2Entries.size()!=0);
		
		// Get Word 1 Glosses
		Iterator<WordEntry> it = w1Entries.iterator();		
		LinkedList<WikiString> w1Glosses = new LinkedList<WikiString>();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ts = we.getGlosses();
			
			Iterator<WikiString> it2 = ts.iterator();
			while(it2.hasNext()) {
				w1Glosses.add(it2.next());
			}
		}//end: while(it)
		
		// Get Word 2 Glosses
		it = w2Entries.iterator();
		LinkedList<WikiString> w2Glosses = new LinkedList<WikiString>();
		while(it.hasNext()) {
			WordEntry we = it.next();
			List<WikiString> ts = we.getGlosses();
			
			Iterator<WikiString> it2 = ts.iterator();
			while(it2.hasNext()) {
				w2Glosses.add(it2.next());
			}
		}//end: while(it)
		
		// Get all the words in the gloss
		Iterator<WikiString>wit1 = w1Glosses.iterator();
		String fullGloss1  = "";
		while(wit1.hasNext()) {
			WikiString ws1 = wit1.next();
			fullGloss1 = fullGloss1 + " " + ws1.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase();
		}
		fullGloss1 = fullGloss1.trim();
		String [] fullGloss1Words = fullGloss1.split(" ");
		
		TreeMap<String, Integer> tm1 = new TreeMap<String,Integer>();
		for(int i=0; i<fullGloss1Words.length; i++) {
			int count = 1;
			if(tm1.containsKey(fullGloss1Words[i])) {
				count += tm1.get(fullGloss1Words[i]);
			}
			tm1.put(fullGloss1Words[i], count);
		}//end: for(i)
		
		// Get all the words in the gloss
		Iterator<WikiString>wit2 = w1Glosses.iterator();
		String fullGloss2  = "";
		while(wit2.hasNext()) {
			WikiString ws2 = wit2.next();
			fullGloss2 = fullGloss2 + " " + ws2.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase();
		}
		fullGloss2 = fullGloss2.trim();
		String [] fullGloss2Words = fullGloss2.split(" ");

		TreeMap<String, Integer> tm2 = new TreeMap<String,Integer>();
		for(int i=0; i<fullGloss2Words.length; i++) {
			int count = 1;
			if(tm2.containsKey(fullGloss2Words[i])) {
				count += tm2.get(fullGloss2Words[i]);
			}
			tm2.put(fullGloss2Words[i], count);
		}//end: for(i)

		double num = 0.0;
		Iterator<WikiString>wgit = w1Glosses.iterator();
		while(wgit.hasNext()) {
			WikiString ws1 = wgit.next();
			String [] words1 = ws1.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase().split(" ");
			double count = 0;
			for(int i=0; i<words1.length; i++) {
				if(tm2.containsKey(words1[i])) {
					count+= tm2.get(words1[i]);
				}
			}//end: for(i)
			num = Math.max(num, count);
//			TreeMap<String, Integer> ts1 = new TreeMap<String,Integer>();
//			for(int i=0; i<words1.length; i++) {
//				int count = 1;
//				if(ts1.containsKey(words1[i])) {
//					count += ts1.get(words1[i]);
//				}
//				ts1.put(words1[i], count);
//			}
//			int count = 1;
//			if(ts1.containsKey(word1)) {
//				count += ts1.get(word1);
//			}
//			ts1.put(word1, count);
//			ts1.add(word1.toLowerCase());
//			
//			while(wit2.hasNext()) {
//				WikiString ws2 = wit2.next();
//				String [] words2 = ws2.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase().split(" ");
//				double tmp = 0.0;
//				for(int i=0; i<words2.length; i++) {
//					if(ts1.containsKey(words2[i])) {
//						tmp = tmp + ts1.get(words2[i]);
//					}//end: if()
//				}//end: for(i)
//				
//				if(ts1.containsKey(word2.toLowerCase())) {
//					tmp = tmp + ts1.get(word2.toLowerCase());
//				}//end: if()
			
			//}//end: while(wit2)
		}//end: while(wit1)
		//System.out.println(num);

		wgit = w2Glosses.iterator();
		while(wgit.hasNext()) {
			WikiString ws2 = wgit.next();
			String [] words2 = ws2.getPlainText().replaceAll("\\p{Punct}", "").toLowerCase().split(" ");
			double count = 0;
			for(int i=0; i<words2.length; i++) {
				if(tm1.containsKey(words2[i])) {
					count+= tm1.get(words2[i]);
				}
			}//end: for(i)
			num = Math.max(num, count);
		}
		
		return num;
	}
	
	/**
	 * Sets the following parameters:<br>
	 * 
	 * <ul>
	 * <li>type (required): enwiki or enwiktionary</li>
	 * <li>date (required)</li>
	 * <li>source (required): M or english_language (wiktionary)</li>
	 * <li>seed (optional): name of top-level category for category graph</li>
	 * <li>countSource (optional): v or e</li>
	 * <li>algorithm (optional)</li>
	 * <li>task (required): name of the synonym task (RDWP300, TOEFL, ESL)</li>
	 * </ul>
	 * 
	 * 
	 * @param args Command-line argument
	 */
	private static void setParameters(String[] args) {
		Common.type = args[0];
		Common.date = args[1];
		Common.source = args[2];
		
		if(args.length == 7) {
			Common.seed = args[3];
			Common.countSource = args[4];
			Common.algorithm = args[5];
			Common.task = args[6];
		} else {
			Common.seed = "";
			Common.countSource = "";
			Common.algorithm = "";
			Common.task = args[3];
		}
	}
	
	/**
	 * Sets the names of:<br>
	 * 
	 * <ul>
	 * <li> {@link AliasStrings} file</li>
	 * <li> {@link AliasSFToID} file</li>
	 * <li> {@link WikiGraph} file</li>
	 * <li> Synonym Task file</li>
	 * </ul>
	 */
	private static void setFiles() {
		/* Set directory, data source */
		String taskDir = "/u/weale/data/wordpair/";
		String transitionSource = "";
		
		/* Set non-uniform transition data sources*/
		if(!Common.seed.equals(""))
			transitionSource = transitionSource + "-" + Common.seed;
		if(!Common.countSource.equals(""))
			transitionSource = transitionSource + "-" + Common.countSource;
		if(!Common.algorithm.equals("")) {
			transitionSource = transitionSource + "-" + Common.algorithm;
		}
		
		taskFile = taskDir + Common.task + ".csv";
	}
}
