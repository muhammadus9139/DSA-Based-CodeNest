package ui;

import data.ActivityTracker;
import data.CommitStorage;
import data.IssueStorage;
import data.RepoStorage;
import data.UserStorage;
import models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class LoginScreen {

    private Stage stage;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public LoginScreen(Stage stage, UserStorage userStorage, RepoStorage repoStorage,
                       CommitStorage commitStorage, IssueStorage issueStorage, ActivityTracker activityTracker) {
        this.stage = stage;
        this.userStorage = userStorage;
        this.repoStorage = repoStorage;
        this.commitStorage = commitStorage;
        this.issueStorage = issueStorage;
        this.activityTracker = activityTracker;
    }

    public Scene getScene() {

        // ── LEFT SIDE — Branding ──
        VBox leftPanel = new VBox(24);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(64));
        leftPanel.setStyle("-fx-background-color: #0d1117;");
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Logo
        Text logo = new Text("CodeNest");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        logo.setFill(Color.WHITE);

        Text tagline = new Text("Where code lives.");
        tagline.setFont(Font.font("Arial", 22));
        tagline.setFill(Color.web("#8b949e"));

        // Features list
        VBox features = new VBox(16);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setMaxWidth(320);

        String[][] featureList = {
                {"📁", "Create and manage repositories"},
                {"💡", "Smart Copilot suggestions"},
                {"🔍", "Trie-powered search"},
                {"📊", "Activity tracking"},
                {"🌍", "Explore public projects"},
                {"🐛", "Built-in issue tracker"},
        };

        for (String[] f : featureList) {
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);
            Text icon = new Text(f[0]);
            icon.setFont(Font.font("Arial", 18));
            Text desc = new Text(f[1]);
            desc.setFont(Font.font("Arial", 14));
            desc.setFill(Color.web("#c9d1d9"));
            item.getChildren().addAll(icon, desc);
            features.getChildren().add(item);
        }

        leftPanel.getChildren().addAll(logo, tagline, features);

        // ── RIGHT SIDE — Login Form ──
        VBox rightPanel = new VBox(0);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(48, 64, 48, 64));
        rightPanel.setPrefWidth(520);
        rightPanel.setMinWidth(520);
        rightPanel.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 0 1;");

        // Form card
        VBox formCard = new VBox(16);
        formCard.setPadding(new Insets(24));
        formCard.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Text formTitle = new Text("Sign in to CodeNest");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        formTitle.setFill(Color.WHITE);

        // Username
        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        usernameLabel.setTextFill(Color.web("#c9d1d9"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setStyle("""
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 10 12;
                -fx-font-size: 14;
                """);

        // Password
        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        passwordLabel.setTextFill(Color.web("#c9d1d9"));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("""
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 10 12;
                -fx-font-size: 14;
                """);

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Arial", 13));
        errorLabel.setTextFill(Color.web("#f85149"));
        errorLabel.setWrapText(true);

        // Login button
        Button loginBtn = new Button("Sign in");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                -fx-font-weight: bold;
                """);
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("""
                -fx-background-color: #2ea043;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                -fx-font-weight: bold;
                """));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                -fx-font-weight: bold;
                """));

        formCard.getChildren().addAll(
                formTitle, usernameLabel, usernameField,
                passwordLabel, passwordField,
                errorLabel, loginBtn
        );

        // Signup box
        HBox signupBox = new HBox(6);
        signupBox.setAlignment(Pos.CENTER);
        signupBox.setPadding(new Insets(16));
        signupBox.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Label signupLabel = new Label("New to CodeNest?");
        signupLabel.setTextFill(Color.web("#8b949e"));
        signupLabel.setFont(Font.font("Arial", 13));

        Hyperlink signupLink = new Hyperlink("Create an account");
        signupLink.setTextFill(Color.web("#58a6ff"));
        signupLink.setFont(Font.font("Arial", 13));
        signupLink.setStyle("-fx-border-color: transparent;");

        signupBox.getChildren().addAll(signupLabel, signupLink);

        rightPanel.getChildren().addAll(formCard, new Region() {{ setPrefHeight(16); }}, signupBox);

        // ── ACTIONS ──
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠️ Please fill all fields!");
                return;
            }

            User user = userStorage.loginUser(username, password);
            if (user != null) {
                DashboardScreen dashboard = new DashboardScreen(stage, user, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                stage.setScene(dashboard.getScene());
                stage.setMaximized(true);  // YE ADD KARO
            } else {
                errorLabel.setText("❌ Incorrect username or password.");
            }
        });

        // Enter key
        passwordField.setOnAction(e -> loginBtn.fire());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        signupLink.setOnAction(e -> {
            SignupScreen signup = new SignupScreen(stage, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(signup.getScene());
            stage.setMaximized(true);
        });

        // ── ROOT ──
        HBox root = new HBox(leftPanel, rightPanel);
        root.setStyle("-fx-background-color: #0d1117;");
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

//        return new Scene(root);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMaximized(true);
        return scene;
    }
}