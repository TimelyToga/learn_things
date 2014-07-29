package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.Globals;
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
                App.getAppContext().getSystemService(NOTIFICATION_SERVICE);
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
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background_green));
            questionResult.setImageResource(R.drawable.success_icn);
        } else {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.light_background_red));
            questionResult.setImageResource(R.drawable.failure_icn);
            answerText.setText(String.format(getString(R.string.answer_),
                    curQuestion.getCorrectAnswer()));
        }
    }

}
