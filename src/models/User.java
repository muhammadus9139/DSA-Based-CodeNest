package models;

public class User {
    private String fullName;
    private String username;
    private String password;
    private String bio;
    private String university;
    private String profilePicPath;

    public User(String fullName, String username, String password, String bio, String university) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.university = university;
        this.profilePicPath = "";
    }

    // Getters
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getBio() { return bio; }
    public String getUniversity() { return university; }
    public String getProfilePicPath() { return profilePicPath; }

    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPassword(String password) { this.password = password; }
    public void setBio(String bio) { this.bio = bio; }
    public void setUniversity(String university) { this.university = university; }
    public void setProfilePicPath(String path) { this.profilePicPath = path; }

    // Save format
    public String toFileString() {
        return fullName + "|" + username + "|" + password + "|" + bio + "|" + university + "|" + profilePicPath;
    }

    // Load format
    public static User fromFileString(String line) {
        String[] parts = line.split("\\|");
        User u = new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
        if (parts.length > 5) u.setProfilePicPath(parts[5]);
        return u;
    }
}