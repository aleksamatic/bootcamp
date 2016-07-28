package com.bootcamp.endava.bootcamp.database;

import android.provider.BaseColumns;

/**
 * Created by aleksa on 28.7.16..
 */
public class MusicContract {
    public static abstract class Song implements BaseColumns {
        public static final String TABLE_NAME = "songs";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String GENRE = "genre";
    }
}
