package edu.osu.slate.relatedness;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Project configuration class.
 * <p>
 * Holds the basic information about the project.
 * Accessed from the various project applications for processing.
 * 
 * @author weale
 * @version 1.0
 */
public class Configuration {
  
 /**
  * Base directory for the relatedness files. 
  */
  public static String baseDir;
  
 /**
  * Directory within the baseDir for the XML and SQL files.
  * <p>
  * Default value is "source".
  */
  public static String sourceDir;
  
 /**
  * Directory within the baseDir for the generated binary files.
  * <p>
  * Default value is "binary".
  */
  public static String binaryDir;
  
 /**
  * Directory within the baseDir for the intermediate processing files.
  * <p>
  * Default value is "tmp".
  */
  public static String tempDir;
  
 /**
  * Type of Wiki to process.
  * <p>
  * No default.
  * <p>
  * <ul>
  *   <li><b>enwiki</b> -- English Wikipedia</li>
  *   <li><b>enwiktionary</b> -- English Wiktionary</li>
  * </ul>
  */
  public static String type;
  
 /**
  * Date of the Wiki data dump.
  * <p>
  * Formatting: YYYYMMDD
  */
  public static String date;
  
 /**
  * Type of graph to use in the processing
  * <p>
  * Examples:
  * <ul>
  *   <li><b>M</b> -- Using "Main" namespace only</li>
  * </ul>
  */
  public static String graph;
  
  /**
   * Parses the XML configuration file.
   * <p>
   * Variable values are placed in the {@link Configuration} class variables.
   * 
   * @param filename XML filename.
   */
   public static void parseConfigurationFile(String filename) {
     
     try
     {
       Scanner config = new Scanner(new FileReader(filename));

       sourceDir = "source";
       binaryDir = "binary";
       tempDir = "tmp";

       while(config.hasNext())
       {
         String s = config.nextLine();
         if(s.contains("<basedir>"))
         {
           baseDir = s.substring(s.indexOf("<basedir>") + 9,
               s.indexOf("</basedir>"));
         }
         else if(s.contains("<sourcedir>"))
         {
           sourceDir = s.substring(s.indexOf("<sourcedir>") + 11,
               s.indexOf("</sourcedir>"));          
         }
         else if(s.contains("<binarydir>"))
         {
           binaryDir = s.substring(s.indexOf("<binarydir>") + 11,
               s.indexOf("</binarydir>"));
         }
         else if(s.contains("<tempdir>"))
         {
           tempDir = s.substring(s.indexOf("<tempdir>") + 9,
               s.indexOf("</tempdir>"));
         }
         else if(s.contains("<type>"))
         {
           type = s.substring(s.indexOf("<type>") + 6,
               s.indexOf("</type>"));
         }
         else if(s.contains("<date>"))
         {
           date = s.substring(s.indexOf("<date>") + 6,
               s.indexOf("</date>"));
         }
         else if(s.contains("<graph>"))
         {
           graph = s.substring(s.indexOf("<graph>") + 7,
               s.indexOf("</graph>"));
         }
       }//end: while(config)
     }//end: try {}
     catch (IOException e) {
       System.err.println("Problem reading from configuration file: " + filename);
       System.exit(1);
     }
   }//end:parseConfigurationFile(String)
}