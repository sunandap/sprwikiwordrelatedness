package edu.osu.slate.relatedness.swwr.setup.category;

import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.XMLParser;
import edu.osu.slate.relatedness.swwr.data.ConvertIDToTitle;
import edu.osu.slate.relatedness.swwr.data.category.CategoryGraph;
import edu.osu.slate.relatedness.swwr.data.category.CategoryTitleToIDTranslation;
import edu.osu.slate.relatedness.swwr.data.category.IDToCategoryTitleTranslation;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;
import edu.osu.slate.relatedness.swwr.data.graph.WikiInvGraph;

/**
 * Program creates counts in the category tree.
 * 
 * Category counts can be done via one of the following:
 * <ul>
 * <li> Vertex Counting
 * <li> Inbound Edge Counting
 * </ul>
 * <p>
 * <ul>
 * <li>Preceded by {@link CreateCategoryAcyclic}</li>
 * <li>Followed by {@link ManipulateTransitionWeights}</li>
 * </ul>
 * @author weale
 *
 */
public class CreateGraphCounts {

  /* Name of the previously generated category graph */
  private static String cgraphFileName;

  /* Name of the output file */
  private static String outputCatFileName;

  /* Source file (categorylinks.sql) */
  private static String catSourceFileName;

  /* Name of the previously generated vertex-ID file */
  private static String vidBinaryFileName;

  /* Name of the previously generated inverted graph file */
  private static String invBinaryFileName;
  
  /* Name of the previously generated category-to-ID file */
  private static String cidBinaryFileName;

  /* Translates category titles to category IDs */
  private static CategoryTitleToIDTranslation Cat2ID;
  
  /* Translates category IDs to category titles */
  private static IDToCategoryTitleTranslation ID2Cat;
  /**
   * Sets the names of:<br>
   * 
   * <ul>
   * <li> {@link AliasStrings} file</li>
   * <li> {@link AliasSFToID} file</li>
   * <li> {@link WikiGraph} file</li>
   * <li> Synonym Task file</li>
   * </ul>
   */
  private static void setFiles() {
    /* Set directory, data source */
    String bindir = Configuration.baseDir   + "/" + 
                    Configuration.binaryDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/";
    
    String srcdir = Configuration.baseDir   + "/" + 
                    Configuration.sourceDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;
    
    String count = "";
    catSourceFileName = srcdir +
                        Configuration.type + "-" +
                        Configuration.date +
                        "-categorylinks.sql";
    
    vidBinaryFileName = bindir + data + ".vid";
    invBinaryFileName = bindir + data + ".iwgp";

    cidBinaryFileName = bindir +
                        Configuration.type  + "-" +
                        Configuration.date  + "-" +
                        "page" + ".cid";

    cgraphFileName = bindir +
                    Configuration.type  + "-" +
                    Configuration.date  + "-" +
                    Configuration.graph + ".cgraph";
    
    outputCatFileName = bindir + data + count+ ".cgraph";
  }
  
  /**
   * @param args
   * @throws IOException 
   * @throws FileNotFoundException 
   * @throws ClassNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

    if(args.length == 1)
    {
      XMLParser.parseConfigurationFile(args[0]);
    }
    else
    {
      XMLParser.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/CreateMappings.xml");
    }

    /* Set file names */
    setFiles();

    /* Open .cid file */
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(new FileInputStream(cidBinaryFileName));       
      System.out.println("Opening .cid file.");
      Cat2ID = (CategoryTitleToIDTranslation)in.readObject();
      ID2Cat = (IDToCategoryTitleTranslation)in.readObject();
      in.close();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file: " + cidBinaryFileName);
      System.exit(1);
    }
    
    /* Open .cgraph file */
    System.out.println("Read .cgraph file.");
    CategoryGraph catGraph = null;
    try {
      in = new ObjectInputStream(new FileInputStream(cgraphFileName));
      catGraph = (CategoryGraph) in.readObject();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file " + cgraphFileName);
      System.exit(1);
    }
    
    /* STEP 2
     * 
     * Open list of valid IDs
     */
    System.out.println("Opening .vid file.");
    IDVertexTranslation vids = new IDVertexTranslation(vidBinaryFileName);

    /* STEP 2a
     * 
     * Open inverted graph file (for edge counting)
     */
    WikiInvGraph wg = null;
    System.out.println("Opening .iwgp file.");
    wg = new WikiInvGraph(invBinaryFileName);

    /* STEP 3:
     * 
     * Open Category source for reading.
     */		
    Scanner catIN = new Scanner(new FileReader(catSourceFileName));
    String str = catIN.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = catIN.nextLine();
    }

    /* STEP 4:
     * 
     * Add vertices to the category graph
     */
    System.out.println("Adding vertices to category graph");
    int tmp = 0;
    while(tmp < 3 && str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the category link information
      String [] arr = str.split("\\d\\),\\(");
      for(int i = 0; i < arr.length; i++)
      {
        // Split the category link information into its parts
        String [] info = arr[i].split(",");

        // Check if the information is in the correct format
        if(info.length >= 4)
        {
          // Extract page, category information
          String childPageID = info[0];

          int breakSpot = -1;
          String parentCategoryTitle = info[1];
          for(int j=2; breakSpot < 0 && j<info.length; j++)
          {
            if(info[j-1].charAt(info[j-1].length()-1) == '\'' &&
                info[j].charAt(0) == '\'') {
              breakSpot = j;
            } else {
              parentCategoryTitle = parentCategoryTitle + "," + info[j];
            }
          }
          
          /* Add vertex if:
           * 
           * -- the vertex is a valid page (as defined by being in the VID file)
           * -- the category title is valid
           */
          if(vids.isValidWikiID(Integer.parseInt(childPageID)) &&
              Cat2ID.isLookupCategory(parentCategoryTitle)) {
            int childVertexNum = vids.getVertex(Integer.parseInt(childPageID));
            int parentCategoryID = Cat2ID.getID(parentCategoryTitle);
            
            if(catGraph.isMember(parentCategoryID))
            { // Parent is found!
              catGraph.addEdge(parentCategoryID, childVertexNum);
            }//end: if(catGraph)
          }//end: if(vids && Cat2ID)
          
        }//end: if(info.length)
      }//end: for(i)

      str = catIN.nextLine();
    }//end: while()
    catIN.close();

    /* Step 5:
     * 
     * With the leaf nodes established, now propagate the appropriate counts up the tree.
     * 
     * Use
     * - the vertex count [propagateVertexCounts()]
     * - the edge count   [propagateInboundEdgeCounts()]
     */
    System.out.println("Propogating Counts");
    catGraph.setInboundEdgeCounts(wg);
    System.out.println("Propogating Counts Done");

    /* STEP 6:
     * 
     * Write .cgraph file 
     */
    System.out.println("Printing file: " + outputCatFileName);
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outputCatFileName));
    out.writeObject(catGraph);
    out.close();

  }//end: main()
}
