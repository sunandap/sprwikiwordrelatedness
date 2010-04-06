package edu.osu.slate.experiments.trec.algorithm;

import java.io.*;
import java.util.*;

import de.tudarmstadt.ukp.wikipedia.util.stemmer.Stemmer;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;

import edu.osu.slate.experiments.trec.QueryFile;
import edu.osu.slate.experiments.trec.TRECExpansion;

import lemurproject.lemur.*;

/**
 * Expands TREC queries using a single word based on the SPR or SPRlog algorithms.
 * 
 * Outline given in the SLATE IR report.
 * 
 * @author weale
 *
 */
public class ConvertToQueryModelEvalForm {

	/* Verbose flag */
	private static boolean verbose = true;
		
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//TRECExpansion.setParameters(args);
		
		/* Open Index Database */
		Index theIndex = IndexManager.openIndex("/scratch/weale/ir/indri/" + "trec7" + "/");
		
		/* Open Query File */
		if(verbose) {
			System.out.println("Opening Query File");
		}
		Scanner scn = new Scanner(new FileReader("/u/weale/data/ir/topics/topics." + "trec7" + ".lemur"));
		
		PrintWriter queryFile = new PrintWriter("/u/weale/Desktop/trec7.xml");
		
		while(scn.hasNext()) {
		  String doc = scn.nextLine();
          String words = scn.nextLine();
          scn.nextLine();
          
          String[] arr = words.split(" ");
          
          queryFile.println(doc.substring(doc.indexOf(" ") + 1, doc.length() - 1) + 
		      " " + arr.length);
		  for(int i = 0; i < arr.length; i++) {
		    arr[i] = arr[i].replaceAll("::", " ");
		    String[] parts = arr[i].split(" ");
		    int term = theIndex.term(parts[0]);
		    double idf = theIndex.docCount();
		    idf = idf /theIndex.docCount(term);
		    idf = Math.log(idf);
		    idf = idf * theIndex.termCount(term) / theIndex.termCount();
		    queryFile.println(theIndex.term(term) + " " + idf);
		  }
		}
		
		queryFile.close();
	}//end: main()
}
