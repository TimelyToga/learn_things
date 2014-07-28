package com.timothyblumberg.autodidacticism.learnthings.question;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.Globals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Tim on 7/26/14.
 */
public class QuestionFactory {

    private final static String TAG = QuestionFactory.class.getSimpleName();

    public static void createQuestions(){
//        String[] array = {"#North", "@South", "#East"};
//        Question q1 = Question.createMC("Which direction is generally down on a map?", array);
//        String[] answers = {"@San Francisco", "#Durham", "#Gray"};
//        String[] answers2 = {"@Paris", "#Toulouse", "#Orleans"};
//        String[] answers3 = {"@IKEA", "#Grand Furniture", "#Zack's Furniture"};
//        String[] answers4 = {"@Santa Claus", "#Easter Bunny", "#Father Time"};
//        Question q5 = Question.createMC("What is the capital of France?", answers2);
//        Question q2 = Question.createMC("What city is the best city?", answers);
//        Question q3 = Question.createMC("Where is cheap furniture bought from?", answers3);
//        Question q4 = Question.createMC("Who comes down the chimney on Christmas?", answers4);
//
//        // FR Questions
//        Question freeQuestion = Question.createFR("What time is it?", "About that time.");
        makeQuestionsFromCSVFile();
    }

    public static Question createQuestionfromJson(String json){
        final Gson gson = new Gson();
        Question q = gson.fromJson(json, Question.class);
        return q;
    }

    public static void makeQuestionsFromCSVFile() {
        ArrayList<Question> questionArrayList = new ArrayList<Question>();
        String csvFile = "qs.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            InputStream is  = App.getAppContext().getResources().openRawResource(R.raw.qs);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                // Skip commented lines
                if(line.startsWith(Globals.COMMENT_STRING)){
                    continue;
                }
                // use comma as separator
                String[] qArray = line.split(cvsSplitBy);
                Question curQ = null;
                if(qArray.length > 2){
                    // MUST be a Multiple Choice Question
                    String[] answers = {qArray[1], qArray[2], qArray[3]};

                    curQ = Question.createMC(qArray[0], answers);
                } else {
                    // Must be a free response,
                    curQ = Question.createFR(qArray[0], qArray[1].substring(1));
                }
                // Add newly created question to list for output to JSON
                questionArrayList.add(curQ);
                Log.d(TAG, "made question " + curQ.question_id + " MC: " + curQ.multipleChoice);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        writeQuestionsToJSON(questionArrayList);
        System.out.println("Done");
    }

    public static void writeQuestionsToJSON(ArrayList<Question> arrayList){
        FileOutputStream writer = null;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/");

            File file = new File(dir, "test_json_out.txt");

            writer = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(writer);

            //Now convert and write questions to JSON
            Gson gson = new Gson();
            for(Question q : arrayList){
                String jsonOutput = gson.toJson(q) + "\n";
                Log.d(TAG, "WRITING --> "+ jsonOutput);
                osw.write(jsonOutput);
            }
            osw.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}
