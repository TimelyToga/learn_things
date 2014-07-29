package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class SettingsActivity extends BaseActivity {

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

        // List View
        setUpListView();

        // General initialization
        initQuestionsAndUser();
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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

    public void resetDB(View v){
        QuestionDAO.resetDB();
        scheduleNotif(Globals.SCHEDULE_NOTIF_DEFAULT_TIME);
    }

    private void setUpListView(){
        frequencyIntensity = (ListView)findViewById(R.id.intensityListView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, Globals.listNames);
        frequencyIntensity.setAdapter(adapter);
        frequencyIntensity.setItemChecked(0, true);
                frequencyIntensity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);
                        view.setActivated(true);
                        runBackgroundAnimation();

                        int newTime = Globals.listDef[position];
                        Globals.curUser.updateNotifTime(newTime);
                        AlarmReceiver.reportTimeToNextNotif();
                        scheduleNotif(newTime);
                    }
                });
    }

    private void runBackgroundAnimation(){
        backgroundAnimator.setDuration(Globals.COLOR_FADE_TIME/2);
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
