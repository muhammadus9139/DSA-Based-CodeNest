package dsa;

import java.util.LinkedList;
import java.util.Queue;

public class SuggestionQueue {
    private Queue<String> queue = new LinkedList<>();

    // Add suggestion
    public void enqueue(String suggestion) {
        queue.add(suggestion);
    }

    // Get next suggestion
    public String dequeue() {
        return queue.poll();
    }

    // Peek
    public String peek() {
        return queue.peek();
    }

    // Get all suggestions
    public Queue<String> getAll() {
        return queue;
    }

    // Clear all
    public void clear() {
        queue.clear();
    }

    // Empty check
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Size
    public int size() {
        return queue.size();
    }
}