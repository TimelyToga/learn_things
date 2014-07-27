package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class SettingsActivity extends BaseActivity {

    private static SeekBar freqencyBar;
    private static TextView frequencyTextView;
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

        //Init values
        frequencyTextView = (TextView)findViewById(R.id.frequencyTextView);
        freqencyBar = (SeekBar)findViewById(R.id.frequencyBar);

        // List View
        setUpListView();

        frequencyTextView.append("" + freqencyBar.getProgress());
        setUpFrequencyBar();

        initQuestionsAndUser();
        scheduleNotif();
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
        scheduleNotif();
    }


    private void setUpFrequencyBar(){
        int i = ((Globals.MILLISECONDS_IN_DAY / Globals.TIME_UNTIL_NEXT_NOTIFICATION))/1000;
        freqencyBar.setProgress(i);
        frequencyTextView.setText(getString(R.string.questions_per_day) + i*1000);
        freqencyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int y = progress*1000;
                frequencyTextView.setText(getString(R.string.questions_per_day) + y);
                // Update time_until next
                Globals.curUser.updateNotifTime(Globals.MILLISECONDS_IN_DAY / (y + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int bigSec = new Integer(Globals.TIME_UNTIL_NEXT_NOTIFICATION) / 1000;
                int minutes = bigSec/60;
                int seconds = bigSec - minutes*60;
                scheduleNotif();
                Toast.makeText(App.getAppContext(),
                        "Time until next question: " + minutes + "m " + seconds + "s",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
                Globals.curUser.updateNotifTime(Globals.listDef[position]);
                view.setActivated(true);
            }
        });
    }

}
