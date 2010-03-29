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

package edu.osu.slate.relatedness.swwr.data;

import java.io.*;
import java.util.*;

public class GetIDS {

	private static ConvertIDToTitle ctt;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Opening Alias File");
		AliasStrings as = new AliasStrings("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.raf");
		
		System.out.println("Opening Alias -> ID File");
		AliasSFToID sf2ID = new AliasSFToID("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.alf");		

		ctt = new ConvertIDToTitle("/scratch/weale/data/binary/enwiktionary/20090203/enwiktionary-20090203-M.tid");
		
		Scanner s = new Scanner(new FileReader("/u/weale/data/wordpair/RDWP300.txt"));
		
		int questionNum = 1;
		
		while(s.hasNext()) {
			String str = s.nextLine();
			
			/* Print Question */
			System.out.println("Question " + questionNum);
			System.out.println(str);
			questionNum++;
			
			/* Split the input string */			
			String[] arr = str.split("\\|");
			for(int i=0;i<arr.length; i++) {
				arr[i] = arr[i].trim();
				System.out.println(i);
				getIDS(arr[i], as, sf2ID);
				System.out.println();
			}
			
			
			
		}
	}
	
	public static int[] getIDS(String w, AliasStrings as, AliasSFToID sf2ID) {
		String targetWord = PointCompare2.resolve(as, w);
		int[] arr = PointCompare2.getCandidateSFIDS(as, targetWord);
		
		TreeSet<Integer> ts = new TreeSet<Integer>();
		
		for(int i=0; i<arr.length; i++) {
			int [] arr2 = sf2ID.getIDs(arr[i]);
			for(int j=0; j<arr2.length; j++) {
				ts.add(arr2[j]);
			}
		}
		
		int[] ids = new int[ts.size()];
		Iterator<Integer> it = ts.iterator();
		for(int i=0; i<ids.length; i++) {
			ids[i] = it.next();
			//System.out.println(ctt.getTitle(ids[i])[0]);
		}
		
		return ids;
	}

}
