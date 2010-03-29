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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.osu.slate.relatedness.WordRelatedness;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;


/**
 * Implements the Pseudo Gloss-based Relatedness Using WordNet.
 * <p>
 * Described in <i>"Study of Semantic Relatedness of Words Using Collaboratively Constructed Semantic Resources"</i>, Torsten Zesch 2009
 *
 * @author weale
 * @version 0.1
 *
 */
public class WordNetPseudoGlossRelatedness implements WordRelatedness {

  /**
   * WordNet dictionary
   */
   private Dictionary dict;

 /**
  * 
  * 
  * @param config
  * @author weale
  */
  public WordNetPseudoGlossRelatedness(String config) {
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

 /**
  * 
  */
  public double getRelatedness(String w1, String w2) {
    double val = 0.0;
    try {
      val = getPseudoWordNetRelatedness(w1, w2);
    }
    catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return val;
  }

  /**
   * 
   * @param word
   * @return
   * @throws JWNLException
   */
  private TreeSet<String> getAllSynsets(String word) throws JWNLException {
    TreeSet<String> ts = new TreeSet<String>();
    //IndexWord iw = dict.lookupIndexWord(POS.NOUN, word);
    //IndexWord[] iwa = {iw};
    IndexWordSet iws = dict.lookupAllIndexWords(word);
    IndexWord[] iwa = iws.getIndexWordArray();
    
    // Get Glosses for all seed synsets
    for(int i = 0; i < iwa.length; i++) {
      
      // Seed Synsets
      Synset[] ssa = iwa[i].getSenses();

      // Get glosses starting at each seed Synset
      for(int j = 0; j < ssa.length; j++) {
        String tmp = getGlossList(ssa[j]);
       // System.out.println(tmp);
        ts.add( tmp );
      }//end: for(j)
    }//end: for(i)
    
    return ts;
  }//end: getAllSynsets()

 /**
  * 
  * @param w1
  * @param w2
  * @return
  * @throws JWNLException 
  */
  private double getPseudoWordNetRelatedness(String w1, String w2) throws JWNLException {
    TreeSet<String> word1Glosses = getAllSynsets(w1);
    TreeSet<String> word2Glosses = getAllSynsets(w2);
		
    double overlap = 0.0;
    Iterator<String> it1 = word1Glosses.iterator();
    while(it1.hasNext()) {
      String gloss1 = it1.next();
      
      Iterator<String> it2 = word2Glosses.iterator();
      while(it2.hasNext()) {
        String gloss2 = it2.next();
        overlap = Math.max(overlap, getGlossOverlap(gloss1, gloss2));
      }//end: while(it2)
    }//end: while(it1)

    return overlap;
  }
  
  private double getGlossOverlap(String g1, String g2) {
    double overlap = 0.0;
    
    TreeMap<String,Integer> tmG1 = new TreeMap<String,Integer>();
    String[] words = g1.split(" ");
    for(int i=0; i<words.length; i++) {
      int count = 1;
      if(tmG1.containsKey(words[i])) {
        count += tmG1.get(words[i]);
      }
      tmG1.put(words[i], count);
    }//end: for(i)

    TreeMap<String,Integer> tmG2 = new TreeMap<String,Integer>();
    words = g2.split(" ");
    for(int i=0; i<words.length; i++) {
      int count = 1;
      if(tmG2.containsKey(words[i])) {
        count += tmG2.get(words[i]);
      }
      tmG2.put(words[i], count);
    }//end: for(i)
    
    Set<Map.Entry<String, Integer>> set = tmG1.entrySet();
    Iterator<Map.Entry<String, Integer>> it = set.iterator();
    while(it.hasNext()) {
      Map.Entry<String, Integer> me = it.next();
      if(tmG2.containsKey(me.getKey()) && !me.getKey().equals("")) {
        overlap += Math.min(tmG2.get(me.getKey()), me.getValue());
      }//end: if()
    }//end: while(it)

    return overlap;
  }
	
  public double[] getRelatedness(String w) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private LinkedList<Synset> getImmediateRels(Synset ss) throws JWNLException {
    LinkedList<Synset> ts = new LinkedList<Synset>();
    Pointer[] pointers = ss.getPointers(PointerType.ANTONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)
    
    pointers = ss.getPointers(PointerType.SIMILAR_TO);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)
    
    pointers = ss.getPointers(PointerType.SEE_ALSO);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)
    
    return ts;
  }
  
  private LinkedList<Synset> getReachableList(Synset ss) throws JWNLException
  {
    LinkedList<Synset> ts = new LinkedList<Synset>();

    Pointer[] pointers = ss.getPointers(PointerType.MEMBER_HOLONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)
    
    pointers = ss.getPointers(PointerType.PART_HOLONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)
    
    pointers = ss.getPointers(PointerType.HYPERNYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)

    pointers = ss.getPointers(PointerType.HYPONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)

    pointers = ss.getPointers(PointerType.MEMBER_MERONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)

    pointers = ss.getPointers(PointerType.PART_MERONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(!ts.contains(ss2))
        ts.add(ss2);
    }//end: for(i)

    return ts;
  }//end: getReachable()
  
 /**
  *
  * @param ss
  * @return
  * @throws JWNLException
  */
  public String getGlossList(Synset ss) throws JWNLException {
    String gloss = "";
    Word[] words = ss.getWords();
    for(int i=0; words != null && i<words.length; i++) {
      gloss = gloss + " " + words[i].getLemma();
    }
    
    // Radius of One
    LinkedList<Synset> ts0 = getImmediateRels(ss);
    LinkedList<Synset> ts1 = getReachableList(ss);

    // Radius of Two
    LinkedList<Synset> ts2 = new LinkedList<Synset>();
    Iterator<Synset> ssit = ts1.iterator();
    while(ssit.hasNext()) {
      ts2.add(ssit.next());
    }
    
    ssit = ts0.iterator();
    while(ssit.hasNext()) {
      ts2.add(ssit.next());
    }
    
//    Iterator<Synset> it2 = ts2.iterator();
//    while(it2.hasNext()) {
//      Synset ss3 = it2.next();
//      words = ss3.getWords();
//      for(int i=0; words != null && i<words.length; i++) {
//        gloss = gloss + " " + words[i].getLemma();
//      }
//    }
    
    Iterator<Synset> it1 = ts1.iterator();
    while(it1.hasNext()) {
      Synset ss1 = it1.next();
      LinkedList<Synset> tmp = getReachableList(ss1);
      ssit = tmp.iterator();
      while(ssit.hasNext()) {
        Synset s = ssit.next();
        ts2.add(s);
      }
    }
    
    /* Radius of Three */
    
    // Initialize Synset Set
    LinkedList<Synset> ts3 = new LinkedList<Synset>();
    ssit = ts2.iterator();
    while(ssit.hasNext()) {
      ts3.add(ssit.next());
    }
    
    Iterator<Synset> it2 = ts2.iterator();
    while(it2.hasNext()) {
      Synset ss2 = it2.next();
      LinkedList<Synset> tmp = getReachableList(ss2);
      ssit = tmp.iterator();
      while(ssit.hasNext()) {
        ts3.add(ssit.next());
      }
    }

    // Set up remainder of gloss
    Iterator<Synset> it3 = ts3.iterator();
    while(it3.hasNext()) {
      Synset ss3 = it3.next();
      words = ss3.getWords();
      for(int i=0; words != null && i<words.length; i++) {
        gloss = gloss + " " + words[i].getLemma();
      }
    }
    
    return gloss;
  }
  
  private TreeSet<Synset> getReachable(Synset ss) throws JWNLException
  {
    TreeSet<Synset> ts = new TreeSet<Synset>();
    Pointer[] pointers = ss.getPointers(PointerType.ANTONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(ss2.getClass().equals(Synset.class))
        ts.add(ss2);
    }//end: for(i)

    pointers = ss.getPointers(PointerType.MEMBER_HOLONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(ss2.getClass().equals(Synset.class))
        ts.add(ss2);
    }//end: for(i)
    
    pointers = ss.getPointers(PointerType.PART_HOLONYM);
    for(int i=0; pointers != null && i<pointers.length; i++) {
      Synset ss2 = pointers[i].getTargetSynset();
      //if(ss2.getClass().equals(Synset.class))
        ts.add(ss2);
    }//end: for(i)
    
      pointers = ss.getPointers(PointerType.HYPERNYM);
      for(int i=0; pointers != null && i<pointers.length; i++) {
        Synset ss2 = pointers[i].getTargetSynset();
        //if(ss2.getClass().equals(Synset.class))
          ts.add(ss2);
      }//end: for(i)

      pointers = ss.getPointers(PointerType.HYPONYM);
      for(int i=0; pointers != null && i<pointers.length; i++) {
        Synset ss2 = pointers[i].getTargetSynset();
        //if(ss2.getClass().equals(Synset.class))
          ts.add(ss2);
      }//end: for(i)

      pointers = ss.getPointers(PointerType.MEMBER_MERONYM);
      for(int i=0; pointers != null && i<pointers.length; i++) {
        Synset ss2 = pointers[i].getTargetSynset();
        //if(ss2.getClass().equals(Synset.class))
          ts.add(ss2);
      }//end: for(i)

      pointers = ss.getPointers(PointerType.PART_MERONYM);
      for(int i=0; pointers != null && i<pointers.length; i++) {
        Synset ss2 = pointers[i].getTargetSynset();
        //if(ss2.getClass().equals(Synset.class))
          ts.add(ss2);
      }//end: for(i)
      
      pointers = ss.getPointers(PointerType.SEE_ALSO);
      for(int i=0; pointers != null && i<pointers.length; i++) {
        Synset ss2 = pointers[i].getTargetSynset();
        //if(ss2.getClass().equals(Synset.class))
          ts.add(ss2);
      }//end: for(i)
      
      pointers = ss.getPointers(PointerType.SIMILAR_TO);
      for(int i=0; pointers != null && i<pointers.length; i++) {
        Synset ss2 = pointers[i].getTargetSynset();
        //if(ss2.getClass().equals(Synset.class))
          ts.add(ss2);
      }//end: for(i)
    return ts;
  }//end: getReachable()
  
 /**
  *
  * @param ss
  * @return
  * @throws JWNLException
  */
  public String getGloss(Synset ss) throws JWNLException {
    String gloss = "";
    Word[] words = ss.getWords();
    for(int i=0; words != null && i<words.length; i++) {
      gloss = gloss + " " + words[i].getLemma();
    }
    
    // Radius of One
    TreeSet<Synset> ts1 = getReachable(ss);

    // Radius of Two
    TreeSet<Synset> ts2 = new TreeSet<Synset>();
    Iterator<Synset> ssit = ts1.iterator();
    while(ssit.hasNext()) {
      ts2.add(ssit.next());
    }
    
    Iterator<Synset> it1 = ts1.iterator();
    while(it1.hasNext()) {
      Synset ss1 = it1.next();
      TreeSet<Synset> tmp = getReachable(ss1);
      ssit = tmp.iterator();
      while(ssit.hasNext()) {
        Synset s = ssit.next();
        //if(s.getClass().equals(Synset.class))
        ts2.add(s);
      }
    }
    
    /* Radius of Three */
    
    // Initialize Synset Set
    TreeSet<Synset> ts3 = new TreeSet<Synset>();
    ssit = ts2.iterator();
    while(ssit.hasNext()) {
      ts3.add(ssit.next());
    }
    
    Iterator<Synset> it2 = ts2.iterator();
    while(it2.hasNext()) {
      Synset ss2 = it2.next();
      TreeSet<Synset> tmp = getReachable(ss2);
      ssit = tmp.iterator();
      while(ssit.hasNext()) {
        Synset s = ssit.next();
        //if(s.getClass().equals(Synset.class))
          ts2.add(s);
      }
    }

    // Set up remainder of gloss
    Iterator<Synset> it3 = ts3.iterator();
    while(it3.hasNext()) {
      Synset ss3 = it3.next();
      words = ss3.getWords();
      for(int i=0; words != null && i<words.length; i++) {
        gloss = gloss + " " + words[i].getLemma();
      }
    }
    
	return gloss;
  }

  public static void main(String [] args) throws FileNotFoundException, JWNLException {
    WordNetPseudoGlossRelatedness fr = new WordNetPseudoGlossRelatedness("/u/weale/opt/jwnl14-rc2/config/file_properties.xml");
    System.out.println(fr.getRelatedness("good", "bad"));
  }//end: main()
  
}//end: WordNetPseudoGlossRelatedness