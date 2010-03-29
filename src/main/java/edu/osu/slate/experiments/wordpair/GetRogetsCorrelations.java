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

import edu.osu.slate.experiments.Common;
import edu.osu.slate.experiments.Pearson;
import edu.osu.slate.experiments.Spearman;
import edu.osu.slate.relatedness.rogets.RogetsPLRelatedness;

/**
 * Calculates the Pearson correlation coefficient between a given data set and the output of a given relatedness data set.
 * 
 * @author weale
 *
 */
public class GetRogetsCorrelations {

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
			Common.task = "MC30";
			//Common.task = "RG65";
			//Common.task = "WS1";
			//Common.task = "WS2";
			//Common.task = "WS353";
		}
		setFiles();
		
		boolean report = true;
		
		if(verbose) {
			System.out.println("Setting Synonym Task: " + Common.task);
		}
		Scanner s = new Scanner(new FileReader(taskFile));
		
		RogetsPLRelatedness rr = new RogetsPLRelatedness("/u/weale/roget_elkb/1911");
		
		// Relatedness value vectors
		Vector<Double> X = new Vector<Double>();
		Vector<Double> Y = new Vector<Double>();
		int i=0;
		
		while(s.hasNext()) {
			String str = s.nextLine();
			String[] arr = str.split(",");
			//System.out.println(str);
			
			double d = 0.0;
			
			String[] words1 = arr[0].split(" ");
			for(int x=0; x<words1.length; x++) {
				String[] words2 = arr[1].split(" ");
				for(int y=0; y<words2.length; y++) {
					d = Math.max(d, rr.getRelatedness(words1[x], words2[y]));
				}
			}
			d = rr.getRelatedness(arr[0], arr[1]);
			
			if(report) {
				System.out.println(arr[2] + " " + d);
			}
			if(d == 0.0) {
				System.out.println(str);
			}
				
			X.add(Double.parseDouble(arr[2]));
			Y.add(d);
			
			i++;
		}//end while(hasNext())
		
		// Calculate the relatedness correlation
		System.out.println("p: " + Spearman.GetCorrelation(X,Y));
		System.out.println("r: " + Pearson.GetCorrelation(X,Y));
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
		String taskDir = "/u/weale/data/wordpair/";
		taskFile = taskDir + Common.task + ".csv";
	}
}
