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

package edu.osu.slate.relatedness.zesch.wordnet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import edu.osu.slate.relatedness.RelatednessTerm;
import edu.osu.slate.relatedness.WordRelatedness;

/**
 * Implements Concept Vector-based Relatedness Using WordNet
 * <p>
 * Described in <i>"Study of Semantic Relatedness of Words Using Collaboratively Constructed Semantic Resources"</i>, Torsten Zesch 2009
 * 
 * @author weale
 * @version 0.1
 *
 */
public class WordNetCVRelatedness  implements WordRelatedness {

 /**
  * WordNet dictionary
  */
  private Dictionary dict;
   
  private int synset;
  private double idf1;
  private double idf2;
  
 /**
  * 
  * 
  * @param config
  * @author weale
  */
  public WordNetCVRelatedness(String config) {
    try {
      FileInputStream fis = new FileInputStream(config);
      JWNL.initialize(fis);
      dict = Dictionary.getInstance();
    }
    catch(FileNotFoundException fnf) {
      fnf.printStackTrace();
    }
    catch (JWNLException e) {
      e.printStackTrace();
    }
  }
  
  public void addToVector(TreeMap<Integer, Double> tm, String w, POS pos) throws JWNLException {
    Iterator it = dict.getSynsetIterator(pos);
    while(it.hasNext()) {
      Synset ss = (Synset) it.next();
      String s = ss.getGloss();
      s = s.toLowerCase().replaceAll("\\p{Punct}", " ");
      String tmp = s;
      
      double count = 0.0;
      while(s.indexOf(w) != -1) {
        count++;
        s = s.substring(s.indexOf(w)+1);
      }
      
      if(count > 0)
        tm.put(synset, count / tmp.split(" ").length);
      
      synset++;
    }
  }
  
  public TreeMap<Integer, Double> getVector(String w1) throws JWNLException {
    synset = 0;
    TreeMap<Integer, Double> tm = new TreeMap<Integer, Double>();
    addToVector(tm, w1, POS.ADJECTIVE);
    addToVector(tm, w1, POS.ADVERB);
    addToVector(tm, w1, POS.NOUN);
    addToVector(tm, w1, POS.VERB);
    return tm;
  }
   
 /**
  * 
  */
  public double getRelatedness(String w1, String w2) {
    double rel = 0.0;
    try {
      TreeMap<Integer, Double> tm1 = getVector(w1);
      idf1 = Math.log(synset / tm1.size());
      TreeMap<Integer, Double> tm2 = getVector(w2);
      idf2 = Math.log(synset / tm2.size());
      
      double dot = 0.0;
      double mag1 = 0.0;
      double mag2 = 0.0;
      
      Set<Map.Entry<Integer, Double>> set = tm1.entrySet();
      Iterator<Map.Entry<Integer, Double>> it = set.iterator();
      while(it.hasNext()) {
        Map.Entry<Integer, Double> me = it.next();
        mag1 += (me.getValue() * idf1) * (me.getValue() * idf1);
        if(tm2.containsKey(me.getKey())) {
          dot += tm2.get(me.getKey()) * idf2 * me.getValue() * idf1;
        }
      }
      
      set = tm2.entrySet();
      it = set.iterator();
      while(it.hasNext()) {
        Map.Entry<Integer, Double> me = it.next();
        mag2 += (me.getValue() * idf2) * (me.getValue() * idf2);
      }
      
      mag1 = Math.sqrt(mag1);
      mag2 = Math.sqrt(mag2);
      rel = dot / (mag1 * mag2);
      
    } catch(Exception e) {
      
    }
    return rel;
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
  * @param args
  */
  public static void main(String[] args) {
    WordNetCVRelatedness fr = new WordNetCVRelatedness("/u/weale/opt/jwnl14-rc2/config/file_properties.xml");
    System.out.println(fr.getRelatedness("good", "bad"));
  }

}//end: WordNetCVRelatedness