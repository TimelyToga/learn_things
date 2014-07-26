package com.timothyblumberg.autodidacticism.learnthings;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
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

public class FRActivity extends BaseActivity {

    private final String TAG = FRActivity.class.getSimpleName();
    private static RelativeLayout frLayout;
    private static TextView wordTextView;
    private static TextView timerTextView;
    private static ImageView questionResult;

    private static App sApp;

    public static Question curQuestion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr);

        // Get important references
        frLayout = (RelativeLayout)findViewById(R.id.fr_layout);
        wordTextView = (TextView)findViewById(R.id.fr_resultText);
        timerTextView = (TextView)findViewById(R.id.fr_timer);
        questionResult = (ImageView)findViewById(R.id.fr_questionResult);
        sApp = App.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isFR = extras.getBoolean(Globals.EXTRA_IS_FR);
            String question_id = extras.getString(Globals.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);
            Log.d(TAG, "FR: " + String.valueOf(isFR));

            wordTextView.setText(getText(R.string.answer) + curQuestion.frAnswerText);

        }  else if(Globals.DEBUG){
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Globals.DEFAULT_NOTIFICATIONS_CODE);


        // Initialization in BaseActivity
        setLayoutTouchListener(frLayout);
        waitTimer = makeTimer(timerTextView);
        initQuestionsAndUser();
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
        questionResult.setImageResource(R.drawable.success_icn);
        curQuestion.setOutcome(true);
        wordTextView.setText("YAY! Correct!");
        scheduleNotif();

    }

    public void incorrectClick(View v){
        waitTimer.start();
        questionResult.setImageResource(R.drawable.failure_icn);
        curQuestion.setOutcome(false);
        wordTextView.setText("Don't worry, we'll ask you again later.");
        scheduleNotif();
    }

}
