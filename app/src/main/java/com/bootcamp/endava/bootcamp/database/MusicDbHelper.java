
package com.bootcamp.endava.bootcamp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

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
        db.execSQL("CREATE TABLE " + MusicContract.Song.TABLE_NAME + " (" + MusicContract.Song._ID
                + " INTEGER PRIMARY KEY," + MusicContract.Song.TITLE + " TEXT,"
                + MusicContract.Song.AUTHOR + " TEXT," + MusicContract.Song.GENRE + " TEXT)");
    }

    public Cursor getSongs(String id, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(MusicContract.Song.TABLE_NAME);
        if (id != null) {
            builder.appendWhere(BaseColumns._ID + " = " + id);
        }
        return builder.query(getReadableDatabase(), projection, selection, selectionArgs, null,
                null, sortOrder);

    }

    public long addSong(ContentValues cv) throws SQLException {
        long id = getWritableDatabase().insert(MusicContract.Song.TABLE_NAME, "", cv);
        if (id <= 0) {
            throw new SQLException("Failed to add a Song");
        }
        return id;
    }

    public int deleteSongs(String id) {
        if (id == null) {
            return getWritableDatabase().delete(MusicContract.Song.TABLE_NAME, null, null);
        } else {
            return getWritableDatabase().delete(MusicContract.Song.TABLE_NAME, "_id=?",
                    new String[] {
                            id
                    });
        }
    }

    public int updateSongs(String id, ContentValues values) {
        if (id == null) {
            return getWritableDatabase().update(MusicContract.Song.TABLE_NAME, values, null, null);
        } else {
            return getWritableDatabase().update(MusicContract.Song.TABLE_NAME, values, "_id=?",
                    new String[] {
                            id
                    });
        }
    }
}
