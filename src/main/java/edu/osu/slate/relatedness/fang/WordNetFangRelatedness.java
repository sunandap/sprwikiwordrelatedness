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

package edu.osu.slate.relatedness.fang;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.osu.slate.relatedness.RelatednessTerm;
import edu.osu.slate.relatedness.WordRelatedness;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * Implements a WordNet-based relatedness metric using the gloss definitions of given words.
 * 
 * Described in <i>A Re-examination of Query Expansion Using Lexical Resources</i>, Hui Fang 2008
 * 
 * @author weale
 * @version 0.5
 *
 */
public class WordNetFangRelatedness implements WordRelatedness {

	/**
	 * WordNet Dictionary
	 */
	private static Dictionary dict;
	
	/**
	 * Constructor.
	 * 
	 * Takes the location of the WordNet configuration file "file_properties.xml" as input.
	 * 
	 * @param config The location of the WordNet configuration file.
	 *
	 */
	public WordNetFangRelatedness(String config) {
	  try {
        FileInputStream fis = new FileInputStream(config);
        JWNL.initialize(fis);
        dict = Dictionary.getInstance();
	  } catch(FileNotFoundException fnf) {
	    System.out.println(config + " was not found.");
	    System.exit(1);
	  } catch(JWNLException e) {
	    e.printStackTrace();
	    System.exit(1);
	  }
  }//end: WordNetFangRelatedness
	
 /**
  * Determines relatedness via the gloss overlap divided by the minimum length
  */
  public double getRelatedness(String w1, String w2) {
    List<String> word1Glosses = getGlosses(w1);
    List<String> word2Glosses = getGlosses(w2);
    
    TreeMap<String,Integer> tm1 = new TreeMap<String,Integer>();
    TreeMap<String,Integer> tm2 = new TreeMap<String,Integer>();
    
    int tm1Length = 0;
    int tm2Length = 0;
    
    for(int i=0; word1Glosses!=null && i<word1Glosses.size(); i++) {
      String[] arr = word1Glosses.get(i).split(" ");
      for(int j = 0; j < arr.length; j++) {
        int count = 1;
        if(tm1.containsKey(arr[j])) {
          count += tm1.get(arr[j]);
        }
        tm1.put(arr[j], count);
        tm1Length++;
      }
    }
		
    for(int i=0; word2Glosses!=null && i<word2Glosses.size(); i++) {
      String[] arr = word2Glosses.get(i).split(" ");
      for(int j = 0; j < arr.length; j++) {
        int count = 1;
        if(tm2.containsKey(arr[j])) {
          count += tm2.get(arr[j]);
        }
        tm2.put(arr[j], count);
        tm2Length++;
      }
    }
    
    // Get the overlap
    double overlap = 0.0;
    Set<Map.Entry<String,Integer>> set = tm1.entrySet();
    Iterator<Map.Entry<String,Integer>> it = set.iterator();
    while(it.hasNext()) {
      Map.Entry<String, Integer> me = it.next();
      if(tm2.containsKey(me.getKey())) {
        overlap += Math.min(tm2.get(me.getKey()), me.getValue());
      }
    }
    
    return ( overlap / Math.min(tm1Length, tm2Length) );
  }

 /**
  * 
  */
  public RelatednessTerm[] getRelatedness(String w) {
    // TODO Auto-generated method stub
    return null;
  }

 /**
  * 
  * @param s
  * @return
  * @throws JWNLException 
  */
  private List<String> getGlosses(String s) {
    
    List<String> glossList = getPOSGlosses(s, POS.ADJECTIVE);
    glossList.addAll(getPOSGlosses(s, POS.ADVERB));
    glossList.addAll(getPOSGlosses(s, POS.NOUN));
    glossList.addAll(getPOSGlosses(s, POS.VERB));
    return glossList;
    
  }
	
 /**
  * 
  * @param s
  * @param pos
  * @return
  * @throws JWNLException 
  */
  private List<String> getPOSGlosses(String s, POS pos) {

    LinkedList<String> glossList = new LinkedList<String>();
    IndexWord idxWord;
    try {
      idxWord = dict.getIndexWord(pos, s);
      if(idxWord != null) {

        Synset[] l = idxWord.getSenses();
        int i=0;
        while(i<l.length) {
          Synset iwid = l[i];
          glossList.add(iwid.getGloss().replaceAll("\\p{Punct}", "").toLowerCase());    
          i++;
        }//end: while(i)
      }
    } catch (JWNLException e) {
      e.printStackTrace();
    }
    
    return glossList;		
  }//end: getGlosses
	
 /**
  * 
  * @param args
  * @throws JWNLException 
  * @throws FileNotFoundException 
  */
  public static void main(String [] args) throws FileNotFoundException, JWNLException {
    WordNetFangRelatedness fr = new WordNetFangRelatedness("/u/weale/opt/jwnl14-rc2/config/file_properties.xml");
    System.out.println(fr.getRelatedness("gem", "jewel"));
  }//end: main()
	
}//end: WordNetFangRelatedness
