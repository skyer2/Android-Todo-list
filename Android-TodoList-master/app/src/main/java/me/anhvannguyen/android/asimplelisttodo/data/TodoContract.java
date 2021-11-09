package me.anhvannguyen.android.asimplelisttodo.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TodoContract {
    // Content provider URI
    public static final String CONTENT_AUTHORITY = "me.anhvannguyen.android.asimplelisttodo";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODO = "todo";

    public static final class TodoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TODO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public static final String TABLE_NAME = "todo";

        public static final String COLUMN_TEXT = "todo_text";

        public static final String COLUMN_CREATED = "date_created";

        public static final String getTodoId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildTodoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
