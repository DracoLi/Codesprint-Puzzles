/**
 * Newsle Clusters
 * 
 * @author Draco Li
 */

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;


public class Solution {
	HashMap<Integer, Article> articles;
	
	public Solution() {
		articles = new HashMap<Integer, Article>();
	}
	
	public static void main(String[] args) {
		Scanner scanner = null;
		try {
			File sourceFile = new File("input00.txt");
			scanner = new Scanner(sourceFile);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		//Scanner scanner = new Scanner(System.in);
		int ordersCount = Integer.parseInt(scanner.nextLine());
		
		Solution fraudDetector = new Solution();
		int currentOrder = 0;
		while (scanner.hasNextLine()) {
			
		}
	}
}

class Article {
	private HashSet<String> keywords;
	private int id;
	private String title;
	private String content;
	
	public Article(int id, String title, String content) {
		this.id = id;
		this.title = title;
		this.content = content;
		
		scanKeyWords();
	}
	
	private void scanKeyWords() {
		// Simple scanner
		
		// Advanced scanner
	}
}