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

package edu.osu.slate.relatedness.swwr.data.mapping.algorithm;

import java.io.*;
import java.util.Arrays;

import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;

import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCountComparator;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;

/**
 * Simplified lookup class for the {@link TermToVertexCount} class.
 * 
 * @author weale
 * @version 1.01
 */
public class TrimmedTwoStepTitleLinkMapping implements Serializable
{
  private static final long serialVersionUID = 5395182204888235246L;

  /* Array of lookup terms */
  private TermToVertexCount[] terms;
  
  public boolean stem;
 /**
  *  
  * @param wvc
  */
  public TrimmedTwoStepTitleLinkMapping(TermToVertexCount[] wvc)
  {
    terms = new TermToVertexCount[wvc.length];
    
    for(int i = 0; i < wvc.length; i++)
    {
      terms[i] = wvc[i];
    }
  }
  
 /**
  * Constructor.
  * <p>
  * Reads the {@link TermToVertexCount} array from the given <i>.wic file</i>.
  * 
  * @param filename Input file name.
  */
  public TrimmedTwoStepTitleLinkMapping(String filename)
  {
    try
    {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
      
      // Read array length
      int len = in.readInt();
      
      // Create and initialize array
      terms = new TermToVertexCount[len];
      for(int i = 0; i < len; i++)
      {
        terms[i] = (TermToVertexCount) in.readObject();
      }//end: for(i)
      
      in.close();
    }//end: try{}
    catch(IOException e)
    {
      System.err.println("Problem reading from file: " + filename);
      e.printStackTrace();
      System.exit(1);
    }
    catch (ClassNotFoundException e)
    {
      System.err.println("Problem with class conversion from file: " + filename);
      e.printStackTrace();
      System.exit(1);
    }
  }//end: WordToIDMapping()
  
 /**
  * Gets the IDs mapped to a given word.
  * <p>
  * Returns null if the word is not found in the mapping function.
  *  
  * @param word Word to be mapped.
  * @return An array of {@link VertexCount} objects.
  */
  public VertexCount[] getVertexMappings(String term)
  {
    if(stem)
    {
      term = PorterStemmerTokenizerFactory.stem(term);
    }
    
    int pos = Arrays.binarySearch(terms, new TermToVertexCount(term),
                                  new TermToVertexCountComparator());

    if(pos >= 0)
    { // FOUND!
      return terms[pos].getVertexCounts();
    }
     
    return null;
  }//end: getWordMappings(int)
  
 /**
  *  
  * @param wvm
  */
  public void joinMappings(TrimmedTwoStepTitleLinkMapping wvm)
  {
    int numToAdd = 0;
    boolean[] addMe = new boolean[wvm.terms.length];
    
    for(int i = 0; i < wvm.terms.length; i++)
    {
      
      int pos = Arrays.binarySearch(terms, wvm.terms[i], new TermToVertexCountComparator());
      
      if(pos >= 0)
      {
        terms[pos].addObject(wvm.terms[i]);
        addMe[i] = false;
      }
      else
      { // Not in Set
        numToAdd++;
        addMe[i] = true;
      }
    }//end: for(i)
    
    TermToVertexCount[] temp = new TermToVertexCount[terms.length + numToAdd];
    System.arraycopy(terms, 0, temp, 0, terms.length);
    int addPos = terms.length;
    for(int i = 0; i < wvm.terms.length; i++)
    {
      if(addMe[i])
      {
        temp[addPos] = wvm.terms[i];
        addPos++;
      }
    }//end: for(i)
    
    terms = temp;
    temp = null;
    
    Arrays.sort(terms, new TermToVertexCountComparator());
    
  }//end: stemMappings()
  
  /**
   * Write a {@link TrimmedTwoStepTitleLinkMapping} class to a file.
   * <p>
   * Writes the number of {@link TermToVertexCount} objects. Then, writes each object in the array to the file.
   * 
   * @param out {@link ObjectOutputStream} to write to.
   * @throws IOException
   */
   private void writeObject(java.io.ObjectOutputStream out) throws IOException
   {
     // Write array length
     out.writeInt(terms.length);
     
     // Write array of WordToVertexCount objects
     for(int i = 0; i < terms.length; i++)
     {
       out.writeObject(terms[i]);
     }
   }//end: writeObject(ObjectOutputStream)
   
   /**
    * Reads an {@link TrimmedTwoStepTitleLinkMapping} class from a file.
    * <p>
    * Reads the length of {@link TermToVertexCount} objects. Then, creates and populates an appropriate array of objects.
    * 
    * @param in {@link ObjectInputStream} to read from.
    * @throws IOException
    * @throws ClassNotFoundException
    */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
    {
      // Read array length
      int len = in.readInt();
      
      // Create and populate array
      terms = new TermToVertexCount[len];
      for(int i = 0; i < len; i++)
      {
        terms[i] = (TermToVertexCount) in.readObject();
      }//end: for(i)
    }//end: readObject(ObjectInputStream)
}
