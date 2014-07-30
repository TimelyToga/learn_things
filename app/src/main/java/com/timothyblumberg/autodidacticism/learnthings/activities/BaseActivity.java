package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.Globals;
import com.timothyblumberg.autodidacticism.learnthings.common.ToastUtil;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionFactory;
import com.timothyblumberg.autodidacticism.learnthings.user.User;
import com.timothyblumberg.autodidacticism.learnthings.user.UserDAO;

import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends Activity {

    private static final String TAG = MCActivity.class.getSimpleName();
    protected CountDownTimer waitTimer;
    protected ViewGroup mViewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/RobotoSlab-Light.ttf", R.attr.fontPath);
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    /**
         * Sets the time
         */
    protected void scheduleNotif(int timeUntilNextNotif) {
        if(timeUntilNextNotif < 0){
            timeUntilNextNotif = Globals.curUser.TIME_UNTIL_NEXT_NOTIFICATION;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + timeUntilNextNotif,
                alarmIntent);
    }

    protected CountDownTimer makeTimer(){
        return new CountDownTimer(Globals.TIMER_COUNTDOWN_LENGTH, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
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
                Button exitButton = (Button)findViewById(R.id.exit_button);
                exitButton.setVisibility(View.VISIBLE);
                ToastUtil.showShort("Auto-exit paused");
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

    public void exitView(View v){
        finish();
    }


//    <!--<ImageView-->
//    <!--android:layout_width="wrap_content"-->
//    <!--android:layout_height="wrap_content"-->
//    <!--android:background="@drawable/card_template"-->
//    <!--android:id="@+id/card"-->
//    <!--android:layout_alignParentTop="true"-->
//    <!--android:layout_centerHorizontal="true" />-->

    protected ImageView setUpImageView(boolean correct, boolean fromFR){
        ImageView im = new ImageView(App.getAppContext());
        if(correct){
            im.setImageResource(R.drawable.success_icn);
        } else {
            im.setImageResource(R.drawable.failure_icn);
        }
        im.setPadding(5,5,5,5);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if(fromFR) p.addRule(RelativeLayout.BELOW, R.id.fr_correctnessText);
        else p.addRule(RelativeLayout.BELOW, R.id.mc_correctnessText);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        im.setLayoutParams(p);

        return im;
    }
}
