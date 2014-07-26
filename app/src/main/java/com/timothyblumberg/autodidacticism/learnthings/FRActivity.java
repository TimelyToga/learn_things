package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.common.QuestionFactory;
import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.user.User;
import com.timothyblumberg.autodidacticism.learnthings.user.UserDAO;

import java.util.Calendar;

public class FRActivity extends Activity {

    private CountDownTimer waitTimer;

    public static TextView wordTextView;
    public static TextView timerTextView;
    public static ImageView questionResult;
    public static App sApp;

    public static Question curQuestion;

    private final String TAG = FRActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isFR = extras.getBoolean(Globals.EXTRA_IS_FR);
            String question_id = extras.getString(Globals.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);
            Log.d(TAG, "FR: " + String.valueOf(isFR));

            if(isFR){

                setContentView(R.layout.activity_fr);
                // Get important references
                wordTextView = (TextView)findViewById(R.id.fr_resultText);
                timerTextView = (TextView)findViewById(R.id.fr_timer);
                questionResult = (ImageView)findViewById(R.id.fr_questionResult);
                sApp = App.getInstance();

            }

        }  else if(Globals.DEBUG){
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Globals.DEFAULT_NOTIFICATIONS_CODE);


        waitTimer = new CountDownTimer(Globals.TIMER_COUNTDOWN_LENGTH, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();

        // initialization
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

        scheduleNotif();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fr, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void correctClick(View v){
        waitTimer.start();
        curQuestion.setOutcome(true);
        wordTextView.setText("YAY! Correct!");
    }

    public void incorrectClick(View v){
        waitTimer.start();
        curQuestion.setOutcome(false);
        wordTextView.setText("Don't worry, we'll ask you again later.");
    }

    private void scheduleNotif() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        int timeUntilNextNotif = Globals.TIME_UNTIL_NEXT_NOTIFICATION +
                Globals.rgen.nextInt(Globals.TIME_UNTIL_NEXT_NOTIFICATION);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + timeUntilNextNotif,
                alarmIntent);
    }

}
