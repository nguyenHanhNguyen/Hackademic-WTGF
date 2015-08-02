package com.freshmen.wtgf.App;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.freshmen.wtgf.Config.Config;
import com.freshmen.wtgf.R;
import com.freshmen.wtgf.WTGF;
import com.freshmen.wtgf.object.Workout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class DetailActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    public static final String TAG = "DETAIL_ACTIVITY";
    TextView txt_workout_desc;
    TextView txt_workout_calories;
    TextView txt_calories_burn;
    RoundCornerProgressBar progressBar;
    public android.support.v7.app.ActionBar actionBar;
    public String youtube_code;
    public int video_duration;
    public int progress = 0;
    public int total_calories;
    public float calories_burn = 0;
    public float calories_per_second;
    public Button btn_tracker;
    public Button btn_fb;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        FacebookSdk.setApplicationId("1004795812898096");
        FacebookSdk.sdkInitialize(this);

        Intent detail_offer = getIntent();
        int workout_id = detail_offer.getIntExtra(WTGF.SELECTED_WORKOUT_TAG, 0);
        Log.d("workout id", String.valueOf(workout_id));

        final String url = detail_offer.getStringExtra(WTGF.SELECTED_WORKOUT_VIDEO_TAG);

        int startIndex = url.indexOf("=");
        youtube_code = url.substring(startIndex + 1, url.length());
        Log.d("Code", youtube_code);

        Toolbar tb = (Toolbar) findViewById(R.id.act_category_tb_toolbar);
        setSupportActionBar(tb);

        actionBar = getSupportActionBar();

        txt_calories_burn = (TextView) findViewById(R.id.txt_calories_burn);
        txt_calories_burn.setText("0 Calories");
        btn_tracker = (Button) findViewById(R.id.btn_start_tracker);
        btn_tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracker();
            }
        });

        btn_fb = (Button)findViewById(R.id.btn_post_fb);

        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postToFaceBook(url);
            }
        });

        //load workout info
        LoadInfo loadInfo = new LoadInfo(workout_id);
        loadInfo.setWorkout_id(workout_id);
        loadInfo.execute();

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Config.DEVELOPER_KEY, this);
        youTubePlayerFragment.setRetainInstance(true);

        this.txt_workout_desc = (TextView) findViewById(R.id.txt_desc);
        this.txt_workout_calories = (TextView) findViewById(R.id.txt_calories);

        this.progressBar = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        progressBar.setProgressColor(Color.parseColor("#4CAF50"));
        progressBar.setBackgroundColor(Color.parseColor("#C8E6C9"));
        progressBar.setPadding(10);
        progressBar.setRadius(30);
    }

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    public void postToFaceBook(final String video_url){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken == null) {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("test", "Login success " + loginResult.getAccessToken().getToken());
                    DetailActivity.this.showShareDialogWithContent(video_url);
                }

                @Override
                public void onCancel() {
                    Log.d("test", "Cancel");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.d("test", "Error " + e.getLocalizedMessage());
                }
            });

            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        } else {
            DetailActivity.this.showShareDialogWithContent(video_url);
        }

    }

    public void showShareDialogWithContent(String video_url){

        shareDialog = new ShareDialog(this);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(video_url))
                .setContentTitle("WTGF")
                .setContentDescription("I have completed this workout. And I dare yo :D")
                .build();

        shareDialog.show(content);

    }


    public void startTracker() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("test", "Progress " + progress);
                Log.d("test", "Progress " + video_duration);
                btn_tracker.setClickable(false);
                while (progress < video_duration) {
                    Log.d("video duration", String.valueOf(video_duration));
                    progress += 1;
                    calories_burn += calories_per_second;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progress);
                            txt_calories_burn.setText(String.format("%.2f", calories_burn) + " Calories");
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            youTubePlayer.loadVideo(youtube_code);

            // Show player controls
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

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
        } else {
            if (data != null && callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return this.youTubePlayerFragment;
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
            total_calories = workout.getEstimated_calories();
            video_duration = workout.getVideo_duration_in_second();
            calories_per_second = (float) total_calories / video_duration;
            Log.d("second", String.valueOf(calories_per_second));
            progressBar.setMax(video_duration);
            super.onPostExecute(workout);
        }
    }
}
