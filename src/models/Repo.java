package models;

import java.util.ArrayList;

public class Repo {
    private String repoName;
    private String ownerUsername;
    private String description;
    private boolean isPublic;
    private String readme;
    private ArrayList<String> files;
    private String language;
    private int stars;
    private int commits;

    public Repo(String repoName, String ownerUsername, String description, boolean isPublic) {
        this.repoName = repoName;
        this.ownerUsername = ownerUsername;
        this.description = description;
        this.isPublic = isPublic;
        this.readme = "# " + repoName + "\n\nAdd your project description here.";
        this.files = new ArrayList<>();
        this.language = "None";
        this.stars = 0;
        this.commits = 0;
    }

    // Getters
    public String getRepoName() { return repoName; }
    public String getOwnerUsername() { return ownerUsername; }
    public String getDescription() { return description; }
    public boolean isPublic() { return isPublic; }
    public String getReadme() { return readme; }
    public ArrayList<String> getFiles() { return files; }

    // Get files in a specific folder
    public ArrayList<String> getFilesInFolder(String folderPath) {
        ArrayList<String> result = new ArrayList<>();
        for (String file : files) {
            if (folderPath.isEmpty()) {
                // Root level — no slash
                if (!file.contains("/")) {
                    result.add(file);
                }
            } else {
                // Files inside this folder
                if (file.startsWith(folderPath + "/")) {
                    String remaining = file.substring(folderPath.length() + 1);
                    if (!remaining.contains("/")) {
                        result.add(file); // full path
                    }
                }
            }
        }
        return result;
    }

    // Get folders in a specific path
    public ArrayList<String> getFoldersInPath(String folderPath) {
        ArrayList<String> folders = new ArrayList<>();
        for (String file : files) {
            if (folderPath.isEmpty()) {
                // Root level folders
                if (file.contains("/")) {
                    String folder = file.substring(0, file.indexOf("/"));
                    if (!folders.contains(folder)) {
                        folders.add(folder);
                    }
                }
            } else {
                // Sub folders
                if (file.startsWith(folderPath + "/")) {
                    String remaining = file.substring(folderPath.length() + 1);
                    if (remaining.contains("/")) {
                        String subFolder = remaining.substring(0, remaining.indexOf("/"));
                        String fullFolder = folderPath + "/" + subFolder;
                        if (!folders.contains(fullFolder)) {
                            folders.add(fullFolder);
                        }
                    }
                }
            }
        }
        return folders;
    }

    // Get display name (last part of path)
    public static String getDisplayName(String path) {
        if (path.contains("/")) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }


    public String getLanguage() { return language; }
    public int getStars() { return stars; }
    public int getCommits() { return commits; }

    // Setters
    public void setRepoName(String repoName) { this.repoName = repoName; }
    public void setDescription(String description) { this.description = description; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
    public void setReadme(String readme) { this.readme = readme; }
    public void setLanguage(String language) { this.language = language; }
    public void setStars(int stars) { this.stars = stars; }
    public void setCommits(int commits) { this.commits = commits; }

    // File operations
    public void addFile(String fileName) {
        if (!files.contains(fileName)) {
            files.add(fileName);
            detectLanguage();
        }
    }

    public void removeFile(String fileName) {
        files.remove(fileName);
        detectLanguage();
    }

    public void renameFile(String oldName, String newName) {
        int index = files.indexOf(oldName);
        if (index != -1) {
            files.set(index, newName);
            detectLanguage();
        }
    }

    // Auto detect language from file extensions
    private void detectLanguage() {
        for (String file : files) {
            if (file.endsWith(".java")) { language = "Java"; return; }
            if (file.endsWith(".py")) { language = "Python"; return; }
            if (file.endsWith(".cpp") || file.endsWith(".c")) { language = "C++"; return; }
            if (file.endsWith(".js")) { language = "JavaScript"; return; }
            if (file.endsWith(".html")) { language = "HTML"; return; }
            if (file.endsWith(".cs")) { language = "C#"; return; }
        }
        language = "None";
    }

    // Save format
    public String toFileString() {
        String fileList = String.join(",", files);
        return repoName + "|" + ownerUsername + "|" + description + "|" + isPublic + "|" +
                language + "|" + stars + "|" + commits + "|" + fileList;
    }

    // Load format
    public static Repo fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        Repo r = new Repo(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3]));
        r.setLanguage(parts[4]);
        r.setStars(Integer.parseInt(parts[5]));
        r.setCommits(Integer.parseInt(parts[6]));
        if (!parts[7].isEmpty()) {
            for (String f : parts[7].split(",")) {
                r.getFiles().add(f);
            }
        }
        return r;
    }
}