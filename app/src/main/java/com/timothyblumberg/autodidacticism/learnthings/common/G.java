package com.timothyblumberg.autodidacticism.learnthings.common;

import com.timothyblumberg.autodidacticism.learnthings.user.User;
import java.util.Random;

/**
 * Created by Tim on 7/23/14.
 * Globals class. Shortened for ease
 */
public class G {
    // Global Objects and states
    public final static Random rgen = new Random();
    public static User curUser;
    public static final boolean DEBUG = false;

    // Timer values
    public static final int TIMER_COUNTDOWN_LENGTH = 2400;
    public static final int ANIMATION_TIMER_LENGTH = 75;
    public static int INITIAL_TIME_FOR_NOTIF = 2000;
    public static final int SCHEDULE_NOTIF_DEFAULT_TIME = -1;
    public static final int COLOR_FADE_TIME = 600;

    // Notif codes
    public static final int DEFAULT_NOTIFICATIONS_CODE = 98;
    public static final String ANSWER_FR = "ANSWER_FR";
    public static final String ANSWER_A = "ANSWER_A";
    public static final String ANSWER_B = "ANSWER_B";
    public static final String ANSWER_C = "ANSWER_C";
    public static final int A_CODE = 0;
    public static final int B_CODE = 1;
    public static final int C_CODE = 2;

    // EXTRAS
    public static final String EXTRA_ANSWER = "EXTRA_ANSWER";
    public static final String EXTRA_QUESTION_ID = "EXTRA_QUESTION_ID";
    public static final String EXTRA_CORRECT = "EXTRA_CORRECT"; // true if correct | false if incorrect
    public static final String EXTRA_IS_FR = "EXTRA_IS_FR"; // true FR, false MC
    public static final String EXTRA_YOUR_ANSWER = "EXTRA_YOUR_ANSWER";

    // Notification Frequency variables and constants
    public static final int MILLISECONDS_IN_DAY = 3600*24000;
    public static final int FREQUENCY_TPD5 = MILLISECONDS_IN_DAY / 5;
    public static final int FREQUENCY_TPD10 = MILLISECONDS_IN_DAY / 10;
    public static final int FREQUENCY_TPD25 = MILLISECONDS_IN_DAY / 25;
    public static final int FREQUENCY_50 = MILLISECONDS_IN_DAY / 50;
    public static final int FREQUENCY_SEC3 = MILLISECONDS_IN_DAY / (24*3600/3);
    public static final int FREQUENCY_MIN15 = MILLISECONDS_IN_DAY / 96;
    public static final int FREQUENCY_MIN30 = MILLISECONDS_IN_DAY / 48;
    public static final int FREQUENCY_HOURLY = MILLISECONDS_IN_DAY / 24;
    public static final int FREQUENCY_HOURLY2 = MILLISECONDS_IN_DAY / 12;
    public static final int FREQUENCY_HOURLY6 = MILLISECONDS_IN_DAY / 4;
    public static final int FREQUENCY_DAILY = MILLISECONDS_IN_DAY;
    public static final int FREQUENCY_DAILY2 = MILLISECONDS_IN_DAY * 2;

    public static final String[] questionFrequencyNames = {"Every 3 Seconds",
                                                "Every 15 minutes",
                                                "Every 30 minutes",
                                                "Every hour",
                                                "Every two hours",
                                                "Every 6 hours",
                                                "Daily",
                                                "Every two days"};
    public static final int[] questionFrequencyTimes = {FREQUENCY_SEC3,
                                                FREQUENCY_MIN15,
                                                FREQUENCY_MIN30,
                                                FREQUENCY_HOURLY,
                                                FREQUENCY_HOURLY2,
                                                FREQUENCY_HOURLY6,
                                                FREQUENCY_DAILY,
                                                FREQUENCY_DAILY2};


    public final static String QPACK_USER_CREATED = "001_USERCREATED";
    public final static String QPACK_STARTING = "000_STARTPACK";

    // booleanString
    public final static String TRUE = "T";
    public final static String FALSE = "F";
    public static String CUR_TRUE = "T";
    public static String CUR_FALSE = "F";

    // SELECTION STRINGS
    public final static String SELECT_QUESTION_ID = "question_id = ?";
    public final static String SELECT_QUESTION_PACK_ID = "qpack_id = ?";

    // MISC.
    public static final String COMMENT_STRING = "//";
    public static final long[] VIBRATE_PATTERN = {0, 75, 100, 75, 150, 300};
}
