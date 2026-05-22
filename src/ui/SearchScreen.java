package ui;

import data.ActivityTracker;
import data.CommitStorage;
import data.GitHubApiService;
import data.IssueStorage;
import data.RepoStorage;
import data.SearchEngine;
import data.UserStorage;
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
import java.util.List;

public class SearchScreen {

    private Stage stage;
    private User currentUser;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;
    private SearchEngine searchEngine;
    private GitHubApiService gitHubApiService = new GitHubApiService();

    public SearchScreen(Stage stage, User currentUser, UserStorage userStorage,
                        RepoStorage repoStorage, CommitStorage commitStorage,
                        IssueStorage issueStorage, ActivityTracker activityTracker) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.userStorage = userStorage;
        this.repoStorage = repoStorage;
        this.commitStorage = commitStorage;
        this.issueStorage = issueStorage;
        this.activityTracker = activityTracker;
        this.searchEngine = new SearchEngine();

        ArrayList<Repo> allRepos = repoStorage.getPublicRepos();
        ArrayList<Repo> userRepos = repoStorage.getUserRepos(currentUser.getUsername());
        for (Repo r : userRepos) {
            if (!allRepos.contains(r)) allRepos.add(r);
        }
        searchEngine.loadRepos(allRepos);
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

        // ── SEARCH SECTION ──
        VBox searchSection = new VBox(12);
        searchSection.setPadding(new Insets(24, 48, 16, 48));
        searchSection.setStyle("-fx-background-color: #0d1117;");

        Text heading = new Text("🔍 Search CodeNest & GitHub");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heading.setFill(Color.web("#e6edf3"));

        Text subheading = new Text("Search repositories, users, and code across CodeNest and GitHub.");
        subheading.setFont(Font.font("Arial", 14));
        subheading.setFill(Color.web("#8b949e"));

        // Search bar
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search or jump to...");
        searchField.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 10 14;
                -fx-font-size: 15;
                """);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10 20;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """);

        searchBox.getChildren().addAll(searchField, searchBtn);

        // Autocomplete
        VBox autocompleteBox = new VBox(2);
        autocompleteBox.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                """);
        autocompleteBox.setVisible(false);
        autocompleteBox.setManaged(false);

        // Filter buttons
        HBox filterBox = new HBox(8);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter:");
        filterLabel.setFont(Font.font("Arial", 13));
        filterLabel.setTextFill(Color.web("#8b949e"));

        String[] languages = {"All", "Java", "Python", "C++", "JavaScript", "HTML"};
        for (String lang : languages) {
            Button filterBtn = new Button(lang);
            filterBtn.setStyle("""
                    -fx-background-color: #21262d;
                    -fx-text-fill: #c9d1d9;
                    -fx-border-color: #30363d;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-font-size: 12;
                    -fx-padding: 4 12;
                    -fx-cursor: hand;
                    """);
            filterBtn.setOnMouseEntered(e -> filterBtn.setStyle("""
                    -fx-background-color: #30363d;
                    -fx-text-fill: white;
                    -fx-border-color: #8b949e;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-font-size: 12;
                    -fx-padding: 4 12;
                    -fx-cursor: hand;
                    """));
            filterBtn.setOnMouseExited(e -> filterBtn.setStyle("""
                    -fx-background-color: #21262d;
                    -fx-text-fill: #c9d1d9;
                    -fx-border-color: #30363d;
                    -fx-border-radius: 12;
                    -fx-background-radius: 12;
                    -fx-font-size: 12;
                    -fx-padding: 4 12;
                    -fx-cursor: hand;
                    """));
            filterBtn.setOnAction(e -> {
                if (lang.equals("All")) {
                    searchField.clear();
                } else {
                    searchField.setText(lang);
                    searchBtn.fire();
                }
            });
            filterBox.getChildren().add(filterBtn);
        }

        searchSection.getChildren().addAll(heading, subheading, searchBox, autocompleteBox, filterBox);

        // ── RESULTS ──
        VBox resultsBox = new VBox(10);
        resultsBox.setPadding(new Insets(10, 48, 30, 48));
        resultsBox.setStyle("-fx-background-color: #0d1117;");

        // Default — show user repos
        showLocalResults(repoStorage.getUserRepos(currentUser.getUsername()), resultsBox);

        // Live autocomplete
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            autocompleteBox.getChildren().clear();
            if (newVal.trim().isEmpty()) {
                autocompleteBox.setVisible(false);
                autocompleteBox.setManaged(false);
                return;
            }
            List<String> suggestions = searchEngine.autocomplete(newVal);
            if (suggestions.isEmpty()) {
                autocompleteBox.setVisible(false);
                autocompleteBox.setManaged(false);
                return;
            }
            autocompleteBox.setVisible(true);
            autocompleteBox.setManaged(true);
            for (String suggestion : suggestions) {
                Label suggLbl = new Label("🔍 " + suggestion);
                suggLbl.setPadding(new Insets(8, 12, 8, 12));
                suggLbl.setFont(Font.font("Arial", 13));
                suggLbl.setTextFill(Color.web("#c9d1d9"));
                suggLbl.setMaxWidth(Double.MAX_VALUE);
                suggLbl.setStyle("-fx-cursor: hand;");
                suggLbl.setOnMouseEntered(e -> suggLbl.setStyle("-fx-background-color: #21262d; -fx-cursor: hand;"));
                suggLbl.setOnMouseExited(e -> suggLbl.setStyle("-fx-cursor: hand;"));
                suggLbl.setOnMouseClicked(e -> {
                    searchField.setText(suggestion);
                    autocompleteBox.setVisible(false);
                    autocompleteBox.setManaged(false);
                    searchBtn.fire();
                });
                autocompleteBox.getChildren().add(suggLbl);
            }
        });

        // Search action
        searchBtn.setOnAction(e -> {
            String query = searchField.getText().trim();
            autocompleteBox.setVisible(false);
            autocompleteBox.setManaged(false);
            resultsBox.getChildren().clear();

            if (query.isEmpty()) {
                showLocalResults(repoStorage.getUserRepos(currentUser.getUsername()), resultsBox);
                return;
            }

            // Local results
            ArrayList<Repo> localResults = searchEngine.search(query);
            if (!localResults.isEmpty()) {
                Text localTitle = new Text("📁 Local Repositories (" + localResults.size() + ")");
                localTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                localTitle.setFill(Color.web("#e6edf3"));
                resultsBox.getChildren().add(localTitle);
                for (Repo r : localResults) {
                    resultsBox.getChildren().add(createLocalRepoCard(r));
                }
            }

            // GitHub results
            Label loading = new Label("🐙 Searching GitHub...");
            loading.setTextFill(Color.web("#8b949e"));
            loading.setFont(Font.font("Arial", 13));
            resultsBox.getChildren().add(loading);

            new Thread(() -> {
                ArrayList<com.google.gson.JsonObject> ghRepos = gitHubApiService.searchRepos(query);
                ArrayList<com.google.gson.JsonObject> ghUsers = gitHubApiService.searchUsers(query);

                javafx.application.Platform.runLater(() -> {
                    resultsBox.getChildren().remove(loading);

                    if (!ghRepos.isEmpty()) {
                        Text ghTitle = new Text("🐙 GitHub Repositories (" + ghRepos.size() + ")");
                        ghTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                        ghTitle.setFill(Color.web("#3fb950"));
                        resultsBox.getChildren().add(ghTitle);
                        for (com.google.gson.JsonObject repo : ghRepos) {
                            resultsBox.getChildren().add(createGitHubRepoCard(repo));
                        }
                    }

                    if (!ghUsers.isEmpty()) {
                        Text ghUserTitle = new Text("👥 GitHub Users (" + ghUsers.size() + ")");
                        ghUserTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                        ghUserTitle.setFill(Color.web("#58a6ff"));
                        resultsBox.getChildren().add(ghUserTitle);
                        for (com.google.gson.JsonObject user : ghUsers) {
                            resultsBox.getChildren().add(createGitHubUserCard(user));
                        }
                    }

                    if (ghRepos.isEmpty() && ghUsers.isEmpty() && localResults.isEmpty()) {
                        Label noResult = new Label("No results found for \"" + query + "\"");
                        noResult.setTextFill(Color.web("#8b949e"));
                        noResult.setFont(Font.font("Arial", 14));
                        resultsBox.getChildren().add(noResult);
                    }
                });
            }).start();
        });

        searchField.setOnAction(e -> searchBtn.fire());

        // ── ROOT ──
        VBox mainLayout = new VBox(navbar, searchSection, resultsBox);
        mainLayout.setStyle("-fx-background-color: #0d1117;");

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");

//        return new Scene(scrollPane);
        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
    }

    private void showLocalResults(ArrayList<Repo> repos, VBox resultsBox) {
        resultsBox.getChildren().clear();
        if (repos.isEmpty()) {
            Label noRepo = new Label("No repositories yet. Create your first repository!");
            noRepo.setTextFill(Color.web("#8b949e"));
            noRepo.setFont(Font.font("Arial", 14));
            resultsBox.getChildren().add(noRepo);
            return;
        }
        Text title = new Text("📁 Your Repositories (" + repos.size() + ")");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setFill(Color.web("#e6edf3"));
        resultsBox.getChildren().add(title);
        for (Repo r : repos) {
            resultsBox.getChildren().add(createLocalRepoCard(r));
        }
    }

    private HBox createLocalRepoCard(Repo repo) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 16, 14, 16));
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
        HBox.setHgrow(info, Priority.ALWAYS);

        Text repoName = new Text("📁 " + repo.getOwnerUsername() + " / " + repo.getRepoName());
        repoName.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        repoName.setFill(Color.web("#58a6ff"));

        Text desc = new Text(repo.getDescription().isEmpty() ? "No description" : repo.getDescription());
        desc.setFont(Font.font("Arial", 12));
        desc.setFill(Color.web("#8b949e"));

        HBox meta = new HBox(16);
        Text lang = new Text("🔵 " + repo.getLanguage());
        lang.setFont(Font.font("Arial", 11));
        lang.setFill(Color.web("#8b949e"));
        Text stars = new Text("⭐ " + repo.getStars());
        stars.setFont(Font.font("Arial", 11));
        stars.setFill(Color.web("#8b949e"));
        Text commits = new Text("📝 " + repo.getCommits() + " commits");
        commits.setFont(Font.font("Arial", 11));
        commits.setFill(Color.web("#8b949e"));
        meta.getChildren().addAll(lang, stars, commits);
        info.getChildren().addAll(repoName, desc, meta);

        card.getChildren().add(info);
        card.setOnMouseClicked(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo,
                    userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        return card;
    }

    private HBox createGitHubRepoCard(com.google.gson.JsonObject repo) {
        HBox card = new HBox(14);
        card.setPadding(new Insets(14, 16, 14, 16));
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

        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text name = new Text("📁 " + repoName);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        name.setFill(Color.web("#58a6ff"));

        Text description = new Text(desc.length() > 80 ? desc.substring(0, 80) + "..." : desc);
        description.setFont(Font.font("Arial", 12));
        description.setFill(Color.web("#8b949e"));

        HBox meta = new HBox(16);
        Text lang = new Text("🔵 " + language);
        lang.setFont(Font.font("Arial", 11));
        lang.setFill(Color.web("#8b949e"));
        Text starsText = new Text("⭐ " + (stars >= 1000 ? String.format("%.1fk", stars / 1000.0) : stars));
        starsText.setFont(Font.font("Arial", 11));
        starsText.setFill(Color.web("#8b949e"));
        Text ghTag = new Text("🐙 GitHub");
        ghTag.setFont(Font.font("Arial", 11));
        ghTag.setFill(Color.web("#3fb950"));
        meta.getChildren().addAll(lang, starsText, ghTag);
        info.getChildren().addAll(name, description, meta);

        card.getChildren().add(info);
        return card;
    }

    private HBox createGitHubUserCard(com.google.gson.JsonObject user) {
        HBox card = new HBox(14);
        card.setPadding(new Insets(14, 16, 14, 16));
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

        String username = user.get("login").getAsString();
        String userType = user.get("type").getAsString();

        StackPane avatar = new StackPane();
        avatar.setMinSize(44, 44);
        avatar.setMaxSize(44, 44);
        avatar.setStyle("-fx-background-color: #2d6a4f; -fx-background-radius: 22;");
        Text initials = new Text(String.valueOf(username.charAt(0)).toUpperCase());
        initials.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        initials.setFill(Color.WHITE);
        avatar.getChildren().add(initials);

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text usernameText = new Text("👤 " + username);
        usernameText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        usernameText.setFill(Color.web("#e6edf3"));

        HBox meta = new HBox(12);
        Text typeText = new Text(userType.equals("Organization") ? "🏢 Organization" : "👤 User");
        typeText.setFont(Font.font("Arial", 12));
        typeText.setFill(Color.web("#8b949e"));
        Text ghTag = new Text("🐙 GitHub");
        ghTag.setFont(Font.font("Arial", 11));
        ghTag.setFill(Color.web("#3fb950"));
        meta.getChildren().addAll(typeText, ghTag);
        info.getChildren().addAll(usernameText, meta);

        Button followBtn = new Button("➕ Follow");
        followBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 12;
                -fx-padding: 5 12;
                -fx-cursor: hand;
                """);
        followBtn.setOnAction(e -> {
            followBtn.setText("✓ Following");
            followBtn.setStyle("""
                    -fx-background-color: #21262d;
                    -fx-text-fill: #c9d1d9;
                    -fx-border-color: #30363d;
                    -fx-border-radius: 6;
                    -fx-background-radius: 6;
                    -fx-font-size: 12;
                    -fx-padding: 5 12;
                    """);
            followBtn.setDisable(true);
        });

        card.getChildren().addAll(avatar, info, followBtn);
        return card;
    }
}