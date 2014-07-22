package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Application;

import com.timothyblumberg.autodidacticism.learnthings.question.DBHelper;

import java.util.Random;

/**
 * As from: http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
 *
 * Usage:
 *    int[] array = {1, 2, 3};
 *    Util.shuffle(array);
 */
public class Util{

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

    public void initializeDB(){

    }

}