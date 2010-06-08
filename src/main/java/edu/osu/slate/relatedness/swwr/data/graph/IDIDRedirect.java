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
 * Converts wiki Page IDs to valid IDs via redirect information.
 * 
 * @author weale
 * @version 2.0-alpha
 */
public class IDIDRedirect implements Serializable
{
  
 /**
   * 
   */
  private static final long serialVersionUID = -5054690260260316105L;

/**
  * Original Wiki Page IDs
  */
  private int[] from;
  
 /**
  * Redirected Page IDs
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
  public int redirectIDToValidID(int id)
  {
    int pos = Arrays.binarySearch(from, id);
    
    if(pos >=0)
    {
      return to[pos];
    }
    else {
      return -1;
    }
  }//end: redirectIDToValidID(int)

 /**
  * Returns redirect status of ID.
  * <p>
  * If ID is redirect, returns true<br>
  * Otherwise, returns false.
  * 
  * @param id Wiki Page ID
  * @return Redirect status of Wiki Page ID
  */
  public boolean isRedirectID(int id)
  {
    return (Arrays.binarySearch(from, id) >= 0);
  }//end: isRedirectID(int)

 /**
  * Constructor.
  * <p>
  * Given a parallel list of from and to arrays,
  * creates an object using their information.
  * 
  * @param from Array of 'from' page IDs.
  * @param to Array of 'to' vertex numbers.
  */
  public IDIDRedirect(int[] from, int[] to)
  {
    this.from = new int[from.length];
    this.to   = new int[to.length];
    
    for(int i = 0; i < to.length; i++)
    {
      this.from[i] = from[i];
      this.to[i]   = to[i];
    }//end: for(i)
  }//end: IDVertexRedirect(int[] int[])
  
 /**
  * Constructor.
  * <p>
  * Given the name of the file, this constructor extracts the
  * list of Page IDs and their corresponding vertex numbers, which
  * have been determined from the redirect file parsed in {@link CreateRedirectFiles}.
  * 
  * @param filename Name of the <i>.rdr file</i>.
  */
  public IDIDRedirect(String filename) {
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
  }//end: iDVertexRedirect(String)
  
  /**
   * Writes the object to the given {@link ObjectOutputStream}.
   * <p>
   * Writes the vertex-id integer array to the file.
   * 
   * @param out {@link ObjectOutputStream} to be written to.
   * @throws IOException
   */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(from);
    out.writeObject(to);
  }//end: writeObject(ObjectOutputStream)

  /**
   * Reads the object from the given {@link ObjectInputStream}.
   * <p>
   * Reads the vertex-id integer arrays.
   * 
   * @param in {@link ObjectInputStream} to read from.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    from = (int []) in.readObject();
    to   = (int []) in.readObject();
  }//end: readObject(ObjectInputStream)
}