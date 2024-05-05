package edu.virginia.cs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseReviewerImpl {
    private DatabaseManager databaseManager = new DatabaseManager();
    private String user;
    private String password;

    enum state { LOGIN, MAIN_MENU }

    private state currentState = state.LOGIN;

    public CourseReviewerImpl() {
        databaseManager.connect();
    }

    public void checkDatabase() throws IllegalStateException {
        databaseManager.checkIfDatabaseExists();
    }

    public void setCurrentState(state newState) { currentState = newState; }
    public boolean inLoginScreen() { return currentState == state.LOGIN; }

    public boolean inMainMenu() { return currentState == state.MAIN_MENU; }


    public void setCurrentLogin(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getCurrentUser() {
        return user;
    }

    public void addNewCourse(String department, int catalogNumber) {
        Course course = new Course(department, catalogNumber);
        ArrayList<Course> list = new ArrayList<>(List.of(course));

        databaseManager.addCourses(list);
    }

    public boolean validLogin(String studentName, String password) {
        try {
            Student student = databaseManager.getStudentByName(studentName);
            return student.getPassword().equals(password);
        }
        catch(IllegalArgumentException | IllegalStateException ignore) { }
        return false;
    }

    public boolean isValidCourse(String department, String catalogNumber) {
        return isValidDepartment(department) && isValidCatalogNumber(catalogNumber);
    }

    public boolean isValidDepartment(String department) {
        if(department.length() > 4)
            return false;

        char[] chars = department.toCharArray();
        for(char c: chars) {
            if(!Character.isLetter(c) || !Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidCatalogNumber(String catalogNumber) {
        if(catalogNumber.length() != 4)
            return false;

        char[] chars = catalogNumber.toCharArray();
        for(char c: chars) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasReviewedCourse(String department, int catalogNumber) {
        int courseID = databaseManager.getCourseID(department, catalogNumber);

        ArrayList<Review> reviews = (ArrayList<Review>) databaseManager.getReviewsByCourseID(courseID);
        for(Review review: reviews) {
            if(review.getStudentName().equals(user))
                return true;
        }
        return false;
    }

    public boolean courseInDepartment(String department, int catalogNumber) {
        try {
            ArrayList<Course> coursesInDepartment = (ArrayList<Course>) databaseManager.getCoursesByDepartment(department);
            for (Course course : coursesInDepartment) {
                if (course.getCatalogNumber() == catalogNumber)
                    return true;
            }
        }
        catch (RuntimeException e) { e.printStackTrace();}
        return false;
    }

    public void addLogin(String userName, String password) throws IllegalArgumentException {
        Student student = new Student(userName, password);
        ArrayList<Student> list = new ArrayList<>(List.of(student));

        databaseManager.addStudents(list);
    }

    public void addReview(String department, int catalogNum, Review review) {
        databaseManager.addReview(user, department, catalogNum, review);
    }

    public void logout() {
        currentState = state.LOGIN;
    }

    public double averageRating(String department, int catalogNumber) {
        double sum = 0;

        int courseID = databaseManager.getCourseID(department, catalogNumber);

        ArrayList<Review> reviews = (ArrayList<Review>) databaseManager.getReviewsByCourseID(courseID);
        for(Review review: reviews) {
            sum += review.getRating();
        }
        return sum/reviews.size();
    }

    public ArrayList<Review> getCourseReviews(String department, int catalogNumber) {
        ArrayList<Review> reviews;
        try {
            int courseID = databaseManager.getCourseID(department, catalogNumber);
            reviews = (ArrayList<Review>) databaseManager.getReviewsByCourseID(courseID);
        }
        catch (IllegalArgumentException e) {
            reviews = new ArrayList<>();
        }

        return reviews;
    }
}
