package com.timothyblumberg.autodidacticism.learnthings.question;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.timothyblumberg.autodidacticism.learnthings.App;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DBHelper extends SQLiteOpenHelper {

    /************************************************
     * Suggested Copy/Paste code. Everything from here to the done block.
     ************************************************/

    // DB name for version 5 and below was leo.db
    private static final String DATABASE_NAME = "learnthings.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TAG = "DBHelper";

    /* Static Initializer to register entities with Cupboard */

    static {
        // Register cupboard models
        cupboard().register(Question.class);
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // This will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted
        cupboard().withDatabase(db).upgradeTables();
    }

    /**
     * Clears all the data in the DB
     */
    public void clearAllData(Context context) {
        // Drop tables - cupboard uses a class's getSimpleName() method.
        final SQLiteDatabase db = App.getWritableDB();
        db.execSQL("DELETE FROM " + Question.class.getSimpleName());
    }

}