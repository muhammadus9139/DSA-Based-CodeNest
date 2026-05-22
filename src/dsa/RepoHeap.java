package dsa;

import models.Repo;
import java.util.ArrayList;

public class RepoHeap {
    private ArrayList<Repo> heap = new ArrayList<>();

    // Insert repo
    public void insert(Repo repo) {
        heap.add(repo);
        heapifyUp(heap.size() - 1);
    }

    // Get top repo (most starred)
    public Repo peek() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    // Remove top repo
    public Repo extractMax() {
        if (heap.isEmpty()) return null;
        Repo max = heap.get(0);
        Repo last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return max;
    }

    // Get all repos sorted by stars
    public ArrayList<Repo> getSortedRepos() {
        RepoHeap tempHeap = new RepoHeap();
        for (Repo r : heap) tempHeap.insert(r);
        ArrayList<Repo> sorted = new ArrayList<>();
        while (!tempHeap.isEmpty()) {
            sorted.add(tempHeap.extractMax());
        }
        return sorted;
    }

    public boolean isEmpty() { return heap.isEmpty(); }
    public int size() { return heap.size(); }

    // Heapify up
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(parent).getStars() < heap.get(index).getStars()) {
                swap(parent, index);
                index = parent;
            } else break;
        }
    }

    // Heapify down
    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && heap.get(left).getStars() > heap.get(largest).getStars())
                largest = left;
            if (right < size && heap.get(right).getStars() > heap.get(largest).getStars())
                largest = right;

            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else break;
        }
    }

    private void swap(int i, int j) {
        Repo temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}