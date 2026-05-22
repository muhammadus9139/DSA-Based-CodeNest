package ui;

import data.ActivityTracker;
import data.CommitStorage;
import data.IssueStorage;
import data.RepoStorage;
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

public class RepoScreen {

    private Stage stage;
    private User currentUser;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public RepoScreen(Stage stage, User currentUser, UserStorage userStorage,
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
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, new RepoStorage(), commitStorage, issueStorage, activityTracker);            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);

        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("""
                -fx-background-color: transparent;
                -fx-text-fill: #8b949e;
                -fx-font-size: 13;
                -fx-cursor: hand;
                """);

        navbar.getChildren().addAll(logo, navSpacer, backBtn);

        // ── FORM ──
        VBox form = new VBox(20);
        form.setPadding(new Insets(32, 48, 32, 48));
        form.setMaxWidth(700);
        form.setStyle("-fx-background-color: #0d1117;");

        StackPane centeredForm = new StackPane(form);
        centeredForm.setStyle("-fx-background-color: #0d1117;");
        StackPane.setAlignment(form, Pos.TOP_CENTER);

        Text formTitle = new Text("Create a new repository");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        formTitle.setFill(Color.WHITE);

        Text formSubtitle = new Text("A repository contains all project files, including the revision history.");
        formSubtitle.setFont(Font.font("Arial", 13));
        formSubtitle.setFill(Color.web("#8b949e"));

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #30363d;");

        // Owner / Repo name row
        HBox ownerRow = new HBox(12);
        ownerRow.setAlignment(Pos.BOTTOM_LEFT);

        VBox ownerBox = new VBox(6);
        Label ownerLabel = new Label("Owner");
        ownerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        ownerLabel.setTextFill(Color.web("#c9d1d9"));
        Button ownerBtn = new Button("👤 " + currentUser.getUsername() + " ▾");
        ownerBtn.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #c9d1d9;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """);
        ownerBox.getChildren().addAll(ownerLabel, ownerBtn);

        Text slash = new Text("/");
        slash.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        slash.setFill(Color.web("#8b949e"));

        VBox repoNameBox = new VBox(6);
        Label repoNameLabel = new Label("Repository name *");
        repoNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        repoNameLabel.setTextFill(Color.web("#c9d1d9"));
        TextField repoNameField = new TextField();
        repoNameField.setPromptText("my-awesome-repo");
        repoNameField.setPrefWidth(300);
        repoNameField.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """);
        repoNameBox.getChildren().addAll(repoNameLabel, repoNameField);

        ownerRow.getChildren().addAll(ownerBox, slash, repoNameBox);

        // Name availability
        Label nameMsg = new Label("");
        nameMsg.setFont(Font.font("Arial", 12));

        repoNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) { nameMsg.setText(""); return; }
            boolean exists = repoStorage.getUserRepos(currentUser.getUsername())
                    .stream().anyMatch(r -> r.getRepoName().equals(newVal.trim()));
            if (exists) {
                nameMsg.setText("❌ Name already taken");
                nameMsg.setTextFill(Color.web("#f85149"));
            } else {
                nameMsg.setText("✅ " + newVal.trim() + " is available");
                nameMsg.setTextFill(Color.web("#3fb950"));
            }
        });

        // Description
        VBox descBox = new VBox(6);
        Label descLabel = new Label("Description (optional)");
        descLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        descLabel.setTextFill(Color.web("#c9d1d9"));
        TextField descField = new TextField();
        descField.setPromptText("Short description of your repository");
        descField.setStyle("""
                -fx-background-color: #161b22;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 8 12;
                -fx-font-size: 13;
                """);
        descBox.getChildren().addAll(descLabel, descField);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #30363d;");

        // Visibility
        Label visLabel = new Label("Visibility");
        visLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        visLabel.setTextFill(Color.web("#c9d1d9"));

        ToggleGroup visGroup = new ToggleGroup();

        // Public option
        HBox publicBox = new HBox(12);
        publicBox.setPadding(new Insets(12));
        publicBox.setAlignment(Pos.CENTER_LEFT);
        publicBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #238636; -fx-border-radius: 8; -fx-background-radius: 8;");
        RadioButton publicBtn = new RadioButton();
        publicBtn.setToggleGroup(visGroup);
        publicBtn.setSelected(true);
        Text publicIcon = new Text("🌐");
        publicIcon.setFont(Font.font("Arial", 20));
        VBox publicInfo = new VBox(2);
        Text publicTitle = new Text("Public");
        publicTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        publicTitle.setFill(Color.web("#c9d1d9"));
        Text publicDesc = new Text("Anyone on the internet can see this repository");
        publicDesc.setFont(Font.font("Arial", 11));
        publicDesc.setFill(Color.web("#8b949e"));
        publicInfo.getChildren().addAll(publicTitle, publicDesc);
        publicBox.getChildren().addAll(publicBtn, publicIcon, publicInfo);

        // Private option
        HBox privateBox = new HBox(12);
        privateBox.setPadding(new Insets(12));
        privateBox.setAlignment(Pos.CENTER_LEFT);
        privateBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");
        RadioButton privateBtn = new RadioButton();
        privateBtn.setToggleGroup(visGroup);
        Text privateIcon = new Text("🔒");
        privateIcon.setFont(Font.font("Arial", 20));
        VBox privateInfo = new VBox(2);
        Text privateTitle = new Text("Private");
        privateTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        privateTitle.setFill(Color.web("#c9d1d9"));
        Text privateDesc = new Text("Only you can see this repository");
        privateDesc.setFont(Font.font("Arial", 11));
        privateDesc.setFill(Color.web("#8b949e"));
        privateInfo.getChildren().addAll(privateTitle, privateDesc);
        privateBox.getChildren().addAll(privateBtn, privateIcon, privateInfo);

        publicBtn.setOnAction(e -> {
            publicBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #238636; -fx-border-radius: 8; -fx-background-radius: 8;");
            privateBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");
        });
        privateBtn.setOnAction(e -> {
            privateBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #58a6ff; -fx-border-radius: 8; -fx-background-radius: 8;");
            publicBox.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-radius: 8; -fx-background-radius: 8;");
        });

        Separator sep3 = new Separator();
        sep3.setStyle("-fx-background-color: #30363d;");

        // README checkbox
        CheckBox readmeCheck = new CheckBox("  Initialize this repository with a README");
        readmeCheck.setSelected(true);
        readmeCheck.setStyle("-fx-text-fill: #c9d1d9; -fx-font-size: 13;");

        Text readmeDesc = new Text("This will let you immediately clone the repository to your computer.");
        readmeDesc.setFont(Font.font("Arial", 12));
        readmeDesc.setFill(Color.web("#8b949e"));

        Separator sep4 = new Separator();
        sep4.setStyle("-fx-background-color: #30363d;");

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.web("#f85149"));
        errorLabel.setFont(Font.font("Arial", 13));

        // Create button
        Button createBtn = new Button("Create repository");
        createBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10 20;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """);
        createBtn.setOnMouseEntered(e -> createBtn.setStyle("""
                -fx-background-color: #2ea043;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10 20;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """));
        createBtn.setOnMouseExited(e -> createBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10 20;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                """));

        createBtn.setOnAction(e -> {
            String name = repoNameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("❌ Repository name is required!");
                return;
            }
            boolean isPublic = publicBtn.isSelected();
            Repo newRepo = new Repo(name, currentUser.getUsername(), descField.getText().trim(), isPublic);
            boolean saved = repoStorage.createRepo(newRepo);
            if (saved) {
                DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, new RepoStorage(), commitStorage, issueStorage, activityTracker);                stage.setScene(db.getScene());
                stage.setMaximized(true);
            } else {
                errorLabel.setText("❌ Repository name already exists!");
            }
        });

        form.getChildren().addAll(
                formTitle, formSubtitle, sep1,
                ownerRow, nameMsg, descBox, sep2,
                visLabel, publicBox, privateBox, sep3,
                readmeCheck, readmeDesc, sep4,
                errorLabel, createBtn
        );

        // ── ACTIONS ──
        backBtn.setOnAction(e -> {
            DashboardScreen db = new DashboardScreen(stage, currentUser, userStorage, new RepoStorage(), commitStorage, issueStorage, activityTracker);            stage.setScene(db.getScene());
            stage.setMaximized(true);
        });

        // ── ROOT ──
        ScrollPane scrollPane = new ScrollPane(centeredForm);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #0d1117; -fx-background: #0d1117;");

        VBox root = new VBox(navbar, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
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
}