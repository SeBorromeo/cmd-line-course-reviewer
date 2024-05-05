package edu.virginia.cs;

import java.util.ArrayList;

public class Course {

    private String department;
    private int catalogNumber;
    private ArrayList<Review> reviews;

    public Course(String department, int catalogNumber) {
        this.department = department;
        this.catalogNumber = catalogNumber;
        reviews = new ArrayList<>();
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public int getCatalogNumber() { return catalogNumber; }

    public void setCatalogNumber(int catalogNumber) { this.catalogNumber = catalogNumber; }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
