package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Issue {
    private String title;
    private String description;
    private String repoName;
    private String ownerUsername;
    private boolean isOpen;
    private String timestamp;

    public Issue(String title, String description, String repoName, String ownerUsername) {
        this.title = title;
        this.description = description;
        this.repoName = repoName;
        this.ownerUsername = ownerUsername;
        this.isOpen = true;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRepoName() { return repoName; }
    public String getOwnerUsername() { return ownerUsername; }
    public boolean isOpen() { return isOpen; }
    public String getTimestamp() { return timestamp; }

    // Setters
    public void setOpen(boolean open) { isOpen = open; }

    // Save format
    public String toFileString() {
        String safeDesc = description.replace("\n", "{{NL}}").replace("|", "{{PIPE}}");
        return title + "|" + safeDesc + "|" + repoName + "|" + ownerUsername + "|" + isOpen + "|" + timestamp;
    }

    // Load format
    public static Issue fromFileString(String line) {
        String[] parts = line.split("\\|", 6);
        Issue issue = new Issue(
                parts[0],
                parts[1].replace("{{NL}}", "\n").replace("{{PIPE}}", "|"),
                parts[2],
                parts[3]
        );
        issue.setOpen(Boolean.parseBoolean(parts[4]));
        issue.timestamp = parts[5];
        return issue;
    }
}