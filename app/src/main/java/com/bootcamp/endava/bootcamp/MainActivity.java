
package com.bootcamp.endava.bootcamp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bootcamp.endava.bootcamp.contentprovider.SongsProvider;
import com.bootcamp.endava.bootcamp.database.MusicContract;
import com.bootcamp.endava.bootcamp.database.MusicDbHelper;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AddSongDialogFragment.Callback {

    FloatingActionButton mFab;
    ListView mList;
    SongsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mList = (ListView)findViewById(R.id.listview);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new QuerySongsTask(ContentUris.withAppendedId(MusicContract.Song.CONTENT_URI,
                        (Integer)view.getTag()), true).execute();
            }
        });
        mFab.setOnClickListener(this);
        new QuerySongsTask(MusicContract.Song.CONTENT_URI, false).execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                displayAddSongFragment();
                break;
        }
    }

    private void displayAddSongFragment() {
        adjustFragmentTransactions();
        AddSongDialogFragment.newInstance().show(getFragmentManager(), AddSongDialogFragment.TAG);
    }

    private void displayAddSongFragment(int id, String title, String author, String genre) {
        adjustFragmentTransactions();
        AddSongDialogFragment.newInstance(id, title, author, genre).show(getFragmentManager(),
                AddSongDialogFragment.TAG);
    }

    private void adjustFragmentTransactions() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(AddSongDialogFragment.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }

    @Override
    public void insertSong(String title, String author, String genre) {
        ContentValues cv = new ContentValues();
        cv.put(MusicContract.Song.TITLE, title);
        cv.put(MusicContract.Song.AUTHOR, author);
        cv.put(MusicContract.Song.GENRE, genre);
        getContentResolver().insert(MusicContract.Song.CONTENT_URI, cv);
        new QuerySongsTask(MusicContract.Song.CONTENT_URI, false).execute();
    }

    @Override
    public void updateSong(int id, String title, String author, String genre) {
        ContentValues cv = new ContentValues();
        cv.put(MusicContract.Song.TITLE, title);
        cv.put(MusicContract.Song.AUTHOR, author);
        cv.put(MusicContract.Song.GENRE, genre);
        getContentResolver().update(
                ContentUris.withAppendedId(MusicContract.Song.CONTENT_URI, (long)id), cv, null,
                null);
        new QuerySongsTask(MusicContract.Song.CONTENT_URI, false).execute();
    }

    private class QuerySongsTask extends AsyncTask<Void, Void, Cursor> {
        Uri mUri;
        boolean mIsUpdate;

        public QuerySongsTask(Uri uri, boolean isUpdate) {
            mUri = uri;
            mIsUpdate = isUpdate;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return getContentResolver().query(mUri, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor c) {
            if (!mIsUpdate) {
                mAdapter = new SongsAdapter(MainActivity.this, c);
                mList.setAdapter(mAdapter);
            } else {
                if (c.getCount() < 1) {
                    return;
                }
                c.moveToFirst();
                String title = c.getString(c.getColumnIndex(MusicContract.Song.TITLE));
                String author = c.getString(c.getColumnIndex(MusicContract.Song.AUTHOR));
                String genre = c.getString(c.getColumnIndex(MusicContract.Song.GENRE));
                displayAddSongFragment(Integer.valueOf(mUri.getLastPathSegment()), title, author, genre);
            }
        }
    }
}
