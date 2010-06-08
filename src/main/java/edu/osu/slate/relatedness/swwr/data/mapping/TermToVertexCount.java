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

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Class to map a given word to a set of vertices.
 * <p>
 * Uses an array of {@link VertexCount} classes for the mapping.
 * 
 * @author weale
 *
 */
public class TermToVertexCount implements Serializable
{
  
  private static final long serialVersionUID = 7700956091903015001L;

 /**
  * Pairs of (vertex, count) Mappings
  */
  private VertexCount[] vertexCounts;
  
 /**
  * Term for Mapping 
  */
  private String term;
  
 /**
  * Constructor.
  *  
  * @param t Term to map to {@link VertexCount} pairs.
  */
  public TermToVertexCount(String t)
  {
    term = t;
    vertexCounts = new VertexCount[0];
  }

  /**
   * Constructor.
   *  
   * @param t Term to map to {@link VertexCount} pairs.
   */
   public TermToVertexCount(String t, VertexCount[] vc)
   {
     term = t;
     vertexCounts = vc;
   }
  
 /**
  * Gets the Term for this mapping.
  * 
  * @return Term contained in this mapping
  */
  public String getTerm()
  {
    return term;
  }
  
 /**
  * Gets the array of {@link VertexCount} pairs for the given term.
  * 
  * @return {@link VertexCount} array contained in this mapping
  */
  public VertexCount[] getVertexCounts()
  {
    return vertexCounts;
  }
  
 /**
  *  
  * @param cutoff
  * @return
  */
  public VertexCount[] getTrimmedVertexCounts(double cutoff)
  {
    double totalCounts = 0;
    for(int i = 0; i < vertexCounts.length; i++)
    {
      totalCounts += vertexCounts[i].getCount();
    }//end: for(i)
    
    if(totalCounts == 0)
    {
      //System.out.println(term);
    }
    
    LinkedList<VertexCount> list = new LinkedList<VertexCount>();
    for(int i = 0; i < vertexCounts.length; i++)
    {
      if((vertexCounts[i].getCount() / totalCounts) >= cutoff)
      {
        list.add(vertexCounts[i]);
      }
    }//end: for(i)
    
    if(list.size() > 0)
    {
      VertexCount[] vcReturn = new VertexCount[list.size()];
      Iterator<VertexCount> it = list.iterator();
      int i = 0;
      while(it.hasNext())
      {
        vcReturn[i] = it.next();
        i++;
      }//end: while(it)
      
      return vcReturn;
    }
    else
    {
      return null;
    }
  }
  
 /**
  * Adds a new vertex for the given term.
  * 
  * If the vertex is found, we increment the existing count.
  * If the vertex is not found, we extend the given {@link VertexCount} array.
  * 
  * @param id New ID for the given word
  */
  public void addVertex(int v)
  {
    int pos = Arrays.binarySearch(vertexCounts, new VertexCount(v,1), new VertexCountComparator());
    if(pos >= 0)
    {
      //IDCount found!
      int count = vertexCounts[pos].getCount();
      vertexCounts[pos].setCount(count+1);
    }
    else
    {
      //Add new IDCount
      VertexCount[] tmp = new VertexCount[vertexCounts.length + 1];
      System.arraycopy(vertexCounts, 0, tmp, 0, vertexCounts.length);
      tmp[vertexCounts.length] = new VertexCount(v, 1);
      vertexCounts = tmp;
      Arrays.sort(vertexCounts, new VertexCountComparator());
    }
  }//end: addID(int)
  
  
  /**
   * Adds a new vertex for the given term.
   * 
   * If the vertex is found, we increment the existing count.
   * If the vertex is not found, we extend the given {@link VertexCount} array.
   * 
   * @param id New ID for the given word
   */
   public void addVertex(int v, int c)
   {
     int pos = Arrays.binarySearch(vertexCounts, new VertexCount(v,1), new VertexCountComparator());
     if(pos >= 0)
     {
       //IDCount found!
       int count = vertexCounts[pos].getCount();
       vertexCounts[pos].setCount(count+c);
     }
     else
     {
       //Add new IDCount
       VertexCount[] tmp = new VertexCount[vertexCounts.length + 1];
       System.arraycopy(vertexCounts, 0, tmp, 0, vertexCounts.length);
       tmp[vertexCounts.length] = new VertexCount(v, c);
       vertexCounts = tmp;
       Arrays.sort(vertexCounts, new VertexCountComparator());
     }
   }//end: addID(int)
   
   /**
    * Adds all {@link VertexCount} for a given term to this object.
    * <p>
    * If the terms are not identical, nothing is added.
    * 
    * @param tvc {@link TermToVertexCount} object.
    */
   public void addObject(TermToVertexCount tvc)
   {
     for(int i = 0; tvc.term.equals(term) && i < tvc.vertexCounts.length; i++)
     {
       int v = tvc.vertexCounts[i].getVertex();
       int c = tvc.vertexCounts[i].getCount();
       this.addVertex(v, c);
     }//end: for(i)
   }//end: addObject()
   
 /**
  * Compares two {@link TermToVertexCount} objects.
  *  
  * @param wc2 Input {@link TermToVertexCount} object.
  * @return Comparison between the two objects.
  */
  public int compareTo(TermToVertexCount wc2)
  {
    return this.term.compareTo(wc2.term);
  }
  
 /**
  * Write a {@link TermToVertexCount} class to a file.
  * <p>
  * Writes the number of {@link VertexCount} objects. Then, writes each object in the array to the file.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException
  {
    // Write String
    out.writeObject(term);
    
    // Write array of IDCounts
    out.writeInt(vertexCounts.length);
    for(int i = 0; i < vertexCounts.length; i++)
    {
      out.writeObject(vertexCounts[i]);
    }
  }
  
 /**
  * Reads a {@link TermToVertexCount} class from a file.
  * <p>
  * Reads the length of {@link VertexCount} objects. Then, creates and populates an appropriate array of objects.
  * 
  * @param in {@link ObjectInputStream} to read from.
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    // Read String
    term = (String) in.readObject();
    
    // Read array of IDCounts
    int len = in.readInt();
    vertexCounts = new VertexCount[len];
    
    for(int i = 0; i < len; i++)
    {
      vertexCounts[i] = (VertexCount) in.readObject();
    }//end: for(i)
  }//end: readObject
}
