package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.ToastUtil;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPack;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPackDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestionsActivity extends BaseActivity {

    public static QuestionPack curQPack;
    public static ListView questionListView;
    public static QuestionViewAdapter questionAdapter;
    public static List<Question> questionList;
    public static List<String> questionTextList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String qPackID = extras.getString(G.EXTRA_QPACK_ID);
            curQPack = QuestionPackDAO.getQPackById(qPackID);
            getActionBar().setTitle(curQPack.displayName);
            questionList = QuestionDAO.getQuestionForQuestionPack(qPackID);
            questionTextList = new ArrayList<String>();

            for(Question q : questionList){
                questionTextList.add(q.qText);
            }

            questionListView = (ListView)findViewById(R.id.question_list);
            questionAdapter = new QuestionViewAdapter(this, R.layout.question_list_item, questionList);
            questionListView.setAdapter(questionAdapter);
            questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Question curQuestion = questionAdapter.getItem(position);
                    ToastUtil.showShort(curQuestion.getCorrectAnswer());
                }
            });

            registerForContextMenu(questionListView);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo info){
        super.onCreateContextMenu(menu, view, info);

        getMenuInflater().inflate(R.menu.view_questions_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Question curQuestion = questionAdapter.getItem(info.position);
        switch (item.getItemId()) {
            case R.id.action_edit:
                ToastUtil.showShort("Editing Question");
                return true;
            case R.id.action_delete:
                ToastUtil.showShort("Deleting Question: " + curQuestion.question_id);
                questionAdapter.remove(curQuestion);
                QuestionDAO.deleteQuestion(curQuestion);
                questionAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_questions, menu);
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
        } else if (id == R.id.action_add_question){
            AddQuestionActivity.launch(this, curQPack.displayName);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
