package edu.osu.slate.relatedness.swwr.data;

import java.util.TreeMap;

import lemurproject.lemur.*;

/**
 * Compares the text vectors of two wiki pages.
 * 
 * @author weale
 *
 */
public class TextVectorComparison
{
  /* Inverse document frequency for words */
  private float[] IDF;
  
  /* Document vector lengths */
  private float[] docLength;
  
  /* Lemur index */
  private Index theIndex;
  
 /**
  * Constructor.
  *  
  * @param indexLocation
  */
  public TextVectorComparison(String indexLocation)
  {
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
  }//end: TextVectorComparison()
  
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
   public float getCOSMeasure(int fromVertex, int toVertex)
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
           float to = (float) (ti.count()/(double) documentLength) * IDF[ti.termID()];
           float from = fromWords.get(ti.termID()).floatValue() * IDF[ti.termID()];
           measure += (float) to * from;
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
   }//end: getCOSMeasure(int, int)
}
