import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

/**
 * Direct Connections
 *
 *  @author: dracoli
 *	  @date: Mar 20, 2012
 * @website: http://www.dracoli.com
 */

public class Solution {

	public static BigInteger MOD = new BigInteger("1000000007");
	
	/**
	 * O(n^2)
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
	/*
	public static String solve2(int[] pops, int[] dis) {
		ArrayList<ArrayList<Integer>> connections = new ArrayList<ArrayList<Integer>>();
		
		int len = pops.length;
		for (int i = 0; i < len; i++) {
			connections.add(new ArrayList<Integer>());
		}
	}
	*/
	public static int[] stringArrayToIntArray(String[] inputs) {
		int[] results = new int[inputs.length];
		for (int i = 0; i < inputs.length; i++) {
			results[i] = Integer.parseInt(inputs[i]);
		}
		return results;
	}
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("tests/input06.txt"));
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
			System.out.println(solve(populations, distances));
		}
	}
}