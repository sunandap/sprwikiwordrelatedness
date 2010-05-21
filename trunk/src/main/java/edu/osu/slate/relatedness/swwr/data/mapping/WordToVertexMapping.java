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

package edu.osu.slate.relatedness.swwr.data.mapping;

import java.io.*;
import java.util.Arrays;

import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;

/**
 * Simplified lookup class for the {@link WordToVertexCount} class.
 * 
 * @author weale
 * @version 1.01
 */
public class WordToVertexMapping implements Serializable
{
 /**
  * 
  */
  private static final long serialVersionUID = 5395182204888235246L;

  private WordToVertexCount[] words;
  
  public boolean stem;
 /**
  *  
  * @param wvc
  */
  public WordToVertexMapping(WordToVertexCount[] wvc)
  {
    words = new WordToVertexCount[wvc.length];
    
    for(int i = 0; i < wvc.length; i++)
    {
      words[i] = wvc[i];
    }
  }
  
 /**
  * Constructor.
  * <p>
  * Reads the {@link WordToVertexCount} array from the given <i>.wic file</i>.
  * 
  * @param filename Input file name.
  */
  public WordToVertexMapping(String filename)
  {
    try
    {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
      
      // Read array length
      int len = in.readInt();
      
      // Create and initialize array
      words = new WordToVertexCount[len];
      for(int i = 0; i < len; i++)
      {
        words[i] = (WordToVertexCount) in.readObject();
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
  public VertexCount[] getVertexMappings(String word)
  {
    if(stem)
    {
      word = PorterStemmerTokenizerFactory.stem(word);
    }
    
    int pos = Arrays.binarySearch(words, new WordToVertexCount(word),
                                  new WordToVertexCountComparator());

    if(pos >= 0)
    { // FOUND!
      return words[pos].getVertexCounts();
    }
     
    return null;
  }//end: getWordMappings(int)
  
  public void joinMappings(WordToVertexMapping wvm)
  {
    int numToAdd = 0;
    boolean[] addMe = new boolean[wvm.words.length];
    
    for(int i = 0; i < wvm.words.length; i++)
    {
      
      int pos = Arrays.binarySearch(words, wvm.words[i], new WordToVertexCountComparator());
      
      if(pos >= 0)
      {
        words[pos].addObject(wvm.words[i]);
        addMe[i] = false;
      }
      else
      { // Not in Set
        numToAdd++;
        addMe[i] = true;
      }
    }//end: for(i)
    
    WordToVertexCount[] temp = new WordToVertexCount[words.length + numToAdd];
    System.arraycopy(words, 0, temp, 0, words.length);
    int addPos = words.length;
    for(int i = 0; i < wvm.words.length; i++)
    {
      if(addMe[i])
      {
        temp[addPos] = wvm.words[i];
        addPos++;
      }
    }//end: for(i)
    
    words = temp;
    temp = null;
    
    Arrays.sort(words, new WordToVertexCountComparator());
    
  }//end: stemMappings()
  
  /**
   * Write a {@link WordToVertexMapping} class to a file.
   * <p>
   * Writes the number of {@link WordToVertexCount} objects. Then, writes each object in the array to the file.
   * 
   * @param out {@link ObjectOutputStream} to write to.
   * @throws IOException
   */
   private void writeObject(java.io.ObjectOutputStream out) throws IOException
   {
     // Write array length
     out.writeInt(words.length);
     
     // Write array of WordToVertexCount objects
     for(int i = 0; i < words.length; i++)
     {
       out.writeObject(words[i]);
     }
   }//end: writeObject(ObjectOutputStream)
   
   /**
    * Reads an {@link WordToVertexMapping} class from a file.
    * <p>
    * Reads the length of {@link WordToVertexCount} objects. Then, creates and populates an appropriate array of objects.
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
      words = new WordToVertexCount[len];
      for(int i = 0; i < len; i++)
      {
        words[i] = (WordToVertexCount) in.readObject();
      }//end: for(i)
    }//end: readObject(ObjectInputStream)
}
