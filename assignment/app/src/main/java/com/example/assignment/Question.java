package com.example.assignment;

import java.util.List;

public class Question {
    private int id;
    private String text;
    private List<Answer> answers;
    private int correctAnswerIndex;

    public Question(int id, String text, List<Answer> answers, int correctAnswerIndex) {
        this.id = id;
        this.text = text;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}


