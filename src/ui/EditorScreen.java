package ui;

import dsa.GroqCopilot;
import data.ActivityTracker;
import data.CommitStorage;
import data.IssueStorage;
import data.RepoStorage;
import data.UserStorage;
import dsa.GeminiCopilot;
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

public class EditorScreen {

    private Stage stage;
    private User currentUser;
    private Repo repo;
    private String fileName;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;
    private GroqCopilot groqCopilot = new GroqCopilot();

    public EditorScreen(Stage stage, User currentUser, Repo repo, String fileName,
                        UserStorage userStorage, RepoStorage repoStorage,
                        CommitStorage commitStorage, IssueStorage issueStorage,
                        ActivityTracker activityTracker) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.repo = repo;
        this.fileName = fileName;
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

        // Breadcrumb
        HBox breadcrumb = new HBox(4);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);

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

        Text fileText = new Text(fileName);
        fileText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        fileText.setFill(Color.web("#e6edf3"));

        breadcrumb.getChildren().addAll(ownerText, slash1, repoText, slash2, fileText);

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        // Top right buttons
        Button cancelBtn = new Button("Cancel changes");
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

        Button commitBtn = new Button("Commit changes ▾");
        commitBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 13;
                -fx-padding: 6 14;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """);

        navbar.getChildren().addAll(logo, breadcrumb, navSpacer, cancelBtn, commitBtn);

        // ── MAIN CONTENT ──
        HBox mainContent = new HBox(0);
        mainContent.setStyle("-fx-background-color: #0d1117;");
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Left — file tree
        VBox fileTree = new VBox(0);
        fileTree.setPrefWidth(220);
        fileTree.setMinWidth(220);
        fileTree.setMaxWidth(220);
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

            boolean isCurrentFile = file.equals(fileName);

            fileItem.setStyle(isCurrentFile ?
                    "-fx-background-color: #21262d; -fx-cursor: hand;" :
                    "-fx-cursor: hand;");

            fileItem.setOnMouseEntered(e -> {
                if (!file.equals(fileName))
                    fileItem.setStyle("-fx-background-color: #1c2128; -fx-cursor: hand;");
            });
            fileItem.setOnMouseExited(e -> {
                if (!file.equals(fileName))
                    fileItem.setStyle("-fx-cursor: hand;");
            });

            Text fileIcon = new Text(getFileIcon(Repo.getDisplayName(file)));
            fileIcon.setFont(Font.font("Arial", 12));
            fileIcon.setMouseTransparent(true);

            Text fileLbl = new Text(Repo.getDisplayName(file));
            fileLbl.setFont(Font.font("Arial", 12));
            fileLbl.setFill(isCurrentFile ? Color.web("#e6edf3") : Color.web("#8b949e"));
            fileLbl.setMouseTransparent(true);

            fileItem.getChildren().addAll(fileIcon, fileLbl);

            fileItem.setOnMouseClicked(e -> {
                if (!file.equals(fileName)) {
                    EditorScreen newEditor = new EditorScreen(stage, currentUser, repo, file,
                            userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                    stage.setScene(newEditor.getScene());
                    stage.setMaximized(true);
                }
            });

            treeItems.getChildren().add(fileItem);
        }

        fileTree.getChildren().addAll(treeHeader, treeItems);

        // Center — editor with line numbers
        HBox editorBox = new HBox(0);
        HBox.setHgrow(editorBox, Priority.ALWAYS);

        // Line numbers
        TextArea lineNumbers = new TextArea();
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
        HBox.setHgrow(editor, Priority.ALWAYS);
        editor.setText(loadFileContent());

        // Update line numbers
        updateLineNumbers(lineNumbers, editor.getText());
        editor.textProperty().addListener((obs, oldVal, newVal) -> {
            updateLineNumbers(lineNumbers, newVal);
        });

        // Sync scroll
        editor.scrollTopProperty().addListener((obs, oldVal, newVal) -> {
            lineNumbers.setScrollTop(newVal.doubleValue());
        });

        editorBox.getChildren().addAll(lineNumbers, editor);

        // Right — Copilot panel
        VBox suggestionBox = new VBox(8);
        suggestionBox.setPadding(new Insets(8));

        Label defaultMsg = new Label("💡 Press Enter for suggestions");
        defaultMsg.setTextFill(Color.web("#6c7086"));
        defaultMsg.setFont(Font.font("Arial", 12));
        defaultMsg.setWrapText(true);
        suggestionBox.getChildren().add(defaultMsg);

        VBox copilotPanel = new VBox(12);
        copilotPanel.setPadding(new Insets(16));
        copilotPanel.setPrefWidth(260);
        copilotPanel.setMinWidth(260);
        copilotPanel.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 0 1;");

        Text copilotTitle = new Text("🤖 Copilot");
        copilotTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        copilotTitle.setFill(Color.web("#e6edf3"));

        Button reviewBtn = new Button("🔍 Review Code");
        reviewBtn.setMaxWidth(Double.MAX_VALUE);
        reviewBtn.setStyle("""
                -fx-background-color: #21262d;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 13;
                -fx-padding: 8 16;
                -fx-cursor: hand;
                """);

        Button historyBtn = new Button("📋 Commit History");
        historyBtn.setMaxWidth(Double.MAX_VALUE);
        historyBtn.setStyle("""
                -fx-background-color: #21262d;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 13;
                -fx-padding: 8 16;
                -fx-cursor: hand;
                """);

        Separator copSep = new Separator();
        copSep.setStyle("-fx-background-color: #30363d;");

        ScrollPane suggestionScroll = new ScrollPane(suggestionBox);
        suggestionScroll.setFitToWidth(true);
        suggestionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        suggestionScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(suggestionScroll, Priority.ALWAYS);

        copilotPanel.getChildren().addAll(copilotTitle, reviewBtn, historyBtn, copSep, suggestionScroll);

        mainContent.getChildren().addAll(fileTree, editorBox, copilotPanel);

        // ── COMMIT DIALOG (hidden) ──
        VBox commitPanel = new VBox(12);
        commitPanel.setPadding(new Insets(16, 24, 40, 24));
        commitPanel.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 1 0 0 0;");
        commitPanel.setVisible(false);
        commitPanel.setManaged(false);

        Text commitTitle = new Text("Commit changes");
        commitTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        commitTitle.setFill(Color.web("#e6edf3"));

        TextField commitMsgField = new TextField("Update " + fileName);
        commitMsgField.setStyle("""
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """);
        commitMsgField.setMaxWidth(500);

        TextArea commitDescField = new TextArea();
        commitDescField.setPromptText("Add an optional extended description...");
        commitDescField.setPrefHeight(60);
        commitDescField.setMaxWidth(500);
        commitDescField.setStyle("""
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """);

        HBox branchInfo = new HBox(8);
        branchInfo.setAlignment(Pos.CENTER_LEFT);
        Text branchIcon = new Text("🌿");
        Text branchText = new Text("Commit directly to the ");
        branchText.setFill(Color.web("#8b949e"));
        branchText.setFont(Font.font("Arial", 13));
        Text branchName = new Text("main");
        branchName.setFill(Color.web("#58a6ff"));
        branchName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        Text branchText2 = new Text(" branch.");
        branchText2.setFill(Color.web("#8b949e"));
        branchText2.setFont(Font.font("Arial", 13));
        branchInfo.getChildren().addAll(branchIcon, branchText, branchName, branchText2);

        HBox commitBtnRow = new HBox(10);
        commitBtnRow.setAlignment(Pos.CENTER_LEFT);

        Button confirmCommitBtn = new Button("Commit changes");
        confirmCommitBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 13;
                -fx-padding: 8 20;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """);

        Button cancelCommitBtn = new Button("Cancel");
        cancelCommitBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #8b949e;
                -fx-font-size: 13;
                -fx-cursor: hand;
                """);

        Label saveMsg = new Label("");
        saveMsg.setTextFill(Color.web("#3fb950"));

        commitBtnRow.getChildren().addAll(confirmCommitBtn, cancelCommitBtn, saveMsg);
        commitPanel.getChildren().addAll(commitTitle, commitMsgField, commitDescField, branchInfo, commitBtnRow);

        // ── ACTIONS ──
        commitBtn.setOnAction(e -> {
            commitPanel.setVisible(true);
            commitPanel.setManaged(true);
            commitBtn.setDisable(true);
        });

        cancelBtn.setOnAction(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                    userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        cancelCommitBtn.setOnAction(e -> {
            commitPanel.setVisible(false);
            commitPanel.setManaged(false);
            commitBtn.setDisable(false);
        });

        confirmCommitBtn.setOnAction(e -> {
            String commitMsg = commitMsgField.getText().trim();
            if (commitMsg.isEmpty()) commitMsg = "Update " + fileName;

            saveFileContent(editor.getText());
            Commit commit = new Commit(commitMsg, fileName, editor.getText());
            commitStorage.saveCommit(currentUser.getUsername(), repo.getRepoName(), fileName, commit);
            activityTracker.recordCommit(currentUser.getUsername());

            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                    userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        // Copilot actions
        editor.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                String[] lines = editor.getText().split("\n");
                String lastLine = lines.length > 1 ? lines[lines.length - 2] : "";
                if (lastLine.trim().length() < 3) return;

                suggestionBox.getChildren().clear();
                Label loading = new Label("💡 Thinking...");
                loading.setTextFill(Color.web("#6c7086"));
                loading.setFont(Font.font("Arial", 13));
                suggestionBox.getChildren().add(loading);

                String finalLastLine = lastLine;
                new Thread(() -> {
                    String suggestion = groqCopilot.getSuggestion(finalLastLine);                    javafx.application.Platform.runLater(() -> {
                        suggestionBox.getChildren().clear();
                        Label lbl = new Label("💡 " + suggestion);
                        lbl.setTextFill(Color.web("#f9e2af"));
                        lbl.setFont(Font.font("Arial", 12));
                        lbl.setWrapText(true);
                        lbl.setPadding(new Insets(6, 8, 6, 8));
                        lbl.setStyle("-fx-background-color: #21262d; -fx-background-radius: 6;");
                        suggestionBox.getChildren().add(lbl);
                    });
                }).start();
            }
        });

        reviewBtn.setOnAction(e -> {
            suggestionBox.getChildren().clear();
            Label loading = new Label("🔍 Reviewing...");
            loading.setTextFill(Color.web("#6c7086"));
            loading.setFont(Font.font("Arial", 13));
            suggestionBox.getChildren().add(loading);

            String code = editor.getText();
            new Thread(() -> {
                String review = groqCopilot.reviewCode(code);
                javafx.application.Platform.runLater(() -> {
                    suggestionBox.getChildren().clear();
                    for (String line : review.split("\n")) {
                        if (line.trim().isEmpty()) continue;
                        Label lbl = new Label(line);
                        lbl.setFont(Font.font("Arial", 12));
                        lbl.setWrapText(true);
                        lbl.setPadding(new Insets(6, 8, 6, 8));
                        if (line.startsWith("❌")) {
                            lbl.setTextFill(Color.web("#f85149"));
                        } else if (line.startsWith("⚠️")) {
                            lbl.setTextFill(Color.web("#f0883e"));
                        } else {
                            lbl.setTextFill(Color.web("#3fb950"));
                        }
                        lbl.setStyle("-fx-background-color: #21262d; -fx-background-radius: 6;");
                        suggestionBox.getChildren().add(lbl);
                    }
                });
            }).start();
        });

        historyBtn.setOnAction(e -> {
            CommitHistoryScreen history = new CommitHistoryScreen(
                    stage, currentUser, repo, fileName, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(history.getScene());
            stage.setMaximized(true);
        });

        // ── ROOT ──
        VBox root = new VBox(navbar, mainContent, commitPanel);
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #0d1117;");

//        return new Scene(root);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
    }

    private void updateLineNumbers(TextArea lineNumbers, String text) {
        int lines = text.split("\n", -1).length;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    private String loadFileContent() {
        try {
            Path path = Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                    fileName.replace("/", File.separator));
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path));
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
        return "// Start coding here...\n";
    }

    private void saveFileContent(String content) {
        try {
            Path dir = Paths.get("repos", currentUser.getUsername(), repo.getRepoName());
            if (fileName.contains("/")) {
                dir = Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                        fileName.substring(0, fileName.lastIndexOf("/")).replace("/", File.separator));
            }
            Files.createDirectories(dir);
            Path filePath = Paths.get("repos", currentUser.getUsername(), repo.getRepoName(),
                    fileName.replace("/", File.separator));
            Files.write(filePath, content.getBytes());
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
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