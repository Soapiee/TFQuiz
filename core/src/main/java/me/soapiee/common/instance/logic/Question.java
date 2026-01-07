package me.soapiee.common.instance.logic;

import lombok.Getter;

public class Question {

    @Getter private final String question;
    @Getter private final String correctionMessage;

    public Question(String question, String correctionMessage) {
        this.question = question;
        this.correctionMessage = correctionMessage;
    }

}
