/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.osu.slate.uima;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import de.tudarmstadt.ukp.dkpro.core.type.*;

/**
 * Example annotator that detects room numbers using Java 1.4 regular expressions.
 */
public class WordPairTokenAnnotator extends JCasAnnotator_ImplBase {
  
  private Pattern tokenPattern = Pattern.compile("\\p{Alpha}+");
  
  /**
   * @see JCasAnnotator_ImplBase#process(JCas)
   */
  public void process(JCas aJCas) {
    // get document text
    String docText = aJCas.getDocumentText();
    
    // search for Yorktown room numbers
    Matcher matcher = tokenPattern.matcher(docText);
    while (matcher.find()) {
      // found one - create annotation
      Token annotation = new Token(aJCas);
      annotation.setBegin(matcher.start());
      annotation.setEnd(matcher.end());
      annotation.addToIndexes();
    }
  }

}
