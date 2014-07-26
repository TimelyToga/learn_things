package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionFactory;
import com.timothyblumberg.autodidacticism.learnthings.user.User;
import com.timothyblumberg.autodidacticism.learnthings.user.UserDAO;

import java.util.Calendar;

public class BaseActivity extends Activity {

    private static final String TAG = MCActivity.class.getSimpleName();
    protected CountDownTimer waitTimer;


    /**
     * Sets the time
     */
    protected void scheduleNotif() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        int timeUntilNextNotif = Globals.TIME_UNTIL_NEXT_NOTIFICATION +
                Globals.rgen.nextInt(Globals.TIME_UNTIL_NEXT_NOTIFICATION);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + timeUntilNextNotif,
                alarmIntent);
    }

    protected CountDownTimer makeTimer(final TextView ttView){
        return new CountDownTimer(Globals.TIMER_COUNTDOWN_LENGTH, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ttView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                finish();
            }
        };
    }

    protected void setLayoutTouchListener(RelativeLayout layout){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitTimer.cancel();
            }
        });
    }

    public static void initQuestionsAndUser(){
        // Initialize questions and User obj
        if(QuestionDAO.getNumberOfQuestions() == 0){
            QuestionFactory.createQuestions();
        }
        try{
            Globals.curUser = UserDAO.testUserExistence();
        } catch (CursorIndexOutOfBoundsException e){
            // If nothing is found, createMC the user
            Log.d(TAG, "\n\n\nCreating user\n\n\n");
            Globals.curUser = User.create();
        }
    }

    @Override
    public void finish(){
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        super.finish();
    }
}
