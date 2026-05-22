package dsa;

import models.Commit;
import java.util.Stack;

public class CommitStack {
    private Stack<Commit> stack = new Stack<>();

    // Push new commit
    public void push(Commit commit) {
        stack.push(commit);
    }

    // Undo — pop last commit
    public Commit undo() {
        if (!stack.isEmpty()) {
            return stack.pop();
        }
        return null;
    }

    // Peek — last commit dekho without removing
    public Commit peek() {
        if (!stack.isEmpty()) {
            return stack.peek();
        }
        return null;
    }

    // Get all commits (history)
    public Stack<Commit> getAllCommits() {
        return stack;
    }

    // Total commits
    public int size() {
        return stack.size();
    }

    // Empty check
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}