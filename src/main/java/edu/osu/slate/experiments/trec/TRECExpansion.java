package edu.osu.slate.experiments.trec;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import edu.osu.slate.experiments.synonym.RelID;
import edu.osu.slate.relatedness.RelatednessInterface;
import edu.osu.slate.relatedness.swwr.algorithm.SourcedPageRank;
import edu.osu.slate.relatedness.swwr.data.AliasIDToSF;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.WikiGraph;

import lemurproject.lemur.Index;
import lemurproject.lemur.IndexManager;

public class TRECExpansion {
	
	/* Verbose Flag */
	public static boolean verbose = true;
	
	/* Lemur data structures*/
	public static Index theIndex;
	public static QueryFile queryFile;
	
	/* Wiki data structures */
	public static AliasStrings as;
	public static AliasSFToID sf2ID;
	public static AliasIDToSF ID2SF;
	public static WikiGraph wgp;
	
	/* Relatedness data structure */
	public static RelatednessInterface spr;
	
	/**
	 * IR Experiment to run {TREC7, TREC8, WT2G}
	 */
	public static String experiment;

	/**
	 * Relatedness function to use {1, SPR, SPRlog}
	 */
	public static String relAlgo;

	/**
	 * Expansion determination {avg, max, multi}
	 */
	public static String expansionAlgorithm;

	/**
	 * Corpus to use for IR {TREC7, WT2G}
	 */
	public static String corpus;

	/**
	 * Set of query questions {TREC7, TREC8}
	 */
	public static String querySource;
	
	/**
	 * Source for relatedness data {enwiki, enwiktionary}
	 */
	public static String relData;

	/**
	 * Date of relatedness data
	 */
	public static String datadate;

	/**
	 * Type of relatedness data {M}
	 */
	public static String datatype;
	
	/**
	 * 
	 * @param args
	 */
	public static void setParameters(String[] args) {
		if(args.length < 6) {
			System.out.println("USAGE: SingleWordExpansionSPR exp relAlgo expAlgo relData datadate datatype");
			System.out.println();
			System.out.println("exp - trec7, trec8, wt2g");
			System.out.println("relAlgo - 1, SPR, SPRlog");
			System.out.println("expAlgo - avg, max, multi");			
			System.out.println("relData - enwiki enwiktionary");
			System.out.println("datadate - 20080103 20090203");
			System.out.println("datatype - M");			
			//System.exit(1);
		}
		
		experiment = args[0];
		querySource = "";
		corpus = "";
		
		if(experiment.equals("trec7")) {
			corpus = "trec7";
			querySource = "trec7";
		} else if(experiment.equals("trec8")) {
			corpus = "trec7";
			querySource = "trec8";
		} else if(experiment.equals("wt2g")) {
			corpus = "wt2g";
			querySource = "trec8";
		}
		
//		String relAlgo = "SPR";
//		String relAlgo = "SPRlog";
 		relAlgo = args[1];
//		String expansionAlgorithm = "max";
		expansionAlgorithm = args[2];
//		String expansionAlgorithm = "multi";
		
//		String dataset = "enwiki";
		relData = args[3];
//		String datadate = "20080103";
		datadate = args[4];
		datatype = args[5];
	}

	/**
	 * 
	 * @throws Exception
	 */
	public static void setFiles() throws Exception {
		/* Open Index Database */
		theIndex = IndexManager.openIndex("/scratch/weale/ir/indri/" + corpus + "/");
		
		/* Open Query File */
		if(verbose) {
			System.out.println("Opening Query File");
		}
		queryFile = new QueryFile("/u/weale/data/ir/topics/topics." + querySource + ".lemur");
		
		/* Set up Wiki Data Source 
		 * 
		 * Alias File: Contains the list of valid vertex names
		 * Alias->ID File: Name to Vertex ID mapping
		 * Graph File: Overall graph structure information
		 * 
		 */
		if(verbose) {
			System.out.println("Opening Alias File");
		}
		as = new AliasStrings("/scratch/weale/data/binary/" + relData + "/" + datadate + "/" + relData + "-" + datadate + "-" + datatype + ".raf");
		
		if(verbose) {
			System.out.println("Opening Alias -> ID File");
		}
		sf2ID = new AliasSFToID("/scratch/weale/data/binary/" + relData + "/" + datadate + "/"
                							+ relData + "-" + datadate + "-" + datatype + ".alf");

		if(verbose) {
			System.out.println("Opening ID -> Alias File");
		}
		ID2SF = new AliasIDToSF("/scratch/weale/data/binary/" + relData + "/" + datadate + "/"
                							+ relData + "-" + datadate + "-" + datatype + ".ialf");

		if(verbose) {
			System.out.println("Opening Graph File");
		}
		wgp = new WikiGraph("/scratch/weale/data/binary/" + relData + "/" + datadate + "/"
		      						+ relData + "-" + datadate + "-" + datatype + ".wgp");
		
		/* Initialize Relatedness Algorithm */
		if(verbose) {
			System.out.println("Opening Sourced PageRank");
		}
		spr = new SourcedPageRank(wgp);
	}//end: setFiles()
	
	
	
	/**
	 * 
	 * @param relatednessValues
	 * @param lq
	 * @return
	 */
	public static RelID[] singleWordMaxRelatedness(RelID[] relatednessValues, LemurQuery lq, String relAlgo) {
		int wordNum = 0;
		
		/* Get the query words for expansion */
		LinkedList<String> tm = lq.getWords();
		Iterator<String> it = tm.iterator();

		/* For each query word */
		String prev = "";
		while(it.hasNext()) {
			
			/* Get Current Word */
			String curr = it.next();
			
			/* if word is valid Wiki word*/
			int sfID = as.getID(curr);
			if(sfID > -1) {
				System.out.println("Expanding : " + curr);

				/* Relatedness values for the current word */
				double [] rel = new double[wgp.getNumVertices()];
				
				/* Get all IDs for current surface form
				 * 
				 * Set relatedness values based on surface forms
				 */
				int [] vertIDs = sf2ID.getIDs(sfID);
				for(int i=0; vertIDs != null && i<vertIDs.length; i++) {	
					/* Get Distribution for Vertex ID*/
					double [] rel2 = spr.getRelatedness(vertIDs[i]);
					for(int j=0; j<rel.length; j++) {
						rel[j] = Math.max(rel[j], rel2[j]);
					}
				}
				
				
				for(int y=0; y<relatednessValues.length; y++) {
					
					/* Set Relatedness Value */
					if(!relAlgo.equals("SPRlog"))
						relatednessValues[y].setRel(QESPR(rel[y]));
					else
						relatednessValues[y].setRel(QESPRLOG(rel[y]));
				}	
				
				/* Check for multi-word expressions */
				if(wordNum > 0) {
					String multiWord = prev + " " + curr;
					sfID = as.getID(multiWord);
					if(sfID > -1) {

						System.out.println("Expanding : " + multiWord);

						/* Get all IDs for current surface form */
						vertIDs = sf2ID.getIDs(sfID);
						rel = new double[wgp.getNumVertices()];
						
						for(int i=0; vertIDs != null && i<vertIDs.length; i++) {	
							/* Get Distribution for Vertex ID*/
							double [] rel2 = spr.getRelatedness(vertIDs[i]);
							for(int j=0; j<rel.length; j++) {
								rel[j] = Math.max(rel[j], rel2[j]);
							}
						}
						
						for(int y=0; y<relatednessValues.length; y++) {
							/* Set Relatedness Value */
							if(!relAlgo.equals("SPRlog"))
								relatednessValues[y].setRel(QESPR(rel[y]));
							else
								relatednessValues[y].setRel(QESPRLOG(rel[y]));

						}
					}
				}
				wordNum++;
				prev = curr;
			}//end: if(sfID)
			
		}//end: while it.hasNext()
		return relatednessValues;
	}
	
	/**
	 * 
	 * @param relatednessValues
	 * @param lq
	 * @return
	 */
	public static RelID[] singleWordAverageRelatedness(RelID[] relatednessValues, LemurQuery lq, String relAlgo) {
		int wordNum = 1;
		int expansions = 1;

		/* Get the query words for expansion */
		LinkedList<String> tm = lq.getWords();
		Iterator<String> it = tm.iterator();

		/* Array to hold raw relatedness values for the words */
		double [] rel = new double[wgp.getNumVertices()];
		
		/* For each query word */
		String prev = "";
		while(it.hasNext()) {
			
			/* Get Current Word */
			String curr = it.next();
			
			/* if word is valid Wiki word*/
			int sfID = as.getID(curr);
			if(sfID > -1) {

				System.out.println("Expanding : " + curr);
				
				/* initialize temporary relatedness array */
				double [] tmpRel = new double[wgp.getNumVertices()];
				for(int i=0; i<rel.length; i++) {
					tmpRel[i] = -100;
				}
				
				/* Get all IDs for current surface form
				 * 
				 * Set relatedness values based on surface forms
				 */
				int [] vertIDs = sf2ID.getIDs(sfID);
				for(int i=0; vertIDs != null && i<vertIDs.length; i++) {
					
					/* Get Distribution for Vertex ID*/
					double [] rel2 = spr.getRelatedness(vertIDs[i]);
					for(int j=0; j<rel.length; j++) {
						tmpRel[j] = Math.max(tmpRel[j], rel2[j]);
					}
				}
				
				expansions++;				
				for(int i=0; i<rel.length; i++) {
					rel[i] += tmpRel[i];
				}
				
				/* Check for multi-word expressions */
				if(wordNum > 1) {
					String multiWord = prev + " " + curr;
					sfID = as.getID(multiWord);
					if(sfID > -1) {

						System.out.println("Expanding : " + multiWord);

						/* Re-initialize tmpRel */
						tmpRel = new double[wgp.getNumVertices()];
						for(int i=0; i<rel.length; i++) {
							tmpRel[i] = -100;
						}
						
						/* Get all IDs for current surface form */
						vertIDs = sf2ID.getIDs(sfID);
						for(int i=0; vertIDs != null && i<vertIDs.length; i++) {	
							/* Get Distribution for Vertex ID*/
							double [] rel2 = spr.getRelatedness(vertIDs[i]);
							for(int j=0; j<rel.length; j++) {
								tmpRel[j] = Math.max(tmpRel[j], rel2[j]);
							}
						}
						
						expansions++;				
						for(int i=0; i<rel.length; i++) {
							rel[i] += tmpRel[i];
						}

					}//end: if(sfID)
				}//end: multi-word
				
				wordNum++;
				prev = curr;
			}//end: if(sfID)
		}//end: while it.hasNext()
		
		for(int y=0; y<relatednessValues.length; y++) {
			/* Set Relatedness Value */
			if(!relAlgo.equals("SPRlog"))
				relatednessValues[y].setRel(QESPR((rel[y]/expansions)));
			else
				relatednessValues[y].setRel(QESPRLOG((rel[y]/expansions)));
		}	
		
		return relatednessValues;
	}
	
	/**
	 * 
	 * @param relatednessValues
	 * @param lq
	 * @return
	 */
	public static RelID[] multiWordRelatedness(RelID[] relatednessValues, LemurQuery lq, String relAlgo) {
		int wordNum = 1;

		/* Get the query words for expansion */
		LinkedList<String> tm = lq.getWords();
		Iterator<String> it = tm.iterator();

		LinkedList<Integer> vertices = new LinkedList<Integer>();
		
		/* Array to hold raw relatedness values for the words */
		double [] rel;
		
		/* For each query word */
		String prev = "";
		while(it.hasNext()) {
			
			/* Get Current Word */
			String curr = it.next();
			
			/* if word is valid Wiki word*/
			int sfID = as.getID(curr);
			if(sfID > -1) {

				System.out.println("Expanding : " + curr);
				
				/* Get all IDs for current surface form
				 * 
				 * Set relatedness values based on surface forms
				 */
				int [] vertIDs = sf2ID.getIDs(sfID);
				for(int i=0; vertIDs != null && i<vertIDs.length; i++) {
					
					/* Get Distribution for Vertex ID*/
					vertices.add(vertIDs[i]);
				}
				
				/* Check for multi-word expressions */
				if(wordNum > 1) {
					String multiWord = prev + " " + curr;
					sfID = as.getID(multiWord);
					if(sfID > -1) {

						System.out.println("Expanding : " + multiWord);
						
						/* Get all IDs for current surface form */
						vertIDs = sf2ID.getIDs(sfID);
						for(int i=0; vertIDs != null && i<vertIDs.length; i++) {	
							vertices.add(vertIDs[i]);
						}
					}//end: if(sfID)
				}//end: multi-word
				
				wordNum++;
				prev = curr;
			}//end: if(sfID)
		}//end: while it.hasNext()
		
		int[] from = new int[vertices.size()];
		int i=0;
		Iterator<Integer> it2 = vertices.iterator();
		while(it2.hasNext()) {
			from[i] = it2.next();
			i++;
		}
		rel = spr.getRelatedness(from);
		
		for(int y=0; y<relatednessValues.length; y++) {
			/* Set Relatedness Value */
			if(!relAlgo.equals("SPRlog"))
				relatednessValues[y].setRel(QESPR(rel[y]));
			else
				relatednessValues[y].setRel(QESPRLOG(rel[y]));
		}	
		
		return relatednessValues;
	}
	
	/**
	 * 
	 * @param relatednessValues
	 * @param lq
	 * @return
	 */
	public static RelID[] advmultiWordRelatedness(RelID[] relatednessValues, LemurQuery lq, String relAlgo, int ratio) {
		int wordNum = 1;

		/* Get the query words for expansion */
		LinkedList<String> tm = lq.getWords();
		Iterator<String> it = tm.iterator();

		TreeMap<Integer,Integer> vertices = new TreeMap<Integer,Integer>();
		
		/* Array to hold raw relatedness values for the words */
		double [] rel;
		
		/* For each query word */
		String prev = "";
		while(it.hasNext()) {
			
			/* Get Current Word */
			String curr = it.next();
			
			/* if word is valid Wiki word*/
			int sfID = as.getID(curr);
			if(sfID > -1) {

				//System.out.println("Expanding : " + curr);
				
				/* Get all IDs for current surface form
				 * 
				 * Set relatedness values based on surface forms
				 */
				int [] vertIDs = sf2ID.getIDs(sfID);
				for(int i=0; vertIDs != null && i<vertIDs.length; i++) {
					
					/* Get Distribution for Vertex ID*/
					vertices.put(vertIDs[i], 1);
				}
				
				/* Check for multi-word expressions */
				if(wordNum > 1) {
					String multiWord = prev + " " + curr;
					sfID = as.getID(multiWord);
					if(sfID > -1) {

						//System.out.println("Expanding : " + multiWord);
						
						/* Get all IDs for current surface form */
						vertIDs = sf2ID.getIDs(sfID);
						for(int i=0; vertIDs != null && i<vertIDs.length; i++) {	
							vertices.put(vertIDs[i], ratio);
						}
					}//end: if(sfID)
				}//end: multi-word
				
				wordNum++;
				prev = curr;
			}//end: if(sfID)
		}//end: while it.hasNext()
		
		int[] from = new int[vertices.size()];
		float[] vals = new float[vertices.size()];
		int i=0;
		float sum = (float) 0.0;
		Set<Entry<Integer,Integer>> set = vertices.entrySet();
		Iterator<Entry<Integer,Integer>> it2 = set.iterator();
		while(it2.hasNext()) {
			Entry<Integer,Integer> e = it2.next();
			from[i] = e.getKey();
			vals[i] = e.getValue();
			sum+= vals[i];
			i++;
		}
		
		for(int j=0; j<vals.length; j++) {
			vals[j] = vals[j] / sum;
		}
		
		rel = spr.getRelatedness(from, vals);
		
		for(int y=0; y<relatednessValues.length; y++) {
			/* Set Relatedness Value */
			if(!relAlgo.equals("SPRlog"))
				relatednessValues[y].setRel(QESPR(rel[y]));
			else
				relatednessValues[y].setRel(QESPRLOG(rel[y]));
		}	
		
		return relatednessValues;
	}
	
	/**
	 * Returns a relatedness value based on the given SPR value.  Three options are available:
	 * 
	 * <ul>
	 *   <li><b>10 ^ -8</b> If the SPR value is tiny or less than zero.</li>
	 *   <li><b>1</b> If the SPR value is greater than one.</li>
	 *   <li><b>SPR value</b> Otherwise </li>
	 * </ul>
	 * 
	 * @param val
	 * @return
	 */
	public static double QESPR(double val) {
		if(val > 1) {
			val = 1;
		} else if(val < 0.000000001) {
			val = 0.000000001;
		}
		
		return val;
	}
	
	/**
	 * Returns the normalized log of the QESPR value based on the following:
	 * <p>
	 * (8.0 + log(SPR(val))) / 8.0
	 * <p>
	 * This returns a value in the [0,1] range to be used for relatedness.
	 * 
	 * @param val
	 * @return
	 */
	public static double QESPRLOG(double val) {
		return (8.0 + Math.log10(QESPR(val)))/ 8.0;
	}
}
