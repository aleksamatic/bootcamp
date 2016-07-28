
package com.bootcamp.endava.bootcamp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aleksa on 28.7.16..
 */
public class MusicDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "music.db";
    public static final int DATABASE_VERSION = 1;

    public MusicDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createSongTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createSongTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MusicContract.Song.TABLE_NAME +" ("
                + MusicContract.Song._ID + " INTEGER PRIMARY KEY," +
                MusicContract.Song.TITLE + " TEXT," +
                MusicContract.Song.AUTHOR + " TEXT," +
                MusicContract.Song.GENRE + " TEXT)");
    }
}
