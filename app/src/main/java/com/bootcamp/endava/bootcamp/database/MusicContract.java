package com.bootcamp.endava.bootcamp.database;

import android.net.Uri;
import android.provider.BaseColumns;

import com.bootcamp.endava.bootcamp.contentprovider.SongsProvider;

/**
 * Created by aleksa on 28.7.16..
 */
public class MusicContract {
    public static abstract class Song implements BaseColumns {
        public static final Uri CONTENT_URI = Uri
                .parse("content://" + SongsProvider.AUTHORITY + "/" +MusicContract.Song.TABLE_NAME);
        public static final String TABLE_NAME = "songs";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String GENRE = "genre";
    }
}
