/*
 * Daren Wang
 * SE3345.005.s17
 * Written in Java 1.8.0
 * Compiled in Eclipse Neon 4.6.0
 * 
 * Project 3
 * This program simulates a Union-find data structure.
 * Structure implements union-by-size and path compression algorithms.
 */
import java.util.Scanner;
// Class contains operating procedures for simulating a union-find structure
public class DAWAp3 {

	/*
	 * public static void main(String[] args)
	 * Method performs simulation of union-find structure
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean notDone = true;
		UnionFind unionFind = new UnionFind();
		Graph graph = new Graph();
		int setSize = 0;
		
		while(notDone) {
			String line = input.nextLine();
			line = line.trim();
			String [] tokens = line.split(" ");
			
			switch(tokens[0]) {
			case "s":
				// Create UnionFind for elements 0 to n-1
				unionFind = new UnionFind(Integer.valueOf(tokens[1]));
				// Create associated Graph
				graph = new Graph(unionFind, unionFind.getSize());
				break;
			case "u":
				// Union two arguments
				if(unionFind.find(Integer.valueOf(tokens[1])) != unionFind.find(Integer.valueOf(tokens[2]))) {
					setSize = unionFind.union(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2]));
					System.out.println(unionFind.find(Integer.valueOf(tokens[1])) + " " + setSize);
				} // end of arguments already unioned if() statement
				break;
			case "f":
				// Find
				System.out.println(unionFind.find(Integer.valueOf(tokens[1])));
				break;
			case "c":
				// Print connectivity list
				unionFind.print();
				break;
			case "m":
				// Create UnionFind for elements 0 to N*N-1
				unionFind = new UnionFind(Integer.valueOf(tokens[1]) * Integer.valueOf(tokens[1]));
				// Create associated Graph
				graph = new Graph(unionFind, Integer.valueOf((tokens[1])));
				graph.makeMaze();
				graph.print();
				break;
			case "e":
				// End of commands
				notDone = false;
				break;
			default:
				System.out.println("Invalid command. Terminating program.");
				notDone = false;
			} // end of command switch() statement

		} // end of process while() loop

		input.close();
	} // end of main() method
} // end of DAWAp3 class

// Class contains data structures and methods to implement union-find disjoint set structure
class UnionFind {
	private int size;
	private int[] ufArray;
	
	// Constructor - default
	UnionFind() {
		size = 0;
	} // end of UnionFind default constructor
	
	// Constructor - takes as input number of initial disjoint sets
	UnionFind(int n) {
		size = n;
		ufArray = new int[n];
		// Initialize all contents to -1, making each index a disjoint set
		for(int i = 0; i < ufArray.length; i++)
			ufArray[i] = -1;
	} // end of UnionFind(int n) constructor
	
	/*
	 * int union(int x, int y)
	 * Method joins inputs x and y into same set using union by size algorithm
	 * Returns size of resulting union set
	 */
	int union(int x, int y) {
		int size = 0,
			root = 0;
		
		// Determine which set is superior and make the other subordinate with union by size
		if(ufArray[find(x)] < ufArray[find(y)]) {
			root = find(x);
			ufArray[find(y)] = x;
		} // end of x contained in larger set if() statement
		else if(ufArray[find(y)] < ufArray[find(x)]) {
			root = find(y);
			ufArray[find(x)] = y;
		} // end of y contained in larger set elseif() statement
		else {
			if(find(x) < find(y)) {
				root = find(x);
				ufArray[find(y)] = x;
			} // end of root of x is smaller if() statement
			else {
				root = find(y);
				ufArray[find(x)] = y;
			} // end of root of y is smaller else() statement
				
		} // end of x and y in same sized sets else() statement
		
		// Calculate size of resultant set
		for(int i = 0; i < ufArray.length; i++)
			if(find(i) == root)
				size++;
		
		// Set root element content to size of set (negated)
		ufArray[root] = size * -1;
		
		return size;
	} // end of union() method
	
	/*
	 * int find(int y)
	 * Method finds the set containing input y
	 * Returns root of containing set
	 */
	int find(int y) {
		if(ufArray[y] < 0)
			return y;
		else {
			// Path compression
			ufArray[y] = find(ufArray[y]);
			return find(ufArray[y]);
		} // end of path compression else() statement
	} // end of find() method
	
	/*
	 * int numberOfSets()
	 * Method returns number of disjoint sets
	 */
	int numberOfSets() {
		int num = 0;
		
		for(int i = 0; i < ufArray.length; i++)
			if(ufArray[i] < 0)
				num++;
		
		return num;
	} // end of numberOfSets() method
	
	/*
	 * void print()
	 * Method prints all sets in object
	 */
	void print() {
		for(int i = 0; i < ufArray.length; i++) {
			if(ufArray[i] < 0) {
				System.out.print(i + " ");
				printSet(i);
			}
		}
	} // end of print() method
	
	/*
	 * void printSet(int n)
	 * Helper method to print set of root n
	 */
	void printSet(int n) {
		for(int i = 0; i < ufArray.length; i++)
			if(ufArray[i] == n)
				System.out.print(i + " ");
		System.out.println();
	} // end of printSet() method
	
	// Getter for total elements
	int getSize() {
		return this.size;
	} // end of getSize() method
	
} // end of UnionFind class

// Class contains data structures and methods to implement an N x N maze
class Graph {
	private UnionFind ufArray;
	private int size, aSize;
	private int [][] edges;
	
	// Constructor - default
	Graph() {
		size = 0;
	} // end of Graph default constructor
	
	// Constructure - takes as input associated Union-Find and size of maze
	Graph(UnionFind uf, int n) {
		ufArray = uf;
		size = n;
		aSize = n * n;
		edges = new int[aSize][4];
		initEdges();
	} // end of Graph(UnionFind uf, int n) constructor
	
	/*
	 * void makeMaze()
	 * Method creates a n x n maze of randomly selected edges
	 */
	void makeMaze() {
		int u, v, direction;
		while(ufArray.numberOfSets() > 1) {
			// Select random vertex and direction
			u = (int) (Math.random() * aSize);
			direction = (int) (Math.random() * 4);
			// Calculate neighbor
			switch(direction) {
			case 0:
				v = u - size;
				break;
			case 1:
				v = u - 1;
				break;
			case 2:
				v = u + size;
				break;
			case 3:
				v = u + 1;
				break;
			default:
				v = u;
			} // end of neighbor calculation switch() statement
			// Check for valid neighbor
			if(v < 0 || v >= aSize)
				// Not valid if out of bounds
				continue;
			// Check for valid edge
			if(ufArray.find(u) == ufArray.find(v))
				// Not valid if edge already exists
				continue;
			if(u > 0) {
				if(u % size == 0 || v % size == 0) {
					if(u % size == size - 1 || v % size == size - 1)
						// Not valid if edge crosses graph grid
						continue;
				} // end of either vertex is on left edge of grid if() statement
			} // end of edge vertex neighbor check if() statement
			// Union valid edges
			ufArray.union(u, v);
			// Add edge to graph
			addEdge(u, v);
			
		} // end of edge selection while() loop
	} // end of makeMaze() method
	
	/*
	 * private void addEdge(int u, int v)
	 * Method adds edge connecting terminal vertices u and v to graph
	 */
	private void addEdge(int u, int v) {
		int dir;
		if((u - v) == size)
			dir = 0;
		else if((u - v) == 1)
			dir = 1;
		else if((u - v) == -1)
			dir = 3;
		else
			dir = 2;
		edges[u][dir] = v;
	} // end of addEdge() method
	
	/*
	 * void print()
	 * Method prints connectivity list of the graph with random edge weights
	 */
	void print() {
		for(int u = 0; u < aSize; u++) {
			for(int v = 0; v < 4; v++) {
				if(edges[u][v] >= 0)
					System.out.println(u + " " + edges[u][v] + " " + (int) (20 * Math.random()));
			} // end of neighbor traversal for() loop
		} // end of vertex traversal for() loop
	} // end of print() method
	
	/*
	 * private void initEdges()
	 * Method intializes all values of edges array to -1
	 */
	private void initEdges() {
		for(int u = 0; u < aSize; u++)
			for(int v = 0; v < 4; v++)
				edges[u][v] = -1;
	} // end of initEdges() method
} // end of Graph class