package com.freshmen.wtgf.App;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freshmen.wtgf.R;
import com.freshmen.wtgf.WTGF;
import com.freshmen.wtgf.object.WeekHistory;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.LargeValueFormatter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * Created by Hung on 01/08/2015.
 */
public class TrackerActivity extends AppCompatActivity {
    private String[] mDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private BarChart mChart;

    private List<BarEntry> mEntries = new ArrayList<>();

    private CircularProgressBar mProgress;
    private LinearLayout        mInfo;
    private RelativeLayout      mProgressLayout;
    private TextView            mTotalCalo;


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
        mTotalCalo = (TextView) findViewById(R.id.act_tracker_tv_total_burn);

        mChart = (BarChart) findViewById(R.id.act_tracker_bc_chart);
        mChart.setDescription(getResources().getString(R.string.act_tracker_bc_desc));
        mChart.setNoDataTextDescription(getResources().getString(R.string.act_tracker_bc_no_data_desc));
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setScaleMinima(1f, 1f);
        mChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());
        mChart.getAxisRight().setEnabled(false);
        mChart.setGridBackgroundColor(getResources().getColor(R.color.background));

        List<String> days = Arrays.asList(mDays);
        BarDataSet data = new BarDataSet(mEntries,
                                         getResources().getString(R.string.act_tracker_bc_legend));
//        data.setColors(new int[]{R.color.primary, R.color.primary_dark, R.color.primary_light,
//                                 R.color.accent}, this);
        data.setColors(ColorTemplate.COLORFUL_COLORS);
        data.setDrawValues(false);
        data.setValueFormatter(new LargeValueFormatter());
        mChart.setData(new BarData(days, data));

        loadData();
    }


    public void loadData() {
        mInfo.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
        ((CircularProgressDrawable) mProgress.getIndeterminateDrawable()).start();

        new LoadUsageTask().execute();
    }

    private class LoadUsageTask extends AsyncTask<Void, Void, WeekHistory> {
        @Override
        protected WeekHistory doInBackground(Void... params) {
            String url = WTGF.SERVER_ADDRESS.concat("total_calo_week=1").concat("/");
            WeekHistory weekHistory = null;
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                weekHistory = restTemplate.getForObject(url, WeekHistory.class);
            } catch (Exception ignored) {
                Log.d("test", ignored.getMessage());
            }
            return weekHistory;
        }

        @Override
        protected void onPostExecute(WeekHistory weekHistory) {
            super.onPostExecute(weekHistory);

            if (weekHistory != null) {
                mEntries.add(new BarEntry(weekHistory.getMonday(), 0));
                mEntries.add(new BarEntry(weekHistory.getTuesday(), 1));
                mEntries.add(new BarEntry(weekHistory.getWednesday(), 2));
                mEntries.add(new BarEntry(weekHistory.getThursday(), 3));
                mEntries.add(new BarEntry(weekHistory.getFriday(), 4));
                mEntries.add(new BarEntry(weekHistory.getSaturday(), 5));
                mEntries.add(new BarEntry(weekHistory.getSunday(), 6));

                mChart.getAxisLeft().resetAxisMaxValue();
                mChart.notifyDataSetChanged();

                Calendar calendar = Calendar.getInstance();
                int day = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
                mTotalCalo.setText(String.valueOf((int) mEntries.get(day).getVal()));
            }

            mProgressLayout.setVisibility(View.GONE);
            mInfo.setVisibility(View.VISIBLE);
            ((CircularProgressDrawable) mProgress.getIndeterminateDrawable()).progressiveStop();
        }
    }
}
