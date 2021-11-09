package me.anhvannguyen.android.asimplelisttodo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.anhvannguyen.android.asimplelisttodo.data.TodoContract;

/**
 * Created by anhvannguyen on 6/26/15.
 */
public class TodoRecycleAdapter extends RecyclerView.Adapter<TodoRecycleAdapter.ViewHolder> {

    private Cursor mCursor;
    final private Context mContext;
    final private TodoAdapterOnClickHandler mClickHandler;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTodoTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTodoTextView = (TextView) itemView.findViewById(R.id.list_item_todo_textview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mClickHandler.onClick(this);
        }
    }


    public static interface TodoAdapterOnClickHandler {
        void onClick(ViewHolder viewHolder);
    }

    public TodoRecycleAdapter(Context context, TodoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public TodoRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_todo, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TodoRecycleAdapter.ViewHolder viewHolder, int i) {
        mCursor.moveToPosition(i);

        int textIndex = mCursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TEXT);
        String todoTextString = mCursor.getString(textIndex);
        int newLinePos = todoTextString.indexOf("\n");
        if (newLinePos != -1) {
            todoTextString = todoTextString.substring(0, newLinePos) + "...";
        }

        viewHolder.mTodoTextView.setText(todoTextString);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
