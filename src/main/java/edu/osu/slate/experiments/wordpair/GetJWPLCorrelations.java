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

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.PageQuery;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import edu.osu.slate.experiments.Common;
import edu.osu.slate.experiments.Pearson;
import edu.osu.slate.experiments.Spearman;

/**
 * Calculates the Pearson correlation coefficient between a given data set and the output of a given relatedness data set.
 * 
 * @author weale
 *
 */
public class GetJWPLCorrelations {

	private static boolean verbose = true;
	private static String taskFile;
	private static boolean valid = true;
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws WikiApiException 
	 */
	public static void main(String [] args) throws IOException, ClassNotFoundException, WikiApiException {
		
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
			Common.task = "MC30";
			//Common.task = "RG65";
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
		
		DatabaseConfiguration dbConfig = new DatabaseConfiguration();
		dbConfig.setDatabase("enwiki_20070206");
		dbConfig.setHost("localhost");
		dbConfig.setUser("wm");
		dbConfig.setPassword("wm##");
		dbConfig.setLanguage(WikiConstants.Language.english);
		Wikipedia wiki = new Wikipedia(dbConfig);
		//System.exit(1);
		// Relatedness value vectors
		Vector<Double> X = new Vector<Double>();
		Vector<Double> Y = new Vector<Double>();
		int i=0;
		
		while(s.hasNext()) {
			String str = s.nextLine();
			String[] arr = str.split(",");

			//double d = getGlossRelatedness(arr[0], arr[1], wkt);
			double d = getGlossRelatedness(arr[0], arr[1], wiki);
			//double d = 0;
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
		System.out.println(Pearson.GetCorrelation(X,Y));
		System.out.println(Spearman.GetCorrelation(X, Y));		
	}//end: main

	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 *
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
	 *
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
	 * @throws WikiApiException 
	 */
	private static double getGlossRelatedness(String word1, String word2, Wikipedia wiki) throws WikiApiException {
		PageQuery q1 = new PageQuery();
		q1.setTitlePattern(word1);
		q1.setMinRedirects(5);
		q1.setMinTokens(100);
		q1.setOnlyArticlePages(true);
		Iterable<Page> q1PageIds = wiki.getPages(q1);
		System.out.println("Q1");
		
		PageQuery q2 = new PageQuery();
		q2.setTitlePattern(word2);
		q2.setMinRedirects(5);
		q2.setMinTokens(100);
		q2.setOnlyArticlePages(true);
		Iterable<Page> q2PageIds = wiki.getPages(q2);
		System.out.println("Q2");
		
		valid = (q1PageIds.iterator().hasNext() && q2PageIds.iterator().hasNext());
		
		double num = 0.0;
		Iterator<Page>pit1 = q1PageIds.iterator();
		Iterator<Page>pit2 = q2PageIds.iterator();
		while(pit1.hasNext()) {
			Page p1 = pit1.next();
			//String [] words1 = ws1.getPlainText().replaceAll("\\p{Punct}", "").split(" ");
			String [] words1 = p1.getPlainText().replaceAll("\\p{Punct}", "").split(" ");
			TreeSet<String> ts1 = new TreeSet<String>();
			for(int i=0; i<words1.length; i++) {
				ts1.add(words1[i]);
			}
			
			while(pit2.hasNext()) {
				Page p2 = pit2.next();
				String [] words2 = p2.getPlainText().replaceAll("\\p{Punct}", "").split(" ");
				double tmp = 0.0;
				for(int i=0; i<words2.length; i++) {
					if(ts1.contains(words2[i])) {
						tmp = tmp + 1.0;
					}//end: if()
				}//end: for(i)
				System.out.println(num);
				num = Math.max(num, tmp);
			}//end: while(wit2)
		}//end: while(wit1)
		
		return num;
	}
	
	/**
	 * 
	 * @param word1
	 * @param word2
	 * @param wkt
	 * @return
	 *
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
		Iterator<WikiString>wit2 = w2Strings.iterator();
		while(wit1.hasNext()) {
			WikiString ws1 = wit1.next();
			//String [] words1 = ws1.getPlainText().replaceAll("\\p{Punct}", "").split(" ");
			String [] words1 = ws1.getTextIncludingWikiMarkup().replaceAll("\\p{Punct}", "").split(" ");
			TreeSet<String> ts1 = new TreeSet<String>();
			for(int i=0; i<words1.length; i++) {
				ts1.add(words1[i]);
			}
			
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
