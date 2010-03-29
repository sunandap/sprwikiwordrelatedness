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

import java.util.*;
import java.io.*;

public class PointCompare2 {

	private static boolean verbose = true;
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String [] args) throws IOException {
		if(verbose) {
			System.out.println("Opening Alias File");
		}
		//AliasStrings as = new AliasStrings("/scratch/weale/data/binary/enwiki/20080103/enwiki-20080103-M.sim.raf");
		AliasStrings as = new AliasStrings("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.raf");
		
		if(verbose) {
			System.out.println("Opening Alias -> ID File");
		}
		//AliasSFToID sf2ID = new AliasSFToID("/scratch/weale/data/binary/enwiki/20080103/enwiki-20080103-M.sim.alf");
		AliasSFToID sf2ID = new AliasSFToID("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.alf");		

		//ConvertIDToTitle3 ctt = new ConvertIDToTitle3("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.tid");
		
		for(int val=0; val<6; val++) {
		if(verbose) {
			System.out.println("Opening Wiki Graph");
		}
		LookupTable lt = new LookupTable("/scratch/weale/data/binary/RDWP.sim."+val+".tmp");
		//LookupTable lt = new LookupTable("/scratch/weale/data/binary/TOEFL.sim.tmp");
		//LookupTable lt = new LookupTable("/scratch/weale/data/binary/ESL.sim.tmp");
		
//		WikiGraph wgp = new WikiGraph("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.wgp");
		//WikiInvGraph iwgp = new WikiInvGraph("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.iwgp");
//		if(verbose) {
//			System.out.println("Opening GreenMeasure");
//		}
//		GreenMeasure green = new GreenMeasure(wgp);
		//NGDVector ngd = new NGDVector(iwgp);
		
		Scanner s = new Scanner(new FileReader("/u/weale/data/wordpair/RDWP300.txt"));
		//Scanner s = new Scanner(new FileReader("/u/weale/data/wordpair/TOEFL.txt"));
		//Scanner s = new Scanner(new FileReader("/u/weale/data/wordpair/ESL.txt"));
		
		int corr=0;
		int attempted = 0;
		
		int questionNum = 1;
		
		while(s.hasNext()) {
						
			/* Get Next Question */
			String str = s.nextLine();
			
			/* Print Question */
			//System.out.println("Question " + questionNum);
			//System.out.println(str);
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
			
			int [] ids = GetIDS.getIDS(arr[0], as, sf2ID);
			
			/* Find the Surface Form ID of the given word */
/*			String targetWord = resolve(as, arr[0]);
			int id = as.getID(targetWord);	
			int [] ids = null;
			
			if(id > -1) {*/
				/* Find the potential IDs of the given word, given the SF ID */

				//ids = sf2ID.getIDs(id);
/*				ids = PointCompare2.getCandidateSFIDS(as, targetWord);
				TreeSet<Integer> ts = new TreeSet<Integer>();
				for(int i=0; i<ids.length; i++) {
					int [] ids2 = sf2ID.getIDs(ids[i]);
					for(int j=0; ids2 != null && j<ids2.length; j++) {
						ts.add(ids2[j]);
					}//end: for(j)
				}//end: for(i)
				if(ts.size() > 0) {
					ids = new int[ts.size()];
					Iterator<Integer> it = ts.iterator();
					for(int i=0; i<ids.length; i++) {
						ids[i] = it.next();
					}//end: for(i)
				} else {
					ids = null;
				}
			}*/
			
			if(ids != null && ( questionNum>(50*val) && questionNum<=50*(val+1))) {
				attempted++;
				for(int x=0; x<ids.length;x++) {

					/* Get relatedness distributions for the ID */
					double [] greenDist = lt.getDistribution(ids[x]);
					//green.getApproximateRelatednessDistribution(ids[x]);

					/* For each potential confusion item */
					for(int i=1;greenDist != null && i<=4;i++) {
						
						int [] ids2 = GetIDS.getIDS(arr[i], as, sf2ID);
						
						/* Break string */
						//String candidate = resolve(as, arr[i]);
						
						/* Get potential IDs from the surface form */
						//int[] candidateSFIDS = getCandidateSFIDS(as, candidate);

						/* For each potential ID for the surface form */
						//for(int j=0; j<candidateSFIDS.length; j++) {
							
							/* For each potential ID for the surface form */
							//int [] ids2 = sf2ID.getIDs(candidateSFIDS[j]);
							
							/* For each potential ID for the surface form */
							for(int k=0; ids2 != null && k<ids2.length; k++) {
								if(vals[i-1] < greenDist[ids2[k]]) {
									vals[i-1] = greenDist[ids2[k]];
								}
							}//end: for(k)
						//}//end: for(j)
					}//end: for(i)
				}//end: for(x)
				
				/* First item in the array is the answer */
				double ansG = vals[0];
				
				for(int i=0; i<vals.length; i++) {
					//System.out.println(arr[i+1] + "\t\t" + vals[i]);
				}
				
				/* Check it compared to the other items */
				if(ansG > vals[1] && ansG > vals[2] && ansG > vals[3]) {
					corr++;
					//System.out.println("CORRECT");
				} else {
					//System.out.println("INCORRECT");
				}
				
			} else {
				//System.err.println("Problem with word: " + arr[0]);
			}
			//System.out.println();
			
			
		}//end while(hasNext())
		
		//System.out.println("ATT  : " + attempted);
		System.out.println("GRN  : " + corr);
		}
	}//end: main
	
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
	
	public static String resolve(AliasStrings as, String w) {
		if(as.getID(w) != -1)
			return w;
		else {
			/* */
			//System.out.println(w);
			w = w.replace('-', ' ');
			
			String [] arr = w.split(" ");
			
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
			
			w = "";
			
			for(int i=0;i<arr.length;i++) {
				w = w + arr[i] + " ";
			}
			w = w.trim();
			
			if(as.getID(w) != -1) {
				//System.out.println("FIXED: " + w);
				return w;
			}
			
			//System.out.println(w);
			return w;
		}
	}

}
