package edu.osu.slate.experiments.synonym;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.osu.slate.relatedness.Configuration;

public class SplitFiles {

  private static String taskFile, task;
  
  private static void setFiles()
  {
    taskFile = Configuration.taskDir + task;
  }
  
  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException
  {
    if(args.length == 1)
    {
      Configuration.parseConfigurationFile(args[0]);
    }
    else
    {
      Configuration.parseConfigurationFile("/scratch/weale/data/config/enwiktionary/WordPair.xml");
    }
    
    String[] tasks = {"ESL", "TOEFL", "RDWP300", "RDWP1K"};
    int[] splits = {3,5,16,16};
    
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      task = tasks[currTask];
      setFiles();
    
      Scanner s = new Scanner(new FileReader(taskFile + ".txt"));
      int numLines = 0;
      while(s.hasNext())
      {
        s.nextLine();
        numLines++;
      }
      s.close();
      
      s = new Scanner(new FileReader(taskFile + ".txt"));
      numLines = numLines / splits[currTask];
      for(int i = 0; i < splits[currTask] - 1; i++)
      {
        PrintWriter pw = new PrintWriter(taskFile + ".part" + i);
        for(int j = 0; j < numLines; j++)
        {
          pw.println(s.nextLine());
        }//end: for(j)
        pw.close();
      }//end: for(i)
      
      PrintWriter pw = new PrintWriter(taskFile + ".part" + (splits[currTask]-1));
      while(s.hasNext())
      {
        pw.println(s.nextLine());
      }//end: while(s.hasNext)
      pw.close();
    }//end: for(currTask)
  }//end: main()
}
