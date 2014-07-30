package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private static ObjectAnimator backgroundAnimator;

    private static App sApp;

    public static Question curQuestion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr);

        // Get important references
        frLayout = (RelativeLayout)findViewById(R.id.fr_layout);
        correctnessText = (TextView)findViewById(R.id.fr_correctnessText);
        answerText = (TextView)findViewById(R.id.fr_answerText);
        sApp = App.getInstance();

        mViewGroup = frLayout;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String question_id = extras.getString(Globals.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);

        }  else if(Globals.DEBUG){
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Globals.DEFAULT_NOTIFICATIONS_CODE);


        // Initialization in BaseActivity
        makeAnimateTimer().start();
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
        // Set up timer Business
        setLayoutTouchListener(frLayout);
        waitTimer.start();

        runBackgroundAnimation(true);
        curQuestion.setOutcome(true);
        correctnessText.setText("YAY! Correct!");
        answerText.setText(String.format(getString(R.string.answer_, curQuestion.getCorrectAnswer())));
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);
        ImageView im = setUpImageView(true, true);

        // ADD / REMOVE VIEWS
        mViewGroup.addView(im);
        findViewById(R.id.fr_correctButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.fr_incorrectButton).setVisibility(View.INVISIBLE);
    }

    public void incorrectClick(View v){
        // Set up timer Business
        setLayoutTouchListener(frLayout);
        waitTimer.start();

        runBackgroundAnimation(false);
        curQuestion.setOutcome(false);
        answerText.setText(String.format(getString(R.string.answer_, curQuestion.getCorrectAnswer())));
        correctnessText.setText("Don't worry, we'll ask you again later.");
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);
        ImageView im = setUpImageView(false, true);

        // ADD / REMOVE VIEWS
        mViewGroup.addView(im);
        findViewById(R.id.fr_correctButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.fr_incorrectButton).setVisibility(View.INVISIBLE);
    }

    public void initViewForAnimation(){
        TextView t = new TextView(App.getAppContext());
        mViewGroup.addView(t);
        correctnessText.setText(getText(R.string.answer) + curQuestion.frAnswerText);
    }

    private void runBackgroundAnimation(boolean correct){
        if(correct){
            backgroundAnimator = ObjectAnimator.ofObject(frLayout,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    getResources().getColor(R.color.light_background_blue),
                    getResources().getColor(R.color.light_background_green));
        }
        else{
            backgroundAnimator = ObjectAnimator.ofObject(frLayout,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    getResources().getColor(R.color.light_background_blue),
                    getResources().getColor(R.color.light_background_red));
        }

        // start
        backgroundAnimator.setDuration(Globals.COLOR_FADE_TIME);
        backgroundAnimator.start();
    }

    private CountDownTimer makeAnimateTimer(){
        return new CountDownTimer(Globals.ANIMATION_TIMER_LENGTH, Globals.ANIMATION_TIMER_LENGTH) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                initViewForAnimation();
            }
        };
    }

}
