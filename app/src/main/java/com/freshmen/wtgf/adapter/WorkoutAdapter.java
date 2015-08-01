package com.freshmen.wtgf.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freshmen.wtgf.App.DetailActivity;
import com.freshmen.wtgf.R;
import com.freshmen.wtgf.WTGF;
import com.freshmen.wtgf.object.Workout;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Hung on 01/08/2015.
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private Context       mContext;
    private List<Workout> mWorkoutList;
    private List<Bitmap>  mThumbList;


    public WorkoutAdapter(Context context, List<Workout> workoutList, List<Bitmap> thumbList) {
        this.mContext = context;
        this.mWorkoutList = workoutList;
        this.mThumbList = thumbList;
    }


    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_workout,
                                                       viewGroup, false);

        return new WorkoutViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mWorkoutList.size();
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder viewHolder, int position) {
        Workout w = mWorkoutList.get(position);

        if (w.getName() == null || w.getName().length() == 0 || w.getName().trim().equals("")) {
            viewHolder.name.setText("NULL");
        } else {
            viewHolder.name.setText(w.getName());
        }

        if (w.getVideo_duration() == null || w.getVideo_duration().length() == 0 ||
            w.getVideo_duration().trim().equals("")) {
            viewHolder.time.setText("NULL");
        } else {
            viewHolder.time.setText(w.getVideo_duration());
        }

        if (w.getEstimated_calories() <= 0) {
            viewHolder.calories.setText("NULL");
        } else {
            viewHolder.calories.setText(String.valueOf(w.getEstimated_calories()));
        }

        if (mThumbList.get(position) == null || position >= mThumbList.size()) {
            viewHolder.image.setImageBitmap(null);
        } else {
            viewHolder.image.setImageBitmap(mThumbList.get(position));
        }
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundedImageView image;
        public TextView         name;
        public TextView         time;
        public TextView         calories;
        ;


        public WorkoutViewHolder(View view) {
            super(view);

            image = (RoundedImageView) view.findViewById(R.id.act_workout_riv_thumb);
            name = (TextView) view.findViewById(R.id.act_workout_tv_name);
            time = (TextView) view.findViewById(R.id.act_workout_tv_time);
            calories = (TextView) view.findViewById(R.id.act_workout_tv_calories);
            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.act_workout_ll_item);
            rl.setOnClickListener(this);
            ViewCompat.setElevation(rl, 16F);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(WTGF.SELECTED_WORKOUT_TAG, getAdapterPosition() + 1);
            intent.putExtra(WTGF.SELECTED_WORKOUT_VIDEO_TAG,
                            mWorkoutList.get(getAdapterPosition()).getVideo_url());
//            mContext.startActivity(intent);
        }
    }
}
