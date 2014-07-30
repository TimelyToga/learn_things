package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

public class AddQuestionActivity extends BaseActivity {

    public static EditText questionTextForm;
    public static EditText questionAnswer1Form;
    public static EditText questionAnswer2Form;
    public static EditText questionAnswer3Form;

    public static void launch(Activity activity){
        Intent intent = new Intent(activity, AddQuestionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        // Init forms
        questionTextForm = (EditText)findViewById(R.id.ad_question_text);
        questionAnswer1Form = (EditText)findViewById(R.id.ad_question_answer1);
        questionAnswer2Form = (EditText)findViewById(R.id.ad_question_answer2);
        questionAnswer3Form = (EditText)findViewById(R.id.ad_question_answer3);

        setImeListener(questionTextForm);
        setImeListener(questionAnswer1Form);
        setImeListener(questionAnswer2Form);
        setImeListener(questionAnswer3Form);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_question, menu);
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

    public void saveQuestion(View v){
        String qText = questionTextForm.getText().toString();
        String ans1 = questionAnswer1Form.getText().toString();
        String ans2 = questionAnswer2Form.getText().toString();
        String ans3 = questionAnswer3Form.getText().toString();

        if(Util.isNotEmpty(qText) && Util.isNotEmpty(ans1)){
            Question newQuestion;
            // Could be a valid FR question
            if(isNewQuestionFR()){
                // Valid FR, create question obj
                newQuestion = Question.createFR(qText, ans1);
            } else {
                if(isNewQuestionValidMC(ans1, ans2, ans3)){
                    // Valid MC, create question obj
                    String[] ans = {"@" + ans1, "#" + ans2, "#" + ans3};
                    newQuestion = Question.createMC(qText, ans);
                } else {
                    // Invalid question. Alert.
                    Toast.makeText(this,
                            getString(R.string.incorrect_add_question),
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // valid question. Save and clear fields.
            QuestionDAO.save(newQuestion);
            Toast.makeText(this, newQuestion.qText + " " + newQuestion.question_id, Toast.LENGTH_LONG).show();
            clearFields();
        } else {
            if(!Util.isNotEmpty(qText)){
                // No question
                Toast.makeText(this,
                        getString(R.string.no_question_add_question),
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                // No first answer
                Toast.makeText(this,
                        getString(R.string.incorrect_add_question),
                        Toast.LENGTH_LONG).show();
                return;
            }

        }
    }

    private void clearFields(){
        questionTextForm.setText("");
        questionAnswer1Form.setText("");
        questionAnswer2Form.setText("");
        questionAnswer3Form.setText("");
    }

    private boolean isNewQuestionFR(){
        if(!Util.isNotEmpty(questionAnswer2Form.getText().toString()) &&
                !Util.isNotEmpty(questionAnswer3Form.getText().toString())){
            return true;
        }

        return false;
    }

    private boolean isNewQuestionValidMC(String a, String b, String c){
        if(Util.isNotEmpty(a) && Util.isNotEmpty(b) && Util.isNotEmpty(c)){
            return true;
        }

        return false;
    }

    private void setImeListener(final EditText editText){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });
    }
}