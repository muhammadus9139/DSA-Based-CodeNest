package dsa;

public class CircularQueue {
    private int[] data;
    private int front, rear, size, capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.data = new int[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    // Add element
    public void enqueue(int value) {
        if (size == capacity) {
            // Overwrite oldest
            front = (front + 1) % capacity;
            size--;
        }
        rear = (rear + 1) % capacity;
        data[rear] = value;
        size++;
    }

    // Get all elements in order
    public int[] getAll() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = data[(front + i) % capacity];
        }
        return result;
    }

    // Size
    public int size() { return size; }

    // Empty check
    public boolean isEmpty() { return size == 0; }

    // Clear
    public void clear() {
        front = 0;
        rear = -1;
        size = 0;
    }
}