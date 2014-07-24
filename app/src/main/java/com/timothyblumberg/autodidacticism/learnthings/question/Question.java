package com.timothyblumberg.autodidacticism.learnthings.question;

import com.timothyblumberg.autodidacticism.learnthings.common.Util;

import java.util.UUID;

/**
 * Created by Tim on 7/21/14.
 */
public class Question {

    public long _id; // required by cupboard
    public String question_id;
    public String qText;
    public String answers_a;
    public String answers_b;
    public String answers_c;
    public int numberAsks = 0;
    public int num_correct = 0;
    public int num_incorrect = 0;
    public String correctlyAnswered = "F"; // Has the question ever been answered correctly?
    public String lastAnswerCorrect = "F"; // Answered correctly last time answered?
    public long lastAsked;

    public static Question create(String qText, String[] answers){

        final Question question = new Question();
        question.question_id = UUID.randomUUID().toString();
        question._id = Util.getLongFromUUIDString(question.question_id);
        question.qText = qText;
        question.numberAsks = 0;
        question.num_correct = 0;
        question.num_incorrect = 0;
        question.correctlyAnswered = "F";
        question.lastAsked = 0; // Should init at Unix epoch (1970) aka 0 BCE

        question.answers_a = answers[0];
        question.answers_b = answers[1];
        question.answers_c = answers[2];

        QuestionDAO.save(question);
        return question;
    }

    /**
     * Method is called with result from answer attempt
     *
     * @param correctlyAnswered T/F as to whether the last attempt succeeded (true) or failed (false)
     */
    public void setOutcome(boolean correctlyAnswered){
        // Set String boolean
        if(correctlyAnswered){
            // Only change correctlyAnswered on correct
            this.correctlyAnswered = "T";
            this.lastAnswerCorrect = "T";
            ++this.num_correct;
        } else {
            this.lastAnswerCorrect = "F";
            ++this.num_incorrect;
        }
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
        String[] temp = {answers_a, answers_b, answers_c};
        Util.shuffle(temp);
        return temp;
    }

    public String getQuestionId(){
        return this.question_id;
    }

    public boolean getCorrectlyAnswered(){
        if(this.correctlyAnswered.equals("T")){
            return true;
        }

        return false;
    }

    public void setCorrectlyAnswered(boolean bool){
        if(bool){
            this.correctlyAnswered = "T";
        } else {
            this.correctlyAnswered = "F";
        }
    }
}
