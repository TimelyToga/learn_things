package com.timothyblumberg.autodidacticism.learnthings.common;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.question.Question;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * As from: http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
 *
 * Usage:
 *    int[] array = {1, 2, 3};
 *    Util.shuffle(array);
 */
public class Util{

    private final static String TAG = Util.class.getSimpleName();
    private static Random random;
    private static DBHelper sDbHelper;
    private static Application instance;
    private static ArrayList<Question> questionArrayList = new ArrayList<Question>();

    public static Application getInstance() {
        return instance;
    }

    /**
     * Code from method java.util.Collections.shuffle();
     */
    public static void shuffle(String[] array) {
        if (random == null) random = new Random();
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

    private static void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static Long getLongFromUUIDString(String uuid) {
        return UUID.fromString(uuid).getMostSignificantBits();
    }

    public static void readCSV() {

        String csvFile = "qs.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            InputStream is  = App.getAppContext().getResources().openRawResource(R.raw.qs);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] qArray = line.split(cvsSplitBy);
                Question cur_question = null;
                if(qArray.length > 1){
                    // MUST be a Multiple Choice Question
                    String[] answers = {qArray[1], qArray[2], qArray[3]};

                    cur_question = Question.createMC(qArray[0], answers);
                } else {
                    // Must be a free response
                    cur_question = Question.createFR(qArray[0]);
                }
                // Add newly created question to list for output to JSON
                questionArrayList.add(cur_question);
                Log.d(TAG, "made question " + cur_question.question_id);
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