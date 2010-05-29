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
import java.util.TreeMap;

import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCount;
import edu.osu.slate.relatedness.swwr.data.mapping.TermToVertexCountComparator;
import edu.osu.slate.relatedness.swwr.data.mapping.VertexCount;

/**
 * Simplified lookup class for the {@link TermToVertexCount} class.
 * 
 * @author weale
 * @version 1.01
 */
public class TermToVertexMapping implements Serializable, MappingInterface
{
  private static final long serialVersionUID = 5395182204888235246L;

  /* Array of lookup terms */
  protected TermToVertexCount[] terms;

  protected boolean stem;

  /**
   * Constructor.
   * <p>
   * Initializes the class using the given array of {@link TermToVertexCount} objects.
   * 
   * @param tvc Array of {@link TermToVertexCount} objects.
   */
  public TermToVertexMapping(TermToVertexCount[] tvc)
  {
    terms = new TermToVertexCount[tvc.length];

    for(int i = 0; i < tvc.length; i++)
    {
      terms[i] = tvc[i];
    }
  }

  /**
   * Constructor.
   * <p>
   * Reads the {@link TermToVertexCount} array from the given <i>.tvc file</i>.
   * 
   * @param filename Input file name.
   */
  public TermToVertexMapping(String filename)
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
  * Returns the number of terms in the mapping.
  * 
  * @return Number of Mapping Terms.
  */
  public int getNumTerms()
  {
    return terms.length;
  }

 /**
  * Sets the stemming flag for the given mapping.
  *  
  * @param s Boolean value for the stem flag.
  */
  public void setStemming(boolean s)
  {
    stem = s;
  }

  /**
   * Empty Mapping
   */
  public TermToVertexCount[] getVertexMappings(String term)
  {
    return null;
  }//end: getVertexMappings(String)
  
 /**
  * Empty Mapping 
  */
  public TermToVertexCount[] getSubTermVertexMappings(String term)
  {
    return null;
  }

  /**
   * Gets the vertices mapped to a given term.
   * <p>
   * Returns null if the term is not found in the mapping function.
   *  
   * @param term Term to be mapped.
   * @return An array of {@link VertexCount} objects.
   */
  public VertexCount[] getTrimmedVertexMappings(String term, double cutoff)
  {
    if(stem)
    {
      term = PorterStemmerTokenizerFactory.stem(term);
    }

    int pos = Arrays.binarySearch(terms, new TermToVertexCount(term),
        new TermToVertexCountComparator());

    if(pos >= 0)
    { // FOUND!
      return terms[pos].getTrimmedVertexCounts(cutoff);
    }
    else
    {
      return null;
    }
  }

  /**
   * 
   * @return
   */
  public TreeMap<Integer,Integer> generateCoverageSupportHistogram()
  {
    TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();

    for(int i = 0; i < terms.length; i++)
    {
      VertexCount[] counts = terms[i].getVertexCounts();
      double totalSupport = 0;
      for(int j = 0; j < counts.length; j++)
      {
        totalSupport += counts[j].getCount();
      }//end: for(j)

      for(int j = 0; j < counts.length; j++)
      {
        double mappingSupport = (counts[j].getCount() / totalSupport) * 100;
        int count = 1;
        mappingSupport = Math.round(mappingSupport);
        int iMS = (int) mappingSupport;

        if(hist.containsKey(iMS))
        {
          count += hist.get(iMS);
        }
        hist.put(iMS, count);

      }//end: for(j)

    }//end: for(i)

    return hist;
  }

  public TreeMap<Integer,Integer> generateTrimmedCoverageSupportHistogram(double cutoff)
  {
    TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();

    for(int i = 0; i < terms.length; i++)
    {
      VertexCount[] counts = terms[i].getTrimmedVertexCounts(cutoff);
      double totalSupport = 0;
      for(int j = 0; j < counts.length; j++)
      {
        totalSupport += counts[j].getCount();
      }//end: for(j)

      for(int j = 0; j < counts.length; j++)
      {
        double mappingSupport = (counts[j].getCount() / totalSupport) * 100;
        int count = 1;
        mappingSupport = Math.round(mappingSupport);
        int iMS = (int) mappingSupport;

        if(hist.containsKey(iMS))
        {
          count += hist.get(iMS);
        }
        hist.put(iMS, count);

      }//end: for(j)

    }//end: for(i)

    return hist;
  }

  public TreeMap<Integer,Integer> generateInverseCoverageSupportHistogram()
  {
    TreeMap<Integer,Integer> vertexCounts = new TreeMap<Integer,Integer>();

    for(int i = 0; i < terms.length; i++)
    {
      VertexCount[] counts = terms[i].getVertexCounts();

      for(int j = 0; j < counts.length; j++)
      {
        int count = counts[j].getCount();

        if(vertexCounts.containsKey(counts[j].getVertex()))
        {
          count += vertexCounts.get(counts[j].getVertex());
        }
        vertexCounts.put(counts[j].getVertex(), count);

      }//end: for(j)

    }//end: for(i)

    TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();

    for(int i = 0; i < terms.length; i++)
    {
      VertexCount[] counts = terms[i].getVertexCounts();

      for(int j = 0; j < counts.length; j++)
      {
        int support = vertexCounts.get(counts[j].getVertex());
        if(support != 0)
        {
          double mappingSupport = (counts[j].getCount() / support) * 100;
          int count = 1;
          mappingSupport = Math.round(mappingSupport);
          int iMS = (int) mappingSupport;

          if(hist.containsKey(iMS))
          {
            count += hist.get(iMS);
          }
          hist.put(iMS, count);
        }        
      }//end: for(j)

    }//end: for(i)

    return hist;
  }

  /**
   * 
   * @return
   */
  public TreeMap<Integer,Integer> generateTermVertexHistogram()
  {
    TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();

    for(int i = 0; i < terms.length; i++)
    {
      VertexCount[] counts = terms[i].getVertexCounts();
      int termVertexCount = counts.length;

      if(counts.length == 0)
      {
        System.out.println(terms[i].getTerm());
      }
      else
      {
        int count = 1;
        if(hist.containsKey(termVertexCount))
        {
          count += hist.get(termVertexCount);
        }
        hist.put(termVertexCount, count);
      }
    }//end: for(i)

    return hist;
  }

  /**
   * 
   * @return
   */
  public TreeMap<Integer,Integer> generateTrimmedTermVertexHistogram(double cutoff)
  {
    TreeMap<Integer,Integer> hist = new TreeMap<Integer,Integer>();

    for(int i = 0; i < terms.length; i++)
    {
      VertexCount[] counts = terms[i].getTrimmedVertexCounts(cutoff);

      if(counts == null || counts.length == 0)
      {
        //System.out.println(terms[i].getTerm());
      }
      else
      {
        int termVertexCount = counts.length;
        int count = 1;
        if(hist.containsKey(termVertexCount))
        {
          count += hist.get(termVertexCount);
        }
        hist.put(termVertexCount, count);
      }
    }//end: for(i)

    return hist;
  }

  /**
   * Adds the content of another {@link TermToVertexMapping} object.
   *  
   * @param tvm Initialized {@link TermToVertexMapping} object.
   */
  public void joinMappings(TermToVertexMapping tvm)
  {
    int numToAdd = 0;
    boolean[] addMe = new boolean[tvm.terms.length];

    for(int i = 0; i < tvm.terms.length; i++)
    {

      int pos = Arrays.binarySearch(terms, tvm.terms[i], new TermToVertexCountComparator());

      if(pos >= 0)
      {
        terms[pos].addObject(tvm.terms[i]);
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
    for(int i = 0; i < tvm.terms.length; i++)
    {
      if(addMe[i])
      {
        temp[addPos] = tvm.terms[i];
        addPos++;
      }
    }//end: for(i)

    terms = temp;
    temp = null;

    Arrays.sort(terms, new TermToVertexCountComparator());

  }//end: stemMappings()

  /**
   * Write a {@link TermToVertexMapping} class to a file.
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
   * Reads an {@link TermToVertexMapping} class from a file.
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
  
  public static TermToVertexMapping getMapping(String wordVertexMapFile)
  {
    try
    {
      // Open Basic Mapping
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(wordVertexMapFile));
      TermToVertexMapping tmp = (TermToVertexMapping) in.readObject();
      in.close();
      
      // Convert Mapping to Algorithm
      if(Configuration.mapsource.equals("extitle"))
      {
        tmp = new ExactMapping(tmp);        
      }
      else if(Configuration.mapsource.equals("title"))
      {
        tmp = new ApproximateMapping(tmp);
      }
      else if(Configuration.mapsource.equals("link"))
      {
        tmp = new ApproximateMapping(tmp);
      }
      else if(Configuration.mapsource.equals("titlelink"))
      {
        tmp = new ApproximateMapping(tmp);
      }
      else if(Configuration.mapsource.equals("t-title"))
      {
        tmp = new TrimmedMapping(tmp);
      }
      else if(Configuration.mapsource.equals("t-link"))
      {
        tmp = new TrimmedMapping(tmp);
      }
      else if(Configuration.mapsource.equals("t-titlelink"))
      {
        tmp = new TrimmedMapping(tmp);
      }
      
      // Set stemming
      tmp.setStemming(Configuration.stemming.equals("t"));
      return tmp;
    }//end: try {}
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wordVertexMapFile);
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }//end: getMapping
}
