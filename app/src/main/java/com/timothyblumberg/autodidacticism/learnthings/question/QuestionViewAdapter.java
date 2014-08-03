package com.timothyblumberg.autodidacticism.learnthings.question;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.timothyblumberg.autodidacticism.learnthings.R;

import java.util.List;

/**
 * Created by Tim on 8/3/14.
 */
public class QuestionViewAdapter extends ArrayAdapter<Question> {

    public QuestionViewAdapter(Context context, int resource, List<Question> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.question_list_item, null);
        }

        Question question = getItem(position);
        if (question != null) {
            TextView qText = (TextView) v.findViewById(R.id.question_list_text);
            TextView correctAnswer = (TextView) v.findViewById(R.id.question_list_answer);

            if (qText != null) qText.setText(question.qText);
            if (correctAnswer != null) correctAnswer.setText(question.getCorrectAnswer());

            return v;
        }

        return super.getView(position, convertView, parent);
    }
}
