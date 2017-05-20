/*
 * Daren Wang
 * SE3345.005.s17
 * Written in Java 1.8.0
 * Compiled in Eclipse Neon 4.6.0
 * 
 * Project 1
 * This program simulates a segmented virtual memory.
 * It implements two algorithms, best fit and first fit.
 */
import java.util.Scanner;

//  Class contains the main operating procedures to simulate a segmented virtual memory.
public class DAWAp1 {
	private static Memory memory = new Memory();
	private static int fitType = 0,
						totalFailures = 0,
						totalProbesOnSuccess = 0,
						totalFreeOnFail = 0;

	/*
	 * public static void main(String[] args) 
	 * Method performs simulation of segmented virtual memory.
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean notDone = true;
		int currentTime = 0,
				memorySize = 0,
				totalSegments = 0,
				totalMemUsed = 0,
				totalRequests = 0;
		
		while(notDone) {
			String line = input.nextLine();
			String [] tokens = line.split(" ");
			
			switch(tokens[0]) {
			case "FF":
				// First Fit procedure
				System.out.println("FIRST FIT");
				fitType = 0;
				break;
			case "BF":
				// Best Fit procedure
				System.out.println("BEST FIT");
				fitType = 1;
				break;
			case "C":
				// Clear memory and set memorySize
				memorySize = Integer.valueOf(tokens[1]);
				memory = new Memory(memorySize);
				break;
			case "P":
				// Place segment
				Request request = new Request(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2]));
				currentTime = place(request, currentTime);
				totalSegments += request.getLifetime();
				totalMemUsed += request.getSize() * request.getLifetime();
				totalRequests++;
				break;
			case "R":
				// Report
				System.out.println("Report at Time: " + currentTime);
				memory.displayMemory();
				break;
			case "S":
				// Run until all segments have left memory
				currentTime = memory.completeRequests(currentTime);
				// Print Statistics
				System.out.printf("Average number of segments in memory: %.2f%n", 1.0 * totalSegments / currentTime);
				System.out.printf("Average number of holes in memory: %.2f%n", memory.totalHoles() / currentTime);
				System.out.printf("Average amount of memory in use: %.2f%n", 1.0 * totalMemUsed / currentTime);
				System.out.printf("Average number of probes in a successful placement: %.2f%n", 1.0 * totalProbesOnSuccess / totalRequests);
				if(totalFailures != 0)
					System.out.printf("Average amount of total free space when a placement fails: %.2f%n", 1.0 * totalFreeOnFail / totalFailures);
				System.out.printf("Average percentage failure rate: %.2f%n", 100.0 * totalFailures / totalRequests);
				break;
			case "E":
				// End of data
				notDone = false;
				break;
			default:
				System.out.println("Invalid command. Simulation terminating.");
				notDone = false;
			} // end of command switch() statement
		} // end of process while() loop
		input.close();
		
	} // end of main() method

	/*
	 * static int place(Request r, int atTime)
	 * Method places a segment in memory.
	 */
	static int place(Request r, int atTime) {
		int time = atTime + 1,
			freeOnFail = 0;
		boolean failedPlace = false;
		
		memory.removeNodesAt(time);
		while(!memory.insertNode(r.getSize(), r.getLifetime() + time, fitType)) {
			time++;
			memory.removeNodesAt(time);
			if(!failedPlace)
				freeOnFail = memory.getFreeSpace();
			failedPlace = true;
			memory.totalProbes();
		} // end of segment insertion while() loop
		// record statistics for failed placements
		if(failedPlace) {
			totalFailures++;
			totalFreeOnFail += freeOnFail;
		} // end of initial failure statistics update if() statement
		totalProbesOnSuccess += memory.totalProbes();
		return time;
	} // end of place() method

} // end of DAWAp1() class

// Class contains data structures and methods to simulate a segmented virtual memory
class Memory {
	private MemoryNode head;
	private int totalHoles = 0,
				totalProbes = 0;

	// Constructor - default
	Memory() {
		head = new MemoryNode(0, 0, false);
	} // end of Memory() default constructor

	// Constructor - takes total memory size
	Memory(int size) {
		head = new MemoryNode(size, 0, false);
	} // end of Memory() constructor

	/*
	 * boolean insertNode(int size, int departs, int fitType)
	 * Method inserts a segment with given size and time of departure.
	 * Returns boolean indicating whether insertion was successful.
	 */
	boolean insertNode(int size, int departs, int fitType) {
		boolean nodeInserted = false;
		MemoryNode insertAfter = null;
		// find insertion point with selected fit algorithm
		if(fitType == 0)
			insertAfter = seekFirstFit(size);
		else
			insertAfter = seekBestFit(size);
		// return failure if no fit found
		if(insertAfter == null) {
			getHoleStats();
			return nodeInserted;
		} // end of insert failure if() statement
		// set true if fit found
		nodeInserted = true;
		// only hole returnable is first node
		if(!insertAfter.isOccupied()) {
			MemoryNode hole = insertAfter;
			hole.setSize(hole.getSize() - size);
			// check if hole is consumed
			if(hole.getSize() == 0)
				hole = hole.getNext();
			head = new MemoryNode(size, departs, true);
			head.setNext(hole);
		} // end of insert at head if() statement
		// all other returnable nodes are occupied and precede a hole of sufficient size
		else {
			MemoryNode hole = insertAfter.getNext();
			hole.setSize(hole.getSize() - size);
			// check if hole is consumed
			if(hole.getSize() == 0)
				hole = hole.getNext();
			insertAfter.setNext(new MemoryNode(size, departs, true));
			insertAfter.getNext().setNext(hole);
		} // end of general insertion else statement
		getHoleStats();
		return nodeInserted;
	} // end of insertNode() method

	/*
	 * void removeNode(MemoryNode ref)
	 * Method removes a node.
	 * Method takes reference to node preceding node to be removed unless
	 * node to be removed is first node.
	 */
	void removeNode(MemoryNode ref) {
		// check if preceding node is occupied segment
		if(ref.isOccupied()) {
			// check if node to be removed is last in memory
			if (ref.getNext().getNext() == null) {
				// change removed segment into hole
				ref.getNext().setTimeToDepart(0);
				ref.getNext().setOccupied(false);
			} // end of removed segment is last in memory if() statement
			// check if following node is occupied segment
			else if (ref.getNext().getNext().isOccupied()) {
				// change removed segment into hole
				ref.getNext().setTimeToDepart(0);
				ref.getNext().setOccupied(false);
			} // end of following occupied segment if() statement
			else {
				// link preceding segment to following hole which consumes removed segment space
				int space = ref.getNext().getSize();
				ref.setNext(ref.getNext().getNext());
				ref.getNext().setSize(ref.getNext().getSize() + space);
			} // end of following hole else statement
		} // end of preceding occupied segment if() statement
		else {
			// check if node to be removed is last in memory
			if (ref.getNext().getNext() == null) {
				// consume size of removed segment
				int space = ref.getNext().getSize();
				ref.setNext(ref.getNext().getNext());
				ref.setSize(ref.getSize() + space);
			} // end of removed segment is last in memory if() statement
			// check if following node is occupied segment
			else if(ref.getNext().getNext().isOccupied()) {
				// preceding hole consumes removed segment space and links to following segment
				int space = ref.getNext().getSize();
				ref.setNext(ref.getNext().getNext());
				ref.setSize(ref.getSize() + space);
			} // end of following occupied segment if() statement
			else {
				// merge space of removed segment and following hole into preceding hole
				int space = ref.getNext().getSize() + ref.getNext().getNext().getSize();
				ref.setNext(ref.getNext().getNext().getNext());
				ref.setSize(ref.getSize() + space);
			} // end of following hole else statement
		} // end of preceding hole else statement
	} // end of removeNode() method
	
	/*
	 * void removeHead()
	 * Method removes the first node of memory.
	 */
	void removeHead() {
		// check if node to be removed is only segment
		if(head.getNext() == null) {
			// change segment into hole
			head.setTimeToDepart(0);
			head.setOccupied(false);
		} // end of one segment in memory if() statement
		else if (head.getNext().isOccupied()) {
			// change segment into hole
			head.setTimeToDepart(0);
			head.setOccupied(false);
		} // end of next node is occupied segment if() statement
		else {
			// consume next hole
			int space = head.getNext().getSize();
			head.setNext(head.getNext().getNext());
			head.setSize(head.getSize() + space);
		} // end of next node is hole else statement
	} // end of removeHead() method

	/*
	 * void removeNodesAt(int time)
	 * Method removes all segments with specified depart time.
	 */
	void removeNodesAt(int time) {
		MemoryNode ref = head;
		// remove first node if scheduled to depart
		while(ref.getTimeToDepart() == time) {
			removeHead();
			ref = head;				// re-establish reference
		} // end of head removal while() loop
		// traverse memory, pass nodes preceding departing segments to removaNode
		while(ref.getNext() != null) {
			// check if current node scheduled to depart
			if(ref.getTimeToDepart() == time) {
				removeNode(ref);
				ref = head;			// re-establish reference
			} // end of head removal if() statement
			// check if next node schedule to depart
			if(ref.getNext().getTimeToDepart() == time)
				removeNode(ref);
			// end traversal if only one node remains
			if(ref.getNext() == null)
				break;
			else
				ref = ref.getNext();
		} // end of memory traversal while() loop
	} // end of removeNodesAt() method

	/*
	 * MemoryNode seekFirstFit(int size)
	 * Method attempts to find the first hole for requested segment size.
	 * Returns segment to be inserted after.
	 */
	MemoryNode seekFirstFit(int size) {
		MemoryNode ref = head;
		// check if first node in memory is hole of sufficient size to place new segment
		if(!ref.isOccupied()) {
			totalProbes++;
			if(ref.getNext() == null && ref.getSize() >= size)
				return ref;
		} // end of head node sufficient if() statement
		// traverse memory while seeking hole of sufficient size to place new segment
		while(ref.getNext() != null) {
			// fit found if hole of sufficient size found
			if(!ref.getNext().isOccupied()) {
				totalProbes++;
				if(ref.getNext().getSize() >= size)
					return ref;			// return segment preceding hole
			} // end of sufficient hole found if() statement
			ref = ref.getNext();
		} // end of memory traversal while() loop
		// return null if no fit found
		return null;
	} // end of seekFirstFit() method

	/*
	 * MemoryNode seekBestFit(int size)
	 * Method attempts to find a best fit hole for requested segment size.
	 * Returns segment to be inserted after.
	 */
	MemoryNode seekBestFit(int size) {
		MemoryNode ref = head,
				best = null;
		// check if first node in memory is hole of sufficient size to place new segment
		if(!ref.isOccupied()) {
			totalProbes++;
			if(ref.getSize() >= size)
				best = ref;
		} // end of head node sufficient if() statement
		// traverse memory while seeking hole of sufficient size to place new segment
		while(ref.getNext() != null) {
			// fit found if hole of sufficient size found
			if(!ref.getNext().isOccupied()) {
				totalProbes++;
				if(ref.getNext().getSize() >= size) {
					if(best == null)
						best = ref;
					else if(ref.getNext().getSize() < best.getNext().getSize())
						best = ref;
				} // end of best fit update if() statement
			} // end of sufficient hole found if() statement
			ref = ref.getNext();
		} // end of memory traversal while() loop
		// return null if no fit found
		return best;
	} // end of seekBestFit() method
	
	/*
	 * void displayMemory()
	 * Method displays contents of memory.
	 */
	void displayMemory() {
		MemoryNode ref = head;
		int address = 0;
		// traverse memory and display contents
		while(ref != null) {
			if(ref.isOccupied())
				System.out.println("Segment: " + address + " " + ref.getSize() + " " + ref.getTimeToDepart());
			else
				System.out.println("Hole: " + address + " " + ref.getSize());
			address += ref.getSize();
			ref = ref.getNext();
		} // end of memory display while() loop
	} // end of displayMemory() method
	
	/*
	 * int completeRequests(int atTime)
	 * Method increments time until all requests have departed.
	 * Returns final time at which all requests have departed.
	 */
	int completeRequests(int atTime) {
		MemoryNode ref = head;
		int time = atTime;
		// remove all nodes departing at current time
		removeNodesAt(time);
		// continue running until body nodes are removed
		while(ref.getNext() != null) {
			time++;
			removeNodesAt(time);
			getHoleStats();
		} // end of body removal while() loop
		// continue running until head is removed
		while(ref.isOccupied()) {
			time++;
			removeNodesAt(time);
			getHoleStats();
		} // end of head removal while() loop
		// return final time of completion
		return time;
	} // end of completeRequests() method
	
	/*
	 * void getHoleStats()
	 * Method collects number of holes.
	 */
	void getHoleStats() {
		MemoryNode ref = head;
		while(ref != null) {
			if(!ref.isOccupied())
				totalHoles++;
			ref = ref.getNext();
		} // end of traversal while() loop
	} // end of getHoleStats() method
	
	/*
	 * double totalHoles()
	 * Returns total number of holes in memory.
	 */
	double totalHoles() {
		return 1.0 * totalHoles;
	} // end of totalHoles() method
	
	/*
	 * double totalProbes()
	 * Returns total number of probes during placement.
	 */
	double totalProbes() {
		double probes = totalProbes * 1.0;
		totalProbes = 0;
		return probes;
	} // end of totalProbes() method
	
	/*
	 * void getFreeSpace()
	 * Method returns amount of free memory.
	 */
	int getFreeSpace() {
		MemoryNode ref = head;
		int freeSpace = 0;
		while(ref != null) {
			if(!ref.isOccupied())
				freeSpace += ref.getSize();
			ref = ref.getNext();
		} // end of traversal while() loop
		
		return freeSpace;
	} // end of getFreeSpace() method

} // end of Memory class

// Class contains data elements and methods to simulate a segment of memory
class MemoryNode {
	private	int segmentSize,
			timeToDepart;
		boolean occupied;
		MemoryNode next;

	// Constructor - default
	MemoryNode() {
		occupied = false;
		next = null;
	} // end of MemoryNode() default constructor

	// Constructor - takes segment size, duration, and occupancy status
	MemoryNode(int size, int tod, boolean type) {
		segmentSize = size;
		timeToDepart = tod;
		occupied = type;
		next = null;
	} // end of MemoryNode() constructor
	
	// Getter for segment size
	public int getSize() {
		return segmentSize;
	} // end of getSize() method
	
	// Setter for segment size
	public void setSize(int size) {
		this.segmentSize = size;
	} // end of setSize() method

	// Getter for segment departure time
	public int getTimeToDepart() {
		return timeToDepart;
	} // end of getTimeToDepart() method

	// Setter for segment departure time
	public void setTimeToDepart(int timeToDepart) {
		this.timeToDepart = timeToDepart;
	} // end of setTimeToDepart() method

	// Getter for segment occupancy status
	public boolean isOccupied() {
		return occupied;
	} // end of isOccupied() method

	// Setter for segment occupancy status
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	} // end of setOccupied() method

	// Getter for next MemoryNode
	public MemoryNode getNext() {
		return next;
	} // end of getNext() method

	// Setter for next MemoryNode
	public void setNext(MemoryNode next) {
		this.next = next;
	} // end of setNext() method

} // end of MemoryNode class

// Class contains data elements and methods to implement a memory insertion request
class Request {
	private int size,
				lifetime;
	
	// Constructor - takes in size and duration of segment
	Request(int s, int l) {
		size = s;
		lifetime = l;
	} // end of Request constructor

	// Getter for size of segment
	public int getSize() {
		return size;
	} // end of getSize() method

	// Getter for duration of segment
	public int getLifetime() {
		return lifetime;
	} // end of getLifetime() method
	
} // end of Request class
