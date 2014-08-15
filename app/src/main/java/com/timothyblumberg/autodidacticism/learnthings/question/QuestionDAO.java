package com.timothyblumberg.autodidacticism.learnthings.question;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.text.TextUtils;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.activities.BaseActivity;
import com.timothyblumberg.autodidacticism.learnthings.activities.WinActivity;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.ToastUtil;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;

import java.util.ArrayList;
import java.util.List;
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
    final public static String RAND_WRONG_FROM_ACTIVE = "SELECT * FROM Question WHERE correctlyAnswered = '%s' AND qpack_id IN %s ORDER BY RANDOM() LIMIT 1";

    final public static String[] queryArray = {RAND_WRONG_FROM_ACTIVE}; //RATIO_QUERY, RANDOM_QUERY, RAND_RATIO_QUERY


    public static Question getQuestionById(String questionId) {
        if (TextUtils.isEmpty(questionId)) {
            return null;
        } else {
            return getQueryBuilder().withSelection(G.SELECT_QUESTION_ID, questionId).get();
        }
    }

    public static Question getRandomQuestion() throws CursorIndexOutOfBoundsException {
        String query = buildRandWrongActiveQueryString();
        if (!Util.isNotEmpty(query)) {
            // Query is null
            ToastUtil.showShort("There are no question packs checked 'active'.");
            return null;
        }
        Cursor c = App.getWritableDB().rawQuery(query, null);

        // Move the cursor, grab question_id, convert to Question obj, return
        try{
            c.moveToFirst();
            int question_idCol = c.getColumnIndex("question_id");
            String question_id = c.getString(question_idCol);
            Question rand_q = getQuestionById(question_id);
            return rand_q;
        } catch (CursorIndexOutOfBoundsException e){
            ToastUtil.showShort("Launched from rand q DAO");
            WinActivity.launch(App.getAppContext());

            return null;
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

    public static int getTotalNumberOfQuestions(){
        Cursor c = cupboard().withDatabase(App.getWritableDB()).query(Question.class).getCursor();
        return c.getCount();
    }

    /**
     * Gives the number of Questions in a specified QuestionPack
     * @param qPackID
     * @return
     */
    public static int qPackQuestionCount(String qPackID){
        Cursor c = cupboard().withDatabase(App.getWritableDB()).query(Question.class)
                .withSelection(G.SELECT_QUESTION_PACK_ID, qPackID).getCursor();
        return c.getCount();
    }

    public static Question[] getQuestionArray(String query, int num_qs) {
        Cursor c = App.getWritableDB().rawQuery(String.format(query, num_qs), null);
        Question[] qArray = new Question[num_qs];
        int i = 0;
        int qTextCol = c.getColumnIndex("qText");

        c.moveToFirst();
        while ( !c.isAfterLast() ) {
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

    public static void resetDB(){
        cupboard().withDatabase(App.getWritableDB()).dropAllTables();
        cupboard().withDatabase(App.getWritableDB()).createTables();
        BaseActivity.initQuestionsAndUser();
        
        Toast.makeText(App.getAppContext(), "Reset DB", Toast.LENGTH_SHORT).show();
    }

    public static List<String> getQuestionPacks(){
        List<String> qPackList = new ArrayList<String>();
        Cursor c = App.getWritableDB().rawQuery("SELECT DISTINCT qpack_id FROM Question", null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            qPackList.add(c.getString(0));
            c.moveToNext();
        }


        return qPackList;
    }

    public static List<Question> getQuestionForQuestionPack(String qPackID){
        return getQueryBuilder().withSelection(G.SELECT_QUESTION_PACK_ID, qPackID).list();
    }

    // Private Methods
    private static DatabaseCompartment.QueryBuilder<Question> getQueryBuilder() {
        return cupboard().withDatabase(App.getWritableDB()).query(Question.class);
    }

    private static String buildRandWrongActiveQueryString(){
        String activePackList = QuestionPackDAO.getActivePackListString();
        if(Util.isNotEmpty(activePackList)){
            return String.format(RAND_WRONG_FROM_ACTIVE, G.CUR_FALSE, activePackList);
        }

        return null;
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
