package com.timothyblumberg.autodidacticism.learnthings.question;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.text.TextUtils;

import com.timothyblumberg.autodidacticism.learnthings.App;

import java.util.Random;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


/**
 * Created by Tim on 7/21/14.
 */
public class QuestionDAO {

    final public static String RATIO_QUERY_FORMAT =  "SELECT *, (num_correct / numberAsks) as ratio FROM Question ORDER BY ratio LIMIT %s";
    final public static String RATIO_QUERY =  "SELECT *, (num_correct / numberAsks) as ratio FROM Question ORDER BY ratio LIMIT 1";
    final public static String RANDOM_QUERY_FORMAT =  "SELECT * FROM Question ORDER BY RANDOM() LIMIT %s";
    final public static String RANDOM_QUERY =  "SELECT * FROM Question ORDER BY RANDOM() LIMIT 1";
    final public static String RAND_RATIO_QUERY = "SELECT *, (num_correct / (num_incorrect + 1) ) as ratio FROM (SELECT * FROM Question ORDER BY RANDOM() LIMIT 3) ORDER BY ratio LIMIT 1";
    final public static String RAND_WRONG = "SELECT * FROM Question WHERE correctlyAnswered = 'F' ORDER BY RANDOM() LIMIT 1";

    final public static String[] queryArray = {RAND_WRONG}; //RATIO_QUERY, RANDOM_QUERY, RAND_RATIO_QUERY


    public static Question getQuestionById(String questionId) {
        if (TextUtils.isEmpty(questionId)) {
            return null;
        } else {
            final String selectionStr = "question_id = ?";
            return getQueryBuilder().withSelection(selectionStr, questionId).get();
        }
    }

    public static Question getRandomQuestion() throws CursorIndexOutOfBoundsException{
        Cursor c = App.getWritableDB().rawQuery(getRandomQuery(), null);

        // Move the cursor, grab question_id, convert to Question obj, return
        c.moveToFirst();
        int question_idCol = c.getColumnIndex("question_id");
        String question_id = c.getString(question_idCol);
        Question rand_q = getQuestionById(question_id);
        return rand_q;

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

        // Delete LeoGroup in DB
        cupboard().withDatabase(App.getWritableDB()).delete(question);
    }

    public static int getNumberOfQuestions(){
        Cursor c = cupboard().withDatabase(App.getWritableDB()).query(Question.class).getCursor();
        return c.getCount();
    }

    public static Question[] getQuestionList(String query, int num_qs) {
        Cursor c = App.getWritableDB().rawQuery(String.format(query, num_qs), null);
        Question[] qArray = new Question[num_qs];
        int i = 0;
        int qTextCol = c.getColumnIndex("qText");

        c.moveToFirst();
        while (c.isAfterLast() == false) {
            // Get question
            int question_idCol = c.getColumnIndex("question_id");
            String question_id = c.getString(question_idCol);
            qArray[i] = getQuestionById(question_id);

            // Bookkeeping
            i++;
            c.moveToNext();
        }

        return qArray;
    }

    // Private Methods
    private static DatabaseCompartment.QueryBuilder<Question> getQueryBuilder() {
        return cupboard().withDatabase(App.getWritableDB()).query(Question.class);
    }

    private static String getRandomQuery(){
        if(queryArray.length == 1){
            return queryArray[0];
        } else {
            Random r = new Random();
            int rand_index = r.nextInt(queryArray.length - 1);
            return queryArray[rand_index];
        }
    }


}
