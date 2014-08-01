package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.user.User;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity {

    @Inject
    User curUser;

    private static ListView frequencyIntensity;
    private static ArrayAdapter<String> adapter;
    private static ObjectAnimator backgroundAnimator;
    private static ObjectAnimator reverseAnimator;
    private static RelativeLayout settingsMainLayout;

    public static void launch(Activity activity){
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
        settingsMainLayout = (RelativeLayout)findViewById(R.id.settings_layout);
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
        setUpListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Override BaseActivity and don't display options menu
        return true;
    }

    // @onClick Reset DB
    public void resetDB(View v){
        QuestionDAO.resetDB();
        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);
    }

    // @onClick Add Questions Button
    public void launchAddQuestions(View v){
        AddQuestionActivity.launch(this);
    }

    private void setUpListView(){
        frequencyIntensity = (ListView)findViewById(R.id.intensityListView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, G.listNames);
        frequencyIntensity.setAdapter(adapter);
        frequencyIntensity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setActivated(true);
                G.curUser.setCurListPosition(position);
                runBackgroundAnimation();

                int newTime = G.listDef[position];
                G.curUser.updateNotifTime(newTime);
                AlarmReceiver.reportTimeToNextNotif();
                scheduleNotif(newTime);
            }
        });

    }

    private void runBackgroundAnimation(){
        backgroundAnimator.setDuration(G.COLOR_FADE_TIME/2);
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
