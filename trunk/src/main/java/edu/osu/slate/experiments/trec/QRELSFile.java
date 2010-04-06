package edu.osu.slate.experiments.trec;

import java.io.*;
import java.util.*;

public class QRELSFile {

	private LinkedList<QREL> answers;
	
	private Iterator<QREL> it;
	
	public QRELSFile(String filename) throws IOException {
		
		answers = new LinkedList<QREL>();
		Scanner fin = new Scanner(new FileReader(filename));

		int currNum = -1;
		QREL lq = null;
		while(fin.hasNext()) {
			String line = fin.nextLine();
			String [] arr = line.split(" ");
			
			if(currNum != Integer.parseInt(arr[0])) {
				
				currNum = Integer.parseInt(arr[0]);
				
				if(lq != null) {
					answers.add(lq);
				}
				
				lq = new QREL(currNum);
			}
			
			if(arr[3].equals("1")) {
				lq.addValidDoc(arr[2]);
			}
		}

		answers.add(lq);
		it = answers.iterator();
	}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public QREL next() {
		return it.next();
	}
}
