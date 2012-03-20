/**
 * 		Picking Cards (Interviewstreet)
 * 
 * 		Author: Draco Li
 * 			Date: March 19, 2012
 * 	 Website: http://www.dracoli.com
 * 
 */

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Solution {
  ArrayList<Integer> samples; // List of cards

  public Solution(String[] sol) {
  	// Parses an array of string ints into an ArrayList of integers.
  	samples = new ArrayList<Integer>();
	  for (int i = 0; i < sol.length; i++) {
	  	samples.add(Integer.parseInt(sol[i]));
    }
	  
	  // Sort our cards for easy calculation
    Collections.sort(samples);
  }
  
  public String getAnswer() {
    BigInteger currentValue = new BigInteger("1");
    int currentSize = 0;
    int validCards = 0;
    for (int i = 0; i < samples.size(); i++) {
      validCards = findValids(currentSize, validCards - 1);
      validCards -= i;
      if (validCards <= 0) return "0";
      currentValue = currentValue.multiply(new BigInteger(Integer.toString(validCards)));
      currentValue = currentValue.mod(new BigInteger("1000000007")); // Scale value down, or else super slow!
      currentSize++;
    }
    return currentValue.toString();
  }
  
  /**
   * Find cards that are equal or larger than pivot.
   * Since cards are sorted, we can skip the first few cards that we know
   * 	are already valid for the previous pivot. 
   */
  public int findValids(int pivot, int start) {
	  int low = start, high = samples.size() - 1;
	  if (samples.get(high) <= pivot) return high + 1; // Check if all cards are valid
	  
	  // Find the last card that is smaller or equal to pivot 
	  while (low + 1 < high) {
	  	int mid = low + (high - low) / 2; 
		  if (samples.get(mid) <= pivot) {
			  low = mid;
		  } else {
			  high = mid;
		  }
	  }
	  return low + 1;
  }

  public static void main(String[] args) {
	  Scanner scanner = null;
	  try {
	  	File sourceFile = new File("input02.txt");
	  	scanner = new Scanner(sourceFile);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	  
    // Scanner scanner = new Scanner(System.in);
	  
	  // Scan inputs in and call solution for every card set
    int totalNum = Integer.parseInt(scanner.nextLine());
    while (totalNum > 0) {
    	int totalCards = Integer.parseInt(scanner.nextLine()); // not used...
    	String[] numbers = scanner.nextLine().split(" ");
    	Solution oneSolution = new Solution(numbers);
    	System.out.println(oneSolution.getAnswer());
      totalNum--;
    }
  }
}