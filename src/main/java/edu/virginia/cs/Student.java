package edu.virginia.cs;

public class Student {

    private String name, password;

    public Student(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }
}
