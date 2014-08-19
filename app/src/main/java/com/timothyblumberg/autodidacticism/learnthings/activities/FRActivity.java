package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class FRActivity extends GenericQuestionResultActivity {

    private final String TAG = FRActivity.class.getSimpleName();
    private static RelativeLayout mainLayout;
    private static RelativeLayout cardLayout;
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
        mainLayout = (RelativeLayout)findViewById(R.id.fr_layout);
        cardLayout = (RelativeLayout)findViewById(R.id.fr_9_card);
        correctnessText = (TextView)findViewById(R.id.fr_correctnessText);
        answerText = (TextView)findViewById(R.id.fr_answerText);
        sApp = App.getInstance();
        mViewGroup = cardLayout;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String question_id = extras.getString(G.EXTRA_QUESTION_ID);
            curQuestion = QuestionDAO.getQuestionById(question_id);

        }  else if(G.DEBUG){
            Toast.makeText(this, "No extras", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager)
                App.getAppContext().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(G.DEFAULT_NOTIFICATIONS_CODE);


        // Initialization in BaseActivity
        makeAnimateTimer().start();
        initQuestionsAndUser();
    }

    public void correctClick(View v){
        performGenericResultButtonClick(true);
        correctnessText.setText(getString(R.string.yay_correct));
        answerText.setText(String.format(getString(R.string.answer_), curQuestion.getCorrectAnswer()));
    }

    public void incorrectClick(View v){
        performGenericResultButtonClick(false);
        answerText.setText(String.format(getString(R.string.answer_), curQuestion.getCorrectAnswer()));
        correctnessText.setText(getString(R.string.ask_again));
    }

    /**
     * This method performs all the actions that must be performed whenever a question is self-graded
     * @param correct
     */
    public void performGenericResultButtonClick(boolean correct){
        // Set up timer Business
        setLayoutTouchListener(mainLayout);
        waitTimer = makeTimer(correct).start();

        runBackgroundAnimation(correct);
        curQuestion.setOutcome(correct);

        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);
        ImageView im = setUpImageView(correct, true);
        mViewGroup.removeView(findViewById(R.id.fr_resultImg));
        im.setId(R.id.fr_resultImg);

        // ADD / REMOVE VIEWS
        mViewGroup.addView(im);
        findViewById(R.id.fr_correctButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.fr_incorrectButton).setVisibility(View.INVISIBLE);


    }

    /**
     * A hack to make animations work. Adds a blank textView and changes the text
     */
    public void initViewForAnimation(){
        TextView t = new TextView(App.getAppContext());
        mViewGroup.addView(t);
        correctnessText.setText(getText(R.string.answer) + curQuestion.getCorrectAnswer());
    }

    /**
     * Initialize the correct background transition based on the question's success or failure
     * @param correct
     */
    private void runBackgroundAnimation(boolean correct){
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

    /**
     * A hack to make the initial answer text animate. A timer is started that waits 100 ms before
     * setting the text, so that the view has already been created and will then animate.
     * @return
     */
    private CountDownTimer makeAnimateTimer(){
        return new CountDownTimer(G.ANIMATION_TIMER_LENGTH, G.ANIMATION_TIMER_LENGTH) {
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
