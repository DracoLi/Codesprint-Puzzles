import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Set;

/**
 * Direct Connections
 *
 *  @author: dracoli
 *	  @date: Mar 23, 2012
 * @website: http://www.dracoli.com
 */

public class Solution {

	public static BigInteger MOD = new BigInteger("1000000007");
	
	// A BIT of cumulative distance of sorted distance array
	public static BigInteger[] disBIT;
	
	// A BIT of cumulative cities count of sorted distance array
	public static BigInteger[] cityCount;
	
	/**
	 * Brute froce method: O(n^2)
	 * Passes only about 2 tests.
	 */
	public static String solve(int[] pops, int[] dis) {
		int len = pops.length;
		BigInteger result = BigInteger.ZERO;
		for (int i = 0; i < len - 1; i++) {
			int popA = pops[i];
			int disA = dis[i];
			for (int j = i + 1; j < len; j++) {
				int popB = pops[j];
				int disB = dis[j];
				String cables = Integer.toString(Math.max(popA, popB));
				String distance = Integer.toString(Math.abs(disA - disB));
				BigInteger connection = new BigInteger(cables);
				connection = connection.multiply(new BigInteger(distance)).mod(MOD);
				result = result.add(connection).mod(MOD);
			}
		}
		return result.toString();
	}
	
	/**
	 * Solving the problem using Binary Indexed Tress
	 * Complexity = O(nlogn), which passes all tests under time limit
	 */
	public static String solve2(int[] pops, int[] dis) {
		int len = pops.length;
		
		// Initialize Binary Indexed Trees - start at index 1
		disBIT = new BigInteger[len+1];
		cityCount = new BigInteger[len+1]; 
		for (int i = 0; i < len + 1; i++) {
			disBIT[i] = BigInteger.ZERO;
			cityCount[i] = BigInteger.ZERO;
		}
		
		// Sort to ascending population
		CityData[] sortedPop = new CityData[len];
		for (int i = 0; i < len; i++) {
			sortedPop[i] = new CityData(i, pops[i], dis[i]);
		}
		Arrays.sort(sortedPop, new PopulationComparator());
		
		// Sort to ascending distance
		CityData[] sortedDis = new CityData[len];
		for (int i = 0; i < len; i++) {
			sortedDis[i] = new CityData(i, pops[i], dis[i]);
		}
		Arrays.sort(sortedDis, new DistanceComparator());
		
		// Create inverse-mapper for original index to sorted distance array's index
		// This is used to locate the city in the sorted distance array using just the city index.
		int[] distanceMapper = new int[sortedDis.length];
		for (int i = 0; i < sortedDis.length; i++) {
			distanceMapper[sortedDis[i].index] = i; 
		}
		
		// Populate BIT of cumulative distance
		for (int i = 1; i < len+1; i++) {
			update(disBIT, i, sortedDis[i-1].dis);
		}
		
		// Populate BIT of cumulative city count (according to distance)
		for (int i = 1; i < len+1; i++) {
			update(cityCount, i, 1);
		}
		
		BigInteger sum = BigInteger.ZERO;
		
		// Loop from largest population to smallest. Loop n times
		for (int index = len - 1; index >= 0; index--) {
			
			// Get targeted city's index in distance array
			int cityIndex = sortedPop[index].index;
			int disIndex = distanceMapper[cityIndex];
			
			// Some city variable
			BigInteger citiesRemain = new BigInteger(Integer.toString(index + 1));
			BigInteger cityPop = new BigInteger(Integer.toString(sortedPop[index].pop));
			BigInteger cityDis = new BigInteger(Integer.toString(sortedDis[disIndex].dis)); 
			BigInteger citiesLeft = read(cityCount, disIndex);
			BigInteger citiesRight = citiesRemain.subtract(citiesLeft.add(BigInteger.ONE));
			
			// Some cumulative distance variables
			BigInteger cumLeft = read(disBIT, disIndex);
			BigInteger cumRight = read(disBIT, sortedDis.length).subtract(cumLeft.add(cityDis));
			
			// Find cumulative distance to left of city - ignoring zero cities
			if (citiesLeft.intValue() > 0) {
				BigInteger temp = cityDis.multiply(citiesLeft).subtract(cumLeft);
				sum = sum.add(temp.multiply(cityPop)).mod(MOD);
			}
			
			// Find cumulative distance to right of city - ignoring zero cities
			if (citiesRight.intValue() > 0) {
				BigInteger temp = cumRight.subtract(cityDis.multiply(citiesRight));
				sum = sum.add(temp.multiply(cityPop)).mod(MOD);
			}
			
			// Make current city distance to zero by negating its distance value
			disBIT = update(disBIT, disIndex + 1, sortedDis[disIndex].dis * -1);
			
			// Make current city count to zero
			cityCount = update(cityCount, disIndex + 1, -1);
		}
		
		return sum.toString();
	}
	
	public static int[] stringArrayToIntArray(String[] inputs) {
		int[] results = new int[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			results[i] = Integer.parseInt(inputs[i]);
		}
		return results;
	}
	
	/*
	 * BIT update function
	 * Complexity = O(logn)
	 */
	public static BigInteger[] update(BigInteger[] data, int idx, int val) {
		while (idx < data.length) {
			data[idx] = data[idx].add(new BigInteger(Integer.toString(val))).mod(MOD);
			idx += (idx & -idx);
		}
		return data;
	}
	
	
	/*
	 * BIT read function.
	 * Complexity = O(logn)
	 */
	public static BigInteger read(BigInteger[] data, int idx) {
		BigInteger sum = BigInteger.ZERO;
		while (idx > 0) {
			sum = sum.add(data[idx]).mod(MOD);
			idx -= (idx & -idx);
		}
		return sum;
	}
	
	/**
	 * This stores all the inputs regarding a city.
	 * We also provide two comparator classes to sort by both distance and population
	 */
	public static class CityData {
		int index;
		int pop;
		int dis;
		
		public CityData(int index, int pop, int dis) {
			this.index = index;
			this.dis = dis;
			this.pop = pop;
		}
	}
	public static class PopulationComparator implements Comparator<CityData> {
    public int compare(CityData o1, CityData o2) {
	    if (o1.pop > o2.pop) return 1;
	    if (o1.pop < o2.pop) return -1;
	    return 0;
    }
	}
	public static class DistanceComparator implements Comparator<CityData> {
    public int compare(CityData o1, CityData o2) {
	    if (o1.dis > o2.dis) return 1;
	    if (o1.dis < o2.dis) return -1;
	    return 0;
    }
	}
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("tests/input09.txt"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// scanner = new Scanner(System.in);
		
		int scenerios = scanner.nextInt();
		while (scenerios-- > 0) {
			int towns = scanner.nextInt();
			String popRow = scanner.nextLine();
			popRow = scanner.nextLine();
			int[] distances= stringArrayToIntArray(popRow.split(" "));
			int[] populations  = stringArrayToIntArray(scanner.nextLine().split(" "));
			System.out.println(solve2(populations, distances));
		}
	}
}