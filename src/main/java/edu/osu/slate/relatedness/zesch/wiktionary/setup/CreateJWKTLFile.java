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

package edu.osu.slate.relatedness.zesch.wiktionary.setup;

import java.util.List;
import de.tudarmstadt.ukp.wiktionary.api.*;

/**
 * 
 * @author weale
 *
 */
public class CreateJWKTLFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //if(args.length != 4) {
        //    throw new IllegalArgumentException("Too few arguments. Required arguments: <DUMP_FILE> <OUTPUT_DIRECTORY> <ISO_LANGUAGE_CODE (en/de)> <OVERWRITE_EXISTING_DATA>");
        //}           
//        String dumpFile = "/scratch/weale/data/source/enwiktionary/20090203/enwiktionary-20090203-pages-articles.xml.bz2";
        String outputDirectory = "/scratch/weale/data/binary/zesch/enwiktionary/20071016";
        String languageIsoCode = "en";
        boolean overwriteExisting = false;
    
        // parse dump file
//        Wiktionary.parseWiktionaryDump(dumpFile, outputDirectory, languageIsoCode, overwriteExisting);
    
        // create new Wiktionary object using the parsed data
        Wiktionary wkt = new Wiktionary(outputDirectory);
    
        // get entries for "Wiktionary"
        List<WordEntry> entries = wkt.getWordEntries("Wiktionary");
    
        // print information of entries
        System.out.println(Wiktionary.getEntryInformation(entries));
    
        // close Wiktionary object
        wkt.close();  
	}//end: main()

}
