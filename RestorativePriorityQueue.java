import java.util.ArrayList;

/*
 * A custom generic priority queue where processed data can be restored to the queue 
 * in its original state without copying the queue or the popped data.
 * This "original state" can be reset by clearing the garbage from the queue.
 * Memory reallocation is powered by the ArrayList data structure
 * but but memory allocation can be make more efficient through manual queue resets.
 */
public class RestorativePriorityQueue<T extends Comparable<T>> {


	//Private Member variables
	private int _size; //Upper bounds for our heap. Any indicies here and beyond are queued for deletion
	private ArrayList<T> _heap;
	
	
	RestorativePriorityQueue(){
		_size = 0;
		_heap = new ArrayList<T>();
	}
	
	RestorativePriorityQueue(T[] values){
		_size = 0;
		//Allocate a spare 25% space
		_heap = new ArrayList<T>(_size + ((int) _size/4));
		for (T val : values) {
			this.insert(val);
		}
	}
	
	
	//----------------------------------------------------------------------------------
	//------------------------Private Member Functions----------------------------------
	//----------------------------------------------------------------------------------
	
	private void swap(ArrayList<T> AL, int v1, int v2) {
		T temp = AL.get(v2);
		AL.set(v2, AL.get(v1));
		AL.set(v1, temp);
		
	}
	
	
	private int getParent(int index) {
		return (index-1) / 2;
	}
	
	private int getRChild(int index) {
		return (index * 2) + 2;
	}
	
	private int getLChild(int index) {
		return (index * 2) + 1;
	}
	
	//Returns the index of the smallest child and -1 if no lesser child exists
	private int getMinChild(int index) {
		int rChild = getRChild(index); int lChild = getLChild(index);
		T lValue, rValue;
		T percValue = _heap.get(index);
		
		//No child exists
		if(rChild >= _size && lChild >= _size)
			return -1;
		
		//Only R Child
		else if (rChild < _size && lChild >= _size) {
			rValue = _heap.get(rChild);
			if(rValue.compareTo(percValue) < 0)
				return rChild;
			else return -1;
		}
		
		//Only L Child
		else if (lChild < _size && rChild >= _size) {
			lValue = _heap.get(lChild);
			if(lValue.compareTo(percValue) < 0)
				return lChild;
			else return -1;
		}
		
		//Both Children Exist
		else {
			lValue = _heap.get(lChild); rValue = _heap.get(rChild);
			//If either is smaller than the perc, return the smallest of those two
			if(lValue.compareTo(percValue) < 0 || rValue.compareTo(percValue) < 0)
				return lValue.compareTo(rValue) < 0 ? lChild : rChild;
			else return -1;
		}
	}
	
	//-Percolate Up (Take val at last index and shift to proper location)
	//Check 
	private void percolateUp() {
		int pos = _size -1;
		int parent = getParent(pos);

		while (parent >= 0 && _heap.get(pos).compareTo(_heap.get(parent)) < 0) {
			swap(_heap, pos, parent);
			pos = parent;
			parent = getParent(pos);
		}
		
	}
	
	//Percolate down (Take element in root  and shift to proper location by swapping with the smaller of its children)
	private void percolateDown() {
		int pos = 0;
		
		//Determine min child
		int minChild = getMinChild(pos);
		
		//while the position has a child and that child is smaller than the position
		while (minChild != -1) {
			swap(_heap, pos, minChild);
			pos = minChild;
			minChild = getMinChild(pos);
		}

		
	}
	

	
	//Prints the list of all heap values. For Debug use only
	private void printHeap() {
		System.out.print("[");
		for (int i = 0; i < _size; ++i) {
			System.out.print(_heap.get(i) + (i==_size-1 ? "":","));
		}
		System.out.println("] Length: " + _size);
	}
	
	//----------------------------------------------------------------------------------
	//------------------------Public Member Functions-----------------------------------
	//----------------------------------------------------------------------------------

	public int getLength() {
		return _size;
	}
	
	public int getCapacity() {
		return _heap.size();
	}
	
	//Add given value to Queue
	public void insert(T value) {
		//If _heap was an array, resize would occur here
		_heap.add(value);
		_size++;
		percolateUp();
		
	}
	
	//Clear the minimum element and return
	public T pop() {
		if(_size == 0) throw new IllegalStateException("Cannot Pop Empty Queue!");
		swap(_heap, 0, _size - 1);
		_size--; //Popped 1st element is now garbage
		percolateDown();
		return _heap.get(_size);
	}
	
	//Wipes all entries from the queue
	public void clear() {
		_heap.clear();
		_size = 0;
	}
	
	//Returns minimum element without clearing
	public T peek() {
		if (_size == 0) throw new ArrayIndexOutOfBoundsException("Cannot Peek an Empty Queue!");
		else return _heap.get(0);
	}
	
	//Removes popped values from memory
	//Prevents the restoration of all elements popped prior to function call
	public void collectGarbage() {
		for (int i =_heap.size()-1; i >= _size; --i) {
			_heap.remove(i);
		}
	}
	
	//Restores all popped heap values stored beyond bounds
	public void restoreHeap() {
		int startSize = _size;
		for (int i =_heap.size()-1; i >= startSize; --i) {
			insert(_heap.remove(i));
		}
	}
	
	//---------------------------------------------------------------------------------
	//------------------------------STRESS TESTING-------------------------------------
	//---------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		RestorativePriorityQueue<Integer> heap = new RestorativePriorityQueue<Integer>(
				new Integer[]{15, 5, 4, 3, 8, 99, 17, -12, 43, 45, 0, 67, 83, 22}
				);
		
		
		
		heap.insert(4); heap.insert(2); heap.insert(8); heap.insert(5);
		heap.printHeap();
		
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println("Popped: " + heap.pop()); heap.printHeap();
		System.out.println(new String(new char[20]).replace("\0", "-"));
		
		System.out.println("Collecting Garbage...");
		heap.collectGarbage(); heap.printHeap();
		
		System.out.println("Restoring...");
		heap.restoreHeap(); heap.printHeap();
		
		System.out.println("Popped: " + heap.pop());
		
		System.out.println("Restoring...");
		heap.restoreHeap(); heap.printHeap();
		
		heap.insert(999);
		
		System.out.println("Collecting Garbage...");
		heap.collectGarbage(); heap.printHeap();
		
		System.out.println("Restoring...");
		heap.restoreHeap(); heap.printHeap();
		
		System.out.println("Clearing...");
		heap.clear(); heap.printHeap();
		
		heap.insert(-4); heap.insert(27); heap.insert(8); heap.insert(9);
		heap.printHeap();
		
		
		//heap.pop(); heap.peek();
		
		
	}

}
