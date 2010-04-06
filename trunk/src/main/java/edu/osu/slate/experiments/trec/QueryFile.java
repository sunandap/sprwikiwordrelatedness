package edu.osu.slate.experiments.trec;
import java.io.*;
import java.util.*;

/**
 * 
 * @author weale
 *
 */
public class QueryFile {

	/* */
	private LinkedList<LemurQuery> queries;
	
	/* */
	private Iterator<LemurQuery> it;
	
	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public QueryFile(String filename) throws IOException {
		queries = new LinkedList<LemurQuery>();
		
		Scanner fin = new Scanner(new FileReader(filename));
		while(fin.hasNext()) {
			LemurQuery lq = new LemurQuery(fin);
			if(lq != null) {
				queries.add(lq);
			}
		}
		
		it = queries.iterator();
	}
	
	public QueryFile copy() {
		QueryFile q = new QueryFile();
		q.queries = new LinkedList<LemurQuery>();
		q.it = this.queries.iterator();
		while(q.it.hasNext()) {
			q.queries.add(it.next().copy());
		}//end: while(q.it)
		
		q.it = q.queries.iterator();
		
		return q;
	}//end: copy()
	
	private QueryFile() {}
	
	public boolean hasNext() {
		return it.hasNext();
	}
	
	public LemurQuery next() {
		return it.next();
	}
	
	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void printExpandedQuery(String filename) throws IOException {

		// Open output file
		PrintWriter pw = new PrintWriter(filename);
		
		// Print all queries to file
		Iterator<LemurQuery> it = queries.listIterator();
		while(it.hasNext()) {
			LemurQuery lq = it.next();
			lq.printQuery(pw);
		}
		
		// Close output file
		pw.close();
	}
	
	public static void main(String [] args) throws Exception {
		QueryFile q = new QueryFile("/u/weale/workspace/ir/data/query/trec7/trec7.simple.t.lemur.orig.xml");
		
		q.printExpandedQuery("/u/weale/workspace/ir/data/query/trec7/trec7.expanded.t.lemur.orig.xml");
	}
}
