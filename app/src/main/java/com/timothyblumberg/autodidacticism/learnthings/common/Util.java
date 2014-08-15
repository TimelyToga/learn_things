package com.timothyblumberg.autodidacticism.learnthings.common;

import android.app.Application;
import android.view.View;
import android.widget.LinearLayout;

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

    public static boolean isNotEmpty(String s){
        if(s == null || s.isEmpty() || s.equals("")){
            return false;
        } else{
            return true;
        }
    }
    public static boolean isEmpty(String s){
        return !isNotEmpty(s);
    }

    public static boolean doesLayoutHaveChild(int childID, LinearLayout layout){
        int numChildren = layout.getChildCount();
        for(int a = 0; a < numChildren; a++){
            View curChild = layout.getChildAt(a);
            if(curChild.getId() == childID) return true;
        }

        // Child not found
        return false;
    }

    public static void toggleCurTrueFalse(){
        String curTrue = G.CUR_TRUE;
        if(curTrue.equals("T")){
            G.CUR_TRUE = "F";
            G.CUR_FALSE = "T";
        } else {
            G.CUR_TRUE = "T";
            G.CUR_FALSE = "F";
        }
    }

}