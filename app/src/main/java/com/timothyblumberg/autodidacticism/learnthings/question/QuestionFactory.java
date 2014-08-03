package com.timothyblumberg.autodidacticism.learnthings.question;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.common.G;

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

    public static void initQuestionDBFromCSV(){
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
        String CSV_SPLIT_BY_STRING = ",";

        try {
            InputStream is  = App.getAppContext().getResources().openRawResource(R.raw.qs);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                // Skip commented lines
                if(line.startsWith(G.COMMENT_STRING)){
                    continue;
                }
                if(line.startsWith(G.QUESTION_PACK_STRING)){
                    // This line must define a Question Pack
                    String[] qPackArray = line.split(CSV_SPLIT_BY_STRING);
                    String qPackID = qPackArray[0].trim().substring(3);
                    String qPackDisplayName = qPackArray[1].trim();
                    String qPackDesc = qPackArray[2].trim();
                    int qPackSource = Integer.valueOf(qPackArray[3].trim());

                    QuestionPack qPack = QuestionPack.createQuestionPack(qPackID, qPackDisplayName, qPackDesc,
                            qPackSource);
                    Log.d(TAG, "Created Question PACK: " + qPackID);
                } else {
                    // This line must be a question
                    String[] qArray = line.split(CSV_SPLIT_BY_STRING);
                    Question curQ = null;

                    if(qArray.length > 3){
                        // MUST be a Multiple Choice Question
                        String qPackID = qArray[0].trim();
                        String qText = qArray[1].trim();
                        String[] answers = {qArray[2], qArray[3], qArray[4]};

                        curQ = Question.createMC(qText, answers, qPackID);
                    } else {
                        // MUST be a free response question
                        String qPackID = qArray[0].trim();
                        String qText = qArray[1].trim();
                        String qAnswer = qArray[2].trim().substring(1);

                        curQ = Question.createFR(qText, qAnswer, qPackID);
                    }

                    // Add newly created question to list for output to JSON
                    questionArrayList.add(curQ);
                }
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
