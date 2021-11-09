package me.anhvannguyen.android.asimplelisttodo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import me.anhvannguyen.android.asimplelisttodo.data.TodoContract;
import me.anhvannguyen.android.asimplelisttodo.data.TodoQueryHandler;


public class EditorActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = EditorActivityFragment.class.getSimpleName();
    public static final String TODO_ITEM = "todo_item";
    private static final int TODO_EDIT_LOADER = 0;

    private String mActionString;
    private String mOldString;
    private EditText mEditorEditText;
    private Uri mUri;
    private TodoQueryHandler mQueryHandler;

    public EditorActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (mActionString.equals(Intent.ACTION_EDIT)) {
            inflater.inflate(R.menu.menu_editor_fragment, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete_item:
                deleteTodo();
                break;
            case android.R.id.home:
                finishEdit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_editor, container, false);

        mEditorEditText = (EditText) rootView.findViewById(R.id.todo_item_edittext);
        Intent intent = getActivity().getIntent();

        mUri = intent.getParcelableExtra(TODO_ITEM);

        if (mUri == null) {
            mActionString = Intent.ACTION_INSERT;
            getActivity().setTitle(getString(R.string.new_item_title));
            mOldString = null;
        } else {
            mActionString = Intent.ACTION_EDIT;
        }

        mQueryHandler = new TodoQueryHandler(getActivity().getContentResolver());
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TODO_EDIT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void finishEdit() {
        String newText = mEditorEditText.getText().toString().trim();

        switch (mActionString) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                } else {
                    insertItem(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteTodo();
                } else if (mOldString.equals(newText)) {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                } else {
                    updateTodo(newText);
                }
                break;

        }
        getActivity().finish();
    }

    private void updateTodo(String newText) {
        if (mUri == null) {
            return;
        }
        ContentValues newValue = new ContentValues();
        newValue.put(TodoContract.TodoEntry.COLUMN_TEXT, newText);
        mQueryHandler.startUpdate(
                -1,
                null,
                mUri,
                newValue,
                null,
                null
        );
//        getActivity().getContentResolver().update(
//                mUri,
//                newValue,
//                null,
//                null
//        );
    }

    private void deleteTodo() {
        mQueryHandler.startDelete(
                -1,
                null,
                mUri,
                null,
                null
        );
//        getActivity().getContentResolver().delete(
//                mUri,
//                null,
//                null
//        );
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    private void insertItem(String todoText) {
        ContentValues newValue = new ContentValues();

        newValue.put(TodoContract.TodoEntry.COLUMN_TEXT, todoText);
        mQueryHandler.startInsert(
                -1,
                null,
                TodoContract.TodoEntry.CONTENT_URI,
                newValue);
//        getActivity().getContentResolver().insert(
//                TodoContract.TodoEntry.CONTENT_URI,
//                newValue
//        );
        getActivity().setResult(Activity.RESULT_OK);
    }

    public void onBackPressed() {
        finishEdit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            int textIndex = data.getColumnIndex(TodoContract.TodoEntry.COLUMN_TEXT);
            mOldString = data.getString(textIndex);
            mEditorEditText.setText(mOldString);
            mEditorEditText.requestFocus();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
