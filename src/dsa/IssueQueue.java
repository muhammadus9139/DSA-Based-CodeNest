package dsa;

import models.Issue;
import java.util.LinkedList;
import java.util.Queue;

public class IssueQueue {
    private Queue<Issue> queue = new LinkedList<>();

    // Add issue
    public void enqueue(Issue issue) {
        queue.add(issue);
    }

    // Get next issue
    public Issue dequeue() {
        return queue.poll();
    }

    // Peek
    public Issue peek() {
        return queue.peek();
    }

    // Get all issues
    public Queue<Issue> getAll() {
        return queue;
    }

    // Size
    public int size() {
        return queue.size();
    }

    // Empty check
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Clear
    public void clear() {
        queue.clear();
    }
}