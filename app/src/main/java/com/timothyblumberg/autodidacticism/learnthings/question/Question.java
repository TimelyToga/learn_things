package com.timothyblumberg.autodidacticism.learnthings.question;

import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.activities.WinActivity;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;

import java.util.UUID;

/**
 * Created by Tim on 7/21/14.
 */
public class Question {

    public long _id; // required by cupboard
    public String question_id;
    public String qText;
    public String qpack_id;

    public String mcAnswerA;
    public String mcAnswerB;
    public String mcAnswerC;

    public String frAnswerText;
    public boolean multipleChoice; // True for MC, False for free response

    public int numberAsks = 0;
    public int num_correct = 0;
    public int num_incorrect = 0;
    public long lastAsked;

    public String correctlyAnswered = "F"; // Has the question ever been answered correctly?
    public String lastAnswerCorrect = "F"; // Answered correctly last time answered?

    public static Question createFR(String qText, String questionAnswer, String qPackID){

        final Question question = new Question();
        question.question_id = UUID.randomUUID().toString();
        question._id = Util.getLongFromUUIDString(question.question_id);
        question.qText = qText;
        question.frAnswerText = questionAnswer;
        question.numberAsks = 0;
        question.num_correct = 0;
        question.num_incorrect = 0;
        question.correctlyAnswered = "F";
        question.lastAsked = 0; // Should init at Unix epoch (1970) aka 0 BCE
        question.multipleChoice = false;
        question.qpack_id = qPackID;

        QuestionPack.createQPackIfNecessary(qPackID);
        QuestionDAO.save(question);
        Log.d(Question.class.getSimpleName(), "Creating FR Question" + question.qText);
        return question;
    }

    public static Question createMC(String qText, String[] answers, String qPackID){

        final Question question = new Question();
        question.question_id = UUID.randomUUID().toString();
        question._id = Util.getLongFromUUIDString(question.question_id);
        question.qText = qText;
        question.numberAsks = 0;
        question.num_correct = 0;
        question.num_incorrect = 0;
        question.correctlyAnswered = "F";
        question.lastAsked = 0; // Should init at Unix epoch (1970) aka 0 BCE
        question.multipleChoice = true;
        question.qpack_id = qPackID;

        question.mcAnswerA = answers[0];
        question.mcAnswerB = answers[1];
        question.mcAnswerC = answers[2];

        QuestionDAO.save(question);
        Log.d(Question.class.getSimpleName(), "Creating MC Question" + question.qText);
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
            this.correctlyAnswered = G.CUR_TRUE;
            this.lastAnswerCorrect = G.CUR_TRUE;
            ++this.num_correct;
        } else {
            this.lastAnswerCorrect = G.CUR_FALSE;
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
        String[] temp = {mcAnswerA, mcAnswerB, mcAnswerC};
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

    /**
     * Returns the correct answer (without '@')
     * @return String of correct answer (no '@')
     */
    public String getCorrectAnswer(){
        if(!this.multipleChoice){
            return this.frAnswerText;
        }
        String[] answers = getAnswers();
        for(String s : answers){
            if(s.startsWith("@")){
                return s.substring(1);
            }
        }
        // SHOULD BE UNREACHABLE.
        return App.getAppContext().getString(R.string.error_malformed_answer);
    }

    public static Question getQuestionOrHandleWin(){
        Question rand_q = null;

        try{
            rand_q = QuestionDAO.getRandomQuestion();
        } catch(CursorIndexOutOfBoundsException e) {
            //TODO: Figure out what to do when all questions have been correctly answered
            Log.d("", "All questions from active packs have been correctly answered");

            WinActivity.launch(App.getAppContext());
        }

        // Handle endcase
        if(rand_q == null){
            // End case handling should be handled already
        }

        return rand_q;
    }

}
