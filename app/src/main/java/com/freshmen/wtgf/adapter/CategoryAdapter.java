package com.freshmen.wtgf.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshmen.wtgf.R;
import com.freshmen.wtgf.object.Category;

import java.util.List;

/**
 * Created by Hung on 01/08/2015.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>
        implements View.OnClickListener{
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

        if (c.getName() == null || c.getName().length() == 0 || c.getName().trim().equals("")) {
            viewHolder.name.setText("NULL");
        } else {
            viewHolder.name.setText(c.getName());
        }

        if (c.getDescription() == null || c.getDescription().length() == 0 ||
            c.getDescription().trim().equals("")) {
            viewHolder.desc.setText("NULL");
        } else {
            viewHolder.desc.setText(c.getDescription());
        }

        viewHolder.categoryItemLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(mContext, );
//        mContext.startActivity(intent);
        Log.d("test", "On Item Click");

    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView desc;
        public LinearLayout categoryItemLinearLayout;


        public CategoryViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.act_category_tv_item_name);
            desc = (TextView) view.findViewById(R.id.act_category_tv_item_desc);
            categoryItemLinearLayout = (LinearLayout) view.findViewById(R.id.act_category_ll_item);

            ViewCompat.setElevation(categoryItemLinearLayout, 16F);
        }
    }
}
