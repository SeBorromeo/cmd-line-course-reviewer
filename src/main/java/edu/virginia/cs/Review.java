package edu.virginia.cs;

public class Review {

    private String text;
    private int rating;
    private String studentName;

    public Review(String text, int rating, String studentName) {
        this.text = text;
        this.rating = rating;
        this.studentName = studentName;
    }

    public Review(String text, int rating) {
        this.text = text;
        this.rating = rating;
    }

    public Review() {
    }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }

    public String getStudentName() {
        return studentName;
    }
}
