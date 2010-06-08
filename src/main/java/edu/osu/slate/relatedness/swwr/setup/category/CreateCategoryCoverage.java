package edu.osu.slate.relatedness.swwr.setup.category;

import java.io.*;
import java.util.*;

import edu.osu.slate.relatedness.swwr.data.category.CICIDComparator;
import edu.osu.slate.relatedness.swwr.data.category.CategoryIDCoverage;
import edu.osu.slate.relatedness.swwr.data.graph.IDIDRedirect;
import edu.osu.slate.relatedness.swwr.data.graph.IDVertexTranslation;

/**
 * 
 * @author weale
 *
 */
public class CreateCategoryCoverage {

  private static String baseDir, sourceDir, binaryDir, tempDir;
  private static String type, date, graph;
  
  private static void parseConfigurationFile(String filename) {
    try {
      Scanner config = new Scanner(new FileReader(filename));
      sourceDir = "source";
      binaryDir = "binary";
      tempDir = "tmp";
      while(config.hasNext()) {
        String s = config.nextLine();
        if(s.contains("<basedir>")) {
          baseDir = s.substring(s.indexOf("<basedir>") + 9, s.indexOf("</basedir>"));
        }
        else if(s.contains("<sourcedir>")) {
          sourceDir = s.substring(s.indexOf("<sourcedir>") + 11, s.indexOf("</sourcedir>"));          
        }
        else if(s.contains("<binarydir>")) {
          binaryDir = s.substring(s.indexOf("<binarydir>") + 11, s.indexOf("</binarydir>"));
        }
        else if(s.contains("<tempdir>")) {
          tempDir = s.substring(s.indexOf("<tempdir>") + 9, s.indexOf("</tempdir>"));
        }
        else if(s.contains("<type>")) {
          type = s.substring(s.indexOf("<type>") + 6, s.indexOf("</type>"));
        }
        else if(s.contains("<date>")) {
          date = s.substring(s.indexOf("<date>") + 6, s.indexOf("</date>"));
        }
        else if(s.contains("<graph>")) {
          graph = s.substring(s.indexOf("<graph>") + 7, s.indexOf("</graph>"));
        }
      }
    }
    catch (IOException e) {
      System.err.println("Problem reading from file: " + filename);
      System.exit(1);
    }
  }
  
  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException
  {
    TreeMap<String,Integer> TitleToID = new TreeMap<String,Integer>();
    parseConfigurationFile("/scratch/weale/data/config/enwiktionary/CreateTitleWordMapping.xml");
    
    System.out.println("Initializing Valid ID List.");
    IDVertexTranslation vid = new IDVertexTranslation(baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + graph + ".vid");
 
    System.out.println("Initializing Redirect List.");
    IDIDRedirect rdl = new IDIDRedirect(baseDir + "/" + binaryDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + graph + ".rdr");
    
    System.out.println("Opening page.sql File");
    Scanner in = new Scanner(new FileReader(baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + "page.sql"));
    
    System.out.println("Opening .ccf File for Writing");
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(baseDir + "/" + tempDir + "/" + type + "-"+ date + "-" + graph + ".ccf"));
    
    /* STEP 1
     * 
     * Create the Category Title to ID mapping.
     */
    
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
       for(int i=0;i<arr.length;i++) {
         
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
           for(int j=3; j<info.length-8;j++)
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

    /* STEP 2a
     * 
     * Create the Category to Vertex mapping from categorylinks file.
     */
     in = new Scanner(new FileReader(baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + "categorylinks.sql"));

     System.out.println("Initializing Category to Vertex Count Mapping from categorylinks.");      
     
     TreeMap<Integer,Integer> cic = new TreeMap<Integer,Integer>();
     str = in.nextLine();
     while(str.indexOf("INSERT INTO") == -1)
     {
       str = in.nextLine();
     }
   
     int line = 0;
   
     while(str != null && !str.trim().equals("")) {
       str = str.substring(str.indexOf("(")+1, str.length()-3);

     // Split the String into the page information
     String [] arr = str.split("\\),\\(");
     for(int i=0;i<arr.length;i++) {
       String [] info = arr[i].split(",");
         
       // Check if the information is in the correct format
       if(info.length >= 3) {
             
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
           if((vid.isValidWikiID(id) || rdl.isRedirectID(id)))
           {

             int vertex = -1;
             if(vid.isValidWikiID(id))
             {
               vertex = vid.getVertex(id);
             }
             else if(rdl.isRedirectID(id))
             {
               vertex = vid.getVertex(rdl.redirectIDToValidID(id));
             }
             //System.out.println(title);
             int catID = -1;
             if(TitleToID.containsKey(title))
             {
               catID = TitleToID.get(title);
             }
             
             /* Check valid from/to pairing */
             if(vertex > -1 && catID > -1) {
               int tmpCount = 0;
               
               if(cic.containsKey(catID))
               {
                 tmpCount = cic.get(catID);
               }
               
               cic.put(catID, tmpCount+1);
             }//end: if(vertex && catID)
           }//end: valid namespace and 'from' page
         }//end: if(title)
       }//end: if(info.length)
     }//end: for(i)  
     str = in.nextLine();
   }//end: while()
   in.close();
   
  /* STEP 2b
   * 
   * Create the Category to Vertex mapping from pagelinks file.
   */
   in = new Scanner(new FileReader(baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + "categorylinks.sql"));

   System.out.println("Initializing Category to Vertex Count Mapping from pagelinks.");
   str = in.nextLine();
   while(str.indexOf("INSERT INTO") == -1)
   {
     str = in.nextLine();
   }

   while(str != null && !str.trim().equals("")) {
     str = str.substring(str.indexOf("(")+1, str.length()-3);

     // Split the String into the page information
     String [] arr = str.split("\\),\\(");
     for(int i=0;i<arr.length;i++) {
       String [] info = arr[i].split(",");

       // Check if the information is in the correct format
       if(info.length >= 3) {

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
           if(namespace.equals("14") && (vid.isValidWikiID(id) || rdl.isRedirectID(id)))
           {

             int vertex = -1;
             if(vid.isValidWikiID(id))
             {
               vertex = vid.getVertex(id);
             }
             else if(rdl.isRedirectID(id))
             {
               vertex = vid.getVertex(rdl.redirectIDToValidID(id));
             }
             //System.out.println(title);
             int catID = -1;
             if(TitleToID.containsKey(title))
             {
               catID = TitleToID.get(title);
             }

             /* Check valid from/to pairing */
             if(vertex > -1 && catID > -1) {
               int tmpCount = 0;

               if(cic.containsKey(catID))
               {
                 tmpCount = cic.get(catID);
               }

               cic.put(catID, tmpCount+1);
             }//end: if(vertex && catID)
           }//end: valid namespace and 'from' page
         }//end: if(title)
       }//end: if(info.length)
     }//end: for(i)  
     str = in.nextLine();
   }//end: while()
   in.close();
   /* STEP 2a
    * 
    * Create the Category to Vertex mapping from categorylinks file.
    */
    in = new Scanner(new FileReader(baseDir + "/" + sourceDir + "/" + type+ "/" + date + "/" + type + "-"+ date + "-" + "categorylinks.sql"));

    System.out.println("Initializing Category to Vertex Count Mapping from categorylinks.");      
    
    TreeMap<Integer,int[]> categoryIDToChildren = new TreeMap<Integer,int[]>();
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
      if(info.length >= 3) {
            
        // Extract page, namespace and redirect information
        int id = Integer.parseInt(info[0]);
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

          // Check that the 'TO' is a category title
          if(TitleToID.containsKey(title))
          {

            int catID = TitleToID.get(title);
            if(!vid.isValidWikiID(id) && !rdl.isRedirectID(id))
            { // cat-to-cat ID pair
              int[] children = null;
              
              if(categoryIDToChildren.containsKey(catID))
              {// Add child to existing catID entry
                int[] temp = categoryIDToChildren.get(catID);
                children = new int[temp.length+1];
                System.arraycopy(temp, 0, children, 0, temp.length);
                children[temp.length] = id;
              }
              else
              {// First catID sighting
                children = new int[1];
                children[0] = id;
              }
              
              categoryIDToChildren.put(catID, children);
            }//end: if(!vid && !rdl)
              
          }//end: valid category title
        }//end: if(title)
      }//end: if(info.length)
    }//end: for(i)  
    str = in.nextLine();
  }//end: while()
  in.close(); 
   out.close();
   System.out.println(TitleToID.size());
   System.out.println(cic.size());
   System.out.println(categoryIDToChildren.size());

  }//end: main()
}//end: CreateCategoryCoverage
