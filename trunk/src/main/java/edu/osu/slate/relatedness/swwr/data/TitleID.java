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

import java.io.*;

/**
 * Simple container for a (Page Title, Page ID) pair.
 * <p>
 * This class holds the raw title and ID of the pair.  That is,
 * if the ID needs to be redirected, it must be done so outside
 * of this class using the {@link IDVertexTranslation} class.
 * 
 * @author weale
 * @version 1.0
 */
public class TitleID implements Serializable {

 /**
  * 
  */
  private static final long serialVersionUID = 1L;
  private String title;
  private int ID;
	
 /**
  * Returns the Page title of the pair.
  * 
  * @return Page title.
  */
  public String getTitle()
  {
    return title;
  }
	
 /**
  * Returns the Page ID of the pair.
  * <p>
  * Page ID is not redirected (if needed).
  * 
  * @return Object ID.
  */
  public int getID()
  {
    return ID;
  }

 /**
  * Constructor.
  * <p>
  * Initializes the (title, ID) pair.
  * 
  * @param t Page title.
  * @param i Page ID.
  */
  public TitleID(String t, int i)
  {
    title = t;
    ID = i;
  }
	
 /**
  * Compares two {@link TitleID} objects.
  * <p>
  * Comparison is made on the title fields.
  * 
  * @param o {@link TitleID} object to compare.
  * @return Integer value of the comparison.
  */
  public int compareTo(TitleID o)
  {
    return this.title.compareTo(o.title);
  }
 /**
  * Synchronized object writing.
  * 
  * @param out {@link ObjectOutputStream} to write to.
  * @throws IOException
  */
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.writeObject(title);
    out.writeInt(ID);
  }
  
 /**
  * Synchronized object reading.
  * 
  * @param in {@link ObjectInputStream} to read from.
  * @throws IOException
  * @throws ClassNotFoundException
  */
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    title = (String) in.readObject();
    ID = in.readInt();
  }
}
