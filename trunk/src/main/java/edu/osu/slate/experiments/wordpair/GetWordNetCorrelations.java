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
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Calculates the Pearson correlation coefficient between a given data set and the output of a given relatedness data set.
 * 
 * @author weale
 *
 */
public class GetWordNetCorrelations {

	private static boolean verbose = true;
	
	private static String aliasStringFile;
	private static String aliasSFIDFile;
	private static String graphFile;
	private static String rawTaskFile;
	private static String snsTaskFile;
	private static String wpsTaskFile;
	private static String stdTaskFile;
	private static boolean controlled;
	
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
			//Common.task = "mt_some";
			//Common.task = "mt_most";
			Common.task = "mt_all";
			//Common.task = "controlled";
		} else {
			setParameters(args);
		}
		setFiles();
		
		boolean report = true;
		
		if(verbose) {
			System.out.println("Setting Synonym Task: " + Common.task);
		}
		Scanner rawScanner = new Scanner(new FileReader(rawTaskFile));
		Scanner snsScanner = new Scanner(new FileReader(snsTaskFile));
		Scanner wpsScanner = new Scanner(new FileReader(wpsTaskFile));
		Scanner stdScanner = null;

		if(controlled)
			stdScanner = new Scanner(new FileReader(stdTaskFile));

		if(verbose) {
			System.out.println("Opening Alias File");
		}
		AliasStrings as = new AliasStrings(aliasStringFile);
		
		if(verbose) {
			System.out.println("Opening Alias -> ID File");
		}
		AliasSFToID sf2ID = new AliasSFToID(aliasSFIDFile);		

		// Set up Wiki graph and relatedness algorithm
		if(verbose) {
			System.out.println("Opening Wiki Graph");
		}
		WikiGraph wgp = new WikiGraph(graphFile);
		SourcedPageRank green = new SourcedPageRank(wgp);
		
		// Relatedness value vectors
		Vector<Double> X = new Vector<Double>();
		Vector<Double> Y = new Vector<Double>();
		int i=0;
		double[] relValues = null;
		String prevWord = "";
		
		while(rawScanner.hasNext() && X.size()<500) {
			String rawVals = rawScanner.nextLine();
			String synsets = snsScanner.nextLine();
			String wordPOS = wpsScanner.nextLine();
			String word1 = wordPOS.substring(0,wordPOS.indexOf('.'));
			wordPOS = wordPOS.substring(wordPOS.indexOf(","));
			String word2 = wordPOS.substring(1,wordPOS.indexOf('.'));
			word1 = word1.replace('_', ' ');
			word2 = word2.replace('_', ' ');
			//System.out.println(word1 + "\t" + word2 + "\t|" + rawVals);
			if(controlled) {
				stdScanner.nextLine();
			}
			if(!prevWord.equals(word1)) {
				//System.out.println(word1 + "\t" + word2 + "\t|" + rawVals);
				int [] arr = sf2ID.getIDs(as.getID(word1));
				if(arr != null) {
					relValues = green.getRelatedness(arr[0]);
				} else {
					relValues = null;
				}
			}
			
			int [] arr = sf2ID.getIDs(as.getID(word2));
			if(relValues != null && arr != null) {	
				System.out.println(word1 + "\t" + word2 + "\t|" + relValues[arr[0]]);
				X.add(relValues[arr[0]]);
				
				double rawValues = 0.0;
				String[] valArray = rawVals.split(" ");
				for(int j=1; j<valArray.length; j++) {
					//System.out.println("'" + valArray[j] + "'");
					rawValues += Double.parseDouble(valArray[j]);
				}
				rawValues /= valArray.length;
				Y.add(rawValues);
			}
			prevWord = word1;
			i++;
		}//end while(hasNext())
		
		rawScanner.close();
		snsScanner.close();
		wpsScanner.close();
		
		if(controlled)
			stdScanner.close();
		
		// Calculate the relatedness correlation
		System.out.println(Pearson(X,Y));
		System.out.println(LogPearson(X,Y));
		System.out.println(i);
	}//end: main
	
	/**
	 * Calculate the Pearson correlation coefficient of X and Y
	 * 
	 * @param X original human relatedness values
	 * @param Y metric relatedness values 
	 * @return
	 */
	private static double Pearson(Vector<Double> X, Vector<Double> Y) {
		double meanX=0.0, meanY=0.0;
		for(int i=0; i<X.size(); i++) {
			meanX+=X.elementAt(i);
			meanY+=Y.elementAt(i);
		}
		meanX /= X.size();
		meanY /= Y.size();
		
		double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0;
		for(int i=0; i<X.size(); i++) {
			sumXY += ((X.elementAt(i)-meanX) * (Y.elementAt(i)-meanY));
			sumX2 += Math.pow(X.elementAt(i)-meanX, 2.0);
			sumY2 += Math.pow(Y.elementAt(i)-meanY, 2.0);
		}
		return (sumXY / (Math.sqrt(sumX2)*Math.sqrt(sumY2)));
	}//end: Pearson2(X,Y)
	
	/**
	 * Calculate the Pearson correlation coefficient of X and log(Y)
	 * 
	 * @param X original human relatedness values
	 * @param Y non-log metric relatedness values 
	 * @return
	 */
	private static double LogPearson(Vector<Double> X, Vector<Double> Y) {
		
		for(int i=0; i<Y.size(); i++) {
			//System.out.println(Math.log10(Y.elementAt(i)));
			Y.set(i, Math.log10(Y.elementAt(i)));
		}
		
		double meanX=0.0, meanY=0.0;
		for(int i=0; i<X.size(); i++) {
			meanX+=X.elementAt(i);
			meanY+=Y.elementAt(i);
		}
		meanX /= X.size();
		meanY /= Y.size();
		
		double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0;
		for(int i=0; i<X.size(); i++) {
			sumXY += ((X.elementAt(i)-meanX) * (Y.elementAt(i)-meanY));
			sumX2 += Math.pow(X.elementAt(i)-meanX, 2.0);
			sumY2 += Math.pow(Y.elementAt(i)-meanY, 2.0);
		}
		
		return (sumXY / (Math.sqrt(sumX2)*Math.sqrt(sumY2)));
	}//end: LogPearson2(X,Y)
	
	/**
	 * 
	 * @param X
	 * @param Y
	 * @return
	 */
	private static double WeightedPearson(Vector<Double> X, Vector<Double> Y) {
		
		double sumX=0.0, sumY=0.0;
		for(int i=0; i<X.size(); i++) {
			sumX+=X.elementAt(i);
			sumY+=Y.elementAt(i);
		}
		
		double wSumX = 0.0, wSumY = 0.0;
		for(int i=0; i<X.size(); i++) {
			wSumX += X.elementAt(i) * X.elementAt(i);
			wSumY += X.elementAt(i) * Y.elementAt(i);
		}
		
		double meanX = sumX / wSumX;
		double meanY = sumY / wSumX;
		
		double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0;
		for(int i=0; i<X.size(); i++) {
			sumXY += X.elementAt(i) * ((X.elementAt(i)-meanX) * (Y.elementAt(i)-meanY));
			sumX2 += X.elementAt(i) * Math.pow(X.elementAt(i)-meanX, 2.0);
			sumY2 += X.elementAt(i) * Math.pow(Y.elementAt(i)-meanY, 2.0);
		}
		
		sumXY /= wSumX;
		sumX2 /= wSumX;
		sumY2 /= wSumX;
		
		return (sumXY / (Math.sqrt(sumX2)*Math.sqrt(sumY2)));
	}
	
	/**
	 * 
	 * @param X
	 * @param Y
	 * @param alpha
	 * @return
	 */
	private static double[] absolutePositive(Vector<Double> X, Vector<Double> Y, double alpha) {
		double maxX=X.elementAt(0);
		for(int i=0; i<X.size(); i++) {
			maxX = Math.max(maxX, X.elementAt(i));
		}
		
		double minValid = maxX * alpha;
		double minYValid = 10.0;
		for(int i=0; i<X.size(); i++) {
			if(X.elementAt(i) > minValid) {
				minYValid = Math.min(minYValid, Y.elementAt(i));
			}
		}
		
		int numPositive = 0;
		int numNegative = 0;
		
		int truePositive = 0;
		int falsePositive = 0;
		int trueNegative = 0;
		int falseNegative = 0;
		
		for(int i=0; i<X.size(); i++) {
			
			
			if(X.elementAt(i) > minValid) {
				numPositive++;
				if(Y.elementAt(i) > minYValid) {
					truePositive++;
				} else {
					falseNegative++;
				}
			} else {
				numNegative++;
				if(Y.elementAt(i) > minYValid) {
					falsePositive++;
				} else {
					trueNegative++;
				}
			}
		}
		
		double truePositiveRate=truePositive / ((double) numPositive);
		double falsePositiveRate=falsePositive / ((double) numPositive);
		double acc = (truePositive+trueNegative) / ((double) X.size());
		
		double[] ret = {truePositiveRate, falsePositiveRate, acc};
		return ret;
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
		String dir = "/scratch/weale/data/binary/" +Common.type+ "/" +Common.date+ "/";
		String data = Common.type + "-" + Common.date + "-" + Common.source;
		String taskDir = "/u/weale/data/wordpair/evocation/release-0.4/";
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
		rawTaskFile = taskDir + Common.task + ".raw";
		snsTaskFile = taskDir + Common.task + ".synsets";
		wpsTaskFile = taskDir + Common.task + ".word-pos-sense";
		
		controlled = Common.task.equals("controlled");
		
		if(controlled)
			stdTaskFile = taskDir + Common.task + ".standard";
	}
}
