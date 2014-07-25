package com.timothyblumberg.autodidacticism.learnthings.user;

import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

/**
 * Created by Tim on 7/24/14.
 */
public class User {

    /* Preferences */

    /* User Stats*/
    public long _id;
    public String user_id;
    public int total_answer_attempts;
    public int total_questions;
    public int total_correct_questions;

    /* User Information */
    public String name;
    public String curTrue;

    public static User create(){
        User user = new User();
        user._id = Util.getLongFromUUIDString(user.user_id);
        user.total_questions = QuestionDAO.getNumberOfQuestions();
        user.curTrue = "F";
        UserDAO.save(user);

        return user;
    }

    public String getUserId(){
        return String.valueOf(this._id);
    }
}
