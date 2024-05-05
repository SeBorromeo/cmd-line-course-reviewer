package edu.virginia.cs;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseManagerInterface {
    //@throws IllegalStateException if the Manager is already connected
    void connect() throws SQLException;

    //@throws IllegalStateException if the tables already exist
    //@throws IllegalStateException if the Manager hasn’t connected yet
    void createTables() throws SQLException;

    //@throws IllegalStateException if the tables don't exist.
    //@throws IllegalStateException if the Manager hasn't connected yet

    void clear() throws SQLException;

    //@throws IllegalStateException if the tables don't exist
    //@throws IllegalStateException if the Manager hasn't connected yet
    void deleteTables() throws SQLException;

    //@throws IllegalStateException if Students table doesn't exist
    //@throws IllegalArgumentException if you add a Student that is already
    // in the database.
    //@throws IllegalStateException if the Manager hasn't connected yet
    void addStudents(List<Student> studentList) throws SQLException;

    //@throws IllegalStateException if Students doesn't exist
    //@throws IllegalStateException if the Manager hasn't connected yet
    List<Student> getAllStudents() throws SQLException;

    //@throws IllegalStateException if Students table doesn't exist
    //@throws IllegalArgumentException if no Student with given Name found
    //@throws IllegalStateException if the Manager hasn't connected yet
    Student getStudentByName(String name);

    //@throws IllegalStateException if Courses table doesn't exist
    //@throws IllegalArgumentException if you add a Course that is already
    // in the database.
    //@throws IllegalStateException if the Manager hasn't connected yet
    void addCourses(List<Course> courseList) throws SQLException;

    //@throws IllegalStateException if Courses doesn't exist
    //@throws IllegalStateException if the Manager hasn't connected yet
    List<Course> getAllCourses() throws SQLException;

//    //@throws IllegalStateException if Courses doesn't exist OR is empty
//    //@throws IllegalArgumentException if no Course with that Department is found
//    //@throws IllegalStateException if the Manager hasn't connected yet
//    Course getCourseByDepartment(String department) throws SQLException;

    //@throws IllegalStateException if Courses doesn't exist OR is empty
    //@throws IllegalArgumentException if no Course with that Catalog_Number is found
    //@throws IllegalStateException if the Manager hasn't connected yet
    Course getCourseByCatalogNumber(int catalog_num) throws SQLException;

    //@throws IllegalStateException if Reviews, Students, OR Courses tables do not exist
    //@throws IllegalArgumentException if you add a Review that is already
    // in the database.
    //@throws IllegalStateException if the Manager hasn't connected yet
    void addReview(String studentName, String department, int catalogNum, Review review) throws SQLException;

    //@throws IllegalStateException if Reviews doesn't exist
    //@throws IllegalStateException if the Manager hasn't connected yet
    List<Review> getAllReviews() throws SQLException;

    //@throws IllegalStateException if Reviews doesn't exist OR is empty
    //@throws IllegalArgumentException if no Review with that Catalog_Number is found
    //@throws IllegalStateException if the Manager hasn't connected yet
    Review getReviewByIDs(int studentID, int courseID) throws SQLException;

    //@throws IllegalStateException if the Manager hasn't connected yet
    void disconnect() throws SQLException;

}
