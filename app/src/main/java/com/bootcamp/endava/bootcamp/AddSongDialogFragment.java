
package com.bootcamp.endava.bootcamp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by aleksa on 28.7.16..
 */
public class AddSongDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = AddSongDialogFragment.class.getName();
    public static final String BUNDLE_KEY_TITLE = "title";
    public static final String BUNDLE_KEY_AUTHOR = "author";
    public static final String BUNDLE_KEY_GENRE = "genre";
    public static final String BUNDLE_KEY_ID = "id";
    private String mTitleText, mAuthorText, mGenreText;
    private int mId;
    public EditText mTitle, mAuthor, mGenre;
    public Button mOk, mCancel;

    public interface Callback {
        public void insertSong(String title, String author, String genre);

        public void updateSong(int id, String title, String author, String genre);
    }

    private enum TypeOfAction {
        INSERT, EDIT
    }

    TypeOfAction mTypeOfAction;

    Callback mCallback;

    public static AddSongDialogFragment newInstance() {
        return new AddSongDialogFragment();
    }

    public static AddSongDialogFragment newInstance(int id, String title, String author,
            String genre) {
        AddSongDialogFragment fragment = new AddSongDialogFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_KEY_ID, id);
        b.putString(BUNDLE_KEY_TITLE, title);
        b.putString(BUNDLE_KEY_AUTHOR, author);
        b.putString(BUNDLE_KEY_GENRE, genre);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mTypeOfAction = TypeOfAction.EDIT;
            mId = args.getInt(BUNDLE_KEY_ID);
            mTitleText = args.getString(BUNDLE_KEY_TITLE);
            mAuthorText = args.getString(BUNDLE_KEY_AUTHOR);
            mGenreText = args.getString(BUNDLE_KEY_GENRE);
        } else {
            mTypeOfAction = TypeOfAction.INSERT;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_song_fragment, container, false);
        mTitle = (EditText)v.findViewById(R.id.editText_title);
        mAuthor = (EditText)v.findViewById(R.id.editText_author);
        mOk = (Button)v.findViewById(R.id.button_ok);
        mCancel = (Button)v.findViewById(R.id.button_cancel);
        mGenre = (EditText)v.findViewById(R.id.editText_genre);
        mOk.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        if (mTypeOfAction == TypeOfAction.EDIT) {
            mTitle.setText(mTitleText);
            mAuthor.setText(mAuthorText);
            mGenre.setText(mGenreText);
        }
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MainActivity)activity;
    }

    private boolean inputFieldsFilled() {
        if (!TextUtils.isEmpty(mTitle.getText()) && !TextUtils.isEmpty(mAuthor.getText())
                && !TextUtils.isEmpty(mGenre.getText())) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_cancel:
                dismiss();
                break;
            case R.id.button_ok:
                if (inputFieldsFilled()) {
                    if (mTypeOfAction == TypeOfAction.INSERT) {
                        mCallback.insertSong(mTitle.getText().toString(),
                                mAuthor.getText().toString(), mGenre.getText().toString());
                    } else {
                        mCallback.updateSong(mId, mTitle.getText().toString(),
                                mAuthor.getText().toString(), mGenre.getText().toString());
                    }
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Not all fields are set", Toast.LENGTH_SHORT)
                            .show();
                }
        }
    }
}
