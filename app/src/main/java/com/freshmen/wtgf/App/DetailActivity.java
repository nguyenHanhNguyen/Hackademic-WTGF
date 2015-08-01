package com.freshmen.wtgf.App;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.freshmen.wtgf.Config.Config;
import com.freshmen.wtgf.R;
import com.freshmen.wtgf.WTGF;
import com.freshmen.wtgf.object.Workout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class DetailActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    public static final String TAG = "DETAIL_ACTIVITY";
    TextView txt_workout_name;
    TextView txt_workout_desc;
    TextView txt_workout_calories;
    RoundCornerProgressBar progressBar;
    public int process;
    public android.support.v7.app.ActionBar actionBar;
    public String youtube_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent detail_offer = getIntent();
        int workout_id = detail_offer.getIntExtra(WTGF.SELECTED_WORKOUT_TAG, 0);
        Log.d("workout id", String.valueOf(workout_id));

        String url = detail_offer.getStringExtra(WTGF.SELECTED_WORKOUT_VIDEO_TAG);

        int startIndex = url.indexOf("=");
        youtube_code = url.substring(startIndex + 1, url.length());
        Log.d("Code", youtube_code);

        Toolbar tb = (Toolbar) findViewById(R.id.act_category_tb_toolbar);
        setSupportActionBar(tb);

        actionBar = getSupportActionBar();


        //load workout info
        LoadInfo loadInfo = new LoadInfo(workout_id);
        loadInfo.setWorkout_id(workout_id);
        loadInfo.execute();

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Config.DEVELOPER_KEY, this);

        this.txt_workout_desc = (TextView) findViewById(R.id.txt_desc);
        this.txt_workout_calories = (TextView) findViewById(R.id.txt_calories);

        this.progressBar = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        progressBar.setProgressColor(Color.parseColor("#8BC34A"));
        progressBar.setBackgroundColor(Color.parseColor("#808080"));
        progressBar.setPadding(5);
        progressBar.setMax(70);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically

            youTubePlayer.loadVideo(Config.YOUTUBE_VIDEO_CODE);

            // Hiding player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            process = youTubePlayer.getCurrentTimeMillis();
            progressBar.setProgress(process);

            //player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.DEVELOPER_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return this.youTubePlayerFragment;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadInfo extends AsyncTask<Void, Void, Workout> {

        int workout_id;

        public LoadInfo(int workout_id) {
            this.workout_id = workout_id;
        }

        public void setWorkout_id(int workout_id) {
            this.workout_id = workout_id;
        }



        @Override
        protected Workout doInBackground(Void... params) {
            setWorkout_id(workout_id);
            String url = WTGF.WORK_OUT_LINK.concat(String.valueOf(workout_id)).concat("/");
            Workout workout = null;

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Log.d("url", url);
                workout = restTemplate.getForObject(url, Workout.class);
                return workout;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return workout;
        }

        @Override
        protected void onPostExecute(Workout workout) {
            Log.d("Workout name", workout.getName());

            actionBar.setTitle(workout.getName());
            txt_workout_desc.setText(workout.getDescription());
            txt_workout_calories.setText(String.valueOf(workout.getEstimated_calories()));

            super.onPostExecute(workout);
        }
    }
}
