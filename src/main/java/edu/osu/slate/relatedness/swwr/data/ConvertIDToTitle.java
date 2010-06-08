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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 
 * @author weale
 *
 */
public class ConvertIDToTitle implements Serializable
{

  /* */
  protected String[] titles;
  protected int[] ids;

  /**
   * 
   * O(log(n)) lookup time
   * 
   * @param title
   * @return
   */
  public String getTitle(int id)
  {
    int i = Arrays.binarySearch(ids, id);
    if( i>= 0 && i<ids.length)
    {
      return titles[i];
    }
    else
    {
      return null;
    }
  }//end: getTitle(int)

  public String printTitles(int id)
  {
    int i = Arrays.binarySearch(ids, id);
    if(i>=0 && i<ids.length)
    {
      String tmp = titles[i];
      for(int j=1; j<titles.length; j++)
      {
        tmp = tmp + ", " + titles[i];
      }
      
      return tmp;
    }
    else
    {
      return null;
    }
  }

  /**
   * 
   * @param title
   * @return
   */
  public boolean isLookupID(int id)
  {
    return (getTitle(id) != null);
  }//end: isLookupID(int)

  /**
   * 
   * @return
   */
  public int getNumIDs()
  {
    return titles.length;
  }//end: getNumIDs()

//  /**
//   * 
//   * @param filename
//   */
//  public ConvertIDToTitle(String filename) {
//    try {
//      ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
//      TitleID[] titleID = (TitleID[]) fileIn.readObject();
//      fileIn.close();
//      IDComparator icom = new IDComparator();
//      Arrays.sort(titleID, icom);
//
//      ids = new int[titleID[titleID.length-1].getID()+1];
//      titles = new String[ids.length][];
//
//      for(int i=0; i<titles.length; i++) {
//        titles[i] = null;
//        ids[i] = i;
//      }
//
//      for(int i=0; i<titleID.length; i++) {
//        int id = titleID[i].getID();
//
//        if(titles[id] == null) {
//          titles[id] = new String[1];
//        } else {
//          String[] tmp = new String[titles[id].length+1];
//          for(int j=1; j<tmp.length;j++) {
//            tmp[j] = titles[id][j-1];
//          }
//          titles[id] = tmp;
//        }
//
//        titles[id][0] = titleID[i].getTitle();
//        Arrays.sort(titles[id]);
//      }
//      titleID = null;
//    }
//    catch (ClassNotFoundException e)
//    {
//      System.err.println("Problem converting to an integer array: " + filename);
//      e.printStackTrace();
//    }
//    catch (FileNotFoundException e)
//    {
//      System.err.println("File not found: " + filename);
//      e.printStackTrace();
//    }
//    catch (IOException e)
//    {
//      System.err.println("Problem reading from file: " + filename);
//      e.printStackTrace();
//    }
//  }

  /**
   * Turns an array of {@link TitleID} objects into corresponding
   * parallel arrays of Strings and ints.
   * 
   * @param titleID Array of {@link TitleID} objects.
   */
  public ConvertIDToTitle(TitleID[] titleID)
  {
    Arrays.sort(titleID, new TIDIDComparator());

    titles = new String[titleID.length];
    ids = new int[titleID.length];

    for(int i = 0; i < titles.length; i++)
    {
      titles[i] = titleID[i].getTitle();
      ids[i] = titleID[i].getID();
    }//end: for(i)
  }//end: createArrays(TitleID[])
  
  /**
   * Writes a {@link ConvertIDToTitle} object to a file.
   * 
   * @param out Output file stream.
   * @throws IOException
   */
   private void writeObject(ObjectOutputStream out) throws IOException
   {
     out.writeObject(titles);
     out.writeObject(ids);
   }//end: writeObject(ObjectOutputStream)

  /**
   * Reads a {@link ConvertIDToTitle} object from a file.
   * 
   * @param in Input file stream.
   * @throws IOException
   * @throws ClassNotFoundException
   */
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
     titles = (String[]) in.readObject();
     ids = (int[]) in.readObject();
   }//end: readObject(ObjectInputStream)
}
