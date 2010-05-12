package edu.osu.slate.relatedness.swwr.setup.textcompare;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;

public class TrimToVertices {
  
  /* Name of the previously generated inverted graph file */
  private static String tempSourceFileName;
  
  private static String indexSourceFileName;
  
  /* Name of the previously generated vertex-ID file */
  private static String vidBinaryFileName;
  
  private static void setFiles() {
    /* Set directory, data source */
    String sourcedir = Configuration.baseDir   + "/" + 
                    Configuration.sourceDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/";
    
    String binarydir = Configuration.baseDir   + "/" + 
                       Configuration.binaryDir + "/" +
                       Configuration.type+ "/" +
                       Configuration.date+ "/";
    
    String xmldata = Configuration.type + "-" +
                     Configuration.date + "-" +
                     "pages-articles";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;
    
    tempSourceFileName = sourcedir + xmldata + ".tmp.xml";
    indexSourceFileName = sourcedir + xmldata + ".lemur.xml";
    vidBinaryFileName = binarydir + data + ".vid";
  }
  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateMappings.xml");
    }
    
    setFiles();
    
    System.out.println("Opening .vid file.");
    IDVertexTranslation vids = new IDVertexTranslation(vidBinaryFileName);
    
    System.out.println("Opening index files.");
    Scanner s = new Scanner(new FileReader(tempSourceFileName));
    PrintWriter pw = new PrintWriter(indexSourceFileName);
    
    String line = s.nextLine();
    while(line.indexOf("<page>") == -1)
    {
      line = s.nextLine();
    }
    
    StringBuilder doc = new StringBuilder();
    while(s.hasNext())
    {
      doc.append(line + "\n");
      if(line.indexOf("</page>") != -1)
      {
        String document = doc.toString();
        int idStart = document.indexOf("<id>");
        int idEnd = document.indexOf("</id>");        
        int id = Integer.parseInt(document.substring(idStart + 4, idEnd));
        
        if(vids.isValidWikiID(id))
        {
          pw.println(document);
        }
        
        doc = new StringBuilder(); 
      }//end: if("</DOC>")
      line = s.nextLine();
    }//end: while(s)
    s.close();
    pw.close();

  }  
}
