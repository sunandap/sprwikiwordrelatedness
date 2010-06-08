package edu.osu.slate.relatedness.swwr.setup.category;

import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.graph.IDIDRedirect;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

/**
 * Creates a list of categories for each graph vertex.
 * <p>
 * Category lists are only of immediate parents.
 * 
 * @author weale
 *
 */
public class CreateVertexToCategoryMapping
{
 
  private static IDVertexTranslation vid;
  private static String vidFileName;
  
  private static IDIDRedirect rdr;
  private static String rdrFileName;
  
  private static WikiGraph wgp;
  private static String wgpFileName;
  
  private static Scanner in;
  private static ObjectOutputStream out;
  
  private static void setFiles()
  {
    vidFileName = Configuration.baseDir + "/" + 
                  Configuration.binaryDir + "/" +
                  Configuration.type+ "/" +
                  Configuration.date + "/" +
                  Configuration.type + "-"+
                  Configuration.date + "-" +
                  Configuration.graph + ".vid";
    
    rdrFileName = Configuration.baseDir + "/" + 
                  Configuration.binaryDir + "/" +
                  Configuration.type+ "/" +
                  Configuration.date + "/" +
                  Configuration.type + "-"+
                  Configuration.date + "-" +
                  Configuration.graph + ".rdr";
    
    wgpFileName = Configuration.baseDir + "/" + 
    Configuration.binaryDir + "/" +
    Configuration.type+ "/" +
    Configuration.date + "/" +
    Configuration.type + "-"+
    Configuration.date + "-" +
    Configuration.graph + ".wgp";
  }
  
  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException
  {
    Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiki/CreateTitleWordMapping.xml");
    
    setFiles();
    
    try
    {
      System.out.println("Initializing Valid ID List.");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(vidFileName));
      vid = (IDVertexTranslation) in.readObject();
      in.close();
    }//end: try {}
    catch(Exception e)
    {
      System.out.println("Problem reading file: " + vidFileName);
      System.exit(1);
    }//end: catch(IOException)
 
    try
    {
      System.out.println("Initializing Redirect List.");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(rdrFileName));
      rdr = (IDIDRedirect) in.readObject();
      in.close();
    }//end: try {}
    catch(Exception e)
    {
      System.out.println("Problem reading file: " + rdrFileName);
      System.exit(1);
    }//end: catch(IOException)
    
    // Open Wiki Graph
    try
    {
      System.out.println("Opening Wiki Graph");
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(wgpFileName));
      wgp = (WikiGraph) in.readObject();
      in.close();
    }
    catch(Exception e)
    {
      System.err.println("Problem with file: " + wgpFileName);
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("Opening page.sql File");
    in = new Scanner(new FileReader(Configuration.baseDir + "/" +
                                    Configuration.sourceDir + "/" +
                                    Configuration.type+ "/" +
                                    Configuration.date + "/" + 
                                    Configuration.type + "-"+ 
                                    Configuration.date + "-" + 
                                    "page.sql"));
    
    System.out.println("Opening .ccf File for Writing");
    out = new ObjectOutputStream(new FileOutputStream(Configuration.baseDir + "/" + 
                                                      Configuration.binaryDir + "/" + 
                                                      Configuration.type + "/" +
                                                      Configuration.date + "/" +
                                                      Configuration.type + "-"+ 
                                                      Configuration.date + "-" + 
                                                      Configuration.graph + ".vxc"));

    
    TreeMap<String,Integer> TitleToID = new TreeMap<String,Integer>();

    /* Create a title-to-ID lookup for categories. */
    // Strip beginning lines from file
    String str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1) {
      str = in.nextLine();
    }
     
    int tmp = 0;
    while(tmp < 3 && str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the individual page information
      String [] arr = str.split("\\d\\),\\(");
      for(int i = 0; i < arr.length; i++)
      {
        String [] info = arr[i].split(",");
           
        // Information is in the correct format if the length == 11
        if(info.length >= 11)
        {
          // Extract page, namespace and redirect information
          String page = info[0];
          String namespace = info[1];
          String redirect = info[info.length-6];
               
          // Extract title information
          String title = info[2];
          for(int j = 3; j < info.length-8; j++)
          {
            title = title + "," + info[j];
          }
          title = title.substring(1, title.length()-1);
          int pageID = Integer.parseInt(page);
               
          // Add the Title-to-ID mapping if page is in the category namespace
          if(namespace.equals("14") && redirect.equals("0"))
          {
            TitleToID.put(title, pageID);
          }//end: if()
                 
        }//end: if(info.length)
      }//end: for(i)
       
      str = in.nextLine();
    }//end: while()
    in.close();
     
    /* Create the Category to Vertex mapping from categorylinks file. */
    in = new Scanner(new FileReader(Configuration.baseDir + "/" +
                                    Configuration.sourceDir + "/" + 
                                    Configuration.type+ "/" + 
                                    Configuration.date + "/" +
                                    Configuration.type + "-"+
                                    Configuration.date + "-" +
                                    "categorylinks.sql"));

    System.out.println("Initializing Category to Vertex Count Mapping from categorylinks.");      
     
    int[][] vertexToCategories = new int[wgp.getNumVertices()][];
    
    str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = in.nextLine();
    }
   
    int line = 0;
   
    while(str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(") + 1, str.length() - 3);

      // Split the String into the page information
      String [] arr = str.split("\\),\\(");
      for(int i = 0; i < arr.length; i++)
      {
        String [] info = arr[i].split(",");
         
        // Check if the information is in the correct format
        if(info.length >= 3)
        {
          // Extract page, namespace and redirect information
          int id = Integer.parseInt(info[0]);
          String namespace = info[1];
          String title = info[1];
          for(int j = 2; j < info.length-2; j++)
          {
            title = title + "," + info[j];
          }
             
          if(title.length() > 0)
          {
            try
            {
              title = title.substring(1, title.length()-1);
            }
            catch(Exception e)
            {
              System.err.println(arr[i]);
            }

            // Add the ID if it's in the needed namespace and not a redirect
            if((vid.isValidWikiID(id) || rdr.isRedirectID(id)))
            {

              int vertex = -1;
              if(vid.isValidWikiID(id))
              {
                vertex = vid.getVertex(id);
              }
              else if(rdr.isRedirectID(id))
              {
                vertex = vid.getVertex(rdr.redirectIDToValidID(id));
              }
              //System.out.println(title);
              int catID = -1;
              if(TitleToID.containsKey(title))
              {
                catID = TitleToID.get(title);
              }

              /* Check valid from/to pairing */
              if(vertex > -1 && catID > -1)
              {
                if(vertexToCategories[vertex] == null)
                {
                  vertexToCategories[vertex] = new int[1];
                  vertexToCategories[vertex][0] = catID;
                }
                else if(Arrays.binarySearch(vertexToCategories[vertex], catID) < 0)
                {
                  int[] newCats = new int[vertexToCategories[vertex].length + 1];
                  System.arraycopy(vertexToCategories[vertex], 0,
                                   newCats, 0, vertexToCategories[vertex].length);
                  newCats[vertexToCategories[vertex].length] = catID;
                  Arrays.sort(newCats);
                  vertexToCategories[vertex] = newCats;
                }
              }//end: if(vertex && catID)
            }//end: valid namespace and 'from' page
          }//end: if(title)
        }//end: if(info.length)
      }//end: for(i)
      
      str = in.nextLine();
    }//end: while()
    in.close();

    /* Create the Category to Vertex mapping from pagelinks file. */
    in = new Scanner(new FileReader(Configuration.baseDir + "/" +
         Configuration.sourceDir + "/" +
         Configuration.type+ "/" +
         Configuration.date + "/" +
         Configuration.type + "-"+
         Configuration.date + "-" + 
         "categorylinks.sql"));

    System.out.println("Initializing Category to Vertex Count Mapping from pagelinks.");
    str = in.nextLine();
    while(str.indexOf("INSERT INTO") == -1)
    {
      str = in.nextLine();
    }

    while(str != null && !str.trim().equals(""))
    {
      str = str.substring(str.indexOf("(")+1, str.length()-3);

      // Split the String into the page information
      String [] arr = str.split("\\),\\(");
      for(int i = 0; i < arr.length; i++)
      {
        String [] info = arr[i].split(",");

        // Check if the information is in the correct format
        if(info.length >= 3)
        {
          // Extract page, namespace and redirect information
          int id = Integer.parseInt(info[0]);
          String namespace = info[1];
          String title = info[2];
          for(int j = 3; j < info.length; j++)
          {
            title = title + "," + info[j];
          }

          if(title.length() > 0)
          {
            try
            {
              title = title.substring(1, title.length()-1);
            }
            catch(Exception e)
            {
              System.err.println(arr[i]);
            }

            // Add the ID if it's in the needed namespace and not a redirect
            if(namespace.equals("14") && (vid.isValidWikiID(id) || rdr.isRedirectID(id)))
            {

              int vertex = -1;
              if(vid.isValidWikiID(id))
              {
                vertex = vid.getVertex(id);
              }
              else if(rdr.isRedirectID(id))
              {
                vertex = vid.getVertex(rdr.redirectIDToValidID(id));
              }
              
              int catID = -1;
              if(TitleToID.containsKey(title))
              {
                catID = TitleToID.get(title);
              }

              /* Check valid from/to pairing */
              if(vertex > -1 && catID > -1)
              {
                if(vertexToCategories[vertex] == null)
                {
                  vertexToCategories[vertex] = new int[1];
                  vertexToCategories[vertex][0] = catID;
                }
                else if(Arrays.binarySearch(vertexToCategories[vertex], catID) < 0)
                {
                  int[] newCats = new int[vertexToCategories[vertex].length + 1];
                  System.arraycopy(vertexToCategories[vertex], 0,
                                   newCats, 0, vertexToCategories[vertex].length);
                  newCats[vertexToCategories[vertex].length] = catID;
                  Arrays.sort(newCats);
                  vertexToCategories[vertex] = newCats;
                }
              }//end: if(vertex && catID)
            }//end: valid namespace and 'from' page
          }//end: if(title)
        }//end: if(info.length)
      }//end: for(i)  
      
      str = in.nextLine();
    }//end: while()
    in.close();
    
    out.writeObject(vertexToCategories);
    out.close();
  }//end: main()
}//end: CreateCategoryCoverage
