package models;

import java.util.ArrayList;

public class DummyData {

    public static ArrayList<User> getDummyUsers() {
        ArrayList<User> users = new ArrayList<>();

        User u1 = new User("Ali Hassan", "alihassan", "pass123", "Java Developer | DSA Enthusiast", "COMSATS University");
        User u2 = new User("Sara Khan", "sarakhan", "pass123", "Python & ML Developer", "LUMS");
        User u3 = new User("Ahmed Raza", "ahmedraza", "pass123", "Full Stack Web Developer", "FAST NUCES");
        User u4 = new User("Fatima Malik", "fatimamalik", "pass123", "Android Developer | Kotlin", "UET Lahore");
        User u5 = new User("Usman Tariq", "usmantariq", "pass123", "C++ & Competitive Programmer", "NUST");

        users.add(u1);
        users.add(u2);
        users.add(u3);
        users.add(u4);
        users.add(u5);

        return users;
    }

    public static ArrayList<Repo> getDummyRepos() {
        ArrayList<Repo> repos = new ArrayList<>();

        // Ali's repos
        Repo r1 = new Repo("DSA-Practice", "alihassan", "All DSA problems solved in Java", true);
        r1.addFile("BinarySearch.java");
        r1.addFile("LinkedList.java");
        r1.addFile("Stack.java");
        r1.setStars(24);
        r1.setCommits(15);

        Repo r2 = new Repo("Java-OOP-Projects", "alihassan", "OOP projects collection", true);
        r2.addFile("BankSystem.java");
        r2.addFile("LibrarySystem.java");
        r2.setStars(18);
        r2.setCommits(10);

        // Sara's repos
        Repo r3 = new Repo("ML-Algorithms", "sarakhan", "Machine learning algorithms in Python", true);
        r3.addFile("LinearRegression.py");
        r3.addFile("KMeans.py");
        r3.setStars(35);
        r3.setCommits(22);

        Repo r4 = new Repo("Data-Analysis", "sarakhan", "Data analysis scripts", true);
        r4.addFile("analysis.py");
        r4.addFile("visualize.py");
        r4.setStars(12);
        r4.setCommits(8);

        // Ahmed's repos
        Repo r5 = new Repo("Portfolio-Website", "ahmedraza", "My personal portfolio website", true);
        r5.addFile("index.html");
        r5.addFile("style.css");
        r5.addFile("script.js");
        r5.setStars(42);
        r5.setCommits(30);

        Repo r6 = new Repo("React-Todo-App", "ahmedraza", "Todo app built with React", true);
        r6.addFile("App.js");
        r6.addFile("TodoList.js");
        r6.setStars(28);
        r6.setCommits(18);

        // Fatima's repos
        Repo r7 = new Repo("Android-Calculator", "fatimamalik", "Calculator app in Kotlin", true);
        r7.addFile("MainActivity.kt");
        r7.addFile("CalculatorLogic.kt");
        r7.setStars(15);
        r7.setCommits(12);

        // Usman's repos
        Repo r8 = new Repo("Competitive-Programming", "usmantariq", "CP solutions in C++", true);
        r8.addFile("graph.cpp");
        r8.addFile("dp.cpp");
        r8.addFile("sorting.cpp");
        r8.setStars(50);
        r8.setCommits(45);

        repos.add(r1);
        repos.add(r2);
        repos.add(r3);
        repos.add(r4);
        repos.add(r5);
        repos.add(r6);
        repos.add(r7);
        repos.add(r8);

        return repos;
    }
}