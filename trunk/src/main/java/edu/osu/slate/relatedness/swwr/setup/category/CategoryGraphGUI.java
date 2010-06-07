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

package edu.osu.slate.relatedness.swwr.setup.category;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.*;

import edu.osu.slate.relatedness.Configuration;
import edu.osu.slate.relatedness.swwr.data.category.CategoryIDGraph;
import edu.osu.slate.relatedness.swwr.data.category.CategoryIDNode;
import edu.osu.slate.relatedness.swwr.data.category.CategoryTitleToIDTranslation;
import edu.osu.slate.relatedness.swwr.data.category.IDToCategoryTitleTranslation;

public class CategoryGraphGUI {

  private JFrame window;
  private JPanel infoPanel;
  private JPanel parentsPanel;
  private JPanel childrenPanel;
  
  /* Translates category titles to category IDs */
  private CategoryTitleToIDTranslation Cat2ID;
  
  /* Translates category IDs to category titles */
  private IDToCategoryTitleTranslation ID2Cat;
  
  public CategoryGraphGUI(String filename)
  {
    Configuration.parseConfigurationFile(filename);
    String cgraphFileName = Configuration.baseDir + "/" +
                            Configuration.binaryDir + "/" +
                            Configuration.type  + "/" +
                            Configuration.date  + "/" +
                            Configuration.type  + "-" +
                            Configuration.date  + "-" +
                            Configuration.graph + ".cgraph";
    /* Open .cgraph file */
    System.out.println("Read .cgraph file.");
    CategoryIDGraph catGraph = null;
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(cgraphFileName));
      catGraph = (CategoryIDGraph) in.readObject();
      in.close();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file " + cgraphFileName);
      e.printStackTrace();
      System.exit(1);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    
    String cidFileName = Configuration.baseDir + "/" +
                         Configuration.binaryDir + "/" +
                         Configuration.type+ "/" +
                         Configuration.date+ "/" +
                         Configuration.type+ "-" +Configuration.date +
                         "-page.cid";
    /* Open input file */
    ObjectInputStream in = null;
    try {
      in = new ObjectInputStream(new FileInputStream(cidFileName));       
      System.out.println("Opening .cid file.");
      Cat2ID = (CategoryTitleToIDTranslation)in.readObject();
      ID2Cat = (IDToCategoryTitleTranslation)in.readObject();
      in.close();
    }
    catch(IOException e)
    {
      System.err.println("Problem opening input file: " + cidFileName);
      System.exit(1);
    }
    catch (ClassNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    }
    
    CategoryIDNode cn = catGraph.getRoot();
    buildPanel(cn);
  }
  
  private void buildPanel(CategoryIDNode cn)
  {
    window = new JFrame();
    window.setTitle("Category Viewer");
    window.setLayout(new BorderLayout());
    
    JLabel titleMessage = new JLabel(ID2Cat.getTitle(cn.getCategoryID())[0] + "\n");
    JLabel idMessage = new JLabel("Category ID: " + cn.getCategoryID() + "\n");
    JLabel vertexMessage = new JLabel("Vertex Count: " + cn.getVertexCoverage());
    JLabel inboundMessage = new JLabel("Inbound Edge Count: " + cn.getInboundEdgeCoverage());
    JLabel outboundMessage = new JLabel("Outbound Edge Count: " + cn.getOutboundEdgeCoverage());
    infoPanel = new JPanel();
    infoPanel.setLayout(new GridLayout(5,1));
    infoPanel.add(titleMessage);
    infoPanel.add(idMessage);
    infoPanel.add(vertexMessage);
    infoPanel.add(inboundMessage);
    infoPanel.add(outboundMessage);
    
    int[] parents = cn.getParentIDs();
    parentsPanel = new JPanel();
    if(parents != null)
    {
      // Create 'parent' buttons
      JButton[] buttons = new JButton[parents.length];
      for(int i=0; i<buttons.length; i++)
      {
        buttons[i] = new JButton(ID2Cat.getTitle(parents[i])[0]);
      }//end: for(i)
      
      for(int i=0; i<buttons.length; i++)
      {
        parentsPanel.add(buttons[i]);
      }//end: for(i)
    }
    else
    {
      JLabel noneMessage = new JLabel("--none--");
      parentsPanel.add(noneMessage);
    }
    
    int[] children = cn.getChildrenIDs();
    childrenPanel = new JPanel();
    if(children != null)
    {
      //Create 'children' buttons
      JButton[] buttons = new JButton[children.length];
      for(int i=0; i<buttons.length; i++)
      {
        buttons[i] = new JButton(ID2Cat.getTitle(children[i])[0]);
      }//end: for(i)
      for(int i=0; i<buttons.length; i++)
      {
        childrenPanel.add(buttons[i]);
      }//end: for(i)
    }
    else
    {
      JLabel noneMessage = new JLabel("--none--");
      childrenPanel.add(noneMessage);
    }
    
    window.add(infoPanel, BorderLayout.CENTER);
    window.add(parentsPanel,BorderLayout.NORTH);
    window.add(childrenPanel,BorderLayout.SOUTH);
    window.pack();
    window.setVisible(true);
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    CategoryGraphGUI cgg = new CategoryGraphGUI("/scratch/weale/data/config/enwiktionary/CreateMappings.xml");
  }

}
