
package com.bootcamp.endava.bootcamp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bootcamp.endava.bootcamp.contentprovider.SongsProvider;
import com.bootcamp.endava.bootcamp.database.MusicContract;
import com.bootcamp.endava.bootcamp.database.MusicDbHelper;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, AddSongDialogFragment.Callback {

    private static final String SHARED_PREFS_KEY_SORT = "sort";
    FloatingActionButton mFab;
    ListView mList;
    SongsAdapter mAdapter;
    String mSortOrder;

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
        mSortOrder = getPreferences(MODE_PRIVATE).getString(SHARED_PREFS_KEY_SORT,
                MusicContract.Song.TITLE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_order) {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Sort Type");
            dialogBuilder.setSingleChoiceItems(new String[] {
                    "title", "genre", "author"
            }, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences sp = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                    switch (i) {
                        case 0:
                            mSortOrder = MusicContract.Song.TITLE;
                            sp.edit().putString(SHARED_PREFS_KEY_SORT, MusicContract.Song.TITLE)
                                    .commit();
                            break;
                        case 1:
                            mSortOrder = MusicContract.Song.GENRE;
                            sp.edit().putString(SHARED_PREFS_KEY_SORT, MusicContract.Song.GENRE)
                                    .commit();
                            break;
                        case 2:
                            mSortOrder = MusicContract.Song.AUTHOR;
                            sp.edit().putString(SHARED_PREFS_KEY_SORT, MusicContract.Song.AUTHOR)
                                    .commit();
                            break;
                    }
                    new QuerySongsTask(MusicContract.Song.CONTENT_URI, false).execute();
                    dialogInterface.dismiss();
                }
            });
            dialogBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
            return getContentResolver().query(mUri, null, null, null, mSortOrder + " ASC");
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
                displayAddSongFragment(Integer.valueOf(mUri.getLastPathSegment()), title, author,
                        genre);
            }
        }
    }
}
