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

package edu.osu.slate.relatedness.swwr.data.category;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import edu.osu.slate.relatedness.swwr.data.TIDIDComparator;
import edu.osu.slate.relatedness.swwr.data.TitleID;

/**
 * Converts the page ID of a Category into its title.
 * <p>
 * Opposite of {@link CategoryTitleToIDTranslation}
 * 
 * @author weale
 *
 */
public class IDToCategoryTitleTranslation implements Serializable {

  /* Serialization variable */
  private static final long serialVersionUID = 1L;

  /* One-to-many array of category names */
  protected String[][] cats;

  /* Array of category IDs */
  protected int[] ids;

 /**
  * Given an array of {@link TitleID} classes, construct a ID->Name lookup table.
  * 
  * @param tidArr TitleID array.
  */
  public IDToCategoryTitleTranslation(TitleID[] tidArr)
  {
    TitleID[] titleID = (TitleID[]) tidArr.clone();
    createArrays(titleID);
    titleID = null;
  }//end: ConvertIDToCategory(TitleID[])

 /**
  * Gets the names for a given ID
  * <p>
  * O(log(n)) lookup time
  * 
  * @param title
  * @return
  */
  public String[] getTitle(int id)
  {
    int i = Arrays.binarySearch(ids, id);
    if(i>=0 && i<ids.length)
    {
      return cats[i];
    }
    else {
      return null;
    }
  }//end: getTitle(int)

 /**
  * Gets all category names.
  * 
  * @return 2D array of category names
  */
  public String[][] getNames()
  {
    return cats;
  }//end: getNames()

 /**
  * Returns the list of names for a given ID for printing
  * 
  * @param id CategoryID
  * @return String containing all names for the category.
  */
  public String printNames(int id) {
    int i = Arrays.binarySearch(ids, id);
    if(i>=0 && i<ids.length) {
      String tmp = cats[i][0];
      for(int j=1; j<cats[i].length; j++){
        tmp = tmp + ", " + cats[i][j];
      }
      return tmp;
    } else {
      return null;
    }
  }//end: printNames(int)

 /**
  * Finds the largest category ID value
  * 
  * @return Largest category ID value
  */
  public int getMaxID()
  {
    return ids[ids.length-1];
  }//end: getMaxID()

 /**
  * Checks to see if the given uncompressed ID number is a category.
  * 
  * @param id Category ID number
  * @return True/false based on the lookup
  */
  public boolean isLookupID(int id)
  {
    return (Arrays.binarySearch(ids, id) >= 0);
  }//end: isLookupID(int)

  /**
   * Gets the number of IDs
   * 
   * @return Number of IDs
   */
  public int getNumIDs()
  {
    return cats.length;
  }//end: getNumIDs()

 /**
  * Creates the private arrays from the given {@link TitleID} array
  * 
  * @param titleID
  */
  private void createArrays(TitleID[] titleID)
  {
    //Arrays.sort(titleID, new TIDIDComparator());
    TreeSet<Integer> vect = new TreeSet<Integer>();

    for(int i = 0; i < titleID.length; i++)
    {
      vect.add(titleID[i].getID());
    }
    
    ids = new int[vect.size()];
    cats = new String[vect.size()][];
    
    Iterator<Integer> it = vect.iterator();
    for(int i = 0; i < ids.length; i++)
    {
      ids[i] = it.next();
      cats[i] = null;
    }
    
    vect = null;
    Arrays.sort(ids);

    for(int i = 0; i < titleID.length; i++)
    {
      int id = titleID[i].getID();
      String title = titleID[i].getTitle();
      
      int pos = Arrays.binarySearch(ids, id);

      if(cats[pos] == null)
      {
        cats[pos] = new String[1];
        cats[pos][0] = titleID[pos].getTitle();
      }
      else
      {
        String[] tmp = new String[cats[pos].length+1];
        System.arraycopy(cats[pos], 0, tmp, 0, cats[pos].length);
        tmp[cats[pos].length] = title;
        cats[pos] = tmp;
        Arrays.sort(cats[pos]);
      }
    }//end: for(i)
  }//end: createArrays(TitleID[])

 /**
  * Writes a {@link IDToCategoryTitleTranslation} object to a file.
  * 
  * @param out Output file stream.
  * @throws IOException
  */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(cats);
    out.writeObject(ids);
  }//end: writeObject(ObjectOutputStream)

  /**
   * Reads a {@link IDToCategoryTitleTranslation} object from a file.
   * 
   * @param in Input file stream.
   * @throws IOException
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    cats = (String[][]) in.readObject();
    ids = (int[]) in.readObject();
  }//end: readObject(ObjectInputStream)
}
