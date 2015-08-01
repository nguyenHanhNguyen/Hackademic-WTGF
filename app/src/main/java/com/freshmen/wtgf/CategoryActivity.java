package com.freshmen.wtgf;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.freshmen.wtgf.App.DetailActivity;
import com.freshmen.wtgf.adapter.CategoryAdapter;
import com.freshmen.wtgf.object.Category;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CategoryActivity extends AppCompatActivity {
    private CategoryAdapter mAdapter;
    private final List<Category> mCategoryList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_category);

        mAdapter = new CategoryAdapter(this, mCategoryList);
        loadCategories();

        Toolbar tb = (Toolbar) findViewById(R.id.act_category_tb_toolbar);
        setSupportActionBar(tb);

        RecyclerView rv = (RecyclerView) findViewById(R.id.act_category_rv_list);

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(mAdapter);

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
            Intent youTubePlayer = new Intent(this, DetailActivity.class);
            startActivity(youTubePlayer);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadCategories() {
        new HttpRequestTask().execute();
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, Category[]> {
        @Override
        protected Category[] doInBackground(Void... params) {

            try {
                final String url = WTGF.SERVER_ADDRESS;
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Category[] categories = restTemplate.getForObject(url, Category[].class);
                mCategoryList.addAll(Arrays.asList(categories));

                Log.d("test", categories.length + "");

            } catch (Exception ignored) {
                Log.d("test", ignored.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Category[] categories) {
            super.onPostExecute(categories);

            mAdapter.notifyDataSetChanged();
        }
    }
}
