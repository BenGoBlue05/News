package com.example.android.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ArticlesFragment extends Fragment {

    private ArticleAdapter mArticleAdapter;
    private int mMaxNumResults = 15;

    public ArticlesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mArticleAdapter = new ArticleAdapter(getActivity(), new ArrayList<Article>());

        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_articles);
        listView.setAdapter(mArticleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Article article = mArticleAdapter.getItem(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getWebURLStr()));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateArticles();
    }

    private void updateArticles() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String searchWord = preferences.getString(getString(R.string.search_word_key),
                getString(R.string.pref_search_word_default));
        new FetchArticleTask().execute(searchWord);
    }

    public class FetchArticleTask extends AsyncTask<String, Void, ArrayList<Article>> {

        private final String LOG_TAG = FetchArticleTask.class.getSimpleName();

        private ArrayList<Article> getArticlesFromJson(String articlesJsonStr, int maxResults)
                throws JSONException {

            final String RESPONSE = "response";
            final String RESULTS = "results";
            final String SECTION_NAME = "sectionName";
            final String WEB_TITLE = "webTitle";
            final String FIELDS = "fields";
            final String THUMBNAIL = "thumbnail";
            final String WEB_URL = "webUrl";

            int numArticles;

            ArrayList<Article> articles = new ArrayList<>();

            try {
                JSONObject articlesJson = new JSONObject(articlesJsonStr);
                JSONObject responseJson = articlesJson.getJSONObject(RESPONSE);
                JSONArray resultsJsonArray = responseJson.getJSONArray(RESULTS);

                if (resultsJsonArray.length() > maxResults) {
                    numArticles = maxResults;
                } else {
                    numArticles = resultsJsonArray.length();
                }

                for (int i = 0; i < numArticles; i++) {
                    String title;
                    String sectionName;
                    String webUrl;
                    String thumbnail;
                    Bitmap bitmap = null;

                    try {
                        JSONObject articleJson = resultsJsonArray.getJSONObject(i);
                        title = articleJson.getString(WEB_TITLE);
                        sectionName = articleJson.getString(SECTION_NAME);
                        webUrl = articleJson.getString(WEB_URL);
                        JSONObject fieldsJson = articleJson.getJSONObject(FIELDS);
                        thumbnail = fieldsJson.getString(THUMBNAIL);

                        try {
                            Log.v(LOG_TAG, "bitmap");
                            URL url = new URL(thumbnail);
                            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Error", e);
                        }

                        articles.add(new Article(title, sectionName, bitmap, webUrl));
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Missing field", e);

                    }
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON exception", e);
            }
            return articles;
        }

        @Override
        protected ArrayList<Article> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String articleJsonStr = null;
            String apiKey = "test";
            String showFields = "thumbnail";

            try {
                final String BASE_URL = "http://content.guardianapis.com/search?";
                final String QUERY_PARAM = "q";
                final String API_KEY = "api-key";
                final String SHOW_FIELDS = "show-fields";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(SHOW_FIELDS, showFields)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                articleJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getArticlesFromJson(articleJsonStr, mMaxNumResults);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            if (articles != null) {
                mArticleAdapter.clear();
                for (Article article : articles) {
                    mArticleAdapter.add(article);
                }
            }
        }
    }
}
