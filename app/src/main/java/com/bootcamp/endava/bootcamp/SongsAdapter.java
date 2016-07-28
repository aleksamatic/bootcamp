package com.bootcamp.endava.bootcamp;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bootcamp.endava.bootcamp.database.MusicContract;

/**
 * Created by aleksa on 28.7.16..
 */
public class SongsAdapter extends CursorAdapter {

    public SongsAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
        v.setTag(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title, genre, author;
        title = (TextView) view.findViewById(R.id.song_title);
        genre = (TextView) view.findViewById(R.id.song_genre);
        author = (TextView) view.findViewById(R.id.song_author);
        title.setText(cursor.getString(cursor.getColumnIndex(MusicContract.Song.TITLE)));
        author.setText(cursor.getString(cursor.getColumnIndex(MusicContract.Song.AUTHOR)));
        genre.setText(cursor.getString(cursor.getColumnIndex(MusicContract.Song.GENRE)));
    }
}
