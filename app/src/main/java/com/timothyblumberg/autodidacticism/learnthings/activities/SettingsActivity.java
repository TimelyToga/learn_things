package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.user.User;

import java.util.List;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity {

    @Inject
    User curUser;

    public static final String TAG = SettingsActivity.class.getSimpleName();

    private static ArrayAdapter<String> adapter;
    private static ObjectAnimator backgroundAnimator;
    private static ObjectAnimator reverseAnimator;
    private static RelativeLayout settingsMainLayout;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set Action Bar title to different from app launcher label
        getActionBar().setTitle(getString(R.string.title_activity_settings));

        // background animation
        settingsMainLayout = (RelativeLayout) findViewById(R.id.settings_layout);
        backgroundAnimator = ObjectAnimator.ofObject(settingsMainLayout,
                "backgroundColor",
                new ArgbEvaluator(),
                getResources().getColor(R.color.light_background_blue),
                getResources().getColor(R.color.light_background_green));
        reverseAnimator = ObjectAnimator.ofObject(settingsMainLayout,
                "backgroundColor",
                new ArgbEvaluator(),
                getResources().getColor(R.color.light_background_green),
                getResources().getColor(R.color.light_background_blue));

        // General initialization
        initQuestionsAndUser();
        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);

        // List View
        setupFrequencyList();
    }

    @Override
    public void onResume(){
        super.onResume();
        setupQuestionPackList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Override BaseActivity and don't display options menu
        return true;
    }

    // @onClick Reset DB
    public void resetDB(View v) {
        QuestionDAO.resetDB();
        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);
    }

    // @onClick Add Questions Button
    public void launchAddQuestions(View view) {
        AddQuestionActivity.launch(this);
    }

    // @onClick frequency radio button
    public void onQuestionFrequencySelected(View view) {
        int number = view.getId();
        int position = 0;
        for (int a = 0; a < G.questionFrequencyTimes.length; a++) {
            if (G.questionFrequencyTimes[a] == number) {
                position = a;
                break;
            }
        }

        view.setActivated(true);
        G.curUser.setCurListPosition(position);
        runBackgroundAnimation();

        int newTime = G.questionFrequencyTimes[position];
        G.curUser.updateNotifTime(newTime);
        AlarmReceiver.reportTimeToNextNotif();
        scheduleNotif(newTime);
    }

    private void setupQuestionPackList() {
        LinearLayout qPackLayout = (LinearLayout) findViewById(R.id.question_pack_select);
        List<String> qPack = QuestionDAO.getQuestionPacks();

        int numPacks = QuestionDAO.getQuestionPacks().size() - 1;
        int layoutSize = qPackLayout.getChildCount() / 2;
        Log.d(TAG, "num_packs: " + numPacks + "layoutSize: " + layoutSize);

        if(numPacks > layoutSize){
            for(String s : qPack){
                // data validation
                if(s == null) continue;

                CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.template_checkbox, null);
                checkBox.setText(s);
                View divider = createDivider();

                // Add the view
                qPackLayout.addView(checkBox);
                qPackLayout.addView(divider);
            }

        }


    }


    private void setupFrequencyList() {
        RadioGroup askIntensityList = (RadioGroup) findViewById(R.id.askIntensityList);
        int curPosition = 0;

        for (int a = 0; a < G.questionFrequencyNames.length; a++) {
            String curFrequencyTitle = G.questionFrequencyNames[a];
            int curFrequencyTime = G.questionFrequencyTimes[a];

            // Create Views
            //TODO: Make RadioButton views stretch all the way across the view
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.template_radio_button, null);
            radioButton.setText(curFrequencyTitle);
            radioButton.setId(curFrequencyTime);
            View divider = createDivider();

            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(p);

            if (curPosition == G.curUser.curListPosition) {
                // should check the correct spot
                radioButton.setSelected(true);
            }

            // Add the views
            askIntensityList.addView(radioButton);
            askIntensityList.addView(divider);

            curPosition++;
        }

    }

    private void runBackgroundAnimation() {
        backgroundAnimator.setDuration(G.COLOR_FADE_TIME / 2);
        reverseAnimator.setDuration(backgroundAnimator.getDuration());
        backgroundAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                reverseAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        backgroundAnimator.start();
    }

}
