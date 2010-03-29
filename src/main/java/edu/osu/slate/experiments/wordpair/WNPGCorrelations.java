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

import edu.osu.slate.experiments.Pearson;
import edu.osu.slate.experiments.Spearman;
import edu.osu.slate.relatedness.zesch.wordnet.WordNetPseudoGlossRelatedness;

/**
 * Calculates the Pearson correlation coefficient between a given data set and the output of a given relatedness data set.
 * 
 * Uses {@link WordNetPseudoGlossRelatedness} to determine relatedness.
 * 
 * @author weale
 * @version 0.9
 */
public class WNPGCorrelations {

  /**
   * 
   */
  private static boolean verbose = true;
  
  /**
   * 
   */
  private static String taskFile;
	
 /**
  * 
  * @param args
  * @throws IOException
  * @throws ClassNotFoundException 
  * @throws JWNLException 
  */
  public static void main(String [] args) throws IOException, ClassNotFoundException {
		
    /* Set parameters */
    String taskDir = "/u/weale/data/wordpair/";

    if(args.length == 0) {
      taskFile = taskDir + "MC30.csv";
      //taskFile = taskDir + "RG65.csv";
      //taskFile = taskDir + "WS1.csv";
      //taskFile = taskDir + "WS2.csv";
      //taskFile = taskDir + "WS353.csv";
    }

    if(verbose) {
      System.out.println("Setting Synonym Task: " + taskFile);
    }
    Scanner s = new Scanner(new FileReader(taskFile));

    WordNetPseudoGlossRelatedness rr = new WordNetPseudoGlossRelatedness("/u/weale/opt/jwnl14-rc2/config/file_properties.xml");

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
      //d = rr.getRelatedness(arr[0], arr[1]);

      //if(report) {
      System.out.println(arr[2] + " " + d);
      //}
      //if(d == 0.0) {
      //	System.out.println(str);
      //}

      X.add(Double.parseDouble(arr[2]));
      Y.add(d);

      i++;
    }//end while(hasNext())

    // Calculate the relatedness correlation
    System.out.println("p: " + Spearman.GetCorrelation(X,Y));
    System.out.println("r: " + Pearson.GetCorrelation(X,Y));
  }//end: main()
}