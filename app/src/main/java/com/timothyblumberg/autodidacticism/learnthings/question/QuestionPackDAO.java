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

    public static List<QuestionPack> getUserEditableQPackList(){
        return getQueryBuilder().withSelection(G.SELECT_QUESTION_PACK_DISPLAY_NAME, G.TRUE).list();
    }


    // Private Methods
    private static DatabaseCompartment.QueryBuilder<QuestionPack> getQueryBuilder() {
        return cupboard().withDatabase(App.getWritableDB()).query(QuestionPack.class);
    }
}