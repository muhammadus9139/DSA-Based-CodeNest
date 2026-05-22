package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Commit {
    private String message;
    private String fileName;
    private String content;
    private String timestamp;

    public Commit(String message, String fileName, String content) {
        this.message = message;
        this.fileName = fileName;
        this.content = content;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getMessage() { return message; }
    public String getFileName() { return fileName; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }

    public String toFileString() {
        String safeContent = content.replace("\n", "{{NL}}").replace("|", "{{PIPE}}");
        return message + "|" + fileName + "|" + timestamp + "|" + safeContent;
    }

    public static Commit fromFileString(String line) {
        String[] parts = line.split("\\|", 4);
        Commit c = new Commit(parts[0], parts[1], parts[3]
                .replace("{{NL}}", "\n")
                .replace("{{PIPE}}", "|"));
        c.timestamp = parts[2];
        return c;
    }

    private String timestamp(String t) { return this.timestamp = t; }
}