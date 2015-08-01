package com.freshmen.wtgf.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshmen.wtgf.R;
import com.freshmen.wtgf.WTGF;
import com.freshmen.wtgf.App.WorkoutActivity;
import com.freshmen.wtgf.object.Category;

import java.util.List;

/**
 * Created by Hung on 01/08/2015.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context        mContext;
    private List<Category> mCategoryList;


    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.mContext = context;
        this.mCategoryList = categoryList;
    }


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_category,
                                                       viewGroup, false);

        return new CategoryViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder viewHolder, int position) {
        Category c = mCategoryList.get(position);

        if (c.getWorkoutNumber() < 0) {
            viewHolder.count.setText("0");
        } else {
            viewHolder.count.setText(String.valueOf(c.getWorkoutNumber()));
        }

        if (c.getDescription() == null || c.getDescription().length() == 0 ||
            c.getDescription().trim().equals("")) {
            viewHolder.desc.setText("NULL");
        } else {
            viewHolder.desc.setText(c.getDescription());
        }

        if (c.getName() == null || c.getName().length() == 0 || c.getName().trim().equals("")) {
            viewHolder.name.setText("NULL");
        } else {
            viewHolder.name.setText(c.getName());
        }
    }


    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView count;
        public TextView desc;
        public TextView name;


        public CategoryViewHolder(View view) {
            super(view);

            count = (TextView) view.findViewById(R.id.act_category_tv_workout_count);
            desc = (TextView) view.findViewById(R.id.act_category_tv_item_desc);
            name = (TextView) view.findViewById(R.id.act_category_tv_item_name);

            LinearLayout ll = (LinearLayout) view.findViewById(R.id.act_category_ll_item);
            ll.setOnClickListener(this);
            ViewCompat.setElevation(ll, 16F);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, WorkoutActivity.class);
            intent.putExtra(WTGF.SELECTED_CATEGORY_TAG, getAdapterPosition() + 1);
            intent.putExtra(WTGF.SELECTED_CATEGORY_NAME_TAG,
                            mCategoryList.get(getAdapterPosition()).getName());
            mContext.startActivity(intent);
        }
    }
}
