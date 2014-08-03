package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPack;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionPackDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddQuestionActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private final String TAG = AddQuestionActivity.class.getSimpleName();

    public static EditText questionTextForm;
    public static EditText questionAnswer1Form;
    public static EditText questionAnswer2Form;
    public static EditText questionAnswer3Form;
    public static Spinner qPackSpinner;
    public static ArrayAdapter<String> adapter;

    private static boolean userClick;

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
        qPackSpinner = (Spinner)findViewById(R.id.q_pack_spinner);


        setImeListener(questionTextForm);
        setImeListener(questionAnswer1Form);
        setImeListener(questionAnswer2Form);
        setImeListener(questionAnswer3Form);
        setUpSpinner();
        userClick = false;

    }

    private void setUpSpinner() {
        List<QuestionPack> qPackList = QuestionPackDAO.getQPackList();
        List<String> qPackNames = new ArrayList<String>();
        qPackNames.add(getString(R.string.create_new_question_pack));
        for(QuestionPack qPack : qPackList){
            qPackNames.add(qPack.displayName);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                qPackNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qPackSpinner.setAdapter(adapter);
        qPackSpinner.setOnItemSelectedListener(this);
        qPackSpinner.setSelection(1);
    }

    public void saveQuestion(View v){
        String qText = questionTextForm.getText().toString();
        String ans1 = questionAnswer1Form.getText().toString();
        String ans2 = questionAnswer2Form.getText().toString();
        String ans3 = questionAnswer3Form.getText().toString();

        if(!Util.isNotEmpty(selectedQPack)){

            String message = getString(R.string.select_valid_question_pack);

        } else {
            if(Util.isNotEmpty(qText) && Util.isNotEmpty(ans1)){
                Question newQuestion;
                // Could be a valid FR question
                if(isNewQuestionFR()){
                    // Valid FR, create question obj
                    newQuestion = Question.createFR(qText, ans1, selectedQPack);
                } else {
                    if(isNewQuestionValidMC(ans1, ans2, ans3)){
                        // Valid MC, create question obj
                        String[] ans = {"@" + ans1, "#" + ans2, "#" + ans3};
                        newQuestion = Question.createMC(qText, ans, selectedQPack);
                    } else {
                        // Invalid question. Alert.
                        Toast.makeText(this,
                                getString(R.string.incorrect_add_question),
                                Toast.LENGTH_LONG).show();
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
                if(actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_NEXT) {
                    makeNextVisible();
                    handled = true;
                }

                return handled;
            }
        });

    }

    public void makeNextVisible(){
        if(questionTextForm.hasFocus() && Util.isNotEmpty(questionTextForm.getText().toString())) {
            questionAnswer1Form.setVisibility(View.VISIBLE);
            questionAnswer1Form.requestFocus();
            return;
        }

        if(questionAnswer1Form.hasFocus() && Util.isNotEmpty(questionAnswer1Form.getText().toString())) {
            questionAnswer2Form.setVisibility(View.VISIBLE);
            questionAnswer2Form.requestFocus();
            return;
        }

        if(questionAnswer2Form.hasFocus() && Util.isNotEmpty(questionAnswer2Form.getText().toString())) {
            questionAnswer3Form.setVisibility(View.VISIBLE);
            questionAnswer3Form.requestFocus();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(userClick){
            Log.d(TAG, "selected item");
            String selectedDisplayName = qPackSpinner.getItemAtPosition(position).toString();
            if(selectedDisplayName.equals(getString(R.string.create_new_question_pack))){
                // Create new question pack
                showCreateQPackDialog();
            } else {
                // Otherwise, set selection and allow question creation
                QuestionPack qPack = QuestionPackDAO.getQPackByDisplayName(selectedDisplayName);
                selectedQPack = qPack.getQPackId();
                questionTextForm.setVisibility(View.VISIBLE);
            }
        }
        userClick = true;

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
                adapter.add(qPackNameText);
                adapter.notifyDataSetChanged();
                int pos = adapter.getPosition(qPackNameText);
                qPackSpinner.setAdapter(adapter);
                qPackSpinner.setSelection(pos);
                setSelectedQPack(uuid);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
}
