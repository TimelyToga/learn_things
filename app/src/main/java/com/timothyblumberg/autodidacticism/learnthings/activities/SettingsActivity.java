package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.fragments.TimePickerFragment;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPack;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPackDAO;
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
    private static LinearLayout qPackLayout;
    private static Button startTimeButton;
    private static Button endTimeButton;


    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    public static void updateTimeButtons(String startTime, String endTime){
        String startText = App.getAppContext().getString(R.string.start_time_button_text);
        String endText = App.getAppContext().getString(R.string.end_time_button_text);
        if(Util.isNotEmpty(startTime)){
            startTimeButton.setText(startText + startTime);
        }

        if(Util.isNotEmpty(endTime)){
            endTimeButton.setText(endText + endTime);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle app exits
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        // View initialization
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

        qPackLayout = (LinearLayout) findViewById(R.id.question_pack_select);
        startTimeButton = (Button) findViewById(R.id.start_time_button);
        endTimeButton = (Button) findViewById(R.id.end_time_button);

        // General initialization
        initQuestionsAndUser();
        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);

        // Set up Settings items
        registerForContextMenu(qPackLayout);
        setupFrequencyList();
        setUpButtons();

        if(!G.DEBUG){
            // If production, hide Debug buttons    
            findViewById(R.id.debug_buttons).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume(){
        setupQuestionPackList();

        super.onResume();
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
            return true;
        } else if(id == R.id.action_add_qpack){
            ViewQPacksActivity.launch(this);
        } else if(id == R.id.action_add_question){
            AddQuestionActivity.launch(this);
        }
        return super.onOptionsItemSelected(item);
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
    public void handleQuestionFrequencySelected(View view) {
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

        int newTime = G.questionFrequencyTimes[position];
        G.curUser.updateNotifTime(newTime);
        AlarmReceiver.reportTimeToNextNotif();

        runBackgroundAnimation();
        scheduleNotif(newTime);
    }

    private void setupQuestionPackList() {
        List<QuestionPack> qPackList = QuestionPackDAO.getQPackList();

        int numPacks = qPackList.size();
        Log.d(TAG, "" + numPacks);
        int layoutSize = qPackLayout.getChildCount() / 2;
        Log.d(TAG, "num_packs: " + numPacks + "layoutSize: " + layoutSize);

        int curChildIndex = 0;
        for(QuestionPack qPack : qPackList){
            if(numPacks >= layoutSize) {
                // data validation
                if(qPack == null) continue;
                if(Util.doesLayoutHaveChild((int) qPack._id, qPackLayout)){
                    // Don't create another checkbox if one already exists for this qPack
                    Log.e(TAG, "Checkbox for: " + qPack.displayName + " exists. DIDN'T CREATE ONE");
                } else {
                    // Make new checkbox
                    final CheckBox checkBox = (CheckBox) getLayoutInflater().inflate(R.layout.template_checkbox, null);
                    checkBox.setText(qPack.displayName);
                    checkBox.setId((int) qPack._id);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            handleCheckBoxClick(checkBox.getText().toString(), isChecked);
                        }
                    });
                    View divider = createDivider();
                    // Add the view
                    qPackLayout.addView(checkBox);
                    qPackLayout.addView(divider);
                }
            } else {
                Log.d(TAG, "SKIPPED BECAUSE LAYOUTSIZE IS GREATER");
            }


            // Set checks
            if(numPacks >= (curChildIndex / 2)){
                View curView = qPackLayout.getChildAt(curChildIndex);
                if(curView.isClickable()){
                    CheckBox curChild = (CheckBox)curView;
                    Log.d(TAG, qPack.qpack_id + " should be checked: " + qPack.active);
                    if(qPack.active.equals(G.TRUE)){
                        curChild.setChecked(true);
                        Log.d(TAG, qPack.qpack_id + " is checked.");
                    } else {
                        curChild.setChecked(false);
                        Log.d(TAG, qPack.qpack_id + " is not checked.");
                    }
                } else {
                    Log.d(TAG, "Got invalid item at index: " + curChildIndex);
                }
                curChildIndex += 2;
            } else {
                Log.d(TAG, "CurChildIndex: " + curChildIndex);
            }

        }


        for(int a = 0; a < layoutSize; a++){
            View curChild = qPackLayout.getChildAt(a);
            int curID = curChild.getId();
            boolean foundID = false;
            for(QuestionPack qPack : qPackList){
                if((int)qPack._id == curID) foundID = true;
            }
            if(!foundID){
                // Remove qPack that no longer exists in DB
                qPackLayout.removeView(curChild);
                Log.d(TAG, "Removing item " + curID);
            } else {
                Log.d(TAG, "Didn't remove " + curID);
            }
        }


    }

    private void handleCheckBoxClick(String displayName, boolean shouldBeChecked){
        QuestionPack questionPack = QuestionPackDAO.getQPackByDisplayName(displayName);
        questionPack.setActive(shouldBeChecked);
        QuestionPackDAO.save(questionPack);

        runBackgroundAnimation();
        scheduleNotif(G.curUser.TIME_UNTIL_NEXT_NOTIFICATION);
    }

    private void setUpButtons(){
        startTimeButton.append(G.curUser.startTime);
        endTimeButton.append(G.curUser.endTime);
    }

    private void setupFrequencyList() {
        RadioGroup askIntensityList = (RadioGroup) findViewById(R.id.askIntensityList);
        int curPosition = 0;

        for (int a = 0; a < G.questionFrequencyNames.length; a++) {
            String curFrequencyTitle = G.questionFrequencyNames[a];
            int curFrequencyTime = G.questionFrequencyTimes[a];

            // Create Views
            RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.template_radio_button, null);
            radioButton.setText(curFrequencyTitle);
            radioButton.setId(curFrequencyTime);
            View divider = createDivider();

            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(p);

            if (curPosition == G.curUser.curListPosition) {
                // should check the correct spot
                radioButton.setChecked(true);
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

    public void showStartTimePickerDialog(View v) {
        Bundle extras = new Bundle();
        extras.putString(G.EXTRA_WHICH_TIME, G.START_TIME);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(extras);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showEndTimePickerDialog(View v) {
        Bundle extras = new Bundle();
        extras.putString(G.EXTRA_WHICH_TIME, G.END_TIME);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(extras);
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
