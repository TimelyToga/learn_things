package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPack;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPackDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewQuestionPacksActivity extends BaseActivity {

    ListView packView;
    ArrayAdapter<String> qPackAdapter;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, ViewQuestionPacksActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question_packs);
        ActionBar actionBar = getActionBar();

        // get view references
        packView = (ListView)findViewById(R.id.question_pack_list);
        List<QuestionPack> qPackList = QuestionPackDAO.getQPackList();
        List<String> qPackNames = new ArrayList<String>();
        for(QuestionPack qPack : qPackList){
            qPackNames.add(qPack.displayName);
        }

        qPackNames.add(0, getString(R.string.create_new_question_pack));
        qPackAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, qPackNames);
        packView.setAdapter(qPackAdapter);
        packView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String qPackName = qPackAdapter.getItem(position);

                if(qPackName.equals(getString(R.string.create_new_question_pack))){
                    // Create new qPack?
                    showCreateQPackDialog();
                } else {
                    // Otherwise, launch ViewQuestionsActivity
                    QuestionPack qPack = QuestionPackDAO.getQPackByDisplayName(qPackName);
                    Intent intent = new Intent(App.getAppContext(), ViewQuestionsActivity.class)
                            .setAction(G.VIEW_QUESTIONS_IN_QUESTION_PACK)
                            .putExtra(G.EXTRA_QPACK_ID, qPack.qpack_id);
                    startActivity(intent);
                }

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_question_pack, menu);
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

    @Override
    public void setUpAlertButtonActions(final EditText qPackText, final EditText qPackDesc){
        // Create and init dialog buttons
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String qPackNameText = qPackText.getText().toString();
                String qPackDescText = qPackDesc.getText().toString();
                String uuid = UUID.randomUUID().toString();
                QuestionPack.createQuestionPack(uuid, qPackNameText, qPackDescText, QuestionPack.THIS_USER_CREATED);
                qPackAdapter.add(qPackNameText);
                qPackAdapter.notifyDataSetChanged();
                int pos = qPackAdapter.getPosition(qPackNameText);
                packView.setAdapter(qPackAdapter);
                packView.setSelection(pos);
                setSelectedQPack(uuid);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
    }

}
