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

public class AliasStrings {
  protected String[] sfs;
  
  public int getSize() {
	  return sfs.length;
  }
  
  public String getSF(int id) {
	  if(id > -1 && id < sfs.length)
		  return sfs[id];
	  else
		  return null;
  }
  
  public int getID(String sf) {
	  int id = Arrays.binarySearch(sfs, sf);
	  if(id >= 0) {
		  return id;
	  } else {
		  return -1;
	  }
  }
  
  public AliasStrings(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			sfs = (String[]) fileIn.readObject();
			fileIn.close();
		} catch (ClassNotFoundException e) {
			System.err.println("Problem converting to an integer array: " + filename);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filename);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem reading from file: " + filename);
			e.printStackTrace();
		}
  }
}
