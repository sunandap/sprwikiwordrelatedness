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

package edu.osu.slate.relatedness.swwr.data.graph;

import java.io.*;
import java.util.Arrays;

/**
 * Converts Wiki Page IDs to graph vertices via redirect information.
 * 
 * @author weale
 * @version 1.0
 */
public class IDVertexRedirect {

 /**
  * Original Wiki Page IDs
  */
  private int[] from;
  
 /**
  * Redirected Vertex numbers
  */
  private int[] to;

 /**
  * Redirects a Wiki ID value to a vertex number.
  * <p>
  * Returns -1 if the ID value is not found.
  * 
  * @param id Wiki Page ID
  * @return Vertex number
  */
  public int redirectIDToVertex(int id)
  {
    int pos = Arrays.binarySearch(from, id);
    
    if(pos >=0)
    {
      return to[pos];
    }
    else {
      return -1;
    }
  }//end: redirectIDToVertex(int)

 /**
  * Returns redirect status of ID.
  * <p>
  * If ID is redirect, returns true<br>
  * Otherwise, returns false.
  * 
  * @param id Wiki Page ID
  * @return Redirect status of Wiki Page ID
  */
  public boolean isRedirectID(int id) {
    return (Arrays.binarySearch(from, id) >= 0);
  }
	
 /**
  * Constructor.
  * <p>
  * Given the name of the file, this constructor extracts the
  * list of Page IDs and their corresponding vertex numbers, which
  * have been determined from the redirect file parsed in {@link CreateRedirectFiles}.
  * 
  * @param filename Name of the <i>.rdr file</i>.
  */
  public IDVertexRedirect(String filename) {
    try
    {
      ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
      from = (int[]) fileIn.readObject();
      to = (int[]) fileIn.readObject();
      fileIn.close();
    }
    catch (ClassNotFoundException e) {
      System.err.println("Problem converting to an integer array: " + filename);
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      System.err.println("File not found: " + filename);
      e.printStackTrace();
    }
    catch (IOException e) {
      System.err.println("Problem reading from file: " + filename);
      e.printStackTrace();
    }
  }
}