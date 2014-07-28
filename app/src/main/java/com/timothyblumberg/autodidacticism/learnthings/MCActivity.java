package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;


public class MCActivity extends BaseActivity{

    private final String TAG = MCActivity.class.getSimpleName();
    public static App sApp;
    public static TextView correctnessText;
    public static TextView answerText;
    public static ImageView questionResult;
    private static RelativeLayout mainLayout;

    private static Question curQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mc);

        // Get important references
        correctnessText = (TextView)findViewById(R.id.mc_correctnessText);
        answerText = (TextView)findViewById(R.id.mc_answerText);
        questionResult = (ImageView)findViewById(R.id.mc_resultImg);
        mainLayout = (RelativeLayout)findViewById(R.id.mc_layout);
        sApp = App.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isFR = extras.getBoolean(Globals.EXTRA_IS_FR);
            String question_id = extras.getString(Globals.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);
            Log.d(TAG, "FR: " + String.valueOf(isFR));

            int answer_code = extras.getInt(Globals.EXTRA_ANSWER);
            boolean correct = extras.getBoolean(Globals.EXTRA_CORRECT);
            String yourAnswer = extras.getString(Globals.EXTRA_YOUR_ANSWER);

            curQuestion.setOutcome(correct);
            setViewResult(answer_code, correct, yourAnswer);

            if(Globals.DEBUG){
                correctnessText.append("\nViews: " + curQuestion.numberAsks);
                Question[] qList = QuestionDAO.getQuestionList(QuestionDAO.RATIO_QUERY_FORMAT, 5);
                for(Question q : qList){
                    if(q.numberAsks > 0){
                        correctnessText.append(q.qText + " -- " + ((double) q.num_correct / (double) (q.num_incorrect + 1)) + "\n");
                    }
                }
            }


        }  else {
            if(Globals.DEBUG) Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
            correctnessText.setText("Welcome to LearnThings!");
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Globals.DEFAULT_NOTIFICATIONS_CODE);

        // Initialization in BaseActivity
        setLayoutTouchListener(mainLayout);
        waitTimer = makeTimer().start();
        initQuestionsAndUser();
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);
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
            SettingsActivity.launch(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method creates a Notification builder from the specified Question obj
     * @param question A simple string with the question (? included)
     * @return NotificationCompat.Builder to createMC the notifs
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
        Intent aIntent = new Intent(this, MCActivity.class)
                .setAction("answer_a")
                .putExtra(Globals.EXTRA_ANSWER, Globals.A_CODE)
                .putExtra(Globals.EXTRA_QUESTION_ID, id)
                .putExtra(Globals.EXTRA_CORRECT, correctArray[0]);
        Intent bIntent = new Intent(this, MCActivity.class)
                .setAction("answer_b")
                .putExtra(Globals.EXTRA_ANSWER, Globals.B_CODE)
                .putExtra(Globals.EXTRA_QUESTION_ID, id)
                .putExtra(Globals.EXTRA_CORRECT, correctArray[1]);        ;
        Intent cIntent = new Intent(this, MCActivity.class)
                .setAction("answer_c")
                .putExtra(Globals.EXTRA_ANSWER, Globals.C_CODE)
                .putExtra(Globals.EXTRA_QUESTION_ID, id)
                .putExtra(Globals.EXTRA_CORRECT, correctArray[2]);

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
        Intent resultIntent = new Intent(this, MCActivity.class);
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
        stackBuilder.addParentStack(MCActivity.class);
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
        mNotificationManager.notify(Globals.DEFAULT_NOTIFICATIONS_CODE, notif);

//        createQuestions();
    }

    /**
     * Prepares the view with the conditions of a question's answering
     * @param answer_code
     * @param correct
     */
    private void setViewResult(int answer_code, boolean correct, String yourAnswer) {
        // Sets the answer you gave without the leading character
        switch (answer_code) {
            case Globals.A_CODE:
                correctnessText.setText(String.format(getString(R.string.you_answered), "A", yourAnswer));
                break;
            case Globals.B_CODE:
                correctnessText.setText(String.format(getString(R.string.you_answered), "B", yourAnswer));
                break;
            case Globals.C_CODE:
                correctnessText.setText(String.format(getString(R.string.you_answered), "C", yourAnswer));
                break;
        }

        if (correct) {
            questionResult.setImageResource(R.drawable.success_icn);
        } else {
            questionResult.setImageResource(R.drawable.failure_icn);
            answerText.setText(String.format(getString(R.string.answer_),
                    curQuestion.getCorrectAnswer()));
        }
    }

}
