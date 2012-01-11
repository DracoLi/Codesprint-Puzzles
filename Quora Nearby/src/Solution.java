import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

public class Solution {
	
	// Story in simple array since we know the size
	private Question[] questions;
	private Topic[] topics; 
	
	public Solution(int topicsCount, int quesCount) {
		topics = new Topic[topicsCount];
		questions = new Question[quesCount];
	}
	
	public String query(String type, int amount, Location loc) {
		
		// A FIFO queue to store  our best results
		LinkedList<Integer> results = new LinkedList<Integer>();
		
		// Sort topics according to distance to query location
		Arrays.sort(topics, new DistanceComparator(loc));
		
		if (type.equals("t")) {
			// Just return top few topics
			for (int i = 0; i < amount; i++) {
				results.add(topics[i].id());
			}
		} else if (type.equals("q")) {
			
			// Loop through questions of the first few topics
			int count = 0, topicsIndex = 0;;
			while (count < amount && topicsIndex < topics.length) {
				
				// Get the questions of the nearest topics
				ArrayList<Integer> goodQuestions = (ArrayList<Integer>)topics[topicsIndex].getSortedQuestions().clone();
				
				// Check if other topic has same distance as this one
				// If so we need to combine them to our goodQuestions
				boolean collectionChanged = false;
				while (topicsIndex + 1 < topics.length && 
					   topics[topicsIndex].loc().distanceTo(loc) == 
					   topics[topicsIndex + 1].loc().distanceTo(loc)) {
					goodQuestions.addAll(topics[topicsIndex + 1].getSortedQuestions());
					topicsIndex++;
					collectionChanged = true;
				}
				
				// Check if we exhausted topics. If we do then we only only break out
				if (topicsIndex == topics.length) break;
				
				// If we had to add more questions, then we need sort
				if (collectionChanged) {
					Collections.sort(goodQuestions);
				}
				
				// This parts adds goodQuestions to our results
				int totalQs = goodQuestions.size();
				for (int i = totalQs - 1; i >= 0; i--) {
					// Adding the largest id first if we haven't already
					if (!results.contains(goodQuestions.get(i))) {
						results.add(goodQuestions.get(i));
						count++;
						
						if (count == amount) break;
					}
				}
				topicsIndex++;
				// Do next topic
			}
		}
		
		return parseResults(results);
	}
	
	public String query(String rawData) {
		String[] data = rawData.split(" ");
		Location loc = new Location(Double.parseDouble(data[2]), Double.parseDouble(data[3]));
		return query(data[0], Integer.parseInt(data[1]), loc);
	}
	
	public void addTopic(String rawTopic) {
		String[] data = rawTopic.split(" ");
		Topic newTopic = new Topic(Integer.parseInt(data[0]),
								   Double.parseDouble(data[1]), 
								   Double.parseDouble(data[2]));
		topics[newTopic.id()] = newTopic;
		
	}
	
	public void addQuestion(String rawQues) {
		String[] data = rawQues.split(" ");
		int questionId = Integer.parseInt(data[0]);
		int questionTopicsCount = Integer.parseInt(data[1]);
		
		if (questionTopicsCount == 0) return; // Skip questions without a topic
		
		// Get a topics array from question
		int[] topics = new int[questionTopicsCount];
		for (int i = 2; i < data.length; i++) {
			int topicID = Integer.parseInt(data[i]);
			topics[i-2] = topicID;
			
			// Insert this question to its corresponding topic
			this.topics[topicID].addQuestionId(questionId);
		}
		
		// Make the quesiton object
		Question newQuestion = new Question(questionId, topics);
		questions[newQuestion.id()] = newQuestion;
	}
	
	public String parseResults(LinkedList<Integer> data) {
		StringBuilder sb = new StringBuilder();
		
		int count = 0;
		for (int oneData : data) {
			count++;
			sb.append(oneData);
			if (count != data.size()) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	class DistanceComparator implements Comparator<Topic> {
		private Location source;
		
		public DistanceComparator(Location sourceLoc) {
			this.source = sourceLoc;
		}
		
		public int compare(Topic o1, Topic o2) {
			double distance1 = o1.loc().distanceTo(source);
			double distance2 = o2.loc().distanceTo(source);
			if 		(distance1 > distance2)	return 1;
			else if (distance1 < distance2) return -1;
			else 							return (o1.id() > o2.id()) ? -1 : 1;
		}
	}
	
	public static void main(String[] args) {
		/*
		Scanner scanner = null;
		try {
			File sourceFile = new File("input00.txt");
			scanner = new Scanner(sourceFile);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		*/
		Scanner scanner = new Scanner(System.in);
		String[] initData = scanner.nextLine().split(" ");
		int T = Integer.parseInt(initData[0]);
		int Q = Integer.parseInt(initData[1]);
		int N = Integer.parseInt(initData[2]);
		
		Solution dracoRocks = new Solution(T, Q);
		
		int topicsCount = 0, questionsCount = 0, queryCount = 0;
		while (scanner.hasNextLine()) {
			String data = scanner.nextLine();
			if (topicsCount < T) {
				dracoRocks.addTopic(data);
				topicsCount++;
			} else if (questionsCount < Q) {
				dracoRocks.addQuestion(data);
				questionsCount++;
			} else if (queryCount < N) {
				System.out.println(dracoRocks.query(data));
				queryCount++;
			}
		}
	}
}

class Topic {
	private ArrayList<Integer> questions; // TODO: Change to max priority queue for O(1)
	private int id;
	private Location loc;
	private boolean isSorted;
	
	public Topic(int id, double lat, double lon) {
		this.id = id;
		this.loc = new Location(lat, lon);
		questions = new ArrayList<Integer>();
		isSorted = true;
	}
	
	public void addQuestion(Question ques) {
		addQuestionId(ques.id());
	}
	
	public void addQuestionId(int id) {
		isSorted = false;
		questions.add(id);
	}
	
	public int id() {
		return id;
	}
	
	public Location loc() {
		return this.loc;
	}
	
	public ArrayList<Integer> getSortedQuestions() {
		if (!isSorted) {
			isSorted = true;
			Collections.sort(questions);
		}
		return questions;
	}
}

class Question {
	private ArrayList<Integer> topics;
	private int id;
	public Question(int id, int[] source) {
		topics = new ArrayList<Integer>();
		for (int i = 0; i < source.length; i++) {
			topics.add(source[i]);
		}
		this.id = id;
	}
	
	public ArrayList<Integer> getTopics() {
		return topics;
	}
	
	public int id() {
		return this.id;
	}
}

class Location {
	private double lon;
	private double lat;
	
	public Location(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public double distanceTo(Location loc) {
		double latDistance = Math.abs(lat - loc.lat);
		double lonDistance = Math.abs(lon - loc.lon);
		
		// Simple cases
		if (lonDistance == 0 && latDistance == 0) {
			return 0;
		} else if (latDistance == 0) {
			return lonDistance;
		} else if (lonDistance == 0) {
			return latDistance;
		}
		
		// Elementary math
		double sum = Math.pow(latDistance, 2.0) + Math.pow(lonDistance, 2.0);
		return Math.sqrt(sum);
	}
}