package com.timothyblumberg.autodidacticism.learnthings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class SettingsActivity extends BaseActivity {

    private static SeekBar freqencyBar;
    private static TextView frequencyTextView;
    private static NumberPicker minutesUntilNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Init values
        frequencyTextView = (TextView)findViewById(R.id.frequencyTextView);
        freqencyBar = (SeekBar)findViewById(R.id.frequencyBar);
        minutesUntilNext = (NumberPicker)findViewById(R.id.minutesUntilNext);

        frequencyTextView.append("" + freqencyBar.getProgress());
        setUpFrequencyBar();
        setUpNumberPicker();

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
        freqencyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int x = progress;
                int y = (int) (((x*x)/200)*Math.log10(x));
                frequencyTextView.setText(getString(R.string.questions_per_day) + y);
                // Update time_until next
                Globals.TIME_UNTIL_NEXT_NOTIFICATION = Globals.MILLISECONDS_IN_DAY / (y + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int bigSec = new Integer(Globals.TIME_UNTIL_NEXT_NOTIFICATION) / 1000;
                int minutes = bigSec/60;
                int seconds = bigSec - minutes*60;
                Toast.makeText(App.getAppContext(),
                        "Time until next question: " + minutes + "m " + seconds + "s",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpNumberPicker(){
        minutesUntilNext.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Globals.TIME_UNTIL_NEXT_NOTIFICATION = newVal*60000;
            }
        });
    }
}
