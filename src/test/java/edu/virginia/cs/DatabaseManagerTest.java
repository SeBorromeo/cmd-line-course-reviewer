package edu.virginia.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabaseManagerTest {

    private DatabaseManager manager;

    @BeforeEach
    public void setup() throws SQLException {
         manager = new DatabaseManager();
         manager.connect();
         manager.deleteTables();
         manager.createTables();
    }

    @Test
    public void testGetReviewByID() throws SQLException {
        Review one = new Review("yes", 4);
        Review two = new Review("it was okay", 3);
        Student s1 = new Student("patrick", "no");
        Student s2 = new Student("rithwik", "yes");
        Course c1 = new Course("CS", 3140);
        ArrayList<Student> students = new ArrayList<Student>();
        ArrayList<Course> courses = new ArrayList<Course>();
        students.add(s1);
        students.add(s2);
        courses.add(c1);
        manager.addStudents(students);
        manager.addCourses(courses);
        manager.addReview("patrick", "CS", 3140, one);
        manager.addReview("rithwik", "CS", 3140, two);

        ArrayList<Review> testReview = (ArrayList<Review>) manager.getReviewsByStudentID(1);
        assertEquals("yes", testReview.get(0).getText());
    }

}
