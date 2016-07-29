
package com.bootcamp.endava.bootcamp.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.bootcamp.endava.bootcamp.database.MusicContract;
import com.bootcamp.endava.bootcamp.database.MusicDbHelper;

/**
 * Created by aleksa on 28.7.16..
 */
public class SongsProvider extends ContentProvider {

    public static final int SONG = 1;
    public static final int SONGS = 2;
    public static final String AUTHORITY = "com.bootcamp.endava.provider.songs";
    public static final UriMatcher sUriMatcher = getUriMatcher();

    private MusicDbHelper mDbHelper;

    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MusicContract.Song.TABLE_NAME, SONGS);
        uriMatcher.addURI(AUTHORITY, MusicContract.Song.TABLE_NAME + "/#", SONG);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MusicDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        String id = null;
        if (sUriMatcher.match(uri) == SONG) {
            id = uri.getLastPathSegment();
        }
        return mDbHelper.getSongs(id, projection, selection, selectionArgs, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        try {
            long id = mDbHelper.addSong(contentValues);
            Uri returnUri = ContentUris.withAppendedId(MusicContract.Song.CONTENT_URI, id);
            return returnUri;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        String id = null;
        if (sUriMatcher.match(uri) == SONG) {
            id = uri.getLastPathSegment();
        }
        return mDbHelper.deleteSongs(id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        String id = null;
        if (sUriMatcher.match(uri) == SONG) {
            id = uri.getLastPathSegment();
        }
        return mDbHelper.updateSongs(id, contentValues);
    }
}
