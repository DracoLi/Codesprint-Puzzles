import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Interviewstreet Fraud Prevention problem
 * @author dracoli
 */
public class Solution {
	// Hash map allows constant search time
	private HashMap<Integer, ArrayList<Order>> orders;
	
	public Solution() {
		// Ideally stored in a database!
		orders = new HashMap<Integer, ArrayList<Order>>();
	}
	
	public void addOrder(Order order) {
		if (orders.containsKey(order.dealid)) {
			// Add to existing dealid
			orders.get(order.dealid).add(order);
		} else {
			// Add new list to dealset
			ArrayList<Order> newList = new ArrayList<Order>();
			newList.add(order);
			orders.put(order.dealid, newList);
		}
	}
	
	public HashMap<Integer, Order> getFrauds() {
		// Integer = orderid, order = fraud order
		HashMap<Integer, Order> fraudList = new HashMap<Integer, Order>();
		
		// Gets all the deals available
		Set<Integer> dealsSet = orders.keySet();
		
		// Loop through all the deal sets to find frauds
		for (int dealid : dealsSet) {
			ArrayList<Order> ordersForDeal = orders.get(dealid);
			
			// Look through one deal set's deals
			// Only search for deals ahead of current.
			int orderIndex = 0;
			for (Order sourceOrder : ordersForDeal) {
				// Check if this order is similar to other deals ahead of it
				for (int i = orderIndex + 1; i < ordersForDeal.size(); i++) {
					Order targetOrder = ordersForDeal.get(i); // O(1) get
					if (fraudList.containsKey((targetOrder.orderid))) continue; // Already a fraud!
					if (targetOrder.isSimiliar(sourceOrder)) {
						if (!fraudList.containsKey(sourceOrder)) {
							// Source as fraud only once
							fraudList.put(sourceOrder.orderid, sourceOrder);
						}
						fraudList.put(targetOrder.orderid, targetOrder);
					}
				}
				orderIndex++;
			}
		}
		
		return fraudList;
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
			String[] orderStr = scanner.nextLine().split(",");
			Order oneOrder = new Order();
			oneOrder.orderid = Integer.parseInt(orderStr[0]);
			oneOrder.dealid = Integer.parseInt(orderStr[1]);
			oneOrder.email = orderStr[2];
			oneOrder.addr = orderStr[3];
			oneOrder.city = orderStr[4];
			oneOrder.state = orderStr[5];
			oneOrder.zipcode = orderStr[6];
			oneOrder.credit = orderStr[7];
			
			fraudDetector.addOrder(oneOrder);
			
			currentOrder++;
			if (currentOrder == ordersCount) {
				break;
			}
		}
		
		HashMap<Integer, Order> frauds = fraudDetector.getFrauds();
		
		// Print out frauds in a nice format!
		StringBuilder sb = new StringBuilder();
		int index = 0;
		Set<Integer> fraudIds = frauds.keySet();
		ArrayList<Integer> fraudlist = new ArrayList(fraudIds);     
		Collections.sort(fraudlist);
		for (Integer id : fraudlist) {
			sb.append(id);
			// Avoid appending to last fraud id
			if (index < (fraudIds.size() - 1)) {
				sb.append(",");
			}
			index++;
		}
		System.out.println(sb.toString());
	}
}

/**
 * In production, these data should be stored in a database or at least another file
 */
class Order {
	public int orderid;
	public int dealid;
	public String email;
	public String addr;
	public String city;
	public String state;
	public String zipcode;
	public String credit;
	
	// Can add more states later
	public static final HashMap<String, String> stateMapper = new HashMap<String, String>();
	{
		stateMapper.put("IL", "ILLINOIS");
		stateMapper.put("CA", "CALIFORNIA");
		stateMapper.put("NY", "NEW YORK");
	}
	
	public boolean isSimiliar(Order other) {
		
		// Basic check - orderid and credicard
		if (this.dealid != other.dealid ||
			this.credit.equals(other.credit)) {
			return false;
		}
		
		if (similiarEmail(other)) 			return true;			
		if (similiarAddress(other)) 		return true;
		
		return false;
	}
	
	private boolean similiarEmail(Order other) {
		// Adjust emails before we compare
		String myEmail = adjustedEmail(this.email);
		String otherEmail = adjustedEmail(other.email);
		
		if (myEmail.equals(otherEmail)) {
			return true;
		}
		return false;
	}
	
	private boolean similiarAddress(Order other) {
		
		// Check zipcode first because its least likely to be same
		if (!this.zipcode.toLowerCase().equals(other.zipcode.toLowerCase())) {
			return false;
		}
		
		// Check address next because its least likely to be same
		String myAddr = adjustAdr(this.addr);
		String otherAddr = adjustAdr(other.addr);
		if (!myAddr.equals(otherAddr)) return false;
		
		// Check city
		if (!this.city.toLowerCase().equals(other.city.toLowerCase())) {
			return false;
		}
		
		// Check state
		String myState = adjustState(this.state);
		String otherState = adjustState(other.state);
		if (!myState.equals(otherState)) return false;
		
		// All addr checks done
		return true;
	}
	
	private String adjustAdr(String addr) {
		String result = addr.toLowerCase();
		
		// This part can be improved if we know similarities of other adjustments
		result = result.replace("street", "st.");
		result = result.replace("road", "rd.");
		return result;
	}
	
	private String adjustState(String state) {
		String result = state.toUpperCase();
		result = stateMapper.containsKey(state) ? stateMapper.get(state) : result;
		return result;
	}
	
	private String adjustedEmail(String email) {
		String adjEmail = email.toLowerCase();
		int atIndex = adjEmail.indexOf("@");
		String userPart = adjEmail.substring(0, atIndex);
		
		// Remove everything after plus
		int endIndex = adjEmail.indexOf("+");
		if (endIndex > 0) {
			userPart = userPart.substring(0, endIndex);
		}
		
		// Remove dots
		userPart = userPart.replace(".", "");
		
		// Get our adjustedEmail using stringbuilder for speed
		StringBuilder sb = new StringBuilder();
		sb.append(userPart);
		sb.append(adjEmail.substring(atIndex));
		
		return sb.toString();
	}
}
