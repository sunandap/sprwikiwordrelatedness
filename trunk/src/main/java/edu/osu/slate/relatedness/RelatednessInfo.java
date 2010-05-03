package edu.osu.slate.relatedness;

public class RelatednessInfo {
  
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
}
