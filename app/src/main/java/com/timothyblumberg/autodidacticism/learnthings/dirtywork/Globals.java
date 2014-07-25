package com.timothyblumberg.autodidacticism.learnthings.dirtywork;

import com.timothyblumberg.autodidacticism.learnthings.user.User;

import java.util.Random;

/**
 * Created by Tim on 7/23/14.
 */
public class Globals {

    final public static String NEW_QUESTION = "New Q!";
    public static final boolean DEBUG = true;
    public static final int MC_COUNTDOWN_LENGTH = 1600;
    public static final int FR_COUNTDOWN_LENGTH = 30000;
    public static int TIME_UNTIL_NEXT_NOTIFICATION = 1000;

    public final static Random rgen = new Random();
    public static User curUser;
}
