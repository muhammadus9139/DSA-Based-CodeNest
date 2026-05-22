package ui;

import data.ActivityTracker;
import data.CommitStorage;
import data.IssueStorage;
import data.RepoStorage;
import data.UserStorage;
import models.Issue;
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

import java.util.ArrayList;

public class RepoDetailScreen {

    private Stage stage;
    private User currentUser;
    private Repo repo;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;
    private String currentPath = "";

    public RepoDetailScreen(Stage stage, User currentUser, Repo repo,
                            UserStorage userStorage, RepoStorage repoStorage,
                            CommitStorage commitStorage, IssueStorage issueStorage,
                            ActivityTracker activityTracker) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.repo = repo;
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

        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-font-size: 13; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        navbar.getChildren().addAll(logo, navSpacer, backBtn);

        // ── REPO HEADER ──
        VBox repoHeader = new VBox(8);
        repoHeader.setPadding(new Insets(16, 24, 0, 24));
        repoHeader.setStyle("-fx-background-color: #0d1117;");

        // Breadcrumb
        HBox breadcrumb = new HBox(6);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);

        Text ownerText = new Text(currentUser.getUsername());
        ownerText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        ownerText.setFill(Color.web("#58a6ff"));
        ownerText.setStyle("-fx-cursor: hand;");
        ownerText.setOnMouseClicked(e -> {
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        Text slash = new Text("/");
        slash.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        slash.setFill(Color.web("#8b949e"));

        Text repoNameText = new Text(repo.getRepoName());
        repoNameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        repoNameText.setFill(Color.web("#58a6ff"));

        Label visTag = new Label(repo.isPublic() ? "Public" : "Private");
        visTag.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-border-color: #30363d; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11;");

        breadcrumb.getChildren().addAll(ownerText, slash, repoNameText, visTag);

        // Action buttons
        HBox actionBtns = new HBox(8);
        actionBtns.setAlignment(Pos.CENTER_LEFT);

        Button watchBtn = repoActionBtn("👁 Watch", "0");
        Button starBtn = repoActionBtn("⭐ Star", String.valueOf(repo.getStars()));
        Button forkBtn = repoActionBtn("🍴 Fork", "0");

        starBtn.setOnAction(e -> {
            repo.setStars(repo.getStars() + 1);
            repoStorage.updateRepo(repo);
            stage.setScene(getScene());
            stage.setMaximized(true);
        });

        forkBtn.setOnAction(e -> {
            Repo forked = new Repo(repo.getRepoName() + "-fork", currentUser.getUsername(),
                    "Forked from " + repo.getOwnerUsername() + "/" + repo.getRepoName(), true);
            for (String f : repo.getFiles()) forked.addFile(f);
            repoStorage.createRepo(forked);
            forkBtn.setText("🍴 Forked!");
            forkBtn.setDisable(true);
        });

        actionBtns.getChildren().addAll(watchBtn, starBtn, forkBtn);
        repoHeader.getChildren().addAll(breadcrumb, actionBtns);

        // ── TAB BAR ──
        HBox tabBar = new HBox(0);
        tabBar.setPadding(new Insets(0, 24, 0, 24));
        tabBar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        int issueCount = issueStorage.getIssues(currentUser.getUsername(), repo.getRepoName()).size();

        Button codeTab = tabBtn("📂 Code", true);
        Button issuesTab = tabBtn("🐛 Issues  " + issueCount, false);
        Button prsTab = tabBtn("🔀 Pull Requests", false);
        Button actionsTab = tabBtn("⚙️ Actions", false);
        Button projectsTab = tabBtn("📋 Projects", false);
        Button settingsTab = tabBtn("⚙️ Settings", false);

        tabBar.getChildren().addAll(codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab);

        // ── CONTENT AREA ──
        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #0d1117;");

        HBox codeContent = createCodeTab();
        VBox issuesContent = createIssuesTab();
        VBox prsContent = createDummyTab("🔀 Pull Requests", "No pull requests yet.", "Pull requests help you collaborate on code with other people.");
        VBox actionsContent = createDummyTab("⚙️ Actions", "No workflows found.", "Automate your workflow from idea to production.");
        VBox projectsContent = createDummyTab("📋 Projects", "No projects yet.", "Coordinate, track, and update your work in one place.");
        VBox settingsContent = createSettingsTab();

        issuesContent.setVisible(false); issuesContent.setManaged(false);
        prsContent.setVisible(false); prsContent.setManaged(false);
        actionsContent.setVisible(false); actionsContent.setManaged(false);
        projectsContent.setVisible(false); projectsContent.setManaged(false);
        settingsContent.setVisible(false); settingsContent.setManaged(false);

        contentArea.getChildren().addAll(codeContent, issuesContent, prsContent, actionsContent, projectsContent, settingsContent);

        // Tab switching
        codeTab.setOnAction(e -> { setActiveTab(new Button[]{codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab}, 0); showOnly(codeContent, issuesContent, prsContent, actionsContent, projectsContent, settingsContent); });
        issuesTab.setOnAction(e -> { setActiveTab(new Button[]{codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab}, 1); showOnly(issuesContent, codeContent, prsContent, actionsContent, projectsContent, settingsContent); });
        prsTab.setOnAction(e -> { setActiveTab(new Button[]{codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab}, 2); showOnly(prsContent, codeContent, issuesContent, actionsContent, projectsContent, settingsContent); });
        actionsTab.setOnAction(e -> { setActiveTab(new Button[]{codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab}, 3); showOnly(actionsContent, codeContent, issuesContent, prsContent, projectsContent, settingsContent); });
        projectsTab.setOnAction(e -> { setActiveTab(new Button[]{codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab}, 4); showOnly(projectsContent, codeContent, issuesContent, prsContent, actionsContent, settingsContent); });
        settingsTab.setOnAction(e -> { setActiveTab(new Button[]{codeTab, issuesTab, prsTab, actionsTab, projectsTab, settingsTab}, 5); showOnly(settingsContent, codeContent, issuesContent, prsContent, actionsContent, projectsContent); });

        // ── ROOT ──
        VBox root = new VBox(navbar, repoHeader, tabBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #0d1117;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");

//        return new Scene(scrollPane);
//        Scene scene = new Scene(scrollPane);
//
//        stage.setMaximized(true);
//        return scene;
        Scene scene = new Scene(scrollPane);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(javafx.stage.Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(javafx.stage.Screen.getPrimary().getBounds().getHeight());
        stage.setMaximized(true);
        return scene;
    }

    // ── CODE TAB ──
    private HBox createCodeTab() {
        HBox contentt = new HBox(16);
        contentt.setPadding(new Insets(20, 24, 20, 24));
        contentt.setStyle("-fx-background-color: #0d1117;");

        // Left section
        VBox leftSection = new VBox(16);
        HBox.setHgrow(leftSection, Priority.ALWAYS);

        // Files box
        VBox filesBox = new VBox(0);
        filesBox.setStyle("-fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Files header
        HBox filesHeader = new HBox(10);
        filesHeader.setPadding(new Insets(10, 12, 10, 12));
        filesHeader.setAlignment(Pos.CENTER_LEFT);
        filesHeader.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8 8 0 0; -fx-background-radius: 8 8 0 0;");

        // Branch button
        Button branchBtn = new Button("🌿 main ▾");
        branchBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;");

        Region fhSpacer = new Region();
        HBox.setHgrow(fhSpacer, Priority.ALWAYS);

        // Go to file
        Button goToFileBtn = new Button("🔍 Go to file");
        goToFileBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;");

        // Add file dropdown
        Button addFileBtn = new Button("+ Add file ▾");
        addFileBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;");

        // Code button
        Button codeBtn = new Button("< > Code ▾");
        codeBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;");

        filesHeader.getChildren().addAll(branchBtn, fhSpacer, goToFileBtn, addFileBtn, codeBtn);

        // Files list
        VBox filesList = new VBox(0);
        filesList.setStyle("-fx-background-color: #0d1117; -fx-border-radius: 0 0 8 8; -fx-background-radius: 0 0 8 8;");

        // Go to file search (hidden)
        HBox goToFileBox = new HBox(8);
        goToFileBox.setPadding(new Insets(8, 12, 8, 12));
        goToFileBox.setAlignment(Pos.CENTER_LEFT);
        goToFileBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");
        goToFileBox.setVisible(false);
        goToFileBox.setManaged(false);

        TextField goToFileField = new TextField();
        goToFileField.setPromptText("Go to file...");
        goToFileField.setPrefWidth(300);
        goToFileField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10; -fx-font-size: 13;");
        HBox.setHgrow(goToFileField, Priority.ALWAYS);

        Button closeGoToFile = new Button("✕");
        closeGoToFile.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-cursor: hand;");

        goToFileBox.getChildren().addAll(goToFileField, closeGoToFile);

        goToFileBtn.setOnAction(e -> {
            goToFileBox.setVisible(true);
            goToFileBox.setManaged(true);
            goToFileField.requestFocus();
        });

        closeGoToFile.setOnAction(e -> {
            goToFileBox.setVisible(false);
            goToFileBox.setManaged(false);
        });

        // Filter files by search
        goToFileField.textProperty().addListener((obs, oldVal, newVal) -> {
            refreshFilesList(filesList, null, newVal.trim());
        });

        HBox addFileForm = new HBox(8);
        addFileForm.setVisible(false);
        addFileForm.setManaged(false);

        refreshFilesList(filesList, addFileForm, "");

        // Add file dropdown menu
        ContextMenu addFileMenu = new ContextMenu();
        MenuItem createFileItem = new MenuItem("📄 Create new file");
        MenuItem uploadFileItem = new MenuItem("📤 Upload files");
        addFileMenu.getItems().addAll(createFileItem, uploadFileItem);

        createFileItem.setOnAction(e -> {
            CreateFileScreen createScreen = new CreateFileScreen(stage, currentUser, repo, currentPath, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(createScreen.getScene());
            stage.setMaximized(true);
        });

        uploadFileItem.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Upload Files");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Code Files", "*.java", "*.py", "*.cpp", "*.js", "*.html", "*.cs", "*.kt", "*.c"),
                    new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
            );
            java.util.List<java.io.File> files = fileChooser.showOpenMultipleDialog(stage);
            if (files != null) {

                // ── Commit Dialog ──
                Dialog<String> commitDialog = new Dialog<>();
                commitDialog.setTitle("Commit uploaded files");
                commitDialog.setHeaderText("Commit " + files.size() + " file(s) to " + repo.getRepoName());

                DialogPane dialogPane = commitDialog.getDialogPane();
                dialogPane.setStyle("-fx-background-color: #161b22;");

                TextField commitMsgField = new TextField("Add " + files.size() + " file(s)");
                commitMsgField.setStyle("""
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """);

                Label filesLabel = new Label("Files to upload:");
                filesLabel.setTextFill(Color.web("#8b949e"));
                StringBuilder fileNames = new StringBuilder();
                for (java.io.File f : files) fileNames.append("📄 ").append(f.getName()).append("\n");
                Label filesList2 = new Label(fileNames.toString());
                filesList2.setTextFill(Color.web("#c9d1d9"));

                VBox content = new VBox(10, filesLabel, filesList2, new Label("Commit message:") {{
                    setTextFill(Color.web("#8b949e"));
                }}, commitMsgField);
                content.setPadding(new Insets(10));
                dialogPane.setContent(content);

                ButtonType commitBtnType = new ButtonType("Commit files", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialogPane.getButtonTypes().addAll(commitBtnType, cancelBtnType);

                commitDialog.setResultConverter(btn -> {
                    if (btn == commitBtnType) return commitMsgField.getText().trim();
                    return null;
                });

                commitDialog.showAndWait().ifPresent(commitMsg -> {
                    if (commitMsg != null && !commitMsg.isEmpty()) {
                        for (java.io.File file : files) {
                            try {
                                String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                                java.nio.file.Path dir = java.nio.file.Paths.get("repos", currentUser.getUsername(), repo.getRepoName());
                                java.nio.file.Files.createDirectories(dir);
                                java.nio.file.Files.write(dir.resolve(file.getName()), fileContent.getBytes());

                                String fullPath = currentPath.isEmpty() ? file.getName() : currentPath + "/" + file.getName();
                                repo.addFile(fullPath);

                                // Save commit
                                models.Commit commit = new models.Commit(commitMsg, fullPath, fileContent);
                                commitStorage.saveCommit(currentUser.getUsername(), repo.getRepoName(), fullPath, commit);
                                activityTracker.recordCommit(currentUser.getUsername());

                            } catch (java.io.IOException ex) {
                                System.out.println("Error uploading: " + ex.getMessage());
                            }
                        }
                        repo.setCommits(repo.getCommits() + files.size());
                        repoStorage.updateRepo(repo);
                        refreshFilesList(filesList, addFileForm, "");
                    }
                });
            }
        });

        addFileBtn.setOnAction(e -> addFileMenu.show(addFileBtn, javafx.geometry.Side.BOTTOM, 0, 0));

        filesBox.getChildren().addAll(filesHeader, goToFileBox, filesList);
        leftSection.getChildren().add(filesBox);

        // README preview
        if (!repo.getReadme().isEmpty()) {
            VBox readmeBox = new VBox(0);
            readmeBox.setStyle("-fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");

            HBox readmeHeader = new HBox(8);
            readmeHeader.setPadding(new Insets(10, 12, 10, 12));
            readmeHeader.setAlignment(Pos.CENTER_LEFT);
            readmeHeader.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0; -fx-border-radius: 8 8 0 0; -fx-background-radius: 8 8 0 0;");

            Text readmeIcon = new Text("📄");
            Text readmeTitle = new Text("README.md");
            readmeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            readmeTitle.setFill(Color.web("#c9d1d9"));


            readmeHeader.getChildren().addAll(readmeIcon, readmeTitle);

            VBox readmeContent = new VBox(8);
            readmeContent.setPadding(new Insets(16));
            readmeContent.setStyle("-fx-background-color: #0d1117;");

            // Parse README - show headings colored
            String[] lines = repo.getReadme().split("\n");
            for (String line : lines) {
                Text lineText;
                if (line.startsWith("# ")) {
                    lineText = new Text(line.substring(2));
                    lineText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                    lineText.setFill(Color.web("#e6edf3"));
                } else if (line.startsWith("## ")) {
                    lineText = new Text(line.substring(3));
                    lineText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                    lineText.setFill(Color.web("#e6edf3"));
                } else if (line.startsWith("### ")) {
                    lineText = new Text(line.substring(4));
                    lineText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    lineText.setFill(Color.web("#e6edf3"));
                } else {
                    lineText = new Text(line);
                    lineText.setFont(Font.font("Arial", 14));
                    lineText.setFill(Color.web("#8b949e"));
                }
                lineText.setWrappingWidth(500);
                readmeContent.getChildren().add(lineText);
            }

            readmeBox.getChildren().addAll(readmeHeader, readmeContent);
            leftSection.getChildren().add(readmeBox);
        }

        // Right — About section
        VBox aboutCol = new VBox(16);
        aboutCol.setPrefWidth(260);
        aboutCol.setMinWidth(260);
        aboutCol.setMaxWidth(260);

        Text aboutTitle = new Text("About");
        aboutTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        aboutTitle.setFill(Color.web("#e6edf3"));

        Text aboutDesc = new Text(repo.getDescription().isEmpty() ? "No description provided." : repo.getDescription());
        aboutDesc.setFont(Font.font("Arial", 13));
        aboutDesc.setFill(Color.web("#8b949e"));
        aboutDesc.setWrappingWidth(240);

        Separator aboutSep = new Separator();
        aboutSep.setStyle("-fx-background-color: #30363d;");

        VBox aboutStats = new VBox(10);
        if (!repo.getLanguage().equals("None")) {
            aboutStats.getChildren().add(aboutItem("🔵", repo.getLanguage()));
        }
        aboutStats.getChildren().addAll(
                aboutItem("⭐", repo.getStars() + " stars"),
                aboutItem("👁", "0 watching"),
                aboutItem("🍴", "0 forks"),
                aboutItem("📝", repo.getCommits() + " commits"),
                aboutItem("🐛", issueStorage.getIssues(currentUser.getUsername(), repo.getRepoName()).size() + " issues"),
                aboutItem(repo.isPublic() ? "🌐" : "🔒", repo.isPublic() ? "Public" : "Private")
        );

        // Releases
        Separator relSep = new Separator();
        relSep.setStyle("-fx-background-color: #30363d;");
        Text relTitle = new Text("Releases");
        relTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        relTitle.setFill(Color.web("#e6edf3"));
        Text noRel = new Text("No releases published");
        noRel.setFont(Font.font("Arial", 12));
        noRel.setFill(Color.web("#8b949e"));

        // Languages
        Separator langSep = new Separator();
        langSep.setStyle("-fx-background-color: #30363d;");
        Text langTitle = new Text("Languages");
        langTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        langTitle.setFill(Color.web("#e6edf3"));

        HBox langBar = new HBox();
        langBar.setPrefHeight(8);
        langBar.setPrefWidth(240);
        langBar.setStyle("-fx-background-color: #2d6a4f; -fx-background-radius: 4;");

        Text langText = new Text("🔵 " + repo.getLanguage() + "  100%");
        langText.setFont(Font.font("Arial", 12));
        langText.setFill(Color.web("#8b949e"));

        aboutCol.getChildren().addAll(
                aboutTitle, aboutDesc, aboutSep, aboutStats,
                relSep, relTitle, noRel,
                langSep, langTitle, langBar, langText
        );

        contentt.getChildren().addAll(leftSection, aboutCol);
        return contentt;
    }

    private void refreshFilesList(VBox filesList, HBox addFileForm, String filter) {
        filesList.getChildren().clear();

        // Breadcrumb
        HBox breadcrumb = new HBox(4);
        breadcrumb.setAlignment(Pos.CENTER_LEFT);
        breadcrumb.setPadding(new Insets(8, 12, 8, 12));
        breadcrumb.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        Text rootLink = new Text(repo.getRepoName());
        rootLink.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        rootLink.setFill(Color.web("#58a6ff"));
        rootLink.setStyle("-fx-cursor: hand;");
        rootLink.setOnMouseClicked(e -> {
            currentPath = "";
            refreshFilesList(filesList, addFileForm, "");
        });
        breadcrumb.getChildren().add(rootLink);

        if (!currentPath.isEmpty()) {
            String[] parts = currentPath.split("/");
            StringBuilder pathSoFar = new StringBuilder();
            for (String part : parts) {
                Text sep = new Text(" / ");
                sep.setFill(Color.web("#8b949e"));
                sep.setFont(Font.font("Arial", 13));
                if (pathSoFar.length() > 0) pathSoFar.append("/");
                pathSoFar.append(part);
                String finalPath = pathSoFar.toString();
                Text partLink = new Text(part);
                partLink.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                partLink.setFill(Color.web("#58a6ff"));
                partLink.setStyle("-fx-cursor: hand;");
                partLink.setOnMouseClicked(ev -> {
                    currentPath = finalPath;
                    refreshFilesList(filesList, addFileForm, "");
                });
                breadcrumb.getChildren().addAll(sep, partLink);
            }
        }

        filesList.getChildren().add(breadcrumb);

        // Folders
        ArrayList<String> folders = repo.getFoldersInPath(currentPath);
        for (String folderPath : folders) {
            String folderName = Repo.getDisplayName(folderPath);
            if (!filter.isEmpty() && !folderName.toLowerCase().contains(filter.toLowerCase())) continue;

            HBox folderRow = new HBox(12);
            folderRow.setPadding(new Insets(8, 12, 8, 12));
            folderRow.setAlignment(Pos.CENTER_LEFT);
            folderRow.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
            folderRow.setOnMouseEntered(e -> folderRow.setStyle("-fx-background-color: #161b22; -fx-border-color: #21262d; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
            folderRow.setOnMouseExited(e -> folderRow.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));

            Text folderIcon = new Text("📁");
            folderIcon.setFont(Font.font("Arial", 14));
            Text folderNameText = new Text(folderName);
            folderNameText.setFont(Font.font("Arial", 13));
            folderNameText.setFill(Color.web("#c9d1d9"));

            Region rowSpacer = new Region();
            HBox.setHgrow(rowSpacer, Priority.ALWAYS);
            Text timeText = new Text("recently");
            timeText.setFont(Font.font("Arial", 12));
            timeText.setFill(Color.web("#8b949e"));

            folderRow.getChildren().addAll(folderIcon, folderNameText, rowSpacer, timeText);
            folderRow.setOnMouseClicked(e -> {
                currentPath = folderPath;
                refreshFilesList(filesList, addFileForm, "");
            });
            filesList.getChildren().add(folderRow);
        }

        // Files
        ArrayList<String> filesInPath = repo.getFilesInFolder(currentPath);
        if (folders.isEmpty() && filesInPath.isEmpty()) {
            HBox emptyRow = new HBox();
            emptyRow.setPadding(new Insets(20));
            emptyRow.setAlignment(Pos.CENTER);
            Label emptyLabel = new Label("No files yet. Click '+ Add file' to get started!");
            emptyLabel.setTextFill(Color.web("#8b949e"));
            emptyLabel.setFont(Font.font("Arial", 13));
            emptyRow.getChildren().add(emptyLabel);
            filesList.getChildren().add(emptyRow);
            return;
        }

        for (String filePath : filesInPath) {
            String fileName = Repo.getDisplayName(filePath);
            if (!filter.isEmpty() && !fileName.toLowerCase().contains(filter.toLowerCase())) continue;

            HBox fileRow = new HBox(12);
            fileRow.setPadding(new Insets(8, 12, 8, 12));
            fileRow.setAlignment(Pos.CENTER_LEFT);
            fileRow.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");
            fileRow.setOnMouseEntered(e -> fileRow.setStyle("-fx-background-color: #161b22; -fx-border-color: #21262d; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
            fileRow.setOnMouseExited(e -> fileRow.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));

            Text fileIcon = new Text(getFileIcon(fileName));
            fileIcon.setFont(Font.font("Arial", 14));
            Text fileNameText = new Text(fileName);
            fileNameText.setFont(Font.font("Arial", 13));
            fileNameText.setFill(Color.web("#c9d1d9"));

            Region rowSpacer = new Region();
            HBox.setHgrow(rowSpacer, Priority.ALWAYS);

            Text timeText = new Text("recently");
            timeText.setFont(Font.font("Arial", 12));
            timeText.setFill(Color.web("#8b949e"));

            Button renameBtn = new Button("✏️");
            renameBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-font-size: 11; -fx-cursor: hand;");

            Button deleteFileBtn = new Button("🗑");
            deleteFileBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #f85149; -fx-font-size: 11; -fx-cursor: hand;");

            renameBtn.setOnAction(e -> {
                TextInputDialog dialog = new TextInputDialog(fileName);
                dialog.setTitle("Rename File");
                dialog.setHeaderText("Enter new name:");
                dialog.showAndWait().ifPresent(newName -> {
                    if (!newName.trim().isEmpty()) {
                        String newPath = currentPath.isEmpty() ? newName.trim() : currentPath + "/" + newName.trim();
                        repo.renameFile(filePath, newPath);
                        repoStorage.updateRepo(repo);
                        refreshFilesList(filesList, addFileForm, "");

                    }
                });
            });

            deleteFileBtn.setOnAction(e -> {
                repo.removeFile(filePath);
                repoStorage.updateRepo(repo);
                refreshFilesList(filesList, addFileForm, "");

            });

            fileRow.setOnMouseClicked(e -> {
                if (e.getTarget() != renameBtn && e.getTarget() != deleteFileBtn) {
                    EditorScreen editor = new EditorScreen(stage, currentUser, repo, filePath, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                    stage.setScene(editor.getScene());
                    stage.setMaximized(true);
                }
            });

            fileRow.getChildren().addAll(fileIcon, fileNameText, rowSpacer, timeText, renameBtn, deleteFileBtn);
            filesList.getChildren().add(fileRow);
        }
    }

    // ── ISSUES TAB ──
    private VBox createIssuesTab() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(20, 24, 20, 24));
        content.setStyle("-fx-background-color: #0d1117;");

        HBox issueHeader = new HBox(10);
        issueHeader.setAlignment(Pos.CENTER_LEFT);
        Text issueTitle = new Text("Issues");
        issueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        issueTitle.setFill(Color.WHITE);
        Region iSpacer = new Region();
        HBox.setHgrow(iSpacer, Priority.ALWAYS);
        Button newIssueBtn = new Button("+ New Issue");
        newIssueBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 6 16; -fx-background-radius: 6; -fx-cursor: hand;");
        issueHeader.getChildren().addAll(issueTitle, iSpacer, newIssueBtn);

        VBox newIssueForm = new VBox(10);
        newIssueForm.setPadding(new Insets(16));
        newIssueForm.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");
        newIssueForm.setVisible(false);
        newIssueForm.setManaged(false);

        TextField issueTitleField = new TextField();
        issueTitleField.setPromptText("Title");
        issueTitleField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13;");

        TextArea issueDescField = new TextArea();
        issueDescField.setPromptText("Leave a comment");
        issueDescField.setPrefHeight(100);
        issueDescField.setStyle("-fx-background-color: #0d1117; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13;");

        Button submitIssueBtn = new Button("Submit new issue");
        submitIssueBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        newIssueForm.getChildren().addAll(issueTitleField, issueDescField, submitIssueBtn);

        VBox issueList = new VBox(0);
        issueList.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");
        refreshIssueList(issueList);

        newIssueBtn.setOnAction(e -> { newIssueForm.setVisible(true); newIssueForm.setManaged(true); });

        submitIssueBtn.setOnAction(e -> {
            String title = issueTitleField.getText().trim();
            if (!title.isEmpty()) {
                Issue issue = new Issue(title, issueDescField.getText().trim(), repo.getRepoName(), currentUser.getUsername());
                issueStorage.addIssue(currentUser.getUsername(), repo.getRepoName(), issue);
                issueTitleField.clear();
                issueDescField.clear();
                newIssueForm.setVisible(false);
                newIssueForm.setManaged(false);
                refreshIssueList(issueList);
            }
        });

        content.getChildren().addAll(issueHeader, newIssueForm, issueList);
        return content;
    }

    private void refreshIssueList(VBox issueList) {
        issueList.getChildren().clear();
        ArrayList<Issue> issues = issueStorage.getIssues(currentUser.getUsername(), repo.getRepoName());

        // Header
        HBox listHeader = new HBox(16);
        listHeader.setPadding(new Insets(10, 16, 10, 16));
        listHeader.setAlignment(Pos.CENTER_LEFT);
        listHeader.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8 8 0 0; -fx-background-radius: 8 8 0 0;");

        long openCount = issues.stream().filter(Issue::isOpen).count();
        long closedCount = issues.stream().filter(i -> !i.isOpen()).count();

        Text openText = new Text("🟢 " + openCount + " Open");
        openText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        openText.setFill(Color.web("#e6edf3"));

        Text closedText = new Text("✅ " + closedCount + " Closed");
        closedText.setFont(Font.font("Arial", 13));
        closedText.setFill(Color.web("#8b949e"));

        listHeader.getChildren().addAll(openText, closedText);
        issueList.getChildren().add(listHeader);

        if (issues.isEmpty()) {
            HBox emptyBox = new HBox();
            emptyBox.setPadding(new Insets(30));
            emptyBox.setAlignment(Pos.CENTER);
            Label noIssues = new Label("✅ No issues found!");
            noIssues.setTextFill(Color.web("#8b949e"));
            noIssues.setFont(Font.font("Arial", 14));
            emptyBox.getChildren().add(noIssues);
            issueList.getChildren().add(emptyBox);
            return;
        }

        for (Issue issue : issues) {
            HBox issueRow = new HBox(12);
            issueRow.setPadding(new Insets(12, 16, 12, 16));
            issueRow.setAlignment(Pos.CENTER_LEFT);
            issueRow.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;");
            issueRow.setOnMouseEntered(e -> issueRow.setStyle("-fx-background-color: #161b22; -fx-border-color: #21262d; -fx-border-width: 0 0 1 0;"));
            issueRow.setOnMouseExited(e -> issueRow.setStyle("-fx-border-color: #21262d; -fx-border-width: 0 0 1 0;"));

            Text statusIcon = new Text(issue.isOpen() ? "🟢" : "🔴");
            statusIcon.setFont(Font.font("Arial", 16));

            VBox issueInfo = new VBox(3);
            HBox.setHgrow(issueInfo, Priority.ALWAYS);

            Text issueTitle = new Text(issue.getTitle());
            issueTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            issueTitle.setFill(Color.web("#e6edf3"));

            Text issueMeta = new Text("Opened " + issue.getTimestamp() + " by " + issue.getOwnerUsername());
            issueMeta.setFont(Font.font("Arial", 11));
            issueMeta.setFill(Color.web("#8b949e"));

            issueInfo.getChildren().addAll(issueTitle, issueMeta);

            Button toggleBtn = new Button(issue.isOpen() ? "Close" : "Reopen");
            toggleBtn.setStyle(issue.isOpen() ?
                    "-fx-background-color: transparent; -fx-text-fill: #f85149; -fx-border-color: #f85149; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 11; -fx-padding: 3 10; -fx-cursor: hand;" :
                    "-fx-background-color: transparent; -fx-text-fill: #3fb950; -fx-border-color: #3fb950; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 11; -fx-padding: 3 10; -fx-cursor: hand;");

            toggleBtn.setOnAction(e -> {
                issue.setOpen(!issue.isOpen());
                ArrayList<Issue> updated = issueStorage.getIssues(currentUser.getUsername(), repo.getRepoName());
                issueStorage.updateIssues(currentUser.getUsername(), repo.getRepoName(), updated);
                refreshIssueList(issueList);
            });

            issueRow.getChildren().addAll(statusIcon, issueInfo, toggleBtn);
            issueList.getChildren().add(issueRow);
        }
    }

    // ── DUMMY TAB ──
    private VBox createDummyTab(String title, String heading, String desc) {
        VBox content = new VBox(16);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: #0d1117;");

        Text titleText = new Text(heading);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleText.setFill(Color.web("#e6edf3"));

        Text descText = new Text(desc);
        descText.setFont(Font.font("Arial", 14));
        descText.setFill(Color.web("#8b949e"));

        content.getChildren().addAll(titleText, descText);
        return content;
    }

    // ── SETTINGS TAB ──
    private VBox createSettingsTab() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(20, 24, 20, 24));
        content.setMaxWidth(600);
        content.setStyle("-fx-background-color: #0d1117;");

        Text title = new Text("Repository Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setFill(Color.WHITE);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #30363d;");

        TextField nameField = new TextField(repo.getRepoName());
        nameField.setStyle("-fx-background-color: #161b22; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13;");

        TextField descField = new TextField(repo.getDescription());
        descField.setStyle("-fx-background-color: #161b22; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13;");

        ToggleGroup visGroup = new ToggleGroup();
        RadioButton publicBtn = new RadioButton("🌐 Public");
        RadioButton privateBtn = new RadioButton("🔒 Private");
        publicBtn.setToggleGroup(visGroup);
        privateBtn.setToggleGroup(visGroup);
        publicBtn.setStyle("-fx-text-fill: #c9d1d9;");
        privateBtn.setStyle("-fx-text-fill: #c9d1d9;");
        if (repo.isPublic()) publicBtn.setSelected(true);
        else privateBtn.setSelected(true);

        HBox visBox = new HBox(20, publicBtn, privateBtn);

        Label saveMsg = new Label("");
        saveMsg.setTextFill(Color.web("#3fb950"));

        Button saveBtn = new Button("Save changes");
        saveBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            repo.setRepoName(nameField.getText().trim());
            repo.setDescription(descField.getText().trim());
            repo.setPublic(publicBtn.isSelected());
            repoStorage.updateRepo(repo);
            saveMsg.setText("✅ Settings saved!");
        });

        Separator dangerSep = new Separator();
        dangerSep.setStyle("-fx-background-color: #f85149;");

        Text dangerTitle = new Text("⚠️ Danger Zone");
        dangerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        dangerTitle.setFill(Color.web("#f85149"));

        Button deleteBtn = new Button("🗑 Delete this repository");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #f85149; -fx-border-color: #f85149; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 13; -fx-padding: 8 16; -fx-cursor: hand;");

        deleteBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Repository");
            alert.setHeaderText("Delete " + repo.getRepoName() + "?");
            alert.setContentText("This cannot be undone.");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    repoStorage.deleteRepo(repo.getRepoName(), currentUser.getUsername());
                    DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                    stage.setScene(db.getScene());
                    stage.setMaximized(true);
                }
            });
        });

        content.getChildren().addAll(title, sep, nameField, descField, visBox, saveMsg, saveBtn, dangerSep, dangerTitle, deleteBtn);
        return content;
    }

    // ── HELPERS ──
    private Button tabBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(active ? """
                -fx-background-color: transparent;
                -fx-text-fill: #f0f6fc;
                -fx-font-size: 13;
                -fx-padding: 12 16;
                -fx-cursor: hand;
                -fx-border-color: #f78166;
                -fx-border-width: 0 0 2 0;
                """ : """
                -fx-background-color: transparent;
                -fx-text-fill: #8b949e;
                -fx-font-size: 13;
                -fx-padding: 12 16;
                -fx-cursor: hand;
                """);
        return btn;
    }

    private void setActiveTab(Button[] tabs, int activeIndex) {
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setStyle(i == activeIndex ? """
                    -fx-background-color: transparent;
                    -fx-text-fill: #f0f6fc;
                    -fx-font-size: 13;
                    -fx-padding: 12 16;
                    -fx-cursor: hand;
                    -fx-border-color: #f78166;
                    -fx-border-width: 0 0 2 0;
                    """ : """
                    -fx-background-color: transparent;
                    -fx-text-fill: #8b949e;
                    -fx-font-size: 13;
                    -fx-padding: 12 16;
                    -fx-cursor: hand;
                    """);
        }
    }

    private void showOnly(Region show, Region... hide) {
        show.setVisible(true);
        show.setManaged(true);
        for (Region r : hide) {
            r.setVisible(false);
            r.setManaged(false);
        }
    }

    private HBox aboutItem(String icon, String text) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        Text i = new Text(icon);
        i.setFont(Font.font("Arial", 14));
        Text t = new Text(text);
        t.setFont(Font.font("Arial", 13));
        t.setFill(Color.web("#8b949e"));
        item.getChildren().addAll(i, t);
        return item;
    }

    private Button repoActionBtn(String text, String count) {
        Button btn = new Button(text + "  " + count);
        btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #30363d; -fx-text-fill: white; -fx-border-color: #8b949e; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 5 12; -fx-cursor: hand;"));
        return btn;
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