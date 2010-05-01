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

import java.util.Arrays;

/**
 * Class to link a vertex to a set of categories.
 * <p>
 * Each category has an associated coverage number via the {@link CategoryIDCoverage} class.
 * 
 * @author weale
 *
 */
public class VertexToCategoryIDCoverage {
  private int vertex;
  private CategoryIDCoverage[] cats;

 /**
  * Constructor.
  * <p>
  * Creates an array of {@link CategoryIDCoverage} objects of length 0.
  * 
  * @param v Vertex number.
  */
  public VertexToCategoryIDCoverage(int v)
  {
    vertex = v;
    cats = new CategoryIDCoverage[0];
  }
 /**
  * Adds a new {@link CategoryIDCoverage} object to the array.
  * <p>
  * Ensures that there are no duplicate objects in the array.
  *  
  * @param cid {@link CategoryIDCoverage} object to add.
  */
  public void addCategory(CategoryIDCoverage cid) {
    int pos = Arrays.binarySearch(cats, cid, new CICIDComparator());
    if(pos < 0)
    { //Category not already defined
      
      //Create new array
      CategoryIDCoverage[] tmp = new CategoryIDCoverage[cats.length+1];
      System.arraycopy(cats, 0, tmp, 0, cats.length);
      tmp[cats.length] = cid;
      cats = tmp;
      Arrays.sort(cats, new CICIDComparator());
    }//end: if(pos)
  }
  
 /**
  * Gets the vertex number.
  * 
  * @return Vertex number.
  */
  public int getVertex()
  {
    return vertex;
  }//end: getVertex()
  
 /**
  * Gets the array of {@link CategoryIDCoverage} items.
  * <p>
  * If no categories have been defined, the array will have length 0.
  * 
  * @return Array of {@link CategoryIDCoverage} objects.
  */
  public CategoryIDCoverage[] getCategories()
  {
    return cats;
  }//end: getCategories()
}
