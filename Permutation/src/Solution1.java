import java.io.File;
import java.util.Random;
import java.util.Scanner;

/**
 * Permutation
 *
 *  @author: dracoli
 *	  @date: Mar 20, 2012
 * @website: http://www.dracoli.com
 */
public class Solution1 {

	public static String solve(int[][] values, int size) {
		int[] arrayHelper = new int[50];
		for (int i = 0; i < 50; i++) {
			arrayHelper[i] = i;
		}
		
		int max[] = new int[size];
		int maxValue = 0; 
		Random rand = new Random();
		
		// Test random permutation sizes
		for (int i = 0; i < 20000000; i++) {
			int permValue = getValues(values, arrayHelper, size);
			if (permValue > maxValue) {
				// Copy this permutation to our max
				for (int j = 0; j < size; j++) {
					max[j] = arrayHelper[j];
					maxValue = permValue;
				}
			}

			// Generate a random permutation
			int x = rand.nextInt(size);
			int y = rand.nextInt(size);
			int temp = arrayHelper[x];
			arrayHelper[x] = arrayHelper[y];
			arrayHelper[y] = temp;
		}
		
		// Return permutation in a formated string
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < max.length - 1; i++) {
			sb.append(max[i]);
			sb.append(" ");
		}
		sb.append(max[max.length-1]);
		return sb.toString();
	}
	
	/**
	 * Get the total value of our permutations
	 */
	public static int getValues(int[][] values, int[] arrayHelper, int size) {
		int finalValue = 0;
		for (int i = 0; i < size - 1; i++) {
			finalValue += values[arrayHelper[i]][arrayHelper[i+1]];
		}
		return finalValue;
	}
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("tests/input00.txt"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// scanner = new Scanner(System.in);
		
		// Scan in the matrix
		int total = scanner.nextInt();
		int[][] values = new int[total][total];
		for (int i = 0; i < total; i++) {
			for (int j = 0; j < total; j++) {
				values[i][j] = scanner.nextInt();
			}
		}
		System.out.println(solve(values, total));
	}
}
