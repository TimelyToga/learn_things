package com.timothyblumberg.autodidacticism.learnthings.dirtywork;

import com.timothyblumberg.autodidacticism.learnthings.user.User;

import java.util.Random;

/**
 * Created by Tim on 7/23/14.
 */
public class Globals {

    final public static String NEW_QUESTION = "New Q!";
    public static final boolean DEBUG = false;
    public static final int TIMER_COUNTDOWN_LENGTH = 1600;
    public static final int DEFAULT_NOTIFICATIONS_CODE = 98;
    public static final String ANSWER_FR = "ANSWER_FR";
    public static final String ANSWER_A = "ANSWER_A";
    public static final String ANSWER_B = "ANSWER_B";
    public static final int A_CODE = 0;
    public static final int B_CODE = 1;
    public static final int C_CODE = 2;
    public static final String EXTRA_ANSWER = "EXTRA_ANSWER";
    public static final String EXTRA_QUESTION_ID = "EXTRA_QUESTION_ID";
    public static final String EXTRA_CORRECT = "EXTRA_CORRECT"; // true if correct | false if incorrect
    public static final String EXTRA_IS_FR = "EXTRA_IS_FR"; // true FR, false MC
    public static final String ANSWER_C = "ANSWER_C";
    public static int TIME_UNTIL_NEXT_NOTIFICATION = 1000;

    public final static Random rgen = new Random();
    public static User curUser;
}
