
package com.bootcamp.endava.bootcamp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.bootcamp.endava.bootcamp.database.MusicContract;
import com.bootcamp.endava.bootcamp.database.MusicDbHelper;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AddSongDialogFragment.Callback {

    private static final int QUERY_ALL_VALUE = -1;
    FloatingActionButton mFab;
    SQLiteDatabase mDatabase;
    MusicDbHelper mDbHelper;
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
                new QuerySongsTask((Integer) view.getTag()).execute();
            }
        });
        mFab.setOnClickListener(this);
        mDbHelper = new MusicDbHelper(this);
        mDatabase = mDbHelper.getWritableDatabase();
        new QuerySongsTask(QUERY_ALL_VALUE).execute();
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
        mDatabase.insert(MusicContract.Song.TABLE_NAME, null, cv);
        new QuerySongsTask(-1).execute();
    }

    @Override
    public void updateSong(int id, String title, String author, String genre) {
        ContentValues cv = new ContentValues();
        cv.put(MusicContract.Song.TITLE, title);
        cv.put(MusicContract.Song.AUTHOR, author);
        cv.put(MusicContract.Song.GENRE, genre);
        String selection = BaseColumns._ID + " =?";
        String [] selectionArgs = new String[] {String.valueOf(id)};
        mDatabase.update(MusicContract.Song.TABLE_NAME, cv, selection, selectionArgs);
        new QuerySongsTask(-1).execute();
    }

    private class QuerySongsTask extends AsyncTask<Void, Void, Cursor> {

        int mId;

        public QuerySongsTask(int id) {
            mId = id;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            if (mId == QUERY_ALL_VALUE) {
                return mDatabase.query(MusicContract.Song.TABLE_NAME, null, null, null, null, null,
                        null);
            } else {
                return mDatabase.query(MusicContract.Song.TABLE_NAME, null, BaseColumns._ID + "=?",
                        new String[] {
                                String.valueOf(mId)
                        }, null, null, null);
            }
        }

        @Override
        protected void onPostExecute(Cursor c) {
            if (mId == QUERY_ALL_VALUE) {
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
                displayAddSongFragment(mId, title, author, genre);
            }
        }
    }
}
