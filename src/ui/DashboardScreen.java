package ui;

import models.Issue;
import java.util.ArrayList;
import data.GitHubApiService;
import data.ActivityTracker;
import data.CommitStorage;
import data.IssueStorage;
import data.RepoStorage;
import data.UserStorage;
import dsa.RepoHeap;
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

public class DashboardScreen {

    private Stage stage;
    private User currentUser;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public DashboardScreen(Stage stage, User currentUser, UserStorage userStorage,
                           RepoStorage repoStorage, CommitStorage commitStorage,
                           IssueStorage issueStorage, ActivityTracker activityTracker) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.userStorage = userStorage;
        this.repoStorage = repoStorage;
        this.commitStorage = commitStorage;
        this.issueStorage = issueStorage;
        this.activityTracker = activityTracker;
    }

    public Scene getScene() {

        // ── SIDEBAR ──
        VBox sidebar = new VBox(8);
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.setPadding(new Insets(16));
        sidebar.setStyle("""
        -fx-background-color: #161b22;
        -fx-border-color: #30363d;
        -fx-border-width: 0 1 0 0;
        """);
        sidebar.setVisible(false);
        sidebar.setManaged(false);

// Sidebar header
        HBox sidebarHeader = new HBox();
        sidebarHeader.setAlignment(Pos.CENTER_LEFT);
        Text sidebarTitle = new Text("Menu");
        sidebarTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        sidebarTitle.setFill(Color.WHITE);
        Region sidebarSpacer = new Region();
        HBox.setHgrow(sidebarSpacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #8b949e;
        -fx-font-size: 16;
        -fx-cursor: hand;
        """);
        sidebarHeader.getChildren().addAll(sidebarTitle, sidebarSpacer, closeBtn);

        closeBtn.setOnAction(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
        });

// Divider
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #30363d;");

// Sidebar items
        VBox navItems = new VBox(4);
        navItems.getChildren().addAll(
                sidebarItem("📊", "Dashboard", true),
                sidebarItem("🐛", "Issues", false),
                sidebarItem("🔀", "Pull Requests", false),
                sidebarItem("📁", "Repositories", false),
                sidebarItem("📋", "Projects", false),
                sidebarItem("💬", "Discussions", false),
                sidebarItem("🤖", "Copilot", false),
                sidebarItem("🌍", "Explore", false),
                sidebarItem("🏪", "Marketplace", false),
                sidebarItem("🔌", "MCP Registry", false)
        );

         // Recent repos section
        Text recentTitle = new Text("RECENT REPOS");
        recentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        recentTitle.setFill(Color.web("#8b949e"));

        VBox recentReposList = new VBox(2);
        ArrayList<Repo> latestRepos = repoStorage.getUserRepos(currentUser.getUsername());
        int repoCount = Math.min(latestRepos.size(), 6);
        for (int i = 0; i < repoCount; i++) {
            Repo r = latestRepos.get(i);
            HBox repoItem = new HBox(8);
            repoItem.setPadding(new Insets(5, 8, 5, 8));
            repoItem.setAlignment(Pos.CENTER_LEFT);
            repoItem.setStyle("-fx-cursor: hand; -fx-background-radius: 6;");
            repoItem.setOnMouseEntered(e -> repoItem.setStyle("-fx-background-color: #21262d; -fx-background-radius: 6; -fx-cursor: hand;"));
            repoItem.setOnMouseExited(e -> repoItem.setStyle("-fx-cursor: hand; -fx-background-radius: 6;"));
            Text repoIcon = new Text("📁");
            repoIcon.setFont(Font.font("Arial", 12));
            Text repoName = new Text(r.getRepoName());
            repoName.setFont(Font.font("Arial", 12));
            repoName.setFill(Color.web("#58a6ff"));
            repoItem.getChildren().addAll(repoIcon, repoName);
            repoItem.setOnMouseClicked(e -> {
                RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, r, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                stage.setScene(detail.getScene());
                stage.setMaximized(true);
            });
            recentReposList.getChildren().add(repoItem);
        }

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #30363d;");

          // Bottom items
        VBox bottomItems = new VBox(4);
        bottomItems.getChildren().addAll(
                sidebarItem("👤", "Your Profile", false),
                sidebarItem("⚙️", "Settings", false),
                sidebarItem("🚪", "Sign out", false)
        );

        sidebar.getChildren().addAll(
                sidebarHeader, sep1, navItems,
                recentTitle, recentReposList,
                sep2, bottomItems
        );

        HBox dashItem = (HBox) navItems.getChildren().get(0);
        HBox issuesSideItem = (HBox) navItems.getChildren().get(1);
        HBox prsSideItem = (HBox) navItems.getChildren().get(2);
        HBox reposSideItem = (HBox) navItems.getChildren().get(3);
        HBox projectsSideItem = (HBox) navItems.getChildren().get(4);
        HBox discussionsSideItem = (HBox) navItems.getChildren().get(5);
        HBox copilotSideItem = (HBox) navItems.getChildren().get(6);
        HBox exploreSideItem = (HBox) navItems.getChildren().get(7);
        HBox marketplaceSideItem = (HBox) navItems.getChildren().get(8);
        HBox mcpSideItem = (HBox) navItems.getChildren().get(9);

        HBox profileSideItem = (HBox) bottomItems.getChildren().get(0);
        HBox settingsSideItem = (HBox) bottomItems.getChildren().get(1);
        HBox logoutSideItem = (HBox) bottomItems.getChildren().get(2);


        // ── TOP NAVBAR ──
        HBox navbar = new HBox(12);
        navbar.setPadding(new Insets(10, 24, 10, 24));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        Text logo = new Text("CodeNest");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        logo.setFill(Color.WHITE);

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

// ── Search bar (right side) ──
        TextField searchBar = new TextField();
        searchBar.setPromptText("🔍 Search or jump to...");
        searchBar.setPrefWidth(240);
        searchBar.setStyle("""
        -fx-background-color: #161b22;
        -fx-text-fill: #c9d1d9;
        -fx-prompt-text-fill: #6e7681;
        -fx-border-color: #30363d;
        -fx-border-radius: 6;
        -fx-background-radius: 6;
        -fx-padding: 6 10;
        -fx-font-size: 13;
        """);

// ── Nav buttons ──
        Button navReposBtn = navBtn("Repositories");
        Button navExploreBtn = navBtn("Explore");

// ── Issues icon with count ──
        int navIssueCount = 0;
        for (Repo r : repoStorage.getUserRepos(currentUser.getUsername())) {
            navIssueCount += issueStorage.getIssues(currentUser.getUsername(), r.getRepoName()).size();
        }
        Button issuesNavBtn = new Button("🐛 Issues  " + navIssueCount);
        Button prsNavBtn = new Button("🔀 PRs  0");

        prsNavBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        """);
        prsNavBtn.setOnMouseEntered(e -> prsNavBtn.setStyle("""
        -fx-background-color: #30363d;
        -fx-text-fill: white;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        -fx-background-radius: 6;
        """));
        prsNavBtn.setOnMouseExited(e -> prsNavBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        """));



        issuesNavBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        """);
        issuesNavBtn.setOnMouseEntered(e -> issuesNavBtn.setStyle("""
        -fx-background-color: #30363d;
        -fx-text-fill: white;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        -fx-background-radius: 6;
        """));
        issuesNavBtn.setOnMouseExited(e -> issuesNavBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        """));

        issuesNavBtn.setOnAction(e -> {
            // All Issues screen — abhi repos screen pe issues tab dikhao
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("All Issues");
            alert.setHeaderText("Your Issues");

            StringBuilder issuesList = new StringBuilder();
            for (Repo r : repoStorage.getUserRepos(currentUser.getUsername())) {
                var issues = issueStorage.getIssues(currentUser.getUsername(), r.getRepoName());
                for (var issue : issues) {
                    issuesList.append("🐛 ").append(issue.getTitle())
                            .append(" (").append(r.getRepoName()).append(")\n");
                }
            }

            alert.setContentText(issuesList.length() > 0 ? issuesList.toString() : "No issues found!");
            alert.showAndWait();
        });

        prsNavBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pull Requests");
            alert.setHeaderText("Coming Soon!");
            alert.setContentText("Pull Requests feature is under development.");
            alert.showAndWait();
        });

        Button hamburgerBtn = new Button("☰");
        hamburgerBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 18;
        -fx-cursor: hand;
        -fx-padding: 0 10 0 0;
        """);

        hamburgerBtn.setOnAction(e -> {
            sidebar.setVisible(true);
            sidebar.setManaged(true);
        });

// ── PRs icon (dummy) ──

        prsNavBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        """);
        prsNavBtn.setOnMouseEntered(e -> prsNavBtn.setStyle("""
        -fx-background-color: #30363d;
        -fx-text-fill: white;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        -fx-background-radius: 6;
        """));
        prsNavBtn.setOnMouseExited(e -> prsNavBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 13;
        -fx-cursor: hand;
        -fx-padding: 5 10;
        """));

// ── Notifications button ──
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 16;
        -fx-cursor: hand;
        -fx-padding: 4 8;
        """);

// Notifications popup
        ContextMenu notifMenu = new ContextMenu();
        notifMenu.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d;");

        MenuItem notif1 = new MenuItem("⭐ alihassan starred your repo");
        MenuItem notif2 = new MenuItem("👥 sarakhan followed you");
        MenuItem notif3 = new MenuItem("🍴 ahmedraza forked your repo");
        MenuItem notif4 = new MenuItem("🐛 New issue opened in DSA-Practice");
        MenuItem noNotifs = new MenuItem("✅ You're all caught up!");

        for (MenuItem item : new MenuItem[]{notif1, notif2, notif3, notif4, noNotifs}) {
            item.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 12;");
        }

        notifMenu.getItems().addAll(notif1, notif2, notif3, notif4, new SeparatorMenuItem(), noNotifs);

        notifBtn.setOnAction(e -> {
            notifMenu.show(notifBtn, javafx.geometry.Side.BOTTOM, 0, 0);
        });

// ── Plus/New dropdown ──
        Button plusBtn = new Button("➕");
        plusBtn.setStyle("""
        -fx-background-color: transparent;
        -fx-text-fill: #c9d1d9;
        -fx-font-size: 16;
        -fx-cursor: hand;
        -fx-padding: 4 8;
        """);

        ContextMenu plusMenu = new ContextMenu();
        plusMenu.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d;");

        MenuItem newRepoItem = new MenuItem("📁 New repository");
        MenuItem newIssueItem = new MenuItem("🐛 New issue");
        newRepoItem.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13;");
        newIssueItem.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13;");

        plusMenu.getItems().addAll(newRepoItem, newIssueItem);

        newRepoItem.setOnAction(e -> {
            RepoScreen repoScreen = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(repoScreen.getScene());
            stage.setMaximized(true);
        });

        plusBtn.setOnAction(e -> {
            plusMenu.show(plusBtn, javafx.geometry.Side.BOTTOM, 0, 0);
        });

// ── Profile dropdown ──
        Button profileBtn = new Button("👤");
        profileBtn.setStyle("""
        -fx-background-color: #2d6a4f;
        -fx-text-fill: white;
        -fx-font-size: 14;
        -fx-cursor: hand;
        -fx-padding: 6 10;
        -fx-background-radius: 20;
        """);

        ContextMenu profileMenu = new ContextMenu();
        profileMenu.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d;");

        MenuItem signedInAs = new MenuItem("Signed in as @" + currentUser.getUsername());
        signedInAs.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 12;");
        signedInAs.setDisable(true);

        MenuItem profileMenuItem = new MenuItem("👤 Your profile");
        MenuItem reposMenuItem = new MenuItem("📁 Your repositories");
        MenuItem settingsMenuItem = new MenuItem("⚙️ Settings");
        MenuItem logoutMenuItem = new MenuItem("🚪 Sign out");

        for (MenuItem item : new MenuItem[]{profileMenuItem, reposMenuItem, settingsMenuItem}) {
            item.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13;");
        }

        profileMenu.getItems().addAll(
                profileMenuItem, reposMenuItem, new SeparatorMenuItem(), settingsMenuItem, new SeparatorMenuItem(), logoutMenuItem
        );

        profileMenuItem.setOnAction(e -> {
            ProfileScreen ps = new ProfileScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(ps.getScene());
            stage.setMaximized(true);
        });

        reposMenuItem.setOnAction(e -> {
            RepoScreen rs = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(rs.getScene());
            stage.setMaximized(true);
        });

        logoutMenuItem.setOnAction(e -> {
            LoginScreen ls = new LoginScreen(stage, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(ls.getScene());
            stage.setMaximized(true);
        });

        profileBtn.setOnAction(e -> {
            profileMenu.show(profileBtn, javafx.geometry.Side.BOTTOM, 0, 0);
        });

        navbar.getChildren().addAll(hamburgerBtn, logo, navSpacer, searchBar, navReposBtn, navExploreBtn, issuesNavBtn, prsNavBtn, notifBtn, plusBtn, profileBtn);
//        HBox mainContent = new HBox(70);
        HBox mainContent = new HBox(24);
        mainContent.setPadding(new Insets(24, 48, 24, 48));
        HBox.setMargin(mainContent, new Insets(0, 24, 0, 0));
        mainContent.setStyle("-fx-background-color: #0d1117;");
        mainContent.setMaxWidth(Double.MAX_VALUE);

        // ── LEFT COLUMN ──
        VBox leftCol = new VBox(16);
        leftCol.setPrefWidth(180);
        leftCol.setMinWidth(0);
        leftCol.setMaxWidth(180);
        HBox.setMargin(leftCol, new Insets(0, 24, 0, 0));

        // Profile mini card
        VBox profileCard = new VBox(6);
        profileCard.setPadding(new Insets(10));
        VBox.setMargin(profileCard, new Insets(0, 0, 0, -40));
        profileCard.setStyle("""
        -fx-background-color: #161b22;
        -fx-border-color: #30363d;
        -fx-border-radius: 8;
        -fx-background-radius: 8;
        """);

//        VBox.setMargin(profileCard, new Insets(0, 160, 0, 0));

        Text userName = new Text(currentUser.getFullName());
        userName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        userName.setFill(Color.WHITE);

        Text userHandle = new Text("@" + currentUser.getUsername());
        userHandle.setFont(Font.font("Arial", 12));
        userHandle.setFill(Color.web("#8b949e"));

        Text userBio = new Text(currentUser.getBio().isEmpty() ? "No bio yet" : currentUser.getBio());
        userBio.setFont(Font.font("Arial", 11));
        userBio.setFill(Color.web("#8b949e"));
        userBio.setWrappingWidth(180);

        Text userUni = new Text("🎓 " + (currentUser.getUniversity().isEmpty() ? "University not set" : currentUser.getUniversity()));
        userUni.setFont(Font.font("Arial", 11));
        userUni.setFill(Color.web("#8b949e"));

        profileCard.getChildren().addAll(userName, userHandle, userBio, userUni);

        // Top Repos
        Text topReposTitle = new Text("Top Repositories");
        topReposTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        topReposTitle.setFill(Color.web("#c9d1d9"));

        VBox topReposList = new VBox(6);
        ArrayList<Repo> allUserRepos = repoStorage.getUserRepos(currentUser.getUsername());
        int count = 0;
        for (Repo r : allUserRepos) {
            if (count >= 6) break;
            topReposList.getChildren().add(leftRepoItem(r));
            count++;
        }
        if (topReposList.getChildren().isEmpty()) {
            Label noRepo = new Label("No repositories yet");
            noRepo.setTextFill(Color.web("#8b949e"));
            noRepo.setFont(Font.font("Arial", 12));
            topReposList.getChildren().add(noRepo);
        }

        Button newRepoBtn = new Button("+ New Repository");
        newRepoBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 12;
                -fx-padding: 6 12;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """);


        newRepoBtn.setOnAction(e -> {
            RepoScreen repoScreen = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(repoScreen.getScene());
            stage.setMaximized(true);
        });

        leftCol.getChildren().addAll(profileCard, topReposTitle, topReposList, newRepoBtn);

        // ── CENTER COLUMN ──
        VBox centerCol = new VBox(20);
        HBox.setHgrow(centerCol, Priority.ALWAYS);
        centerCol.setMaxWidth(Double.MAX_VALUE);


        // Welcome bar
        HBox welcomeBar = new HBox(16);
//        HBox.setMargin(welcomeBar, new Insets(0, 0, 0, 0));
        welcomeBar.setAlignment(Pos.CENTER_LEFT);
        Text welcomeText = new Text("Welcome back, " + currentUser.getFullName().split(" ")[0] + "!");
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        welcomeText.setFill(Color.WHITE);
        welcomeBar.getChildren().add(welcomeText);

        // Stats row
        HBox statsRow = new HBox(12);
        int totalRepos = repoStorage.getUserRepos(currentUser.getUsername()).size();
        int totalCommits = 0;
        for (Repo r : repoStorage.getUserRepos(currentUser.getUsername())) {
            totalCommits += r.getCommits();
        }
        int totalIssues = 0;
        for (Repo r : repoStorage.getUserRepos(currentUser.getUsername())) {
            totalIssues += issueStorage.getIssues(currentUser.getUsername(), r.getRepoName()).size();
        }

        statsRow.getChildren().addAll(
                statChip("📁 " + totalRepos + " Repos"),
                statChip("📝 " + totalCommits + " Commits"),
                statChip("🐛 " + totalIssues + " Issues")
        );

        // Activity feed title
        Text feedTitle = new Text("📰 Home Feed");
        feedTitle.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        feedTitle.setFill(Color.web("#c9d1d9"));

        // Activity feed
        VBox feedBox = createFeedBox();
        feedBox.setMaxWidth(Double.MAX_VALUE);
        centerCol.getChildren().addAll(welcomeBar, statsRow, feedTitle, feedBox);

        // ── RIGHT COLUMN ──
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(180);
        rightCol.setMinWidth(0);
        rightCol.setMaxWidth(180);

        // Changelog
        Text changelogTitle = new Text("📋 Latest Updates");
        changelogTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        changelogTitle.setFill(Color.web("#c9d1d9"));

        VBox changelogBox = new VBox(8);
        changelogBox.setPadding(new Insets(8));
        changelogBox.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        String[][] changelog = {
                {"🚀", "Copilot Engine", "Real-time code suggestions"},
                {"🔍", "Smart Search", "Trie-powered autocomplete"},
                {"📊", "Activity Graph", "7-day commit tracker"},
                {"🍴", "Fork & Star", "Explore public repos"},
                {"🐛", "Issues Tracker", "Per-repo bug tracking"},
                {"↩️", "Undo Commits", "Stack-based history"},
        };

        for (String[] item : changelog) {
            HBox entry = new HBox(8);
            entry.setAlignment(Pos.CENTER_LEFT);
            Text icon = new Text(item[0]);
            icon.setFont(Font.font("Arial", 16));
            VBox info = new VBox(2);
            Text entryTitle = new Text(item[1]);
            entryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            entryTitle.setFill(Color.web("#58a6ff"));
            Text entryDesc = new Text(item[2]);
            entryDesc.setFont(Font.font("Arial", 11));
            entryDesc.setFill(Color.web("#8b949e"));
            info.getChildren().addAll(entryTitle, entryDesc);
            entry.getChildren().addAll(icon, info);
            changelogBox.getChildren().add(entry);
        }

        // Trending section
        Text trendingTitle = new Text("🔥 Trending");
        trendingTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        trendingTitle.setFill(Color.web("#c9d1d9"));

        VBox trendingBox = new VBox(8);
        trendingBox.setPadding(new Insets(8));
        trendingBox.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Label trendingLoading = new Label("⏳ Loading...");
        trendingLoading.setTextFill(Color.web("#8b949e"));
        trendingLoading.setFont(Font.font("Arial", 12));
        trendingBox.getChildren().add(trendingLoading);

        GitHubApiService gitHubApi = new GitHubApiService();
        new Thread(() -> {
            ArrayList<com.google.gson.JsonObject> ghRepos = gitHubApi.getTrendingRepos();
            javafx.application.Platform.runLater(() -> {
                trendingBox.getChildren().remove(trendingLoading);
                if (!ghRepos.isEmpty()) {
                    for (com.google.gson.JsonObject repo : ghRepos.subList(0, Math.min(5, ghRepos.size()))) {
                        String name = repo.get("full_name").getAsString();
                        String lang = repo.has("language") && !repo.get("language").isJsonNull()
                                ? repo.get("language").getAsString() : "Unknown";
                        int stars = repo.get("stargazers_count").getAsInt();

                        HBox tItem = new HBox(8);
                        tItem.setAlignment(Pos.CENTER_LEFT);
                        VBox tInfo = new VBox(2);
                        Text tName = new Text(name.length() > 25 ? name.substring(0, 25) + "..." : name);
                        tName.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        tName.setFill(Color.web("#58a6ff"));
                        HBox tMeta = new HBox(8);
                        Text tStars = new Text("⭐ " + (stars >= 1000 ? String.format("%.1fk", stars/1000.0) : stars));
                        tStars.setFont(Font.font("Arial", 11));
                        tStars.setFill(Color.web("#8b949e"));
                        Text tLang = new Text(lang);
                        tLang.setFont(Font.font("Arial", 11));
                        tLang.setFill(Color.web("#8b949e"));
                        tMeta.getChildren().addAll(tStars, tLang);
                        tInfo.getChildren().addAll(tName, tMeta);
                        tItem.getChildren().add(tInfo);
                        trendingBox.getChildren().add(tItem);
                    }
                }
            });
        }).start();

        rightCol.getChildren().addAll(changelogTitle, changelogBox, trendingTitle, trendingBox);

//        ScrollPane centerScroll = new ScrollPane(centerCol);
//        centerScroll.setFitToWidth(true);
//        centerScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        centerScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        centerScroll.setStyle("""
//        -fx-background-color: #0d1117;
//        -fx-background: #0d1117;
//        -fx-border-color: transparent;
//        """);
//        centerScroll.setMaxWidth(700);
//        HBox.setHgrow(centerScroll, Priority.ALWAYS);
//        mainContent.getChildren().addAll(leftCol, centerScroll, rightCol);
        HBox.setHgrow(centerCol, Priority.ALWAYS);
        centerCol.setMaxWidth(700);
        mainContent.getChildren().addAll(leftCol, centerCol, rightCol);

        // ── ACTIONS ──

        // Hamburger toggle
//        hamburgerBtn.setOnAction(e -> {
//            sidebar.setVisible(true);
//            sidebar.setManaged(true);
//        });

//         ── ROOT ──
        HBox contentRow = new HBox(mainContent);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        VBox mainRoot = new VBox(navbar, contentRow);
        VBox.setVgrow(contentRow, Priority.ALWAYS);
        mainRoot.setStyle("-fx-background-color: #0d1117;");

        StackPane rootStack = new StackPane(mainRoot, sidebar);
        StackPane.setAlignment(sidebar, Pos.TOP_LEFT);
        sidebar.setTranslateY(50);

        stage.setMaximized(true);
        ScrollPane scrollPane = new ScrollPane(rootStack);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        stage.setMaximized(true);
        reposSideItem.setOnMouseClicked(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            RepoScreen rs = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(rs.getScene());
            stage.setMaximized(true);
        });

        exploreSideItem.setOnMouseClicked(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            ExploreScreen es = new ExploreScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(es.getScene());
            stage.setMaximized(true);
        });

        profileSideItem.setOnMouseClicked(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            ProfileScreen ps = new ProfileScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(ps.getScene());
            stage.setMaximized(true);
        });

        logoutSideItem.setOnMouseClicked(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            LoginScreen ls = new LoginScreen(stage, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(ls.getScene());
            stage.setMaximized(true);
        });

        dashItem.setOnMouseClicked(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
        });

        searchBar.setOnAction(e -> {
            SearchScreen searchScreen = new SearchScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(searchScreen.getScene());
            stage.setMaximized(true);
        });

        navReposBtn.setOnAction(e -> {
            RepoScreen repoScreen = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(repoScreen.getScene());
            stage.setMaximized(true);
        });

        navExploreBtn.setOnAction(e -> {
            ExploreScreen exploreScreen = new ExploreScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(exploreScreen.getScene());
            stage.setMaximized(true);
        });

        dummyAction(prsSideItem, "Pull Requests", sidebar);
        dummyAction(projectsSideItem, "Projects", sidebar);
        dummyAction(discussionsSideItem, "Discussions", sidebar);
        dummyAction(copilotSideItem, "Copilot", sidebar);
        dummyAction(marketplaceSideItem, "Marketplace", sidebar);
        dummyAction(mcpSideItem, "MCP Registry", sidebar);
        dummyAction(settingsSideItem, "Settings", sidebar);
        dummyAction(issuesSideItem, "Issues", sidebar);

//        return new Scene(scrollPane);
//        Scene scene = new Scene(scrollPane);
//        stage.setScene(scene);
//        stage.setMaximized(true);
//        return scene;
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(javafx.stage.Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(javafx.stage.Screen.getPrimary().getBounds().getHeight());
        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
    }

    // ── Helper Methods ──

    private HBox sidebarItem(String icon, String label, boolean active) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(8, 12, 8, 12));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle(active ?
                "-fx-background-color: #21262d; -fx-background-radius: 6; -fx-cursor: hand;" :
                "-fx-cursor: hand; -fx-background-radius: 6;");
        item.setOnMouseEntered(e -> item.setStyle(
                "-fx-background-color: #21262d; -fx-background-radius: 6; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle(active ?
                "-fx-background-color: #21262d; -fx-background-radius: 6; -fx-cursor: hand;" :
                "-fx-cursor: hand; -fx-background-radius: 6;"));

        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 15));
        iconText.setMouseTransparent(true);

        Text labelText = new Text(label);
        labelText.setFont(Font.font("Arial", 13));
        labelText.setFill(Color.web("#c9d1d9"));
        labelText.setMouseTransparent(true);

        item.getChildren().addAll(iconText, labelText);
        return item;
    }

    private Button navBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #c9d1d9;
                -fx-font-size: 13;
                -fx-cursor: hand;
                -fx-padding: 5 10;
                """);
        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: #30363d;
                -fx-text-fill: white;
                -fx-font-size: 13;
                -fx-cursor: hand;
                -fx-padding: 5 10;
                -fx-background-radius: 6;
                """));
        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #c9d1d9;
                -fx-font-size: 13;
                -fx-cursor: hand;
                -fx-padding: 5 10;
                """));
        return btn;
    }

    private HBox leftRepoItem(Repo repo) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(6, 8, 6, 8));
        item.setStyle("-fx-cursor: hand; -fx-background-radius: 6;");
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #21262d; -fx-background-radius: 6; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-cursor: hand; -fx-background-radius: 6;"));

        Text dot = new Text("📁");
        dot.setFont(Font.font("Arial", 13));

        VBox info = new VBox(2);
        Text name = new Text(repo.getRepoName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        name.setFill(Color.web("#58a6ff"));

        Text lang = new Text(repo.getLanguage() + " • " + repo.getCommits() + " commits");
        lang.setFont(Font.font("Arial", 11));
        lang.setFill(Color.web("#8b949e"));

        info.getChildren().addAll(name, lang);
        item.getChildren().addAll(dot, info);

        item.setOnMouseClicked(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        return item;
    }

    private Label statChip(String text) {
        Label chip = new Label(text);
        chip.setFont(Font.font("Arial", 12));
        chip.setTextFill(Color.web("#8b949e"));
        chip.setPadding(new Insets(4, 10, 4, 10));
        chip.setStyle("""
                -fx-background-color: #21262d;
                -fx-border-color: #30363d;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                """);
        return chip;
    }

    private HBox feedCard(Repo repo) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """);
        card.setOnMouseEntered(e -> card.setStyle("""
                -fx-background-color: #1c2128;
                -fx-border-color: #58a6ff;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """));
        card.setOnMouseExited(e -> card.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text repoName = new Text("📁 " + repo.getRepoName());
        repoName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        repoName.setFill(Color.web("#58a6ff"));

        Text desc = new Text(repo.getDescription().isEmpty() ? "No description" : repo.getDescription());
        desc.setFont(Font.font("Arial", 12));
        desc.setFill(Color.web("#8b949e"));

        HBox meta = new HBox(16);
        Text lang = new Text("🔵 " + repo.getLanguage());
        lang.setFont(Font.font("Arial", 11));
        lang.setFill(Color.web("#8b949e"));
        Text commits = new Text("📝 " + repo.getCommits() + " commits");
        commits.setFont(Font.font("Arial", 11));
        commits.setFill(Color.web("#8b949e"));
        Text visibility = new Text(repo.isPublic() ? "🌐 Public" : "🔒 Private");
        visibility.setFont(Font.font("Arial", 11));
        visibility.setFill(Color.web("#8b949e"));
        meta.getChildren().addAll(lang, commits, visibility);

        info.getChildren().addAll(repoName, desc, meta);
        card.getChildren().add(info);

        card.setOnMouseClicked(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        return card;
    }

    private VBox feedEmptyCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-border-style: dashed;
                """);
        Text msg = new Text("No repositories yet");
        msg.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        msg.setFill(Color.web("#8b949e"));
        Text sub = new Text("Create your first repository to get started!");
        sub.setFont(Font.font("Arial", 12));
        sub.setFill(Color.web("#6e7681"));
        Button createBtn = new Button("+ New Repository");
        createBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 13;
                -fx-padding: 8 16;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """);
        createBtn.setOnAction(e -> {
            RepoScreen repoScreen = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(repoScreen.getScene());
            stage.setMaximized(true);
        });
        card.getChildren().addAll(msg, sub, createBtn);
        return card;
    }

    private VBox createFeedBox() {
        VBox feedBox = new VBox(14);
        feedBox.setPadding(new Insets(10, 10, 100, 10));
         VBox.setMargin(feedBox, new Insets(0, 0, 0, -0));



        // ── Real User Activity ──
        ArrayList<Repo> userRepos = repoStorage.getUserRepos(currentUser.getUsername());

        // Real commits
        for (Repo r : userRepos) {
            if (r.getCommits() > 0) {
                feedBox.getChildren().add(createFeedCard(new String[]{
                        currentUser.getUsername(),
                        currentUser.getFullName(),
                        "pushed " + r.getCommits() + " commit(s) to",
                        r.getRepoName(),
                        r.getLanguage(),
                        String.valueOf(r.getStars()),
                        r.getDescription().isEmpty() ? "No description" : r.getDescription()
                }));
            }
        }

        // Real issues
        for (Repo r : userRepos) {
            ArrayList<models.Issue> issues = issueStorage.getIssues(currentUser.getUsername(), r.getRepoName());
            for (models.Issue issue : issues) {
                if (issue.isOpen()) {
                    feedBox.getChildren().add(issueEventCard(
                            r.getRepoName(), issue.getTitle(), issue.getTimestamp()
                    ));
                }
            }
        }

        // ── Dummy Social Events ──
        if (feedBox.getChildren().isEmpty() || feedBox.getChildren().size() < 3) {
            String[][] dummyEvents = {
                    {"alihassan", "Ali Hassan", "created a new repository", "DSA-Practice", "Java", "24", "All DSA problems solved in Java"},
                    {"sarakhan", "Sara Khan", "starred a repository", "ML-Algorithms", "Python", "35", "Machine learning algorithms in Python"},
                    {"ahmedraza", "Ahmed Raza", "created a new repository", "Portfolio-Website", "JavaScript", "42", "My personal portfolio website"},
                    {"usmantariq", "Usman Tariq", "pushed commits to", "Competitive-Programming", "C++", "50", "CP solutions in C++"},
                    {"fatimamalik", "Fatima Malik", "created a new repository", "Android-Calculator", "Kotlin", "15", "Calculator app in Kotlin"},
            };
            for (String[] event : dummyEvents) {
                feedBox.getChildren().add(createFeedCard(event));
            }
        }

        // ── Trending Section ──
        Text trendingHeader = new Text("🔥 Trending on GitHub");
        trendingHeader.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        trendingHeader.setFill(Color.web("#c9d1d9"));
        feedBox.getChildren().add(trendingHeader);

        Label loadingLabel = new Label("⏳ Loading trending repos...");
        loadingLabel.setTextFill(Color.web("#8b949e"));
        loadingLabel.setFont(Font.font("Arial", 13));
        feedBox.getChildren().add(loadingLabel);

        // Load real GitHub trending in background
        GitHubApiService gitHubApi = new GitHubApiService();
        new Thread(() -> {
            ArrayList<com.google.gson.JsonObject> ghRepos = gitHubApi.getTrendingRepos();
            javafx.application.Platform.runLater(() -> {
                feedBox.getChildren().remove(loadingLabel);
                if (!ghRepos.isEmpty()) {
                    for (com.google.gson.JsonObject repo : ghRepos.subList(0, Math.min(4, ghRepos.size()))) {
                        String repoName = repo.get("full_name").getAsString();
                        String desc = repo.has("description") && !repo.get("description").isJsonNull()
                                ? repo.get("description").getAsString() : "No description";
                        String lang = repo.has("language") && !repo.get("language").isJsonNull()
                                ? repo.get("language").getAsString() : "Unknown";
                        int stars = repo.get("stargazers_count").getAsInt();
                        feedBox.getChildren().add(createTrendingCard(new String[]{
                                repoName, "", lang, String.valueOf(stars), desc
                        }));
                    }
                }
            });
        }).start();

        return feedBox;
    }

    private HBox issueEventCard(String repoName, String issueTitle, String timestamp) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #1c2128;
            -fx-border-color: #f85149;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """));
        card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """));

        // Icon
        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(36, 36);
        iconCircle.setMaxSize(36, 36);
        iconCircle.setStyle("-fx-background-color: #21262d; -fx-background-radius: 18;");
        Text iconText = new Text("🐛");
        iconText.setFont(Font.font("Arial", 16));
        iconCircle.getChildren().add(iconText);

        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Text mainText = new Text("Issue opened in " + repoName + ": " + issueTitle);
        mainText.setFill(Color.web("#c9d1d9"));
        mainText.setFont(Font.font("Arial", 13));
        mainText.setWrappingWidth(400);

        Text timeText = new Text(timestamp);
        timeText.setFill(Color.web("#6e7681"));
        timeText.setFont(Font.font("Arial", 11));

        textBox.getChildren().addAll(mainText, timeText);
        card.getChildren().addAll(iconCircle, textBox);
        return card;
    }

    private VBox createFeedCard(String[] event) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        VBox.setMargin(card, new Insets(0, 30, 0, 0));
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #1c2128;
            -fx-border-color: #58a6ff;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """));
        card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """));

        // ── User Row ──
        HBox userRow = new HBox(10);
        userRow.setAlignment(Pos.CENTER_LEFT);

        // Avatar circle
        StackPane avatar = new StackPane();
        avatar.setMinSize(38, 38);
        avatar.setMaxSize(38, 38);
        avatar.setStyle("-fx-background-color: #2d6a4f; -fx-background-radius: 19;");
        Text avatarText = new Text(String.valueOf(event[1].charAt(0)));
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        avatarText.setFill(Color.WHITE);
        avatar.getChildren().add(avatarText);

        VBox userInfo = new VBox(2);
        HBox.setHgrow(userInfo, Priority.ALWAYS);

        Text username = new Text(event[1] + "  @" + event[0]);
        username.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        username.setFill(Color.web("#c9d1d9"));

        Text action = new Text(event[2]);
        action.setFont(Font.font("Arial", 12));
        action.setFill(Color.web("#8b949e"));

        userInfo.getChildren().addAll(username, action);

        // Follow button
        Button followBtn = new Button("Follow");
        followBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #c9d1d9;
            -fx-border-color: #30363d;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-size: 12;
            -fx-padding: 4 12;
            -fx-cursor: hand;
            """);
        followBtn.setOnAction(e -> {
            followBtn.setText("Following ✓");
            followBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 4 12;
                """);
            followBtn.setDisable(true);
        });

        userRow.getChildren().addAll(avatar, userInfo, followBtn);

        // ── Repo Card ──
        HBox repoBox = new HBox(12);
        repoBox.setPadding(new Insets(10, 12, 10, 12));
        repoBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(repoBox, Priority.ALWAYS);
        repoBox.setAlignment(Pos.CENTER_LEFT);
        repoBox.setStyle("""
            -fx-background-color: #0d1117;
            -fx-border-color: #30363d;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """);

        VBox repoInfo = new VBox(4);
        HBox.setHgrow(repoInfo, Priority.ALWAYS);
        Text repoName = new Text("📁 " + event[3]);
        repoName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        repoName.setFill(Color.web("#58a6ff"));

        Text repoDesc = new Text(event[6]);
        repoDesc.setFont(Font.font("Arial", 12));
        repoDesc.setFill(Color.web("#8b949e"));

        HBox repoMeta = new HBox(16);
        Text repoLang = new Text("🔵 " + event[4]);
        repoLang.setFont(Font.font("Arial", 11));
        repoLang.setFill(Color.web("#8b949e"));

        Text repoStars = new Text("⭐ " + event[5]);
        repoStars.setFont(Font.font("Arial", 11));
        repoStars.setFill(Color.web("#8b949e"));

        repoMeta.getChildren().addAll(repoLang, repoStars);
        repoInfo.getChildren().addAll(repoName, repoDesc, repoMeta);
        repoBox.getChildren().add(repoInfo);

        card.getChildren().addAll(userRow, repoBox);
        return card;
    }

    private HBox createTrendingCard(String[] t) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10, 12, 10, 12));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #1c2128;
            -fx-border-color: #58a6ff;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """));
        card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """));

        VBox info = new VBox(5);
        VBox.setMargin(info,new Insets(0,150,0,0));
        HBox.setHgrow(info, Priority.ALWAYS);

        Text repoName = new Text("📁 " + t[1] + " / " + t[0]);
        repoName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        repoName.setFill(Color.web("#58a6ff"));

        Text desc = new Text(t[4]);
        desc.setFont(Font.font("Arial", 12));
        desc.setFill(Color.web("#8b949e"));

        HBox meta = new HBox(16);
        Text lang = new Text("🔵 " + t[2]);
        lang.setFont(Font.font("Arial", 11));
        lang.setFill(Color.web("#8b949e"));

        Text stars = new Text("⭐ " + t[3] + " stars");
        stars.setFont(Font.font("Arial", 11));
        stars.setFill(Color.web("#8b949e"));

        meta.getChildren().addAll(lang, stars);
        info.getChildren().addAll(repoName, desc, meta);

        // Star button
        Button starBtn = new Button("⭐ Star");
        starBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #c9d1d9;
            -fx-border-color: #30363d;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-size: 12;
            -fx-padding: 6 14;
            -fx-cursor: hand;
            """);
        starBtn.setOnAction(e -> {
            starBtn.setText("⭐ Starred!");
            starBtn.setStyle("""
                -fx-background-color: #e3b341;
                -fx-text-fill: #0d1117;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 6 14;
                """);
            starBtn.setDisable(true);
        });

        card.getChildren().addAll(info, starBtn);
        return card;
    }

    private HBox feedEvent(String icon, String iconColor, String text, String highlight, String time) {
        HBox card = new HBox(12);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """);
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #1c2128;
            -fx-border-color: #58a6ff;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """));
        card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """));

        // Icon circle
        StackPane iconCircle = new StackPane();
        iconCircle.setMinSize(36, 36);
        iconCircle.setMaxSize(36, 36);
        iconCircle.setStyle("-fx-background-color: #21262d; -fx-background-radius: 18;");
        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 16));
        iconCircle.getChildren().add(iconText);

        // Text
        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        TextFlow tf = new TextFlow();
        Text mainText = new Text(text);
        mainText.setFill(Color.web("#c9d1d9"));
        mainText.setFont(Font.font("Arial", 13));
        mainText.setWrappingWidth(380);

        if (!highlight.isEmpty()) {
            Text highlightText = new Text(highlight);
            highlightText.setFill(Color.web("#58a6ff"));
            highlightText.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            tf.getChildren().addAll(mainText, highlightText);
        } else {
            tf.getChildren().add(mainText);
        }

        Text timeText = new Text(time);
        timeText.setFill(Color.web("#6e7681"));
        timeText.setFont(Font.font("Arial", 11));

        textBox.getChildren().addAll(tf, timeText);
        card.getChildren().addAll(iconCircle, textBox);
        return card;
    }

    private void dummyAction(HBox item, String name, VBox sidebar) {
        item.setOnMouseClicked(e -> {
            sidebar.setVisible(false);
            sidebar.setManaged(false);
            stage.setScene(createDummyScreen(name));
        });
    }

    private Scene createDummyScreen(String name) {
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
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, new RepoStorage(), commitStorage, issueStorage, activityTracker);
            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-font-size: 13; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, new RepoStorage(), commitStorage, issueStorage, activityTracker);
            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        navbar.getChildren().addAll(logo, navSpacer, backBtn);

        // ── CONTENT ──
        VBox content = new VBox(20);
        content.setPadding(new Insets(40, 60, 40, 60));
        content.setStyle("-fx-background-color: #0d1117;");

        // Icon + Title
        String icon = getScreenIcon(name);
        Text titleIcon = new Text(icon);
        titleIcon.setFont(Font.font("Arial", 48));

        Text title = new Text(name);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setFill(Color.web("#e6edf3"));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #30363d;");

        // Dummy content based on screen
        VBox dummyContent = getDummyContent(name);

        content.getChildren().addAll(titleIcon, title, sep, dummyContent);

        VBox root = new VBox(navbar, content);
        root.setStyle("-fx-background-color: #0d1117;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");


        stage.setMaximized(true);
        return new Scene(scrollPane, 1100, 650);
    }

    private String getScreenIcon(String name) {
        return switch (name) {
            case "Pull Requests" -> "🔀";
            case "Projects" -> "📋";
            case "Discussions" -> "💬";
            case "Marketplace" -> "🏪";
            case "MCP Registry" -> "🔌";
            case "Issues" -> "🐛";
            case "Copilot" -> "🤖";
            case "Settings" -> "⚙️";
            default -> "📄";
        };
    }

    private VBox getDummyContent(String name) {
        VBox box = new VBox(12);

        switch (name) {
            case "Pull Requests" -> {
                box.getChildren().add(sectionTitle("Open Pull Requests (0)"));
                box.getChildren().add(dummyCard("🔀", "No open pull requests", "When you open pull requests, they will appear here."));
                box.getChildren().add(sectionTitle("Recently Closed"));
                box.getChildren().add(dummyCard("✅", "feature/add-search-trie", "Merged 2 days ago by alihassan"));
                box.getChildren().add(dummyCard("✅", "fix/commit-stack-bug", "Merged 5 days ago by sarakhan"));
                box.getChildren().add(dummyCard("❌", "feature/graph-visualization", "Closed without merging · 1 week ago"));
            }
            case "Projects" -> {
                box.getChildren().add(sectionTitle("Your Projects"));
                box.getChildren().add(dummyCard("📋", "CodeNest Development", "Track progress of CodeNest features · 12 items · Updated today"));
                box.getChildren().add(dummyCard("📋", "DSA Assignment Tracker", "Keep track of DSA assignments · 8 items · Updated 2 days ago"));
                box.getChildren().add(dummyCard("📋", "Bug Fixes Sprint", "Current sprint bug fixes · 5 items · Updated 3 days ago"));
                Button newProjectBtn = new Button("+ New Project");
                newProjectBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
                box.getChildren().add(newProjectBtn);
            }
            case "Discussions" -> {
                box.getChildren().add(sectionTitle("Recent Discussions"));
                box.getChildren().add(dummyCard("💬", "Best practices for DSA in Java?", "Started by alihassan · 15 replies · 2 hours ago"));
                box.getChildren().add(dummyCard("💬", "How to implement Trie efficiently?", "Started by sarakhan · 8 replies · 1 day ago"));
                box.getChildren().add(dummyCard("💬", "Graph vs Adjacency Matrix — which is better?", "Started by usmantariq · 23 replies · 3 days ago"));
                box.getChildren().add(dummyCard("💬", "Copilot suggestions not working", "Started by fatimamalik · 5 replies · 5 days ago"));
                box.getChildren().add(dummyCard("📌", "Welcome to CodeNest Discussions!", "Pinned · Read our community guidelines"));
            }
            case "Marketplace" -> {
                box.getChildren().add(sectionTitle("Featured Extensions"));
                box.getChildren().add(dummyCard("🔧", "Code Formatter Pro", "Automatically format your Java, Python, and JavaScript code · Free"));
                box.getChildren().add(dummyCard("🧪", "Unit Test Generator", "Auto-generate unit tests for your Java methods · $4.99/month"));
                box.getChildren().add(dummyCard("📊", "Code Analytics", "Track code quality metrics and complexity · Free tier available"));
                box.getChildren().add(dummyCard("🔒", "Security Scanner", "Scan your code for vulnerabilities · Free"));
                box.getChildren().add(dummyCard("🤖", "AI Code Reviewer", "Get AI-powered code reviews · Powered by Gemini · Free"));
                box.getChildren().add(sectionTitle("Categories"));
                HBox categories = new HBox(10);
                String[] cats = {"Code Quality", "Testing", "Security", "CI/CD", "Monitoring", "Deployment"};
                for (String cat : cats) {
                    Button catBtn = new Button(cat);
                    catBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 5 12; -fx-font-size: 12; -fx-cursor: hand;");
                    categories.getChildren().add(catBtn);
                }
                box.getChildren().add(categories);
            }
            case "MCP Registry" -> {
                box.getChildren().add(sectionTitle("Available MCP Servers"));
                box.getChildren().add(dummyCard("🔌", "GitHub MCP", "Connect to GitHub repositories and issues · Official · Free"));
                box.getChildren().add(dummyCard("🔌", "Slack MCP", "Send notifications to Slack channels · Official · Free"));
                box.getChildren().add(dummyCard("🔌", "Jira MCP", "Sync issues with Jira project management · $2.99/month"));
                box.getChildren().add(dummyCard("🔌", "Firebase MCP", "Connect to Firebase databases · Free tier available"));
                box.getChildren().add(dummyCard("🔌", "AWS MCP", "Deploy to AWS directly from CodeNest · Pay per use"));
                box.getChildren().add(dummyCard("🔌", "Docker MCP", "Build and deploy Docker containers · Free"));
                box.getChildren().add(sectionTitle("Connected Servers (0)"));
                Label noConnected = new Label("No MCP servers connected yet. Browse and connect above.");
                noConnected.setTextFill(Color.web("#8b949e"));
                noConnected.setFont(Font.font("Arial", 13));
                box.getChildren().add(noConnected);
            }
            case "Copilot" -> {
                box.getChildren().add(sectionTitle("CodeNest Copilot"));
                box.getChildren().add(dummyCard("🤖", "AI-Powered Code Suggestions", "Get real-time suggestions as you type · Powered by Gemini AI"));
                box.getChildren().add(dummyCard("🔍", "Code Review Mode", "Full code analysis with error detection and optimization tips"));
                box.getChildren().add(dummyCard("💡", "Smart Autocomplete", "Trie-powered search for your code snippets and files"));
                box.getChildren().add(sectionTitle("Usage This Month"));
                box.getChildren().add(dummyCard("📊", "Suggestions: 47", "Review requests: 12 · Tokens used: 15,420"));
            }
            case "Issues" -> {
                box.getChildren().add(sectionTitle("All Issues"));
                ArrayList<models.Repo> repos = repoStorage.getUserRepos(currentUser.getUsername());
                boolean hasIssues = false;
                for (models.Repo r : repos) {
                    ArrayList<models.Issue> issues = issueStorage.getIssues(currentUser.getUsername(), r.getRepoName());
                    for (models.Issue issue : issues) {
                        hasIssues = true;
                        box.getChildren().add(dummyCard(
                                issue.isOpen() ? "🟢" : "🔴",
                                issue.getTitle(),
                                r.getRepoName() + " · " + issue.getTimestamp()
                        ));
                    }
                }
                if (!hasIssues) {
                    Label noIssues = new Label("✅ No issues found across all repositories.");
                    noIssues.setTextFill(Color.web("#8b949e"));
                    noIssues.setFont(Font.font("Arial", 14));
                    box.getChildren().add(noIssues);
                }
            }
            default -> {
                box.getChildren().add(dummyCard("🚀", "Coming Soon", "This feature is under development."));
            }
        }
        return box;
    }

    private Text sectionTitle(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        t.setFill(Color.web("#e6edf3"));
        return t;
    }

    private HBox dummyCard(String icon, String title, String desc) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """);
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #1c2128;
            -fx-border-color: #58a6ff;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """));
        card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: #161b22;
            -fx-border-color: #30363d;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """));

        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 22));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text titleText = new Text(title);
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titleText.setFill(Color.web("#e6edf3"));

        Text descText = new Text(desc);
        descText.setFont(Font.font("Arial", 12));
        descText.setFill(Color.web("#8b949e"));
        descText.setWrappingWidth(600);

        info.getChildren().addAll(titleText, descText);
        card.getChildren().addAll(iconText, info);
        return card;
    }
}