package edu.osu.slate.relatedness.swwr.setup.textcompare;

import java.io.*;
import java.util.TreeMap;

import lemurproject.lemur.*;
import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.graph.WikiGraph;

public class CompareTextVectors
{
  /* Name of the previously generated inverted graph file */
  private static String graphBinaryFileName;
  private static String indexLocation;
  private static String outputGraphFileName;
  
  private static float[] IDF;
  private static float[] docLength;
  
  private static void setFiles() {
    /* Set directory, data source */
    String bindir = Configuration.baseDir   + "/" + 
                    Configuration.binaryDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/";
    
    indexLocation = Configuration.baseDir   + "/" + 
                    Configuration.indexDir + "/" +
                    Configuration.type+ "/" +
                    Configuration.date+ "/";
    
    String data = Configuration.type + "-" +
                  Configuration.date + "-" +
                  Configuration.graph;
    
    graphBinaryFileName = bindir + data + ".wgp";
    outputGraphFileName = bindir + data + "-costext.wgp";
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/CreateMappings.xml");
    }
    
    setFiles();
    
    WikiGraph wg = null;
    System.out.println("Opening .wgp file.");
    try
    {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(graphBinaryFileName));
      wg = (WikiGraph) in.readObject();
      in.close();
    }//end: try {}
    catch(Exception e)
    {
      System.err.println("Problem reading from file: " + graphBinaryFileName);
      e.printStackTrace();
      System.exit(1);
    }

    Index theIndex = null;
    try
    {
      System.out.println("Opening Index");
      theIndex = IndexManager.openIndex(indexLocation);
      
      /* Set term Inverse Document Frequencies */
      System.out.println("Setting IDF Values");
      IDF = new float[theIndex.termCountUnique() + 1];
      for(int i = 1; i < IDF.length; i++)
      {
        IDF[i] = (float) Math.log( theIndex.docCount() / ((double)theIndex.docCount(i)) );
      }//end: for(i)
      
      /* Set Document Lengths */
      /* Document IDs are Vertex Numbers + 1*/
      System.out.println("Setting Document Lengths");
      docLength = new float[theIndex.docCount()];
      for(int i = 0; i < docLength.length; i++)
      {
        // Get list of terms
        TermInfoList til = theIndex.termInfoList(i+1);
        int length = theIndex.docLength(i+1);
        til.startIteration();
        while(til.hasMore()) 
        {
          TermInfo ti = til.nextEntry();
          
          // Find the TF
          double d = (ti.count() / (double) length);
          
          // Multiply by IDF
          d = d * IDF[ti.termID()];
          
          // Square
          d = Math.pow(d, 2.0);
          
          // Sum
          docLength[i] += (float) d;
        }//end: while(til)
        
        // Square Root of the sum
        docLength[i] = (float) Math.sqrt(docLength[i]);
        if(docLength[i] <= 0)
        {
          System.err.println("Blank document");
        }
      }//end: for(i)
    } //end: try {}
    catch (Exception e)
    {
      System.err.println("Problem with the index location: " + indexLocation);
      e.printStackTrace();
      System.exit(1);
    }
   
    try
    {
      System.out.println("Setting Edge Weights");
      int numVertices = wg.getNumVertices();
      
      for(int fromVertex = 0; fromVertex < numVertices; fromVertex++) {
        int[] toVertices = wg.getOutboundLinks(fromVertex);
        
        // Check for valid out-bound links
        if(toVertices != null)
        {
          float[] transitions = new float[toVertices.length];
          for(int j = 0; j < transitions.length; j++)
          {
            transitions[j] = getCOSMeasure(fromVertex, toVertices[j], theIndex);            
          }//end: for(j)
          
          // Normalize transition probabilities
          normalize(transitions);
          
          // Set transition probabilities for the vertex
          wg.setOutboundTransitions(fromVertex, transitions);
        }//end: if(!null)
        
      }//end: for(fromVertex)
      
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputGraphFileName));
      WikiGraph tmp = new WikiGraph(wg);
      oos.writeObject(tmp);
      oos.close();
    } //end: try {}
    catch (IOException e)
    {
      System.err.println("Problem with file: " + outputGraphFileName);
      e.printStackTrace();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
 /**
  * Calculates the Cosine similarity measure between two vertices.
  * <p>
  * Make sure you are sure about when to use the vertex number and
  * when to use the Lemur document index number.
  *  
  * @param fromVertex Graph 'from' vertex
  * @param toVertex Graph 'to' vertex
  * @param theIndex Lemur index
  * @return Cosine Similarity measure
  */
  private static float getCOSMeasure(int fromVertex, int toVertex, Index theIndex)
  {
    float measure = (float) 0.0;
    try {
      TreeMap<Integer,Double> fromWords = new TreeMap<Integer,Double>();
      TermInfoList tilF = theIndex.termInfoList(fromVertex+1);
      int documentLength = theIndex.docLength(fromVertex+1);
      
      tilF.startIteration();
      while(tilF.hasMore()) 
      {
        TermInfo ti = tilF.nextEntry();
        fromWords.put(ti.termID(), (ti.count()/(double)documentLength));
      }//end: while(tilF)
      
      TermInfoList tilT = theIndex.termInfoList(toVertex+1);
      documentLength = theIndex.docLength(toVertex+1);
      
      tilT.startIteration();
      while(tilT.hasMore())
      {
        TermInfo ti = tilT.nextEntry();
        if(fromWords.containsKey(ti.termID()))
        {
          measure += (float) (ti.count()/(double) documentLength) * IDF[ti.termID()] * fromWords.get(ti.termID()) * IDF[ti.termID()];
        }//end: if(fromWords)
      }//end: while(tilT)
      
      measure = measure / (docLength[fromVertex] * docLength[toVertex]);
    } //end: try {}
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    return measure;
  }
  
 /**
  * Normalize to a transition probability.
  * 
  * @param vals Array of transition values.
  */
  private static void normalize(float[] vals)
  {
    float sum = (float) 0.0;
    
    for(int i = 0; i < vals.length; i++)
    {
      sum += vals[i];
    }
    
    for(int i = 0; i < vals.length; i++)
    {
      vals[i] = vals[i] / sum;
    }
    
    /* Invalid case
     * 
     * Normalize values to uniform distribution
     */
    if(sum == Float.NaN || sum == 0)
    {
      for(int i = 0; i < vals.length; i++)
      {
        vals[i] = (float) 1.0 / vals.length;
      }//end: for(i)
    }//end: if(sum)
  }//end: normalize(float[])
}
