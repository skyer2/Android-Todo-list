package me.anhvannguyen.android.asimplelisttodo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.anhvannguyen.android.asimplelisttodo.data.TodoContract;
import me.anhvannguyen.android.asimplelisttodo.data.TodoQueryHandler;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int LIST_TODO_LOADER = 0;
    private static final int EDITOR_REQUEST_CODE = 100;

    private static final String[] TODO_PROJECTION = {
            TodoContract.TodoEntry._ID,
            TodoContract.TodoEntry.COLUMN_TEXT,
            TodoContract.TodoEntry.COLUMN_CREATED
    };

    private static final int COL_ID = 0;
    private static final int COL_TEXT = 1;
    private static final int COL_CREATED = 2;


//    private ListView mTodoListView;
    private RecyclerView mTodoRecyclerView;
//    private TodoCursorAdapter mCursorAdapter;
    private TodoRecycleAdapter mRecycleAdapter;
    private FloatingActionButton mAddButton;
    private CoordinatorLayout mCoordinatorLayout;

    private Cursor mTempDataCursor;
    private TodoQueryHandler mQueryHandler;

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LIST_TODO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        mCursorAdapter = new TodoCursorAdapter(
//                getActivity(),
//                null,
//                0
//        );
        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout);

        mRecycleAdapter = new TodoRecycleAdapter(getActivity(), new TodoRecycleAdapter.TodoAdapterOnClickHandler() {
            @Override
            public void onClick(TodoRecycleAdapter.ViewHolder viewHolder) {
                if (mRecycleAdapter.getCursor() != null) {
                    int idIndex = mRecycleAdapter.getCursor().getColumnIndex(TodoContract.TodoEntry._ID);
                    long id = mRecycleAdapter.getCursor().getLong(idIndex);
                    Intent intent = new Intent(getActivity(), EditorActivity.class);
                    Uri uri = TodoContract.TodoEntry.buildTodoUri(id);
                    intent.putExtra(EditorActivityFragment.TODO_ITEM, uri);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            }
        });

//        mTodoListView = (ListView) rootView.findViewById(R.id.todo_listview);
//        mTodoListView.setAdapter(mCursorAdapter);
//        mTodoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), EditorActivity.class);
//                Uri uri = TodoContract.TodoEntry.buildTodoUri(id);
//                intent.putExtra(EditorActivityFragment.TODO_ITEM, uri);
//                startActivityForResult(intent, EDITOR_REQUEST_CODE);
//
//            }
//        });

        mAddButton = (FloatingActionButton) rootView.findViewById(R.id.add_fab);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditor();
            }
        });

        mTodoRecyclerView = (RecyclerView) rootView.findViewById(R.id.todo_recycleview);
        // improve performance if the content of the layout
        // does not change the size of the RecyclerView
        mTodoRecyclerView.setHasFixedSize(true);

        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTodoRecyclerView.setAdapter(mRecycleAdapter);

        mQueryHandler = new TodoQueryHandler(getActivity().getContentResolver());

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
//            case R.id.action_create_item:
//                openEditor();
//                break;
//            case R.id.action_create_sample:
//                generateTodoSample();
//                break;
            case R.id.action_delete_all:
                if (mRecycleAdapter.getItemCount() == 0) {
                    Snackbar.make(mCoordinatorLayout, "Nothing to delete", Snackbar.LENGTH_SHORT)
                            .show();
                    break;
                }
                final List<ContentValues> tempList = new ArrayList<ContentValues>();
                mTempDataCursor = getActivity().getContentResolver().query(
                        TodoContract.TodoEntry.CONTENT_URI,
                        TODO_PROJECTION,
                        null,
                        null,
                        null
                );
                while (mTempDataCursor.moveToNext()) {
                    String todoItem = mTempDataCursor.getString(COL_TEXT);
                    String created = mTempDataCursor.getString(COL_CREATED);
                    ContentValues value = new ContentValues();
                    value.put(TodoContract.TodoEntry.COLUMN_TEXT, todoItem);
                    value.put(TodoContract.TodoEntry.COLUMN_CREATED, created);
                    tempList.add(value);
                }
                Snackbar.make(mCoordinatorLayout, "All items deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // User undo delete, add all items back to database
                                ContentValues[] values = new ContentValues[tempList.size()];
                                tempList.toArray(values);
                                getActivity().getContentResolver().bulkInsert(
                                        TodoContract.TodoEntry.CONTENT_URI,
                                        values
                                );
                                // Clean up the temp items
                                tempList.clear();
                                mTempDataCursor.close();
                                restartLoader();
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.red))
                        .show();
                // Delete all the items
                deleteAllTodo();
                // Restart the loader
                restartLoader();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditor() {
        Intent intent = new Intent(getActivity(), EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    private void deleteAllTodo() {
        mQueryHandler.startDelete(
                -1,
                null,
                TodoContract.TodoEntry.CONTENT_URI,
                null,
                null
        );
        restartLoader();
    }

//    private void generateTodoSample() {
//        insertTodo("Something something");
//        insertTodo("Another junk \nbelow");
//        insertTodo("Something something blah blah junk junk, stuff do do do do lalalalalalala");
//        restartLoader();
//    }


    @Override
    public void onPause() {
        super.onPause();
        if (mTempDataCursor != null) {
            mTempDataCursor.close();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LIST_TODO_LOADER, null, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            restartLoader();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = TodoContract.TodoEntry.COLUMN_CREATED + " DESC";
        return new CursorLoader(
                getActivity(),
                TodoContract.TodoEntry.CONTENT_URI,
                TODO_PROJECTION,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mCursorAdapter.swapCursor(data);
        mRecycleAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mCursorAdapter.swapCursor(null);
        mRecycleAdapter.swapCursor(null);
    }
}
