package edu.virginia.cs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class CommandLineCourseReviewer {

    public static void main(String[] args) {
        CommandLineCourseReviewer courseReviewer = new CommandLineCourseReviewer();
        courseReviewer.run();
    }

    private Scanner scanner;
    private CourseReviewerImpl courseReviewer;
    private boolean running = true;
    public void run() {
        courseReviewer = new CourseReviewerImpl();
        try {
            courseReviewer.checkDatabase();
        } catch (IllegalStateException e) {
            System.out.println("Reviews.sqlite3 doesn't exist/is empty or table missing. Creating new file");
        }

        initializeScanner();

        System.out.println("Welcome to the Course Reviewer! \n");

        while (running) {
            loginOrCreate();
            mainMenu();
        }
    }

    private void initializeScanner() {
        scanner = new Scanner(System.in);
    }

    private void mainMenu() {
        while(courseReviewer.inMainMenu()) {
            System.out.println("\n======================MAIN MENU======================");
            System.out.println("Options (Type character enclosed in parenthesis):");
            System.out.println("(1) - Submit a review for a course");
            System.out.println("(2) - See the reviews for a course");
            System.out.println("(3) - Logout");
            System.out.println("(4) - Exit.");

            String userResponse = prompt("");
            if(userResponse.equals("1"))
                submitReview();
            else if(userResponse.equals("2"))
                seeReviews();

            else if(userResponse.equals("3")) {
                courseReviewer.logout();
            }
            else if(userResponse.equals("4"))
                quit();
            else {
                System.out.println("Invalid Input. Please try again.");
            }
        }
    }

    private void submitReview() {
        String[] courseName = inputCourseName();

        if(isInvalidCourseName(courseName))
            System.out.println("Invalid Course Name. Must follow format like APMA 3100");
        else {
            String department = courseName[0];
            int catalogNum = Integer.parseInt(courseName[1]);

            if (!courseReviewer.courseInDepartment(department, catalogNum)) {
                courseReviewer.addNewCourse(department, catalogNum);
                addReview(department, catalogNum);
            }
            else if(courseReviewer.hasReviewedCourse(department, catalogNum))
                System.out.println("You've already written a review for this course!");
            else
                addReview(department, catalogNum);
        }
    }

    private boolean isInvalidCourseName(String[] courseName) {
        return courseName.length < 2 || !courseReviewer.isValidCourse(courseName[0], courseName[1]);
    }

    private void addReview(String department, int catalogNum) {
        String message = prompt("Enter Message: ");
        int rating = promptRating();

        Review review = new Review(message, rating, courseReviewer.getCurrentUser());
        courseReviewer.addReview(department, catalogNum, review);
        System.out.println("");
        System.out.println("Review successfully submitted! ");
//        System.out.println("");
    }


    private String[] inputCourseName() {
        String userResponse = prompt("Enter Course Name (ex. CS 3140): ");
        return userResponse.split(" ");
    }

    private int promptRating() {
        int rating = -1;
        do {
            try {
                System.out.println("Enter Rating (1-5):");
                rating = Integer.parseInt(scanner.nextLine());
            }
            catch(NumberFormatException e) {
                System.out.println("Non-integer entered. Try again.");
            }
        } while(rating < 1 || rating > 5);
        return rating;
    }

    public void seeReviews() {
        String[] courseName = inputCourseName();

        if(isInvalidCourseName(courseName))
            System.out.println("Invalid Course Name. Must follow format like APMA 3100");
        else {
            String department = courseName[0];
            int catalogNum = Integer.parseInt(courseName[1]);

            ArrayList<Review> reviewList = courseReviewer.getCourseReviews(department, catalogNum);
            if(reviewList.size() < 1)
                System.out.println("No reviews yet for course " + department + " " + catalogNum);
            else
                printReviews(reviewList, department, catalogNum);
        }
    }

    private void printReviews(ArrayList<Review> reviewList, String department, int catalogNum) {
        System.out.println("\nCourse entered: " + department + " " + catalogNum + "\n");
        double averageRating = courseReviewer.averageRating(department, catalogNum);
        printEveryReview(reviewList, averageRating);
    }

    private void printEveryReview(ArrayList<Review> reviewList, double averageRating) {
        System.out.println("Review Messages:");
        for (int i = 0; i < reviewList.size(); i++) {
            System.out.println((i + 1) + ": " +
                    reviewList.get(i).getText());
        }
        System.out.print("\nCourse Average: " + averageRating + "/5 \n");
    }

    private void quit() {
        System.exit(0);
    }

    private void loginOrCreate() {
        while(courseReviewer.inLoginScreen()) {
            String userResponse = prompt("Would you like to login or create a new user? (L - login, C - create new user): ");
            if(userResponse.equalsIgnoreCase("L"))
                login();
            else if(userResponse.equalsIgnoreCase("C"))
                createUser();
            else {
                System.out.println("Invalid Input. Please try again.");
            }
        }
    }

    private void login() {
        String name = prompt("Enter Name: ");
        String password = prompt("Enter Password: ");

        if(courseReviewer.validLogin(name, password)) {
            courseReviewer.setCurrentState(CourseReviewerImpl.state.MAIN_MENU);
            courseReviewer.setCurrentLogin(name, password);
        }
        else {
            System.out.println("Could not find matching login");
            loginOrCreate();
        }
    }

    private void createUser() {
        String name = prompt("Enter Name: ");
        String password = prompt("Enter Password: ");
        String confirmPassword = prompt("Confirm Password: ");

        if(password.equals(confirmPassword)) {
            try {
                courseReviewer.addLogin(name, password);
                courseReviewer.setCurrentLogin(name, password);
                courseReviewer.setCurrentState(CourseReviewerImpl.state.MAIN_MENU);
            }
            catch (IllegalArgumentException e) {
                System.out.println("User with name " + name + " already exists");
            }
            catch (IllegalStateException e) {
//                System.out.println("Students Table does not exist.");
                throw new RuntimeException(e);
            }
        }
        else {
            System.out.println("Passwords did not match");
            loginOrCreate();
        }
    }

    private String prompt(String s) {
        System.out.print(s);
        return scanner.nextLine();
    }
}
