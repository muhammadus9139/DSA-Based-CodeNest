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

public class SignupScreen {

    private Stage stage;
    private UserStorage userStorage;
    private RepoStorage repoStorage;
    private CommitStorage commitStorage;
    private IssueStorage issueStorage;
    private ActivityTracker activityTracker;

    public SignupScreen(Stage stage, UserStorage userStorage, RepoStorage repoStorage,
                        CommitStorage commitStorage, IssueStorage issueStorage, ActivityTracker activityTracker) {
        this.stage = stage;
        this.userStorage = userStorage;
        this.repoStorage = repoStorage;
        this.commitStorage = commitStorage;
        this.issueStorage = issueStorage;
        this.activityTracker = activityTracker;
    }

    public Scene getScene() {

        // ── LEFT SIDE ──
        VBox leftPanel = new VBox(24);
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(64, 64, 64, 100));
        leftPanel.setStyle("-fx-background-color: #0d1117;");
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        Text logo = new Text("CodeNest");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        logo.setFill(Color.WHITE);

        Text tagline = new Text("Join millions of developers.");
        tagline.setFont(Font.font("Arial", 22));
        tagline.setFill(Color.web("#8b949e"));

        VBox features = new VBox(16);
        features.setAlignment(Pos.CENTER_LEFT);
        features.setMaxWidth(320);

        String[][] featureList = {
                {"🚀", "Build and ship software"},
                {"🤝", "Collaborate with others"},
                {"📦", "Store all your code"},
                {"🔒", "Keep your code private"},
                {"💡", "Get smart suggestions"},
                {"🌍", "Explore open source"},
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

        // ── RIGHT SIDE ──
        VBox rightPanel = new VBox(0);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(32, 48, 32, 48));
        rightPanel.setPrefWidth(520);
        rightPanel.setMinWidth(520);
        rightPanel.setStyle("-fx-background-color: #0d1117; -fx-border-color: #30363d; -fx-border-width: 0 0 0 1;");
        VBox.setMargin(rightPanel, new Insets(0, 40, 0, 0));

        VBox formCard = new VBox(12);
        formCard.setPadding(new Insets(24));
        formCard.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Text formTitle = new Text("Create your account");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        formTitle.setFill(Color.WHITE);

        TextField fullNameField = styledField("Full Name");
        TextField usernameField = styledField("Username");
        TextField universityField = styledField("University");
        TextField bioField = styledField("Bio (e.g. CS Student)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle(fieldStyle());

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        confirmField.setStyle(fieldStyle());

        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Arial", 12));
        errorLabel.setTextFill(Color.web("#f85149"));
        errorLabel.setWrapText(true);

        Button signupBtn = new Button("Create account");
        signupBtn.setMaxWidth(Double.MAX_VALUE);
        signupBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                -fx-font-weight: bold;
                """);
        signupBtn.setOnMouseEntered(e -> signupBtn.setStyle("""
                -fx-background-color: #2ea043;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                -fx-font-weight: bold;
                """));
        signupBtn.setOnMouseExited(e -> signupBtn.setStyle("""
                -fx-background-color: #238636;
                -fx-text-fill: white;
                -fx-font-size: 14;
                -fx-padding: 10;
                -fx-background-radius: 6;
                -fx-cursor: hand;
                -fx-font-weight: bold;
                """));

        formCard.getChildren().addAll(
                formTitle,
                fieldLabel("Full Name *"), fullNameField,
                fieldLabel("Username *"), usernameField,
                fieldLabel("University"), universityField,
                fieldLabel("Bio"), bioField,
                fieldLabel("Password *"), passwordField,
                fieldLabel("Confirm Password *"), confirmField,
                errorLabel, signupBtn
        );

        HBox loginBox = new HBox(6);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(16));
        loginBox.setStyle("""
                -fx-background-color: #161b22;
                -fx-border-color: #30363d;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);

        Label loginLabel = new Label("Already have an account?");
        loginLabel.setTextFill(Color.web("#8b949e"));
        loginLabel.setFont(Font.font("Arial", 13));

        Hyperlink loginLink = new Hyperlink("Sign in");
        loginLink.setTextFill(Color.web("#58a6ff"));
        loginLink.setFont(Font.font("Arial", 13));
        loginLink.setStyle("-fx-border-color: transparent;");

        loginBox.getChildren().addAll(loginLabel, loginLink);

        ScrollPane formScroll = new ScrollPane(formCard);
        formScroll.setFitToWidth(true);
        formScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        formScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        formScroll.setPadding(new Insets(0, 20, 0, 0));

        Region gap = new Region();
        gap.setPrefHeight(16);
        rightPanel.getChildren().addAll(formScroll, gap, loginBox);

        // ── ACTIONS ──
        signupBtn.setOnAction(e -> {
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String university = universityField.getText().trim();
            String bio = bioField.getText().trim();
            String password = passwordField.getText().trim();
            String confirm = confirmField.getText().trim();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠️ Please fill all required fields!");
                return;
            }
            if (!password.equals(confirm)) {
                errorLabel.setText("❌ Passwords do not match!");
                return;
            }

            User newUser = new User(fullName, username, password, bio, university);
            boolean saved = userStorage.saveUser(newUser);
            if (saved) {
                LoginScreen login = new LoginScreen(stage, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
                stage.setScene(login.getScene());
                stage.setMaximized(true);
            } else {
                errorLabel.setText("❌ Username already taken!");
            }
        });

        loginLink.setOnAction(e -> {
            LoginScreen login = new LoginScreen(stage, userStorage, repoStorage, commitStorage, issueStorage, activityTracker);
            stage.setScene(login.getScene());
            stage.setMaximized(true);  // YE ADD KARO
        });

        // ── ROOT ──
        HBox root = new HBox(leftPanel, rightPanel);
        root.setStyle("-fx-background-color: #0d1117;");
        HBox.setHgrow(leftPanel, Priority.ALWAYS);


//        return new Scene(root);

//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setMaximized(true);
//        return scene;

        Scene scene = new Scene(root);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(javafx.stage.Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(javafx.stage.Screen.getPrimary().getBounds().getHeight());
        stage.setMaximized(true);
        return scene;

    }

    private TextField styledField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(fieldStyle());
        return field;
    }

    private String fieldStyle() {
        return """
                -fx-background-color: #0d1117;
                -fx-text-fill: #c9d1d9;
                -fx-prompt-text-fill: #6e7681;
                -fx-border-color: #30363d;
                -fx-border-radius: 6;
                -fx-background-radius: 6;
                -fx-padding: 10 12;
                -fx-font-size: 13;
                """;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setTextFill(Color.web("#c9d1d9"));
        return label;
    }
}