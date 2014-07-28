package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.AlarmReceiver;
import com.timothyblumberg.autodidacticism.learnthings.common.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class SettingsActivity extends BaseActivity {

    private static ListView frequencyIntensity;
    private static ArrayAdapter<String> adapter;

    public static void launch(Activity activity){
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

                int newTime = Globals.listDef[position];
                Globals.curUser.updateNotifTime(newTime);
                AlarmReceiver.reportTimeToNextNotif();
                scheduleNotif(newTime);
            }
        });
    }

}
