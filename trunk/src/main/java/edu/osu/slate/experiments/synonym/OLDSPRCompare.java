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

package edu.osu.slate.experiments.synonym;

import java.util.*;
import java.io.*;

import edu.osu.slate.experiments.Common;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.*;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Runs the standard PageRank-inspired relatedness metric on the given source.
 * <p>
 * Input arguments:
 * <ul>
 * <li>type (required): enwiki or enwiktionary</li>
 * <li>date (required)</li>
 * <li>source (required): M or english_language (wiktionary)</li>
 * <li>seed (optional): name of top-level category for category graph</li>
 * <li>countSource (optional): v or e</li>
 * <li>algorithm (optional)</li>
 * <li>task (required): name of the synonym task (RDWP300, TOEFL, ESL)</li>
 * </ul>
 * <p>
 * Uses the following classes:<br>
 * <ul>
 * <li> {@link AliasStrings}</li>
 * <li> {@link AliasSFToID}</li>
 * <li> {@link WikiGraph}</li>
 * </ul>
 * @author weale
 *
 */
public class OLDSPRCompare {

	private static boolean verbose = true;
	
	private static String aliasStringFile;
	private static String aliasSFIDFile;
	private static String graphFile;
	private static String taskFile;
	
	/**
	 * Main portion of the program
	 * <p>
	 * If no arguments are provided, uses defaults hard-coded by user.
	 * 
	 * @param args 0, 4 or 7 arguments accepted
	 * @throws IOException General IO errors
	 * @throws ClassNotFoundException Problems with the {@link WikiGraph} file
	 */
	public static void main(String [] args) throws IOException, ClassNotFoundException {

		/* Set parameters */
		if(args.length == 0) {
			Common.type = "enwiktionary";
			Common.date = "20090203";
			Common.source = "M";
			Common.seed = "'*Topics'";
			Common.countSource = "v";
			Common.algorithm = "";
			//task = "RDWP300";
			Common.task = "TOEFL";
			//task = "ESL";
		} else {
			setParameters(args);
		}
		
		setFiles();
		
		boolean report = true;
		
		if(verbose) {
			System.out.println("Setting Synonym Task: " + Common.task);
		}
		Scanner s = new Scanner(new FileReader(taskFile));

		if(verbose) {
			System.out.println("Opening Alias File");
		}
		AliasStrings as = new AliasStrings(aliasStringFile);
		
		if(verbose) {
			System.out.println("Opening Alias -> ID File");
		}
		AliasSFToID sf2ID = new AliasSFToID(aliasSFIDFile);		

		if(verbose) {
			System.out.println("Opening Wiki Graph");
		}
	    ObjectInputStream in = new ObjectInputStream(new FileInputStream(graphFile));
	    WikiGraph wgp = (WikiGraph) in.readObject();
	    in.close();
		SourcedPageRank ngd = new SourcedPageRank(wgp);
		
		/* Run Synonym Task */
		int corr=0;
		int questionNum = 1;
		
		while(s.hasNext()) {
						
			/* Get Next Question */
			String str = s.nextLine();
			
			/* Print Question */
			if(report) {
				System.out.print(questionNum);
			}
			questionNum++;
			
			/* Split the input string */			
			String[] arr = str.split("\\|");
			for(int i=0;i<arr.length; i++) {
				arr[i] = arr[i].trim();
			}

			/* Make placeholders for relatedness values */
			double [] vals = new double[4];
			for(int i=0; i<vals.length; i++) {
				vals[i] = -10;
			}
			
			/* Find the Surface Form ID of the given word */
			int [] ids = GetIDS.getIDS(arr[0], as, sf2ID);
			for(int x=0; x<ids.length;x++) {

				/* Get relatedness distributions for the ID */
				double [] greenDist = ngd.getRelatedness(ids[x]);

				/* For each potential confusion item */
				for(int i=1; greenDist != null && i <= vals.length; i++) {
						
					/* Break string */
					String candidate = Common.resolve(as, arr[i]);
						
					/* For each potential ID for the surface form */
					int[] candidateSFIDS = Common.getCandidateSFIDS(as, candidate);
					for(int j=0; j<candidateSFIDS.length; j++) {
							
						/* For each potential ID for the surface form */
						int [] ids2 = sf2ID.getIDs(candidateSFIDS[j]);
						for(int k=0; ids2 != null && k<ids2.length; k++) {
							vals[i-1] = Math.max(vals[i-1], greenDist[ids2[k]]);
						}//end: for(k)

					}//end: for(j)
				}//end: for(i)
			}//end: for(x)
				
			/* Check results */
			
			/* First item in the array is the answer */
			double ansG = vals[0];
			
			/* Check it compared to the other items */
			if (ansG > vals[1] && ansG > vals[2] && ansG > vals[3]) {
				corr++;
			} 
			
			if(report) {
				for(int i=0; i<vals.length; i++) {
					System.out.print(" " + vals[i]);
				}//end: for(i)
				System.out.println();
			}
		}//end while(hasNext())
		
		/* Print Results */
		System.out.println("Correct : " + corr);
	}//end: main
	
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
		String dir = "/scratch/weale/data/binary/" +Common.type+ "/" +Common.date+ "/";
		String data = Common.type + "-" + Common.date + "-" + Common.source;
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
		
		aliasStringFile = dir + data + ".raf";
		aliasSFIDFile = dir + data + ".alf";
		graphFile = dir + data + transitionSource + ".wgp";
		taskFile = taskDir + Common.task + ".txt";
	}
}
