package com.timothyblumberg.autodidacticism.learnthings.question;

import android.text.TextUtils;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.common.G;

import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by Tim on 7/30/14.
 */
public class QuestionPackDAO {

    public static void save(QuestionPack qPack) {

        // See if the qPack already exists
        final QuestionPack existingQPack = getQPackById(qPack.getQPackId());
        if (existingQPack != null) {

            // If the qPack does exists, then update the _id. We need to do this
            // since calling .put() checks for the _id (Long), and not the
            // group_id (String), so that it updates the group fields accordingly.
            qPack._id = existingQPack._id;
        }

        cupboard().withDatabase(App.getWritableDB()).put(qPack);
    }

    public static QuestionPack getQPackById(String qPackID) {
        if (TextUtils.isEmpty(qPackID)) {
            return null;
        } else {
            return getQueryBuilder().withSelection(G.SELECT_QUESTION_PACK_ID, qPackID).get();
        }
    }

    public static QuestionPack getQPackByDisplayName(String displayName){
        if (TextUtils.isEmpty(displayName)) {
            return null;
        } else {
            return getQueryBuilder().withSelection(G.SELECT_QUESTION_PACK_DISPLAY_NAME, displayName).get();
        }
    }

    public static boolean deleteQuestionPack(QuestionPack qPack) {
        if(qPack.userEditable.equals(G.TRUE)){
            // Delete QuestionPack in DB only the user has the permission
            cupboard().withDatabase(App.getWritableDB()).delete(qPack);
            // Deleted
            return true;
        }

        // Did not delete
        return false;
    }

    /**
     * Gives list of QuestionPacks
     *
     * @return
     */
    public static List<QuestionPack> getQPackList() {
        return getQueryBuilder().distinct().list();
    }

    public static List<QuestionPack> getActiveQPackList(){
        return getQueryBuilder().withSelection(G.SELECT_QUESTION_PACK_ACTIVE, G.TRUE).list();
    }

    /**
     * Creates a list in correct format of all active qPackIDs
     *
     * ('qpackID0001', 'qpackID0002', ..., 'qpackIDXXX')
     * @return
     */
    public static String getActivePackListString(){
        String quotes = "'%s', ";
        List<QuestionPack> questionPacks = getActiveQPackList();
        if(questionPacks.size() == 0){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for(QuestionPack qPack : questionPacks){
            builder.append(String.format(quotes, qPack.qpack_id));
        }
        // Should delete last comma
        builder.delete(builder.length() - 2, builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

    public static List<QuestionPack> getUserEditableQPackList(){
        return getQueryBuilder().withSelection(G.SELECT_QUESTION_PACK_DISPLAY_NAME, G.TRUE).list();
    }


    // Private Methods
    private static DatabaseCompartment.QueryBuilder<QuestionPack> getQueryBuilder() {
        return cupboard().withDatabase(App.getWritableDB()).query(QuestionPack.class);
    }
}