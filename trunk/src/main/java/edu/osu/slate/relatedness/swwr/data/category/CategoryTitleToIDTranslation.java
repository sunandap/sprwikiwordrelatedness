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

import edu.osu.slate.relatedness.swwr.data.TIDTitleComparator;
import edu.osu.slate.relatedness.swwr.data.TitleID;

/**
 * Converts the name of a category into its uncompressed ID.
 * <p>
 * Opposite of {@link IDToCategoryTitleTranslation}.
 * 
 * @author weale
 *
 */
public class CategoryTitleToIDTranslation implements Serializable {

  /* Serialization variable */
  private static final long serialVersionUID = 1L;

  /* Array of category names */
  protected String[] cats;

  /* Array of category IDs */
  protected int[] ids;

 /**
  * Given an array of {@link TitleID} classes, construct a Name->ID lookup table.
  * 
  * @param tidArr Array of {@link TitleID} objects.
  */
  public CategoryTitleToIDTranslation(TitleID[] tidArr)
  {
    TitleID[] titleID = (TitleID[]) tidArr.clone();
    TIDTitleComparator tcom = new TIDTitleComparator();
    Arrays.sort(titleID, tcom);
    cats = new String[titleID.length];
    ids = new int[titleID.length];
    for(int i=0; i<cats.length; i++) {
      cats[i] = titleID[i].getTitle();
      ids[i] = titleID[i].getID();
    }
    titleID = null;
  }//end: CategoryTitleToIDTranslation(TitleID[])

 /**
  * Gets the ID for a category name.
  * <p>
  * O(log(n)) lookup time
  * 
  * @param title Name of the category
  * @return Uncompressed ID number
  */
  public int getID(String cat)
  {
    int i = Arrays.binarySearch(cats, cat);
    if(i>=0)
    {
      return ids[i];
    }
    return i;
  }//end: getID(String)

 /**
  * Determines if the name is a category name.
  * 
  * @param name Potential category name
  * @return Whether the name was found or not.
  */
  public boolean isLookupCategory(String name)
  {
    return (getID(name) >= 0);
  }//end: isLookupCategory(String)

 /**
  * Finds the number of categories.
  * 
  * @return The number of categories
  */
  public int getNumCategories()
  {
    return cats.length;
  }

 /**
  * Writes a {@link CategoryTitleToIDTranslation} object to a file.
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
  * Reads a {@link CategoryTitleToIDTranslation} object from a file.
  * 
  * @param in Input file stream.
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    cats = (String[]) in.readObject();
    ids = (int[]) in.readObject();
  }//end: readObject(ObjectInputStream)
}