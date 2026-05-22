package ui;

import data.ActivityTracker;
import data.IssueStorage;
import data.CommitStorage;
import data.RepoStorage;
import data.UserStorage;
import dsa.CommitStack;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class CommitHistoryScreen {

    private Stage stage;
    private User currentUser;
    private Repo repo;
    private String fileName;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public CommitHistoryScreen(Stage stage, User currentUser, Repo repo, String fileName,
                               UserStorage userStorage, RepoStorage repoStorage,
                               CommitStorage commitStorage, IssueStorage issueStorage, ActivityTracker activityTracker) {
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

        // ── Navbar ──
        HBox navbar = new HBox();
        navbar.setPadding(new Insets(14, 24, 14, 24));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setSpacing(20);
        navbar.setStyle("-fx-background-color: #2d6a4f;");

        Text logo = new Text("CodeNest");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        logo.setFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = new Button("← Back to Editor");
        backBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-cursor: hand;
                """);

        navbar.getChildren().addAll(logo, spacer, backBtn);

        // ── Header ──
        VBox header = new VBox(4);
        header.setPadding(new Insets(20, 30, 10, 30));

        Text title = new Text("Commit History");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.web("#222222"));

        Text subtitle = new Text("📁 " + repo.getRepoName() + "  /  " + fileName);
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setFill(Color.web("#666666"));

        header.getChildren().addAll(title, subtitle);

        // ── Undo Button ──
        Button undoBtn = new Button("↩️ Undo Last Commit");
        undoBtn.setStyle("""
                -fx-background-color: #e63946;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10 20;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """);

        Label undoMsg = new Label("");
        undoMsg.setFont(Font.font("Arial", 13));
        undoMsg.setTextFill(Color.web("#2d6a4f"));

        HBox undoBox = new HBox(16, undoBtn, undoMsg);
        undoBox.setPadding(new Insets(10, 30, 10, 30));
        undoBox.setAlignment(Pos.CENTER_LEFT);

        // ── Commit List ──
        VBox commitList = new VBox(10);
        commitList.setPadding(new Insets(10, 30, 30, 30));

        refreshCommitList(commitList);

        // ── Undo Action ──
        undoBtn.setOnAction(e -> {
            Commit undone = commitStorage.undo(
                    currentUser.getUsername(), repo.getRepoName(), fileName);

            if (undone != null) {
                // Restore previous content
                Commit previous = commitStorage.getCommits(
                        currentUser.getUsername(), repo.getRepoName(), fileName).peek();

                String restoredContent = previous != null ? previous.getContent() : "// Start coding here...\n";

                try {
                    Files.write(
                            Paths.get("repos", currentUser.getUsername(), repo.getRepoName(), fileName),
                            restoredContent.getBytes()
                    );
                } catch (IOException ex) {
                    System.out.println("Error restoring file: " + ex.getMessage());
                }

                if (repo.getCommits() > 0) {
                    repo.setCommits(repo.getCommits() - 1);
                    repoStorage.updateRepo(repo);
                }

                undoMsg.setText("✅ Undone: " + undone.getMessage());
                refreshCommitList(commitList);
            } else {
                undoMsg.setTextFill(Color.RED);
                undoMsg.setText("No commits to undo!");
            }
        });

        // ── Back Action ──
        backBtn.setOnAction(e -> {
            EditorScreen editor = new EditorScreen(
                    stage, currentUser, repo, fileName, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(editor.getScene());
            stage.setMaximized(true);
        });

        // ── Main Layout ──
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(navbar, header, undoBox, commitList);
        mainLayout.setStyle("-fx-background-color: #f0f4f0;");

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #f0f4f0;");

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
//        return new Scene(scrollPane);
    }

    private void refreshCommitList(VBox commitList) {
        commitList.getChildren().clear();

        CommitStack commitStack = commitStorage.getCommits(
                currentUser.getUsername(), repo.getRepoName(), fileName);

        if (commitStack.isEmpty()) {
            Label noCommits = new Label("No commits yet for this file.");
            noCommits.setTextFill(Color.web("#999999"));
            noCommits.setFont(Font.font("Arial", 14));
            commitList.getChildren().add(noCommits);
            return;
        }

        // Stack copy karke reverse order mein dikhao (latest first)
        Stack<Commit> temp = new Stack<>();
        Stack<Commit> all = commitStack.getAllCommits();
        temp.addAll(all);

        while (!temp.isEmpty()) {
            Commit c = temp.pop();
            commitList.getChildren().add(commitCard(c));
        }
    }

    private HBox commitCard(Commit commit) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 5, 0, 0, 2);
                """);

        Text icon = new Text("📝");
        icon.setFont(Font.font("Arial", 20));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Text message = new Text(commit.getMessage());
        message.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        message.setFill(Color.web("#222222"));

        Text time = new Text("🕐 " + commit.getTimestamp() + "  |  📄 " + commit.getFileName());
        time.setFont(Font.font("Arial", 12));
        time.setFill(Color.web("#888888"));

        info.getChildren().addAll(message, time);
        card.getChildren().addAll(icon, info);
        return card;
    }
}