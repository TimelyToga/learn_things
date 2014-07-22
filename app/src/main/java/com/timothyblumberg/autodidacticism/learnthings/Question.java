package com.timothyblumberg.autodidacticism.learnthings;

import java.util.Date;

/**
 * Created by Tim on 7/21/14.
 */
public class Question {

    String qText;
    String[] answers;
    int numberAsks = 0;
    boolean correctlyAnswered = false;
    Date lastAsked;

    public Question(String qText, String[] answers){
        this.qText = qText;
        this.answers = answers;
        this.numberAsks = 0;
        this.correctlyAnswered = false;
        this.lastAsked = new Date(0); // Should init at Unix epoch (1970) aka 0 BCE
    }

    /**
     * Method is called with result from answer attempt
     *
     * @param correctlyAnswered T/F as to whether the last attempt succeeded (true) or failed (false)
     */
    public void setOutcome(boolean correctlyAnswered){
        this.correctlyAnswered = correctlyAnswered;
        ++this.numberAsks;
        this.lastAsked = new Date(); // Should update to current time
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
}
