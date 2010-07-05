package edu.osu.slate.experiments.dissertation.chapter5;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class RemoveOne {

  /**
   * @param args
   * @throws FileNotFoundException 
   */
  public static void main(String[] args) throws FileNotFoundException
  {
    String rmOne = "/u/weale/results/removeone/enwiktionary/";
    String data = "/u/weale/data/wordpair/";
    
    String[] tasks = {"MC30", "RG65", "WS1", "WS2", "YP130"};
    
    for(int currTask = 0; currTask < tasks.length; currTask++)
    {
      Scanner s1 = new Scanner(new FileReader(rmOne + tasks[currTask] + ".txt"));
      Scanner s2 = new Scanner(new FileReader(data + tasks[currTask] + ".txt"));
      TreeMap<Double,String> tm = new TreeMap<Double,String>();
      
      while(s1.hasNext())
      {
        double d = s1.nextDouble();
        double d2 = s1.nextDouble();
        String s = s2.nextLine() + "," + d2;
        tm.put(d,s);
      }
      s1.close();
      s2.close();
      Set<Map.Entry<Double,String>> set = tm.entrySet();
      Iterator<Map.Entry<Double,String>> it = set.iterator();
      int i = 0;
      while(it.hasNext())
      {
        Map.Entry<Double,String> me = it.next();
        if(i == tm.size()-10)
          System.out.println(tasks[currTask]);
        
        if(i >= tm.size()-10)
          System.out.println(me.getValue() + "\t" + me.getKey());
        i++;
      }
    }
  }

}
