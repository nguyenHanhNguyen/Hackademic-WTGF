package com.freshmen.wtgf.App;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.freshmen.wtgf.R;
import com.freshmen.wtgf.WTGF;
import com.freshmen.wtgf.adapter.WorkoutAdapter;
import com.freshmen.wtgf.object.Workout;
import com.squareup.picasso.Picasso;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;


public class WorkoutActivity extends AppCompatActivity {
    private int mCategory;
    private String mCatName = "";

    private WorkoutAdapter mAdapter;
    private final List<Workout> mWorkoutList = new ArrayList<>();
    private final List<Bitmap>  mThumbList   = new ArrayList<>();

    private CircularProgressBar mProgress;
    private RecyclerView        mRecycler;
    private RelativeLayout      mProgressLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_workout);

        if (getIntent() != null) {
            mCategory = getIntent().getIntExtra(WTGF.SELECTED_CATEGORY_TAG, 0);
            mCatName = getIntent().getStringExtra(WTGF.SELECTED_CATEGORY_NAME_TAG);
        }

        mAdapter = new WorkoutAdapter(this, mWorkoutList, mThumbList);

        Toolbar tb = (Toolbar) findViewById(R.id.act_workout_tb_toolbar);
        setSupportActionBar(tb);
        assert getSupportActionBar() != null;
        if (!mCatName.equals("")) {
            getSupportActionBar().setTitle(mCatName);
        }
        tb.setNavigationIcon(R.drawable.ic_back);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mRecycler = (RecyclerView) findViewById(R.id.act_workout_rv_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                                                           false));
        mRecycler.setAdapter(mAdapter);

        mProgress = (CircularProgressBar) findViewById(R.id.act_workout_cpb_progress);

        mProgressLayout = (RelativeLayout) findViewById(R.id.act_workout_rv_progress);

        loadCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tracker) {
            Intent intent = new Intent(this, TrackerActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadCategories() {
        if (mCategory > 0) {
            mRecycler.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.VISIBLE);
            ((CircularProgressDrawable) mProgress.getIndeterminateDrawable()).start();
            new HttpRequestTask().execute(this);
        }
    }


    private class HttpRequestTask extends AsyncTask<Context, Void, Workout[]> {
        private Context mContext;

        @Override
        protected Workout[] doInBackground(Context... params) {
            mContext = params[0];
            try {
                final String url = WTGF.SERVER_ADDRESS + "category=" + mCategory;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Workout[] workouts = restTemplate.getForObject(url, Workout[].class);
                mWorkoutList.addAll(Arrays.asList(workouts));
            } catch (Exception ignored) {}

            return null;
        }

        @Override
        protected void onPostExecute(Workout[] workouts) {
            super.onPostExecute(workouts);

            mProgressLayout.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
            ((CircularProgressDrawable) mProgress.getIndeterminateDrawable()).progressiveStop();

            for (int i = 0; i < mWorkoutList.size(); i++) {
                mThumbList.add(null);
            }

            mAdapter.notifyDataSetChanged();

            for (int i = 0; i < mWorkoutList.size(); i++) {
                new LoadThumbnailTask().execute(mContext,
                                                mWorkoutList.get(i).getVideo_thumbnail(),
                                                i);
            }
        }
    }

    private class LoadThumbnailTask extends AsyncTask<Object, Void, Integer> {
        private Context mContext;

        @Override
        protected Integer doInBackground(Object... params) {
            mContext = (Context) params[0];
            String url = (String) params[1];
            Integer pos = (Integer) params[2];
            try {
                mThumbList.set(pos, Picasso.with(mContext).load(url).get());
            } catch (Exception ignored) {}

            return pos;
        }

        @Override
        protected void onPostExecute(Integer pos) {
            super.onPostExecute(pos);

            mAdapter.notifyItemChanged(pos);
        }
    }
}
