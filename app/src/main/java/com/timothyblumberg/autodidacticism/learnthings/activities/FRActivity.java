package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class FRActivity extends BaseActivity {

    private final String TAG = FRActivity.class.getSimpleName();
    private static RelativeLayout frLayout;
    private static TextView correctnessText;
    private static TextView answerText;
    private static ImageView resultImage;

    private static App sApp;

    public static Question curQuestion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr);

        // Get important references
        frLayout = (RelativeLayout)findViewById(R.id.fr_layout);
        correctnessText = (TextView)findViewById(R.id.fr_correctnessText);
        resultImage = (ImageView)findViewById(R.id.mc_correctnessImg);
        answerText = (TextView)findViewById(R.id.fr_answerText);
        sApp = App.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isFR = extras.getBoolean(Globals.EXTRA_IS_FR);
            String question_id = extras.getString(Globals.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);
            Log.d(TAG, "FR: " + String.valueOf(isFR));

            correctnessText.setText(getText(R.string.answer) + curQuestion.frAnswerText);

        }  else if(Globals.DEBUG){
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Globals.DEFAULT_NOTIFICATIONS_CODE);


        // Initialization in BaseActivity
        setLayoutTouchListener(frLayout);
        waitTimer = makeTimer();
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
            SettingsActivity.launch(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void correctClick(View v){
        waitTimer.start();
        resultImage.setImageResource(R.drawable.success_icn);
        curQuestion.setOutcome(true);
        correctnessText.setText("YAY! Correct!");
        answerText.setText(String.format(getString(R.string.answer_, curQuestion.getCorrectAnswer())));
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);

    }

    public void incorrectClick(View v){
        waitTimer.start();
        resultImage.setImageResource(R.drawable.failure_icn);
        curQuestion.setOutcome(false);
        answerText.setText(String.format(getString(R.string.answer_, curQuestion.getCorrectAnswer())));
        correctnessText.setText("Don't worry, we'll ask you again later.");
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);
    }

}
