package edu.osu.slate.relatedness;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import edu.osu.slate.relatedness.Configuration;

/**
 * Parser for configuration XML file.
 * <p>
 * Uses the {@link Configuration} class for variable storage.
 * 
 * @author weale
 * @version 1.0
 */
public class XMLParser {
  
 /**
  * Parses the XML filename.
  * <p>
  * Variable values are placed in the {@link Configuration} class.
  * 
  * @param filename XML filename.
  */
  public static void parseConfigurationFile(String filename) {
    
    try
    {
      Scanner config = new Scanner(new FileReader(filename));

      Configuration.sourceDir = "source";
      Configuration.binaryDir = "binary";
      Configuration.tempDir = "tmp";

      while(config.hasNext())
      {
        String s = config.nextLine();
        if(s.contains("<basedir>"))
        {
          Configuration.baseDir = s.substring(s.indexOf("<basedir>") + 9,
              s.indexOf("</basedir>"));
        }
        else if(s.contains("<sourcedir>"))
        {
          Configuration.sourceDir = s.substring(s.indexOf("<sourcedir>") + 11,
              s.indexOf("</sourcedir>"));          
        }
        else if(s.contains("<binarydir>"))
        {
          Configuration.binaryDir = s.substring(s.indexOf("<binarydir>") + 11,
              s.indexOf("</binarydir>"));
        }
        else if(s.contains("<tempdir>"))
        {
          Configuration.tempDir = s.substring(s.indexOf("<tempdir>") + 9,
              s.indexOf("</tempdir>"));
        }
        else if(s.contains("<type>"))
        {
          Configuration.type = s.substring(s.indexOf("<type>") + 6,
              s.indexOf("</type>"));
        }
        else if(s.contains("<date>"))
        {
          Configuration.date = s.substring(s.indexOf("<date>") + 6,
              s.indexOf("</date>"));
        }
        else if(s.contains("<graph>"))
        {
          Configuration.graph = s.substring(s.indexOf("<graph>") + 7,
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
