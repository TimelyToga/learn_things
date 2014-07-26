package com.timothyblumberg.autodidacticism.learnthings.question;

import com.google.gson.Gson;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;

/**
 * Created by Tim on 7/26/14.
 */
public class QuestionFactory {

    public static void createQuestions(){
        String[] array = {"#North", "@South", "#East"};
        Question q1 = Question.createMC("Which direction is generally down on a map?", array);
        String[] answers = {"@San Francisco", "#Durham", "#Gray"};
        String[] answers2 = {"@Paris", "#Toulouse", "#Orleans"};
        String[] answers3 = {"@IKEA", "#Grand Furniture", "#Zack's Furniture"};
        String[] answers4 = {"@Santa Claus", "#Easter Bunny", "#Father Time"};
        Question q5 = Question.createMC("What is the capital of France?", answers2);
        Question q2 = Question.createMC("What city is the best city?", answers);
        Question q3 = Question.createMC("Where is cheap furniture bought from?", answers3);
        Question q4 = Question.createMC("Who comes down the chimney on Christmas?", answers4);

        // FR Questions
        Question freeQuestion = Question.createFR("What time is it?");
        Util.readCSV();
    }

    public static Question createQuestionfromJson(String json){
        final Gson gson = new Gson();
        Question q = gson.fromJson(json, Question.class);
        return q;
    }
}
