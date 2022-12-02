package com.ltp.gradesubmission;

public class Grade {
    private String name;
    private String subject;
    private String score;

    public Grade(String name, String subject, String grade) {
        this.name = name;
        this.subject = subject;
        this.score = grade;
    }

    public Grade() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
