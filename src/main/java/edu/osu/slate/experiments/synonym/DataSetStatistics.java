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

package edu.osu.slate.experiments.synonym;

import java.util.*;
import java.io.*;

import edu.osu.slate.relatedness.Configuration;

/**
 * Gathers term statistics on the synonym data sets.
 *
 * @author weale
 */
public class DataSetStatistics {

  private static String taskFile;
  
  /**
   * Sets the name of the synonym task file.
   */
  private static void setFiles()
  {
    taskFile = Configuration.taskDir + Configuration.task + ".txt";
  }
  
  /**
   * Main portion of the program
   * <p>
   * If no arguments are provided, uses defaults hard-coded by user.
   * 
   * @param args 0 or 1 argument accepted
   * @throws IOException General IO errors
   * @throws ClassNotFoundException Problems with the {@link WikiGraph} file
   */
  public static void main(String [] args) throws IOException, ClassNotFoundException {
    
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/SynonymTask.xml");
    }
    setFiles();

    String[] tasks = {"ESL", "TOEFL", "RDWP300", "RDWP1K"};
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Configuration.task = tasks[currTask];
      System.out.println("Setting Synonym Task: " + Configuration.task);
      setFiles();
      Scanner s = new Scanner(new FileReader(taskFile));
      double terms = 0, words = 0;
      double soloterms = 0;
      while(s.hasNext())
      {
        /* Get Next Question */
        String str = s.nextLine();
  
        /* Split the input string */			
        String[] arr = str.split("\\|");
        terms += arr.length;
        
        for(int i=0; i<arr.length; i++)
        {
          arr[i] = arr[i].trim();
          
          String[] arr2 = arr[i].split(" ");
          words += arr2.length;
          for(int j = 0; j< arr2.length; j++)
          {
            //System.out.println(arr2[j]);
            soloterms += arr2[j].length();
          }
        }//end: for(i)
      }//end: while(s.hasNext())
      System.out.println(words / terms);
      System.out.println(soloterms/words);
    }    
  }//end: main
}
