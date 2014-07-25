package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.user.User;
import com.timothyblumberg.autodidacticism.learnthings.user.UserDAO;

import java.util.Calendar;


public class MainActivity extends Activity {

    public static final int DEFAULT_NOTIFICATIONS_CODE = 98;

    public CountDownTimer waitTimer;


    public static final int A_CODE = 0;
    public static final int B_CODE = 1;
    public static final int C_CODE = 2;

    private final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_ANSWER = "EXTRA_ANSWER";
    public static final String EXTRA_QUESTION_ID = "EXTRA_QUESTION_ID";
    public static final String EXTRA_CORRECT = "EXTRA_CORRECT"; // true if correct | false if incorrect
    public static App sApp;
    public static TextView wordTextView;
    public static TextView timerTextView;
    public static ImageView questionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(DEFAULT_NOTIFICATIONS_CODE);

        // Get important references
        wordTextView = (TextView)findViewById(R.id.resultText);
        timerTextView = (TextView)findViewById(R.id.timer);
        questionResult = (ImageView)findViewById(R.id.questionResult);
        sApp = App.getInstance();

        waitTimer = new CountDownTimer(Globals.COUNTDOWN_LENGTH, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int answer_code = extras.getInt(EXTRA_ANSWER);
            boolean correct = extras.getBoolean(EXTRA_CORRECT);
            String question_id = extras.getString(EXTRA_QUESTION_ID);

            Question question = QuestionDAO.getQuestionById(question_id);
            question.setOutcome(correct);

            switch(answer_code){
                case A_CODE:
                    wordTextView.setText("Answered A");
                    break;
                case B_CODE:
                    wordTextView.setText("Answered B");
                    break;
                case C_CODE:
                    wordTextView.setText("Answered C");
                    break;
            }

            if(correct){
                questionResult.setImageResource(R.drawable.success_icn);
            } else {
                questionResult.setImageResource(R.drawable.failure_icn);
            }

            if(Globals.DEBUG){
                wordTextView.append("\nViews: " + question.numberAsks);
                Question[] qList = QuestionDAO.getQuestionList(QuestionDAO.RATIO_QUERY_FORMAT, 5);
                for(Question q : qList){
                    if(q.numberAsks > 0){
                        wordTextView.append(q.qText + " -- " + ((double) q.num_correct / (double) (q.num_incorrect + 1)) + "\n");
                    }
                }
            }

        } else if(Globals.DEBUG){
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        // initialization
        if(QuestionDAO.getNumberOfQuestions() == 0){
            createQuestions();
        }
        try{
            Globals.curUser = UserDAO.testUserExistence();
        } catch (CursorIndexOutOfBoundsException e){
            // If nothing is found, create the user
            Log.d(TAG, "\n\n\nCreating user\n\n\n");
            Globals.curUser = User.create();
        }

        scheduleNotif();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * This method creates a Notification builder from the specified Question obj
     * @param question A simple string with the question (? included)
     * @return NotificationCompat.Builder to create the notifs
     */
    public NotificationCompat.Builder createMCBuilder(Question question){
        // Get pertinent fields from Question obj
        String curQText = question.qText;
        String[] answers = question.getAnswers();
        String id = question.getQuestionId();

        boolean[] correctArray = new boolean[3];
        // Find right answer, strip identifiers
        for(int a = 0; a < answers.length; a++){
            String curString = answers[a];
            if(curString.startsWith("@")){
                correctArray[a] = true;
                answers[a] = answers[a].substring(1);
            } else {
                correctArray[a] = false;
                answers[a] = answers[a].substring(1);
            }
        }

        // Creates an explicit intent for an Activity in your app
        Intent aIntent = new Intent(this, MainActivity.class)
                .setAction("answer_a")
                .putExtra(EXTRA_ANSWER, A_CODE)
                .putExtra(EXTRA_QUESTION_ID, id)
                .putExtra(EXTRA_CORRECT, correctArray[0]);
        Intent bIntent = new Intent(this, MainActivity.class)
                .setAction("answer_b")
                .putExtra(EXTRA_ANSWER, B_CODE)
                .putExtra(EXTRA_QUESTION_ID, id)
                .putExtra(EXTRA_CORRECT, correctArray[1]);        ;
        Intent cIntent = new Intent(this, MainActivity.class)
                .setAction("answer_c")
                .putExtra(EXTRA_ANSWER, C_CODE)
                .putExtra(EXTRA_QUESTION_ID, id)
                .putExtra(EXTRA_CORRECT, correctArray[2]);

        // Create the pending intents
        PendingIntent aPIntent = PendingIntent.getActivity(this, 0, aIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent bPIntent = PendingIntent.getActivity(this, 0, bIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent cPIntent = PendingIntent.getActivity(this, 0, cIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create and return the Notification Builder
        return new NotificationCompat.Builder(this)
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setBigContentTitle(getString(R.string.new_q))
                                .addLine(curQText)
//                                .setSummaryText(curQText)
                                .addLine(String.format("A) %s", answers[0]))
                                .addLine(String.format("B) %s", answers[1]))
                                .addLine(String.format("C) %s",answers[2])) )
                        .setSmallIcon(R.drawable.notif_pic)
                        .addAction(R.drawable.a_icn, "", aPIntent)
                        .addAction(R.drawable.b_icn, "", bPIntent)
                        .addAction(R.drawable.c_icn, "", cPIntent)
                        .setContentTitle(getString(R.string.new_q))
                        .setContentText(curQText)
                        .setAutoCancel(true);
    }

    public void notify(View v){
        if(Globals.DEBUG) Toast.makeText(this,
                String.valueOf(QuestionDAO.getNumberOfQuestions()),
                Toast.LENGTH_SHORT)
                .show();

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        Question rand_q;
        try{
            rand_q = QuestionDAO.getRandomQuestion();
        } catch(CursorIndexOutOfBoundsException e) {
            //TODO: Figure out what to do when all questions have been correctly answered
            Log.d(TAG, "All questions have been correctly answered");
            rand_q = QuestionDAO.getQuestionList(QuestionDAO.RANDOM_QUERY_FORMAT, 1)[0];
        }

        NotificationCompat.Builder mBuilder = createMCBuilder(rand_q);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        Notification notif = mBuilder.build();
        mNotificationManager.notify(DEFAULT_NOTIFICATIONS_CODE, notif);

//        createQuestions();
    }

    public void createQuestions(){
        String[] array = {"#North", "@South", "#East"};
        Question q1 = Question.create("Which direction is generally down on a map?", array);
        String[] answers = {"@San Francisco", "#Durham", "#Gray"};
        String[] answers2 = {"@Paris", "#Toulouse", "#Orleans"};
        String[] answers3 = {"@IKEA", "#Grand Furniture", "#Zack's Furniture"};
        String[] answers4 = {"@Santa Claus", "#Easter Bunny", "#Father Time"};
        Question q5 = Question.create("What is the capital of France?", answers2);
        Question q2 = Question.create("What city is the best city?", answers);
        Question q3 = Question.create("Where is cheap furniture bought from?", answers3);
        Question q4 = Question.create("Who comes down the chimney on Christmas?", answers4);
        Util.readCSV();
    }

    public Question createQuestionfromJson(String json){
        final Gson gson = new Gson();
        Question q = gson.fromJson(json, Question.class);
        Toast.makeText(this, "Your questions have been saved.", Toast.LENGTH_SHORT).show();
        return q;
    }


    @Override
    public void finish(){
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        super.finish();
    }

    /**
     * Sets the time
     */
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
