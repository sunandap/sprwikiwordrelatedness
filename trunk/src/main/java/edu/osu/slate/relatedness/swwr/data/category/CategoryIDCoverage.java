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

/**
 * Class containing a (Category ID, coverage) pair.
 * 
 * @author weale
 * @version 1.0
 */
public class CategoryIDCoverage
{
  private int catID;
  private int catCount;
  private float coverage;
  
 /**
  * Constructor.
  *  
  * @param id Category ID.
  */
  public CategoryIDCoverage(int id)
  {
    catID = id;
    catCount = 0;
    coverage = 0;
  }//end: CategoryIDCoverage(int,float)
  
 /**
  * Calculates the category coverage.
  *  
  * @param total Total number of vertices.
  */
  public void calculateCoverage(int total)
  {
    coverage = catCount;
    coverage = coverage / total;
  }
  
 /**
  * Adds the given vertex count to the previous category count.
  * <p>
  * <b>NOTE:</b> There is NO guarantee that vertices added will be unique.
  * In order to guarantee uniqueness, use another class (forthcoming).
  * 
  * @param vertexCount Count of vertices to add.
  */
  public void addToCategoryCount(int vertexCount)
  {
    catCount += vertexCount;
  }
  
  public int getRawCategoryCount()
  {
    return catCount;
  }
  
 /**
  * Sets the category coverage.
  * 
  * @param c New coverage amount.
  */
  public void setCoverage(float c)
  {
    coverage = c;
  }//end: setCoverage()
  
 /**
  * Returns the coverage value for the (ID, coverage) pair.
  * 
  * @return Coverage value.
  */
  public float getCoverage()
  {
    return coverage;
  }//end: getCoverage()
  
 /**
  * Returns the Category ID for the (ID, coverage) pair.
  * 
  * @return Category ID.
  */
  public int getCatID() {
    return catID;
  }//end: getCatID()
}
