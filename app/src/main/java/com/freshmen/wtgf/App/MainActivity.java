package com.freshmen.wtgf.App;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

import com.freshmen.wtgf.R;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends AppCompatActivity {

    private Uri uriYouTube;
    private VideoView videoView;
    private MediaController vidControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = (VideoView) findViewById(R.id.video_view);

        vidControl = new MediaController(MainActivity.this);
        videoView.setMediaController(vidControl);
        videoView.requestFocus();
        //videoView.start();

        /*Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/"
                + R.raw.sample);
        videoView.setVideoURI(uri);
        videoView.start();*/

        if (savedInstanceState != null) {
            int loc = savedInstanceState.getInt("Loc");
            Log.i("Loaction: ", loc + "");
            uriYouTube = Uri.parse(savedInstanceState.getString("url"));
            videoView.setVideoURI(uriYouTube);
            videoView.seekTo(loc);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.v("onPrepared", "ok");
                    mp.start();
                }
            });
        } else {
            RTSPUrlTask truitonTask = new RTSPUrlTask();
            truitonTask.execute("http://www.youtube.com/watch?v=2zNSgSzhBfM");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("loc",videoView.getCurrentPosition());
        outState.putString("url",uriYouTube.toString());
    }

    void startPlaying(String url) {
        uriYouTube = Uri.parse(url);
        videoView.setVideoURI(uriYouTube);
        videoView.start();
    }

    private class RTSPUrlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = getRTSPVideoUrl(urls[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            startPlaying(result);
        }

        public String getRTSPVideoUrl(String urlYoutube) {
            try {
                String gdy = "http://gdata.youtube.com/feeds/api/videos/";
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                String id = extractYoutubeId(urlYoutube);
                URL url = new URL(gdy + id);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                Document doc = dBuilder.parse(connection.getInputStream());
                Element el = doc.getDocumentElement();
                NodeList list = el.getElementsByTagName("media:content");
                String cursor = urlYoutube;
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (node != null) {
                        NamedNodeMap nodeMap = node.getAttributes();
                        HashMap<String, String> maps = new HashMap<String, String>();
                        for (int j = 0; j < nodeMap.getLength(); j++) {
                            Attr att = (Attr) nodeMap.item(j);
                            maps.put(att.getName(), att.getValue());
                        }
                        if (maps.containsKey("yt:format")) {
                            String f = maps.get("yt:format");
                            if (maps.containsKey("url"))
                                cursor = maps.get("url");
                            if (f.equals("1"))
                                return cursor;
                        }
                    }
                }
                return cursor;
            } catch (Exception ex) {
                return urlYoutube;
            }
        }

        public String extractYoutubeId(String url) throws MalformedURLException {
            String query = new URL(url).getQuery();
            String[] param = query.split("&");
            String id = null;
            for (String row : param) {
                String[] param1 = row.split("=");
                if (param1[0].equals("v")) {
                    id = param1[1];
                }
            }
            return id;
        }
    }
}
