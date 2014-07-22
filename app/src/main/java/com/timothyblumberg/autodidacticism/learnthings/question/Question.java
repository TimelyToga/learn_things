package com.timothyblumberg.autodidacticism.learnthings.question;

import com.timothyblumberg.autodidacticism.learnthings.Util;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Tim on 7/21/14.
 */
public class Question {

    public long _id; // required by cupboard
    public String question_id;
    public String qText;
    public String[] answers;
    public int numberAsks = 0;
    public boolean correctlyAnswered = false;
    public int lastAsked;

    public static Question create(String qText, String[] answers){

        final Question question = new Question();
        question.question_id = UUID.randomUUID().toString();
        question._id = UUID.fromString(this.question_id).getMostSignificantBits();
        question.qText = qText;
        question.answers = answers;
        question.numberAsks = 0;
        question.correctlyAnswered = false;
        question.lastAsked = 0; // Should init at Unix epoch (1970) aka 0 BCE

        Question.save(question);
        return question;
    }

    /**
     * Method is called with result from answer attempt
     *
     * @param correctlyAnswered T/F as to whether the last attempt succeeded (true) or failed (false)
     */
    public void setOutcome(boolean correctlyAnswered){
        this.correctlyAnswered = correctlyAnswered;
        ++this.numberAsks;
        this.lastAsked = System.currentTimeMillis(); // Should update to current time
        QuestionDAO.save(this);
    }

    /**
     * Returns the randomized contents of the answers array
     *
     * @return randomized string array of answers
     */
    public String[] getAnswers(){
        String[] temp = this.answers;
        Util.shuffle(temp);
        return temp;
    }

    public String getQuestionId(){
        return this.question_id;
    }
}
