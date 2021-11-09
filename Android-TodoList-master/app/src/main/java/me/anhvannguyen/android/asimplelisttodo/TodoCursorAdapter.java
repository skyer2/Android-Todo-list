package me.anhvannguyen.android.asimplelisttodo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import me.anhvannguyen.android.asimplelisttodo.data.TodoContract;

public class TodoCursorAdapter extends CursorAdapter {

    public TodoCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_todo, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int todoIndex = cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TEXT);
        String todoItem = cursor.getString(todoIndex);
        int newLinePos = todoItem.indexOf("\n");
        if (newLinePos != -1) {
            todoItem = todoItem.substring(0, newLinePos) + "...";
        }
        viewHolder.mTodoItemTextView.setText(todoItem);

    }

    // Viewholder to cache the children view
    public static class ViewHolder {
        TextView mTodoItemTextView;

        public ViewHolder(View view) {
            mTodoItemTextView = (TextView) view.findViewById(R.id.list_item_todo_textview);
        }
    }
}
