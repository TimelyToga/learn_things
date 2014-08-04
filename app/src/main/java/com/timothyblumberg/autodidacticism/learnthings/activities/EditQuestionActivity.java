package com.timothyblumberg.autodidacticism.learnthings.activities;

/**
 * Created by poboty on 8/4/14.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;


public class EditQuestionActivity extends BaseActivity{

    private static Question curQuestion;
    public static EditText questionTextForm;
    public static EditText questionAnswer1Form;
    public static EditText questionAnswer2Form;
    public static EditText questionAnswer3Form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        Intent i = getIntent();
        curQuestion = QuestionDAO.getQuestionById(i.getStringExtra("Question"));

        questionTextForm = (EditText)findViewById(R.id.edit_question_text);
        questionAnswer1Form = (EditText)findViewById(R.id.edit_question_answer1);
        questionAnswer2Form = (EditText)findViewById(R.id.edit_question_answer2);
        questionAnswer3Form = (EditText)findViewById(R.id.edit_question_answer3);

        fillFields();
    }

    public static void launch(Activity activity, Question question) {
        Intent intent = new Intent(activity, EditQuestionActivity.class).putExtra("Question", question.question_id);
        activity.startActivity(intent);
    }

    private void fillFields() {
        questionTextForm.setText(curQuestion.qText);
        if(!curQuestion.multipleChoice) {
            questionAnswer2Form.setVisibility(View.INVISIBLE);
            questionAnswer3Form.setVisibility(View.INVISIBLE);
            questionAnswer1Form.setText(curQuestion.frAnswerText);
        }
        else {
            questionAnswer1Form.setText(curQuestion.mcAnswerA.substring(1));
            questionAnswer2Form.setText(curQuestion.mcAnswerB.substring(1));
            questionAnswer3Form.setText(curQuestion.mcAnswerC.substring(1));
        }
    }

    public void saveChanges(View view) {
        curQuestion.qText  = questionTextForm.getText().toString();
        if(!curQuestion.multipleChoice) {
            curQuestion.frAnswerText = questionAnswer1Form.getText().toString();
        }
        else {
            curQuestion.mcAnswerA = "@"+questionAnswer1Form.getText().toString();
            curQuestion.mcAnswerB = "#"+questionAnswer2Form.getText().toString();
            curQuestion.mcAnswerC = "#"+questionAnswer3Form.getText().toString();
        }
        QuestionDAO.save(curQuestion);
        Intent i = new Intent(this, ViewQuestionsActivity.class).putExtra(G.EXTRA_QPACK_ID,
                curQuestion.qpack_id);
        startActivity(i);
    }
}
