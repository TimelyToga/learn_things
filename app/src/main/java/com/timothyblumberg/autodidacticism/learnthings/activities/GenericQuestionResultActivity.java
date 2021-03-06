package com.timothyblumberg.autodidacticism.learnthings.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.ToastUtil;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;

public class GenericQuestionResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected CountDownTimer makeTimer(boolean isCorrect){
        int timerLength;
        if(isCorrect) timerLength = G.TIMER_COUNTDOWN_LENGTH;
        else timerLength = G.INCORRECT_TIMER_COUNTDOWN_LENGTH;
        return new CountDownTimer(timerLength, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(GenericQuestionResultActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();
            }
        };
    }

    protected void setLayoutTouchListener(RelativeLayout layout){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitTimer.cancel();
                Button exitButton = (Button)findViewById(R.id.exit_button);
                exitButton.setVisibility(View.VISIBLE);
                ToastUtil.showShort("Auto-exit paused");
            }
        });
    }

    public void exitView(View v){
        finish();
    }

    protected ImageView setUpImageView(boolean correct, boolean fromFR){
        ImageView im = new ImageView(App.getAppContext());
        if(correct){
            im.setImageResource(R.drawable.success_icn);
        } else {
            im.setImageResource(R.drawable.failure_icn);
        }
        im.setPadding(5,5,5,5);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if(fromFR) p.addRule(RelativeLayout.BELOW, R.id.fr_correctnessText);
        else p.addRule(RelativeLayout.BELOW, R.id.mc_correctnessText);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        im.setLayoutParams(p);

        return im;
    }

    @Override
    public void finish(){
        if(waitTimer != null) {
            waitTimer.cancel();
            waitTimer = null;
        }

        // Launches WinActivity if necessary
        Question.getQuestionOrHandleWin();

        super.finish();
    }

}
