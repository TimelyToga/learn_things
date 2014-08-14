package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.ToastUtil;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPack;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPackDAO;

import java.util.List;

public class WinActivity extends BaseActivity {

    public static void launch(Context context){
        // Debug
        G.TEST_COUNTER += 1;
        ToastUtil.showShort("Launched WinActivity " + G.TEST_COUNTER);

        Intent intent = new Intent(context, WinActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.win, menu);
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

    public void handleAgain(View v){
        ToastUtil.showShort("Handling Again.");
        Util.toggleCurTrueFalse();
        scheduleNotif(G.SCHEDULE_NOTIF_DEFAULT_TIME);
        finish();
    }

    public void handleDeactivate(View v){
        ToastUtil.showShort("Handle Deactivate");
        List<QuestionPack> activeQPacks = QuestionPackDAO.getActiveQPackList();
        for(QuestionPack qPack : activeQPacks){
            qPack.setActive(false);
            QuestionPackDAO.save(qPack);
        }

        // Launch new so that checkboxes update
        SettingsActivity.launch(this);
        finish();
    }
}
