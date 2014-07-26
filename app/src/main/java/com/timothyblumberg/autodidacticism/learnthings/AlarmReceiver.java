package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.timothyblumberg.autodidacticism.learnthings.dirtywork.Globals;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;
import com.timothyblumberg.autodidacticism.learnthings.question.QuestionDAO;


/**
 * Created by Tim on 7/24/14.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String TAG = BroadcastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d(TAG, "--> onReceive called!");
        publishNotif();
    }

    public void publishNotif(){
        if(Globals.DEBUG) Toast.makeText(App.getAppContext(),
                String.valueOf(QuestionDAO.getNumberOfQuestions()),
                Toast.LENGTH_SHORT)
                .show();

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(App.getAppContext(), MainActivity.class);

        Question rand_q;
        try{
            rand_q = QuestionDAO.getRandomQuestion();
        } catch(CursorIndexOutOfBoundsException e) {
            //TODO: Figure out what to do when all questions have been correctly answered
            Log.d("", "All questions have been correctly answered");
            rand_q = QuestionDAO.getQuestionList(QuestionDAO.RANDOM_QUERY_FORMAT, 1)[0];
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.getAppContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Create the notification builder of the correct type
        NotificationCompat.Builder mBuilder = null;
        if (rand_q.multipleChoice){
            mBuilder = createMCBuilder(rand_q);
        } else {
            mBuilder = createFRBuilder(rand_q);

            // Assign the correct intent for click through
            resultIntent.setAction(Globals.ANSWER_FR)
                        .putExtra(Globals.EXTRA_IS_FR, true);
            PendingIntent pIntent = PendingIntent.getActivity(App.getAppContext(),
                                                                0,
                                                                resultIntent,
                                                                PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(pIntent);
        }


        NotificationManager mNotificationManager =
                (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = mBuilder.build();
        mNotificationManager.notify(Globals.DEFAULT_NOTIFICATIONS_CODE, notif);

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
        Intent aIntent = new Intent(context, MainActivity.class)
                .setAction(Globals.ANSWER_A)
                .putExtra(Globals.EXTRA_ANSWER, Globals.A_CODE)
                .putExtra(Globals.EXTRA_QUESTION_ID, id)
                .putExtra(Globals.EXTRA_CORRECT, correctArray[0]);
        Intent bIntent = new Intent(context, MainActivity.class)
                .setAction(Globals.ANSWER_B)
                .putExtra(Globals.EXTRA_ANSWER, Globals.B_CODE)
                .putExtra(Globals.EXTRA_QUESTION_ID, id)
                .putExtra(Globals.EXTRA_CORRECT, correctArray[1]);
        Intent cIntent = new Intent(context, MainActivity.class)
                .setAction(Globals.ANSWER_C)
                .putExtra(Globals.EXTRA_ANSWER, Globals.C_CODE)
                .putExtra(Globals.EXTRA_QUESTION_ID, id)
                .putExtra(Globals.EXTRA_CORRECT, correctArray[2]);

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
                .setAutoCancel(true);
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
                .setAutoCancel(true);
    }
}
