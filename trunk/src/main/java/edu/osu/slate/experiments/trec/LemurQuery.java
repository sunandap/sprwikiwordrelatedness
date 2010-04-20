package edu.osu.slate.experiments.trec;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

/**
 * 
 * @author weale
 *
 */
public class LemurQuery {
	
  /* */
  private TreeMap<String,Double> tm;

  private LinkedList<String> ll;
	
  //private SimpleQueryExpansion exp;
	
  /* */
  private int queryNum;
	
 /**
  * Query number accessor.
  * 
  * @return Query number
  */
  public int getQueryNum() {
    return queryNum;
  }

  public LemurQuery copy() {
    LemurQuery lq = new LemurQuery();
		
    // Deep Copy the TreeMap
    lq.tm = new TreeMap<String,Double>();
    Set<Entry<String, Double>> map = tm.entrySet();
    Iterator<Entry<String,Double>> mapIT = map.iterator();
    while(mapIT.hasNext()) {
      Entry<String,Double> e = mapIT.next();
      lq.tm.put(e.getKey(), e.getValue());
    }
		
    // Deep Copy the LinkedList
    lq.ll = new LinkedList<String>();
    Iterator<String> llIT = ll.iterator();
    while(llIT.hasNext()) {
      ll.add(llIT.next());
    }
    
    return lq;
  }
	
  private LemurQuery() { }
	
 /**
  * 
  * @param fin
  */
  public LemurQuery(Scanner fin) {
    tm = new TreeMap<String,Double>();
    ll = new LinkedList<String>();
    //expansion = new TreeMap<String,Double>();
    //exp = new PriorityQueue<SFRel>(20);
    //words = new TreeSet<String>();
    //exp = new SimpleQueryExpansion(20);
    
    // Get the document number
    String doc = fin.nextLine();		
    doc = doc.substring(5);
    int end = doc.indexOf(">");
    doc = doc.substring(0,end);
    queryNum = Integer.parseInt(doc);
			
    // Get query words
    String query = fin.nextLine();
    String [] words = query.split(" ");
    for(int i=0; i<words.length; i++) {
      words[i] = words[i].substring(0, words[i].indexOf("::")).trim();
      tm.put(new String(words[i]), 1.0);
      ll.add(words[i]);
    }
    
    // Throw away last line
    fin.nextLine();
  }
	
 /**
  * 
  * @param word
  * @param value
  */
  public boolean addRelatedWord(String word, double value) {
    if(word != null && tm.containsKey(word)) {
      return false;
    }
    else if(value > 0.0) {
      //exp.add(word, value);
      return true;
    }//end: if()
    return false;
  }
	
  public LinkedList<String> getWords() {
    return ll;
  }
	
 /**
  * 
  * @param pw
  */
  public void printQuery(PrintWriter pw) {
    // Open XML
    pw.println("<DOC " + queryNum + ">");
		
    // Print word/value pairs to file
    Set<Entry<String,Double>> tmSet = tm.entrySet();
    Iterator<Entry<String,Double>> it = tmSet.iterator();
    while(it.hasNext()) {
      Entry<String,Double> e = it.next();
      pw.print(e.getKey() + "::" + e.getValue() + " ");
    }   

    pw.println("</DOC>");
  }
}