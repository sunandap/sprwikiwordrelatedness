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
import java.util.LinkedList;
import java.util.List;

import edu.osu.slate.relatedness.WordRelatedness;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * Implements the WordNet-based relatedness metric
 * 
 * Described in <i>A Re-examination of Query Expansion Using Lexical Resources</i>, Hui Fang 2008
 * 
 * @author weale
 * @version 0.1
 *
 */
public class WordNetFangRelatedness implements WordRelatedness {

	/**
	 * 
	 */
	private static Dictionary dict;
	
	/**
	 * @throws FileNotFoundException 
	 * @throws JWNLException 
	 * 
	 */
	public WordNetFangRelatedness(String config) throws FileNotFoundException, JWNLException {
    FileInputStream fis = new FileInputStream(config);
    JWNL.initialize(fis);
    dict = Dictionary.getInstance();
  }//end: WordNetFangRelatedness
	
	/**
	 * @throws  
	 * 
	 */
	public double getRelatedness(String w1, String w2) {
		List<String> word1Glosses = getGlosses(w1);
		List<String> word2Glosses = getGlosses(w2);
		
		for(int i=0; word1Glosses!=null && i<word1Glosses.size(); i++) {
			System.out.println(word1Glosses.get(i));
		}
		
		for(int i=0; word2Glosses!=null && i<word2Glosses.size(); i++) {
			System.out.println(word2Glosses.get(i));
		}
		
		return 0;
	}

	/**
	 * 
	 */
	public double[] getRelatedness(String w) {
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

//        List<WordID> l = (List<WordID>) ((Object) idxWord).getWordIDs();
//        int i=0;
//        while(i<l.size()) {
//          WordID iwid = l.get(i);
//          Word word = dict.getWord(iwid);
//          glossList.add(word.getSynset().getGloss().replaceAll("\\p{Punct}", "").toLowerCase());    
//          i++;
//        }//end: while(i)
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
		WordNetFangRelatedness fr = new WordNetFangRelatedness("");
		fr.getRelatedness("tree", "apple");
	}//end: main()
	
}//end: WordNetFangRelatedness
