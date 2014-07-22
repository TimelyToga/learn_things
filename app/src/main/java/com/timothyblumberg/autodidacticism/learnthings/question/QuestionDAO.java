package com.timothyblumberg.autodidacticism.learnthings.question;

import android.text.TextUtils;

import com.timothyblumberg.autodidacticism.learnthings.App;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


/**
 * Created by Tim on 7/21/14.
 */
public class QuestionDAO {

    public static Question getQuestionById(String questionId) {
        if (TextUtils.isEmpty(questionId)) {
            return null;
        } else {
            final String selectionStr = "question_id = ?";
            return getQueryBuilder().withSelection(selectionStr, questionId).get();
        }
    }

    public static void save(Question question) {

        // See if the group already exists
        final Question existingQuestion = getQuestionById(question.getQuestionId());
        if (existingQuestion != null) {

            // If the group does exists, then update the _id. We need to do this
            // since calling .put() checks for the _id (Long), and not the
            // group_id (String), so that it updates the group fields accordingly.
            question._id = existingQuestion._id;
        }

        cupboard().withDatabase(App.getWritableDB()).put(question);
    }

    public static void deleteQuestion(Question question) {

        // Delete Question in DB
        cupboard().withDatabase(App.getWritableDB()).delete(question);
    }

    private static DatabaseCompartment.QueryBuilder<Question> getQueryBuilder() {
        return cupboard().withDatabase(App.getWritableDB()).query(Question.class);
    }

}
