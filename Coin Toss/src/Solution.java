import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * 		Coin Toss (Interviewstreet)
 * 
 * 		Author: Draco Li
 * 		  Date: March 19, 2012
 * 	   Website: http://www.dracoli.com
 * 
 */

public class Solution {
	
	/**
	 * result = 2^(n+1) - 2^(m+1)
	 */
	public static BigInteger solve(int n, int m) {
		BigInteger result = BigInteger.valueOf(2).pow(n+1);
		result = result.subtract(BigInteger.valueOf(2).pow(m+1));
		return result;
	}
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("tests/input03.txt"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		//scanner = new Scanner(System.in);
		int totalInputs = scanner.nextInt();
		while (totalInputs-- > 0) {
			int n = scanner.nextInt();
			int m = scanner.nextInt();
			System.out.println(solve(n, m) + ".00");
		}
	}
}
