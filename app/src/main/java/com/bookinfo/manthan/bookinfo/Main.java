package com.bookinfo.manthan.bookinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Manthan on 9/20/2014.
 * This class provides the list of books related to the search entered by the user
 */

public class Main extends Activity {

    final String url_1stpart = "https://query.yahooapis.com/v1/public/yql?q=SELECT%20*%20FROM%20google.books%20WHERE%20q%3D%22";
    final String url_2ndpart = "%22%20&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
    private String url_middlepart = "";
    ProgressDialog pDialog;
    ListView listview;
    EditText bookNameEditText;
    String jsonString = "";
    List books;
    static final String KEY_TITLE = "title";
    static final String KEY_AUTHORS = "autors";
    static final String KEY_IMAGE = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookNameEditText = (EditText) findViewById(R.id.BookNameEditText);
        Button searchButton = (Button) findViewById(R.id.SearchButton);
        listview = (ListView) findViewById(R.id.listView);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent openBookDetail = new Intent(Main.this, BookDetails.class);
                Bundle bundle = new Bundle();
                bundle.putString("jsonString", jsonString);
                bundle.putInt("selectedBook", i);
                openBookDetail.putExtras(bundle);
                startActivity(openBookDetail);
            }
        });
    }


    public void searchButtonOnClick(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bookNameEditText.getWindowToken(), 0);

        if (bookNameEditText.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(), "Please Enter a valid Book Name", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            url_middlepart = bookNameEditText.getText().toString();
            if (url_middlepart.contains(" "))
                url_middlepart = url_middlepart.replaceAll(" ", "%20");
            String url = url_1stpart + url_middlepart + url_2ndpart;

            new GetDataFromWeb().execute(url);
        }

    }


    public class GetDataFromWeb extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Main.this);
            pDialog.setMessage("Loading Books...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            InputStream inStream = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(strings[0]); // Be sure to replace with your actual script
                HttpResponse postResponse = client.execute(post);
                HttpEntity responseEntity = postResponse.getEntity();
                inStream = responseEntity.getContent();
            } catch (Exception e) {
                Log.e("Error connecting", e.getLocalizedMessage());
            }

            try {
                //read the stream to a single JSON string
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 10); // iso-8859-1 is the character converter
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(reader.readLine() + "\n");

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                inStream.close();
                jsonString = stringBuilder.toString();

            } catch (Exception e) {
                Log.e("Error creating JSON string", e.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            parseJson();
            pDialog.dismiss();

        }
    }


    //parsing Json and calling custom adapter to set listView
    private void parseJson() {
        books = new ArrayList();
        JSONObject jsonobj = null;

        try {
            jsonobj = new JSONObject(jsonString);
            JSONObject query = jsonobj.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONObject json = results.getJSONObject("json");
            JSONArray items = json.getJSONArray("items");

            for (int bookno = 0; bookno < items.length(); bookno++) {
                HashMap hm = new HashMap();
                JSONObject book = items.getJSONObject(bookno);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                hm.put(KEY_TITLE, volumeInfo.getString("title"));

                try {
                    hm.put(KEY_AUTHORS, volumeInfo.getString("authors"));
                } catch (JSONException e) {
                    hm.put(KEY_AUTHORS, " ");
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                hm.put(KEY_IMAGE, imageLinks.getString("smallThumbnail"));
                books.add(hm);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomAdapter adapt = new CustomAdapter(this, R.layout.list_row, books);
        listview.invalidateViews();
        listview.setAdapter(adapt);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menu.addIntentOptions(Menu.NONE, Menu.NONE, Menu.NONE, this.getComponentName(), null, i, Menu.NONE, null);
        //menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
