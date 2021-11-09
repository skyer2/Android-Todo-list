package me.anhvannguyen.android.asimplelisttodo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class TodoProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBOpenHelper mOpenHelper;

    private static final int TODO = 100;
    private static final int TODO_WITH_ID = 101;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBOpenHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TodoContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TodoContract.PATH_TODO, TODO);
        matcher.addURI(authority, TodoContract.PATH_TODO + "/#", TODO_WITH_ID);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TODO:
                return TodoContract.TodoEntry.CONTENT_TYPE;
            case TODO_WITH_ID:
                return TodoContract.TodoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        switch (match) {
            case TODO:
                retCursor = db.query(
                        TodoContract.TodoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TODO_WITH_ID:
                String todoId = TodoContract.TodoEntry.getTodoId(uri);
                retCursor = db.query(
                        TodoContract.TodoEntry.TABLE_NAME,
                        projection,
                        TodoContract.TodoEntry._ID + " = ?",
                        new String[]{todoId},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (match) {
            case TODO: {
                long _id = db.insert(
                        TodoContract.TodoEntry.TABLE_NAME,
                        null,
                        values
                );
                if (_id > 0) {
                    returnUri = TodoContract.TodoEntry.buildTodoUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";

        switch (match) {
            case TODO: {
                rowsDeleted = db.delete(
                        TodoContract.TodoEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case TODO_WITH_ID: {
                String todoId = TodoContract.TodoEntry.getTodoId(uri);
                rowsDeleted = db.delete(
                        TodoContract.TodoEntry.TABLE_NAME,
                        TodoContract.TodoEntry._ID + " = ?",
                        new String[]{todoId}
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (match) {
            case TODO: {
                rowsUpdated = db.update(
                        TodoContract.TodoEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case TODO_WITH_ID: {
                String todoId = TodoContract.TodoEntry.getTodoId(uri);
                rowsUpdated = db.update(
                        TodoContract.TodoEntry.TABLE_NAME,
                        values,
                        TodoContract.TodoEntry._ID + " = ?",
                        new String[]{todoId}
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(
                                TodoContract.TodoEntry.TABLE_NAME,
                                null,
                                value
                        );
                        if (_id > 0) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);

        }
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
