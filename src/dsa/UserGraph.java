package dsa;

import java.util.ArrayList;
import java.util.HashMap;

public class UserGraph {
    // Adjacency list — username -> list of following
    private HashMap<String, ArrayList<String>> graph = new HashMap<>();

    // Add user node
    public void addUser(String username) {
        graph.putIfAbsent(username, new ArrayList<>());
    }

    // Follow
    public void follow(String follower, String following) {
        graph.putIfAbsent(follower, new ArrayList<>());
        graph.putIfAbsent(following, new ArrayList<>());
        if (!graph.get(follower).contains(following)) {
            graph.get(follower).add(following);
        }
    }

    // Unfollow
    public void unfollow(String follower, String following) {
        if (graph.containsKey(follower)) {
            graph.get(follower).remove(following);
        }
    }

    // Is following?
    public boolean isFollowing(String follower, String following) {
        return graph.containsKey(follower) &&
                graph.get(follower).contains(following);
    }

    // Get following list
    public ArrayList<String> getFollowing(String username) {
        return graph.getOrDefault(username, new ArrayList<>());
    }

    // Get followers count
    public int getFollowersCount(String username) {
        int count = 0;
        for (ArrayList<String> following : graph.values()) {
            if (following.contains(username)) count++;
        }
        return count;
    }

    // Get following count
    public int getFollowingCount(String username) {
        return graph.getOrDefault(username, new ArrayList<>()).size();
    }
}