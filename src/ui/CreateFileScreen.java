package ui;

import data.ActivityTracker;
import data.CommitStorage;
import data.IssueStorage;
import data.RepoStorage;
import data.UserStorage;
import models.Commit;
import models.Repo;
import models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;

public class CreateFileScreen {

    private Stage stage;
    private User currentUser;
    private Repo repo;
    private String currentPath;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public CreateFileScreen(Stage stage, User currentUser, Repo repo, String currentPath,
                            UserStorage userStorage, RepoStorage repoStorage,
                            CommitStorage commitStorage, IssueStorage issueStorage,
                            ActivityTracker activityTracker) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.repo = repo;
        this.currentPath = currentPath;
        this.userStorage = userStorage;
        this.repoStorage = repoStorage;
        this.commitStorage = commitStorage;
        this.issueStorage = issueStorage;
        this.activityTracker = activityTracker;
    }

    public Scene getScene() {

        // ── NAVBAR ──
        HBox navbar = new HBox(16);
        navbar.setPadding(new Insets(10, 24, 10, 24));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        Text logo = new Text("CodeNest");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logo.setFill(Color.WHITE);
        logo.setStyle("-fx-cursor: hand;");
        logo.setOnMouseClicked(e -> {
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #8b949e;
                -fx-font-size: 13;
                -fx-cursor: hand;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 6 14;
                """);

        Button commitBtn = new Button("Commit new file");
        commitBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 13;
                -fx-padding: 6 14;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """);

        navbar.getChildren().addAll(logo, navSpacer, cancelBtn, commitBtn);

        // ── PATH BAR ──
        HBox pathBar = new HBox(4);
        pathBar.setPadding(new Insets(10, 24, 10, 24));
        pathBar.setAlignment(Pos.CENTER_LEFT);
        pathBar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        Text ownerText = new Text(currentUser.getUsername());
        ownerText.setFont(Font.font("Arial", 14));
        ownerText.setFill(Color.web("#58a6ff"));
        ownerText.setStyle("-fx-cursor: hand;");
        ownerText.setOnMouseClicked(e -> {
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        Text slash1 = new Text(" / ");
        slash1.setFill(Color.web("#8b949e"));
        slash1.setFont(Font.font("Arial", 14));

        Text repoText = new Text(repo.getRepoName());
        repoText.setFont(Font.font("Arial", 14));
        repoText.setFill(Color.web("#58a6ff"));
        repoText.setStyle("-fx-cursor: hand;");
        repoText.setOnMouseClicked(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        Text slash2 = new Text(" / ");
        slash2.setFill(Color.web("#8b949e"));
        slash2.setFont(Font.font("Arial", 14));

        // File name field
        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Name your file...");
        fileNameField.setPrefWidth(300);
        fileNameField.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #e6edf3;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 6 10;
                -fx-font-size: 14;
                """);

        // Hint
        Text hint = new Text("  Tip: type folder/filename to create in folder");
        hint.setFont(Font.font("Arial", 12));
        hint.setFill(Color.web("#6e7681"));

        if (!currentPath.isEmpty()) {
            Text pathText = new Text(currentPath + " / ");
            pathText.setFill(Color.web("#58a6ff"));
            pathText.setFont(Font.font("Arial", 14));
            pathBar.getChildren().addAll(ownerText, slash1, repoText, slash2, pathText, fileNameField, hint);
        } else {
            pathBar.getChildren().addAll(ownerText, slash1, repoText, slash2, fileNameField, hint);
        }

        // ── MAIN CONTENT ──
        HBox mainContent = new HBox(0);
        mainContent.setStyle("-fx-background-color: #0d1117;");
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Left — file tree
        VBox fileTree = new VBox(0);
        fileTree.setPrefWidth(200);
        fileTree.setMinWidth(200);
        fileTree.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 1 0 0;");

        HBox treeHeader = new HBox(8);
        treeHeader.setPadding(new Insets(10, 12, 10, 12));
        treeHeader.setAlignment(Pos.CENTER_LEFT);
        treeHeader.setStyle("-fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");
        Text treeIcon = new Text("📁");
        Text treeName = new Text(repo.getRepoName());
        treeName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        treeName.setFill(Color.web("#e6edf3"));
        treeHeader.getChildren().addAll(treeIcon, treeName);

        VBox treeItems = new VBox(0);
        for (String file : repo.getFiles()) {
            HBox fileItem = new HBox(8);
            fileItem.setPadding(new Insets(5, 12, 5, 12));
            fileItem.setAlignment(Pos.CENTER_LEFT);
            fileItem.setStyle("-fx-cursor: hand;");
            fileItem.setOnMouseEntered(e -> fileItem.setStyle("-fx-background-color: #1c2128; -fx-cursor: hand;"));
            fileItem.setOnMouseExited(e -> fileItem.setStyle("-fx-cursor: hand;"));

            Text fileIcon = new Text(getFileIcon(Repo.getDisplayName(file)));
            fileIcon.setFont(Font.font("Arial", 12));
            fileIcon.setMouseTransparent(true);

            Text fileLbl = new Text(Repo.getDisplayName(file));
            fileLbl.setFont(Font.font("Arial", 12));
            fileLbl.setFill(Color.web("#8b949e"));
            fileLbl.setMouseTransparent(true);

            fileItem.getChildren().addAll(fileIcon, fileLbl);
            treeItems.getChildren().add(fileItem);
        }

        fileTree.getChildren().addAll(treeHeader, treeItems);

        // Center — editor with line numbers
        HBox editorBox = new HBox(0);
        HBox.setHgrow(editorBox, Priority.ALWAYS);

        // Line numbers
        TextArea lineNumbers = new TextArea("1");
        lineNumbers.setPrefWidth(50);
        lineNumbers.setMinWidth(50);
        lineNumbers.setMaxWidth(50);
        lineNumbers.setEditable(false);
        lineNumbers.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #8b949e;
                -fx-font-family: 'Courier New';
                -fx-font-size: 14;
                -fx-padding: 16 8 16 8;
                -fx-border-color: #30363d;
                -fx-border-width: 0 1 0 0;
                """);

        // Code editor
        TextArea editor = new TextArea();
        editor.setFont(Font.font("Courier New", 14));
        editor.setStyle("""
                -fx-control-inner-background: #0d1117;
                -fx-background-color: #0d1117;
                -fx-text-fill: #e6edf3;
                -fx-padding: 16;
                -fx-border-color: transparent;
                """);
        editor.setWrapText(false);
        editor.setPromptText("// Start writing your code here...");
        HBox.setHgrow(editor, Priority.ALWAYS);

        // Line numbers update
        editor.textProperty().addListener((obs, oldVal, newVal) -> {
            int lines = newVal.split("\n", -1).length;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= lines; i++) sb.append(i).append("\n");
            lineNumbers.setText(sb.toString());
        });

        editor.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            lineNumbers.setScrollTop(newVal.doubleValue());
        });

        editorBox.getChildren().addAll(lineNumbers, editor);
        mainContent.getChildren().addAll(fileTree, editorBox);

        // ── COMMIT PANEL ──


        // ── ACTIONS ──
        // Commit action (both buttons)
        Runnable doCommit = () -> {
            String fileName = fileNameField.getText().trim();
            if (fileName.isEmpty()) {
                fileNameField.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #e6edf3;
                -fx-border-color: #f85149;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 6 10;
                -fx-font-size: 14;
                """);
                return;
            }

            String fullPath = currentPath.isEmpty() ? fileName : currentPath + "/" + fileName;
            String content = editor.getText();
            String commitMsg = "Create " + fileName;

            try {
                java.nio.file.Path dir = Paths.get("repos", currentUser.getUsername(), repo.getRepoName());
                if (fullPath.contains("/")) {
                    String folderPart = fullPath.substring(0, fullPath.lastIndexOf("/"));
                    dir = Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                            folderPart.replace("/", File.separator));
                }
                Files.createDirectories(dir);
                Files.write(Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                        fullPath.replace("/", File.separator)), content.getBytes());

                repo.addFile(fullPath);
                repo.setCommits(repo.getCommits() + 1);
                repoStorage.updateRepo(repo);

                Commit commit = new Commit(commitMsg, fullPath, content);
                commitStorage.saveCommit(currentUser.getUsername(), repo.getRepoName(), fullPath, commit);
                activityTracker.recordCommit(currentUser.getUsername());

                RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                        userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                stage.setScene(detail.getScene());
                stage.setMaximized(true);

            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        };

//        commitBtn.setOnAction(e -> doCommit.run());

        cancelBtn.setOnAction(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                    userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        commitBtn.setOnAction(e -> {
            String fileName = fileNameField.getText().trim();
            if (fileName.isEmpty()) {
                // Show error in path bar
                fileNameField.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #e6edf3;
                -fx-border-color: #f85149;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 6 10;
                -fx-font-size: 14;
                """);
                return;
            }

            String fullPath = currentPath.isEmpty() ? fileName : currentPath + "/" + fileName;
            String content = editor.getText();
            String commitMsg = "Create " + fileName;

            try {
                java.nio.file.Path dir = Paths.get("repos", currentUser.getUsername(), repo.getRepoName());
                if (fullPath.contains("/")) {
                    String folderPart = fullPath.substring(0, fullPath.lastIndexOf("/"));
                    dir = Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                            folderPart.replace("/", File.separator));
                }
                Files.createDirectories(dir);
                Files.write(Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                        fullPath.replace("/", File.separator)), content.getBytes());

                repo.addFile(fullPath);
                repo.setCommits(repo.getCommits() + 1);
                repoStorage.updateRepo(repo);

                Commit commit = new Commit(commitMsg, fullPath, content);
                commitStorage.saveCommit(currentUser.getUsername(), repo.getRepoName(), fullPath, commit);
                activityTracker.recordCommit(currentUser.getUsername());

                RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                        userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                stage.setScene(detail.getScene());
                stage.setMaximized(true);

            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                    userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        // ── ROOT ──
        VBox root = new VBox(navbar, pathBar, mainContent);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #0d1117;");

//        return new Scene(root);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(javafx.stage.Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(javafx.stage.Screen.getPrimary().getBounds().getHeight());
        Scene scene = new Scene(root);
        stage.setMaximized(true);
        return scene;
    }

    private String getFileIcon(String fileName) {
        if (fileName.endsWith(".java")) return "☕";
        if (fileName.endsWith(".py")) return "🐍";
        if (fileName.endsWith(".cpp") || fileName.endsWith(".c")) return "⚙️";
        if (fileName.endsWith(".js")) return "🟨";
        if (fileName.endsWith(".html")) return "🌐";
        if (fileName.endsWith(".cs")) return "🔷";
        if (fileName.endsWith(".kt")) return "🟣";
        if (fileName.endsWith(".md")) return "📝";
        return "📄";
    }
}