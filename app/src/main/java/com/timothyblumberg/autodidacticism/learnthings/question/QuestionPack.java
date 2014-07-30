package com.timothyblumberg.autodidacticism.learnthings.question;

import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;

import java.util.UUID;

/**
 * Created by Tim on 7/30/14.
 */
public class QuestionPack {

    public class QuestionPackSource {

        public final int INTERNET = 0;
        public final int THIS_USER_CREATED = 1;
        public final int OTHER_USER_CREATED = 2;
    }

    public long _id; // required by cupboard
    public String qpack_id;
    public String displayName;
    public int source;
    public int numQuestions;
    public int numAllCorrect;
    public String active; // booleanString

    public static QuestionPack createQuestionPack(String qPackDisplayName, int qPackSource) {

        final QuestionPack qPack = new QuestionPack();
        qPack.qpack_id = UUID.randomUUID().toString();
        qPack._id = Util.getLongFromUUIDString(qPack.qpack_id);
        qPack.displayName = qPackDisplayName;
        qPack.active = G.FALSE;
        qPack.numQuestions = QuestionDAO.qPackQuestionCount(qPack.qpack_id);
        qPack.numAllCorrect = 0;
        qPack.source = qPackSource;

        QuestionPackDAO.save(qPack);
        return qPack;
    }

    public String getQPackId(){
        return this.qpack_id;
    }

}
