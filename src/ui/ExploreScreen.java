package ui;

import data.ActivityTracker;
import data.CommitStorage;
import data.GitHubApiService;
import data.IssueStorage;
import data.RepoStorage;
import data.UserStorage;
import dsa.RepoHeap;
import dsa.UserGraph;
import models.DummyData;
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

public class ExploreScreen {

    private Stage stage;
    private User currentUser;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;
    private UserGraph userGraph;
    private RepoHeap repoHeap;
    private GitHubApiService gitHubApiService = new GitHubApiService();

    public ExploreScreen(Stage stage, User currentUser, UserStorage userStorage,
                         RepoStorage repoStorage, CommitStorage commitStorage,
                         IssueStorage issueStorage, ActivityTracker activityTracker) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.userStorage = userStorage;
        this.repoStorage = repoStorage;
        this.commitStorage = commitStorage;
        this.issueStorage = issueStorage;
        this.activityTracker = activityTracker;

        this.userGraph = new UserGraph();
        userGraph.addUser(currentUser.getUsername());
        for (User u : DummyData.getDummyUsers()) {
            userGraph.addUser(u.getUsername());
        }

        this.repoHeap = new RepoHeap();
        for (Repo r : DummyData.getDummyRepos()) {
            repoHeap.insert(r);
        }
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

        // ── TAB BAR ──
        HBox tabBar = new HBox(0);
        tabBar.setPadding(new Insets(0, 24, 0, 24));
        tabBar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        Button trendingTab = exploreTabBtn("🔥 Trending", true);
        Button usersTab = exploreTabBtn("👥 Discover People", false);
        Button starredTab = exploreTabBtn("⭐ Starred", false);

        tabBar.getChildren().addAll(trendingTab, usersTab, starredTab);

        // ── CONTENT ──
        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #0d1117;");

        VBox trendingContent = createTrendingTab();
        VBox usersContent = createUsersTab();
        usersContent.setVisible(false);
        usersContent.setManaged(false);
        VBox starredContent = createStarredTab();
        starredContent.setVisible(false);
        starredContent.setManaged(false);

        contentArea.getChildren().addAll(trendingContent, usersContent, starredContent);

        trendingTab.setOnAction(e -> {
            setActiveExploreTab(trendingTab, usersTab, starredTab);
            showOnly(trendingContent, usersContent, starredContent);
        });
        usersTab.setOnAction(e -> {
            setActiveExploreTab(usersTab, trendingTab, starredTab);
            showOnly(usersContent, trendingContent, starredContent);
        });
        starredTab.setOnAction(e -> {
            setActiveExploreTab(starredTab, trendingTab, usersTab);
            showOnly(starredContent, trendingContent, usersContent);
        });

        // ── ROOT ──
        VBox root = new VBox(navbar, tabBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        root.setStyle("-fx-background-color: #0d1117;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");

//        return new Scene(scrollPane);
        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
    }

    // ── TRENDING TAB ──
    private VBox createTrendingTab() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24, 48, 24, 48));
        content.setStyle("-fx-background-color: #0d1117;");

        Text title = new Text("🔥 Trending Repositories");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setFill(Color.web("#e6edf3"));

        Text subtitle = new Text("See what the CodeNest and GitHub community is most excited about today.");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setFill(Color.web("#8b949e"));

        content.getChildren().addAll(title, subtitle);

        Label loading = new Label("⏳ Loading real GitHub trending repos...");
        loading.setTextFill(Color.web("#8b949e"));
        loading.setFont(Font.font("Arial", 14));
        content.getChildren().add(loading);

        new Thread(() -> {
            ArrayList<com.google.gson.JsonObject> repos = gitHubApiService.getTrendingRepos();
            javafx.application.Platform.runLater(() -> {
                content.getChildren().remove(loading);
                if (repos.isEmpty()) {
                    ArrayList<Repo> sorted = repoHeap.getSortedRepos();
                    for (Repo repo : sorted) {
                        content.getChildren().add(createLocalRepoCard(repo));
                    }
                } else {
                    for (com.google.gson.JsonObject repo : repos) {
                        content.getChildren().add(createGitHubRepoCard(repo));
                    }
                }
            });
        }).start();

        return content;
    }

    // ── USERS TAB ──
    private VBox createUsersTab() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24, 48, 24, 48));
        content.setStyle("-fx-background-color: #0d1117;");

        Text title = new Text("👥 Discover People");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setFill(Color.web("#e6edf3"));

        Text subtitle = new Text("Find interesting people to follow on CodeNest.");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setFill(Color.web("#8b949e"));

        content.getChildren().addAll(title, subtitle);

        for (User user : DummyData.getDummyUsers()) {
            content.getChildren().add(createUserCard(user));
        }

        return content;
    }

    // ── STARRED TAB ──
    private VBox createStarredTab() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(24, 48, 24, 48));
        content.setStyle("-fx-background-color: #0d1117;");

        Text title = new Text("⭐ Starred Repositories");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setFill(Color.web("#e6edf3"));

        Label empty = new Label("You haven't starred any repos yet. Explore trending repos!");
        empty.setTextFill(Color.web("#8b949e"));
        empty.setFont(Font.font("Arial", 14));

        content.getChildren().addAll(title, empty);
        return content;
    }

    // ── GITHUB REPO CARD ──
    private HBox createGitHubRepoCard(com.google.gson.JsonObject repo) {
        HBox card = new HBox(16);
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

        String repoName = repo.get("full_name").getAsString();
        String desc = repo.has("description") && !repo.get("description").isJsonNull()
                ? repo.get("description").getAsString() : "No description";
        String language = repo.has("language") && !repo.get("language").isJsonNull()
                ? repo.get("language").getAsString() : "Unknown";
        int stars = repo.get("stargazers_count").getAsInt();

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text name = new Text("📁 " + repoName);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        name.setFill(Color.web("#58a6ff"));

        Text description = new Text(desc.length() > 100 ? desc.substring(0, 100) + "..." : desc);
        description.setFont(Font.font("Arial", 13));
        description.setFill(Color.web("#8b949e"));
        description.setWrappingWidth(500);

        HBox meta = new HBox(20);
        meta.setAlignment(Pos.CENTER_LEFT);

        Text lang = new Text("🔵 " + language);
        lang.setFont(Font.font("Arial", 12));
        lang.setFill(Color.web("#8b949e"));

        Text starsText = new Text("⭐ " + formatNumber(stars));
        starsText.setFont(Font.font("Arial", 12));
        starsText.setFill(Color.web("#8b949e"));

        Text ghTag = new Text("🐙 GitHub");
        ghTag.setFont(Font.font("Arial", 12));
        ghTag.setFill(Color.web("#3fb950"));

        meta.getChildren().addAll(lang, starsText, ghTag);
        info.getChildren().addAll(name, description, meta);

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

    // ── LOCAL REPO CARD ──
    private HBox createLocalRepoCard(Repo repo) {
        HBox card = new HBox(16);
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

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text name = new Text("📁 " + repo.getOwnerUsername() + " / " + repo.getRepoName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        name.setFill(Color.web("#58a6ff"));

        Text desc = new Text(repo.getDescription().isEmpty() ? "No description" : repo.getDescription());
        desc.setFont(Font.font("Arial", 13));
        desc.setFill(Color.web("#8b949e"));

        HBox meta = new HBox(20);
        Text lang = new Text("🔵 " + repo.getLanguage());
        lang.setFont(Font.font("Arial", 12));
        lang.setFill(Color.web("#8b949e"));
        Text stars = new Text("⭐ " + repo.getStars());
        stars.setFont(Font.font("Arial", 12));
        stars.setFill(Color.web("#8b949e"));
        meta.getChildren().addAll(lang, stars);
        info.getChildren().addAll(name, desc, meta);

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
            repo.setStars(repo.getStars() + 1);
            stars.setText("⭐ " + repo.getStars());
            starBtn.setText("⭐ Starred!");
            starBtn.setDisable(true);
        });

        Button forkBtn = new Button("🍴 Fork");
        forkBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 6 14;
                -fx-cursor: hand;
                """);
        forkBtn.setOnAction(e -> {
            Repo forked = new Repo(repo.getRepoName() + "-fork", currentUser.getUsername(),
                    "Forked from " + repo.getOwnerUsername() + "/" + repo.getRepoName(), true);
            for (String f : repo.getFiles()) forked.addFile(f);
            repoStorage.createRepo(forked);
            forkBtn.setText("🍴 Forked!");
            forkBtn.setDisable(true);
        });

        card.getChildren().addAll(info, starBtn, forkBtn);
        return card;
    }

    // ── USER CARD ──
    private HBox createUserCard(User user) {
        HBox card = new HBox(16);
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

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setMinSize(48, 48);
        avatar.setMaxSize(48, 48);
        avatar.setStyle("-fx-background-color: #2d6a4f; -fx-background-radius: 24;");
        Text initials = new Text(String.valueOf(user.getFullName().charAt(0)).toUpperCase());
        initials.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        initials.setFill(Color.WHITE);
        avatar.getChildren().add(initials);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text fullName = new Text(user.getFullName());
        fullName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        fullName.setFill(Color.web("#e6edf3"));

        Text username = new Text("@" + user.getUsername());
        username.setFont(Font.font("Arial", 13));
        username.setFill(Color.web("#8b949e"));

        Text bio = new Text(user.getBio());
        bio.setFont(Font.font("Arial", 12));
        bio.setFill(Color.web("#8b949e"));

        Text uni = new Text("🎓 " + user.getUniversity());
        uni.setFont(Font.font("Arial", 12));
        uni.setFill(Color.web("#8b949e"));

        info.getChildren().addAll(fullName, username, bio, uni);

        Button followBtn = new Button(userGraph.isFollowing(currentUser.getUsername(), user.getUsername()) ? "✓ Following" : "➕ Follow");
        followBtn.setStyle(userGraph.isFollowing(currentUser.getUsername(), user.getUsername()) ? """
                -fx-background-color: #21262d;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 6 14;
                -fx-cursor: hand;
                """ : """
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 6 14;
                -fx-cursor: hand;
                """);

        followBtn.setOnAction(e -> {
            if (userGraph.isFollowing(currentUser.getUsername(), user.getUsername())) {
                userGraph.unfollow(currentUser.getUsername(), user.getUsername());
                followBtn.setText("➕ Follow");
                followBtn.setStyle("""
                        -fx-background-color: #238636;
                        -fx-text-fill: white;
                        -fx-border-radius: 6;
                        -fx-background-radius: 6;
                        -fx-font-size: 12;
                        -fx-padding: 6 14;
                        -fx-cursor: hand;
                        """);
            } else {
                userGraph.follow(currentUser.getUsername(), user.getUsername());
                followBtn.setText("✓ Following");
                followBtn.setStyle("""
                        -fx-background-color: #21262d;
                        -fx-text-fill: #c9d1d9;
                        -fx-border-color: #30363d;
                        -fx-border-radius: 6;
                        -fx-background-radius: 6;
                        -fx-font-size: 12;
                        -fx-padding: 6 14;
                        -fx-cursor: hand;
                        """);
            }
        });

        card.getChildren().addAll(avatar, info, followBtn);
        return card;
    }

    // ── HELPERS ──
    private Button exploreTabBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(active ? """
                -fx-background-color: transparent;
                -fx-text-fill: #f0f6fc;
                -fx-font-size: 14;
                -fx-padding: 12 16;
                -fx-cursor: hand;
                -fx-border-color: #f78166;
                -fx-border-width: 0 0 2 0;
                """ : """
                -fx-background-color: transparent;
                -fx-text-fill: #8b949e;
                -fx-font-size: 14;
                -fx-padding: 12 16;
                -fx-cursor: hand;
                """);
        return btn;
    }

    private void setActiveExploreTab(Button active, Button... others) {
        active.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #f0f6fc;
                -fx-font-size: 14;
                -fx-padding: 12 16;
                -fx-cursor: hand;
                -fx-border-color: #f78166;
                -fx-border-width: 0 0 2 0;
                """);
        for (Button b : others) {
            b.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #8b949e;
                    -fx-font-size: 14;
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

    private String formatNumber(int num) {
        if (num >= 1000) return String.format("%.1fk", num / 1000.0);
        return String.valueOf(num);
    }
}