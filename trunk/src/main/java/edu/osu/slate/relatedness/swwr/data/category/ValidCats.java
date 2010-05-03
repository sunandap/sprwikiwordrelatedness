package edu.osu.slate.relatedness.swwr.data.category;

import java.io.*;
import java.util.*;

/**
 * This class is used to contain a list of the valid pages found in the graph.
 * <p>
 * File input is a simple list of valid page numbers.
 * <p>
 * 
 * @author weale
 * @version 1.0; alpha
 * 
 */
public class ValidCats {

	/**
	 * Integer array containing the values of the valid ids in the graph.
	 * The position of the ids in the array correspond to their 'compressed' id.
	 * 
	 */
	private int[] validList;
	
	/**
	 * Given the name of the file, this constructor parses the file and stores the valid ids for future use.
	 * 
	 * @param filename
	 */
	public ValidCats(String filename) {
		try {
			ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(filename));
			validList = (int[]) fileIn.readObject();
			fileIn.close();
			
			//Ensure a sorted list
			Arrays.sort(validList);
			
		} catch (ClassNotFoundException e) {
			System.err.println("Problem converting to an integer array: " + filename);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filename);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Problem reading from file: " + filename);
			e.printStackTrace();
		}
	}//end: ValidCats(String)
	
	/**
	 * Gets the number of valid IDs.
	 * 
	 * @return number of valid IDs
	 */
	public int numValidIDs() {
		return validList.length;
	}//end: numValidIDs()
	
	/**
	 * This method takes a non-'compressed' ID value and returns whether or not that id was considered a 'valid' id in the graph.
	 * 
	 * @param id non-'compressed' ID value
	 * @return boolean value corresponding to whether or not the id is classifies as 'valid'
	 */
	public boolean isValidID(int id) {
		return (Arrays.binarySearch(validList, id) >= 0);
	}//end: isValidID(int)
	
	/**
	 * This method takes a non-'compressed' ID value and returns the 'compressed' ID value.
	 * <p>
	 * If the ID is valid, the method will return a positive number (>=0).  Invalid IDs return a negative value (>0).
	 * 
	 * @param id non-'compressed' ID value
	 * @return integer value of the 'compressed' ID value
	 */
	public int getCompressedID(int id) {
		return Arrays.binarySearch(validList, id);
	}//end: getCompressedID(int)
	
	/**
	 * Converts the 'compressed' ID value to a non-'compressed' ID value
	 * <p>
	 * If the compressed ID value is within range, it will return a positive number.  Otherwise, a -1 is returned.
	 * 
	 * @param id 'compressed' ID value
	 * @return non-'compressed' ID value
	 */
	public int getOriginalID(int id) {
		if(id > -1 || id < validList.length) {
			return validList[id];
		} else {
			return -1;
		}
	}//end: getOriginalID(int)
}
