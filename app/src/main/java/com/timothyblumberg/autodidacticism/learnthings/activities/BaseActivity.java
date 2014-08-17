package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionFactory;
import com.timothyblumberg.autodidacticism.learnthings.user.User;
import com.timothyblumberg.autodidacticism.learnthings.user.UserDAO;

import java.util.Calendar;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends FragmentActivity {

    private static final String TAG = MCActivity.class.getSimpleName();
    protected CountDownTimer waitTimer;
    protected ViewGroup mViewGroup;
    protected AlertDialog.Builder alert;
    public static String selectedQPackID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/RobotoSlab-Light.ttf", R.attr.fontPath);
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    // <editor-fold desc="Options Menus">
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
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

    //</editor-fold>


    protected void scheduleNotif(int timeUntilNextNotif) {
        if(timeUntilNextNotif < 0){
            timeUntilNextNotif = G.curUser.TIME_UNTIL_NEXT_NOTIFICATION;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + timeUntilNextNotif,
                alarmIntent);
    }

    public static void initQuestionsAndUser(){
        // Initialize questions and User obj
        if(QuestionDAO.getTotalNumberOfQuestions() == 0){
            QuestionFactory.initQuestionDBFromCSV();
        }
        try{
            G.curUser = UserDAO.testUserExistence();
            Log.d(TAG, "Got user: " + G.curUser.getUserId());
        } catch (CursorIndexOutOfBoundsException e){
            // If nothing is found, createMC the user
            Log.d(TAG, "\n\n\nCreating user\n\n\n");
            G.curUser = User.create();
        }
    }

    @Override
    public void finish(){
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        // Launch WinActivity if necessary MAY NEED TO READD

        super.finish();
    }

    public View createDivider(){
        View divider = new View(App.getAppContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 4);
        divider.setLayoutParams(p);
        divider.setBackgroundColor(getResources().getColor(R.color.darker_gray));

        return divider;
    }

    protected void showCreateQPackDialog(){
        // Init dialog
        alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.create_new_question_pack));
        alert.setMessage(getString(R.string.create_question_pack_dialog_message));

        // Set an EditText view to get user input
        final EditText qPackName = (EditText)getLayoutInflater().inflate(R.layout.template_edit_text, null);
        qPackName.setHint(R.string.create_question_dialog_qpack_name);
        final EditText qPackDesc = (EditText)getLayoutInflater().inflate(R.layout.template_edit_text, null);
        qPackDesc.setHint(R.string.create_question_dialog_qpack_desc);

        // Create container view and add EditTexts
        final LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(qPackName);
        container.addView(qPackDesc);
        alert.setView(container);

        setUpAlertButtonActions(qPackName, qPackDesc);

        alert.show();
    }


    public void setUpAlertButtonActions(final EditText qPackText, final EditText qPackDesc){

    }

    protected void setSelectedQPackID(String newQPackID){
        selectedQPackID = newQPackID;
    }







}
