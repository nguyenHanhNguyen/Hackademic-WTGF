package com.freshmen.wtgf.App;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.freshmen.wtgf.R;
import com.freshmen.wtgf.object.Workout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * Created by Hung on 01/08/2015.
 */
public class TrackerActivity extends AppCompatActivity {
    private String[] mDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private BarChart mChart;

    private CircularProgressBar mProgress;
    private LinearLayout        mInfo;
    private RelativeLayout      mProgressLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_tracker);

        Toolbar tb = (Toolbar) findViewById(R.id.act_tracker_tb_toolbar);
        setSupportActionBar(tb);
        tb.setNavigationIcon(R.drawable.ic_back);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mInfo = (LinearLayout) findViewById(R.id.act_tracker_ll_info);
        mProgress = (CircularProgressBar) findViewById(R.id.act_tracker_cpb_progress);
        mProgressLayout = (RelativeLayout) findViewById(R.id.act_tracker_rv_progress);

        mChart = (BarChart) findViewById(R.id.act_tracker_bc_chart);

        mChart.setDescription(getResources().getString(R.string.act_tracker_bc_desc));
        mChart.setNoDataTextDescription(getResources().getString(R.string.act_tracker_bc_no_data_desc));
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);

        List<String> strings = Arrays.asList(mDays);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(10, 0));
        entries.add(new BarEntry(8, 1));
        entries.add(new BarEntry(5, 2));
        entries.add(new BarEntry(3, 3));
        entries.add(new BarEntry(9, 4));
        BarDataSet set = new BarDataSet(entries, "cal");
        mChart.setData(new BarData(strings, set));
    }


    public void loadData() {
        mInfo.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
        ((CircularProgressDrawable) mProgress.getIndeterminateDrawable()).start();
    }

    private class LoadUsageTask extends AsyncTask<Context, Void, Workout[]> {
        private Context mContext;

        @Override
        protected Workout[] doInBackground(Context... params) {
            mContext = params[0];
            try {
            } catch (Exception ignored) {}

            return null;
        }

        @Override
        protected void onPostExecute(Workout[] workouts) {
            super.onPostExecute(workouts);

            mProgressLayout.setVisibility(View.GONE);
            mInfo.setVisibility(View.VISIBLE);
            ((CircularProgressDrawable) mProgress.getIndeterminateDrawable()).progressiveStop();
        }
    }
}
