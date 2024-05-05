package edu.virginia.cs;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements DatabaseManagerInterface {

    private final String databaseURL = "jdbc:sqlite:../../Reviews.sqlite3";
    private Connection connection;

    @Override
    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new IllegalStateException("Manager is already connected.");
        }
    }

    @Override
    public void createTables() {
        checkConnection();
        try {
            String sql1 = "CREATE TABLE Students"
                    + "("
                    + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " Name VARCHAR(255) NOT NULL UNIQUE,"
                    + " Password VARCHAR(255) NOT NULL"
                    + ")";
            String sql2 = "CREATE TABLE Courses"
                    + "("
                    + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " Department VARCHAR(4) NOT NULL,"
                    + " Catalog_Number INTEGER(4) NOT NULL"
                    + ")";
            String sql3 = "CREATE TABLE Reviews"
                    + "("
                    + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " StudentID INT(255) NOT NULL,"
                    + " CourseID INT(255) NOT NULL,"
                    + " Text VARCHAR(65000) NOT NULL,"
                    //TODO: check that Rating is an integer
                    + " Rating INT(2) NOT NULL,"
                    + " foreign key (StudentID) references Students(ID) ON DELETE CASCADE,"
                    + " foreign key (CourseID) references Courses(ID) ON DELETE CASCADE"
                    + ")";
            Statement stmt = connection.createStatement();
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
        } catch (SQLException e) {
            throw new IllegalStateException("Tables already exist.");
        }
    }

    @Override
    public void clear() {
        checkConnection();
        try {
            String deleteQuery1 = "DELETE FROM Students";
            String deleteQuery2 = "DELETE FROM Courses";
            String deleteQuery3 = "DELETE FROM Reviews";

            Statement statement = connection.createStatement();
            statement.execute(deleteQuery1);
            statement.execute(deleteQuery2);
            statement.execute(deleteQuery3);
        }
        catch (SQLException e) {
            throw new IllegalStateException("Tables don't exists");
        }
    }

    @Override
    public void deleteTables() {
        checkConnection();

        deleteStudentsTable();
        deleteCoursesTable();
        deleteReviewsTable();
    }

    private void deleteStudentsTable() {
        try {
            String query = "DROP TABLE Students";

            Statement statement = connection.createStatement();
            statement.execute(query);
        }
        catch (SQLException ignored) { }
    }

    private void deleteCoursesTable() {
        try {
            String query = "DROP TABLE Courses";

            Statement statement = connection.createStatement();
            statement.execute(query);
        }
        catch (SQLException ignored) { }
    }

    private void deleteReviewsTable() {
        try {
            String query = "DROP TABLE Reviews";

            Statement statement = connection.createStatement();
            statement.execute(query);
        }
        catch (SQLException ignored) { }
    }

    @Override
    public void addStudents(List<Student> studentList) {
        try {
            checkConnection();

            ArrayList<Student> databaseStudentList = (ArrayList<Student>) getAllStudents();
            for(Student s : studentList) {
                if(!isStudentInList(s, databaseStudentList)) {
                    String studentInsertQuery = generateStudentInsertQuery(s);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(studentInsertQuery);
                }
                else { throw new IllegalArgumentException("Attempted to add Student that is already in database."); }
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("Tables don't exist.");
        }
    }

    @Override
    public List<Student> getAllStudents() throws SQLException {
        checkConnection();
        checkStudentsTableExists();
        try {
            ArrayList<Student> studentList = new ArrayList<Student>();

            String query = "SELECT * from Students";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            studentList = (ArrayList<Student>) createStudentListFromResultSet(rs);
            return studentList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Student getStudentByName(String name) {
        try {
            checkConnection();
            checkStudentsExistsOrEmpty();

            String selectQuery = String.format("""
                    select *
                        from Students
                        where Name = "%s"
                    """, name);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);
            boolean nextRow = rs.next();

            if(!nextRow) { throw new IllegalArgumentException("ERROR: Student " + name + " not found."); }

            return new Student(name, rs.getString("Password"));
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Student getStudentByID(int id) {
        try {
            checkConnection();
            checkStudentsExistsOrEmpty();

            String selectQuery = String.format("""
                    select *
                        from Students
                        where ID = %d
                    """, id);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);
            boolean nextRow = rs.next();

            if(!nextRow) { throw new IllegalArgumentException("ERROR: Student with" + id + " not found."); }

            return new Student(rs.getString("Name"), rs.getString("Password"));
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void addCourses(List<Course> courseList) {
        try {
            checkConnection();
            checkCoursesTableExists();

            ArrayList<Course> databaseCourseList = (ArrayList<Course>) getAllCourses();
            for(Course course : courseList) {
                if(!isCourseInList(course, databaseCourseList)) {
                    String courseInsertQuery = generateCourseInsertQuery(course);
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(courseInsertQuery);
                }
                else { throw new IllegalArgumentException("Attempted to add Course that is already in database."); }
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("Tables don't exist.");
        }
    }

    @Override
    public List<Course> getAllCourses() throws SQLException {
        checkConnection();
        checkCoursesTableExists();
        try {
            ArrayList<Course> courseList = new ArrayList<Course>();

            String query = "SELECT * from Courses";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            courseList = (ArrayList<Course>) createCourseListFromResultSet(rs);
            return courseList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Course getCourse(String department, int catalogNumber) {
        try {
            checkConnection();
            checkCoursesTableExists();

            String selectQuery = String.format("""
                SELECT *
                    from Courses
                    where Department = "%s"
                    and Catalog_Number = %d
                """, department, catalogNumber);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);

            ArrayList<Course> courseList = (ArrayList<Course>) createCourseListFromResultSet(rs);
            return courseList.get(0);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> getCoursesByDepartment(String department) {
        try {
            checkConnection();
            checkCoursesTableExists();

            ArrayList<Course> courseList;

            String query = String.format("""
                SELECT *
                    from Courses
                    where Department = "%s"
                """, department);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            courseList = (ArrayList<Course>) createCourseListFromResultSet(rs);
            return courseList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Can have many courses with same catalog number not just one
    @Override
    public Course getCourseByCatalogNumber(int catalog_num) throws SQLException {
        checkConnection();
        checkCoursesExistsOrEmpty();
        try {
            String selectQuery = String.format("""
                    select *
                        from Courses
                        where Catalog_Number = %d
                    """, catalog_num);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);
            boolean nextRow = rs.next();

            if(!nextRow) { throw new IllegalArgumentException("ERROR: Course " + catalog_num + " not found."); }

            return new Course(rs.getString("Department"), catalog_num);
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    //TODO: check if Review is already in database
    private void addReviewToDatabase(int studentID, int courseID, Review review) throws SQLException {
        try {
            //if(!isReviewInDatabase)
            String reviewInsertQuery = generateReviewInsertQuery(studentID, courseID, review);
            Statement statement = connection.createStatement();
            statement.executeUpdate(reviewInsertQuery);
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
    @Override
    public void addReview(String studentName, String department, int catalogNum, Review review) {
        try {
            checkConnection();
            checkStudentsExistsOrEmpty();
            checkCoursesExistsOrEmpty();
            checkReviewsTableExists();

            int studentID = getStudentID(studentName);
            int courseID = getCourseID(department, catalogNum);

            addReviewToDatabase(studentID, courseID, review);
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<Review> getAllReviews() throws SQLException {
        checkConnection();
        checkReviewsExistsOrEmpty();
        try {
            ArrayList<Review> reviewsList;

            String query = "SELECT * from Reviews";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            reviewsList = (ArrayList<Review>) createReviewListFromResultSet(rs);
            return reviewsList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Review> getReviewsByStudentID(int studentID) {
        try {
            checkConnection();
            checkAllTablesExistsOrEmpty();

            ArrayList<Review> reviewsList;

            String reviewQuery = " SELECT * from Reviews where StudentID = " + studentID;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(reviewQuery);

            reviewsList = (ArrayList<Review>) createReviewListFromResultSet(rs);
            return reviewsList;
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Review> getReviewsByCourseID(int courseID) {
        try {
            checkConnection();
            checkAllTablesExistsOrEmpty();

            ArrayList<Review> reviewsList;

            String reviewQuery = " SELECT * from Reviews where CourseID = " + courseID;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(reviewQuery);

            reviewsList = (ArrayList<Review>) createReviewListFromResultSet(rs);
            return reviewsList;
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Review getReviewByIDs(int studentID, int courseID) throws SQLException {
        checkConnection();
        checkAllTablesExistsOrEmpty();
        try {
            String reviewQuery = "SELECT * from Reviews";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(reviewQuery);

            while(rs.next()) {
                if(rs.getInt("StudentID") == studentID && rs.getInt("CourseID") == courseID) {
                    return new Review(rs.getString("Text"), rs.getInt("Rating"), "studentID");
                }
            }
            throw new IllegalArgumentException("Review by Student " + studentID + " for Course "
                    + courseID + " not found.");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void disconnect() throws SQLException {
        if(connection.isClosed()) {
            throw new IllegalStateException("Manager has not connected yet.");
        }
        connection.commit();
        connection.close();
    }

    private String generateStudentInsertQuery(Student student) {
        return String.format("""
                insert into Students(Name, Password)
                    values ("%s", "%s");
                """, student.getName(), student.getPassword());
    }

    private String generateCourseInsertQuery(Course course) {
        return String.format("""
                insert into Courses(Department, Catalog_Number)
                    values ("%s", %d);
                """, course.getDepartment(), course.getCatalogNumber());
    }

    private String generateReviewInsertQuery(int studentID, int courseID, Review review) {
        return String.format("""
                insert into Reviews(StudentID, CourseID, Text, Rating)
                    values (%d, %d, "%s", %d);
                """,studentID, courseID, review.getText(), review.getRating());
    }

    private List<Student> createStudentListFromResultSet(ResultSet rs) throws SQLException{
        ArrayList<Student> studentList = new ArrayList<Student>();
        while(rs.next()) {
            Student tempStudent = new Student(rs.getString("Name"),
                    rs.getString("Password"));
            studentList.add(tempStudent);
        }
        return studentList;
    }

    private List<Course> createCourseListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Course> courseList = new ArrayList<Course>();
        while(rs.next()) {
            Course tempCourse = new Course(rs.getString("Department"),
                    rs.getInt("Catalog_Number"));

//            ArrayList<Review> reviews = (ArrayList<Review>) getReviewsByCourseID(rs.getInt("ID"));
//            for(Review review: reviews)
//                tempCourse.addReview(review);

            courseList.add(tempCourse);
        }
        return courseList;
    }

    private List<Review> createReviewListFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Review> reviewsList = new ArrayList<Review>();
        while(rs.next()) {
            Student student = getStudentByID(rs.getInt("StudentID"));
            Review tempReview = new Review(rs.getString("Text"), rs.getInt("Rating"), student.getName());
            reviewsList.add(tempReview);
        }
        return reviewsList;
    }

    private void checkStudentsTableExists() {
        try {
            String query = "SELECT * from Students";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Students Table does not exist.");
        }
    }
    private void checkStudentsExistsOrEmpty() throws SQLException {
        try {
            String query = "SELECT * from Students";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (!rs.next()) {
                throw new IllegalStateException("Students Table is empty.");
            }
        }
        catch(SQLException e) {
            throw new IllegalStateException("Students table doesn't exist");
        }
    }

    private void checkCoursesTableExists() throws SQLException {
        try {
            String query = "SELECT * from Courses";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Courses Table does not exist.");
        }
    }

    private void checkCoursesExistsOrEmpty() throws SQLException {
        try {
            String query = "SELECT * from Courses";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (!rs.next()) {
                throw new IllegalStateException("Courses Table is empty.");
            }
        }
        catch(SQLException e) {
            throw new IllegalStateException("Courses table doesn't exist");
        }
    }

    private void checkReviewsTableExists() {
        try {
            String query = "SELECT * from Reviews";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Reviews Table does not exist.");
        }
    }

    private void checkReviewsExistsOrEmpty() {
        try {
            String query = "SELECT * from Reviews";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (!rs.next()) {
                throw new IllegalStateException("Reviews Table is empty.");
            }
        }
        catch(SQLException e) {
            throw new IllegalStateException("Reviews table doesn't exist");
        }
    }

    private void checkAllTablesExistsOrEmpty() throws SQLException {
        checkStudentsExistsOrEmpty();
        checkCoursesExistsOrEmpty();
        checkReviewsExistsOrEmpty();
    }

    private boolean checkAllTablesExist() {
        try {
            checkStudentsTableExists();
            checkCoursesTableExists();
            checkReviewsTableExists();
            return true;
        }
        catch(IllegalStateException | SQLException ignored) {
            return false;
        }
    }

    //This works because Name field is UNIQUE
    private boolean isStudentInList(Student student, List<Student> studentList) {
        for(Student s : studentList) {
            if(s.getName().equals(student.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isCourseInList(Course course, List<Course> courseList) {
        for(Course c : courseList) {
            if(c.getDepartment().equals(course.getDepartment()) &&
            c.getCatalogNumber() == course.getCatalogNumber()) {
                return true;
            }
        }
        return false;
    }

//    private boolean isReviewInDatabase(int studentID, int courseID) throws SQLException {
//        String query = "SELECT * from Students, Courses, Reviews";
//        Statement statement = connection.createStatement();
//        ResultSet rs = statement.executeQuery(query);
//
//        while(rs.next()) {
//            if(rs.get)
//        }
//    }

    private void checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("Manager has not connected yet.");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkIfDatabaseExists() {
        if(!checkAllTablesExist()) {
            deleteTables();
            createTables();
            throw new IllegalStateException();
        }
    }
    public int getStudentID(String name) throws SQLException {
        String selectQuery = String.format("""
                    select *
                        from Students
                        where Name = "%s"
                    """, name);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(selectQuery);
        boolean nextRow = rs.next();
        if(!nextRow) { throw new IllegalArgumentException("ERROR: Student " + name + " not found."); }
        return rs.getInt("ID");
    }
    public int getCourseID(String department, int catalogNum) {
        int courseID = 0;
        try {
            String selectQuery = "SELECT * from Courses";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(selectQuery);

            while (rs.next()) {
                if (rs.getString("Department").equals(department) &&
                        rs.getInt("Catalog_Number") == catalogNum) {
                    courseID = rs.getInt("ID");
                }
            }

            if (courseID == 0) {
                throw new IllegalArgumentException("ERROR: Course " + department + " " + catalogNum
                        + " not found.");
            }
        } catch (SQLException e) {}
        return courseID;
    }
//    public static void main (String[] args) {
//        DatabaseManager manager = new DatabaseManager();
//        manager.connect();
////        manager.createTables();
//        manager.clear();
//
//    }
}
