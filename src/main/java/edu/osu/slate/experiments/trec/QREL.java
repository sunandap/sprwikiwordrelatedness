package edu.osu.slate.experiments.trec;

import java.util.*;

public class QREL {

	private LinkedList<String> validDocs;
	private int queryNum;
	
	public QREL(int num) {
		validDocs = new LinkedList<String>();
		queryNum = num;
	}
	
	public void addValidDoc(String s) {
		validDocs.add(s);
	}
	
	public LinkedList<String> getValidDocs() {
		return validDocs;
	}
	
	public int getQueryNum() {
		return queryNum;
	}
}
