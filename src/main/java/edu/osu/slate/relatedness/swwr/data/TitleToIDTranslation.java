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
import java.util.Arrays;
import java.util.Comparator;

/**
 * Translates Page titles to un-redirected Page ID values.
 * <p>
 * The internal data structure of this class uses parallel
 * arrays of Strings and integers in order to save space,
 * as memory may be limited.
 * 
 * @author weale
 *
 */
public class TitleToIDTranslation
{
	
  /* List of Wiki Page titles*/
  protected String[] titles;
	
  /* Un-redirected Wiki Page ID values */
  protected int[] ids;
		
 /**
  * Translates a given Page title to its un-redirected Page ID value.
  * <p>
  * If the title is not found, a negative value is returned.
  * <p>
  * This has a O(log(n)) lookup time.
  * 
  * @param title Page title to find.
  * @return Un-redirected Page ID.
  */
  public int getID(String title)
  {
    int i = Arrays.binarySearch(titles, title);
    
    if(i>=0)
    {
      return ids[i];
    }
    else {
      return i;
    }
  }//end: getID(String)
	
 /**
  * Determines whether the given title can be translated to a Wiki ID.
  * 
  * @param title Page title to find.
  * @return Boolean value based on if the title is found.
  */
  public boolean isLookupTitle(String title)
  {
    return (getID(title) >= 0);
  }//end: isLookupTitle(String)
	
 /**
  * Returns the number of lookup titles.
  * 
  * @return Number to lookup titles.
  */
  public int getNumTitles()
  {
    return titles.length;
  }//end: getNumTitles()

 /**
  * 
  * @param temp
  */
  private TitleToIDTranslation(TitleID[] temp)
  {
    TitleID[] titleID = (TitleID[]) temp.clone();
    createArrays(titleID);
    titleID = null;
  }
	
  /**
   * Constructor.
   * <p>
   * Takes a <i>.tid file</i> as its input.
   * 
   * @param filename Name of the <i>.tid file</i>.
   */
  public TitleToIDTranslation(String filename)
  {
    try
    {
      ObjectInputStream fileIn = new ObjectInputStream(
                                 new FileInputStream(filename));
      TitleID[] titleID = (TitleID[]) fileIn.readObject();
      fileIn.close();

      createArrays(titleID);
      titleID = null; // Free memory (?)
    }//end: try {}
    catch (ClassNotFoundException e)
    {
      System.err.println("Problem converting to an integer array: " +
                         filename);
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
  }//end: TitleToIDTranslation(String)
	
 /**
  * Turns an array of {@link TitleID} objects into corresponding
  * parallel arrays of Strings and ints.
  * 
  * @param titleID Array of {@link TitleID} objects.
  */
  private void createArrays(TitleID[] titleID)
  {
    TitleIDComparator tcom = new TitleIDComparator();
    Arrays.sort(titleID, tcom);
    
    titles = new String[titleID.length];
    ids = new int[titleID.length];
    
    for(int i = 0; i < titles.length; i++) {
      titles[i] = titleID[i].getTitle();
      ids[i] = titleID[i].getID();
    }//end: for(i)
  }//end: createArrays(TitleID[])
}