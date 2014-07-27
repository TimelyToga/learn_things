package com.timothyblumberg.autodidacticism.learnthings.user;

import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

import java.util.UUID;

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
    public int TIME_UNTIL_NEXT_NOTIFICATION;

    /* User Information */
    public String name;
    public String curTrue;

    public static User create(){
        User user = new User();
        user.user_id = UUID.randomUUID().toString();
        user._id = Util.getLongFromUUIDString(user.user_id);
        user.total_questions = QuestionDAO.getNumberOfQuestions();
        user.TIME_UNTIL_NEXT_NOTIFICATION = Globals.TIME_UNTIL_NEXT_NOTIFICATION;
        user.curTrue = "F";
        UserDAO.save(user);

        return user;
    }

    public void updateNotifTime(int newTime){
        UserDAO.updateNotifTime(this, newTime);
        Toast.makeText(App.getAppContext(), "times per day: " + Globals.MILLISECONDS_IN_DAY/newTime, Toast.LENGTH_SHORT).show();
    }

    public String getUserId(){
        return String.valueOf(this._id);
    }
}
