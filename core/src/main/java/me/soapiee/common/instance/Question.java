package me.soapiee.common.instance;

import lombok.Getter;

public class Question {

    private final String question;
    @Getter private final String correctionMessage;

    public Question(String question, String correctionMessage) {
        this.question = question;
        this.correctionMessage = correctionMessage;
    }

    @Override
    public String toString() {
        return question;
    }

}
