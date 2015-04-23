package com.example.rohit.cryptogame;

/**
 * Created by Rohit on 4/22/15.
 */
public class Question {

    private int id;

    private String question;
    private String[] options;
    private int correctAnswer;

    public Question(int id, String s){
        this.id = id;
        question = s;
    }

    public void setOptions(String[] strings,int correctAnswer){
        options = new String[strings.length];
        for (int i=0; i<strings.length; i++){
            options[i] = strings[i];
        }
        this.correctAnswer = correctAnswer;
    }

    public String[] getOptions() {
        return options;
    }

    public String getQuestion() {
        return question;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
