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

package edu.osu.slate.relatedness.swwr.data.graph;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.AliasSFToID;
import edu.osu.slate.relatedness.swwr.data.AliasStrings;
import edu.osu.slate.relatedness.swwr.data.ConvertIDToTitle;
import edu.osu.slate.relatedness.swwr.data.ConvertTitleToID;

public class WikiGraphGUI extends JFrame
{

  private JPanel IDPanel;
  private JTable Table;
  private JScrollPane IDScroll;

  private static int[] validList;

  private static String graphFile;

  private static String tidFile;

  private static String vidFile;
  
  /* Translates category titles to category IDs */
  private static ConvertTitleToID Title2ID;
  
  /* Translates category IDs to category titles */
  private static ConvertIDToTitle ID2Title;
  
  private static IDVertexTranslation ID2Vertex;
  
  public WikiGraphGUI(int[][] edgeInfo, float[] edgeWeights)
  {
    setTitle("");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    Object[][] data = new Object[edgeInfo.length][3];
    for(int i=0; i<edgeInfo.length; i++) {
      String fromTitle = "--undef--";
      String toTitle = "--undef--";
      try
      {
        fromTitle = new String(ID2Title.getTitle(ID2Vertex.getID(edgeInfo[i][0])));
        toTitle = new String(ID2Title.getTitle(ID2Vertex.getID(edgeInfo[i][1])));
      }
      catch(Exception e) {}
      
      data[i][0] = fromTitle;
      data[i][1] = toTitle;
      data[i][2] = new Float(edgeWeights[i]);
    }

    String[] cn = {"From", "To", "Weight"};
    buildPanel(data, cn);
    add(IDPanel);
    pack();
    setVisible(true);
  }

  private void buildPanel(Object[][] data, String[] columnNames)
  {
    IDPanel = new JPanel();
    Table = new JTable(data, columnNames);
    IDScroll = new JScrollPane(Table);
    IDPanel.add(IDScroll);
  }

 /**
  * @param args
  * @throws Exception 
  * @throws IOException 
  */
  public static void main(String[] args) throws IOException, Exception
  {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/CreateMappings.xml");
    }
    setFiles();

    System.out.println("Opening Wiki Graph.");
    WikiGraph wg = null;
    try
    {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(graphFile));
      wg = (WikiGraph) in.readObject();
      in.close();
      wg.setEdgeCount();
    }//end: try {}
    catch(Exception e)
    {
      System.err.println("Problem reading from graph file: " + graphFile);
      e.printStackTrace();
      System.exit(1);
    }
    
    /* Open input file */
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(new FileInputStream(tidFile));       
      System.out.println("Opening .tid file.");
      Title2ID = (ConvertTitleToID)in.readObject();
      ID2Title = (ConvertIDToTitle)in.readObject();
      in.close();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file: " + tidFile);
      System.exit(1);
    }
    
    System.out.println("Opening .vid file.");
    try {
      in = new ObjectInputStream(new FileInputStream(vidFile));       
      ID2Vertex = (IDVertexTranslation) in.readObject();
      in.close();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file: " + vidFile);
      System.exit(1);
    }
    
    int edges = wg.getNumEdges();
    int[][] edgeInfo = new int[edges][2];
    float[] edgeWeight = new float[edges];
    int currEdge = 0;
    for(int i = 0; i < wg.getNumVertices(); i++)
    {
      int[] arr = wg.getOutboundLinks(i);
      float[] fArr = wg.getOutboundTransitions(i);
      for(int j = 0; arr != null && j<arr.length; j++)
      {
        edgeInfo[currEdge][0] = i;
        edgeInfo[currEdge][1] = arr[j];
        edgeWeight[currEdge] = fArr[j];
        currEdge++;
      }//end: for(j)

      float sum = 0;
      for(int j = 0; fArr != null && j < fArr.length; j++)
      {
        sum+=fArr[j];
      }//end: for(j)
      
      if(Float.isNaN(sum))
      {
        System.out.println(i + "\t" + sum);
      }//end: if()
    }//end: for(i)

    WikiGraphGUI v = new WikiGraphGUI(edgeInfo, edgeWeight);
  }//end: main()
  
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
  private static void setFiles()
  {
    /* Set directory, data source */
    String dir = Configuration.baseDir + "/" +
    Configuration.binaryDir + "/" +
    Configuration.type+ "/" +
    Configuration.date+ "/";
    String data = Configuration.type + "-" +
    Configuration.date + "-" + 
    Configuration.graph;
    
    tidFile = dir + data + ".tid";
    graphFile = dir + data + "-costext.wgp";
    //graphFile = dir + data + ".wgp";
    vidFile = dir + data + ".vid";
  }
}
