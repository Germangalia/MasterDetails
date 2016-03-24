package com.ggalia84.masterdetails.Adapters;

/**
 * Created by ggalia84 on 24/03/16.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import com.ggalia84.masterdetails.ItemDetailActivity;
import com.ggalia84.masterdetails.ItemDetailFragment;
import com.ggalia84.masterdetails.ItemListActivity;
import com.ggalia84.masterdetails.Models.TodoItem;
import com.ggalia84.masterdetails.Models.TodoListViewHolder;
import com.ggalia84.masterdetails.R;

/**
 * Created by adam on 12/03/16.
 */
public class ListAdapter extends RecyclerView.Adapter<TodoListViewHolder> {
    private final List<TodoItem> mValues;
    private final FragmentActivity activity;
    int urgentColor = Color.parseColor("#fe0000");
    int mediumColor = Color.parseColor("#0099ff");
    int notUrgentColor = Color.parseColor("#00ff19");

    public ListAdapter(List<TodoItem> items, Activity activity) {
        mValues = items;
        this.activity =(FragmentActivity) activity;
    }

    @Override
    public TodoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new TodoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TodoListViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).name);
        holder.mDoneView.setChecked(mValues.get(position).done);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch (mValues.get(position).priority){
                case 1: holder.mDoneView.setButtonTintList(ColorStateList.valueOf(urgentColor));
                    break;
                case 2: holder.mDoneView.setButtonTintList(ColorStateList.valueOf(mediumColor));
                    break;
                case 3: holder.mDoneView.setButtonTintList(ColorStateList.valueOf(notUrgentColor));
                    break;
            }
        } else {
            switch (mValues.get(position).priority){
                case 1: holder.mDoneView.setBackgroundColor(urgentColor);
                    break;
                case 2: holder.mDoneView.setBackgroundColor(mediumColor);
                    break;
                case 3: holder.mDoneView.setBackgroundColor(notUrgentColor);
                    break;
            }
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ItemListActivity.mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}