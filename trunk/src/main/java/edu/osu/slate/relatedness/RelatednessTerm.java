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

package edu.osu.slate.relatedness;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author weale
 *
 */
public class RelatednessTerm implements Serializable {

 /**
  * 
  */
  private static final long serialVersionUID = 1L;

  private String term;
  private float rel;

  public String getTerm() {
    return term;
  }

  public float getRel() {
    return rel;
  }

  public RelatednessTerm(String w, float f) {
    rel = f;
    term = w;
  }
	
  public int compareTo(RelatednessTerm o) {
    if(this.rel - o.rel < 0) {
      return -1;
    }
    else if (this.rel - o.rel > 0) {
      return 1;
    }
    else {
      return 0;
    }
  }//end: compareTo()

  public int compareID(RelatednessTerm o) {
    return this.term.compareTo(o.term);
  }//end: compareID()

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeDouble(rel);
    out.writeObject(term);
  }//end: writeObject()
 
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    rel = in.readFloat();
    term = (String) in.readObject();
  }//end: readObject()
  
}//end: RelatednessTerm
