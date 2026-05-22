package dsa;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private TrieNode root = new TrieNode();

    // Insert word
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
        current.fullWord = word;
    }

    // Search exact word
    public boolean search(String word) {
        TrieNode node = getNode(word);
        return node != null && node.isEndOfWord;
    }

    // Starts with prefix
    public boolean startsWith(String prefix) {
        return getNode(prefix) != null;
    }

    // Autocomplete — get all words with prefix
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = getNode(prefix);
        if (node == null) return results;
        collectWords(node, results);
        return results;
    }

    // Helper — get node at end of prefix
    private TrieNode getNode(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            if (!current.children.containsKey(c)) return null;
            current = current.children.get(c);
        }
        return current;
    }

    // Helper — collect all words from node
    private void collectWords(TrieNode node, List<String> results) {
        if (node.isEndOfWord) {
            results.add(node.fullWord);
        }
        for (TrieNode child : node.children.values()) {
            collectWords(child, results);
        }
    }

    // Delete word
    public void delete(String word) {
        deleteHelper(root, word.toLowerCase(), 0);
    }

    private boolean deleteHelper(TrieNode node, String word, int index) {
        if (index == word.length()) {
            if (!node.isEndOfWord) return false;
            node.isEndOfWord = false;
            return node.children.isEmpty();
        }
        char c = word.charAt(index);
        TrieNode child = node.children.get(c);
        if (child == null) return false;
        boolean shouldDelete = deleteHelper(child, word, index + 1);
        if (shouldDelete) {
            node.children.remove(c);
            return node.children.isEmpty() && !node.isEndOfWord;
        }
        return false;
    }

    // Clear all
    public void clear() {
        root = new TrieNode();
    }
}