package com.timothyblumberg.autodidacticism.learnthings.user;

import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
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
    public int curListPosition;

    /* User Information */
    public String name;
    public String curTrue;

    public static User create(){
        User user = new User();
        user.user_id = UUID.randomUUID().toString();
        user._id = Util.getLongFromUUIDString(user.user_id);
        user.total_questions = QuestionDAO.getTotalNumberOfQuestions();
        user.TIME_UNTIL_NEXT_NOTIFICATION = G.INITIAL_TIME_FOR_NOTIF;
        user.curTrue = "F";
        user.curListPosition = 0;
        UserDAO.save(user);

        return user;
    }

    public void updateNotifTime(int newTime){
        this.TIME_UNTIL_NEXT_NOTIFICATION = newTime;
        UserDAO.save(this);

        // Report
        String message = String.format(App.getAppContext().getString(R.string.toast_times_per_day),
                G.MILLISECONDS_IN_DAY/newTime);
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void setCurListPosition(int newListPos){
        this.curListPosition = newListPos;
        UserDAO.save(this);
    }

    public String getUserId(){
        return String.valueOf(this._id);
    }
}
