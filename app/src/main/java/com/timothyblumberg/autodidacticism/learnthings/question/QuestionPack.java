package com.timothyblumberg.autodidacticism.learnthings.question;

import android.util.Log;

import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;

import java.util.UUID;

/**
 * Created by Tim on 7/30/14.
 */
public class QuestionPack {


    public static final int INTERNET = 0;
    public static final int THIS_USER_CREATED = 1;
    public static final int OTHER_USER_CREATED = 2;
    public static final int CAME_WITH_APP = 3;

    public long _id; // required by cupboard
    public String qpack_id;
    public String displayName;
    public String qPackDescription;
    public int source;
    public int numQuestions;
    public int numAllCorrect;
    public String active; // booleanString
    public String userEditable; // booleanString

    public static QuestionPack createQuestionPack(String qPackID, String qPackDisplayName,
                                                  String qPackDesc, int qPackSource) {

        final QuestionPack qPack = new QuestionPack();
        qPack.qpack_id = qPackID;
        qPack._id = Util.getLongFromUUIDString(UUID.randomUUID().toString());
        qPack.displayName = qPackDisplayName;
        qPack.active = G.TRUE;
        qPack.numQuestions = QuestionDAO.qPackQuestionCount(qPack.qpack_id);
        qPack.numAllCorrect = 0;
        qPack.source = qPackSource;
        qPack.qPackDescription = qPackDesc;
        if(qPackSource == THIS_USER_CREATED){
            // Created by this user
            qPack.userEditable = G.TRUE;
        } else {
            // Another source
            qPack.userEditable = G.FALSE;
        }

        QuestionPackDAO.save(qPack);
        return qPack;
    }

    public String getQPackId(){
        return this.qpack_id;
    }

    public void setActive(boolean newActive){
        if(newActive){
            this.active = G.TRUE;
        } else {
            this.active = G.FALSE;
        }
    }

    public static QuestionPack createQPackIfNecessary(String qPackID){
        QuestionPack qPack = QuestionPackDAO.getQPackById(qPackID);
        if(qPack == null){
            // qPack doesn't exist; create it;
            qPack = QuestionPack.createQuestionPack(qPackID, "User Created Questions", "", THIS_USER_CREATED);
            Log.wtf(QuestionPack.class.getSimpleName(), "\n\n---------->\n\nCreated question Pack instead of finding " +
                    "it");
        }

        Log.d(QuestionPack.class.getSimpleName(), "All should be good?");

        return qPack;
    }

}
