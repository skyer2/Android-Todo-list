package me.anhvannguyen.android.asimplelisttodo.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by anhvannguyen on 7/8/15.
 */
public class TodoQueryHandler extends AsyncQueryHandler {

    public TodoQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
    }
}
