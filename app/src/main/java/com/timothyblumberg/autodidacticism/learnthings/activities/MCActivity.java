package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;


public class MCActivity extends BaseActivity{

    private final String TAG = MCActivity.class.getSimpleName();
    public static App sApp;
    public static TextView correctnessText;
    public static TextView answerText;
    private static RelativeLayout mainLayout;
    private static RelativeLayout cardLayout;
    private static Question curQuestion;

    // extras
    private static boolean correct;
    private static int answer_code;
    private static String yourAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mc);

        // Get important references
        correctnessText = (TextView)findViewById(R.id.mc_correctnessText);
        answerText = (TextView)findViewById(R.id.mc_answerText);
        mainLayout = (RelativeLayout)findViewById(R.id.mc_layout);
        cardLayout = (RelativeLayout)findViewById(R.id.mc_9_card);
        mViewGroup = cardLayout;
        sApp = App.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isFR = extras.getBoolean(G.EXTRA_IS_FR);
            String question_id = extras.getString(G.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);
            Log.d(TAG, "FR: " + String.valueOf(isFR));

            answer_code = extras.getInt(G.EXTRA_ANSWER);
            correct = extras.getBoolean(G.EXTRA_CORRECT);
            yourAnswer = extras.getString(G.EXTRA_YOUR_ANSWER);

            curQuestion.setOutcome(correct);

            if(G.DEBUG){
                correctnessText.append("\nViews: " + curQuestion.numberAsks);
                Question[] qList = QuestionDAO.getQuestionArray(QuestionDAO.RATIO_QUERY_FORMAT, 5);
                for(Question q : qList){
                    if(q.numberAsks > 0){
                        correctnessText.append(q.qText + " -- " + ((double) q.num_correct / (double) (q.num_incorrect + 1)) + "\n");
                    }
                }
            }


        }  else {
            if(G.DEBUG) Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
            correctnessText.setText("Welcome to LearnThings!");
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(G.DEFAULT_NOTIFICATIONS_CODE);

        // Initialization in BaseActivity
        setLayoutTouchListener(mainLayout);
        makeAnimateTimer().start();
        waitTimer = makeTimer().start();
        initQuestionsAndUser();
        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);
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
     * Prepares the view with the conditions of a question's answering
     * @param answer_code
     * @param correct
     */
    private void setViewResult(int answer_code, boolean correct, String yourAnswer) {
        // Sets the answer you gave without the leading character
        switch (answer_code) {
            case G.A_CODE:
                correctnessText.setText(String.format(getString(R.string.you_answered), "A", yourAnswer));
                break;
            case G.B_CODE:
                correctnessText.setText(String.format(getString(R.string.you_answered), "B", yourAnswer));
                break;
            case G.C_CODE:
                correctnessText.setText(String.format(getString(R.string.you_answered), "C", yourAnswer));
                break;
        }
        ImageView im = setUpImageView(correct, false); // false because mc not fr
        mViewGroup.removeView(findViewById(R.id.mc_resultImg));
        im.setId(R.id.mc_resultImg);
        mViewGroup.addView(im);

        if (!correct) {
            answerText.setText(String.format(getString(R.string.answer_),
                    curQuestion.getCorrectAnswer()));
        }
    }

    private void runBackgroundAnimation(boolean correct){
        ObjectAnimator backgroundAnimator;
        if(correct){
            backgroundAnimator = ObjectAnimator.ofObject(mainLayout,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    getResources().getColor(R.color.light_background_blue),
                    getResources().getColor(R.color.light_background_green));
        }
        else{
            backgroundAnimator = ObjectAnimator.ofObject(mainLayout,
                    "backgroundColor",
                    new ArgbEvaluator(),
                    getResources().getColor(R.color.light_background_blue),
                    getResources().getColor(R.color.light_background_red));
        }

        // start
        backgroundAnimator.setDuration(G.COLOR_FADE_TIME);
        backgroundAnimator.start();
    }

    private CountDownTimer makeAnimateTimer(){
        return new CountDownTimer(G.ANIMATION_TIMER_LENGTH, G.ANIMATION_TIMER_LENGTH) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                setViewResult(answer_code, correct, yourAnswer);
                runBackgroundAnimation(correct);
            }
        };
    }

}
