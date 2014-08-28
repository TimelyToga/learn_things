package com.timothyblumberg.autodidacticism.learnthings.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.activities.FRActivity;
import com.timothyblumberg.autodidacticism.learnthings.activities.MCActivity;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;

import java.util.Calendar;


/**
 * Created by Tim on 7/24/14.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public final static int NEW_QUESTION_SUCCESS = 0;
    public final static int QUESTION_PACK_DEACTIVATED = 1;
    public final static int QUESTION_PACK_RENEWED = 2;

    public static final String TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(TAG, "--> onReceive called!");

        final Calendar c = Calendar.getInstance();

        String[] startArray = G.curUser.startTime.split(":");
        int startHour = Integer.valueOf(startArray[0]);
        int startMin = Integer.valueOf(startArray[1]);

        String[] endArray = G.curUser.endTime.split(":");
        int endHour = Integer.valueOf(endArray[0]);
        int endMin = Integer.valueOf(endArray[1]);

        int curHour = c.get(Calendar.HOUR_OF_DAY);
        int curMin = c.get(Calendar.MINUTE);

        if((curHour >= startHour) && (curHour <= endHour)){
            // Hours are equal, handle minutes
            if(curHour == startHour){
                if(curMin > startMin){
                    publishNotif();
                } else{
                    rescheduleNotif();
                    Log.d(TAG, "MINUTES TOO SMALL");
                }
            } else if(curHour == endHour){
                if(curMin < endMin){
                    publishNotif();
                } else{
                    rescheduleNotif();
                    Log.d(TAG, "MINUTES TOO BIG");
                }
            } else {
                // Publish if current time clearly in range
                publishNotif();
            }
        } else {
            rescheduleNotif();

            Log.d(TAG, "SKIPPED NOTIFICATION PUBLISH: NOT IN TIME RANGE");
            Log.d(TAG, "start: " + startHour + ":" + startMin);
            Log.d(TAG, "end: " + endHour + ":" + endMin);
//            ToastUtil.showShort("cur: " + curHour + ":" + curMin);
        }

    }

    public void publishNotif(){
        // Don't try to make a new notification without any questions
        if(QuestionDAO.getTotalNumberOfQuestions() == 0) return;

        if(G.DEBUG) Toast.makeText(App.getAppContext(),
                String.valueOf(QuestionDAO.getTotalNumberOfQuestions()),
                Toast.LENGTH_SHORT)
                .show();

        Question rand_q = Question.getQuestionOrHandleWin();

        // Handle endcase
        if(rand_q == null){
            // Win Condition / Lack of checks should already be handled, bounce.
            return;
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getAppContext());

        // Create the notification builder of the correct type
        NotificationCompat.Builder mBuilder = null;
        if (rand_q.multipleChoice){
            // Creates an explicit intent for an Activity in your app
            Intent mcIntent = new Intent(App.getAppContext(), MCActivity.class);

            stackBuilder.addParentStack(MCActivity.class);
            stackBuilder.addNextIntent(mcIntent);
            mBuilder = createMCBuilder(rand_q);
        } else {

            // Creates an explicit intent for an Activity in your app
            Intent frIntent = new Intent(App.getAppContext(), FRActivity.class)
                    .setAction(G.ANSWER_FR)
                    .putExtra(G.EXTRA_QUESTION_ID, rand_q.question_id)
                    .putExtra(G.EXTRA_IS_FR, !rand_q.multipleChoice);

            stackBuilder.addParentStack(FRActivity.class);
            stackBuilder.addNextIntent(frIntent);
            mBuilder = createFRBuilder(rand_q);

            // Assign the correct intent for click through
            frIntent.setAction(G.ANSWER_FR)
                        .putExtra(G.EXTRA_IS_FR, true);
            PendingIntent pIntent = PendingIntent.getActivity(App.getAppContext(),
                                                                0,
                                                                frIntent,
                                                                PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(pIntent);
        }


        NotificationManager mNotificationManager =
                (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = mBuilder.build();
        mNotificationManager.notify(G.DEFAULT_NOTIFICATIONS_CODE, notif);

    }


    /**
     * This method creates a Notification builder from the specified Question obj
     * @param question A simple string with the question (? included)
     * @return NotificationCompat.Builder to createMC the notifs
     */
    public NotificationCompat.Builder createMCBuilder(Question question){
        Context context = App.getAppContext();

        // Get pertinent fields from Question obj
        String curQText = question.qText;
        String[] answers = question.getAnswers();
        String id = question.getQuestionId();

        boolean[] correctArray = new boolean[3];
        // Find right answer, strip identifiers
        for(int a = 0; a < answers.length; a++){
            String curString = answers[a];
            if(curString.startsWith("@")){
                correctArray[a] = true;
                answers[a] = answers[a].substring(1);
            } else {
                correctArray[a] = false;
                answers[a] = answers[a].substring(1);
            }
        }

        // Creates an explicit intent for an Activity in your app
        Intent aIntent = new Intent(context, MCActivity.class)
                .setAction(G.ANSWER_A)
                .putExtra(G.EXTRA_ANSWER, G.A_CODE)
                .putExtra(G.EXTRA_YOUR_ANSWER, answers[0])
                .putExtra(G.EXTRA_QUESTION_ID, id)
                .putExtra(G.EXTRA_CORRECT, correctArray[0]);
        Intent bIntent = new Intent(context, MCActivity.class)
                .setAction(G.ANSWER_B)
                .putExtra(G.EXTRA_ANSWER, G.B_CODE)
                .putExtra(G.EXTRA_YOUR_ANSWER, answers[1])
                .putExtra(G.EXTRA_QUESTION_ID, id)
                .putExtra(G.EXTRA_CORRECT, correctArray[1]);
        Intent cIntent = new Intent(context, MCActivity.class)
                .setAction(G.ANSWER_C)
                .putExtra(G.EXTRA_YOUR_ANSWER, answers[2])
                .putExtra(G.EXTRA_ANSWER, G.C_CODE)
                .putExtra(G.EXTRA_QUESTION_ID, id)
                .putExtra(G.EXTRA_CORRECT, correctArray[2]);

        // Create the pending intents
        PendingIntent aPIntent = PendingIntent.getActivity(context, 0, aIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent bPIntent = PendingIntent.getActivity(context, 0, bIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent cPIntent = PendingIntent.getActivity(context, 0, cIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create and return the Notification Builder
        return new NotificationCompat.Builder(context)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle(context.getString(R.string.new_q))
                        .addLine(curQText)
//                                .setSummaryText(curQText)
                        .addLine(String.format("A) %s", answers[0]))
                        .addLine(String.format("B) %s", answers[1]))
                        .addLine(String.format("C) %s",answers[2])) )
                .setSmallIcon(R.drawable.notif_pic)
                .addAction(R.drawable.a_icn, "", aPIntent)
                .addAction(R.drawable.b_icn, "", bPIntent)
                .addAction(R.drawable.c_icn, "", cPIntent)
                .setContentTitle(context.getString(R.string.new_q))
                .setContentText(curQText)
                .setAutoCancel(true)
                .setVibrate(G.VIBRATE_PATTERN)
                .setLights(Color.argb(1,30, 223, 152), 500, 300)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

    public NotificationCompat.Builder createFRBuilder(Question question){
        Context context = App.getAppContext();

        // Get pertinent fields from Question obj
        String curQText = question.qText;


        //        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);

        // Create and return the Notification Builder
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notif_pic)
                .setContentTitle(context.getString(R.string.new_q))
                .setContentText(curQText)
                .setAutoCancel(true)
                .setVibrate(G.VIBRATE_PATTERN)
                .setLights(Color.argb(1, 30, 223, 152), 500, 300)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

    public static void reportTimeToNextNotif(){
        int bigSec = G.curUser.TIME_UNTIL_NEXT_NOTIFICATION / 1000;
        int minutes = bigSec/60;
        int hours = minutes / 60;
        int seconds = bigSec - minutes*60;
        String units;
        if(hours > 0) units = hours + "h";
        else if(minutes > 0) units = minutes + "m";
        else units = seconds + "s";
        String message = String.format(App.getAppContext().getString(R.string.toast_time_till_next),
                units);
        Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Created by Tim on 7/26/14.
     */
    public static class Frequency {

        public int millisecondsTillNext;
        public String itemName;
        public boolean isSelected;

        public Frequency(int millisecondsTillNext, String itemName){
            this.millisecondsTillNext = millisecondsTillNext;
            this.itemName = itemName;
            this.isSelected = false;
        }


    }

    private void rescheduleNotif() {
        int timeUntilNextNotif = G.curUser.TIME_UNTIL_NEXT_NOTIFICATION;

        AlarmManager alarmManager = (AlarmManager) App.getAppContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(App.getAppContext(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(App.getAppContext(), 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis() + timeUntilNextNotif,
                alarmIntent);
    }
}
