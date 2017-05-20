/*
 * Daren Wang
 * SE3345.005.s17
 * Written in Java 1.8.0
 * Compiled in Eclipse Neon 4.6.0
 * 
 * Project 2
 * This program simulates a Trie structure.
 */
import java.util.Scanner;

// Class contains main operating procedures to simulate a Trie.
public class DAWAp2 {

	/*
	 * public static void main(String[] args)
	 * method performs simulation of Trie structure
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean notDone = true,
				success = false;
		Trie trie = new Trie();
		
		// TEST SECTION - DELETE BEFORE SUBMISSION
//		Trie test = new Trie();
//		test.root = new TrieNode();
//		test.root.setChild('i');
//		test.root.getChild(8).setTerminal(true);
//		test.root.getChild(8).setChild('s');
//		test.root.getChild(8).getChild(18).setTerminal(true);
//		test.root.setChild('a');
//		test.root.getChild(0).setTerminal(true);
//		test.root.getChild(0).setChild('r');
//		test.root.getChild(0).getChild(17).setChild('c');
//		test.root.getChild(0).getChild(17).getChild(2).setTerminal(true);
//		test.root.getChild(0).getChild(17).setChild('t');
//		test.root.getChild(0).getChild(17).getChild(19).setTerminal(true);
//		test.root.getChild(0).getChild(17).setChild('m');
//		test.root.getChild(0).getChild(17).getChild(12).setChild('y');
//		test.root.getChild(0).getChild(17).getChild(12).getChild(24).setTerminal(true);
		
//		test.insert("a");
//		test.insert("army");
//		test.insert("army");
//		test.insert("arms");
//		test.listAll();
//		System.out.println(test.membership());
//		String target = "arms";
//		target = target.trim();
//		System.out.println(test.isPresent(target));
		// END OF TEST SECTION
		
		while(notDone) {
			String line = input.nextLine();
			line = line.trim();
			String [] tokens = line.split(" ");
			
			switch(tokens[0]) {
			case "A":
				// Insert
				success = trie.insert(tokens[1]);
				if(success)
					System.out.println("Word inserted");
				else
					System.out.println("Word already exists");
				break;
			case "D":
				// Delete
				success = trie.delete(tokens[1]);
				if(success)
					System.out.println("Word deleted");
				else
					System.out.println("Word not present");
				break;
			case "S":
				// Search
				success = trie.isPresent(tokens[1]);
				if(success)
					System.out.println("Word found");
				else
					System.out.println("Word not present");
				break;
			case "M":
				// Print membership
				System.out.println("Membership is " + trie.membership());
				break;
			case "L":
				// List all
				trie.listAll();
				break;
			case "E":
				// End of data
				notDone = false;
				break;
			default:
					System.out.println("Invalid command. Terminating program.");
					notDone = false;
			} // end of command switch() statement
		} // end of process while() loop
		input.close();

	} // end of main() method

} // end of DAWAp2 class

// Class contains data structures and methods to implement a Trie
class Trie {
	TrieNode root;
	
	// Constructor - default
	Trie() {
		root = new TrieNode();
	} // end of Trie() default constructor
	
	/*
	 * boolean insert(String s)
	 * Method inserts provided string to Trie
	 */
	boolean insert(String s) {
		// Check if string already exists
		if(isPresent(s))
			return false;
		TrieNode ref = root;
		// Call recursive helper method
		return insert(s, ref);
	} // end of insert() method
	
	/*
	 * boolean insert(String s, TrieNode ref)
	 * Helper method to recursively insert into Trie
	 */
	boolean insert(String s, TrieNode ref) {
		// Check if string is empty
		if(s.length() == 0 && ref != root) {
			ref.setTerminal(true);
			return true;
		}
		// Check if ref is null
		if(ref == null) {
			ref = new TrieNode();
			return insert(s, ref);
		} // end of null ref if() check
		// Check if child is set
		if(ref.getChild(s.charAt(0) - 97) == null) {
			ref.setChild(s.charAt(0));
			ref.setOutDegree(ref.getOutDegree() + 1);
			// Check if end of string
			if(s.length() == 1) {
				ref.getChild((int) s.charAt(0) - 97).setTerminal(true);
				return true;
			} // end of last letter inserted if() statement
			return insert(s.substring(1), ref.getChild((int) s.charAt(0) - 97));
		} // end of child not set if() statement
		return insert(s.substring(1), ref.getChild((int) s.charAt(0) - 97));
	} // end of recursive helper insert() method
	
	/*
	 * boolean isPresent(String s)
	 * Method determines if provided string is already present in Trie.
	 */
	boolean isPresent(String s) {
		TrieNode ref = root;
		// Call recursive helper method
		return search(s, ref);
	} // end of isPresent() method
	
	/*
	 * boolean search(String s, TrieNode ref)
	 * Helper method to recursively search Trie.
	 */
	boolean search(String s, TrieNode ref) {
		if(ref == null)
			return false;
		if(s.length() == 0) {
			if(ref.isTerminal())
				return true;
			else
				return false;
		}
		for(int i = 0; i < 26; i++) {
			if((int) (s.charAt(0) - 97) == i)
				return search(s.substring(1), ref.getChild(i));
		} // end of children traversal for() loop
		return false;
	} // end of recursive helper search() method
	
	/*
	 * boolean delete(String s)
	 * Method removes provided string from Trie.
	 */
	boolean delete(String s) {
		TrieNode ref = root;
		// Check for empty Trie
		if(ref == null)
			return false;
		// Check for empty string
		if(s.length() == 0)
			return true;
		// Check that string exists in Trie
		if(!isPresent(s))
			return false;
		return delete(s, ref);
	} // end of delete() method
	
	/*
	 * boolean delete(String s, TrieNode ref)
	 * Helper method to recursively delete from Trie.
	 */
	boolean delete(String s, TrieNode ref) {
		if(s.length() == 0) {
			// Check if node to be deleted has children
			if(ref.getOutDegree() > 0) {
				ref.setTerminal(false);
				return true; // return true if children exist
			} // end of children check if() statement
			return false; // return false if no children
		} // end of node to be deleted found if() statement
		if(delete(s.substring(1), ref.getChild((int) s.charAt(0) - 97)))
			return true;
		// if node to be deleted has no children
		else {
			// fully remove child
			ref.deleteChild(s.charAt(0));
			// check if current node should be removed
			if(ref.getOutDegree() == 0 && !ref.isTerminal())
				return false; // remove if current node has no children and is not terminal
			return true;
		} // end of delete empty child else() statement
	} // end of recursive helper delete() method
	
	/*
	 * int membership()
	 * Method determines the total number of words in Trie.
	 */
	int membership() {
		TrieNode ref = root;
		// Call recursive helper method
		return membership(ref);
	} // end of membership() method
	
	/*
	 * int membership(TrieNode ref)
	 * Helper method to recursively count words in Trie.
	 */
	int membership(TrieNode ref) {
		int mem = 0;
		if(ref == null)
			return mem;
		if(ref.isTerminal())
			mem++;
		for(int i = 0; i < 26; i++)
			if(ref.getChild(i) != null)
				mem += membership(ref.getChild(i));
		
		return mem;
	} // end of recursive helper membership() method
	
	/*
	 * void listAll()
	 * Method prints all words in Trie.
	 */
	void listAll() {
		TrieNode ref = root;
		String out = "";
		// Call recursive helper method
		listAll(ref, out);
		
	} // end of listAll() method
	
	/*
	 * void listAll(TrieNode ref, String out)
	 * Helper method to recursively print words in Trie.
	 */
	void listAll(TrieNode ref, String out) {
		if(ref == null)
			return;
		if(ref.isTerminal())
			System.out.println(out);
		for(int i = 0; i < 26; i++) {
			TrieNode tempNode = ref;
			String tempOut = out;
			if(ref.getChild(i) != null) {
				out += (char) (i + 97);
				ref = ref.getChild(i);
				listAll(ref, out);
			} // end of child exists at index i if() statement
			ref = tempNode;
			out = tempOut;
		} // end of children traversal for() loop
	} // end of listAll() recursive helper method
} // end of Trie class

// Class contains data structures and methods to instantiate a Trie node.
class TrieNode {
	private boolean terminal;
	private int outDegree;
	private TrieNode[] children;
	
	// Constructor - default
	TrieNode() {
		terminal = false;
		outDegree = 0;
		children = new TrieNode[26];
	} // end of TrieNode() default constructor

	// Getter for node terminal status.
	public boolean isTerminal() {
		return terminal;
	} // end of isTerminal() method

	// Setter for node terminal status.
	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
	} // end of setTerminal() method

	// Getter for node out degree.
	public int getOutDegree() {
		return outDegree;
	} // end of getOutDegree() method

	// Setter for node out degree.
	public void setOutDegree(int outDegree) {
		this.outDegree = outDegree;
	} // end of setOutDegree() method
	
	// Getter for node child at index.
	public TrieNode getChild(int index) {
		return children[index];
	} // end of getChild() method
	
	// Setter for node child of letter.
	public void setChild(char letter) {
		this.children[(int) letter - 97] = new TrieNode();
	} // end of setChild() method
	
	// Deletes node child of specified letter
	public void deleteChild(char letter) {
		this.children[(int) letter - 97] = null;
	}
	
} // end of TrieNode class 