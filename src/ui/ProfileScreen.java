package ui;

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
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class ProfileScreen {

    private Stage stage;
    private User currentUser;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public ProfileScreen(Stage stage, User currentUser, UserStorage userStorage,
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

        // ── PROFILE TAB BAR ──
        HBox profileTabBar = new HBox(0);
        profileTabBar.setPadding(new Insets(0, 24, 0, 24));
        profileTabBar.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        Button overviewTab = profileNavBtn("Overview", true);
        Button reposTabBtn = profileNavBtn("Repositories", false);
        Button projectsTabBtn = profileNavBtn("Projects", false);
        Button starsTabBtn = profileNavBtn("Stars", false);

        profileTabBar.getChildren().addAll(overviewTab, reposTabBtn, projectsTabBtn, starsTabBtn);

        // ── MAIN LAYOUT ──
        HBox mainContent = new HBox(24);
        mainContent.setPadding(new Insets(24, 48, 24, 48));
        mainContent.setStyle("-fx-background-color: #0d1117;");


        // ── LEFT — Profile Card ──
        VBox leftCol = new VBox(16);
        leftCol.setPrefWidth(280);
        leftCol.setMinWidth(280);
        leftCol.setMaxWidth(280);

        // Avatar
        StackPane avatar = new StackPane();
        avatar.setPrefSize(260, 260);
        avatar.setMaxSize(260, 260);

        String picPath = currentUser.getProfilePicPath();
        if (picPath != null && !picPath.isEmpty()) {
            try {
                ImageView profilePic = new ImageView(new Image(new FileInputStream(new File(picPath))));
                profilePic.setFitWidth(260);
                profilePic.setFitHeight(260);
                profilePic.setPreserveRatio(true);
                Circle clip = new Circle(130, 130, 130);
                profilePic.setClip(clip);
                avatar.getChildren().add(profilePic);
                avatar.setStyle("-fx-background-color: #2d6a4f; -fx-background-radius: 130;");
            } catch (Exception ex) {
                avatar.getChildren().add(avatarInitials(260));
            }
        } else {
            avatar.getChildren().add(avatarInitials(260));
        }

        // Name
        Text fullName = new Text(currentUser.getFullName());
        fullName.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        fullName.setFill(Color.web("#e6edf3"));

        Text username = new Text("@" + currentUser.getUsername());
        username.setFont(Font.font("Arial", 18));
        username.setFill(Color.web("#8b949e"));

        // Bio
        Text bio = new Text(currentUser.getBio().isEmpty() ? "No bio yet" : currentUser.getBio());
        bio.setFont(Font.font("Arial", 14));
        bio.setFill(Color.web("#e6edf3"));
        bio.setWrappingWidth(260);

        // Edit profile button
        Button editBtn = new Button("Edit profile");
        editBtn.setMaxWidth(Double.MAX_VALUE);
        editBtn.setStyle("""
                -fx-background-color: #21262d;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-font-size: 13;
                -fx-padding: 6 16;
                -fx-cursor: hand;
                """);

        // Change photo
        Button changePicBtn = new Button("📷 Change photo");
        changePicBtn.setMaxWidth(Double.MAX_VALUE);
        changePicBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #58a6ff;
                -fx-font-size: 12;
                -fx-cursor: hand;
                -fx-padding: 4 0;
                """);
        changePicBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Profile Picture");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                currentUser.setProfilePicPath(file.getAbsolutePath());
                userStorage.updateUser(currentUser);
                stage.setScene(getScene());
                stage.setMaximized(true);
            }
        });

        // Followers/Following
        HBox followStats = new HBox(12);
        followStats.setAlignment(Pos.CENTER_LEFT);

        int totalCommits = 0;
        ArrayList<Repo> userRepos = repoStorage.getUserRepos(currentUser.getUsername());
        for (Repo r : userRepos) totalCommits += r.getCommits();

        HBox followersBox = new HBox(4);
        Text followersIcon = new Text("👥");
        Text followersCount = new Text("0");
        followersCount.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        followersCount.setFill(Color.web("#e6edf3"));
        Text followersLabel = new Text("followers");
        followersLabel.setFont(Font.font("Arial", 13));
        followersLabel.setFill(Color.web("#8b949e"));
        followersBox.getChildren().addAll(followersIcon, followersCount, followersLabel);

        HBox followingBox = new HBox(4);
        Text followingCount = new Text("0");
        followingCount.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        followingCount.setFill(Color.web("#e6edf3"));
        Text followingLabel = new Text("following");
        followingLabel.setFont(Font.font("Arial", 13));
        followingLabel.setFill(Color.web("#8b949e"));
        followingBox.getChildren().addAll(followingCount, followingLabel);

        followStats.getChildren().addAll(followersBox, followingBox);

        // Info items
        VBox infoBox = new VBox(8);
        if (!currentUser.getUniversity().isEmpty()) {
            infoBox.getChildren().add(infoItem("🎓", currentUser.getUniversity()));
        }
        infoBox.getChildren().add(infoItem("📝", totalCommits + " total commits"));
        infoBox.getChildren().add(infoItem("📁", userRepos.size() + " repositories"));

        // Edit form
        VBox editForm = createEditForm();
        editForm.setVisible(false);
        editForm.setManaged(false);

        editBtn.setOnAction(e -> {
            editForm.setVisible(true);
            editForm.setManaged(true);
            editBtn.setVisible(false);
            editBtn.setManaged(false);
        });

        leftCol.getChildren().addAll(
                avatar, changePicBtn, fullName, username, bio,
                editBtn, followStats, infoBox, editForm
        );

        // ── RIGHT — Tab Content ──
        VBox rightCol = new VBox(0);
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        // Overview content
        VBox overviewContent = createOverviewTab(totalCommits);

        // Repos content
        VBox reposContent = createReposTab();
        reposContent.setVisible(false);
        reposContent.setManaged(false);

        // Projects content (dummy)
        VBox projectsContent = createDummyTab("📋 Projects", "No projects yet.");
        projectsContent.setVisible(false);
        projectsContent.setManaged(false);

        // Stars content
        VBox starsContent = createStarsTab();
        starsContent.setVisible(false);
        starsContent.setManaged(false);

        StackPane tabContent = new StackPane(overviewContent, reposContent, projectsContent, starsContent);
//        rightCol.getChildren().add(tabContent);
        rightCol.getChildren().addAll(overviewContent, reposContent, projectsContent, starsContent);

        // Tab switching
        overviewTab.setOnAction(e -> {
            setActiveProfileTab(overviewTab, reposTabBtn, projectsTabBtn, starsTabBtn);
            showOnly(overviewContent, reposContent, projectsContent, starsContent);
        });
        reposTabBtn.setOnAction(e -> {
            setActiveProfileTab(reposTabBtn, overviewTab, projectsTabBtn, starsTabBtn);
            showOnly(reposContent, overviewContent, projectsContent, starsContent);
        });
        projectsTabBtn.setOnAction(e -> {
            setActiveProfileTab(projectsTabBtn, overviewTab, reposTabBtn, starsTabBtn);
            showOnly(projectsContent, overviewContent, reposContent, starsContent);
        });
        starsTabBtn.setOnAction(e -> {
            setActiveProfileTab(starsTabBtn, overviewTab, reposTabBtn, projectsTabBtn);
            showOnly(starsContent, overviewContent, reposContent, projectsContent);
        });

        mainContent.getChildren().addAll(leftCol, rightCol);

        // ── ROOT ──
        VBox root = new VBox(navbar, profileTabBar, mainContent);
        root.setStyle("-fx-background-color: #0d1117;");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


//        return new Scene(scrollPane);
        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
    }

    // ── OVERVIEW TAB ──
    private VBox createOverviewTab(int totalCommits) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0, 0, 20, 0));

        // Pinned repos
        Text pinnedTitle = new Text("Pinned");
        pinnedTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        pinnedTitle.setFill(Color.web("#e6edf3"));

        // Get top 4 repos
        RepoHeap heap = new RepoHeap();
        ArrayList<Repo> repos = repoStorage.getUserRepos(currentUser.getUsername());
        for (Repo r : repos) heap.insert(r);

        HBox pinnedGrid = new HBox(12);

        int pinCount = 0;
        while (!heap.isEmpty() && pinCount < 4) {
            Repo r = heap.extractMax();
            pinnedGrid.getChildren().add(pinnedRepoCard(r));
            pinCount++;
        }

        if (pinnedGrid.getChildren().isEmpty()) {
            Label noPinned = new Label("No repositories yet. Create your first repository!");
            noPinned.setTextFill(Color.web("#8b949e"));
            noPinned.setFont(Font.font("Arial", 13));
            pinnedGrid.getChildren().add(noPinned);
        }

        content.getChildren().addAll(pinnedTitle, pinnedGrid);

        // Contribution graph
        int[] activityData = activityTracker.getWeeklyActivity(currentUser.getUsername());
        int totalThisWeek = 0;
        for (int v : activityData) totalThisWeek += v;

        Text contribTitle = new Text(totalCommits + " contributions in the last year");
        contribTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        contribTitle.setFill(Color.web("#e6edf3"));

        // Contribution grid (52 weeks x 7 days)
        VBox contribGraph = new VBox(4);
        contribGraph.setPadding(new Insets(16));
        contribGraph.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        // Month labels row
        HBox monthLabels = new HBox(0);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (int m = 0; m < 12; m++) {
            Label monthLabel = new Label(months[m]);
            monthLabel.setFont(Font.font("Arial", 10));
            monthLabel.setTextFill(Color.web("#8b949e"));
            monthLabel.setPrefWidth(44);
            monthLabels.getChildren().add(monthLabel);
        }

        // Grid
        HBox grid = new HBox(3);
        grid.setAlignment(Pos.TOP_LEFT);

        // Day labels
        VBox dayLabels = new VBox(3);
        String[] days = {"", "Mon", "", "Wed", "", "Fri", ""};
        for (String day : days) {
            Label dayLabel = new Label(day);
            dayLabel.setFont(Font.font("Arial", 9));
            dayLabel.setTextFill(Color.web("#8b949e"));
            dayLabel.setPrefHeight(12);
            dayLabels.getChildren().add(dayLabel);
        }

        grid.getChildren().add(dayLabels);

        // 52 weeks
        for (int week = 0; week < 52; week++) {
            VBox weekCol = new VBox(3);
            for (int day = 0; day < 7; day++) {
                int index = week * 7 + day;
                // Use real data for last 7 days
                int commits = 0;
                if (index >= 52 * 7 - 7) {
                    int recentIndex = index - (52 * 7 - 7);
                    if (recentIndex < activityData.length) {
                        commits = activityData[recentIndex];
                    }
                }

                Rectangle cell = new Rectangle(11, 11);
                if (commits == 0) {
                    cell.setFill(Color.web("#161b22"));
                    cell.setStroke(Color.web("#30363d"));
                    cell.setStrokeWidth(0.5);
                } else if (commits == 1) {
                    cell.setFill(Color.web("#0e4429"));
                } else if (commits <= 3) {
                    cell.setFill(Color.web("#006d32"));
                } else if (commits <= 6) {
                    cell.setFill(Color.web("#26a641"));
                } else {
                    cell.setFill(Color.web("#39d353"));
                }
                cell.setArcWidth(2);
                cell.setArcHeight(2);
                weekCol.getChildren().add(cell);
            }
            grid.getChildren().add(weekCol);
        }

        // Legend
        HBox legend = new HBox(6);
        legend.setAlignment(Pos.CENTER_RIGHT);
        Text lessText = new Text("Less");
        lessText.setFont(Font.font("Arial", 10));
        lessText.setFill(Color.web("#8b949e"));

        String[] legendColors = {"#161b22", "#0e4429", "#006d32", "#26a641", "#39d353"};
        HBox legendCells = new HBox(3);
        for (String color : legendColors) {
            Rectangle cell = new Rectangle(11, 11);
            cell.setFill(Color.web(color));
            cell.setStroke(Color.web("#30363d"));
            cell.setStrokeWidth(0.5);
            cell.setArcWidth(2);
            cell.setArcHeight(2);
            legendCells.getChildren().add(cell);
        }

        Text moreText = new Text("More");
        moreText.setFont(Font.font("Arial", 10));
        moreText.setFill(Color.web("#8b949e"));
        legend.getChildren().addAll(lessText, legendCells, moreText);

        contribGraph.getChildren().addAll(monthLabels, grid, legend);

        // Contribution Activity
        Text activityTitle = new Text("Contribution activity");
        activityTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        activityTitle.setFill(Color.web("#e6edf3"));

        VBox activityBox = new VBox(8);

        // 2026
        Text year2026 = new Text("2026");
        year2026.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        year2026.setFill(Color.web("#e6edf3"));

        activityBox.getChildren().add(year2026);

        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        LocalDate today = LocalDate.now();
        for (int m = today.getMonthValue(); m >= 1; m--) {
            int repoCount = repos.size();
            int commitCount = totalCommits / 12 + (m % 3);

            HBox monthActivity = new HBox(12);
            monthActivity.setPadding(new Insets(8, 12, 8, 12));
            monthActivity.setAlignment(Pos.CENTER_LEFT);
            monthActivity.setStyle("""
                    -fx-background-color: #161b22;
                    -fx-border-color: #30363d;
                    -fx-border-radius: 6;
                    -fx-background-radius: 6;
                    """);

            Label monthName = new Label(monthNames[m - 1]);
            monthName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            monthName.setTextFill(Color.web("#e6edf3"));
            monthName.setPrefWidth(100);

            Text monthCommits = new Text("📝 " + commitCount + " commits");
            monthCommits.setFont(Font.font("Arial", 12));
            monthCommits.setFill(Color.web("#8b949e"));

            Text monthRepos = new Text("📁 " + repoCount + " repositories");
            monthRepos.setFont(Font.font("Arial", 12));
            monthRepos.setFill(Color.web("#8b949e"));

            monthActivity.getChildren().addAll(monthName, monthCommits, monthRepos);
            activityBox.getChildren().add(monthActivity);
        }

        // 2025
        Text year2025 = new Text("2025");
        year2025.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        year2025.setFill(Color.web("#e6edf3"));
        activityBox.getChildren().add(year2025);

        for (int m = 12; m >= 1; m--) {
            int commitCount = (int)(Math.random() * 10);
            HBox monthActivity = new HBox(12);
            monthActivity.setPadding(new Insets(8, 12, 8, 12));
            monthActivity.setAlignment(Pos.CENTER_LEFT);
            monthActivity.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6;");

            Label monthName = new Label(monthNames[m - 1]);
            monthName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            monthName.setTextFill(Color.web("#e6edf3"));
            monthName.setPrefWidth(100);

            Text monthCommits = new Text("📝 " + commitCount + " commits");
            monthCommits.setFont(Font.font("Arial", 12));
            monthCommits.setFill(Color.web("#8b949e"));

            monthActivity.getChildren().addAll(monthName, monthCommits);
            activityBox.getChildren().add(monthActivity);
        }

        Button showMoreBtn = new Button("Show more activity ▾");
        showMoreBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #58a6ff; -fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 0;");

        content.getChildren().addAll(contribTitle, contribGraph, activityTitle, activityBox, showMoreBtn);
        return content;
    }

    // ── REPOS TAB ──
    private VBox createReposTab() {
        VBox content = new VBox(0);
        content.setPadding(new Insets(16, 0, 16, 0));

        // Search + filter bar
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(0, 0, 16, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Find a repository...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #161b22; -fx-text-fill: #c9d1d9; -fx-prompt-text-fill: #6e7681; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8 12; -fx-font-size: 13;");

        Button typeBtn = new Button("Type ▾");
        Button langBtn = new Button("Language ▾");
        Button sortBtn = new Button("Sort ▾");

        for (Button btn : new Button[]{typeBtn, langBtn, sortBtn}) {
            btn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12; -fx-font-size: 12; -fx-cursor: hand;");
        }

        Button newRepoBtn = new Button("+ New");
        newRepoBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 12; -fx-font-size: 12; -fx-cursor: hand;");
        newRepoBtn.setOnAction(e -> {
            RepoScreen rs = new RepoScreen(stage, currentUser, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(rs.getScene());
            stage.setMaximized(true);
        });

        filterBar.getChildren().addAll(searchField, typeBtn, langBtn, sortBtn, newRepoBtn);

        VBox reposList = new VBox(0);
        ArrayList<Repo> repos = repoStorage.getUserRepos(currentUser.getUsername());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            reposList.getChildren().clear();
            for (Repo r : repos) {
                if (r.getRepoName().toLowerCase().contains(newVal.toLowerCase())) {
                    reposList.getChildren().add(repoCard(r));
                }
            }
        });

        for (Repo r : repos) {
            reposList.getChildren().add(repoCard(r));
        }

        content.getChildren().addAll(filterBar, reposList);
        return content;
    }

    private VBox repoCard(Repo repo) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16, 0, 16, 0));
        card.setStyle("-fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Text repoName = new Text(repo.getRepoName());
        repoName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        repoName.setFill(Color.web("#58a6ff"));
        repoName.setStyle("-fx-cursor: hand;");
        repoName.setOnMouseClicked(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        Label visTag = new Label(repo.isPublic() ? "Public" : "Private");
        visTag.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-border-color: #30363d; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 2 8; -fx-font-size: 11;");

        Region tSpacer = new Region();
        HBox.setHgrow(tSpacer, Priority.ALWAYS);

        Button starBtn = new Button("⭐ Star");
        starBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12; -fx-padding: 4 10; -fx-cursor: hand;");

        titleRow.getChildren().addAll(repoName, visTag, tSpacer, starBtn);

        Text desc = new Text(repo.getDescription().isEmpty() ? "" : repo.getDescription());
        desc.setFont(Font.font("Arial", 13));
        desc.setFill(Color.web("#8b949e"));

        HBox meta = new HBox(20);
        meta.setAlignment(Pos.CENTER_LEFT);

        if (!repo.getLanguage().equals("None")) {
            HBox langBox = new HBox(6);
            Rectangle langDot = new Rectangle(12, 12);
            langDot.setFill(Color.web("#2d6a4f"));
            langDot.setArcWidth(6);
            langDot.setArcHeight(6);
            Text lang = new Text(repo.getLanguage());
            lang.setFont(Font.font("Arial", 12));
            lang.setFill(Color.web("#8b949e"));
            langBox.setAlignment(Pos.CENTER_LEFT);
            langBox.getChildren().addAll(langDot, lang);
            meta.getChildren().add(langBox);
        }

        Text stars = new Text("⭐ " + repo.getStars());
        stars.setFont(Font.font("Arial", 12));
        stars.setFill(Color.web("#8b949e"));

        Text commits = new Text("📝 " + repo.getCommits() + " commits");
        commits.setFont(Font.font("Arial", 12));
        commits.setFill(Color.web("#8b949e"));

        Text updated = new Text("Updated recently");
        updated.setFont(Font.font("Arial", 12));
        updated.setFill(Color.web("#8b949e"));

        meta.getChildren().addAll(stars, commits, updated);
        card.getChildren().addAll(titleRow, desc, meta);
        return card;
    }

    // ── STARS TAB ──
    private VBox createStarsTab() {
        VBox content = new VBox(12);
        content.setPadding(new Insets(20, 0, 20, 0));
        Label msg = new Label("⭐ Repositories you star will appear here.");
        msg.setTextFill(Color.web("#8b949e"));
        msg.setFont(Font.font("Arial", 14));
        content.getChildren().add(msg);
        return content;
    }

    // ── DUMMY TAB ──
    private VBox createDummyTab(String title, String msg) {
        VBox content = new VBox(12);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        Text t = new Text(msg);
        t.setFont(Font.font("Arial", 14));
        t.setFill(Color.web("#8b949e"));
        content.getChildren().add(t);
        return content;
    }

    // ── EDIT FORM ──
    private VBox createEditForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(16));
        form.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");

        TextField nameField = new TextField(currentUser.getFullName());
        nameField.setStyle(fieldStyle());
        nameField.setPromptText("Full Name");

        TextField bioField = new TextField(currentUser.getBio());
        bioField.setStyle(fieldStyle());
        bioField.setPromptText("Bio");

        TextField uniField = new TextField(currentUser.getUniversity());
        uniField.setStyle(fieldStyle());
        uniField.setPromptText("University");

        Label msg = new Label("");
        msg.setTextFill(Color.web("#3fb950"));

        Button saveBtn = new Button("Save profile");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color: #238636; -fx-text-fill: white; -fx-font-size: 13; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setStyle("-fx-background-color: #21262d; -fx-text-fill: #c9d1d9; -fx-border-color: #30363d; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 13; -fx-padding: 8 16; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            currentUser.setFullName(nameField.getText().trim());
            currentUser.setBio(bioField.getText().trim());
            currentUser.setUniversity(uniField.getText().trim());
            userStorage.updateUser(currentUser);
            stage.setScene(getScene());
            stage.setMaximized(true);
        });

        cancelBtn.setOnAction(e -> stage.setScene(getScene()));
        stage.setMaximized(true);

        Label editTitle = new Label("Edit Profile");
        editTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        editTitle.setTextFill(Color.WHITE);

        form.getChildren().addAll(editTitle, nameField, bioField, uniField, msg, saveBtn, cancelBtn);
        return form;
    }

    // ── PINNED REPO CARD ──
    private VBox pinnedRepoCard(Repo repo) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setPrefWidth(200);
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

        HBox titleRow = new HBox(6);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Text repoIcon = new Text("📁");
        Text repoName = new Text(repo.getRepoName());
        repoName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        repoName.setFill(Color.web("#58a6ff"));
        Label visTag = new Label(repo.isPublic() ? "Public" : "Private");
        visTag.setStyle("-fx-background-color: transparent; -fx-text-fill: #8b949e; -fx-border-color: #30363d; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 1 6; -fx-font-size: 10;");
        titleRow.getChildren().addAll(repoIcon, repoName, visTag);

        Text desc = new Text(repo.getDescription().isEmpty() ? "No description" : repo.getDescription());
        desc.setFont(Font.font("Arial", 11));
        desc.setFill(Color.web("#8b949e"));
        desc.setWrappingWidth(180);

        HBox meta = new HBox(10);
        if (!repo.getLanguage().equals("None")) {
            Text lang = new Text("🔵 " + repo.getLanguage());
            lang.setFont(Font.font("Arial", 11));
            lang.setFill(Color.web("#8b949e"));
            meta.getChildren().add(lang);
        }
        Text stars = new Text("⭐ " + repo.getStars());
        stars.setFont(Font.font("Arial", 11));
        stars.setFill(Color.web("#8b949e"));
        meta.getChildren().add(stars);

        card.getChildren().addAll(titleRow, desc, meta);
        card.setOnMouseClicked(e -> {
            RepoDetailScreen detail = new RepoDetailScreen(stage, currentUser, repo, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(detail.getScene());
            stage.setMaximized(true);
        });

        return card;
    }

    // ── HELPERS ──
    private StackPane avatarInitials(int size) {
        StackPane circle = new StackPane();
        circle.setPrefSize(size, size);
        circle.setMaxSize(size, size);
        circle.setStyle("-fx-background-color: #2d6a4f; -fx-background-radius: " + (size / 2) + ";");
        String initials = currentUser.getFullName().isEmpty() ? "?" :
                String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase();
        Text text = new Text(initials);
        text.setFont(Font.font("Arial", FontWeight.BOLD, size / 3));
        text.setFill(Color.WHITE);
        circle.getChildren().add(text);
        return circle;
    }

    private Button profileNavBtn(String text, boolean active) {
        Button btn = new Button(text);
        btn.setStyle(active ? """
                -fx-background-color: transparent;
                -fx-text-fill: #f0f6fc;
                -fx-font-size: 14;
                -fx-padding: 14 16;
                -fx-cursor: hand;
                -fx-border-color: #f78166;
                -fx-border-width: 0 0 2 0;
                """ : """
                -fx-background-color: transparent;
                -fx-text-fill: #8b949e;
                -fx-font-size: 14;
                -fx-padding: 14 16;
                -fx-cursor: hand;
                """);
        return btn;
    }

    private void setActiveProfileTab(Button active, Button... others) {
        active.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #f0f6fc;
                -fx-font-size: 14;
                -fx-padding: 14 16;
                -fx-cursor: hand;
                -fx-border-color: #f78166;
                -fx-border-width: 0 0 2 0;
                """);
        for (Button b : others) {
            b.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #8b949e;
                    -fx-font-size: 14;
                    -fx-padding: 14 16;
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

    private HBox infoItem(String icon, String text) {
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

    private String fieldStyle() {
        return """
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """;
    }
}